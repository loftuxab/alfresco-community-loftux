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


=================================
Simple Installation on Windows XP
=================================


Install JDK 5.0
---------------

- If you already have J2SE Development Kit 5.0 installed, skip to "Install MySQL"

- Browse to http://java.sun.com/j2se/1.5.0/download.jsp
- Select the "JDK 5.0 Update x" option
- Download the Windows Offline Installation" option (~55M)
- Install once downloaded
- Ensure the JAVA_HOME variable is set correctly


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
- Download the "Alfresco Windows Tomcat Bundle" option
- Create a folder 'C:\alfresco'
- Unzip alfresco-tomcat-xxxxx.zip in C:\alfresco

Navigate to the 'C:\alfresco' folder and run 'db_setup.bat' if you did a new
install of MySQL above.  This creates a MySQL database named 'alfresco' with a user 
account and password of 'alfresco'.

You have now installed all the components needed to run the Alfresco server.


Optional Install of OpenOffice
------------------------------

If you would like to have a range of document transformations available from within
Alfresco, you need to install OpenOffice 1.1.4.  This is entirely optional and can be
done at any point after Alfresco has been installed.  OpenOffice should be installed
in C:\Program Files\OpenOffice.org1.1.4

- Browse to http://download.openoffice.org/1.1.4/index.html
- Download the Windows version
- Install OpenOffice with defaults (except file associations, unless you wish to)
- Start one of the OpenOffice programs to go through initial registration, then close it
- Rename the 'zstart_oo.bat' in 'C:\alfresco' to 'start_oo.bat'
- Stop and restart the Alfresco server if it is already running


===========================
Running the Alfresco Server
===========================

Navigate to the 'C:\alfresco' folder and run 'alf_start_tc.bat'
- two minimized windows will be started for MySQL and OpenOffice
- a console window will open for Tomcat
- when the console has the message 'INFO: Server startup in nnnnn ms', Tomcat is running
- you can now try Alfresco by visiting:

http://localhost:8080/alfresco/faces/jsp/login.jsp

The server is configured with a single administrative login with user name and password
both set to 'admin'.

To test the installation, you may wish to follow the tutorial, available from:

http://www.alfresco.org/downloads or from the company space from within the Web Client.


===========================
Closing the Alfresco Server
===========================

Navigate to the 'C:\alfresco' folder and run 'alf_stop_tc.bat'


=====================
Using the CIFS Server
=====================

The Preview release with CIFS is configured for ease of deployment.  To enable the CIFS
server on a Windows platform, the Win32NetBIOS.dll in 'C:\alfresco\bin' needs to be copied
into a folder on the system path, such as \windows\system32.  The Alfresco server will 
need to be re-started once the dll has been copied.

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


====================
Manual Installations
====================

For other operating systems or where MySQL or Tomcat are already installed,
you may need to adjust the instructions above as appropriate, such as changing
the Tomcat port settings.

The Alfresco server is packaged as a war file and can be found in:
c:\alfresco\tomcat\webapps\alfresco.war

The Alfresco 'db_setup.bat' performs the following MySQL commands:

c:\mysql\bin\mqslqadmin -u root -p create alfresco
c:\mysql\bin\mysql -u root -e "grant all on alfresco.* to 'alfresco'@'localhost'
                   identified by 'alfresco' with grant option;"

The Alfresco 'alf_start_tc.bat' starts the database and runs Tomcat's 'run.bat'.
The 'alf_stop_tc.bat' runs Tomcat's 'shutdown.bat' and shutsdown the database.


================
Trouble-Shooting
================

If you have problems with your installation, please look for help on the Installation
forum at http://www.alfresco.org/forums and ask for any additional help you may need.

