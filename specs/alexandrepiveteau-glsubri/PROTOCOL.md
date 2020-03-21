# Calculator Protocol

## About this document

In this protocol file, we propose a way for a computer to write a calculation locally, save it, and ask a remote computer to perform a computation and return its result.

+ Alexandre **Piveteau**
+ Guy-Laurent **Subri**

## Specification

There are two actors in the protocol : a **server**, which will perform computations, and a **client**, which will ask the _server_ to perform the operations.

### Discovery

The _server_ opens a TCP socket on the port 8080. Both the client and the server run on `localhost`. Messages are sent in **UTF-8**.

### Protocol
#### Connection set up

01. `client -> "START\n"`
02. `server -> "GO AHEAD\n"`
03. `client -> "N 647\n"`
04. `server -> "OK N\n"`
05. `client -> "O ADD\n"`
06. `server -> "OK O ADD\n"`
07. `client -> "N 123\n"`
08. `server -> "OK N\n"`
09. `client -> "PERFORM\n"`
10. `server -> "RES 770\n"`

The connection is closed by the server whenever the result is sent.

Valid numbers are positive and negative integers.
Valid operations are `ADD`, `SUB` (which subtracts the second operand from the first one), `MUL` and `DIV`.

In case of an error, the server or client sends the message `"ERR\n"`. The connection is reset by the actor who sent the error.

The flow of request should always be `N x`, `O op` and finally `N y`, otherwise, an error will be thrown.