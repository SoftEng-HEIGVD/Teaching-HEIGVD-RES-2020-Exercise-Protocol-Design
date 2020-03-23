package server;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Stack;

public class Server
{
    private ServerSocket receptionistSocket;
    private Socket commSocket;
    private BufferedReader fromClient;
    private BufferedWriter toClient;
    private boolean connectionOpened = false;
    private Stack<Integer> numberStack;
    int result;

    public Server() throws IOException
    {
        receptionistSocket = new ServerSocket(50000);
        numberStack = new Stack<>();
        System.out.println("Server created\n");
        while (true)
        {
            commSocket = receptionistSocket.accept();
            runActivity();
        }
    }


    private void runActivity() throws IOException
    {
        fromClient = new BufferedReader(new InputStreamReader(commSocket.getInputStream()));
        toClient = new BufferedWriter(new PrintWriter(commSocket.getOutputStream()));
        result = 0;
        numberStack.clear();

        if (! connectionOpened && fromClient.readLine().equals("OPEN"))
        {
            sendToClient("ACK");
            connectionOpened = true;
            System.out.println("Connection established");
        }

        String line;
        while (connectionOpened && (line=fromClient.readLine())!=null)
        {
            if (line.equals("RECEIVED"))
            {
                sendToClient("CLOSE");
                connectionOpened = false;
                fromClient.close();
                toClient.close();
            }
            else
            {
                try
                {
                    result = calculateRequest(line);
                    sendToClient(String.valueOf(result));
                }
                catch (Exception e)
                {
                    throw new IOException("Invalid format");
                }
            }
        }
    }

    private Integer calculateRequest(String request)
    {
        int firstOperand = 0;
        int secondOperand = 0;
        for (int x = 0; x < request.length(); ++ x)
        {
            char c = request.charAt(x);
            if (Character.isDigit(c))
            {
                numberStack.push(Character.getNumericValue(c));
            }
            else
            {
                switch (c)
                {
                    case ('+'):
                        firstOperand = numberStack.pop();
                        secondOperand = numberStack.pop();
                        numberStack.push(firstOperand + secondOperand);
                        break;
                    case ('-'):
                        firstOperand = numberStack.pop();
                        secondOperand = numberStack.pop();
                        numberStack.push(firstOperand - secondOperand);
                        break;
                    case ('*'):
                        firstOperand = numberStack.pop();
                        secondOperand = numberStack.pop();
                        numberStack.push(firstOperand * secondOperand);
                        break;
                    case ('/'):
                        firstOperand = numberStack.pop();
                        secondOperand = numberStack.pop();
                        numberStack.push(firstOperand / secondOperand);
                        break;
                    default:
                        throw new RuntimeException("Invalid format");
                }//end switch
            }//end if-else
        }//end for
        System.out.println("Calculation done, result is " + numberStack.firstElement() + "\n");
        return numberStack.firstElement();
    }

    private void sendToClient(String instruct) throws IOException
    {
        toClient.write(instruct + "\n");
        toClient.flush();
    }
}
