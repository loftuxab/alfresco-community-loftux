
=== Installation Requirements ===

 - Alfresco RC1b Repository
 - PHP 5.x
 - SOAP Pear Module
 - PHP enabled web server

 NOTE: this has only been tested on a WinXP with Apache 2.0.x see 'http://us2.php.net/manual/en/install.windows.apache2.php' for some tips on how to install and configure.


===  Installing Alfresco PHP Library ===

 - Unzip Alfresco php distribution.
 - Ensure that 'alfresco' directory is on the PHP include path (have a look here for a brief intro on how to set the PHP include path 'http://www.modwest.com/help/kb.phtml?qid=98&cat=5')
 - Add the following config to your php.ini file pointing to the server hositng your Alfresco repository:
 
   [alfresco]
   ; Alfresco server location
   alfresco.server=http://localhost:8080

 - The Alfresco PHP library is now ready for use.


=== Getting the Examples Working ===

 - Ensure that the 'examples' directory is accessable from your PHP enabled web server.
 - Point your browser to <host>/examples/browse/index.php to browse the Alfresco repository.


