// Call the repo for the site memberships
var json = remote.call("/api/sites/" + page.url.args.site + "/memberships");

// Create javascript objects from the repo response
var memberships = eval('(' + json + ')');
if(!memberships)
{
   memberships = new Array();
}

// Prepare the model
model.memberships = memberships;
