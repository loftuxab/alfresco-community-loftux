====================
Alfresco 2.1 Release
====================

Welcome to the Alfresco 2.1  Release.

===================
Installing Alfresco
===================

The following is a guide to installing Alfresco.


=========================
Alfresco WAR Installation
=========================

Requirements:
- Java Development Kit available from http://java.sun.com
- Alfresco available from http://www.alfresco.com

Optional:
- MySQL Database available from http://www.mysql.com (other databases are supported)
- OpenOffice for document transformation available from http://www.openoffice.org


===================
Simple Installation
===================

Install JDK 5.0/6.0
-------------------

- If you already have J2SE Development Kit 5.0 or 6.0 installed, skip to "Install Alfresco WAR Bundle"

- Browse to http://java.sun.com/javase/downloads
- Select the "JDK 6uN" option
- Download the appropriate option (~55M)
- Install once downloaded


Install Alfresco WAR Bundle
---------------------------

- Browse to http://www.alfresco.com/downloads
- Download the "Alfresco WAR Bundle" option
- Unzip alfresco-war-xxxxx.zip to a new directory or folder
- Copy the alfresco.war file to the appropriate location for your application server
- If you have deployed previous versions of Alfresco, you may wish to remove any
  temporary files you application server has created.  


Add Additional Required Libraries
---------------------------------

Some additional libraries are required to be added to the container's endorsed JAR
files.  Currently, this is only required for WCM functionality, but may become necessary
more generally for Alfresco in future releases.

- Copy the JAR files from <alfresco>/endorsed into <container common>/endorsed
  (e.g. tomcat/common/endorsed)


Extensions Configuration
------------------------

Alfresco can be customized in a number of ways.  Some samples and default configurations
are provided in the extensions directory.  These should be copied into the container's
shared classes location.  These configurations also set the database to be HSQL and will 
use the alf_data directory provided.  You will need to change the path to the HSQL data
directory in the custom-repository.properties file.  Alternatively, for a MySQL based
installation, follow instructions in the README_mysql.txt file.


Configure the Database Connection and Data Locations
----------------------------------------------------

Details can be found at http://wiki.alfresco.com/wiki/Repository_Configuration#New_Installations


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
Upgrading from V1.x to V2.1
===========================

Not yet available.


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


================
Trouble-Shooting
================

If you have problems with your installation, please look for help on the Installation
forum at http://forums.alfresco.com and ask for any additional help you may need.

- The JAVA_HOME variable must be set correctly to your Java installation.

- If the following errors are reported on the console:
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
