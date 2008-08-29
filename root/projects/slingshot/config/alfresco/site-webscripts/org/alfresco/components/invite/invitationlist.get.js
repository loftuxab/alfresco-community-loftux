var siteId = page.url.templateArgs.site;

// get the roles available for the given site
var url = "/api/sites/" + siteId + "/roles";
var json = remote.call(url);
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
