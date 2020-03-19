package main.java.ch.heigvd.res;


import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;


public class Client {


    private Socket serverSocket;
    private BufferedReader reader;
    private BufferedWriter writer;
    private Scanner scanner;
    boolean isReading;
    private final InetAddress host;
    private double leftHand;
    private double rightHand;
    private String op;
    private OutputStream out;
    private InputStream in;


    public Client(InetAddress host) {
        this.host = host;
    }

    public void start() throws IOException {
        serverSocket = new Socket(host, Protocol.CALC_DEFAULT_PORT);

        isReading = true;
        while (isReading) {
            System.out.println("Instruction (EX: ADD 1 1): ");
            scanner = new Scanner(System.in);

            try {
                op = scanner.next();
                leftHand = scanner.nextDouble();
                rightHand = scanner.nextDouble();
            } catch (Throwable e) {
                System.out.println("scanner failure");
            }

            isReading = false;
        }

        try {
            out = serverSocket.getOutputStream();
            in = serverSocket.getInputStream();

            writer = new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
            reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));

            writer.write(op + Protocol.EOL);
            writer.flush();

            if (!reader.readLine().equals(Protocol.OK)) {
                throw new IOException("failed op");
            }

            writer.write(Double.toString(leftHand) + Protocol.EOL);
            writer.flush();

            if (!reader.readLine().equals(Protocol.OK)) {
                throw new IOException("failed lefthand");
            }

            writer.write(Double.toString(rightHand) + Protocol.EOL);
            writer.flush();

            if (!reader.readLine().equals(Protocol.OK)) {
                throw new IOException("failed righthand");
            }

            String result = reader.readLine();
            if (result.equals(Protocol.NOK)) {
                throw new IOException("failed calculation");
            }

            System.out.println("the result is: " + result);

            writer.write(Protocol.OK + Protocol.EOL);
            writer.flush();


        } catch (Throwable e) {
            System.out.println(e);
        } finally {
            serverSocket.close();
        }
    }


}