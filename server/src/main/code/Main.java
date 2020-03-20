package main.code;

public class Main {

    public static void main(String[] args) {
        Server clacServer = new Server();
        Thread trServer = new Thread(clacServer);
        trServer.start();

        try {
            Thread.sleep((1000 * 60 * 3));
            clacServer.quit();
            trServer.join();
        }catch (InterruptedException iExc){
            iExc.printStackTrace();
        }
    }
}


