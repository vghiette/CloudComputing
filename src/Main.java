/**
 * Create a new runnable instance of the main type. This file needs to be run to
 * initiate a Slave or Master instance of the program. The argument given
 * dictates if the program will behave as master or slave.
 * 
 * @author Vincent Ghiette
 *
 */
public class Main {

	/**
	 * Main method, given the argument the behavior of the application will
	 * change.
	 * 
	 * @param args
	 *            the list of arguments given while starting the program.
	 */
	public static void main(String[] args) {

		// Check if the number of arguments is correct, if not then print out
		// the manual.
		if (args.length == 1) {

			// Decide weather to run a master instance of a slave instance. If
			// none print out the manual
			if (args[0].equals("master")) {
				new Master();
			} else if (args[0].equals("slave")) {
				new Slave();
			} else {
				printProgramInstructions();
				System.exit(1);
			}

		} else {
			printProgramInstructions();
		}
	}

	/**
	 * Print the program instructions so the user knows how to use it.
	 */
	private static void printProgramInstructions() {
		System.err.println("The program must be called in the following way:\n"
				+ "\tjava -jar cloud.jar <type>\n" + "with:\n"
				+ "\t<type>: 'master' or 'slave', without the '\n");

	}

}
