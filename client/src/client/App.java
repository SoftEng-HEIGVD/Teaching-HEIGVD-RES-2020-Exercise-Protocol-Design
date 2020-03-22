package client;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class App {
    public static void main(String[] args) throws UnknownHostException {
        Client client = new Client(5190, InetAddress.getLocalHost());
        client.phoneServer();
    }
}
