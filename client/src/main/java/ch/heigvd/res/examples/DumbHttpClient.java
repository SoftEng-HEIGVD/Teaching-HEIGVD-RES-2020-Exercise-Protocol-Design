package ch.heigvd.res.examples;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is not really an HTTP client, but rather a very simple program that
 * establishes a TCP connection with a real HTTP server. Once connected, the 
 * client sends "garbage" to the server (the client does not send a proper
 * HTTP request that the server would understand). The client then reads the
 * response sent back by the server and logs it onto the console.
 * 
 * @author Olivier Liechti
 */
public class DumbHttpClient {

	static final Logger LOG = Logger.getLogger(DumbHttpClient.class.getName());

	final static int BUFFER_SIZE = 1024;

	/**
	 * This method does the whole processing
	 */
	public void sendWrongHttpRequest() {
		Socket clientSocket = null;
		PrintWriter os = null;
		BufferedReader is = null;
		
		try {
			clientSocket = new Socket("localhost", 2020);
			os = new PrintWriter(clientSocket.getOutputStream());
			is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

			while(true){
				String userInput = reader.readLine();
				os.println(userInput);
				os.flush();

				LOG.log(Level.INFO, String.format("Server response: %s", is.readLine()));
			}
		} catch (IOException ex) {
			LOG.log(Level.SEVERE, null, ex);
		} finally {
			try {
				is.close();
			} catch (IOException ex) {
				Logger.getLogger(DumbHttpClient.class.getName()).log(Level.SEVERE, null, ex);
			}
			os.close();
			try {
				clientSocket.close();
			} catch (IOException ex) {
				Logger.getLogger(DumbHttpClient.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s %n");

		DumbHttpClient client = new DumbHttpClient();
		client.sendWrongHttpRequest();

	}

}
