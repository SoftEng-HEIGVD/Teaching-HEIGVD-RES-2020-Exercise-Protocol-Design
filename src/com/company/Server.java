package com.company;

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
    private boolean connectionOpened=false;
    private Stack<Integer> numberStack;
    int result;

    public Server() throws IOException
    {
        receptionistSocket = new ServerSocket(80);
        commSocket = receptionistSocket.accept();
        numberStack=new Stack<>();
        result=0;
    }

    private void runActivity() throws IOException
    {
        fromClient = new BufferedReader(new InputStreamReader(commSocket.getInputStream()));
        toClient = new BufferedWriter(new PrintWriter(commSocket.getOutputStream()));

        if(!connectionOpened && fromClient.readLine().equals("OPEN"))
        {
            sendToClient("ACK");
            connectionOpened=true;
        }
        else if(connectionOpened)
        {
            try
            {
                result=calculateRequest(fromClient.readLine());
            }
            catch (Exception e)
            {
                throw new IOException("Invalid format");
            }
        }
        else
        {
            throw new IOException("Invalid format");
        }
    }

    private Integer calculateRequest(String request)
    {
        int firstOperand=0;
        int secondOperand=0;
        for(int x=0;x<request.length();++x)
        {
            char c=request.charAt(x);
            if(Character.isDigit(c))
            {
                numberStack.push(Character.getNumericValue(c));
            }
            else if(c=='+')
            {
                firstOperand=numberStack.pop();
                secondOperand=numberStack.pop();
                numberStack.push(firstOperand+secondOperand);
            }
            else if(c=='-')
            {
                firstOperand=numberStack.pop();
                secondOperand=numberStack.pop();
                numberStack.push(firstOperand-secondOperand);
            }
            else if(c=='*')
            {
                firstOperand=numberStack.pop();
                secondOperand=numberStack.pop();
                numberStack.push(firstOperand*secondOperand);
            }
            else if(c=='/')
            {
                firstOperand=numberStack.pop();
                secondOperand=numberStack.pop();
                numberStack.push(firstOperand/secondOperand);
            }
            else
            {
                throw new RuntimeException("Invalid format");
            }
        }
        return numberStack.firstElement();
    }

    private void sendToClient(String instruct) throws IOException
    {
        toClient.write(instruct);
        toClient.flush();
    }
}
