/**
 * Main class for the CalcServer
 * Initializes the server on port 2112 as defined in our specifications
 *
 * @author Tiffany Bonzon
 */
public class CalcServer {
    public static void main(String[] args) {
        Server srv = new Server(2112);
        srv.serveClients();
    }
}
