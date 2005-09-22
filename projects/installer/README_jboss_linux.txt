========================
Alfresco Preview Release
========================

Welcome to the Alfresco Preview Release.  This is provided as a
snapshot of where we are currently in the development of the Alfresco
system.  It is intended for preview use only and should not be used for
any other purpose.  Not all functionality is available or complete.


===================================
Installing Alfresco Preview Release
===================================

The Alfresco Preview Release is intended for evaluation purposes only.


===================================
Alfresco JBoss Bundled Installation
===================================

Requirements:
- Java Development Kit available from http://java.sun.com
- MySQL Database available from http://www.mysql.com
- Alfresco available from http://www.alfresco.org

Optional:
- OpenOffice for document transformation available from http://www.openoffice.org


============================
Simple Installation on Linux
============================

Install JDK 5.0
---------------

- If you already have J2SE Development Kit 5.0 installed, skip to "Create Database"

- Browse to http://java.sun.com/j2se/1.5.0/download.jsp
- Select the "JDK 5.0 Update x" option
- Download your preferred "Linux Platform" option (~45M)
- Install once downloaded
- Ensure the JAVA_HOME variable is set correctly


Install MySQL
-------------

- If you already have MySQL 4.1 installed, skip to "Install Alfresco JBoss Bundle"

- Browse to http://dev.mysql.com/downloads/mysql/4.1.html
- Download the approprate MySQL Max for your platform
- Install by following MySQL's installation instructions


Install Alfresco JBoss Bundle
-----------------------------

- Browse to http://www.alfresco.org/downloads
- Download the "Alfresco Linux JBoss Bundle" option
- Create a directory named 'alfresco'
- Tar uncompress alfresco-jboss-xxxxxx.tar.gz in the '~/alfresco' directory


Set Paths
---------

Edit 'paths_jb.sh' and set the variables to the locations where MySQL, Java and Tomcat
are installed.


Create Database
---------------

Navigate to the '~/alfresco' folder and run 'db_setup.sh' if you did a new
install of MySQL above.  

This creates a MySQL database named 'alfresco' with a user 
account and password of 'alfresco'.


You have now installed all the components needed to run the Alfresco server.


Optional Install of OpenOffice
------------------------------

If you would like to have a range of document transformations available from within
Alfresco, you need to install OpenOffice 1.1.4.  This is entirely optional and can be
done at any point after Alfresco has been installed.  OpenOffice should be installed
in /opt/OpenOffice.org1.1.4

- Browse to http://download.openoffice.org/1.1.4/index.html
- Download the Windows version
- Install OpenOffice with defaults (except file associations, unless you wish to)
- Start one of the OpenOffice programs to go through initial registration, then close it
- Edit '~/alfresco/start_oo.sh' and set the correct location for OpenOffice
- Stop and restart the Alfresco server if it is already running


===========================
Running the Alfresco Server
===========================

Ensure that the MySQL server is running.
Open a new command window and navigate to the '~/alfresco' directory
- Run 'sudo alf_start_jb.sh' to start JBoss
- If you wish to use OpenOffice document transformations, run 'start_oo.sh'
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

Navigate to the '~/alfresco' directory and run 'alf_stop_jb.sh'
If you started OpenOffice as above, then also run 'stop_oo.sh'


=====================
Using the CIFS Server
=====================

The Preview release with CIFS is configured for ease of deployment.
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

To check the CIFS server is running, try connecting from the Alfresco server using smbclient.

If you are unable to connect to the CIFS server, then depending on your network, you may need 
to configure the domain for CIFS to use.  You will need to have started the Alfresco server
at least once to be able to do this.  To set the domain, edit the 'file-servers.xml' 
file in the '~/alfresco/jboss/server/default/conf/alfresco' directory and add the 
domain into the following line:
   <host name="${localname}_A"/>
so that it is something like:
   <host name="${localname}_A" domain="MYDOMAIN"/>

You will need to restart the Alfresco server for this to take effect.


================
Trouble-Shooting
================

If you have problems with your installation, please look for help on the Installation
forum at http://www.alfresco.org/forums and ask for any additional help you may need.

. The JAVA_HOME variable must be set correctly to your Java5 installation.

. Most installation issues can be resolved by following advice in this forum article:
 - http://www.alfresco.org/forums/viewtopic.php?t=7
  and in this forum generally:
 - http://www.alfresco.org/forums/viewforum.php?f=8

. WAR file name is now called alfresco.war
NOTE: If you deployed the war previously from source (rather than use a standard Alfresco installation package) then you must clear out the web-client stuff from your appservers before deploying the new WAR file:

Tomcat:
- Delete <tomcat-home>/webapps/web-client.war
- Delete <tomcat-home>/webapps/web-client
- Delete <tomcat-home>/work

JBoss:
- Delete <jboss-home>/server/default/deploy/web-client.war

. If the following errors are reported on the console:
ERROR [AbstractImageMagickContentTransformer] JMagickContentTransformer not available:
ERROR [AbstractImageMagickContentTransformer] ImageMagickContentTransformer not available: Failed to execute command: imconvert ...
  These are not issues which will cause the server to fail, Alfresco is reporting the fact that various external document transformation engines are not available for use by the server. Either follow the instructions at the bottom of the Release Notes Wiki page:
 - http://www.alfresco.org/mediawiki/index.php/Preview_Release_5
  or remove the transformer references completely if you don't require them:
 - http://www.alfresco.org/forums/viewtopic.php?t=90

. If you see this error on server startup:
 ERROR [protocol] FTP Socket error
    java.net.BindException: Address already in use: JVM_Bind
         at java.net.PlainSocketImpl.socketBind(Native Method)
 Check to see if you have any services running against port 8080 for the Alfresco server and port 21 for the Alfresco FTP integration.

. To access the CIFS repository directly from the FireFox browser, you need to install the Alfresco FireFox Extension from here:
 - http://sourceforge.net/projects/alfresco
 Internet Explorer does not require the extension to see display CIFS folders directly.
