model.userIsSiteManager = false;

// Check the role of the user - looking for SiteManagers
var obj = context.properties["memberships"];
if (!obj)
{
   var json = remote.call("/api/sites/" + page.url.templateArgs.site + "/memberships/" + stringUtils.urlEncode(user.name));
   if (json.status == 200)
   {
      obj = eval('(' + json + ')');
   }
}
if (obj)
{
   model.userIsSiteManager = (obj.role == "SiteManager");
}