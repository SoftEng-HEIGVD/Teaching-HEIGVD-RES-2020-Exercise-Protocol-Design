package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import protocol.*;

public class Server {

    static final Logger LOG = Logger.getLogger(Server.class.getName());

    private String operator;
    private double firstOperand;
    private double secondOperand;
    private double result;

    public void start(){
        LOG.info("Starting server...");

        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        BufferedReader reader = null;
        BufferedWriter writer = null;

        try{
            LOG.log(Level.INFO, "Creating a server socket and binding it on port {0}", new Object[]{Integer.toString(Protocol.PORT_NO)});
            serverSocket = new ServerSocket(Protocol.PORT_NO);
            logServerSocketAddress(serverSocket);

            while(true){
                LOG.log(Level.INFO, "Waiting (blocking) for connection request on address {0} port {1}", new Object[]{serverSocket.getInetAddress(),Integer.toString(serverSocket.getLocalPort())});
                clientSocket = serverSocket.accept();


                LOG.log(Level.INFO, "A client has arrived. We now have a client socket with following attributes:");
                logSocketAddress(clientSocket);
                reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                LOG.log(Level.INFO, "Waiting for protocol initiation...");
                String line;
                line = reader.readLine();

                if(!line.equals(Protocol.CLIENT_OPENING)){
                    LOG.log(Level.WARNING, String.format("Getting an incorrect initiation : %s instead of %s",line,Protocol.CLIENT_OPENING));
                    clientSocket.close();
                    writer.close();
                    reader.close();
                    continue;
                }

                LOG.log(Level.INFO,"Client has started protocol initiation...");
                writer.write(Protocol.SERVER_OPENING+'\n');
                writer.flush();

                LOG.log(Level.INFO,"Trying to read from client...");
                while(true){
                    line = reader.readLine();
                    LOG.log(Level.INFO, String.format("%s",line));

                    //Commande invalide
                    if(!getAndCheckUserInput(line)){
                        LOG.log(Level.WARNING,"Invalid user input");
                        writer.write(Protocol.SERVER_ERROR);
                        writer.flush();
                        continue;
                    }

                    //Commande valide
                    if(operator.equals(Protocol.CLIENT_CLOSE_REQUEST)){ //Close request
                        LOG.log(Level.INFO, "Closing...");
                        clientSocket.close();
                        writer.close();
                        reader.close();
                        break;
                    }else{                                              //Résultat opération
                        LOG.log(Level.INFO, "Computing...");
                        writer.write(getOperationResult());
                        writer.flush();
                    }
                }
            }
        }catch(IOException ex){
            if(reader != null){
                try{
                    reader.close();
                }catch(IOException ex1){
                    LOG.log(Level.SEVERE, ex1.getMessage(), ex1);
                }
            }
            if(writer != null){
                try{
                    writer.close();
                }catch(IOException ex1){
                    LOG.log(Level.SEVERE, ex1.getMessage(),ex1);
                }
            }
            if(clientSocket != null){
                try{
                    clientSocket.close();
                }catch (IOException ex1){
                    LOG.log(Level.SEVERE, ex1.getMessage(), ex1);
                }
            }
            LOG.log(Level.SEVERE, ex.getMessage(),ex);
        }
    }

    private boolean checkCloseRequest(String message) {
        return message.equals(Protocol.CLIENT_CLOSE_REQUEST);
    }

    private boolean getAndCheckUserInput(String input) {
        boolean valid = false;
        String[] inputTokens = new String[3];

        StringTokenizer st = new StringTokenizer(input, " ", false);
        int numberOfTokens = 0;
        while(st.hasMoreTokens() && numberOfTokens < 3){
            inputTokens[numberOfTokens++] = st.nextToken();
        }


        if(numberOfTokens == 3 && !st.hasMoreTokens()){
            if(isNumeric(inputTokens[0]) && isNumeric(inputTokens[2]) && isOperator(inputTokens[1])){
                operator = inputTokens[1];
                firstOperand = Double.parseDouble(inputTokens[0]);
                secondOperand = Double.parseDouble(inputTokens[2]);

                if(!(secondOperand == 0 && operator.equals("/"))){
                    valid = true;
                }
            }
        }else if(numberOfTokens == 1){
            if(checkCloseRequest(inputTokens[0])){
                operator = inputTokens[0];
                valid = true;
            }
        }
        return valid;
    }


    private String getOperationResult()
    {
        if(operator.equals("+")){       //TODO sale
            result = firstOperand + secondOperand;
        }else if(operator.equals("-")){
            result = firstOperand - secondOperand;
        }else if(operator.equals("*")){
            result = firstOperand * secondOperand;
        }else if(operator.equals("/")){
            result = firstOperand / secondOperand;
        }

        return firstOperand + " " + operator + " " + secondOperand  + " = " + result + "\n";
    }


    private boolean isNumeric(String line) {
        try{
            Double.parseDouble(line);
        }catch(NumberFormatException nfe){
            return false;
        }
        return true;
    }


    private boolean isOperator(String input) {
        for(String op : Protocol.OPERATORS)
            if (input.equals(op)){
                return true;
            }
        return false;
    }

    private void logSocketAddress(Socket clientSocket) {
        LOG.log(Level.INFO, "       Local IP address: {0}", new Object[]{clientSocket.getLocalAddress()});
        LOG.log(Level.INFO, "             Local port: {0}", new Object[]{Integer.toString(clientSocket.getLocalPort())});
        LOG.log(Level.INFO, "  Remote Socket address: {0}", new Object[]{clientSocket.getRemoteSocketAddress()});
        LOG.log(Level.INFO, "            Remote port: {0}", new Object[]{Integer.toString(clientSocket.getPort())});
    }

    private void logServerSocketAddress(ServerSocket serverSocket) {
        LOG.log(Level.INFO, "       Local IP address: {0}", new Object[]{serverSocket.getLocalSocketAddress()});
        LOG.log(Level.INFO, "             Local port: {0}", new Object[]{serverSocket.getLocalPort()});
        LOG.log(Level.INFO, "               is bound: {0}", new Object[]{serverSocket.isBound()});
    }

}