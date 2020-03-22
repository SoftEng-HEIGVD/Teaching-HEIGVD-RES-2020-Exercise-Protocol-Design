package server;

public class App {
    public static void main(String[] args) {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s %n");

        Server server = new Server(5190);
        server.start();
    }
}
