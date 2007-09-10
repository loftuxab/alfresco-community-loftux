## This script will execute a full build of the Alfresco installer
## which includes the JDK and OpenOffice components.

## Command-line Example:
##
## ./installjammer --build --control-script <this script> <project file>
##

## Change the name of the install executable to reflect the full build.
::BuilderAPI::SetPlatformProperty -platform Windows -property Executable \
    -value "<%AppName%> Full-<%Version%>-Setup<%Ext%>"
::BuilderAPI::SetPlatformProperty -platform Linux-x86 -property Executable \
    -value "<%AppName%> Full-<%Version%>-<%Platform%>-Install"

## Activate the two file groups that are disabled in the default project.
::BuilderAPI::ModifyObject -object "Java File Group" -active Yes
::BuilderAPI::ModifyObject -object "OpenOffice File Group" -active Yes
