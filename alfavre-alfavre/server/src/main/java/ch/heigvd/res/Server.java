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


    private void sendCustomDouble(double toSend) throws IOException {
        if (toSend == Double.NaN) {
            sendNOK("calculation, rightHand and/or " +
                    "leftHand has not been initialised" +
                    "\n(or the calculation was mathematically impossible)");
        }
        writer.write(Double.toString(toSend) + Protocol.EOL);
        writer.flush();
    }

    private void sendNOK(String errorMsg) throws IOException {
        writer.write(Protocol.NOK + Protocol.EOL);
        writer.flush();
        throw new IOException("failed " + errorMsg);
    }

    private void sendOK() throws IOException {
        writer.write(Protocol.OK + Protocol.EOL);
        writer.flush();
    }

    private static boolean opControl(String op) {
        for (String s : Protocol.OP) {
            if (op.equals(s)) {
                return true;
            }
        }
        return false;
    }

    public void start() {
        try {
            shouldRun = true;
            LOG.log(Level.INFO, "Starting Calc Server on port {0}", Protocol.CALC_DEFAULT_PORT);
            serverSocket = new ServerSocket(Protocol.CALC_DEFAULT_PORT);

            clientSocket = serverSocket.accept();

            reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(), CHARSET));
            writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream(), CHARSET));

            while (shouldRun) {
                //Get START instruction
                String meeting = reader.readLine();
                if (!meeting.equals(Protocol.START)) {
                    sendNOK("meeting");
                }
                sendOK();

                //Get operator instruction
                String operation = reader.readLine();
                if (!opControl(operation)) {
                    sendNOK("operation");
                }
                sendOK();


                //Get leftHand number
                //why NaN? https://www.youtube.com/watch?v=5TFDG-y-EHs
                double leftHand = Double.NaN;
                try {
                    leftHand = Double.parseDouble(reader.readLine());
                } catch (Throwable e) {
                    sendNOK("lefthand: " + e);
                }
                sendOK();


                //Get rightHand number
                double rightHand = Double.NaN;
                try {
                    rightHand = Double.parseDouble(reader.readLine());
                } catch (Throwable e) {
                    sendNOK("righthand: " + e);
                }
                sendOK();

                //on calcul la réponse et on l'envoie
                double answer;
                switch (operation) {
                    case Protocol.ADD:
                        answer = leftHand + rightHand;
                        sendCustomDouble(answer);
                        break;
                    case Protocol.SUB:
                        answer = leftHand - rightHand;
                        sendCustomDouble(answer);
                        break;
                    case Protocol.MUL:
                        answer = leftHand * rightHand;
                        sendCustomDouble(answer);
                        break;
                    case Protocol.DIV:
                        answer = leftHand / rightHand;
                        sendCustomDouble(answer);
                        break;
                    default:
                        sendNOK("impossible operation (should be impossible to trigger)");
                        break;
                }

                //on verifie si on fini ou si on fait un nouveau calcul
                String finished = reader.readLine();
                if (finished.equals(Protocol.NOK)) {
                    shouldRun = true;
                } else if (finished.equals(Protocol.OK)) {
                    shouldRun = false;
                } else {
                    throw new IOException("veryfing if over or new calc failed");
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