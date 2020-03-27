# Phase 1

- Utilisation du protocole TCP 
- On se connecte sur le port 31415 sur localhost
- le client effectue la première requête.
- **Ne pas oublier l'encodage**
- **Les opérations prise en compte**
- **amélioré l'acceptation d'un client**
- **comportement après une erreur**
- La séquence de message envoyé est tel que suit :
  - Client : envoie sa requête "nombre<espace>signe<espace>nombre"
  - Serveur :
    - requête correct effectue le calcul "nombre"
    - requête incorrect envoie une erreur "ERROR" 
  - le client ferme la connexion "BYE" ou le serveur après un timeout "BYE"

# Phase 3

![](/home/jerome/HEIG/Labo/RES/Teaching-HEIGVD-RES-2020-Exercise-Protocol-Design/specs/hansArn/img/PROTOCOL.md.png)


