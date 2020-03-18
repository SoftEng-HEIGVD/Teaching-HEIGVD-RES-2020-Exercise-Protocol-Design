package client;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Client {
    private enum Op { ADD, SUB, MUL, DIV };
    private static final String ERROR_FORMAT = "Error in format. Must be 'a <op> b'\n" +
            "a and b must be integers\n" +
            "<op> must be +, -, * or /\n";
    private static final String ERROR_COMM = "Error while talking with server.\n";

    private final InetAddress host;
    private final int PORT = 8080;

    private BufferedWriter writer;
    private BufferedReader reader;

    public Client(InetAddress host) {
        this.host = host;
    }

    private void sendReq(String req) throws IOException {
        writer.write(req + "\n");
        writer.flush();
    }

    private void checkAnswer(String ans) throws IOException {
        if (! reader.readLine().equals(ans)) {
            sendReq("ERR");
            reset();
        }
    }

    private void reset() throws IOException {
        throw new IOException(ERROR_COMM);
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

        if (line.length() < 3 || line.substring(0, 3).equals("ERR"))
            reset();

        int res = Integer.parseInt(line.substring(4));
        return res;
    }

    public void start() throws IOException {
        Socket server = new Socket(host, PORT);
        int f = 0;
        int s = 0;
        Op op = Op.ADD;

        boolean incorrectInput = true;
        while (incorrectInput) {
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
                incorrectInput = false;
            } catch (Throwable e) {
                System.out.println(ERROR_FORMAT);
            }
        }

        try {
            OutputStream os = server.getOutputStream();
            InputStream is = server.getInputStream();

            writer = new BufferedWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
            reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

            int res = askServer(f, op, s);
            System.out.println("Result is: " + res);
        } catch (Throwable e) {
            System.out.println(ERROR_COMM);
        }finally {
            server.close();
        }
    }
}
