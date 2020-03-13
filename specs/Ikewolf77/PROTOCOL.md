##specifications

* TCP protocol, UTF-8
* Address : calculator_online Port : 3030
* Client speaks first
* Client says : "Hello calculator" (if other -> server close connection)
* Server says : "Connection operational" (if other -> client close connection)
* Semantic :
    * Client sends "(number) (operator) (number) calculate"
    * Server sends "(number) calculated"
    * ===  
    * Client sends impossible Operations (Ex : 3 / 0 calculate)
    * Server sends "Impossible operation"
    * ===
    * Client sends not an operation
    * Server sends "Unrecognized command"
* Client wants to quit : sends "Quit"
* Server closes connection
