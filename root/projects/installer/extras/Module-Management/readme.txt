Module Manager
----------------------

The module manager will allow you to install an Alfresco AMP file into a WAR file.

Module managment tool available commands:
    
install: Installs a AMP file into an Alfresco WAR file, updates if an older version is already installed.
usage:   ModuleManager install <AMPFile> <WARFile> <options>
valid options: 
   -verbose  : enable verbose output
   -force    : forces installation of AMP regardless of currently installed module version
   -preview  : previews installation of AMP without modifying WAR file
   -nobackup : indicates that no backup should be made of the WAR

list:  Lists all the modules currently installed in an Alfresco WAR file.
usage: ModuleManager list <WARFile>