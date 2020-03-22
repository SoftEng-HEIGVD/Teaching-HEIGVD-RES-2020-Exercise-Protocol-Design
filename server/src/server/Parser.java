package server;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    private static Pattern spacePattern = Pattern.compile(" ");
    private static Pattern numberPattern = Pattern.compile("([+\\-])?[0-9]");
    private static Pattern operatorPattern = Pattern.compile("[+\\-*/%]");

    /**
     * Split the command string using the space character
     *
     * @param command the command string sent by the client
     * @return an array of string split by spaces
     */
    public static String[] splitCommand (String command) {
        return spacePattern.split(command, 4);
    }

    /**
     * Get the operands sent by the client
     *
     * @param number the number to parse
     * @return the parsed number
     * @throws InterruptedException if it is not a number, throws an exception
     */
    public static double getNumber (String number) throws InterruptedException {
        Matcher m = numberPattern.matcher(number);
        if (!m.matches())
            throw new InterruptedException("J'te demande un chiffre ici.");
        return Double.parseDouble(number);
    }

    /**
     * Get the operaton sent by the client
     *
     * @param op the opeator to parse
     * @return the parsed operator
     * @throws InterruptedException if it is not supported, throws an exception
     */
    public static char getOperator (String op) throws InterruptedException {
        Matcher m = operatorPattern.matcher(op);
        if (!m.matches())
            throw new InterruptedException("J'te demande soit : +, -, * ou /. Rien d'autre.");
        return op.charAt(0);
    }
}
