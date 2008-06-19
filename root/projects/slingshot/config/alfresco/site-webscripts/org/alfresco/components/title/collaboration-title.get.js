// Call the repository for the site profile
var json = remote.call("/api/sites/" + page.url.args.site);

var profile =
{
   title: "[Not Found]"
};

if (json.status == 200)
{
   // Create javascript objects from the repo response
   var obj = eval('(' + json + ')');
   if (obj && obj.title)
   {
      profile = obj;
   }
}

// Prepare the model
model.profile = profile;