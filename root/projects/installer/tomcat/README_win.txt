============================
Alfresco 2.0 Preview Release
============================

Welcome to the Alfresco 2.0 Preview Release.  This software is for evaluation 
purposes only and cannot be used against an existing Alfresco repository.  There
will not be an upgrade path from this Preview Release to the final 2.0 release.


===================
Installing Alfresco
===================

The following is a guide to installing Alfresco.


====================================
Alfresco Tomcat Bundled Installation
====================================

Requirements:
- Java Development Kit available from http://java.sun.com
- Alfresco available from http://www.alfresco.org

Optional:
- MySQL Database available from http://www.mysql.com (or other database)
- OpenOffice for document transformation available from http://www.openoffice.org


=================================
Simple Installation on Windows XP
=================================

Install JDK 5.0
---------------

- If you already have J2SE Development Kit 5.0 installed, skip to "Install Alfresco Tomcat Bundle"

- Browse to http://java.sun.com/j2se/1.5.0/download.jsp
- Select the "JDK 5.0 Update x" option
- Download the "Windows Offline Installation" option (~55M)
- Install once downloaded


Install Alfresco Tomcat Bundle
------------------------------

- Browse to http://www.alfresco.org/downloads
- Download the "Alfresco Windows Tomcat Bundle" option
- Create a folder 'C:\alfresco'
- Unzip alfresco-tomcat-xxxxx.zip in C:\alfresco


Optional Install of Database
----------------------------

Alfresco is pre-configured to use the HSQL database, but can easily be
configured to use other databases, please visit the forums and wiki at
www.alfresco.com for guidelines

- See 'README-mysql.txt' for details on configuring for use with MySQL.

General configuration details can be found at:
http://wiki.alfresco.com/wiki/Repository_Configuration#New_Installations


You have now installed all the components needed to run the Alfresco server.


Optional Install of OpenOffice
------------------------------

If you would like to have a range of document transformations available from within
Alfresco, you need to install OpenOffice.  This is entirely optional and can be
done at any point after Alfresco has been installed.  

*** There are known issues with OpenOffice 2.0.1 and 2.0.2 ***

- Browse to http://download.openoffice.org
- Download the Windows version
- Install OpenOffice with defaults (except file associations, unless you wish to)
- Start one of the OpenOffice programs to go through initial registration, then close it
- Stop and restart the Alfresco server if it is already running


===========================
Running the Alfresco Server
===========================

Navigate to the 'C:\alfresco' folder and run 'alf_start.bat'
- a minimized window will be started for OpenOffice
- a console window will open for Tomcat
- when the console has the message 'INFO: Server startup in nnnnn ms', Tomcat is running
- if you plan to use WCM preview features, run 'virtual_start.bat'
- you can now try Alfresco by visiting:

http://localhost:8080/alfresco

The server is configured with a single administrative login with user name and password
both set to 'admin'.

To test the installation, you may wish to follow the tutorial, available from:

http://www.alfresco.com/downloads or from the company space from within the Web Client.


===========================
Closing the Alfresco Server
===========================

Navigate to the 'C:\alfresco' folder and run 'alf_stop.bat', also run 'virtual_stop.bat'
if you started the Virtualization server.


===========================
Upgrading from V1.x to V2.0
===========================

Not yet available.


=====================
Using the CIFS Server
=====================

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
file in the 'c:\alfresco\tomcat\shared\classes\alfresco\extension' directory and add the 
domain into the following line:
   <host name="${localname}_A"/>
so that it is something like:
   <host name="${localname}_A" domain="MYDOMAIN"/>

You will need to restart the Alfresco server for this to take effect.


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
