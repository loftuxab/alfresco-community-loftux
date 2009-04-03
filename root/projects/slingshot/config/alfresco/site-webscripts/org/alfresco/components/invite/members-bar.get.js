model.isManager = false;

// Check the role of the user - only SiteManagers are allowed to invite people/view invites
var json = remote.call("/api/sites/" + page.url.templateArgs.site + "/memberships/" + stringUtils.urlEncode(user.name));
if (json.status == 200)
{
   var obj = eval('(' + json + ')');
   model.isManager = (obj.role == "SiteManager");
}

context.properties["isManager"] = model.isManager;