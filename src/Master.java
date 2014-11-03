import static spark.Spark.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.*;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import spark.*;

/**
 * Master class is a class which handles the Master part of the program. It
 * handles the incoming requests and splits up the workload and sends it to the
 * slave nodes which do the actual text analysis. The Master does that by
 * handling the sent POST requests by the client and divides them in other POST
 * requests which are sent to the slave nodes. For handling the post requests
 * the master relies on the Spark library. In order to sent different POST
 * requests to different slave instances the MAster sends them in different
 * threads so the requests are sent simultaneously, therefore creating a kind of
 * parallelism.
 * 
 * @author Vincent Ghiette
 *
 */
public class Master {

	// Log in credential for Amazon
	String accessKey = "AKIAINKV5YEJDW5OIJZQ";
	String secretKey = "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";
	String s3IP = "s3.eu-central-1.amazonaws.com";
	String slaveELBIP = "http://localhost:5678";
	int masterPort = 4567;

	/**
	 * Create a new instance of the MAster class. This instance listens to
	 * incoming POST requests and sends subsets to the Slaves which handle them
	 * in parallel. The answers are combined and sent back to the client.
	 */
	public Master() {

		// Set the port to which the master should listen to.
		setPort(masterPort);

		// Tell the master that cross domain references are allowed.
		enableCORS("*", "*", "*");

		// Listen to incoming POST requests, Spark library
		post(new Route("/") {
			@Override
			public Object handle(Request request, Response response) {
				// Get the POST data from the client
				String personName = request.queryParams("nameInput");
				String locationName = request.queryParams("locationInput");
				String organisationName = request
						.queryParams("organisationInput");
				String slaveNumber = request.queryParams("slaveInput");

				/*********** Get the documents from S3 ****/

				// Connect to S3
				AWSCredentials credentials = new BasicAWSCredentials(accessKey,
						secretKey);
				AmazonS3 conn = new AmazonS3Client(credentials);
				conn.setEndpoint(s3IP);

				// Get the list of all buckets used
				List<Bucket> buckets = conn.listBuckets();

				// Get all the files in the buckets and store the in a
				// FileLocation ArrayList
				ArrayList<FileLocation> keys = new ArrayList<FileLocation>();

				for (Bucket bucket : buckets) {
					ObjectListing objects = conn.listObjects(bucket.getName());
					do {
						for (S3ObjectSummary objectSummary : objects
								.getObjectSummaries()) {
							keys.add(new FileLocation(bucket.getName(),
									objectSummary.getKey()));

						}
						objects = conn.listNextBatchOfObjects(objects);
					} while (objects.isTruncated());
				}

				/***************** Create batches for the slaves ****************/

				// The number of files which are processed per slave
				int intSlaveNumber = Integer.parseInt(slaveNumber);

				// Create the batches for the slave containing at most the
				// number of files which need to be processed by one slave
				ArrayList<FileBatch> batches = new ArrayList<FileBatch>();

				for (int start = 0; start < keys.size(); start += intSlaveNumber) {
					int end = Math.min(start + intSlaveNumber, keys.size());
					batches.add(new FileBatch(keys.subList(start, end)));
				}

				/****************
				 * Send the POST request asynchronously to the ELB which will
				 * send the through to the slaves
				 ************/
				// For each batch create a new post request and send it to the
				// ELB
				ExecutorService pool = Executors.newFixedThreadPool(Math.max(1,
						(int) Math.ceil(keys.size() / intSlaveNumber)));
				Set<Future<String>> set = new HashSet<Future<String>>();

				// This creates new threads for all the POST requests and sends
				// them, the thread are for parallel processing by the slaves
				for (FileBatch batch : batches) {
					// Create a parameter string for the POST request
					String parameters = "nameInput=" + personName
							+ "&locationInput=" + locationName
							+ "&organisationInput=" + organisationName
							+ "&files=" + batch.toString();
					// Create a Callable task for sending the POST request
					// asynchronously
					Callable<String> callable = new PostCallable(slaveELBIP,
							parameters);
					Future<String> future = pool.submit(callable);
					set.add(future);
				}

				/********************** Get the results from the Slaves, this is synchronous *************/
				// This gets all the POST request results
				ArrayList<Text> results = new ArrayList<Text>();
				Gson gson = new Gson();
				for (Future<String> future : set) {
					try {
						// Create anew type to convert the slave answer which is
						// in json to an ArrayList of Text instances.
						java.lang.reflect.Type listType = new TypeToken<ArrayList<Text>>() {
						}.getType();
						// get the json slave response.
						String jsonString = future.get();
						// Create an ArrayList of text instances from the json
						// result.
						ArrayList<Text> temptext = gson.fromJson(jsonString,
								listType);

						// Merge the total results with the results form that
						// slave
						results.addAll(temptext);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				// Convert the merged results to json
				String json = gson.toJson(results);

				// Return the json to the client
				return json;
			}
		});
	}

	// Enable cross origin reference
	private void enableCORS(final String origin, final String methods,
			final String headers) {
		before(new Filter() {
			@Override
			public void handle(Request request, Response response) {
				response.header("Access-Control-Allow-Origin", origin);
				response.header("Access-Control-Request-Method", methods);
				response.header("Access-Control-Allow-Headers", headers);
			}
		});
	}
}
