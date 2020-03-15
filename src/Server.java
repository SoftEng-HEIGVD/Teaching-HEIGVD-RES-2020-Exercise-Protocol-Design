import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server {

    static final Logger LOG = Logger.getLogger(Server.class.getName());

    private final Charset encoding = StandardCharsets.UTF_8;
    private final int LISTEN_PORT = 2205;

    public void run(){
        LOG.info("Starting the server");

        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        BufferedReader reader = null;
        Scanner s = null;
        PrintWriter writer = null;

        try{
            LOG.log(Level.INFO, "Creating a server socket and binding it on port {0}", new Object[]{Integer.toString(LISTEN_PORT)});
            serverSocket = new ServerSocket(LISTEN_PORT);
            logServerSocketAddress(serverSocket);
            while(true){
                LOG.log(Level.INFO, "Waiting (blocking) for a connection request on {0} : {1}", new Object[]{serverSocket.getInetAddress(), Integer.toString(serverSocket.getLocalPort())});
                clientSocket = serverSocket.accept();

                LOG.log(Level.INFO, "A client has arrived. We now have a client socket with following attributes:");
                logSocketAddress(clientSocket);

                reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), encoding));
                writer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream(), encoding));

                writer.write("Welcome to the calculator ! \n" +
                        "You can enter an operation with '+', '-', '*' or '/' and 2 operands\n" +
                                "'exit' to quit \n");
                writer.flush();


                double result;
                char op;
                String inputLine;

                while((inputLine = reader.readLine()) != null){
                    if(inputLine.equals("exit")){
                        writer.write("Good bye ! \n");
                        writer.flush();
                        LOG.log(Level.INFO, "Exit from the client");
                        clientSocket.close();
                        break;
                    }
                    String[] operation = inputLine.split(" ");
                    try{
                        switch(operation[1]){
                            case "+" :
                                writer.write(Double.toString(Double.parseDouble(operation[0]) + Double.parseDouble(operation[2])) + "\n");
                                writer.flush();
                                break;
                            case "-" :
                                writer.write(Double.toString(Double.parseDouble(operation[0]) - Double.parseDouble(operation[2])) + "\n");
                                writer.flush();
                                break;
                            case "*" :
                                writer.write(Double.toString(Double.parseDouble(operation[0]) * Double.parseDouble(operation[2])) + "\n");
                                writer.flush();
                                break;
                            case "/" :
                                writer.write(Double.toString(Double.parseDouble(operation[0]) / Double.parseDouble(operation[2])) + "\n");
                                writer.flush();
                                break;
                        }
                    }catch (Exception ex){
                        LOG.log(Level.WARNING, "Wrong input from the client");
                        writer.write("Wrong input ! \n");
                        writer.flush();
                    }

                }


            }
        } catch (IOException ex){
            LOG.log(Level.SEVERE, ex.getMessage());
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

    public static void main(String[] args) {
        Server server = new Server();
        server.run();
    }
}
