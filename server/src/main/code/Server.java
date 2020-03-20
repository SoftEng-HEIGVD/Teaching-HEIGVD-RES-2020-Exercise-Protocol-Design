package main.code;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server implements Runnable{
    static final Logger LOG = Logger.getLogger(Server.class.getName());

    private ServerSocket serverSocket;
    private Socket clientSocket;
    private int listenPort = 6942;
    private final ArrayList<ClientHandler> handlers = new ArrayList();
    private boolean run = true;
    @Override
    public void run() {
        try {
            LOG.log(Level.INFO, "Starting Server Socket");
            serverSocket = new ServerSocket(listenPort);
            logServerSocketAddress(serverSocket);

            while (run) {
                LOG.log(Level.INFO, "Waiting on connection");
                clientSocket = serverSocket.accept();

                LOG.log(Level.INFO, "New Client:");
                logSocketAddress(clientSocket);
                try{
                    ClientHandler ch = new ClientHandler(clientSocket);
                    handlers.add(ch);
                    new Thread(ch).start();
                } catch (IOException ex) {
                    LOG.log(Level.SEVERE, "Cannot create Client handler\n" + ex.getMessage());
                }

            }

        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage());
        } finally {
            LOG.log(Level.INFO, "We are done. Cleaning up resources, closing streams and sockets...");

        }
    }

    public void quit(){
        run = false;
        try{
            serverSocket.close();
        }catch (IOException ex){
            ex.printStackTrace();
        }

        for (ClientHandler ch: handlers) {
            try{
                ch.close();
            }catch (IOException ex){
                ex.printStackTrace();
            }
        }
    }

    /**
     * A utility method to print server socket information
     * Author: Liechti Olivier
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
     * Author: Liechti Olivier
     *
     * @param clientSocket the socket that we want to log
     */
    private void logSocketAddress(Socket clientSocket) {
        //LOG.log(Level.INFO, "       Local IP address: {0}", new Object[]{clientSocket.getLocalAddress()});
        //LOG.log(Level.INFO, "             Local port: {0}", new Object[]{Integer.toString(clientSocket.getLocalPort())});
        LOG.log(Level.INFO, "  Remote Socket address: {0}", new Object[]{clientSocket.getRemoteSocketAddress()});
        //LOG.log(Level.INFO, "            Remote port: {0}", new Object[]{Integer.toString(clientSocket.getPort())});
    }



}
