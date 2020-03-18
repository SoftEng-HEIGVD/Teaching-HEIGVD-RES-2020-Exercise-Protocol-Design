package ch.heigvd.res.examples;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Scanner;
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
		OutputStream os = null;
		InputStream is = null;
		Scanner sc = new Scanner(System.in);

		
		try {
			clientSocket = new Socket("localhost", 31415);
			os = clientSocket.getOutputStream();
			is = clientSocket.getInputStream();


			ByteArrayOutputStream responseBuffer = new ByteArrayOutputStream();
			byte[] buffer = new byte[BUFFER_SIZE];
			int newBytes =0;

			os.write(sc.nextLine().concat("\n").getBytes());
			while ((newBytes = is.read(buffer)) != -1) {
				responseBuffer.write(buffer, 0, newBytes);
				if (buffer[newBytes-1]==10)
					LOG.log(Level.INFO, responseBuffer.toString());
					if( responseBuffer.toString().contains("BYE"))
						break;

				os.write(sc.nextLine().getBytes());

				responseBuffer.reset();
			}

			LOG.log(Level.INFO, responseBuffer.toString());
		} catch (IOException ex) {
			LOG.log(Level.SEVERE, null, ex);
		} finally {
			try {
				is.close();
			} catch (IOException ex) {
				Logger.getLogger(DumbHttpClient.class.getName()).log(Level.SEVERE, null, ex);
			}
			try {
				os.close();
			} catch (IOException ex) {
				Logger.getLogger(DumbHttpClient.class.getName()).log(Level.SEVERE, null, ex);
			}
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
