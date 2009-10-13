## This script will execute a full build of the Alfresco installer
## which includes the JDK and OpenOffice components.

## Command-line Example:
##
## ./installjammer --build --control-script <this script> <project file>
##

## Activate the two file groups that are disabled in the default project.
::BuilderAPI::ModifyObject -object "Java File" -active Yes
::BuilderAPI::ModifyObject -object "OpenOffice File" -active Yes
::BuilderAPI::ModifyObject -object "Java" -active Yes
::BuilderAPI::ModifyObject -object "OpenOffice" -active Yes

if {[info exists ::info(Codeline)] && $::info(Codeline) eq
"Community"} {
     source community.ijs
}
