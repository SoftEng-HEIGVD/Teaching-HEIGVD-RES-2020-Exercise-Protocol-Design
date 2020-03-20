package client;

import common.Operator;
import common.Protocol;

public class ClientApplication {
    public static void main(String[] args) {
        Client c1 = new Client();
        Client c2 = new Client();
        c1.connect("192.168.0.150");
        c2.connect("192.168.0.150");
        c1.getCalculationResult(1,2, Operator.ADD);
        c1.getCalculationResult(1,2, Operator.ADD);
        c1.getCalculationResult(1,2, Operator.ADD);
        c2.getCalculationResult(2,3, Operator.ADD);
        c1.disconnect();
        c2.disconnect();
    }
}
