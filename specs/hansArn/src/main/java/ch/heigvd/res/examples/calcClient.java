package ch.heigvd.res.examples;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Scanner;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
/**
 * This is not really an HTTP client, but rather a very simple program that
 * establishes a TCP connection with a real HTTP server. Once connected, the 
 * client sends "garbage" to the server (the client does not send a proper
 * HTTP request that the server would understand). The client then reads the
 * response sent back by the server and logs it onto the console.
 * 
 * @author Olivier Liechti
 */
public class calcClient {

	static final Logger LOG = Logger.getLogger(calcClient.class.getName());

	final static int BUFFER_SIZE = 1024;

	/**
	 * This method does the whole processing
	 */
	public void sendWrongHttpRequest() {
		Socket clientSocket = null;
		BufferedReader in = null;
		PrintWriter out = null;
		Scanner sc = new Scanner(System.in);

		
		try {
			clientSocket = new Socket("localhost", 31415);
			in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			out = new PrintWriter(clientSocket.getOutputStream());


			ByteArrayOutputStream responseBuffer = new ByteArrayOutputStream();

			String str;
			out.println(sc.nextLine());
			out.flush();
			while ((str = in.readLine()) != null ) {
				LOG.log(Level.INFO, str);
				if( str.contains("bye"))
					break;
				out.println(sc.nextLine());
				out.flush();
			}

			LOG.log(Level.INFO, str);
		} catch (IOException ex) {
			LOG.log(Level.SEVERE, null, ex);
		} finally {
			try {
				in.close();
			} catch (IOException ex) {
				Logger.getLogger(calcClient.class.getName()).log(Level.SEVERE, null, ex);
			}
			out.close();
			try {
				clientSocket.close();
			} catch (IOException ex) {
				Logger.getLogger(calcClient.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s %n");

		calcClient client = new calcClient();
		client.sendWrongHttpRequest();

	}

}
