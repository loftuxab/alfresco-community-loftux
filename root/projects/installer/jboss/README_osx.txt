============================
Alfresco 1.4 Release Preview
============================

Welcome to the Alfresco 1.4 Release Preview.  This software is for evaluation purposes only and cannot 
be used against an existing Alfresco repository.  There will not be an upgrade path from this
Release Preview to the final 1.4 release.


===================
Installing Alfresco
===================

The following is a guide to installing Alfresco.


===================================
Alfresco JBoss Bundled Installation
===================================

Requirements:
- Mac OS X 10.4 or above
- Java Development Kit available from http://www.apple.com
- MySQL Database available from http://www.mysql.com (other databases are supported)
- Alfresco available from http://www.alfresco.org

Optional:
- OpenOffice for document transformation available from http://www.openoffice.org


===========================
Simple Installation on OS X
===========================

All these instructions assume knowledge of using OS X from Terminal commands.  You
may need to prefix some of the commands with 'sudo' if you do not have administrative
access.


Install JDK 5.0
---------------

- If you already have J2SE Development Kit 5.0 installed, skip to "Install MySQL"

- Browse to http://www.apple.com/support/downloads/java2se50release1.html
- Install once downloaded
- Ensure the JAVA_HOME variable is set


Install MySQL
-------------

- If you wish to use other databases, please visit the forums and wiki at
  www.alfresco.org for guidelines
- If you already have MySQL 4.1 or higher installed, skip to "Create Database"

- Browse to http://dev.mysql.com/downloads
- Download the approprate MySQL Max for your platform
- Install by following MySQL's installation instructions
  (for ease, we recommend installing the included StartupItem package too)


Install Alfresco JBoss Bundle
-----------------------------

- Browse to http://www.alfresco.org/downloads
- Download the "Alfresco Linux JBoss Bundle" option
- Create a directory in '/opt' named 'alfresco'
- Tar uncompress alfresco-jboss-xxxxxx.tar.gz in the '/opt/alfresco' directory


Set Paths
---------

Edit 'alfresco.sh' and check the variables are to the location where JBoss
is installed:
- For the JBoss bundle, this will be '/opt/alfresco/jboss'


Create Database
---------------

Navigate to the '/opt/alfresco' folder and run the 'db_setup.sql' script in
MySQL: 'mysql -u root -p <db_setup.sql'.  

This creates a MySQL database named 'alfresco' with a user 
account and password of 'alfresco'.

You have now installed all the components needed to run the Alfresco server.


Optional Install of OpenOffice
------------------------------

If you would like to have a range of document transformations available from within
Alfresco, you need to install OpenOffice.  This is entirely optional and can be
done at any point after Alfresco has been installed.

*** There are known issues with OpenOffice 2.0.1 and 2.0.2, we recommend 2.0.0 ***

- Browse to http://download.openoffice.org
- Download the OS X version
- Install OpenOffice
- Start one of the OpenOffice programs to go through initial registration, then close it
- Rename '/opt/alfresco/zstart_oo.sh' to '/opt/alfresco/start_oo.sh'
- Edit '/opt/alfresco/start_oo.sh' and set the correct location for OpenOffice
- Stop and restart the Alfresco server if it is already running


Configure the Database Connection and Data Locations
----------------------------------------------------

Details can be found at http://wiki.alfresco.com/wiki/Repository_Configuration#New_Installations


===========================
Running the Alfresco Server
===========================

Ensure that the MySQL server is running.
Open a new command window and navigate to the '/opt/alfresco' directory
- Run 'alfresco.sh start' to start JBoss
- You can now try Alfresco by visiting:

http://localhost:8080/portal and navigating to 'Alfresco' from the Page Menu and
then maximizing the portlet (top-right-most icon).

Or:

http://localhost:8080/portal/index.html?ctrl:id=window.default.AlfrescoClientWindow&ctrl:type=nav&ctrl:windowstate=maximized

The server is configured with a single administrative login with user name and password
both set to 'admin'.

To test the installation, you may wish to follow the tutorial, available from:

http://www.alfresco.org/downloads or from the company space from within the Web Client.


===========================
Closing the Alfresco Server
===========================

Navigate to the '/opt/alfresco' folder and run 'alfresco.sh stop'.


===========================
Upgrading from V1.x to V1.4
===========================

Not yet available.


=====================
Using the CIFS Server
=====================

Once the Alfresco server is running, it should be possible to connect to it by mapping a
drive to it.  The name to use for the mapping is based on the name of the server on which
Alfresco is running, with '_A' on the end.  For example, if the PC name is 'MYPC01', then 
the CIFS server name will be 'MYPC01_A'.  

To map the drive on a MS Windows client, open Windows Explorer, go
to the Tools menu and select 'Map Network Drive...'.  In the Map Network Drive dialog,
choose the drive letter you wish to use.  To locate the CIFS server, click the 'Browse...' 
button and find the server name as described above.  You should then have the option to
select a folder within it called 'alfresco'.  Click 'OK' to select the folder, then click
'Finish' to map the drive.  You should now have access to the Alfresco repository from
the mapped drive.  If the CIFS server name does not show in the browse dialog, you may also
enter the folder location directly in the dialog, for example '\\MYPC01_A\alfresco'. 

You may mount the repository on a Mac client using a Network connection of the form
smb://MYPC01_A/alfresco'.

To check the CIFS server is running, try connecting from the Alfresco server using smbclient.

If you are unable to connect to the CIFS server, then depending on your network, you may need 
to configure the domain for CIFS to use.  You will need to have started the Alfresco server
at least once to be able to do this.  To set the domain, edit the 'file-servers.xml' 
file in the '/opt/alfresco/tomcat/webapps/alfresco/WEB-INF/classes/alfresco' directory and add the 
domain into the following line:
   <host name="${localname}_A"/>
so that it is something like:
   <host name="${localname}_A" domain="MYDOMAIN"/>

You will need to restart the Alfresco server for this to take effect.


==================
Image Manipulation
==================

To enable image manipulation on a Linux platform, the ImageMagick package needs to be installed.  On
many Linux distributions it is already available.  To check, try running the 'convert' command, which 
is part of ImageMagick and usually found in /usr/bin.  


================
Trouble-Shooting
================

If you have problems with your installation, please look for help on the Installation
forum at http://forums.alfresco.com and ask for any additional help you may need.

- The JAVA_HOME variable must be set correctly to your Java5 installation.

- If the following errors are reported on the console:
  ERROR [AbstractImageMagickContentTransformer] JMagickContentTransformer not available:
  ERROR [AbstractImageMagickContentTransformer] ImageMagickContentTransformer not available:
  Failed to execute command: convert ...

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

