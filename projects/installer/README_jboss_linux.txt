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

- If you already have J2SE Development Kit 5.0 installed, skip to "Install MySQL"

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

Then create a database schema named 'alfresco', e.g.:
   mysqladmin -u root create alfresco

Then create a new user with full rights on this database, e.g.:
   mysql -u root -e "grant all on alfresco.* to 'alfresco'@'localhost' identified by 'alfresco' with grant option;"

To check that this is working correctly, start MySQL and connect to the database:
   mysql -u alfresco -p
   mysql> use alfresco;
   Database changed
   mysql> quit

Install Alfresco JBoss Bundle
-----------------------------

- Browse to http://www.alfresco.org/downloads
- Download the "Alfresco Linux JBoss Bundle" option
- Create a directory named 'alfresco'
- Tar uncompress alfresco-jboss-xxxxxx.tar.gz in the '~/alfresco' directory

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
- Rename the 'zstart_oo.sh' in '~/alfresco' to 'start_oo.sh'
- Stop and restart the Alfresco server if it is already running


===========================
Running the Alfresco Server
===========================

Navigate to the '~/alfresco' directory
- Run 'jboss/bin/run.sh' to start JBoss
- Open a new command window
- If you wish to use OpenOffice document transformations, run 'start_oo.sh'
- You can now try Alfresco by visiting:

http://localhost:8080/portal and navigating to 'web-client' from the Page Menu and
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

Navigate to the '~/alfresco' directory and run 'jboss/bin/shutdown.sh -S'
If you started OpenOffice as above, then also run 'killall soffice.bin'


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
to configure the domain for CIFS to use.  To set this, edit the 'file-servers.xml' file in the 
'~/alfresco' directory and add the domain into the following line:
   <host name="${localname}_A"/>
so that it is something like:
   <host name="${localname}_A" domain="MYDOMAIN"/>

You then need to place this file in the org/alfresco/filesys directory within the
repository.jar file, itself within the web-client.war file.  This should only be attempted
if you are familiar with these things.

You will need to restart the Alfresco server for this to take effect.


================
Trouble-Shooting
================

If you have problems with your installation, please look for help on the Installation
forum at http://www.alfresco.org/forums and ask for any additional help you may need.

