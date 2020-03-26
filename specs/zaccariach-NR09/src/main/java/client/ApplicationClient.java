/**
 * Authors : Christian Zaccaria & Nenad Rajic
 * Date 26.03.2020
 * Exercice Protocol-Design
 * File : ApplicationClient.java
 */
package client;

import protocol.Protocol;

import java.util.Scanner;

public class ApplicationClient {

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Client client = new Client();
        String request = "";
        client.connect(args[0], Protocol.DEFAULT_PORT, "User");
        boolean closeConnection = false;
        do{
            request = sc.nextLine();
            //If user wants to stop connection
            if(request.compareTo("STOP") == 0){
                closeConnection = true;
            }
            client.calculate(request);
        }while(!closeConnection);
        client.disconnect();
    }
}
