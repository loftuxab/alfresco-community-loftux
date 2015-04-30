// THOR-776

var idxEveryone = model.groupNames.indexOf("EVERYONE");
if (idxEveryone != -1)
{
   model.groupNames.splice(idxEveryone, 1);
   model.permissionGroups.splice(idxEveryone, 1);
}

var idxNone = model.siteRoles.indexOf("None");
if (idxNone != -1)
{
   model.siteRoles.splice(idxNone, 1);
}
