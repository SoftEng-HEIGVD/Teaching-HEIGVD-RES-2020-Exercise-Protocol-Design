package main.java.ch.heig.res.protocol.test.x47726579;

import main.java.ch.heig.res.protocol.server.x47726579.Server;

import java.awt.desktop.SystemSleepEvent;

/**
 * The server reacts to the following commands, defined in the protocol:
 * - HELLO name: the user "behind" the client is not anonymous anymore
 * - SAY message: the message is broadcasted to connected clients
 * - WHO: the server returns the list of connected users
 * - BYE: the client is disconnected and the others are notified
 *
 * @author Olivier Liechti
 */
public class ApplicationServer
{

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args)
	{
		System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s %n");

		Thread listenThread = new Thread(new Server());
		listenThread.start();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		while (listenThread.isAlive()) {
			;
		}


	}

}
