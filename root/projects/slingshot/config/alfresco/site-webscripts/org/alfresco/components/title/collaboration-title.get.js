// Call the repository for the site profile
var json = remote.call("/api/sites/" + page.url.templateArgs.site);

var profile =
{
   title: "",
   shortName: ""
};

if (json.status == 200)
{
   // Create javascript objects from the repo response
   var obj = eval('(' + json + ')');
   if (obj)
   {
      profile = obj;
   }
}

// Call the repository to see if the user is site manager or not
var userIsSiteManager = false;
json = remote.call("/api/sites/" + page.url.templateArgs.site + "/memberships/" + user.name);
if (json.status == 200)
{
   var obj = eval('(' + json + ')');
   userIsSiteManager = obj.role == "SiteManager";
}

// Prepare the model
model.profile = profile;
model.userIsSiteManager = userIsSiteManager;

