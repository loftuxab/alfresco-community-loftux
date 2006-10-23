============
Alfresco 1.4
============

Welcome to the Alfresco 1.4 Release.  


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
- Alfresco available from http://www.alfresco.com

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
  www.alfresco.com for guidelines
- If you already have MySQL 4.1 or above installed, skip to "Install Alfresco WAR Bundle"

- Browse to http://dev.mysql.com/downloads/mysql
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


Install Alfresco WAR Bundle
---------------------------

- Browse to http://www.alfresco.com/downloads
- Download the "Alfresco WAR Bundle" option
- Unzip alfresco-war-xxxxx.zip to a new directory or folder
- Copy the alfresco.war file to the appropriate location for your application server
- If you have deployed previous versions of Alfresco, you may wish to remove any
  temporary files you application server has created.  


Add MySQL Database Connector
----------------------------

- Download the MySQL Java Database Connector http://dev.mysql.com/downloads/connector/j/
- Copy the JAR file into <container>/common/lib or <container>/server/default/lib


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

*** There are known issues with OpenOffice 2.0.1 and 2.0.2 ***

- Browse to http://download.openoffice.org
- Download the appropriate version
- OpenOffice needs to be started in a specific way to work with Alfresco:

  soffice "-accept=socket,host=localhost,port=8100;urp;StarOffice.ServiceManager" -nologo -headless


Configure the Database Connection and Data Locations
----------------------------------------------------

Details can be found at http://wiki.alfresco.com/wiki/Repository_Configuration#New_Installations


===========================
Running the Alfresco Server
===========================

Start your application server
- you can now try Alfresco by visiting:

http://localhost:8080/alfresco

The server is configured with a single administrative login with user name and password
both set to 'admin'.

To test the installation, you may wish to follow the tutorial, available from:

http://www.alfresco.com/downloads or from the company space from within the Web Client.


===========================
Closing the Alfresco Server
===========================

Shut down your application server.  You may also wish to stop the OpenOffice process, but 
the command for this depends on your platform.


===========================
Upgrading from V1.x to V1.4
===========================

http://wiki.alfresco.com/wiki/Upgrading_to_1.4.0


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
at least once to be able to do this.  To set the domain, edit the 'file-servers-custom.xml' 
file in the '<config>/alfresco/extension' directory and add the 
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
forum at http://forums.alfresco.com and ask for any additional help you may need.

- The JAVA_HOME variable must be set correctly to your Java5 installation.

- If the following errors are reported on the console:
  ERROR [AbstractImageMagickContentTransformer] JMagickContentTransformer not available:
  ERROR [AbstractImageMagickContentTransformer] ImageMagickContentTransformer not available:
  Failed to execute command: imconvert ...

  These are not issues which will cause the server to fail. Alfresco is reporting that 
  various external document transformation engines are not available for use by the server.   

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
