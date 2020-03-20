public class Main {
    public static void main(String[] args) {
        Server S = new Server(31415, 10);
        //Server S = new Server(31415);
        S.serveClients();
    }
}
