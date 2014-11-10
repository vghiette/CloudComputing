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

		if (args.length == 6 && args[0].equals("master")) {
			String accessKey = args[1];
			String secretKey = args[2];
			String s3IP = args[3];
			int masterPort = Integer.parseInt(args[4]);
			String slaveELBIP = args[5];
			new Master(accessKey, secretKey, s3IP, slaveELBIP, masterPort);

		} else if (args.length == 5 && args[0].equals("slave")) {
			String accessKey = args[1];
			String secretKey = args[2];
			String s3IP = args[3];
			int slavePort = Integer.parseInt(args[4]);

			new Slave(accessKey, secretKey, s3IP, slavePort);
		} else {
			printProgramInstructions();
			System.exit(1);
		}

	}

	/**
	 * Print the program instructions so the user knows how to use it.
	 */
	private static void printProgramInstructions() {
		System.err
				.println("The program must be called in the following way:\n"
						+ "\tjava -jar cloud.jar master <accessKey> <secretKey> <s3IP> <portNumber> <slaveELBIP>\n"
						+ "or\n"
						+ "\tjava -jar cloud.jar slave <accessKey> <secretKey> <s3IP> <portNumber>\n"
						+ "with:\n"
						+ "\t<accessKey>: the access key from AWS\n"
						+ "\t<secretKey>: the secret key from AWS\n"
						+ "\t<s3IP>: the address of S3 instance ex: s3.eu-central-1.amazonaws.com\n"
						+ "\t<portNumber>: the port number on whihc the instance should listen to usually 80\n"
						+ "\t<slaveELBIP>: the IP address of the ELB which links to the slaves\n");

	}

}
