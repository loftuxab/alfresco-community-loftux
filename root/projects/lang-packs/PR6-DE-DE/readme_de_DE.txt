====================
Alfresco Sprachpaket
====================

Für Alfresco PR6

de_DE -- Deutsch (Deutschland)


================================
Bestandteile dieses Sprachpakets
================================

action-config_de_DE.properties

messages_de_DE.properties

rule-config_de_DE.properties


============
Installation
============

- Kopiere 'messages_de_DE.properties' in das Verzeichnis <config root>.

- Kopiere 'action-config_de_DE.properties' in das Verzeichnis <config root>/messages.

- Kopiere 'rule-config_de_DE.properties' in das Verzeichnis <config root>/messages.

- Ergänzen die Datei 'web-client-config.xml' im Verzeichnis <config root> um alle Sprachen,
  die in Ihrer Installation zur Verfügung stehen sollen:

  - Suchen Sie den Abschnitt '<languages>'
  - Tragen Sie dieses Sprachpaket wie folgt ein:

       '<language locale="de_DE">Deutsch (Deutschland)</language>'

- Speichern Sie die Datei

- Starten Sie den Alfresco Server neu


==================================
Contributors to this Language Pack
==================================

See the Alfresco Forum for status on Language Packs:
http://www.alfresco.org/forums/viewforum.php?f=16

Original Author(s): Alfresco Team
Contributors: Betty Mai, Gert Thiel