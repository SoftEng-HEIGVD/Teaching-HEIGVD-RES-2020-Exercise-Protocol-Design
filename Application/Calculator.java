/**
 * Based on the application given as an example by Olivier Liechti in the course
 * The server reacts to the following commands, defined in the protocol:
 * -- to be defined --
 * @author Mathias Maillard
 * @author Rosy-Laure Wonjamouna
 */
public class Calculator {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s %n");
        Server s1 = new Server(1000);
        Client c1 = new Client();
        c1.connect("localhost", Protocol.PRESENCE_DEFAULT_PORT);
        c1.disconnect();

    }

}