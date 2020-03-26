/**
 * Authors : Christian Zaccaria & Nenad Rajic
 * Date 26.03.2020
 * Exercice Protocol-Design
 * File : Server.java
 */
package server;

import protocol.Protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {
    final static Logger LOG = Logger.getLogger(Server.class.getName());

    private int defaultPort;

    /**
     * Constructor of Server class, define the default port
     */
    public Server(){
        defaultPort = Protocol.DEFAULT_PORT;
    }

    /**
     * Start whole of engine for the server
     */
    public void start(){
        LOG.info("Starting server...");

        ServerSocket serverSocket;
        Socket clientSocket = null;
        BufferedReader reader = null;
        PrintWriter writer = null;
        boolean run = true;

        try{
            LOG.log(Level.INFO, "Try to starting socket server");
            serverSocket = new ServerSocket(defaultPort);
            logServerSocketAddress(serverSocket);
        } catch (IOException ex){
            LOG.log(Level.SEVERE, null, ex);
            return;
        }

        while(true){
            try{
                LOG.log(Level.INFO, "Waiting for connection with client on port " + defaultPort);
                clientSocket = serverSocket.accept();
                logSocketAddress(clientSocket);

                LOG.log(Level.INFO, "Connect a reader and writer");
                reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                writer = new PrintWriter(clientSocket.getOutputStream());

                LOG.log(Level.INFO, "Waiting for communication");
                String line;

                writer.write("Welcome to the calculator ! Please choose an operation ADD / SUB / MULTIPLY / DIV [number1] [number2] \n");
                writer.write("If you want to stop to calcule enter STOP \n");
                writer.flush();

                LOG.log(Level.INFO,"Listen and process client request");

                while((line = reader.readLine()) != null && run){
                    String[] tokens = line.split(" ");
                    String operation = tokens[0];

                    if(tokens.length != 3){
                        if(tokens[0] == "STOP"){
                            run = false;
                            break;
                        }else{
                            writer.write("Too many /few arguments");
                            writer.flush();
                        }
                        continue;
                    }

                    if((!isNumerical(tokens[1]) || (!isNumerical(tokens[2])))){
                        writer.println("Error, insert only numbers to calculate");
                        writer.flush();
                        continue;
                    }

                    switch(operation){
                        case(Protocol.ADD):
                            writer.println("Result of " + tokens[1] + " + " + tokens[2] + " = " + (Double.parseDouble(tokens[1]) + Double.parseDouble(tokens[2])));
                            break;
                        case (Protocol.SUB):
                            writer.println("Result of " + tokens[1] + " - " + tokens[2] + " = " + (Double.parseDouble(tokens[1]) - Double.parseDouble(tokens[2])));
                            break;
                        case (Protocol.MULTI):
                            writer.println("Result of " + tokens[1] + " * " + tokens[2] + " = " + (Double.parseDouble(tokens[1]) * Double.parseDouble(tokens[2])));
                            break;
                        case(Protocol.DIVIDE):
                            writer.println(Double.parseDouble(tokens[2]) != 0 ? "Result of " + tokens[1] + " / " + tokens[2] + " = " +
                                    (Double.parseDouble(tokens[1]) / Double.parseDouble(tokens[2])) : "Error : cannot divide by 0");
                            break;
                        default:
                            writer.println("Error, request is not an ADD / SUB / MULTIPLY / DIV operation");
                            break;
                    }
                    writer.flush();

                }

                LOG.log(Level.INFO,"Cleaning ressources...");
                clientSocket.close();
                reader.close();
                writer.close();

            } catch (IOException ex){
                if(reader != null){
                    try{
                        reader.close();
                    } catch (IOException ex1){
                        LOG.log(Level.SEVERE, ex1.getMessage(), ex1);
                    }
                }
                if(writer != null){
                    writer.close();
                }
                if(clientSocket != null){
                    try {
                        clientSocket.close();
                    } catch (IOException ex1){
                        LOG.log(Level.SEVERE, ex1.getMessage(), ex1);
                    }
                }
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
            }
        }
    }

    /**
     * Check if can transform a string to a number
     * @param line String to convert to number
     * @return true if he can convert, false if he cannot
     */
    private boolean isNumerical(String line) {
        try{
            Double.parseDouble(line);
        }catch(NumberFormatException format){
            return false;
        }
        return true;
    }

    /**
     * A utility method to print server socket information
     *
     * @param serverSocket the socket that we want to log
     */
    private void logServerSocketAddress(ServerSocket serverSocket) {
        LOG.log(Level.INFO, "       Local IP address: {0}", new Object[]{serverSocket.getLocalSocketAddress()});
        LOG.log(Level.INFO, "             Local port: {0}", new Object[]{Integer.toString(serverSocket.getLocalPort())});
        LOG.log(Level.INFO, "               is bound: {0}", new Object[]{serverSocket.isBound()});
    }

    /**
     * A utility method to print socket information
     *
     * @param clientSocket the socket that we want to log
     */
    private void logSocketAddress(Socket clientSocket) {
        LOG.log(Level.INFO, "       Local IP address: {0}", new Object[]{clientSocket.getLocalAddress()});
        LOG.log(Level.INFO, "             Local port: {0}", new Object[]{Integer.toString(clientSocket.getLocalPort())});
        LOG.log(Level.INFO, "  Remote Socket address: {0}", new Object[]{clientSocket.getRemoteSocketAddress()});
        LOG.log(Level.INFO, "            Remote port: {0}", new Object[]{Integer.toString(clientSocket.getPort())});
    }
}


