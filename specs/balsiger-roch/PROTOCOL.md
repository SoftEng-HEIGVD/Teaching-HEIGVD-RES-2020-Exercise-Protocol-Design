# PROTOCOLE

Le serveur écoute le port TCP 4452.

Le client a besoin de l'ip du serveur et utilise le port 4452 pour s'y connecter.

Le client initie la connexion avec le serveur, et attend la liste des opérations supportées.

Le serveur envoie un message du format :

`CALC SERVER (`*Nom du serveur et version*`)`

puis il envoie la liste des opérations supportées séparées par un espace.

Les opérations standards sont `ADD` `SUB` `MULT` `DIV`

Après réception de la liste des opérations, le client peux demander un calcul au serveur avec un message de la forme : *Opération* **Espace** *opérandes*

Les opérandes sont séparées par des espaces. Pour `ADD` `SUB` `MULT` et `DIV` il y a toujours 2 opérandes.

Le serveur envoie le résultat du calcul sous la forme : `RESULT ` *nombre*

Dans le cas d'une erreur le serveur n'envoie pas de résultat, mais un message d'erreur au format : `ERROR ` *description optionnel*

Plusieurs calculs peuvent être demandés par le client avec la même connexion TCP, sur des lignes différentes. Les réponses du serveur doivent être dans le même ordre que les demandes du client.

Le client ferme la connexion en fermant son socket.

En cas de non respect du protocole, la connexion est fermée. L'entité fermant la connexion peux envoyé un message d'erreur juste avant de fermer la connexion sous la forme : `ERROR ` *message*

## Exemple

### Demande addition

```sequence
participant Client as cli
participant Serveur as srv
cli -->> srv: TCP connection
srv -> cli: CALC SERVER (Ali v.0.2.1)
srv -> cli: ADD SUB MULT DIV
cli -> srv: ADD 2 4
srv -> cli: RESULT 6
```

### Erreur serveur

```sequence
participant Client as cli
participant Serveur as srv
cli -->> srv: TCP connection
srv -> cli: CALC SERVER (Ali v.0.2.1)
srv -> cli: ADD SUB MULT DIV
cli -> srv: ADD 123456789012345 1234567890123456789
srv -> cli: ERROR overflow
```

### Erreur de protocole côté client

```sequence
participant Client as cli
participant Serveur as srv
cli -->> srv: TCP connection
srv -> cli: CALC SERVER (Ali v.0.2.1)
srv -> cli: ADD SUB MULT DIV
cli -> srv: 45 + 23
srv -> cli: ERROR parsing failed
srv -->> cli: TCP close
```

### Erreur de protocole côté serveur

```sequence
participant Client as cli
participant Serveur as srv
cli -->> srv: TCP connection
srv -> cli: CALC SERVER (Ali v.0.2.1)
srv -> cli: ADD SUB MULT DIV
cli -> srv: SUB 3 2
srv -> cli: 1
cli -> srv: ERROR parsing failed
cli -->> srv: TCP close
```

### 