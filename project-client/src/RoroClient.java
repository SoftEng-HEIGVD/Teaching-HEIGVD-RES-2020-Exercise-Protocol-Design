
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
public class RoroClient {

    static final Logger LOG = Logger.getLogger(RoroClient.class.getName());

    final static int BUFFER_SIZE = 1024;

    static final char[] SYMBOL_OPERATORS = {'+','*','-','/'}; // used to check input user

    private byte[] buffer = new byte[BUFFER_SIZE];
    private int newBytes;

    /**
     * This method does the whole processing :
     * Client starts conection, ask operations to server with acquittment of the answer computed
     * and process to stop conection
     */
    public void sendRequest() {

        Socket clientSocket = null;
        OutputStream os = null;
        InputStream is = null;

        try {

            clientSocket = new Socket("www.google.ch", 80);
            os = clientSocket.getOutputStream();
            is = clientSocket.getInputStream();

            // Client starts connection
            writeToServer(os, "Hello, I am Roro client\r\n\r\n");

            // Waits for Server to answer
            readServerAnswer(is);
                    // ByteArrayOutputStream responseBuffer = new ByteArrayOutputStream(); no need
                    // buffer et tout -> déplacé dans attributs privés ?

            // While the user hasn't entered 'Q' (to quit), ask again to do an operation
            String myString = "";
            do{
                 // Get input client user
                Scanner input = new Scanner(System.in);
                System.out.print("Enter the operation you want to do : (enter 'Q' to quit)");
                myString = input.nextLine();
                myString = myString.replaceAll("\\s+",""); // Remove whitespace from input user

                if(isCorrect(myString)){ // If user input is valid
                    // Send to server the operation user
                    writeToServer(os, myString);

                    // Waits for Server to answer
                    readServerAnswer(is);

                    // Send to server confirmation
                    writeToServer(os, "I got the answer\r\n\r\n");

                } else { // If user input is not valid
                    // Error msg
                    System.out.println("INPUT OPERATION NOT VALID");
                }
            } while(!myString.equals("Q")); // User client wants to quit the application client

            // Waits for Server to answer
            readServerAnswer(is);

            // Send goodbye
            writeToServer(os, "Goodbye, Bastien server :D\r\n\r\n");

            LOG.log(Level.INFO, "Response sent by the server: ");
            // LOG.log(Level.INFO, responseBuffer.toString());

        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            try {
                is.close();
            } catch (IOException ex) {
                Logger.getLogger(RoroClient.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                os.close();
            } catch (IOException ex) {
                Logger.getLogger(RoroClient.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                clientSocket.close();
            } catch (IOException ex) {
                Logger.getLogger(RoroClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void writeToServer(OutputStream os, String message) throws IOException {
        os.write(message.getBytes());
    }

    public void readServerAnswer(InputStream is) throws IOException {
        while ((newBytes = is.read(buffer)) != -1) {
            //responseBuffer.write(buffer, 0, newBytes); no need
        }
    }

    public boolean isSymbolOperator(char c){

        for (char symbolOperator : SYMBOL_OPERATORS) {
            if (c == symbolOperator)
                return true;
        }

        return false;
    }

    public boolean isCorrect(String str) {
        // Check if the input user is an operation (at least 2 operands and 1 operator)
        if (str.length() >= 3) {
            // Check first, second and final symbol
            if (!Character.isDigit(str.charAt(0)) ||
                    !Character.isDigit(str.charAt(1)) ||
                    !isSymbolOperator(str.charAt(str.length() - 1))) {
                return false;

            } else if (str.length() != 3) {
                // Check the rest (between)
                for (int i = 2; i < str.length() - 1; i++) {
                    if (i % 2 == 0) { // if even, it should be a digit
                        if (!isSymbolOperator(str.charAt(i)))
                            return false;
                    } else { // if odd, it should be an operator
                        if (!Character.isDigit(str.charAt(i)))
                            return false;
                    }
                }
                return true;
            }
            return true;
        } else {
            return false;
        }
    }



    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s %n");

        RoroClient client = new RoroClient();
        client.sendRequest();

    }

}