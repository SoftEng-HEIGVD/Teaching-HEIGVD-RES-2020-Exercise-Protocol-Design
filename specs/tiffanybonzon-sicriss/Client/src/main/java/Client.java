import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client
{
    Socket socket;
    BufferedReader in = null;
    PrintWriter out = null;
    Scanner scan = null;

    public void main(String[] args)
    {
        try
        {
            System.out.println("Welcome to the calculator !");

            System.out.println("\nTrying to establish a socket !");
            this.socket = new Socket("127.0.0.1", 2112);

            System.out.println("Socket established, setting up the IO stuff");
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream());

            System.out.println("IO stuff seems to work, sending greetings to the server.");
            out.println("Hello"); out.flush();

            String line;
            scan = new Scanner(System.in);
            boolean end = false;

            while (!end)
            {
                while ((line = in.readLine()) != null)
                {
                    if (line.equals("Error"))
                    {
                        throw new Exception("The server responded with an error");
                    }

                    if (line.startsWith("OPs : "))
                    {
                        System.out.println("Connexion established with the server.");
                        System.out.println("The available commands are : " + line.substring(6) + " & END");
                    }
                    else
                    {
                        System.out.println("The result is " + line);
                    }

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
        catch (Exception e)
        {
            System.out.println("Oh no, something went wrong !");
            e.printStackTrace();
        }
    }
}
