package zoubaidas;

import sun.reflect.annotation.ExceptionProxy;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.logging.Level.INFO;

public class ClientSide {

    //Setting server logger
    final static Logger LOG = Logger.getLogger(ServerSide.class.getName());
    //Port we are going to use to communicate with client application.


    public void start(String hostname, int port) {
        Socket clientSocket = null;
        BufferedReader reader = null;
        PrintWriter writer = null;

        LOG.info("I am a client... \n");
        try {
            LOG.log(Level.INFO, "Creating server socket and binding " + port);
            clientSocket = new Socket(hostname, port);
            //Logging server socket infos
            Utils.logSocketAddress(LOG, clientSocket);

            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            writer = new PrintWriter(clientSocket.getOutputStream());

            writer.println("add 1 2");
            writer.flush();
            while (true) ;
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            try {
                reader.close();
                writer.close();
                clientSocket.close();
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
    }
}

