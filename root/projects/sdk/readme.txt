====================
Alfresco SDK Release
====================

Welcome to the Alfresco SDK Release.

The SDK provides support for developers who wish to extend or
customise the Alfresco platform.  It includes:

/lib      - pre-built Alfresco .jars and all dependencies
/doc      - reference material and Javadoc
/src      - source code
/bin      - supporting dll's, exe's
/licenses - license files
/samples  - template projects for common development scenarios

Supported Development scenarios are:

- Embedding Alfresco and writing custom code against Alfresco's
  Java Foundation API or standards-compliant JCR API
- Writing client applications against a standalone Alfresco server
  via Alfresco's Web Service API
- Developing Repository plug-ins such as:
  - Custom Actions / Conditions
  - Custom Aspects
  - Custom Transformers

The SDK may be used stand-alone, but an Alfresco Release Installation is
also required if performing any of the following:

- Customising the Alfresco Web Client
- Testing a custom client that connects to a remote Alfresco Server
- Deploying a tested custom plug-in to an Alfresco Server


============
Requirements
============

- Java Development Kit 5.0 available from http://java.sun.com
- MySQL Database available from http://www.mysql.com (other databases are supported)

Optional:
- Eclipse IDE available from http://www.eclipse.org (recommended)
- OpenOffice for document transformation available from http://www.openoffice.org


===================
Simple Installation
===================

Install JDK 5.0
---------------

- If you already have J2SE Development Kit 5.0 installed, skip to "Install MySQL"

- Browse to http://java.sun.com/j2se/1.5.0/download.jsp
- Select the "JDK 5.0 Update x" option
- Download the "Windows Offline Installation" option (~55M)
- Install once downloaded


Install MySQL
-------------

- If you wish to use other databases, please visit the forums and wiki at
  www.alfresco.org for guidelines
- If you already have MySQL 4.1 installed, skip to "Install Alfresco"

- Browse to http://dev.mysql.com/downloads/mysql/4.1.html
- Download the appropriate package for your platform
- Install once downloaded
- Use Typical setup type
- You may skip the MySQL.com sign-up
- Configure using options appropriate to required use
  - for demo, choose default selected options
  - for non-English or non-West European languages, choose UTF8 Character set
- Test MySQL is installed and running by opening a command prompt and entering:
  'mysql -u root -p'
- When prompted, give the password you set during installation.  If no errors
  are reported, then it is installed and running.  Enter 'quit' to exit.


Create Database
---------------

The SDK samples assume a MySQL database named 'alfresco' with a user account
and password of 'alfresco'.  This can be set up manually by loading the 'db_setup.sql'
file located in the 'bin' directory of this bunlde into MySQL, for example, 

'mysql -u root -p < ./bin/db_setup.sql'.


You have now installed all the components needed to develop with the SDK.


Optional Install of Eclipse
---------------------------

TODO:


Optional Install of OpenOffice
------------------------------

If you would like to have a range of document transformations available from within
Alfresco, you need to install OpenOffice.  This is entirely optional and can be
done at any point after Alfresco has been installed.

- Browse to http://download.openoffice.org
- Download the appropriate version
- OpenOffice needs to be started in a specific way to work with Alfresco:

  soffice "-accept=socket,host=localhost,port=8100;urp;StarOffice.ServiceManager" -nologo -headless


================================
Getting Started with the Samples
================================

TODO: Refer to WIKI page

