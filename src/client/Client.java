package client;

import protocol.Protocol;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {
    private String firstOperand;
    private String secondOperand;
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

            writer.write(Protocol.CLIENT_OPENING + '\n');
            writer.flush();
            checkServerMessage(Protocol.SERVER_OPENING);

            while(true){
                getUserInput();
                if(stopRequest){
                    writer.write(Protocol.CLIENT_CLOSE_REQUEST + "\n");
                    writer.flush();
                    break;
                }
                writer.write(firstOperand + " " + operator + " " + secondOperand +"\n");
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

    private void getUserInput() {
        Scanner scan = new Scanner(System.in);
        String input = scan.nextLine();
        String[] inputTokens = {"","",""};

        StringTokenizer st = new StringTokenizer(input, " ", false);
        int numberOfTokens = 0;
        while(st.hasMoreTokens() && numberOfTokens < 3){
            inputTokens[numberOfTokens++] = st.nextToken();
        }

        if(numberOfTokens == 1){
            checkCloseRequest(inputTokens[0]);
        }
         operator = inputTokens[1];
         firstOperand = inputTokens[0];
         secondOperand = inputTokens[2];
    }

    private boolean checkCloseRequest(String message) {
        stopRequest = message.equals(Protocol.CLIENT_CLOSE_REQUEST);
        return stopRequest;
    }

    private void checkServerMessage(String message) throws IOException {
        String line = reader.readLine();
        if(line == null){
            throw new IOException("null line read from server // incorrect initiation");
        }
        if (message != null && !line.equals(message)) {
            throw new IOException("Unexpected message from server");
        }
        System.out.println(line);
    }

}