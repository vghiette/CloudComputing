import static spark.Spark.*;

import java.util.ArrayList;

import com.google.gson.Gson;

import spark.*;

public class WebListner {
	public static void main(String[] args)
	{
		setPort(4567);
		enableCORS("*", "*", "*");
		
		 get(new Route("/hello") {
			 @Override
			 public Object handle(Request request, Response response) {
			 	return "<html><body>lool</body></html>";
			 }
		 });
		 
		 post(new Route("/search") {
			 @Override
			 public Object handle(Request request, Response response) {
	                String personName = request.queryParams("nameInput");
	                String locationName = request.queryParams("locationInput");
	                String organisationName = request.queryParams("organisationInput");
	                
	                
	                TextAnalyzer ta = new TextAnalyzer();
	                
	                System.out.println("Searching for person: " + personName + " location: " +locationName + " organisation: " + organisationName);
	                
	                ArrayList<Text> relevantTexts = ta.getRelevantTexts(personName, locationName, organisationName);
	                
	                System.out.println("Searching for person: " + personName + " location: " +locationName + " organisation: " + organisationName);
	                
	                Gson gson = new Gson();
	                String json = gson.toJson(relevantTexts);
	                	                
	                return json;
	            }			 
		 });
		
	}
	
	// Enable cross origin reference
	private static void enableCORS(final String origin, final String methods, final String headers) {
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
