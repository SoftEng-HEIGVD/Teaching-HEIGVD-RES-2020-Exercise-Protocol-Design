/**
 * Test program for network protocol.
 *
 * @author Robin Demarta, Loïc Dessaules
 */

public class Main {

    public static void main(String[] args) {
	    Server server = new Server(12000);
	    server.serveClients();

	    Client client = new Client(Client.DEFAULT_SERVER_ADDRESS, 12000);
	    client.calculate("37 + 5"); // Correct calculation request
	    client.calculate("Hey"); // Wrong message
        client.calculate("68 + 29"); // Another correct calculation request

	    client.close();

    }

}
