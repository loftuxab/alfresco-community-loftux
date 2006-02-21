======================
Alfresco 1.2.0 Release
======================

Welcome to the Alfresco 1.2.0 Release.

If you installed Alfresco as a service, you will now need to start them.  There is an option to start the services from the Windows Start menu.  To test the installation, use the Alfresco Web Client option from the Start menu.

For release notes, please visit http://www.alfresco.org/mediawiki/index.php/Release_1.2

For information and help, please visit http://www.alfresco.org/forums


=====================================
Using the CIFS (Shared Folder) Server
=====================================

To enable the CIFS server on a Windows platform, the Win32NetBIOS.dll in 'C:\alfresco\bin'
needs to be copied into a folder on the system path, such as 'c:\windows\system32'.  The 
Alfresco server will need to be re-started once the dll has been copied.
Or you could add 'C:\alfresco\bin' to your path.

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
at least once to be able to do this.  To set the domain, edit the 'file-servers.xml' 
file in the 'c:\alfresco\tomcat\webapps\alfresco\WEB-INF\classes\alfresco' directory and add the 
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
Or you could add 'C:\alfresco\bin' to your path.


================
Trouble-Shooting
================

If you have problems with your installation, please look for help on the Installation
forum at http://www.alfresco.org/forums and ask for any additional help you may need.

- The JAVA_HOME variable must be set correctly to your Java5 installation.

- Most installation issues can be resolved by following advice in this forum article:
  http://www.alfresco.org/forums/viewtopic.php?t=7
  and in this forum generally:
  http://www.alfresco.org/forums/viewforum.php?f=8

- WAR file name is called alfresco.war
  NOTE: If you deployed the war previously then you must clear out the web-client files 
  before deploying the new WAR file, having first copied any configurations made:

  Delete <tomcat-home>/webapps/alfresco.war
  Delete <tomcat-home>/webapps/alfresco
  Delete <tomcat-home>/work/alfresco

- If the following errors are reported on the console:
  ERROR [AbstractImageMagickContentTransformer] JMagickContentTransformer not available:
  ERROR [AbstractImageMagickContentTransformer] ImageMagickContentTransformer not available:
  Failed to execute command: imconvert ...

  These are not issues which will cause the server to fail. Alfresco is reporting that 
  various external document transformation engines are not available for use by the server.   
  Either follow the instructions at the bottom of the Release Notes Wiki page:
  http://www.alfresco.org/mediawiki/index.php/Preview_Release_5
  or remove the transformer references completely if you don't require them:
  http://www.alfresco.org/forums/viewtopic.php?t=90

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
