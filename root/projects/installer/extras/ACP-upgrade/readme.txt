1.3 to 1.4 ACP Upgrade
----------------------

The ACP XML format has changed between release 1.3 and 1.4.  There is a utility that can be used
to convert the XML file within an ACP zip file to the 1.4 format.  The 1-3_to_1-4 scripts can
be used to run that utility from the command line.  You may need to edit the script and change
the location of the class library for your installation.  The scripts assume a Tomcat install and
will need changing and the appropriate jars extracted if used for a JBoss install.

To use the script, you first need to extract the XML file from an ACP (just use an unzip utility).
The give this as the input file to the script along with an output file name, e.g.:

  1-3_to_1-4 13acp.xml 14acp.xml

Then copy the original .acp file and within it, overwrite the old XML file with the new one.