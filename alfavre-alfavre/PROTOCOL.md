# Calculator Protocol
## Authors Alban Favre and Alban Favre
This is a small server-client application: the client sends an arithmetical instruction like ADD 1 1, the server does the calculation, and sends the result back.

### What transport protocol do we use?

TCP

### How does the client find the server (addresses and ports)?

With it's ip address (or 127.0.0.1 if it's on localhost) and port 2020 (that's this year).

### Who speaks first?

the client send the meeting message (START).

### What is the sequence of messages exchanged by the client and the server? (flow)
the protocol is quite simple:

- client sends start, indicating it's ready to work

- server answers OK

- client sends operator

- server sends ok or nok (nok is not ok)

- client sends lefthand value

- server sends ok or nok

- client sends righthand value

- server sends ok or nok

- server sends result

- client sends if it want to do another operation (nok if it wants to, ok is ok for leaving)

- client stops if ok, do another round if ok

### What happens when a message is received from the other party? (semantics)

the server does the calculation (and the verification, even if it's useless).

the client checks that the value are correct, then sends them to the server for calculation, then it displays the answer.

### What is the syntax of the messages? How we generate and parse them? (syntax)

the parsing of double is done with methods from java.util.Scanner and with Double.parseDouble(Double_that_became_a_string_for_the_writer);

the string signals are shared, they are found in the Protocol class

### Who closes the connection and when?

the client can close it's connection: when asked to do another operation, and no is choosen, the server sends an OK which means that the client is ok to be disconnected.

the server is never closed, as another client might emerge and ask something

### Notes

protocol: 
```java
public class Protocol {

    public static final int CALC_DEFAULT_PORT = 2020;
    
    public static final String EOL = "\n";
    public static final String ADD = "ADD";
    public static final String SUB = "SUB";
    public static final String MUL = "MUL";
    public static final String DIV = "DIV";
    public static final String OK = "OK";
    public static final String NOK = "NOT OK";
    public static final String START = "START";
    
    public static final String OP[]={ADD,SUB,MUL,DIV};

}
```

0/0 throws an error (not X/0 as this has at least some sense), this is a choice, as NaN is used to check if the lefthand and righthand are correctly initialized

the input verification is done in both the client and server for the time being ( making the server one redundant)

I don't know how to use maven, therefore there is no pom.xml file

I don't know how to make a shared library out of Protocol.java for both the server and client