
// Call the repository to see if the user is site manager or not
var userIsSiteManager = false;
json = remote.call("/api/sites/" + page.url.templateArgs.site + "/memberships/" + user.name);
if (json.status == 200)
{
   var obj = eval('(' + json + ')');
   userIsSiteManager = obj.role == "SiteManager";
}

// Prepare the model
model.userIsSiteManager = userIsSiteManager;
