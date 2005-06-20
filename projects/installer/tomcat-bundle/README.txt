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

Currently only tested on Windows XP Pro


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

This will create a folder 'C:\mysql'.


Install Alfresco Tomcat Bundle
------------------------------

- Browse to http://www.alfresco.org/downloads
- Download the "Alfresco Tomcat Bundle" option
- Unzip alfrescoTC.zip in C:\

This will create a folder 'C:\alfresco'

Navigate to the 'C:\alfresco' folder and run 'install.bat'
- this creates a MySQL database named 'alfresco'

You have now installed all the components needed to run the Alfresco server.


===========================
Running the Alfresco Server
===========================

Navigate to the 'C:\alfresco' folder and run 'startup.bat'
- a console window will open for Tomcat
- when the console has the message 'INFO: Server startup in nnnnn ms', Tomcat is running
- you can now try Alfresco by visiting:

http://localhost:8080/web-client/faces/jsp/browse/browse.jsp

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
you will need to adjust the instructions above as appropriate, such as changing
the Tomcat port settings.

The Alfresco 'install.bat' performs the following MySQL command:

c:\mysql\bin\mqslqadmin -u root -p create alfresco

The Alfresco 'startup.bat' runs Tomcat's 'startup.bat', likewise for the
'shutdown.bat'.
