
=== Installation Requirements ===

 - Alfresco 1.4 RC1 or higher repository
 - PHP 5.1.6 (this is the version the libraray has currently been tested on)
 - PHP enabled web server
 - PHPUnit PEAR module needs to be installed to run unit tests.
 - ANT is required to build distribution packages

Make sure the PHP5 SOAP implementation is enabled by adding the following line in your php.ini.

extension=php_soap.dll

 NOTE: this has only been tested on a WinXP with Apache 2.0.x see 'http://us2.php.net/manual/en/install.windows.apache2.php' for some tips on how to install and configure.

===  Installing Alfresco PHP Library ===

 - Unzip Alfresco php distribution.
 - Ensure that 'Alfresco' directory is on the PHP include path (have a look here for a brief intro on how to set the PHP include path 'http://www.modwest.com/help/kb.phtml?qid=98&cat=5')
 - The Alfresco PHP library is now ready for use.


=== Getting the Examples Working ===

 - Ensure that the 'Examples' directory is accessable from your PHP enabled web server.
 - Point your browser to <host>/Examples to browse the available examples.


