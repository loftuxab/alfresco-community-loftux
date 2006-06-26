======================
Alfresco Language Pack
======================

Pour la version : 1.3.0dev

Pour la langue : fr_FR


===========================
Contenu de ce Language Pack
===========================

*_fr_FR.properties, reflétant les modifications du language pack en_US
Les fichiers XLIFF et diff sont uniquement inclus dans le language pack fr_FR et ne sont pas reconduits dans celui-ci.


============
Installation
============

- Copier tous les fichiers vers le répertoire <custom config>/messages.

- Editer le fichier 'web-client-config-custom.xml' dans le répertoire <custom config> pour préciser la liste des langues disponibles :

  - Rechercher la section '<languages>'
  - Ajouter ou retirer une langue de la forme :

       '<language locale="fr_FR">Française (France)</language>'

- Cette modification ajoute la langue aux langues configurés par défaut. Si vous souhaitez plutôt remplacer ceux-ci, alors ajoutez l'option "replace=true".

- l'ordre de configuration des langues définit l'ordre dans lequel elles sont présentés sur la page de connexion.

- Enregistrer le ficher

- Redémarrer le serveur Alfresco


=========================
Note pour les traducteurs
=========================

Si le message contient une variable, c'est à dire {0}, alors les apostrophes (') doivent être doublées ('') pour être affichées correctement. Si le message ne contient pas de variable, alors une simple apostrophe peut être utilisée.

Tous les messages de ce language pack ont été traduits. Nous recommandons d'utiliser l'application Attesoro pour l'édition simplifiée des traductions. Vous pouvez le télécharger sur http://attesoro.org/

Exécutez-le simplement et ouvrez le répertoire où se trouvent les fichiers *_en_US.properties et *_fr_FR.properties.

Les fichiers XLIFF sont uniquement inclus dans le language pack en_US. Ils peuvent être utilisés par de nombreux outils de traduction, comme les Open Language Tools (http://open-language-tools.dev.java.net).


================================
Contributeurs à ce Language Pack
================================

Voir le groupe de discussion Alfresco Forum pour le statut des Language Packs :
http://www.alfresco.org/forums/viewforum.php?f=16

Pour le language pack Français :
http://www.alfresco.org/forums/viewforum.php?f=150

Auteur(s) Originaux : L'équipe Alfresco
Traduction originale Anglais - Français : Camille Bégnis, Laurent Genier, Christian Roy, Philippe Seillier, Frank Shipley
Contributeurs à cette version : Laurent Genier