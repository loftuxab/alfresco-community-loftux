======================
Alfresco Language Pack
======================

For release: PR6

For locale: zh_CN (Simplified Chinese)


==============================
Contents of this Language Pack
==============================

action-config_zh_CN.properties

messages_zh_CN.properties

rule-config_zh_CN.properties


============
Installation
============

- Copy 'messages_zh_CN.properties' file into <config root> folder.

- Copy 'action-config_zh_CN.properties' file into <config root>/messages folder.

- Copy 'rule-config_zh_CN.properties' file into <config root>/messages folder.

- Edit the 'web-client-config.xml' file in the <config root> folder to set what languages
  you wish to be available:

  - Find the '<languages>' section
  - Add a new language line:

       '<language locale="zh_CN">Chinese (Simplified)</language>'

- Save the file

- Restart the Alfresco server


==================================
Contributors to this Language Pack
==================================

See the Alfresco Forum for status on Language Packs:
http://www.alfresco.org/forums/viewforum.php?f=16

Original Author(s): cnalfresco

Contributors: