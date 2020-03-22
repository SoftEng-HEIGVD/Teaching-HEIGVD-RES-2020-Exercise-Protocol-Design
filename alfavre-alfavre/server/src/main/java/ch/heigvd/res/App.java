package main.java.ch.heigvd.res;

import java.io.IOException;

public class App {

    /**
     * server, I half assed the log, it logs almost nothing
     * Doesn't work with double, int only (idk why, but i wont fix)
     * Does return the double division (3/6=0.5 and not 0)
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {

        Server server = new Server();
        server.run();
    }

}