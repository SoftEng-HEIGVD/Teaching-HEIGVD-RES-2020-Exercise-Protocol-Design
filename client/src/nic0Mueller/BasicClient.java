import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BasicClient {

    private static final Logger LOG = Logger.getLogger(BasicClient.class.getName());

    private final static int BUFFER_SIZE = 1024;

    private final int serverPort = 6942;
    private final String serverIp = "10.192.104.13";

    private Socket clientSocket;
    private InputStream is;
    private OutputStream os;

    private boolean connected = false;


    public Double calc(double n1, double n2, char operator) {

        if (!connected) {

            LOG.log(Level.INFO, "Can't calculate. Not connected to server.");
            return null;
        }

        String request = generateCorrectRequest(n1, n2, operator);
        LOG.info("Created request: " + request);

        try {
            // Envoi du calcul
            os.write(request.getBytes());

            // Lecture de la répponse
            ByteArrayOutputStream responseBuffer = new ByteArrayOutputStream();
            byte[] buffer = new byte[BUFFER_SIZE];
            int newBytes;

            while ((newBytes = is.read(buffer)) != -1) {
                responseBuffer.write(buffer, 0, newBytes);
            }

            return Double.parseDouble(responseBuffer.toString());

        }
        catch (IOException e) {
            LOG.log(Level.SEVERE, "Can't send message: " + e);
            return null;
        }

    }

    public void connect() {

        if (connected) {

            LOG.log(Level.INFO, "Already connected. Cancelling new connection...");
            return;
        }

        try {

            clientSocket = new Socket(serverIp, serverPort);
            is = clientSocket.getInputStream();
            os = clientSocket.getOutputStream();
            connected = true;


        }
        catch (IOException e) {
            LOG.log(Level.SEVERE, "Couldn't connect to server:" + e);
            clean();
            return;
        }

        LOG.log(Level.INFO, "Connected to server.");
    }

    public void disconnect() {

        if (!connected) {

            LOG.log(Level.INFO, "Not connected te server. Cancelling disconnection...");
            return;
        }

        connected = false;
        clean();

        LOG.log(Level.INFO, "Disconnected from server.");
    }



    private String generateCorrectRequest(double n1, double n2, char operator) {

        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(operator);
        stringBuilder.append(';');
        stringBuilder.append(n1);
        stringBuilder.append(';');
        stringBuilder.append(n2);

        return stringBuilder.toString();
    }

    private void clean() {

        try {
            if (is != null) {
                is.close();
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE,"Couldn't close input stream: " + e);
        }

        try {
            if (os != null) {
                os.close();
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE,"Couldn't close output stream: " + e);
        }

        try {
            if (clientSocket != null) {
                clientSocket.close();
            }
        } catch (IOException e) {
            LOG.log(Level.SEVERE,"Couldn't close socket: " + e);
        }
    }

    public static void main(String[] args) {

        BasicClient client = new BasicClient();

        client.connect();
        Double response = client.calc(3, 4, '*');

        if (response != null) {
            LOG.log(Level.INFO, "Result = " + response);
        }
        else {
            LOG.log(Level.INFO, "Couldn't calculate");
        }

        client.disconnect();

    }
}