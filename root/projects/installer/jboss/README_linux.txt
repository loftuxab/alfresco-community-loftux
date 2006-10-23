============
Alfresco 1.4
============

Welcome to the Alfresco 1.4 Release.  


===================
Installing Alfresco
===================

The following is a guide to installing Alfresco.


===================================
Alfresco JBoss Bundled Installation
===================================

Requirements:
- Java Development Kit available from http://java.sun.com
- Alfresco available from http://www.alfresco.com

Optional:
- MySQL Database available from http://www.mysql.com (or other database)
- OpenOffice for document transformation available from http://www.openoffice.org


============================
Simple Installation on Linux
============================

All these instructions assume knowledge of using Linux from Terminal commands.  You
may need to prefix some of the commands with 'sudo' if you do not have administrative
access.


Install JDK 5.0
---------------

- If you already have J2SE Development Kit 5.0 installed, skip to "Install Alfresco JBoss Bundle"

- Browse to http://java.sun.com/j2se/1.5.0/download.jsp
- Select the "JDK 5.0 Update x" option
- Download your preferred "Linux Platform" option (~45M)
- Install once downloaded
- Ensure the JAVA_HOME variable is set


Install Alfresco JBoss Bundle
-----------------------------

- Browse to http://www.alfresco.com/downloads
- Download the "Alfresco Linux JBoss Bundle" option
- Create a directory in '/opt' named 'alfresco'
- Tar uncompress alfresco-jboss-xxxxxx.tar.gz in the '/opt/alfresco' directory


Set Paths
---------

Edit 'alfresco.sh' and check the variables are pointing to the location where JBoss
is installed:
- For the JBoss bundle, this will be '/opt/alfresco/jboss'


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

*** There are known issues with OpenOffice 2.0.1 and 2.0.2, we recommend 2.0.0 ***

- Browse to http://download.openoffice.org
- Download the Linux version
- Install OpenOffice
- Start one of the OpenOffice programs to go through initial registration, then close it
- Rename '/opt/alfresco/zstart_oo.sh' to '/opt/alfresco/start_oo.sh'
- Edit '/opt/alfresco/start_oo.sh' and set the correct location for OpenOffice
- Stop and restart the Alfresco server if it is already running


===========================
Running the Alfresco Server
===========================

Open a new command window and navigate to the '/opt/alfresco' directory
- Run 'alfresco.sh start'
- You can now try Alfresco by visiting:

http://localhost:8080/portal and navigating to 'Alfresco' from the Page Menu and
then maximizing the portlet (top-right-most icon).

Or:

http://localhost:8080/portal/index.html?ctrl:id=window.default.AlfrescoClientWindow&ctrl:type=nav&ctrl:windowstate=maximized

The server is configured with a single administrative login with user name and password
both set to 'admin'.

To test the installation, you may wish to follow the tutorial, available from:

http://www.alfresco.com/downloads or from the company space from within the Web Client.


===========================
Closing the Alfresco Server
===========================

Navigate to the '/opt/alfresco' directory and run 'alfresco.sh stop'


===========================
Upgrading from V1.x to V1.4
===========================

http://wiki.alfresco.com/wiki/Upgrading_to_1.4.0


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

To check the CIFS server is running, try connecting from the Alfresco server using smbclient.

If you are unable to connect to the CIFS server, then depending on your network, you may need 
to configure the domain for CIFS to use.  You will need to have started the Alfresco server
at least once to be able to do this.  To set the domain, edit the 'file-servers-custom.xml' 
file in the '<alfresco>/jboss/server/default/conf/alfresco/extension' directory and add the 
domain into the following line:
   <host name="${localname}_A"/>
so that it is something like:
   <host name="${localname}_A" domain="MYDOMAIN"/>

You will need to restart the Alfresco server for this to take effect.


==================
Image Manipulation
==================

To enable image manipulation on a Linux platform, the ImageMagick package needs to be installed.  On
many Linux distributions it is already available.  To check, try running the 'convert' command, which 
is part of ImageMagick and usually found in /usr/bin.


================
Trouble-Shooting
================

If you have problems with your installation, please look for help on the Installation
forum at http://forums.alfresco.com and ask for any additional help you may need.

- The JAVA_HOME variable must be set correctly to your Java5 installation.

- If the following errors are reported on the console:
  ERROR [AbstractImageMagickContentTransformer] JMagickContentTransformer not available:
  ERROR [AbstractImageMagickContentTransformer] ImageMagickContentTransformer not available:
  Failed to execute command: convert ...

  These are not issues which will cause the server to fail. Alfresco is reporting that 
  various external document transformation engines are not available for use by the server.   
  We have also noted cases on some distributions and configurations of Linux
  where convert is configured correctly but you recieve the 'Failed to execute command: convert ...'
  error. This can be solved in some instances by editing the 'content-services-context.xml' 
  and removing the quotes around the convert command:
  <value>convert '${source}' ${options} '${target}'</value>
  to:
  <value>convert ${source} ${options} ${target}</value>

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

