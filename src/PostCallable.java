import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

/**
 * This class is used to ensure that the POST requests sent by the master can be
 * executed in separate threads. The class implements Callable which ensures the
 * POST can be sent in different threads and the answer can be collected after
 * all the POST requests are sent. It Allows the SLave instances to process the
 * POST request in parallel.
 * 
 * @author Vincent Ghiette
 *
 */
public class PostCallable implements Callable<String> {
	// The url of the ELB instance to which the POST request needs to be sent.
	// The ELB will send the POST to a slave.
	private String url;
	// The PPOST parameters
	private String urlParameters;

	/**
	 * Create a new PostCallable instance.
	 * 
	 * @param u
	 *            the url to which the POST request needs to be sent.
	 * @param p
	 *            the parameters of the POST request.
	 */
	public PostCallable(String u, String p) {
		url = u;
		urlParameters = p;
	}

	/**
	 * Overrides the original call method, it send the POST request and
	 * retrieves the answer.
	 * 
	 * @return A String containing the response of the POST request
	 * 
	 * @throws Exception
	 *             if handling the post request fails
	 */
	@Override
	public String call() throws Exception {
		// Tell to act as a Mozilla browser
		String USER_AGENT = "Mozilla/5.0";
		// Create a new URL object to send the POSt request
		URL obj = new URL(url);
		// Open the connection
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();

		// Add the request header
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", USER_AGENT);
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();

		// Get the response
		BufferedReader in = new BufferedReader(new InputStreamReader(
				con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		// Return the result
		return response.toString();
	}
}
