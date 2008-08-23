var json = remote.call("/api/sites/" + args.site + "/roles");
var roles = eval('(' + json + ')');

var groupNames = [];
var permissionGroups = roles.permissionGroups;
for (group in permissionGroups)
{
   groupNames.push(permissionGroups[group].substring(permissionGroups[group].lastIndexOf("_") + 1));
}

model.siteRoles = roles.siteRoles;
model.permissionGroups = permissionGroups;
model.groupNames = groupNames;
