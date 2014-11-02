import static spark.Spark.*;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.*;
import java.util.concurrent.*;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.amazonaws.util.StringUtils;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import spark.*;

public class Master {

	// Log in credential for amazone
	String accessKey = "AKIAINKV5YEJDW5OIJZQ";
	String secretKey = "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";
	String s3IP = "s3.eu-central-1.amazonaws.com";
	String slaveELBIP = "http://localhost:5678";
	
	public Master(){
		
		setPort(4567);
		enableCORS("*", "*", "*");
		
//		 get(new Route("/hello") {
//			 @Override
//			 public Object handle(Request request, Response response) {
//			 	return "<html><body>lool</body></html>";
//			 }
//		 });
		
		 post(new Route("/") 
		 {
			 @Override
			 public Object handle(Request request, Response response) 
			 {
                String personName = request.queryParams("nameInput");
                String locationName = request.queryParams("locationInput");
                String organisationName = request.queryParams("organisationInput");
                String slaveNumber = request.queryParams("slaveInput");
                	                
                /*********** Get the documents from S3 ****/
                
                // Connect to S3
                AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
                AmazonS3 conn = new AmazonS3Client(credentials);
                conn.setEndpoint(s3IP);
                
                // get the list of all buckets used
                List<Bucket> buckets = conn.listBuckets();
                
                //Get all the files in the buckets
                ArrayList<FileLocation> keys = new ArrayList<FileLocation>();
                
                for(Bucket bucket: buckets)
                {
	                ObjectListing objects = conn.listObjects(bucket.getName());
	                do {
	                        for (S3ObjectSummary objectSummary : objects.getObjectSummaries()) {
	                            keys.add(new FileLocation(bucket.getName(), objectSummary.getKey()));
	                        	
	                        }
	                        objects = conn.listNextBatchOfObjects(objects);
	                } while (objects.isTruncated());
                }
                
                /***************** create batches for the slaves ****************/
                
                // Number of files which remain
                int intSlaveNumber = Integer.parseInt(slaveNumber);
                int remainder = keys.size() % intSlaveNumber;
                
                //Create the batches
                ArrayList<FileBatch> batches = new ArrayList<FileBatch>();
                
                for (int start = 0; start < keys.size(); start += intSlaveNumber) {
                    int end = Math.min(start + intSlaveNumber, keys.size());
                    batches.add(new FileBatch(keys.subList(start, end)));
                }
                
                /**************** send the POST request asychronously to the ELB ************/
                // For each batch create a new post request and send it to the ELB
                ExecutorService pool = Executors.newFixedThreadPool(Math.max(1, (int)Math.ceil(keys.size() / intSlaveNumber)));
                Set<Future<String>> set = new HashSet<Future<String>>();
                
                // This ceate new threads for all the POST requests and send them
                for (FileBatch batch: batches) 
                {
                	String parameters = "nameInput=" + personName + "&locationInput=" + locationName + "&organisationInput=" + organisationName + "&files=" + batch.toString();
                	Callable<String> callable =  new PostCallable(slaveELBIP, parameters);
                	Future<String> future = pool.submit(callable);
                    set.add(future);
                }
                
                /********************** Get the results from the SLAVES *************/
                // This gets all the POST request results
                ArrayList<Text> results = new ArrayList<Text>();
                Gson gson = new Gson();
                for (Future<String> future : set) {
                	try {
                		java.lang.reflect.Type listType = new TypeToken<ArrayList<Text>>() {}.getType();
                		String jsonString = future.get();
                		ArrayList<Text> temptext = gson.fromJson(jsonString, listType);
                		
						results.addAll(temptext);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ExecutionException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }
                
                // Convert the result to json               
                String json = gson.toJson(results);
                	                
                return json;
            }			 
	 	});
	}
	
	// Enable cross origin reference
	private void enableCORS(final String origin, final String methods, final String headers) 
	{
	    before(new Filter() 
	    {
	        @Override
	        public void handle(Request request, Response response) 
	        {
	            response.header("Access-Control-Allow-Origin", origin);
	            response.header("Access-Control-Request-Method", methods);
	            response.header("Access-Control-Allow-Headers", headers);
	        }
	    });
	}
}

