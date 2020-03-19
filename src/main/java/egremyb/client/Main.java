package egremyb.client;

import java.util.Scanner;

public class Main {
    private final static String NO_CONNECTION = "Unable to connect to server.";
    private final static String EXIT          = "exit";
    private final static String QUIT          = "quit";
    private final static String FORMAT        = "Enter a format such as : <number> <operator> <number>\n"      +
                                                " - <number>   : Any decimal number like 3, 5.2, -8, -1.2 .\n" +
                                                " - <operator> : Only [+, -, *, /, ^].\n"                      +
                                                " - Each element his separated by a blank space.\n"          +
                                                "Type " + QUIT + " or " + EXIT + " to end the app.\n";
    private final static String FORMAT_ERR    = "Please respect the format.";
    private final static String NUMBER_ERR    = "Insert a decimal number like 3, 5.2, -8, -1.2.";
    private final static String TERMINATE     = "Terminating the app...";

    public static void main(String[] args) throws InterruptedException {
        Client   client = new Client();
        Scanner  in     = new Scanner(System.in);
        String   input;
        String[] token;
        double   op1, op2;
        Double   result;

        // try to connect to the server
        if (client.openConnection() == -1) {
            System.out.println(NO_CONNECTION + ' ' + TERMINATE);
            return;
        }

        System.out.println(FORMAT);

        while (true) {
            // get user input and split it in token(s)
            System.out.print("Enter an equation : ");
            input = in.nextLine();
            token = input.split(" ");
            // if three tokens -> input of an equation
            if (token.length == 3) {
                try {
                    op1 = Double.parseDouble(token[0]);
                    op2 = Double.parseDouble(token[2]);
                } catch (NumberFormatException ex) {
                    System.out.println(FORMAT_ERR + " " + NUMBER_ERR);
                    continue;
                }
                // send equation
                result = client.sendCalculationToCompute(op1, token[1], op2);
                // print result
                System.out.println(token[0] + " " + token[1] + " " + token[2] + " = " + result);
            }
            // if one token -> leaving the app
            else if (token.length == 1 &&
                      (token[0].equals(EXIT) || token[0].equals(QUIT))) {
                break;
            }
            // otherwise it's an error
            else {
                System.out.println(FORMAT_ERR);
            }
        }
        // close the connection
        client.closeConnection();
        System.out.println(TERMINATE);

    }
}
