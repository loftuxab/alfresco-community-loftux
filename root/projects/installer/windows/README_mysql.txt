Optional Install of MySQL
-------------------------

Alfresco is pre-configured to use the HSQL database, but can easily be
configured to use other databases to use other databases, please visit
the forums and wiki at www.alfresco.com for guidelines.

Alfresco requires MySQL 4.1 or higher.

- Browse to http://dev.mysql.com/downloads
- Download the appropriate option (22-50M)
- Install once downloaded
- Use Typical setup type
- You may skip the MySQL.com sign-up
- Configure using options appropriate to required use
  - generally, choose default selected options
  - choose UTF8 Character set
  - install as a Windows service if appropriate
  - include MySQL in path
- Test MySQL is installed and running by opening a command prompt and entering:
  'mysql -u root -p'
- When prompted, give the password you set during installation.  If no errors
  are reported, then it is installed and running.  Enter 'quit' to exit.


MySQL Database Connector
------------------------

You may need to add or update the MySQL Java Connector library (required for WAR bundle):
- Download the MySQL Java Database Connector http://dev.mysql.com/downloads/connector/j/
- Copy the JAR file into <container>/common/lib or <container>/server/default/lib


Remove Existing HSQL Database
-----------------------------

If you have already started the Alfresco server once, then it will have created some
HSQL database files and content.  You need to remove these for Alfresco to start using
a new database.  The safest option is to rename the 'alf_data' folder to something like
'alf_data_hsql'.   As an alternative, you can change the location of the content store
(for example using the Alfresco configuration tool).


Create Database
---------------

Navigate to the '<alfresco>/extras/databases/mysql' folder.  

Setup the Alfresco database and user manually by loading the 'db_setup.sql' file
into MySQL, for example, by running the command:
   mysql -u root -p <db_setup.sql

This creates a MySQL database named 'alfresco' with a user account and password
of 'alfresco'.  If this fails, it may be because the MySQL service is not
running or that the mysql command cannot be found. 


Configure Alfresco
------------------

To convert the default installation to MySQL, you simple need to modify 2 files
from the tomcat/shared/classes/alfresco/extension directory that are set to use
HSQL.  Those files are:

- custom-repository.properties
     - comment out the HSQL connection lines (using #)
     - uncomment the MySQL connection lines and adjust as appropriate
- custom-hibernate-dialect.properties
     - comment out the HSQL line using #
     - uncomment the MySQL line

