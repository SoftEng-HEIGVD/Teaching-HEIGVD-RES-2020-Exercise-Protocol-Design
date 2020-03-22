# RES 2020 Exercice 
#### @Authors  : Wenes Limem & Zied naimi

* What transport protocol do we use ?
 
   __We opt toward using TCP/IP.__ 
* How does the client find the server (addresses and ports)?

 __Using the IP address given by Docker, and port number is given and fixed : 2342__    
* Who speaks first?

  __The client starts contacting the server with a request.__
* What is the sequence of messages exchanged by the client and the server? (flow)
    1. Handshake & block all other incoming requests 
    2. Request to calculate --> from : client to : server
    3. Answer with the result--> from : server to : client
    4. Expect next request ...  

*  What happens when a message is received from the other party? (semantics)
  
    __It must be answered.__ 
* What is the syntax of the messages? How we generate and parse them? (syntax)
    
    __**OPERATOR OPERAND1 OPERAND2**__   ==> ADD 2 5 
    
   On the server end , a dedicated function will parse the received 
   request. Then a string containing the result will be sent back to client. 
    __**Answer is : ANSWER**__           ==> Answer is : 7.0  
* Who closes the connection and when?
      
    __The server closes the connection after a timeout.__
        