======================
Alfresco Language Pack
======================

For release: PR6

For locale: el-GR


==============================
Contents of this Language Pack
==============================

action-config_el_GR.properties

messages_el_GR.properties

rule-config_el_GR.properties


============
Installation
============

- Copy 'messages_el_GR.properties' file into <config root> folder.

- Copy 'action-config_el_GR.properties' file into <config root>/messages folder.

- Copy 'rule-config_el_GR.properties' file into <config root>/messages folder.

- Edit the 'web-client-config.xml' file in the <config root> folder to set what languages
  you wish to be available:

  - Find the '<languages>' section
  - Add a new line:

       '<language locale="_el_GR">Greek</language>'

- Save the file

- Restart the Alfresco server


==================================
Contributors to this Language Pack
==================================

See the Alfresco Forum for status on Language Packs:
http://www.alfresco.org/forums/viewforum.php?f=16

Original Author(s): Alfresco Team
Contributors: Theodoros Papageorgiou (ptheo@menalon.com)