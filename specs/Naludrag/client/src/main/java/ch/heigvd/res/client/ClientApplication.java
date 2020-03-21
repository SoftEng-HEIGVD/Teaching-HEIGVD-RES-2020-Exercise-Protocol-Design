package ch.heigvd.res.client;

import ch.heigvd.res.common.Operator;
import ch.heigvd.res.common.Protocol;

public class ClientApplication {
    public static void main(String[] args) {
        Client c1 = new Client();
        Client c2 = new Client();
        c1.connect("localhost");
        c2.connect("localhost");
        c1.getCalculationResult(1,2, Operator.ADD);
        c1.getCalculationResult(1,2, Operator.MULTIPLY);
        c1.getCalculationResult(1,2, Operator.SUB);
        c2.getCalculationResult(2,3, Operator.ADD);
        c2.getCalculationResult(2,3, Operator.DIVIDE);
        c1.disconnect();
        c2.disconnect();
    }
}
