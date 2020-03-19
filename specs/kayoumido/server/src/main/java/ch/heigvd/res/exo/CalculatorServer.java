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
    static final String QUIT = "QUIT";
    private void start() {
        LOG.info("Starting server...");

        ServerSocket server     = null;
        Socket client           = null;
        BufferedReader reader   = null;
        PrintWriter writer      = null;
        
        try {
            server = new ServerSocket(LISTEN_PORT);

            client  = server.accept();
            reader  = new BufferedReader(new InputStreamReader(client.getInputStream()));
            writer  = new PrintWriter(client.getOutputStream());

            writer.println("hello client");
            writer.flush();

            String cmd;
            while ((cmd = reader.readLine()) != null ) {
                if (cmd.equalsIgnoreCase(QUIT))
                    break;

                writer.println("> " + cmd.toUpperCase());
                writer.flush();
            }

            reader.close();
            client.close();
        } catch (IOException e) {
            LOG.log(Level.SEVERE, e.getMessage());
        } finally {
            LOG.log(Level.INFO, "We are done. Cleaning up resources, closing streams and sockets...");
            try {
                reader.close();
            } catch (IOException ex) {
                Logger.getLogger(CalculatorServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            writer.close();
            try {
                client.close();
            } catch (IOException ex) {
                Logger.getLogger(CalculatorServer.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                server.close();
            } catch (IOException ex) {
                Logger.getLogger(CalculatorServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void main(String[] args) {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s %n");

        CalculatorServer server = new CalculatorServer();

        server.start();
    }
}
