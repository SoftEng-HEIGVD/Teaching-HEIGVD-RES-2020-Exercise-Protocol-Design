
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

    private char[] buffer = new char[BUFFER_SIZE];

    /**
     * This method does the whole processing :
     * Client starts conection, ask operations to server with acquittment of the answer computed
     * and process to stop conection
     */
    public void sendRequest() {

        Socket clientSocket = null;
        BufferedReader reader = null;
        PrintWriter writer = null;

        try {

           clientSocket = new Socket("10.192.19.159", 49500);

            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            writer = new PrintWriter(clientSocket.getOutputStream());

            // Client starts connection
            writeToServer(writer, "Hello I'm a Roro client");

            // Waits for Server to answer

            int sizeMsg = reader.read(buffer, 0, BUFFER_SIZE);
            LOG.info(new String(buffer,0,sizeMsg));

            // While the user hasn't entered 'Q' (to quit), ask again to do an operation
            String myString = "";
            do{

                 // Get input client user
                Scanner input = new Scanner(System.in);
                System.out.print("Enter the operation you want to do : (enter 'Q' to quit)");
                myString = input.nextLine();

                if(isCorrect(myString)){ // If user input is valid
                    // Send to server the operation user
                    writeToServer(writer, myString);

                    // Waits for Server to answer

                    sizeMsg = reader.read(buffer,0,BUFFER_SIZE);
                    String result = new String(buffer,0,sizeMsg);

                    System.out.println(result);


                }

            } while(!myString.equals("Q")); // User client wants to quit the application client

            // Send goodbye
            writeToServer(writer, "GOOD BYE");

        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                Logger.getLogger(RoroClient.class.getName()).log(Level.SEVERE, null, ex);
            }
            writer.close();
            try {
                clientSocket.close();
            } catch (IOException ex) {
                Logger.getLogger(RoroClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void writeToServer(PrintWriter writer, String myString){
        writer.println(myString);
        writer.flush();
    }

    public boolean isSymbolOperator(String str){

        if(str.length() != 1)
            return false;

        for (char symbolOperator : SYMBOL_OPERATORS) {
            if (str.charAt(0) == symbolOperator)
                return true;
        }

        return false;
    }

    public boolean isNumber(String str){
        for(int i = 0;  i < str.length(); i++){
            if(!Character.isDigit(str.charAt(i)))
                return false;
        }
        return true;
    }

    public boolean isCorrect(String str) {
        // Split string into tokens
        String[] strSplited = str.split("\\s+");

        // Check if the input user is an operation (at least 2 operands and 1 operator)
        if (strSplited.length >= 3) {
            // Check first, second and final symbol
            if (!isNumber(strSplited[0]) ||
                !isNumber(strSplited[1]) ||
                    !isSymbolOperator(strSplited[strSplited.length - 1])) {
                return false;

            } else if (strSplited.length != 3) {
                // Check the rest (between)
                for (int i = 2; i < strSplited.length - 1; i++) {
                    if (i % 2 == 0) { // if index is even, it should be an operator
                        if (!isSymbolOperator(strSplited[i]))
                            return false;
                    } else { // if index is odd, it should be an number
                        if (!isNumber(strSplited[i]))
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