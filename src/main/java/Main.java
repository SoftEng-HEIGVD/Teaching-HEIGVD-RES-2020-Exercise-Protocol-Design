import client.Client;
import server.Server;

public class Main {
    public static void main(String[] args) {
        if(args.length > 0 && args[0].equals("startClient")){
            Client client = new Client();
            client.run();
        }
        else if(args.length > 0 && args[0].equals("startServer")){
            Server server = new Server();
            server.serveClients();
        }
        else{
            System.out.println("Error, choose 'startServer' or 'startClient' as parameter");
        }
    }
}
