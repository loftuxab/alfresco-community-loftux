Module Manager
----------------------

The module manager will allow you to install an Alfresco AMP file into a WAR file.
The tool is distributed as an executable jar.  Run it with "java -jar alfresco-mmt-xxx.jar".

Module managment tool available commands:
    
install: Installs a AMP file(s) into an Alfresco WAR file, updates if an older version is already installed.
usage:   ModuleManager install <AMPFile(s)Location> <WARFileLocation> [options]
valid options: 
   -verbose   : enable verbose output
   -directory : indicates that the amp file location specified is a directory.
        	All amp files found in the directory and its sub directories are installed.
   -force     : forces installation of AMP regardless of currently installed module version
   -preview   : previews installation of AMP without modifying WAR file
   -nobackup  : indicates that no backup should be made of the WAR

list:  Lists all the modules currently installed in an Alfresco WAR file.
usage: ModuleManager list <WARFile>