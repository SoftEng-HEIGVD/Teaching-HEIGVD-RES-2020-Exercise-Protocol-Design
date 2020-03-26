/**
 * Authors : Christian Zaccaria & Nenad Rajic
 * Date 26.03.2020
 * Exercice Protocol-Design
 * File : Client.java
 */
package client;

import protocol.Protocol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {

    final static Logger LOG = Logger.getLogger(Client.class.getName());

    private int defaultPort;

    /**
     * Constructor of Client class, define the default port
     */
    public Client(){
        defaultPort = Protocol.DEFAULT_PORT;
    }

    Socket clientSocket = null;
    BufferedReader reader = null;
    PrintWriter writer = null;
    boolean connected = false;
    String userName;

    /**
     * This inner class implements the Runnable interface, so that the run()
     * method can execute on its own thread. This method reads data sent from the
     * server, line by line, until the connection is closed or lost.
     */
    class NotificationListener implements Runnable {

        @Override
        public void run() {
            String notification;
            try {
                while ((connected && (notification = reader.readLine()) != null)) {
                    LOG.log(Level.INFO, "Server notification for {1}: {0}", new Object[]{notification,userName});
                }
            } catch (IOException e) {
                LOG.log(Level.SEVERE, "Connection problem in client used by {1}: {0}", new Object[]{e.getMessage(),userName});
                connected = false;
            } finally {
                cleanup();
            }
        }
    }


    /**
     * This method is used to connect to the server and to inform the server that
     * the user "behind" the client has a name (in other words, the HELLO command
     * is issued after successful connection).
     *
     * @param serverAddress the IP address used by the Presence Server
     * @param serverPort the port used by the Presence Server
     */
    public void connect(String serverAddress, int serverPort, String userName) {
        try {
            clientSocket = new Socket(serverAddress, serverPort);
            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            writer = new PrintWriter(clientSocket.getOutputStream());
            connected = true;
            this.userName = userName;
        } catch (IOException e) {
            LOG.log(Level.SEVERE, "Unable to connect to server: {0}", e.getMessage());
            cleanup();
            return;
        }
        // Let us start a thread, so that we can listen for server notifications
        new Thread(new NotificationListener()).start();

    }

    /**
     * Request to calculate
     * @param request
     */
    public void calculate(String request){
        writer.println(request);
        writer.flush();
    }

    /**
     * Disconnection with the server
     */
    public void disconnect() {
        LOG.log(Level.INFO, "{0} has requested to be disconnected.", userName);
        connected = false;
        writer.println("BYE");
        cleanup();
    }

    /**
     * Clean all stuff used to conmmunicate with the server
     */
    private void cleanup() {

        try {
            if (reader != null) {
                reader.close();
            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }

        if (writer != null) {
            writer.close();
        }

        try {
            if (clientSocket != null) {
                clientSocket.close();
            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }
}