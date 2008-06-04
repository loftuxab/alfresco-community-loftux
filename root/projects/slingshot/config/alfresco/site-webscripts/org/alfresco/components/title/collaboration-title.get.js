// CAll the repo for the site profile
var json = remote.call("/api/sites/" + page.url.args.site);

// Create javascript objects from the repo response
var profile = eval('(' + json + ')');
if(!profile)
{                                                        
   profile = {};
}

// Prepare the model
model.profile = profile;