import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client
{
    static Socket socket;
    static BufferedReader in = null;
    static PrintWriter out = null;
    static Scanner scan = null;

    private static void init() throws IOException
    {
        System.out.println("Welcome to the calculator !\n");

        System.out.println("Trying to establish a socket !");
        socket = new Socket("127.0.0.1", 2112);

        System.out.println("Socket established, setting up the IO stuff");
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream());

        System.out.println("IO stuff seems to work, sending greetings to the server.\n");
        out.println("HELLO"); out.flush();
    }

    private static void worker() throws Exception
    {
        String line;
        scan = new Scanner(System.in);
        boolean end = false;

        while (!end)
        {
            while ((line = in.readLine()) != null)
            {
                if (line.equals("> ERROR"))
                {
                    throw new Exception("The server responded with an error");
                }

                System.out.println(line);

                System.out.println("Please enter a command");
                String command = scan.nextLine();

                if (command.startsWith("END"))
                {
                    end = true;
                    System.out.println("Very well, exiting now. Goodbye");
                }

                out.println(command); out.flush();
            }
        }
    }

    public static void main(String[] args)
    {
        try
        {
            init();
            worker();
        }
        catch (Exception e)
        {
            System.out.println("Oh no, something went wrong !");
            e.printStackTrace();
        }
    }
}
