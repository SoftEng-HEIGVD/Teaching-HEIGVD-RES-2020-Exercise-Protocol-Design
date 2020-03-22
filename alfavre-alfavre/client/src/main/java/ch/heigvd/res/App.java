package main.java.ch.heigvd.res;

import java.io.IOException;
import java.net.InetAddress;


public class App {

    /**
     * Doesn't work with double, int only (idk why, but i wont fix)
     * Does return the double division (3/6=0.5 and not 0)
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        Client client = new Client(InetAddress.getLocalHost());
        client.start();

    }

}