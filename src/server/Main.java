package server;

import protocol.Protocol;

public class Main {
    public static void main(String[] args) {
        /*Server server = new Server();
        server.start();*/

        ServerMT serverMT = new  ServerMT(Protocol.PORT_NO);
        serverMT.serveClients();
    }
}
