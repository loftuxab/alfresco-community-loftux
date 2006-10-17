readme.txt: 

====================== 
Alfresco Language Pack 
====================== 

For release: 1.4 

For locale: tr-TR (default) 


============================== 
Contents of this Language Pack 
============================== 

*_tr_TR.properties 


============ 
Installation 
============ 

- Download the File V1.4_tr_TR.zip from Alfresco Forge 

- Decompress it to a temporary folder 

- Copy all *.properties files into <config_root>/messages folder. From release 1.2, the config_root folder can be in a 
  different location to the web application root. <config_root> under tomcat is usually something like: 
  alfresco/tomcat/webapps/alfresco/WEB-INF/classes/alfresco 

- Edit the 'web-client-config.xml' file in the <config root> folder to set what languages you wish 
  to be available: 

- Find the '<languages>' section 
- Add or remove languages of the form: 

'<language locale="tr_TR">Turkce</language>' 

- The order of the language entries determines the order they are presented on the login prompt. 
- Save the file. 

- Restart the Alfresco server. 

================================== 
Contributors to this Language Pack 
================================== 

See the Alfresco Forum for status on Language Packs: 
http://www.alfresco.org/forums/viewforum.php?f=16 

Contributed By: Vardar Software, Consultancy, Training
                http://vardar.biz.tr
Contributors: Umit Vardar, Iffet Oguz, Turgay Zengin