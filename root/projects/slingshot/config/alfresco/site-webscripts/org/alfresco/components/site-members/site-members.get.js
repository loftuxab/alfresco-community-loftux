var siteId = page.url.templateArgs.site;

// get the membership info for the current user in the current site
var url = "/api/sites/" + siteId + "/memberships/" + user.id;
var json = remote.call(url);
var membership = eval('(' + json + ')');

// add the role to the model
model.currentUserRole = membership.role;

// get the roles available in the current site
url = "/api/sites/" + siteId + "/roles";
json = remote.call(url);
var data = eval('(' + json + ')');

// add all roles except "None"
model.siteRoles = [];
for (var x=0; x < data.siteRoles.length; x++)
{
   if (data.siteRoles[x] != "None")
   {
      model.siteRoles.push(data.siteRoles[x]);
   }
}
