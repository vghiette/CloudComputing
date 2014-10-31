
public class Main {

	public static void main(String[] args) {
		
		if(args.length == 1){
			
			if(args[0].equals("master"))
			{
				
			}
			else if(args[0].equals("slave"))
			{
				
			}
			else
			{
				printProgramInstructions();
				System.exit(1);
			}
			
		}
		else
		{
			printProgramInstructions();
		}
		
		// Wait for the server to start
		/*
		try {
		    Thread.sleep(2000);                 //1000 milliseconds is one second.
		} catch(InterruptedException ex) {
		    Thread.currentThread().interrupt();
		}
	 
		HttpMessageSender msgSender = new HttpMessageSender();
		try {
			msgSender.sendGet("http://localhost:4567/hello");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			String urlParameters = "nameInput=ber&locationInput=&organisationInput=cnn";
			msgSender.sendPost("http://localhost:4567/search", urlParameters);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}
	
	private static void printProgramInstructions()
	{
		System.err.println("The program must be called in the following way:\n"
				+ "\tjava -jar cloud.jar <type>\n"
				+ "with:\n"
				+ "\t<type>: 'master' or 'slave', without the '\n");
		
	}

}
