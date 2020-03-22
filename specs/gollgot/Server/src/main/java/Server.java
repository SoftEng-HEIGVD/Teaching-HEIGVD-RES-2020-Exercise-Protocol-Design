import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

/**
 * This class is a server implementation to do basic calculation from a specific client
 *
 * @author Loïc Dessaules
 */
public class Server {

    final static Logger LOG = Logger.getLogger(Server.class.getName());
    private int port;


    /**
     * Run a server with the port in args parameters. This way it can be used standalone
     * @param args [0] port
     */
    public static void main(String[] args) {
        if(args.length != 1){
            System.out.println("One argument require : The port number");
        }else{
            try {
                int port = Integer.parseInt(args[0]);
                Server server = new Server(port);
                server.serveClients();
            }catch(NumberFormatException e){
                System.out.println("Wrong argument, the port must be an integer value");
            }
        }
    }


    public Server(int port){
        this.port = port;
    }

    /**
     * Initiates the process. Server creates a socket and binds it to the chosen port. Then waits for
     * clients inside an infinite loop. When client arrives, server will read it's input line.
     */
    public void serveClients(){
        LOG.info("Starting the Receptionist Worker on a new thread ...");
        new Thread(new ReceptionistWorker()).start();
    }


    /**
     * This inner class implements the behaviour of the "Receptionist", whose responsibility is to
     * listen for incoming connection request. As soon as a new client has arrived, the receptionist
     * delegates the processing to "servant" who will execute on its own thread.
     */
    private class ReceptionistWorker implements Runnable {

        @Override
        public void run() {
            ServerSocket serverSocket;

            try {
                serverSocket = new ServerSocket(port);
            } catch (IOException e) {
                LOG.warning("Error occurred on the ServerSocket initialisation : " + e.getMessage());
                return;
            } catch(IllegalArgumentException e){
                LOG.warning("Error port value out of range");
                return;
            }

            while(true){
                LOG.info("Waiting for a new client on port " + port);
                try {
                    Socket clientSocket = serverSocket.accept();
                    LOG.info("A new client has arrived. Starting a new thread and delegating work to a new servant...");
                    new Thread(new ServantWorker(clientSocket)).start();
                } catch (IOException e) {
                    LOG.warning("Error occurred on the Socket Client initialisation (server accept) : " + e.getMessage());
                }
            }

        }

    }

    /**
     * This inner class implements the behavior of the "servants", whose
	 * responsibility is to take care of clients once they have connected. This
	 * is where we implement the application protocol logic, more details about the protocol
     * into the protocol.md file
     */
    private class ServantWorker implements Runnable {

        Socket clientSocket;
        BufferedReader in = null;
        PrintWriter out = null;

        private final static String INIT_MSG = "HELLO";
        private final static String SERVER_READY_MSG = "READY";
        private final static String SERVER_ERROR_MSG = "ERROR";
        private final static String EXIT_MSG = "BYE";

        ServantWorker(Socket clientSocket) {
            this.clientSocket = clientSocket;
            try {
                // Notice that we have a UTF-8 encoding
                this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), StandardCharsets.UTF_8));
                this.out = new PrintWriter(clientSocket.getOutputStream());
            } catch (IOException e) {
                LOG.warning("Error occurred on in / out buffer creation : " + e.getMessage());
            }
        }


        @Override
        public void run() {
            String line;
            boolean shouldRun = true;

            try {
                LOG.info("Reading until client sends BYE or close connection ...");
                while((shouldRun && (line = in.readLine()) != null)){
                    // Client ask for calculation
                    if(line.equalsIgnoreCase(INIT_MSG)){
                        out.println(SERVER_READY_MSG);
                        out.flush();
                        // Loop through all calculation the client want to do
                        while(shouldRun && (line = in.readLine()) != null){
                            // Client ask to quit
                            if(line.equalsIgnoreCase(EXIT_MSG)){
                                shouldRun = false;
                            }
                            // Otherwise client may ask for a calculation
                            else{
                                String result = calculate(line);
                                if(!result.equals("invalid")){
                                    out.println(result);
                                }else{
                                    out.println(SERVER_ERROR_MSG);
                                }
                                out.flush();
                            }
                        }
                    }

                    // Client ask to quit
                    if(line.equalsIgnoreCase(EXIT_MSG)){
                        break;
                    }

                    // Unknown keyword -> error
                    out.println(SERVER_ERROR_MSG);
                    out.flush();
                }

                LOG.info("Cleaning up resources...");
                clientSocket.close();
                in.close();
                out.close();

            } catch (IOException e) {
                cleanUp();
                LOG.warning("Error occurred on read line : " + e.getMessage());
            }
        }

        /**
         * Cleanup all our opened resources
         */
        private void cleanUp(){
            // Correctly close the BufferedReader
            if(in != null){
                try {
                    in.close();
                } catch (IOException e) {
                    LOG.warning("Error occurred on cleaning resource : " + e.getMessage());
                }
            }

            // Correctly close the PrintWriter
            if(out != null){
                out.close();
            }

            // Correctly close the Socket
            if(clientSocket != null) {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    LOG.warning("Error occurred on cleaning resource : " + e.getMessage());
                }
            }
        }

        /**
         * Calculate a basic math calculation that respect this format : <nb1> <space> <operator> <space> <nb2>
         * And the operator can be +, -, *, /
         * @param calculation The String line contains the calculation
         * @return "invalid" if an error occurred, the result as a String otherwise
         */
        private String calculate(String calculation){
            int nb1 = 0;
            int nb2 = 0;
            int answer = 0;
            String operation;
            // Split our calculation in 3 parts (operand1, operator, operand2)
            String[] parts = calculation.split(" ");
            boolean error = false;

            // We must have our 3 parts for a right calculation
            if(parts.length != 3) {
                error = true;
            } else{
                try {
                    nb1 = Integer.parseInt(parts[0]);
                    nb2 = Integer.parseInt(parts[2]);
                } catch(NumberFormatException e){
                    LOG.warning("Error occurred on calculation parsing : " + e.getMessage());
                    error = true;
                }

                if(!error) {
                    operation = parts[1];
                    switch (operation) {
                        case "+":
                            answer = nb1 + nb2;
                            break;
                        case "-":
                            answer = nb1 - nb2;
                            break;
                        case "*":
                            answer = nb1 * nb2;
                            break;
                        case "/":
                            try{
                                answer = nb1 / nb2;
                            }catch(ArithmeticException e){
                                LOG.warning("Error occurred, arithmetic exception : " + e.getMessage());
                                error = true;
                            }
                            break;
                        default:
                            error = true;
                            break;
                    }
                }

            }

            return error ? "invalid" : String.valueOf(answer);
        }

    }

}
