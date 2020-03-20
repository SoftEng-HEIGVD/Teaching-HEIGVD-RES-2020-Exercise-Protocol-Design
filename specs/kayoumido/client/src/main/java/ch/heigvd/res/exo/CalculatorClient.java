/**
 * @author : Bonzon Ludovic
 */

package ch.heigvd.res.exo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CalculatorClient {
    static final Logger LOG = Logger.getLogger(CalculatorClient.class.getName());
    private final String SERVER_IP = "localhost";
    private final int SERVER_PORT = 49153;
    final static int BUFFER_SIZE = 1024;
    private boolean connected = false;
    private Socket clientSocket = null;
    private OutputStream os = null;
    private InputStream is = null;

    /**
     * Method to connect to a server
     */
    public void connect() {
        if(connected) {
            LOG.log(Level.INFO, "Already connected.");
            return;
        }

        try {
            clientSocket = new Socket(SERVER_IP, SERVER_PORT);
            os = clientSocket.getOutputStream();
            is = clientSocket.getInputStream();
            connected = true;
        } catch(IOException ex) {
            LOG.log(Level.SEVERE, "Couldn't connect to server.", ex);
            clean();
            return;
        }
        LOG.log(Level.INFO, "Connected to server.");
    }

    /**
     * Method to disconnect from a server
     */
    public void disconnect() {
        if(!connected) {
            LOG.log(Level.INFO, "Not connected to server.");
            return;
        }

        connected = false;
        clean();

        LOG.log(Level.INFO, "Disconnected from server.");
    }

    /**
     * Method to clean all possible open streams and sockets
     */
    private void clean() {
        try {
            if(is != null) {
                is.close();
            }
        } catch(IOException ex) {
            LOG.log(Level.SEVERE, "Couldn't close input stream.", ex);
        }

        try {
            if(os != null) {
                os.close();
            }
        } catch(IOException ex) {
            LOG.log(Level.SEVERE, "Couldn't close output stream.", ex);
        }

        try {
            if(clientSocket != null) {
                clientSocket.close();
            }
        } catch(IOException ex) {
            LOG.log(Level.SEVERE, "Couldn't close socket.", ex);
        }
    }

    /**
     * Given a calculation request, this method can send it to the server and recieve the value of the operation
     *
     * @param calculus to process
     * @return a string representing the calculus and its answer
     */
    public String calculate(String calculus) {
        if(!connected) {
            LOG.log(Level.INFO, "Cannot caluclate, not connected to server.");
            return null;
        }

        LOG.log(Level.INFO, "Calculus is : " + calculus);
        calculus += "\n";

        try {
            // Sending calculus
            os.write(calculus.getBytes(StandardCharsets.UTF_8));
            os.flush();

            // Reading the server's reply
            ByteArrayOutputStream responseBuffer = new ByteArrayOutputStream();
            byte[] buffer = new byte[BUFFER_SIZE];
            int newBytes;
            while((newBytes = is.read(buffer)) != -1) {
                responseBuffer.write(buffer, 0, newBytes);
                break;
            }

            return responseBuffer.toString();
        } catch(IOException ex) {
            LOG.log(Level.SEVERE, "Cannot send message", ex);
            return null;
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Creates a client
        CalculatorClient client = new CalculatorClient();
        // Connect it to the server
        client.connect();

        // Ask user for calculation to perform
        String calculus = "";
        while(true) {
            System.out.println("Enter calculation");
            Scanner userInput = new Scanner(System.in);
            calculus = userInput.nextLine();  // Read user input

            if(calculus.equals("QUIT"))
                break;

            // Calculate and store the response
            String response = client.calculate(calculus);

            // Display the result
            if(response != null) {
                System.out.print(response);
            } else {
                LOG.log(Level.INFO, "Couldn't calculate.");
            }
        }

        client.disconnect();
    }
}
