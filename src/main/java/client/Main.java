package client;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {

        final String HOST = "localhost";

        try {
            Client c = new Client(HOST);
            c.sendRequest("2 3 * 4 +");
            c.verifyResponse();

            c.terminate();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
