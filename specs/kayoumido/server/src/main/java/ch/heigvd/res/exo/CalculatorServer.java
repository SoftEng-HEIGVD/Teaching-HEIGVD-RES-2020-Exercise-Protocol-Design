package ch.heigvd.res.exo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CalculatorServer {
    private final int LISTEN_PORT = 49153;

    static final Logger LOG = Logger.getLogger(CalculatorServer.class.getName());

    private void start() {
        LOG.info("Starting server...");

        try {
            ServerSocket serverSocket = new ServerSocket(LISTEN_PORT);

            Socket client           = serverSocket.accept();
            BufferedReader reader   = new BufferedReader(new InputStreamReader(client.getInputStream()));
            PrintWriter writer      = new PrintWriter(client.getOutputStream());

            String cmd;
            while ((cmd = reader.readLine()) != null ) {
                writer.println("> " + cmd.toUpperCase());
                writer.flush();
            }
            
            reader.close();
            client.close();
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getMessage());
        }
    }

    public static void main(String[] args) {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s %n");

        CalculatorServer server = new CalculatorServer();

        server.start();
    }
}
