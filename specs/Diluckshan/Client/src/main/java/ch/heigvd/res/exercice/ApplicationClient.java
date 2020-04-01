package ch.heigvd.res.exercice;

import java.io.PrintWriter;
import java.util.Scanner;

public class ApplicationClient {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Client calc = new Client();
        calc.connect(args[0], Protocol.PORT_CALCULATOR);
    }
}
