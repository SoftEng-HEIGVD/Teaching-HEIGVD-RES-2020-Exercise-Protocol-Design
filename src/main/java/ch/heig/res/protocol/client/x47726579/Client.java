package main.java.ch.heig.res.protocol.client.x47726579;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * This class implements a simple client for our custom presence protocol.
 * When the client connects to a server, a thread is started to listen for
 * notifications sent by the server.
 *
 * @author Olivier Liechti
 * @modified_by Laurent Scherer
 */
public class Client
{

	Socket         clientSocket;
	BufferedReader in;
	BufferedReader cliIn;
	PrintWriter    out;
	boolean        connected = false;

	class NotificationListener implements Runnable
	{

		@Override
		public void run()
		{
			String notification;
			try {
				while ((connected && (notification = in.readLine()) != null)) {
					System.out.println(notification);
				}
			} catch (IOException e) {
				connected = false;
			} finally {
				cleanup();
			}
		}
	}

	/**
	 * This method is used to connect to the server and to inform the server that
	 * the user "behind" the client has a name (in other words, the HELLO command
	 * is issued after successful connection).
	 *
	 * @param serverAddress the IP address used by the Presence Server
	 * @param serverPort    the port used by the Presence Server
	 */
	public void connect(String serverAddress, int serverPort)
	{
		try {
			clientSocket = new Socket(serverAddress, serverPort);
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			cliIn = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			out = new PrintWriter(clientSocket.getOutputStream());
			connected = true;
		} catch (IOException e) {
			cleanup();
			return;
		}
		new Thread(new NotificationListener()).start();
		out.println("Greetings");
		out.flush();
		run();
	}

	public void run()
	{
		try {
			String commandLine;
			while (connected && ((commandLine = cliIn.readLine()) != null)) {
				out.println(commandLine);
				out.flush();
			}
		} catch (IOException ex) {System.out.println("ex = " + ex);}
	}

	public void disconnect()
	{
		connected = false;
		out.println("BYE");
		cleanup();
	}

	private void cleanup()
	{

		if (in != null) {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if (out != null) {
			out.close();
		}

		if (clientSocket != null) {
			try {
				clientSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}


}