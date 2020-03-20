# Specification

## 1. What transport protocol do we use?

We use TCP.

## 2. How does the client find the server (addresses and ports)?

The port used is 25566. 

The IP address is dynamic.

## 3. Who speaks first?

The client initialize the communication

## 4. What is the sequence of messages exchanged by the client and the server? (flow)

client open connection

**client -> server** : handshake

**server -> client** : handshake response

**client -> server** : send operation

**server -> client** : send result or error

client close connection

## 5. What happens when a message is received from the other party? (semantics)

**Server :**

1. **Receive handshake** : 
   1. Send handshake response with supported operations
2. **Receive operation** : 
   1. Check if the operation is supported
   2. Execute calculation 
   3. Send result or error if any

**Client :**

1. **Receive handshake response** : 
   1. Check if desired operation is supported by the server
   2. If yes, send operation
   3. If no, close connection
2. **Receive operation result or error** :
   1. Print result or error if any

## 6. What is the syntax of the messages? How we generate and parse them? (syntax)

**handshake :**

```
CALC CLIENT
```



**handshake response :**

```
CALC SERVER;<SUPPORTED OPERATIONS>
```

Example : 

```
CALC SERVER;ADD SUB MUL DIV
```



**send operation :**

```
<OPERATION> <OPERAND 1> <OPERAND 2>
```

Example :

```
ADD 6 4
SUB 5 1
MUL 2 4
DIV 10 0
```



**send result or error :**

If the operation is correct :

```
RES <RESULT>
```

If the operation is incorrect or there is an error :

```
ERR <DESCRIPTION>
```

Example :

```
RES 10
RES 4
RES 8
ERR Cannot divide by 0
```



## 6. Who closes the connection and when?

The client close the connection when he receive the response of the operation from the server.