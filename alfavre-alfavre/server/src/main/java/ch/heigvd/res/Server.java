package main.java.ch.heigvd.res;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * largely based on presence example form MrLiechti
 * https://github.com/SoftEng-HEIGVD/Teaching-HEIGVD-RES-2020/blob/master/examples/06-PresenceApplication/PresenceApplication/src/main/java/ch/heigvd/res/examples/PresenceServer.java
 */
public class Server {

    final static Logger LOG = Logger.getLogger(Server.class.getName());
    private static Charset CHARSET = StandardCharsets.UTF_8;
    private ServerSocket serverSocket;
    private boolean shouldRun;

    private Socket clientSocket;
    private BufferedReader reader;
    private BufferedWriter writer;


    public void start() {
        try {
            shouldRun = true;
            LOG.log(Level.INFO, "Starting Calc Server on port {0}", Protocol.CALC_DEFAULT_PORT);
            serverSocket = new ServerSocket(Protocol.CALC_DEFAULT_PORT);

            while (shouldRun) {
                clientSocket = serverSocket.accept();

                reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), CHARSET));
                writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), CHARSET));

                String operation = reader.readLine();

                writer.write(Protocol.OK + Protocol.EOL);
                writer.flush();

                double leftHand = Double.parseDouble(reader.readLine());

                writer.write(Protocol.OK + Protocol.EOL);
                writer.flush();

                double rightHand = Double.parseDouble(reader.readLine());

                writer.write(Protocol.OK + Protocol.EOL);
                writer.flush();

                double answer;
                switch (operation) {
                    case Protocol.ADD:
                        answer = leftHand + rightHand;
                        writer.write(Double.toString(answer) + Protocol.EOL);
                        writer.flush();
                        break;
                    case Protocol.SUB:
                        answer = leftHand - rightHand;
                        writer.write(Double.toString(answer) + Protocol.EOL);
                        writer.flush();
                        break;
                    case Protocol.MUL:
                        answer = leftHand * rightHand;
                        writer.write(Double.toString(answer) + Protocol.EOL);
                        writer.flush();
                        break;
                    case Protocol.DIV:
                        answer = leftHand / rightHand;
                        writer.write(Double.toString(answer) + Protocol.EOL);
                        writer.flush();
                        break;
                    default:
                        writer.write(Protocol.NOK + Protocol.EOL);
                        writer.flush();
                        break;
                }

                String finished = reader.readLine();
                if (finished.equals(Protocol.NOK)) {
                    shouldRun = false;
                    if (!finished.equals(Protocol.OK)) {
                        throw new IOException("Calculation Failure");
                    }
                }

            }
            serverSocket.close();
            LOG.info("shouldRun is false... server going down");
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, ex.getMessage(), ex);

            System.exit(-1);
        }
    }


}