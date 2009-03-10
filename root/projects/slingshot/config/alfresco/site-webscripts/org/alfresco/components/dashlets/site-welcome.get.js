model.userIsSiteManager = false;

// Call the repository to see if the user is site manager or not
var json = remote.call("/api/sites/" + page.url.templateArgs.site + "/memberships/" + stringUtils.urlEncode(user.name));
if (json.status == 200)
{
   var obj = eval('(' + json + ')');
   model.userIsSiteManager = (obj.role == "SiteManager");
}