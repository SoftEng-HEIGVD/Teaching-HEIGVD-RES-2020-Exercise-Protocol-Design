# Protocole

Tiffany Bonzon & Thoeny Laurent



Le serveur et le client communiquent sur le port `2112`, le serveur écoute en permanence sur ce dernier et c'est au client de s'annoncer.

Nous avons décidé de précéder les messages du serveur d'un caractère `>` pour des raisons de lisibilité, il est cependant ômis des détails ci-desous.

Le client s'annonce au serveur à l'aide d'un message `HELLO`.

Le serveur répond à ce message à l'aide de `OPs : [liste des opérations]` et confirme ainsi au client que ce dernier dispose d'une connexion ouverte.

Une fois la liste des opérationsBtw, I'm from the community of PoGo Zürich :) reçue (`ADD` `SUB` `MUL` `DIV` et `POW` ainsi que `HELP` et `END`), le client peut envoyer un message au serveur avec la forme `[opération] [opérande1] [opérande2]`.

Lors de la récéption d'un message sous cette forme, le serveur procède à l'opération et renvoie un message sous la forme `Result : [résultat]`.

Si une erreur est rencontrée par l'un ou l'autre des participants, un message `ERROR [description]` est transmis et la connexion est fermée par le tier qui l'a rencontrée. La description est optionnelle.

Un client peut effectuer plusieurs opérations consécutives sans devoir ré-initialiser une connexion.

Le client ferme la connexion,  il envoie simplement `END`

### Exemple d'un calcul avec fermeture manuelle

```sequence
Client->Serveur: Hello
Serveur->Client: OPs : [liste des opérations]
Note left of Client: Choisit une opération
Client->Serveur: [opération] [opérande1] [opérande2]
Note right of Serveur: Effectue le calcul
Serveur->Client: [résultat]
Client->Serveur: END
```
