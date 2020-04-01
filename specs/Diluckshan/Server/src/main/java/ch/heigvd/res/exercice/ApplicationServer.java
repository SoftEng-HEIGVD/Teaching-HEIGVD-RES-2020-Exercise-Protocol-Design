package ch.heigvd.res.exercice;

public class ApplicationServer {

    public static void main(String[] args) {
    Server calculator = new Server(Protocol.PORT_CALCULATOR);
    calculator.connectClient();
    }
}
