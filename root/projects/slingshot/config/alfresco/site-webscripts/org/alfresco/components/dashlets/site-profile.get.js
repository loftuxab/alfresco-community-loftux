// Call the repo for the sites profile
var profile =
{
   description: "[Not found]"
}

var json = remote.call("/api/sites/" + page.url.templateArgs.site);
if (json.status == 200)
{
   // Create javascript object from the repo response
   var obj = eval('(' + json + ')');
   if (obj && obj.description)
   {
      profile = obj;
   }
}

// Find the manager for the site
var sitemanagers = [{}];

json = remote.call("/api/sites/" + page.url.templateArgs.site + "/memberships?rf=SiteManager");
if (json.status == 200)
{
   var obj = eval('(' + json + ')');
   if (obj)
   {
      sitemanagers = obj;
   }
}

// Prepare the model
model.profile = profile;
model.sitemanager = sitemanagers[0];