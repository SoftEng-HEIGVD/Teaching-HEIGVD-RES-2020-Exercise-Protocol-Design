*What transport protocol do we use?
 TCP

*How does the client find the server (addresses and ports)?
 Adresses : Given
 Port : 8080

*Who speaks first?
 Client speaks firsts and say "Hello".

*What is the sequence of messages exchanged by the client and the server? (flow)
 Client  - Hello
 Serveur - Hello, type help for the help menu
 Client  - help
 Sereur  - <help menu>
 Client  - add 2 2
 Serveur - 4
 Client  - sub 18 5
 Serveur - 13
 ....


*What happens when a message is received from the other party? (semantics)
 The client displays the message and the server executes the command.

*What is the syntax of the messages? How we generate and parse them? (syntax)
 message = <op> <var1><var2>
 <op> = add|sub|mul|div
 <var1> = <number>
 <var2> = <number>
    
*Who closes the connection and when?
 In the normal situation, the client.
 In the case of timeout, the server.
 In the case of an error, the server