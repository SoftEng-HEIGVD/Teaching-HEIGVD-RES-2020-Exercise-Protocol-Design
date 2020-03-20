import java.io.*;
import java.net.Socket;

public class Client {

    private final static String HOSTNAME = "10.192.91.10";
    private final static String LOCALHOST = "127.0.0.1";
    private final static int PORT = 9999;


    public static void main(String[] args) {
        // Create a client and try to connect
        try (Socket clientSocket = new Socket(LOCALHOST, PORT)) {

            InputStream is = clientSocket.getInputStream();
            OutputStream os = clientSocket.getOutputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            // Read welcome messages
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            // Show the server response
            String line = reader.readLine();
            System.out.println(line);

            line = reader.readLine();
            System.out.println(line);

            String userInput;

            userInput = br.readLine();

            while (!userInput.equals("END")) {
                // Read the user input

                String[] inputArray = userInput.split(" ");


                // Write through the socker
                PrintWriter writer = new PrintWriter(os);

                writer.println("COMPUTE " + inputArray[1] + " " + inputArray[0] + " " + inputArray[2]);
                writer.flush();

                // Wait the server response
                System.out.println("Waiting server response...");

                // Show the server response
                line = reader.readLine();
                System.out.println(line);

                userInput = br.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
