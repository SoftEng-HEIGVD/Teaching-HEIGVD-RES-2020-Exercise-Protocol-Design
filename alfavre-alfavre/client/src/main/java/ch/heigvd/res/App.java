package main.java.ch.heigvd.res;

import java.io.IOException;
import java.net.InetAddress;


public class App {

    /**
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        Client client = new Client(InetAddress.getLocalHost());
        client.start();

    }

}