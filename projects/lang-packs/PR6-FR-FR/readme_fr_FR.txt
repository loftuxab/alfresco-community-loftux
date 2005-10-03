======================
Alfresco Language Pack
======================

For release: PR6

For locale: fr_FR


===========================
Contenu de ce Language Pack
===========================

action-config_fr_FR.properties

messages_fr_FR.properties

rule-config.properties_fr_FR


============
Installation
============

- Copier le fichier 'messages_fr_FR.properties' vers le dossier <config root>.

- Copier le fichier 'action-config_fr_FR.properties' vers le dossier <config root>/messages.

- Copier 'rule-config_fr_FR.properties' vers le dossier <config root>/messages.

- Editer le fichier 'web-client-config.xml' dans le dossier <config root> pour préciser la liste des langues disponibles :

  - Rechercher la section '<languages>'
  - Ajouter ou retirer une langue de la forme :

       '<language locale="fr_FR">Française (France)</language>'

- Enregistrer le ficher

- Redémarrer le serveur Alfresco


================================
Contributeurs à ce Language Pack
================================

Voir le groupe de discussion Alfresco Forum pour le statut des Language Packs :
http://www.alfresco.org/forums/viewforum.php?f=16

Auteur(s) Originaux : L'équipe Alfresco
Contributeurs :
Traducteurs Anglais - Français : Camille Bégnis, Laurent Genier, Christian Roy, Philippe Seillier, Frank Shipley