import java.net.*;
import java.io.*;


 */
public class Client {

    public static void main(String[] args) {
        //if (args.length < 3) return;

       // String hostname = args[0];
        int port = 6607;

        try {
            Socket socket = new Socket("localhost", port);
            InputStream input = socket.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            String result = reader.readLine();
            System.out.println(result);

        } catch (UnknownHostException ex) {

            System.out.println("Server not found: " + ex.getMessage());

        } catch (IOException ex) {

            System.out.println("I/O error: " + ex.getMessage());
        }
    }
}