package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server{

    static final Logger LOG = Logger.getLogger(Server.class.getName());
    private final Charset encoding = StandardCharsets.UTF_8;
    private final int LISTEN_PORT = 2205;

    public void serveClients(){
        new Thread(new ReceptionistWorker()).start();
    }

    private class ReceptionistWorker implements Runnable{
        @Override
        public void run() {
            ServerSocket serverSocket = null;

            try{
                LOG.log(Level.INFO,
                        "Creating a server socket and binding it on port {0}",
                        new Object[]{Integer.toString(LISTEN_PORT)});
                serverSocket = new ServerSocket(LISTEN_PORT);
                logServerSocketAddress(serverSocket);
            }
            catch (IOException ex){
                LOG.log(Level.SEVERE, ex.getMessage());
            }

            while(true){
                LOG.log(Level.INFO,
                        "Waiting (blocking) for a connection request on {0} : {1}",
                        new Object[]{serverSocket.getInetAddress(),
                        Integer.toString(serverSocket.getLocalPort())});
                try{
                    Socket clientSocket = serverSocket.accept();
                    new Thread(new ServantWorker(clientSocket)).start();
                }
                catch (IOException ex){
                    LOG.log(Level.SEVERE, ex.getMessage(), ex);
                }

            }
        }
        private class ServantWorker implements Runnable{
            Socket clientSocket;
            BufferedReader reader = null;
            PrintWriter writer = null;

            public ServantWorker(Socket clientSocket){
                try{
                    LOG.log(Level.INFO, "A client has arrived. We now have a client socket with following attributes:");
                    this.clientSocket = clientSocket;
                    reader = new BufferedReader(new
                            InputStreamReader(clientSocket.getInputStream(), encoding));
                    writer = new PrintWriter(new
                            OutputStreamWriter(clientSocket.getOutputStream(), encoding));
                }catch (IOException ex){
                    LOG.log(Level.SEVERE, ex.getMessage(), ex);
                }
            }
            @Override
            public void run() {
                writer.write("Welcome to the calculator ! " +
                        "You can enter an operation with '+', '-', '*' or '/' and 2 operands," +
                        " 'exit' to quit \n");
                writer.flush();

                double result;
                char op;
                String inputLine;
                boolean shouldRun = true;

                try{
                    while((shouldRun && (inputLine = reader.readLine()) != null)) {
                        if (inputLine.equals("exit")) {
                            writer.write("Good bye ! \n");
                            writer.flush();
                            LOG.log(Level.INFO, "Exit from the client");
                            shouldRun = false;
                        }

                        if (shouldRun) {
                            String[] operation = inputLine.split(" ");
                            try {
                                switch (operation[1]) {
                                    case "+":
                                        writer.write(Double.toString(Double.parseDouble(operation[0]) + Double.parseDouble(operation[2])) + "\n");
                                        writer.flush();
                                        break;
                                    case "-":
                                        writer.write(Double.toString(Double.parseDouble(operation[0]) - Double.parseDouble(operation[2])) + "\n");
                                        writer.flush();
                                        break;
                                    case "*":
                                        writer.write(Double.toString(Double.parseDouble(operation[0]) * Double.parseDouble(operation[2])) + "\n");
                                        writer.flush();
                                        break;
                                    case "/":
                                        writer.write(Double.toString(Double.parseDouble(operation[0]) / Double.parseDouble(operation[2])) + "\n");
                                        writer.flush();
                                        break;
                                }
                            } catch (Exception ex) {
                                LOG.log(Level.WARNING, "Wrong input from the client");
                                writer.write("Wrong input ! \n");
                                writer.flush();
                            }
                        }
                    }
                }
                catch (IOException ex){
                    if(reader != null){
                        try{
                            reader.close();
                        }
                        catch(IOException ex1){
                           LOG.log(Level.SEVERE, ex.getMessage(), ex1);
                        }
                    }
                    if(writer != null){
                        writer.close();
                    }
                    if(clientSocket != null){
                        try{
                            clientSocket.close();
                        }
                        catch(IOException ex1){
                            LOG.log(Level.SEVERE, ex.getMessage(), ex1);
                        }
                    }
                    LOG.log(Level.SEVERE, ex.getMessage(), ex);
                }
            }
        }
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
}
