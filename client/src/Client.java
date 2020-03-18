import com.sun.xml.internal.ws.util.StringUtils;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

public class Client {

    private final static String HOSTNAME = "10.192.91.10";
    private final static String LOCALHOST = "127.0.0.1";
    private final static int PORT = 220;


    public static void main(String[] args) {
        // Create a client and try to connect
        try (Socket clientSocket = new Socket(LOCALHOST, PORT)) {

            InputStream is = clientSocket.getInputStream();
            OutputStream os = clientSocket.getOutputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

            String userInput;

            userInput = br.readLine();

            while (!userInput.equals("END")) {
                // Read the user input

                String[] inputArray = userInput.split(" ");

                // Check if the input is valid
                if (inputArray.length != 3 || !isNumeric(inputArray[0]) || !isNumeric(inputArray[2]) || inputArray[1]
                        .length() != 1 || !isValidOperator(inputArray[1].charAt(0))) {
                    System.out.println("Input invalid : val1 op val2");
                } else {
                    // Write through the socker
                    PrintWriter writer = new PrintWriter(os);

                    writer.write("COMPUTE " + inputArray[1] + " " + inputArray[0] + " " + inputArray[2]);
                    writer.flush();

                    // Wait the server response
                    System.out.println("Waiting server response...");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));

                    // Show the server response
                    String line = reader.readLine();
                    System.out.println(line);

                }
                userInput = br.readLine();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static boolean isNumeric(String str) {

        if (str == null || str.length() == 0) {
            return false;
        }

        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }

        return true;

    }

    public static boolean isValidOperator(char c) {
        final ArrayList<Character> validOperator = new ArrayList<>(Arrays.asList('+', '*', '-', '/'));

        return validOperator.contains(c);
    }
}
