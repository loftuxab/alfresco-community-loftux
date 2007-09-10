============
Alfresco 2.1
============

Welcome to the Alfresco 2.1 Release.

This release uses HSQL as the default database.  Please see README_mysql.txt for details on changing it to use MySQL.  Instructions for other databases are provided on the Alfresco Wiki.

There are options in the Start menu for starting and stopping the Alfresco Server, and for starting the Alfresco Web Client.

For release notes, please visit http://wiki.alfresco.com/wiki/Release_2.1

For information and help, please visit http://forums.alfresco.com

The first time OpenOffice is run, you may need complete the registration and then close OpenOffice.  You must also ensure that the OpenOffice System Tray is set to not automatically load on Windows startup and that it is closed before starting the OpenOffice Service.  To do this, right-click the OpenOffice Quickstarter icon in the System Tray and uncheck 'Load OpenOffice.org during system start-up' and then select 'Exit quickstarter'.


=====================================
Using the CIFS (Shared Folder) Server
=====================================

Once the Alfresco server is running, it should be possible to connect to it by mapping a drive to it.  The name to use for the mapping is based on the name of the server on which Alfresco is running, with '_A' on the end.  For example, if the PC name is 'MYPC01', then the CIFS server name will be 'MYPC01_A'.  To map the drive, open Windows Explorer, go to the Tools menu and select 'Map Network Drive...'.  In the Map Network Drive dialog, choose the drive letter you wish to use.  To locate the CIFS server, click the 'Browse...' button and find the server name as described above.  You should then have the option to select a folder within it called 'alfresco'.  Click 'OK' to select the folder, then click 'Finish' to map the drive.  You should now have access to the Alfresco repository from the mapped drive.  If the CIFS server name does not show in the browse dialog, you may also enter the folder location directly in the dialog, for example '\\MYPC01_A\alfresco'.

To check the CIFS server name and whether it is running, enter the command 'nbtstat -n' from a Command Prompt.  One of the listed names should be the CIFS server name.

If you are unable to connect to the CIFS server, then depending on your network, you may need to configure the domain for CIFS to use.  You will need to have started the Alfresco server at least once to be able to do this.  To set the domain, edit the 'file-servers-custom.xml' file in the 'tomcat\shared\classes\alfresco\extension' directory and add the domain into the following line:
   <host name="${localname}_A"/>
so that it is something like:
   <host name="${localname}_A" domain="MYDOMAIN"/>

You will need to restart the Alfresco server for this to take effect.


==================
Image Manipulation
==================

If you encounter problems with image manipulation on a Windows platform, the 'imconvert.exe' in '<alfresco>\bin' may need to be copied into a folder on the system path, such as 'C:\windows\system32'.


================
Trouble-Shooting
================

If you have problems with your installation, please look for help on the Installation forum at http://forums.alfresco.com and ask for any additional help you may need.

- If the following errors are reported on the console:
  ERROR [AbstractImageMagickContentTransformer] JMagickContentTransformer not available:
  ERROR [AbstractImageMagickContentTransformer] ImageMagickContentTransformer not available:
  Failed to execute command: imconvert ...

These are not issues which will cause the server to fail. Alfresco is reporting that various external document transformation engines are not available for use by the server.   

- If you see this error on server startup:
  ERROR [protocol] FTP Socket error
    java.net.BindException: Address already in use: JVM_Bind
         at java.net.PlainSocketImpl.socketBind(Native Method)
Check to see if you have any services running against port 8080 for the Alfresco server and port 21 for the Alfresco FTP integration.

- To access the CIFS repository directly from the FireFox browser, you need to install the Alfresco FireFox Extension from here:
  http://sourceforge.net/projects/alfresco
  
Internet Explorer does not require the extension to open CIFS folders directly.
