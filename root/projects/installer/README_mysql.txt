Optional Install of MySQL
-------------------------

Alfresco is pre-configured to use the HSQL database, but can easily be
configured to use other databases to use other databases, please visit
the forums and wiki at www.alfresco.com for guidelines.

Alfresco requires MySQL 4.1 or higher.

- Browse to http://dev.mysql.com/downloads
- Download the "Windows (x86)" option (~37M)
- Install once downloaded (run setup.exe)
- Use Typical setup type
- You may skip the MySQL.com sign-up
- Configure using options appropriate to required use
  - for demo, choose default selected options
  - for non-English or non-West European languages, choose UTF8 Character set
  - install as a Windows service
  - include MySQL in path
- Test MySQL is installed and running by opening a command prompt and entering:
  'mysql -u root -p'
- When prompted, give the password you set during installation.  If no errors
  are reported, then it is installed and running.  Enter 'quit' to exit.


Create Database
---------------

Navigate to the 'C:\alfresco\extras\databases\mysql' folder and run 'db_setup.bat'.  

This creates a MySQL database named 'alfresco' with a user account and password
of 'alfresco'.  If db_setup fails, this may be because the MySQL service is not
running or that the mysql command cannot be found.  Either correct this or setup 
the Alfresco database and user manually by loading the 'db_setup.sql' file into
MySQL, for example, 'mysql -u root -p <db_setup.sql'.


Configure Alfresco
------------------

To convert the default installation to MySQL, you simple need to remove 3 files
from the tomcat/shared/classes/alfresco/extension directory that are set to use
HSQL.  Those files are:

- custom-db-and-data-context.xml
- custom-db-connection.properties
- custom-hibernate-dialect.properties

