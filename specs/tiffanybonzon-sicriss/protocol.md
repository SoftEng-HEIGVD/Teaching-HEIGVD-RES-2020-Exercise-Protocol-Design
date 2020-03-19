# Protocole

Tiffany Bonzon & Thoeny Laurent



Le serveur et le client communiquent sur le port `2112`, le serveur écoute en permanence sur ce dernier et c'est au client de s'annoncer.

Le client s'annonce au serveur à l'aide d'un message `Hello`.

Le serveur répond à ce message à l'aide de `OPs : [liste des opérations]` et confirme ainsi au client que ce dernier dispose d'une connexion ouverte.

Une fois la liste des opérations reçue (`ADD` `SUB` `MULT` `DIV` et `POW`), le client peut envoyer un message au serveur avec la forme `[opération] [opérande1] [opérande2]`.

Lors de la récéption d'un message sous cette forme, le serveur procède à l'opération et renvoie un message sous la forme `[résultat]`.

Si une erreur est rencontrée par l'un ou l'autre des participants, un message `ERROR [description]` est transmis et la connexion est fermée par le tier qui l'a rencontrée. La description est optionnelle.

Un client peut effectuer plusieurs opérations consécutives sans devoir ré-initialiser une connexion.

Lorsque le client ne veut plus effectuer d'opération ou après _60 secondes_ d'inactivité, la connexion est fermée par le serveur. Si le client désire fermer la connexion il envoie simplement `END`

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