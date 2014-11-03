
public class Main {

	public static void main(String[] args) {
		
		if(args.length == 1){
			
			if(args[0].equals("master"))
			{
				new Master();
			}
			else if(args[0].equals("slave"))
			{
				new Slave();
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
	}
	
	private static void printProgramInstructions()
	{
		System.err.println("The program must be called in the following way:\n"
				+ "\tjava -jar cloud.jar <type>\n"
				+ "with:\n"
				+ "\t<type>: 'master' or 'slave', without the '\n");
		
	}

}
