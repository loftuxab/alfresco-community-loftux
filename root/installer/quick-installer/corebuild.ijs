## This script will execute a full build of the Alfresco installer
## which includes the JDK and OpenOffice components.

## Command-line Example:
##
## ./installjammer --build --control-script <this script> <project file>
##

## Activate the OpenOffice location dialog that is disabled in the default project.
::BuilderAPI::ModifyObject -object "OpenOffice File" -active No
::BuilderAPI::ModifyObject -object "OpenOffice" -active No
::BuilderAPI::ModifyObject -object "Java File" -active No
::BuilderAPI::ModifyObject -object "Java" -active No

if {[info exists ::info(Codeline)] && $::info(Codeline) eq
"Community"} {
     source community.ijs
}
