package application;

import common.Operator;
import common.Protocol;
import server.Server;
import client.Client;

public class Application {
    public static void main(String[] args) {
        Server srv = new Server();
        Thread listenThread = new Thread(srv);
        listenThread.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Client c1 = new Client();
        Client c2 = new Client();
        c1.connect("localhost");
        c2.connect("localhost");
        c1.getCalculationResult(1,2, Operator.ADD);
        c1.getCalculationResult(1,2, Operator.ADD);
        c1.getCalculationResult(1,2, Operator.ADD);
        c2.getCalculationResult(2,3, Operator.ADD);
        srv.shutdown();
    }
}
