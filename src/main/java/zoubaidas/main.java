package zoubaidas;

public class main {
    public static void main(String[] args) {


        if (args[0].equals("startServer")) {
            int port = Integer.parseInt(args[1]);
            ServerSide calc = new ServerSide();
            calc.start(port);
        } else if (args[0].equals("startClient")) {
            String hostname = args[1];
            int port = Integer.parseInt(args[2]);
            ClientSide client = new ClientSide();
            client.start(hostname, port);
        }
    }
}
