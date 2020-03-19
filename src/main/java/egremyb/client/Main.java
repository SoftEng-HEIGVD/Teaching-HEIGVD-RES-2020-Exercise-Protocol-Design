package egremyb.client;

import egremyb.common.Protocol;

import java.util.Scanner;

import static java.lang.Thread.sleep;

public class Main {
    private final static String FORMAT = "Enter a format such as : <number> <operator> <number>\n"      +
                                         " - <number>   : Any decimal number like 3, 5.2, -8, -1.2 .\n" +
                                         " - <operator> : Only [+, -, *, /, ^].\n"                      +
                                         " - Each element his separated by a blank space.";

    public static void main(String[] args) throws InterruptedException {
        Client client = new Client();
        Scanner in    = new Scanner(System.in);
        int     op1, op2;
        String  op;
        Double result;

        client.openConnection();
        System.out.println(FORMAT);

        while (true) {
            System.out.println("Enter an equation : ");

            result = client.sendCalculationToCompute(2d, Protocol.ADD_OPERATOR, 7d);
            System.out.println("Result : " + result);
            sleep(1000);
        }
    }
}
