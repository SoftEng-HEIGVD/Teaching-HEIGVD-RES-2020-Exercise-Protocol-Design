package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import protocol.*;

public class Server {

    static final Logger LOG = Logger.getLogger(Server.class.getName());

    private enum OPERATORS{
        ADD,SUB,MUL,DIV;
    }

    private OPERATORS op;
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
            LOG.log(Level.INFO, "Creating a server socket and binding it on any of the available network interfaces and on port {0}", new Object[]{Integer.toString(Protocol.SERVER_PORT)});
            serverSocket = new ServerSocket(Protocol.SERVER_PORT);
            logServerSocketAddress(serverSocket);

            while(true){
                LOG.log(Level.INFO, "Waiting (blocking) for connection request on {0} : {1}", new Object[]{serverSocket.getInetAddress(),Integer.toString(serverSocket.getLocalPort())});
                clientSocket = serverSocket.accept(); //appel bloquant jusqu'à une connection

                //Si on arrive ici c'est qu'un client a demandé à se connecter
                LOG.log(Level.INFO, "A client has arrived. We now have a client socket with following attributes:");
                logSocketAddress(clientSocket);

                //On connecte un reader et un writer au client afin de communiquer avec lui
                LOG.log(Level.INFO,"Getting a Reader and a Writer connected to the client socket...");
                logSocketAddress(clientSocket);
                reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                //On a mtn un lien avec le client, on attend son initiation du protocole
                LOG.log(Level.INFO, "Waiting for protocol initiation...");
                String line;
                line = reader.readLine();

                //Si l'initiation n'est pas correcte, aucun message n'est envoyé au client pour lui indiquer de son erreur
                if(!line.equals("Hello!")){
                    LOG.log(Level.WARNING, String.format("Getting an incorrect initiation : %s instead of %s",line,Protocol.CLIENT_GREETINGS));
                    continue;
                }

                //On repond aux greetings du client
                LOG.log(Level.INFO,"Client has started protocol initiation...");
                writer.write(Protocol.SERVER_GREETINGS+'\n');

                ////////////////////////////////////////////////////////////////////////////////////////////////////////
                //////////Phase dans laquelle on va demander aux clients l'operateur ainsi que les 2 opérandes//////////
                ////////////////////////////////////////////////////////////////////////////////////////////////////////

                //////////////////////////////////////////////Operator//////////////////////////////////////////////////
                writer.write("I am a calculator.Please chose an operator : ADD, SUB, MUL and DIV.\n");
                writer.flush();

                LOG.log(Level.INFO,"Trying to read operator from client...");

                line = reader.readLine();

                LOG.log(Level.INFO, String.format("%s",line));

                if(!isLegalOperator(line)){
                    LOG.log(Level.SEVERE,String.format("Getting an incorrect operator : (%s)",line));
                    LOG.log(Level.SEVERE,"Connection will be close.");
                    writer.write("Wrong operator, closing connection" +'\n');
                    writer.flush();
                    continue;
                }

                op = OPERATORS.valueOf(line);

                LOG.log(Level.INFO,"Operator received");

                writer.write(String.format("Received %s\n",line));
                writer.flush();

                LOG.log(Level.INFO, "ACK for Operator send");

                //////////////////////////////////////////////FirstOperand//////////////////////////////////////////////
                writer.write("Enter first operand"+'\n');
                writer.flush();

                LOG.log(Level.INFO, "Asked for first operand");

                line = reader.readLine();
                if(!isNumeric(line)){
                    LOG.log(Level.SEVERE,String.format("Getting an incorrect operand : %s",line));
                    LOG.log(Level.SEVERE,"Connection will be close.");
                    writer.write("Wrong operand, closing connection"+'\n');
                    writer.flush();
                    continue;
                }

                LOG.log(Level.INFO, "FirstOperand received");

                firstOperand = Double.parseDouble(line);
                writer.write(String.format("Received %s",line) +'\n');
                LOG.log(Level.INFO, "ACK for firstOperand send");

                //////////////////////////////////////////////SecondOperand/////////////////////////////////////////////
                writer.write("Enter second operand" +'\n');
                writer.flush();

                LOG.log(Level.INFO, "Ask for secondOperand send");

                line = reader.readLine();
                if(!isNumeric(line)){
                    LOG.log(Level.SEVERE,String.format("Getting an incorrect operand : %s",line));
                    LOG.log(Level.SEVERE,"Connection will be close.");
                    writer.write("Wrong operand, closing connection"+'\n');
                    writer.flush();
                    continue;
                }

                LOG.log(Level.INFO, "SecondOperand received");

                secondOperand = Double.parseDouble(line);

                writer.write(String.format("Received %s",line)+'\n');

                LOG.log(Level.INFO, "ACK for secondOperand send");

                //////////////////////////////////////////////Result//////////////////////////////////////////////
                writer.write(getOperationResult()+'\n');
                writer.flush();

                line = reader.readLine();
                if(!line.equals("Done")){
                    LOG.log(Level.SEVERE,"Client has not received result");
                }

                //////////////////////////////////////////////Cleaning Up//////////////////////////////////////////////
                LOG.log(Level.INFO, "Cleaning up resources...");
                clientSocket.close();
                writer.close();
                reader.close();
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


    /**
     * Solve the operation we got from client
     * @return String containing equation and result of operation
     */
    private String getOperationResult()
    {
        LOG.log(Level.INFO,String.format("We have everything we need to do the maths..."));
        //Now we get everything we need for the calculation
        String opOutputFormat = "";
        switch(op){
            case ADD:
                result = firstOperand + secondOperand;
                opOutputFormat = "+";
                break;
            case SUB:
                result = firstOperand - secondOperand;
                opOutputFormat = "-";
                break;
            case MUL:
                result = firstOperand * secondOperand;
                opOutputFormat = "*";
                break;
            case DIV:
                result = firstOperand / secondOperand;
                opOutputFormat = "/";
                break;
        }

        LOG.log(Level.INFO,"Maths done...");
        return "Result : " + firstOperand + " " + opOutputFormat + " " + secondOperand  + " = " + result;
    }



    /**
     * Check if String is a numeric or not
     * @param line String on which we want to do the check
     * @return true if line is numeri, else false
     */
    private boolean isNumeric(String line) {
        try{
            Double.parseDouble(line);
        }catch(NumberFormatException nfe){
            return false;
        }
        return true;
    }


    /**
     * Check if String contains valid Operator
     * @param line String on which we want to do the check
     * @return true is line is a valid Operator
     */
    private boolean isLegalOperator(String line) {
        for(OPERATORS o : OPERATORS.values())
            if (line.equals(o.name())) {
                return true;
            }
        return false;
    }

    /**
     * A utility to print client socket information
     * @param clientSocket the socket that we want to log
     */
    private void logSocketAddress(Socket clientSocket) {
        LOG.log(Level.INFO, "       Local IP address: {0}", new Object[]{clientSocket.getLocalAddress()});
        LOG.log(Level.INFO, "             Local port: {0}", new Object[]{Integer.toString(clientSocket.getLocalPort())});
        LOG.log(Level.INFO, "  Remote Socket address: {0}", new Object[]{clientSocket.getRemoteSocketAddress()});
        LOG.log(Level.INFO, "            Remote port: {0}", new Object[]{Integer.toString(clientSocket.getPort())});
    }


    /**
     * A utility to print server socket information
     * @param serverSocket the socket that we want to log
     */
    private void logServerSocketAddress(ServerSocket serverSocket) {
        LOG.log(Level.INFO, "       Local IP address: {0}", new Object[]{serverSocket.getLocalSocketAddress()});
        LOG.log(Level.INFO, "             Local port: {0}", new Object[]{serverSocket.getLocalPort()});
        LOG.log(Level.INFO, "               is bound: {0}", new Object[]{serverSocket.isBound()});
    }

}
