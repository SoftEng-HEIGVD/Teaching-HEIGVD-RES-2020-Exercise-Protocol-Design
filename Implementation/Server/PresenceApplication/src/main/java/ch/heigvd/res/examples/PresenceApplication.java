package ch.heigvd.res.examples;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * The server reacts to the following commands, defined in the protocol:
 * - HELLO name: the user "behind" the client is not anonymous anymore
 * - SAY message: the message is broadcasted to connected clients
 * - WHO: the server returns the list of connected users
 * - BYE: the client is disconnected and the others are notified
 * 
 * @author Olivier Liechti
 */
public class PresenceApplication {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) throws Exception {
		System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s %n");

		BufferedReader cmdIn = null;
		String cmd, servInfo;

		System.out.println("\n\n\n\n");
		System.out.println("WARNING !!!! INITIAL INPUTS NOT VERIFIED MAKE SURE THEY'RE CORRECT\n");
		System.out.println("Enter server port || (ex : 9907)\n");

		cmdIn = new BufferedReader(new InputStreamReader(System.in));
		servInfo = cmdIn.readLine();

		Thread listenThread = new Thread(new PresenceServer( Integer.parseInt(servInfo)));
		listenThread.start();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
