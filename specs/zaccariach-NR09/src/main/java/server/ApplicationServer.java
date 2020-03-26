/**
 * Authors : Christian Zaccaria & Nenad Rajic
 * Date 26.03.2020
 * Exercice Protocol-Design
 * File : ApplicationServer.java
 */
package server;

public class ApplicationServer {

    public static void main(String[] args){
        Server server = new Server();
        server.start();
    }
}
