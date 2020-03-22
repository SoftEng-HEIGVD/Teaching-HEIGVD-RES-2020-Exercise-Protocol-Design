transport protocol used : TCP

encoding : UTF-8

The client uses and IP address and a port number : 1777

The client speaks first

**flow:**

1: client requests a connection

2: server accepts the connection

3: client sends a calculation request

4: server responds with the result

repeat 3 and 4 indefinetly

5: client ends connection

The client only listens for a message after having sent a calculation request.

when a message is revieved by the server, it processes the message and sends an answer if needed

**Messages syntax:**  
calculation request : 
    Is composed of a string "CALC", a first number, an operator, a second number.
    All are separated by one space.
result : 
    Is composed of a string "RSLT", a number
close connection : 
    Is composed of only a string "CEND"

a number is :  
    -first, an optional + or -  
    -Then, one or more digits  
    -Finally, an optional dot (".") followed by other digits  
    The maximal length of a number is 10 digits


Any other message recieved by the server will get no answer.  
the client closes the connection when they choose to.