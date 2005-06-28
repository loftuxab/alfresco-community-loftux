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

====================================
Alfresco Tomcat Bundled Installation
====================================

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

- Browse to http://dev.mysql.com/downloads/mysql/4.1.html
- From "Linux downloads", choose your platform "Max" option (~29M)
- Tar uncompress the download in your home directory

This will create a folder '~/mysql-4.xxxxx'.  Rename this folder to:
mysql

If you already have a MySQL installation, you will either have to uninstall
it first, or make any changes as implied by the Manual Installation notes below.


Install Alfresco Tomcat Bundle
------------------------------

- Browse to http://www.alfresco.org/downloads
- Download the "Alfresco Linux Tomcat Bundle" option
- Unzip alfresco-tc.tar.gz in your home directory

This will create a folder '~/alfresco'

Navigate to the '~/alfresco' folder and run 'db_setup.sh' if you did a new
install of MySQL above.  This creates a MySQL database named 'alfresco' with a user 
account and password of 'alfresco'.

You have now installed all the components needed to run the Alfresco server.


Optional Install of OpenOffice
------------------------------

If you would like to have a range of document transformations available from within
Alfresco, you need to install OpenOffice 1.1.4.  This is entirely optional and can be
done at any point after Alfresco has been installed.  OpenOffice should be installed
in /opt/OpenOffice.org1.1.4

- Browse to http://download.openoffice.org/1.1.4/index.html
- Download the Linux version
- Install OpenOffice into your home directory, i.e. ~/OpenOffice.org1.1.4
- Start one of the OpenOffice programs to go through initial registration, then close it
- Rename the 'zstart_oo.sh' in '~/alfresco' to 'start_oo.sh'
- Stop and restart the Alfresco server if it is already running


===========================
Running the Alfresco Server
===========================

Navigate to the '~/alfresco' folder and run 'alf_start_tc.sh'
- two processes will be started for MySQL and OpenOffice
- a console window will open for Tomcat
- when the console has the message 'INFO: Server startup in nnnnn ms', Tomcat is running
- you can now try Alfresco by visiting:

http://localhost:8080/web-client/faces/jsp/login.jsp

The server is configured with a single administrative login with user name and password
both set to 'admin'.

To test the installation, you may wish to follow the tutorial, available from:

http://www.alfresco.org/downloads or from the company space from within the Web Client.


===========================
Closing the Alfresco Server
===========================

Navigate to the '~/alfresco' folder and run 'alf_stop_tc.sh'



====================
Manual Installations
====================

For other operating systems or where MySQL or Tomcat are already installed,
you may need to adjust the instructions above as appropriate, such as changing
the Tomcat port settings.

The Alfresco server is packaged as a war file and can be found in:
~/alfresco/tomcat/webapps/web-client.war

The Alfresco 'db_setup.sh' performs the following MySQL commands:

mqslqadmin -u root -p create alfresco
mysql -u root -e "grant all on alfresco.* to 'alfresco'@'localhost'
                   identified by 'alfresco' with grant option;"

The Alfresco 'alf_start_tc.sh' starts the database and runs Tomcat's 'run.sh'.
The 'alf_stop_tc.sh' runs Tomcat's 'shutdown.sh' and shutsdown the database.
