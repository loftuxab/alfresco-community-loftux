======================
Alfresco Language Pack
======================

For release: PR6

For locale: ru-RU (Russian)


==============================
Contents of this Language Pack
==============================

action-config_ru_RU.properties

messages_ru_RU.properties

rule-config_ru_RU.properties


============
Installation
============

- Copy 'messages_ru_RU.properties' file into <config root> folder.

- Copy 'action-config_ru_RU.properties' file into <config root>/messages folder.

- Copy 'rule-config_ru_RU.properties' file into <config root>/messages folder.

- Edit the 'web-client-config.xml' file in the <config root> folder to set what languages
  you wish to be available:

  - Find the '<languages>' section
  - Add a new language line:

       '<language locale="ru_RU">Russian</language>'

- Save the file

- Restart the Alfresco server


==================================
Contributors to this Language Pack
==================================

See the Alfresco Forum for status on Language Packs:
http://www.alfresco.org/forums/viewforum.php?f=16

Original Author(s):
Andrejus Chaliapinas
INFOSANA - Information Systems Analysis and Adaptation, Lithuania
info@infosana.com

Contributors: