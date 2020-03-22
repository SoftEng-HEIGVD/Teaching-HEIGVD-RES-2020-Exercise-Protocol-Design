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
    private final InetAddress host;
    private double leftHand;
    private double rightHand;
    private String op;
    private OutputStream out;
    private InputStream in;


    /**
     * constructor
     * @param host
     */
    public Client(InetAddress host) {
        this.host = host;
    }

    private static boolean opControl(String op){
        for(String s:Protocol.OP){
            if(op.equals(s)){
                return true;
            }
        }
        return false;
    }


    private void fillValue(){
        boolean isReading;

        do {
            System.out.println("Instruction (EX: ADD 1 1): ");
            System.out.println("ADD SUB MUL DIV only");
            System.out.println("All extraneous token will be ignored");
            System.out.println("For some reason, you might have to use 1,2 instead of 1.2");
            Scanner scanner = new Scanner(System.in);

            try {
                op = scanner.next();

                if(!opControl(op)){
                    //je devrait laisser ca au serveur
                    throw new RuntimeException("Incorrect operand");
                }

                try {
                    leftHand = scanner.nextDouble();
                } catch (Throwable e){
                    throw new RuntimeException("Incorrect lefthand type: "+e);
                }

                try {
                    rightHand = scanner.nextDouble();
                } catch (Throwable e){
                    throw new RuntimeException("Incorrect righthand type: "+e);
                }

                /*if(!scanner.hasNext()){
                    throw new RuntimeException("Too many arguments");
                }
                // THIS DOESN'T WORK
                */

                isReading=false;

            } catch (Throwable e) {
                isReading=true;
                System.out.println("scanner failure type: " + e);
            }

        }while (isReading);


    }

    private void communication() throws IOException {
        writer.write(Protocol.START + Protocol.EOL);
        writer.flush();

        if (!reader.readLine().equals(Protocol.OK)) {
            throw new IOException("Failed meeting");
        }

        writer.write(op + Protocol.EOL);
        writer.flush();

        if (!reader.readLine().equals(Protocol.OK)) {
            throw new IOException("failed op");
        }

        writer.write(leftHand + Protocol.EOL);
        writer.flush();

        if (!reader.readLine().equals(Protocol.OK)) {
            throw new IOException("failed lefthand");
        }

        writer.write(rightHand + Protocol.EOL);
        writer.flush();

        if (!reader.readLine().equals(Protocol.OK)) {
            throw new IOException("failed righthand");
        }

        String result = reader.readLine();
        if (result.equals(Protocol.NOK)) {
            throw new IOException("failed calculation");
        }

        System.out.println("the result is: " + result);
    }

    private boolean nextCalc() throws IOException {
        String again;
        System.out.println("Do you want to do another calculation?\n0 yes\n1 no");
        Scanner s = new Scanner(System.in);

        if (s.nextInt() == 0) {
            again = Protocol.NOK;

        } else {
            System.out.println("Actually, we don't care if you entered 1, if it's not 0 then it's no");
            again = Protocol.OK;

        }
        writer.write(again + Protocol.EOL);
        writer.flush();
        return (again.equals(Protocol.NOK));
    }
    /**
     * run?
     * @throws IOException
     */
    public void start() throws IOException {


        try {
            serverSocket = new Socket(host, Protocol.CALC_DEFAULT_PORT);

            out = serverSocket.getOutputStream();
            in = serverSocket.getInputStream();

            writer = new BufferedWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));
            reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));

            boolean isRunning=true;

            while(isRunning) {
                fillValue();

                communication();

                isRunning=nextCalc();

            }
        } catch (Throwable e) {
            System.out.println(e);
        } finally {
            serverSocket.close();
        }
    }


}