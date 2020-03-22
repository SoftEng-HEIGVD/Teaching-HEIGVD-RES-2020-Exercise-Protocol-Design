# Calculator Protocol
## Authors Alban Favre and Alban Favre
### What
This is a small server-client application: the client sends an arithmetical instruction like ADD 1 1, the server does the calculation, and sends back the result.

### How
This server uses a TCP socket on port 2020 (the COVID-19 year).
the protocol is quite simple:

- client sends start, indicating it's ready to work

- client sends operator
- server sends ok or nok (not ok)
- client sends lefthand value
- server sends ok or nok
- client sends righthand value
- server sends ok or nok
- server sends result
- client sends if it want to do another operation (nok if it wants to, ok is ok for leaving)
- client and server stop if ok, do another round if ok

it gives the result in double.

### Note

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

