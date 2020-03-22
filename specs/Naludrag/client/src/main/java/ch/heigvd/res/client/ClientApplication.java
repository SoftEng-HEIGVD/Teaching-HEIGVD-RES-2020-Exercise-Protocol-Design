package ch.heigvd.res.client;

import ch.heigvd.res.common.Operator;

public class ClientApplication {
    public static void main(String[] args) {
        Client c1 = new Client();
        Client c2 = new Client();
        c1.connect("localhost");
        c2.connect("localhost");

        System.out.println("Sending 1 + 2 to the server");
        System.out.println(c1.getCalculationResult(1,2, Operator.ADD));

        System.out.println("Sending 1 * 2 to the server");
        System.out.println(c1.getCalculationResult(1,2, Operator.MULTIPLY));

        System.out.println("Sending 1 - 2 to the server");
        System.out.println(c1.getCalculationResult(1,2, Operator.SUB));

        System.out.println("Sending 2 + 3 to the server");
        System.out.println(c1.getCalculationResult(2,3, Operator.ADD));

        System.out.println("Sending 2 / 3 to the server");
        System.out.println(c1.getCalculationResult(2,3, Operator.DIVIDE));

        c1.disconnect();
        c2.disconnect();
    }
}
