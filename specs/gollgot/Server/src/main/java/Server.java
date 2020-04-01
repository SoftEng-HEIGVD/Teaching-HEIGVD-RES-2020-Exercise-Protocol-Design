import java.awt.image.RescaleOp;
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
    public final static int DEFAULT_PORT = 12000; // Can be used as the default port
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
        ReceptionistWorker receptionistWorker = new ReceptionistWorker(port);
        new Thread(receptionistWorker).start();
    }


}
