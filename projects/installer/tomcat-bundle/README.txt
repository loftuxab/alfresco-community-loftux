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
- Java Development Kit Environment available from http://java.sun.com
- MySQL Database available from http://www.mysql.com
- alfrescoTC.zip available from http://www.alfresco.org


=================================
Simple Installation on Windows XP
=================================


Install JSEE SDK
----------------

- Browse to http://java.sun.com/j2se/1.5.0/download.jsp
- Select the "JDK 5.0 Update 3" option
- Download the Windows Offline Installation" option (~55M)
- Install once downloaded


Install MySQL
-------------

- Browse to http://dev.mysql.com/downloads/mysql/4.1.html
- Download the Windows "without installer" option (~37M)
- Unzip the download in C:\

This will create a folder 'C:\mysql-4.xxxxx'.  Rename this folder to:
C:\mysql

If you already have a MySQL installation, you will either have to uninstall
it first, or make any changes as implied by the Manual Installation notes below.


Install Alfresco Tomcat Bundle
------------------------------

- Browse to http://www.alfresco.org/downloads
- Download the "Alfresco Tomcat Bundle" option
- Unzip alfrescoTC.zip in C:\

This will create a folder 'C:\alfresco'

Navigate to the 'C:\alfresco' folder and run 'db_setup.bat' if you did a new
install of MySQL above.  This creates a MySQL database named 'alfresco' with a user 
account and password of 'alfresco'.

You have now installed all the components needed to run the Alfresco server.


===========================
Running the Alfresco Server
===========================

Navigate to the 'C:\alfresco' folder and run 'startup.bat'
- a console window will open for Tomcat
- when the console has the message 'INFO: Server startup in nnnnn ms', Tomcat is running
- you can now try Alfresco by visiting:

http://localhost:8080/web-client/faces/jsp/login.jsp

The server is configured with a single administrative login with user name and password
both set to 'admin'.

To test the installation, you may wish to follow the tutorial, available from:

http://www.alfresco.org/tutorial


===========================
Closing the Alfresco Server
===========================

Navigate to the 'C:\alfresco' folder and run 'shutdown.bat'



====================
Manual Installations
====================

For other operating systems or where MySQL or Tomcat are already installed,
you may need to adjust the instructions above as appropriate, such as changing
the Tomcat port settings.

The Alfresco server is packaged as a war file and can be found in:
c:\alfresco\tomcat\webapps\web-client.war

The Alfresco 'db_setup.bat' performs the following MySQL commands:

c:\mysql\bin\mqslqadmin -u root -p create alfresco
c:\mysql\bin\mysql -u root -e "grant all on alfresco.* to 'alfresco'@'localhost'
                   identified by 'alfresco' with grant option;"

The Alfresco 'startup.bat' starts the database and runs Tomcat's 'startup.bat'.
The 'shutdown.bat' runs Tomcat's 'shutdown.bat' and shutsdown the database.
