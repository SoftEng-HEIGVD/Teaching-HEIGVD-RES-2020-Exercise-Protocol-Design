import java.io.*;
import java.net.Socket;
import java.util.UnknownFormatConversionException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is not really an HTTP client, but rather a very simple program that
 * establishes a TCP connection with a real HTTP server. Once connected, the
 * client sends "garbage" to the server (the client does not send a proper
 * HTTP request that the server would understand). The client then reads the
 * response sent back by the server and logs it onto the console.
 *
 * @author Olivier Liechti
 */
public class client {

    static final Logger LOG = Logger.getLogger(client.class.getName());

    final static int BUFFER_SIZE = 1024;
    final static char NEW_LINE = '\n';

    /**
     * This method does the whole processing
     */
    public void sendWrongHttpRequest() {
        Socket clientSocket = null;
        OutputStream os = null;
        InputStream is = null;

        try {
            clientSocket = new Socket("localhost", 3030);
            os = clientSocket.getOutputStream();
            is = clientSocket.getInputStream();

            //send the first string, client HELLO
            String clientIn = protocol.CMD_CLIENTIN + NEW_LINE;
            os.write(clientIn.getBytes());

            //buffers
            ByteArrayOutputStream responseBuffer = new ByteArrayOutputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            byte[] buffer = new byte[BUFFER_SIZE];
            int newBytes;

            //get the first string. Close connection if it's not protocol like
            if((newBytes = is.read(buffer)) == -1)
                throw new UnknownFormatConversionException("Server not reachable");

            responseBuffer.write(buffer, 0, newBytes);
            if(responseBuffer.toString() != protocol.CMD_SERVERIN)
                throw new UnknownFormatConversionException("Server not reachable");

            //do the calculations
            while (true) {
                //ask calculation in
                LOG.log(Level.INFO, "Enter a calculation <number> <op> <number>: ");
                String op = reader.readLine();

                //check if quit
                if(op == protocol.CMD_CLIENTOUT) {
                    os.write(op.getBytes());
                    break;
                }

                op += " " + protocol.CMD_CLIENTCALC;
                os.write(op.getBytes());

                //get server answer
                newBytes = is.read(buffer);
                responseBuffer.reset();
                responseBuffer.write(buffer, 0, newBytes);
                LOG.log(Level.INFO, "Response sent by the server: ");
                LOG.log(Level.INFO, responseBuffer.toString());
            }

        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        } finally {
            try {
                is.close();
            } catch (IOException ex) {
                Logger.getLogger(client.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                os.close();
            } catch (IOException ex) {
                Logger.getLogger(client.class.getName()).log(Level.SEVERE, null, ex);
            }
            try {
                clientSocket.close();
            } catch (IOException ex) {
                Logger.getLogger(client.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s %n");

        client client = new client();
        client.sendWrongHttpRequest();

    }

}
