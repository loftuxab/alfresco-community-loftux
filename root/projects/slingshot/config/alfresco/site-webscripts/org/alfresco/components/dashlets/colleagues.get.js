// Call the repo for the site memberships
var json = remote.call("/api/sites/" + page.url.args.site + "/memberships");

var memberships = [];

if (json.status == 200)
{
   // Create javascript objects from the repo response
   var obj = eval('(' + json + ')');
   if (obj)
   {
      memberships = obj;
   }
}

// Prepare the model
model.memberships = memberships;
