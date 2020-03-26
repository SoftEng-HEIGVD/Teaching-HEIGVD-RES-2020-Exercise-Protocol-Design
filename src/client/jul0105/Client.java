package client.jul0105;

import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class Client {
    private static final String SERVER_IP = "";
    private static final int SERVER_PORT = 25566;
    private static final String HANDSHAKE = "CALC CLIENT";
    private static final String HANDSHAKE_RESPONSE = "CALC SERVER";

    private static final Logger LOG = Logger.getLogger(Client.class.getName());

    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean isConnected = false;
    private String supportedOperations;

    public double calculate(double op1, char operator, double op2) {
        String response = null;

        // Test if there is an active connection with the server
        if (!isConnected) {
            throw new RuntimeException("Cannot execute calculation. No active connection.");
        }

        // Test if the server support the desired operation
        if (supportedOperations.indexOf(operator) < 0) {
            throw new RuntimeException("Operation unsupported by the server.");
        }

        // Send operation
        out.println(String.format("%s %s %s", op2, operator, op2));

        // Read response
        try {
            response = in.readLine();
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }

        // Return response
        if (response != null && response.contains("RES")) {
            return Double.parseDouble(response.split("\\s")[1]);
        } else {
            throw new RuntimeException(response);
        }
    }

    public void connect() {
        String response;
        LOG.log(Level.INFO, "Initialize new connection...");
        if (isConnected) {
            LOG.log(Level.WARNING, "Already connected.");
        } else {
            try {
                // Initialize connection
                clientSocket = new Socket(SERVER_IP, SERVER_PORT);
                out = new PrintWriter(clientSocket.getOutputStream());
                in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                // Handshake
                out.println(HANDSHAKE);
                response = in.readLine();
                if (response.contains(HANDSHAKE_RESPONSE)) {
                    isConnected = true;

                    // Set supported operations
                    supportedOperations = response.split(";")[1];

                    LOG.log(Level.INFO, "Connected with success.");
                } else {
                    LOG.log(Level.WARNING, "Handshake failed. Abort connection.");
                    cleanup();
                }

            } catch (IOException ex) {
                LOG.log(Level.SEVERE, ex.getMessage(), ex);
                cleanup();
            }
        }

    }
    public void disconnect() {
        LOG.log(Level.INFO, "Disconnection request.");
        if (isConnected) {
            cleanup();
            isConnected = false;
            LOG.log(Level.INFO, "Disconnected with success.");
        } else {
            LOG.log(Level.INFO, "Unable to disconnect. No active connection.");
        }
    }
    public void cleanup() {
        try {
            if (in != null) {
                in.close();
            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }

        if (out != null) {
            out.close();
        }

        try {
            if (clientSocket != null) {
                clientSocket.close();
            }
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.connect();

        try {
            System.out.print("4 * 4 = ");
            System.out.println(client.calculate(4, '*', 4));
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);
            System.out.println(ex.getMessage());
        }

        client.disconnect();

    }

}