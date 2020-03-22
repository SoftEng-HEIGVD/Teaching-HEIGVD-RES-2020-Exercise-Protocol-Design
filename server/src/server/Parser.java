package server;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Parser {
    private static Pattern spacePattern = Pattern.compile(" ");
    private static Pattern numberPattern = Pattern.compile("([+\\-])?[0-9]");
    private static Pattern operatorPattern = Pattern.compile("[+\\-*/%]");

    public static String[] splitCommand (String command) {
        return spacePattern.split(command, 4);
    }

    public static int getNumber (String number) throws InterruptedException {
        Matcher m = numberPattern.matcher(number);
        if (!m.matches())
            throw new InterruptedException("J'te demande un chiffre ici.");
        return Integer.parseInt(number);
    }

    public static char getOperator (String op) throws InterruptedException {
        Matcher m = operatorPattern.matcher(op);
        if (!m.matches())
            throw new InterruptedException("J'te demande soit : +, -, * ou /. Rien d'autre.");
        return op.charAt(0);
    }
}
