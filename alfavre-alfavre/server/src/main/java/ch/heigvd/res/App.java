package main.java.ch.heigvd.res;

import java.io.IOException;

public class App {

    /**
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {

        Server server = new Server();
        server.run();
    }

}