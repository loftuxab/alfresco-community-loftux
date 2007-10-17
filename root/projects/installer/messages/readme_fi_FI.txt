======================
Alfresco Language Pack
======================

For release: 2.0Preview

For locale: EN-US (default)


==============================
Contents of this Language Pack
==============================

*_fi_FI.properties


============
Installation
============

- Copy all files into <config root>/messages folder.

- Edit the 'web-client-config.xml' file in the <config root> folder to set what languages
  you wish to be available:

  - Find the '<languages>' section
  - Add or remove languages of the form:

       '<language locale="XX_YY">LangName</language>'

- The order of the language entries determines the order they are presented on the login prompt.
- Save the file.

- Restart the Alfresco server.


====================
Note for translators
====================

If the message contains a variable e.g. {0} then any single quotes must be doubled up as '' to 
escape them correctly.  If the message does not contain a variable then a single quote character
should be used as normal.

We recommend using the Attesoro application for easy editing of translations, is is allows side-by-side editing and automatic encoding of characters, as well as highlighting untranslated entries.  You can download it from: 
  http://attesoro.org/

Simply start it and open the folder where the *_fi_FI.properties are located.


==================================
Contributors to this Language Pack
==================================

See the Alfresco Forum for status on Language Packs:
http://www.alfresco.org/forums/viewforum.php?f=16

Original Author(s): Alfresco Team
Contributors:
