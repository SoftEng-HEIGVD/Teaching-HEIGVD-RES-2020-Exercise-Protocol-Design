package specs.Grimlix.src.src;

/*
Je n'ai malheureusement que pu tester en local avec mes deux programmes ouverts. Un des deux lance le server (Server part)
et l'autre lance le client (Client part). Tout le code contient des LOG.info afin de ppouvoir voir le parcours. Si la
connection se fait bien et qu'elle se termine bien.
 */

public class App {
    public static void main(String[] args) {
        /*
        Server part
         */
        Server serv = new Server();
        serv.run();

        /*
        Client part
         */
        //Client cli = new Client();
        //cli.connect(Protocol.DEFAULT_ADDRESS, Protocol.DEFAULT_PORT);
    }
}
