import static spark.Spark.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import spark.Filter;
import spark.Request;
import spark.Response;
import spark.Route;
import spark.utils.IOUtils;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.google.gson.Gson;

public class Slave {
	// Log in credential for amazone
	String accessKey = "AKIAINKV5YEJDW5OIJZQ";
	String secretKey = "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";
	String s3IP = "s3.eu-central-1.amazonaws.com";
	
	public Slave(){
		
		setPort(5678);
		enableCORS("*", "*", "*");

		
		 post(new Route("/") 
		 {
			 @Override
			 public Object handle(Request request, Response response) 
			 {
                String personName = request.queryParams("nameInput");
                String locationName = request.queryParams("locationInput");
                String organisationName = request.queryParams("organisationInput");
                String filesString = request.queryParams("files");
                	                
                /*********** Get the documents from S3 ****/

                ArrayList<String> files = getFilesContent(filesString);
                
                TextAnalyzer ta = new TextAnalyzer(files);

                ArrayList<Text> relevantTexts = ta.getRelevantTexts(personName, locationName, organisationName);
                
                Gson gson = new Gson();
                String json = gson.toJson(relevantTexts);
                	                
                return json;
            }			 
	 	});
	}
	
	private ArrayList<String> getFilesContent(String filesString)
	{
		// Connect to S3
        AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        AmazonS3 conn = new AmazonS3Client(credentials);
        conn.setEndpoint(s3IP);
        
        //Do something to the files so that I can retrieve them
        FileBatch batch = new FileBatch(filesString);
		ArrayList<String> filesContent = new ArrayList<String>();
        
        
        for(FileLocation f: batch.batch)
        {
        	S3Object object = conn.getObject(
        	                  new GetObjectRequest(f.bucket, f.key));
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
        
        return filesContent;
		
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
