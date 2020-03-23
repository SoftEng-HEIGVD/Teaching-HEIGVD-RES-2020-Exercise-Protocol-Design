package server.ClarFleu;

public class Launcher {
    public static void main(String[] args) {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s %n");

        CalculatorServer multi = new CalculatorServer(2205);
        multi.serveClients();
    }
}
