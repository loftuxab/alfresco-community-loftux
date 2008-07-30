// Call the repo for the sites profile
var profile =
{
   title: "",
   shortName: "",   
   description: ""
}

var json = remote.call("/api/sites/" + page.url.templateArgs.site);
if (json.status == 200)
{
   // Create javascript object from the repo response
   var obj = eval('(' + json + ')');
   if (obj)
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
      // the REST API doesn't yet filter per role, we have to do it here
      var managers = [];
      for (var x=0; x < obj.length; x++)
      {
         if (obj[x].role == "SiteManager")
         {
            managers.push(obj[x]);
         }
      }
       
      sitemanagers = managers;
   }
}

// Prepare the model
model.profile = profile;
model.sitemanager = sitemanagers[0];