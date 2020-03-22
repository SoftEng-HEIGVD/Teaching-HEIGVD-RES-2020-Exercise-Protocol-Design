package main.java.ch.heig.res.protocol.client.x47726579;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import static main.java.ch.heig.res.protocol.protocol.x47726579.Protocol.*;

/**
 * This class implements a simple client
 * This client is not meant to be called in parallel to anything else
 *
 * @author Olivier Liechti
 * @modified_by Laurent Scherer
 */
public class Client
{

	Socket         clientSocket;
	BufferedReader in;
	PrintWriter    out;
	boolean        connected = false;


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
			out = new PrintWriter(clientSocket.getOutputStream());
			connected = true;
		} catch (IOException e) {
			cleanup();
			return;
		}
		out.println("Greetings");
		out.flush();
		run();
	}

	public void run()
	{
		Scanner input = new Scanner(System.in);
		try {
			String commandLine, info;
			while (connected) {
				while ((info = in.readLine()) != null && !info.equals(CMD_END)) {
					System.out.println(info);
				}
				System.out.print(">");

				while ((commandLine = input.nextLine()).isBlank() && commandLine.length() < 3) { ; }
				commandLine = commandLine.toUpperCase();
				if (commandLine.equals(CMD_EXT)) {
					out.println(CMD_EXT);
					cleanup();
					break;
				} else if (commandLine.equals(CMD_KILL)) {
					out.println(CMD_KILL);
					cleanup();
					break;
				}
				out.println(commandLine);
				out.flush();
			}
		} catch (IOException ex) {System.out.println("ex = " + ex);}
	}

	private void cleanup()
	{
		out.flush();
		connected = false;
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