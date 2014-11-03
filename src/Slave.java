import static spark.Spark.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import spark.Filter;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.utils.IOUtils;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.google.gson.Gson;

/**
 * handles POST requests from the Master node. It retrieves the text files form
 * S3 Then it searches through the files for name occurrences. It then analyzes
 * the texts and returns the relevant texts to the master as the answer to the
 * POST request.
 * 
 * @author Vincent Ghiette
 *
 */
public class Slave {
	// Log in credential for Amazon
	String accessKey = "";
	String secretKey = "";
	String s3IP = "";
	int slavePort = 80;

	/**
	 * Creates a new slave instance which will handle incoming POST requests
	 * from the Master nodes.
	 */
	public Slave(String ak, String sk, String s3, int p) {
		// Set the data
		accessKey = ak;
		secretKey = sk;
		s3IP = s3;
		slavePort = p;		
		
		// Set the port to which the slaves listens to.
		setPort(slavePort);
		// tell the slave to listen to cross domain calls.
		enableCORS("*", "*", "*");

		// Handles the POST requests, for more info look at the Spark library
		post(new Route("/") {
			@Override
			public Object handle(Request request, Response response) {
				// Get the POST parameters
				String personName = request.queryParams("nameInput");
				String locationName = request.queryParams("locationInput");
				String organisationName = request
						.queryParams("organisationInput");
				String filesString = request.queryParams("files");

				// Get the documents from S3
				ArrayList<String> files = getFilesContent(filesString);

				// Analyze the texts
				TextAnalyzer ta = new TextAnalyzer(files);

				// Filter the texts to get the relevant texts given the POST
				// parameters
				ArrayList<Text> relevantTexts = ta.getRelevantTexts(personName,
						locationName, organisationName);

				// Put the resulting texts in json
				Gson gson = new Gson();
				String json = gson.toJson(relevantTexts);

				// Return the json
				return json;
			}
		});
	}

	/**
	 * Gets the content of the files in the S3 storage
	 * 
	 * @param filesString
	 *            , the parameter string containing all the information about
	 *            the stored files in S3
	 * @return an ArrayList containing the texts from the files
	 */
	private ArrayList<String> getFilesContent(String filesString) {
		// Connect to S3
		AWSCredentials credentials = new BasicAWSCredentials(accessKey,
				secretKey);
		AmazonS3 conn = new AmazonS3Client(credentials);
		conn.setEndpoint(s3IP);

		// Convert the parameter string containing the file info into a
		// FileBatch
		FileBatch batch = new FileBatch(filesString);
		ArrayList<String> filesContent = new ArrayList<String>();

		// For each FileLocation in the batch retriev the file ontent from S3
		for (FileLocation f : batch.batch) {
			S3Object object = conn.getObject(new GetObjectRequest(f.bucket,
					f.key));
			InputStream objectData = object.getObjectContent();

			String fileContent = "";
			try {
				fileContent = IOUtils.toString(objectData);

			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// Process the objectData stream.
			try {
				objectData.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			filesContent.add(fileContent);
		}

		// Return the ArrayList containing the texts from the files
		return filesContent;
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
