package egremyb.server;

import egremyb.server.Server;

public class main {
    public static void main(String[] args){

        Server server = new Server();

        System.out.println("Starting Server\n");
        server.serveClients();
    }
}
