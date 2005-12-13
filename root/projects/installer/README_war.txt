======================
Alfresco 1.1.1 Release
======================

Welcome to the Alfresco 1.1.1 Release.


===================
Installing Alfresco
===================

The following is a guide to installing Alfresco.


=========================
Alfresco WAR Installation
=========================

Requirements:
- Java Development Kit available from http://java.sun.com
- MySQL Database available from http://www.mysql.com (other databases are supported)
- Alfresco available from http://www.alfresco.org

Optional:
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


Install Alfresco Tomcat Bundle
------------------------------

- Browse to http://www.alfresco.org/downloads
- Download the "Alfresco WAR Bundle" option
- Unzip alfresco-war-xxxxx.zip to a new directory or folder
- Copy the alfresco.war file to the appropriate location for your application server
- If you have deployed previous versions of Alfresco, you may wish to remove any
  temporary files you application server has created.  


Create Database
---------------

Alfresco requires a MySQL database named 'alfresco' with a user account and password
of 'alfresco'.  This can be set up manually by loading the 'db_setup.sql' file into
MySQL, for example, 'mysql -u root -p <db_setup.sql'.


You have now installed all the components needed to run the Alfresco server.


Optional Install of OpenOffice
------------------------------

If you would like to have a range of document transformations available from within
Alfresco, you need to install OpenOffice.  This is entirely optional and can be
done at any point after Alfresco has been installed.

- Browse to http://download.openoffice.org
- Download the appropriate version
- OpenOffice needs to be started in a specific way to work with Alfresco:

  soffice "-accept=socket,host=localhost,port=8100;urp;StarOffice.ServiceManager" -nologo -headless


===========================
Running the Alfresco Server
===========================

Start your application server
- you can now try Alfresco by visiting:

http://localhost:8080/alfresco

The server is configured with a single administrative login with user name and password
both set to 'admin'.

To test the installation, you may wish to follow the tutorial, available from:

http://www.alfresco.org/downloads or from the company space from within the Web Client.


===========================
Closing the Alfresco Server
===========================

Shut down your application server.  You may also wish to stop the OpenOffice process, but 
the command for this depends on your platform.


=============================
Upgrading from V1.0 to V1.1.1
=============================

In V1.1.1 some of the configuration has been changed.
In order to deploy V1.1.1 over the top of an existing V1.0 installation the following steps must be taken:

Note: Your existing Database and 'alf_data' directory will be preserved.

*** Any Configuration changes that you have made need to be noted. ***

*** Tomcat ***
For Tomcat, the 'alfresco' directory within 'Tomcat/webapps' needs to be deleted
Copy the V1.1 'alfresco.war' file into 'Tomcat/webapps' - either from downloading the WAR or the Tomcat Bundle
Start Tomcat, so that the 'alfresco' directory within 'Tomcat/webapps' is created
Stop Tomcat
Modify any configuration files that need changing
Start Tomcat

*** Jboss ***
For Jboss the 'Jboss/server/default/deploy/' and the 'Jboss/server/default/config/alfresco' directories need to be deleted.
Copy the V1.1 'alfresco.war' file from the V1.1 Jboss Bundle into 'Jboss/server/default/deploy/'
Copy the V1.1 'alfresco' config directory ('Jboss/server/default/config/alfresco') from the V1.1 Jboss Bundle into your existing 'Jboss/server/default/config/alfresco' directory
Modify any configuration files that need changing
Start Jboss


=====================
Using the CIFS Server
=====================

To enable the CIFS server on a Windows platform, the Win32NetBIOS.dll in 'C:\alfresco\bin' 
needs to be copied into a folder on the system path, such as 'c:\windows\system32'.  The 
Alfresco server will need to be re-started once the dll has been copied.

Once the Alfresco server is running, it should be possible to connect to it by mapping a
drive to it.  The name to use for the mapping is based on the name of the server on which
Alfresco is running, with '_A' on the end.  For example, if the PC name is 'MYPC01', then 
the CIFS server name will be 'MYPC01_A'.  To map the drive, open Windows Explorer, go
to the Tools menu and select 'Map Network Drive...'.  In the Map Network Drive dialog,
choose the drive letter you wish to use.  To locate the CIFS server, click the 'Browse...' 
button and find the server name as described above.  You should then have the option to
select a folder within it called 'alfresco'.  Click 'OK' to select the folder, then click
'Finish' to map the drive.  You should now have access to the Alfresco repository from
the mapped drive.  If the CIFS server name does not show in the browse dialog, you may also
enter the folder location directly in the dialog, for example '\\MYPC01_A\alfresco'.

To check the CIFS server name and whether it is running, enter the command 'nbtstat -n'
from a Command Prompt.  One of the listed names should be the CIFS server name.

If you are unable to connect to the CIFS server, then depending on your network, you may need 
to configure the domain for CIFS to use.  You will need to have started the Alfresco server
at least once to be able to do this.  To set the domain, edit the 'file-servers.xml' 
file in the '<config>/alfresco' directory and add the 
domain into the following line:
   <host name="${localname}_A"/>
so that it is something like:
   <host name="${localname}_A" domain="MYDOMAIN"/>

You will need to restart the Alfresco server for this to take effect.


==================
Image Manipulation
==================

To enable image manipulation on a Windows platform, the 'imconvert.exe' in
'C:\alfresco\bin' needs to be copied into a folder on the system path, such as 'C:\windows\system32'. 
On a Linux platform, just create a symbolic link named imconvert to the convert executable.


================
Trouble-Shooting
================

If you have problems with your installation, please look for help on the Installation
forum at http://www.alfresco.org/forums and ask for any additional help you may need.

- The JAVA_HOME variable must be set correctly to your Java5 installation.

- Most installation issues can be resolved by following advice in this forum article:
  http://www.alfresco.org/forums/viewtopic.php?t=7
  and in this forum generally:
  http://www.alfresco.org/forums/viewforum.php?f=8

- WAR file name is called alfresco.war
  NOTE: If you deployed the war previously then you must clear out the web-client files 
  before deploying the new WAR file, having first copied any configurations made:

  Previous release was PR6 or later:
  Delete <tomcat-home>/webapps/alfresco.war
  Delete <tomcat-home>/webapps/alfresco  <-- make sure any configurations are copied
  Delete <tomcat-home>/work/alfresco

- If the following errors are reported on the console:
  ERROR [AbstractImageMagickContentTransformer] JMagickContentTransformer not available:
  ERROR [AbstractImageMagickContentTransformer] ImageMagickContentTransformer not available:
  Failed to execute command: imconvert ...

  These are not issues which will cause the server to fail. Alfresco is reporting that 
  various external document transformation engines are not available for use by the server.   
  Either follow the instructions at the bottom of the Release Notes Wiki page:
  http://www.alfresco.org/mediawiki/index.php/Preview_Release_5
  or remove the transformer references completely if you don't require them:
  http://www.alfresco.org/forums/viewtopic.php?t=90

- If you see this error on server startup:
  ERROR [protocol] FTP Socket error
    java.net.BindException: Address already in use: JVM_Bind
         at java.net.PlainSocketImpl.socketBind(Native Method)
  Check to see if you have any services running against port 8080 for the Alfresco server and
  port 21 for the Alfresco FTP integration.

- To access the CIFS repository directly from the FireFox browser, you need to install the
  Alfresco FireFox Extension from here:
  http://sourceforge.net/projects/alfresco
  
  Internet Explorer does not require the extension to open CIFS folders directly.
