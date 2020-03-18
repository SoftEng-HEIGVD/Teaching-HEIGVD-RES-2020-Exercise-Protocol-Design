package client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client {
    private enum Op { ADD, SUB, MUL, DIV };
    private static final String ERROR_FORMAT = "Error in format. Must be 'a <op> b'\n" +
            "a and b must be integers\n" +
            "<op> must be +, -, * or /\n";

    private static final Logger LOG = Logger.getLogger(Client.class.getName());

    private final int PORT = 8080;
    private final BufferedWriter writer;
    private final BufferedReader reader;

    public Client(InetAddress host) throws IOException {
        Socket server = new Socket(host, PORT);
        OutputStream os = server.getOutputStream();
        InputStream is = server.getInputStream();
        writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
        reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
    }

    private void sendReq(String req) throws IOException {
        writer.write(req + "\n");
        writer.flush();
    }

    private void checkAnswer(String ans) throws IOException {
        LOG.log(Level.INFO, ans);
        if (! reader.readLine().equals(ans)) {
            sendReq("ERR");
            reset();
        }
    }

    private void reset() throws IOException {
        System.out.println("An error occured.");
        start();
    }

    private int askServer(int f, Op op, int s) throws IOException {
        sendReq("START");
        checkAnswer("GO AHEAD");

        sendReq("N " + f);
        checkAnswer("OK N");

        sendReq("O " + op);
        checkAnswer("OK O " + op);

        sendReq("N " + s);
        checkAnswer("OK N");

        sendReq("PERFORM");
        String line = reader.readLine();

        if (line.substring(0, 3).equals("ERR"))
            reset();

        int res = Integer.parseInt(line.substring(4));
        return res;
    }

    public void start() throws IOException {
        while (true) {
            int f = 0;
            int s = 0;
            Op op = Op.ADD;

            System.out.println("Enter a simple calculation to be made:");
            Scanner in = new Scanner(System.in);
            try {
                f = in.nextInt();
                switch (in.next("[+*/\\-]")) {
                    case "+":
                        op = Op.ADD;
                        break;
                    case "-":
                        op = Op.SUB;
                        break;
                    case "*":
                        op = Op.MUL;
                        break;
                    case "/":
                        op = Op.DIV;
                        break;
                    default:
                        throw new InputMismatchException("Wrong operator");
                }

                s = in.nextInt();
            } catch (Throwable e) {
                System.out.println(ERROR_FORMAT);
                start();
            }

            int res = askServer(f, op, s);
            System.out.println("Result is: " + res);
        }
    }
}
