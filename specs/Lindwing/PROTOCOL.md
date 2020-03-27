### What transport protocol do we use?
Nous utilisons le protocol TCP.
   ### How does the client find the server (addresses and ports)?
   Le client doit connaître l'adresse et le port du serveur.
   ###   Who speaks first?
   Le client doit parler en premier pour initier la "conversation".
   ###   What is the sequence of messages exchanged by the client and the server? (flow)
   client (envoie message) -> serveur (reçoit le message, puis traite la demande et envoie la réponse) -> le client (reçoit le message et l'affiche)
   ###   What happens when a message is received from the other party? (semantics)
   Quand le serveur reçoit le message: 
- regarde si le message envoyé est valide
	- Si le message est valide, le serveur va réaliser l'opération demandé et retourner la réponse.
	- Si le message est invalide, il le fait savoir au client.
	
Quand le client reçoit le message il l'affiche
   ###   What is the syntax of the messages? How we generate and parse them? (syntax)
  Le syntaxe du message du client vers le serveur est :
- "opération" "opérande 1" "opérande 2"

Lorsqu'on reçoit le message on sépare les différentes parties en utilisant les espaces.

Les espaces permettront de parser le message.
   ###   Who closes the connection and when?
   Le serveur et cela se passe après que le serveur ait envoyé vers le client la réponse.