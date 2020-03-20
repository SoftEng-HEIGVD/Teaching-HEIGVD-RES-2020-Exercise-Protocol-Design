package server;

public class ServerApplication {
    public static void main(String[] args) {
        Server srv = new Server();
        srv.run();
        //srv.shutdown();
    }
}
