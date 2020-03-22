package zoubaidas;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.logging.Level.INFO;

public class ClientSide {

    //Setting server logger
    final static Logger LOG = Logger.getLogger(ServerSide.class.getName());
    //Port we are going to use to communicate with client application.
    private final int LISTEN_PORT = 2342;


    public void start(String ip_adress) {
        Socket clientSocket = null;
        BufferedReader reader = null;
        PrintWriter writer = null;

        LOG.info("Starting connection with server ... \n");
        try {
            clientSocket = new Socket(ip_adress, LISTEN_PORT);
        }
    }
}
