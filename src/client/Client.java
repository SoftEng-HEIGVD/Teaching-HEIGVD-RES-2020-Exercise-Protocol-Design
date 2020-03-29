package client;

import protocol.Protocol;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {
    private double firstOperand;
    private double secondOperand;
    private String operator;
    private boolean stopRequest = false;
    static final Logger LOG = Logger.getLogger(Client.class.getName());

    private Socket clientSocket = null;
    private BufferedReader reader = null;
    private BufferedWriter writer = null;

    public void start() {

        try {
            clientSocket = new Socket("localhost", Protocol.PORT_NO);
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

            writer.write(Protocol.CLIENT_OPENING);
            writer.flush();
            checkServerMessage(Protocol.SERVER_OPENING);

            while(!stopRequest){
                getUserInput();
                writer.write(Double.toString(firstOperand) + " " + operator.toString() + " " + Double.toString(secondOperand) +"\n");   //TODO reconversion string bête
                writer.flush();
                checkServerMessage(null);
            }

            clientSocket.close();
            writer.close();
            reader.close();

        } catch (IOException ex) {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex1) {
                    LOG.log(Level.SEVERE, ex1.getMessage(), ex1);
                }
            }
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ex1) {
                    LOG.log(Level.SEVERE, ex1.getMessage(), ex1);
                }
            }
            if (clientSocket != null) {
                try {
                    clientSocket.close();
                } catch (IOException ex1) {
                    LOG.log(Level.SEVERE, ex1.getMessage(), ex1);
                }
            }
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    private boolean getUserInput() {
        Scanner scan = new Scanner(System.in);
        String input = scan.next();
        String[] inputTokens = new String[3];
        boolean valid = false;

        StringTokenizer st = new StringTokenizer(input, " ", false);
        int numberOfTokens = 0;
        while(st.hasMoreTokens() && numberOfTokens < 3){
            inputTokens[numberOfTokens++] = st.nextToken();
        }

        if(numberOfTokens == 1){
            valid = checkCloseRequest(inputTokens[0]);
        }else if(numberOfTokens != 3){
            valid = false;
        }else{
            operator = inputTokens[1];
            firstOperand = Double.parseDouble(inputTokens[0]);  //TODO exception
            secondOperand = Double.parseDouble(inputTokens[2]);
            valid = checkUserInput();
        }

        return valid;

    }

    private boolean checkUserInput() {
        boolean valid = false;
        for(int i = 0; i < Protocol.OPERATORS.length; ++i){
            if(Protocol.OPERATORS[i].equals(operator)){
                valid = true;
            }
        }

        if(secondOperand == 0 && operator.equals("/")){
            valid = false;
        }

        return valid;
    }

    private boolean checkCloseRequest(String message) {
        stopRequest = message.equals(Protocol.CLIENT_CLOSE_REQUEST);
        return stopRequest;
    }

    private void checkServerMessage(String message) throws IOException {
        String line = reader.readLine();
        if (message != null && !line.equals(message)) {
            throw new IOException("Unexpected message from server");
        }
        System.out.println(line);
    }

}