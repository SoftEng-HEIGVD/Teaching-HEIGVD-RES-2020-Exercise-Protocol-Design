import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Server {

    static final Logger LOG = Logger.getLogger(Server.class.getName());

    private final int LISTEN_PORT = 6666;
    private final String CONFIRM_CONNECTION_MESSAGE = "no";
    private final String EXIT_MESSAGE = "exit";
    private final String[] AVAILABLE_OPERATIONS = {"-", "+", "*", "/"};

    public void run(){
        LOG.info("Starting the server");

        ServerSocket serverSocket = null;
        Socket clientSocket = null;
        BufferedReader reader = null;
        PrintWriter writer = null;


        try{
            LOG.log(Level.INFO, "Creating a server socket and binding it on port {0}", new Object[]{Integer.toString(LISTEN_PORT)});
            serverSocket = new ServerSocket(LISTEN_PORT);

            while(true){
                LOG.log(Level.INFO, "Waiting (blocking) for a connection request on {0} : {1}", new Object[]{serverSocket.getInetAddress(), Integer.toString(serverSocket.getLocalPort())});
                clientSocket = serverSocket.accept();

                LOG.log(Level.INFO, "New client has arrived");
                LOG.log(Level.INFO, "Getting Reader and Writer connected to the client");
                writer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
                writer.println("Corona? [yes/no]");
                writer.flush();

                reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String line = reader.readLine();

                if(!line.contentEquals(CONFIRM_CONNECTION_MESSAGE)){
                    writer.write("Infected clients not allowed here");
                    writer.flush();
                    LOG.log(Level.WARNING, "Infected client tried to connect");
                    clientSocket.close();
                    break;
                }


                writer.println("Welcome on the server Calculator. Enter a calculus to be solved. \n" +
                                "List of available operations: \n" +
                                "- Addition / Substraction \n" +
                                "- Multiplication / Division \n \n" +
                                "You can quit at any moment by sending exit \n");
                writer.flush();

                LOG.log(Level.INFO, "Welcome message sent to client");

                while((line = reader.readLine()) != null) {
                    if (line.equals(EXIT_MESSAGE)) {
                        writer.println("Have a nice day!");
                        writer.flush();
                        LOG.log(Level.INFO, "Client disconnected");
                        clientSocket.close();
                        break;
                    }


                    line = line.replace(" ", "");
                    String operation = "";
                    String[] operands = new String[2];
                    for (String s : AVAILABLE_OPERATIONS) {
                        int operationIndex;
                        if ((operationIndex = line.indexOf(s)) != -1) {
                            operation = s;
                            operands[0] = line.substring(0, operationIndex);
                            operands[1] = line.substring(operationIndex + 1);
                        }
                    }

                    if (operation.isEmpty()) {
                        throw new RuntimeException("No valid operation found");
                    }

                    switch (operation) {
                        case "+":
                            writer.println(Double.toString(Double.parseDouble(operands[0]) + Double.parseDouble(operands[1])));
                            break;
                        case "-":
                            writer.println(Double.toString(Double.parseDouble(operands[0]) - Double.parseDouble(operands[1])));
                            break;
                        case "*":
                            writer.println(Double.toString(Double.parseDouble(operands[0]) * Double.parseDouble(operands[1])));
                            break;
                        case "/":
                            writer.println(Double.toString(Double.parseDouble(operands[0]) / Double.parseDouble(operands[1])));
                            break;
                    }
                    writer.flush();
                }
            }

        }

        catch(IOException | RuntimeException e){
            LOG.log(Level.SEVERE, e.getMessage());
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.run();
    }
}
