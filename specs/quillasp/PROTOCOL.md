# RES - Labo3 - Spécification du protocol
> Auteur : Quillasp (c'est moi)

### What transport protocol do we use?
TCP

### How does the client find the server (addresses and ports)?
Adresse ip + port 5190 

### Who speaks first?
Le client

### What is the sequence of messages exchanged by the client and the server? (flow)
* client téléphone serveur : « steuplé, tu peux m'faire ça ? »,
* serveur répond client : « ouais, pad'souci ! »,
* serveur envoie réponse,
* client affiche réponse

### What happens when a message is received from the other party? (semantics)
* serveur traite commande client
* serveur envoie retour :
    * si c'est bon : «Tiens, c'est tout bon : <réponse>. »
    * si c'est pas bon : « euh… j'pas compris »
* client affiche réponse serveur

### What is the syntax of the messages? How we generate and parse them? (syntax)
`plsdodat <operand1> <operand2> <operator>`

* s'il n'y a pas `plsdodat`, le serveur refuse l'ordre (faut toujours dire « sivoplé »),
* si `<operand1>` et `<operand2>` ne sont pas des chiffres, le serveur va aussi « hurler »,
* `<operator>` doit être soit :
    * `+` pour l'addition,
    * `-` pour la soustraction,
    * `*` pour la multiplication,
    * `/` pour la division,
    * `%` faut pas déconner.
    
Contrairement à Fortran (j'sé plus lequel), l'espace est déterminant.

### Who closes the connection and when?
Le client, quand il dira `kthx` (faut toujours dire « merci »).

### Pourquoi markdown quand on a org avec Emacs ?
Je me pose encore la question.

(ce fichier est encore sujet à modification pour faire en sorte qu'il corresponde plus ou moins à la réalité (parce 
que sur la papier, ça a l'air d'aller))
