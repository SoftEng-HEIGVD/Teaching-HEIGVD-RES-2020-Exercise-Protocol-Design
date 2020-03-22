package ch.heigvd.res.examples;

import java.io.BufferedReader;
import java.io.IOException;
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
		String cmd, logInfo;

		System.out.println("\n\n\n\n");
		System.out.println("WARNING !!!! INITIAL INPUTS NOT VERIFIED MAKE SURE THEY'RE CORRECT\n");
		System.out.println("Enter ip address, port and username  || (ex : localhost 9907 michael)\n");

		cmdIn = new BufferedReader(new InputStreamReader(System.in));
		logInfo = cmdIn.readLine();
		String[] tokens = logInfo.split(" ");

		PresenceClient c1 = new PresenceClient();
//		c1.connect("localhost", Protocol.PRESENCE_DEFAULT_PORT, "Sacha");

		c1.connect(tokens[0], Integer.parseInt(tokens[1]) ,tokens[2]);

		while (true){
		cmdIn = new BufferedReader(new InputStreamReader(System.in));
		cmd = cmdIn.readLine();
		c1.talk(cmd);
		if(cmd.toUpperCase().equals("BYE"))break;
	}

		c1.disconnect();
	}

}
