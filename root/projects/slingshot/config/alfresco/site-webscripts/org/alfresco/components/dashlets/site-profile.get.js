// Call the repo for the sites profile
var json = remote.call("/api/sites/" + page.url.args.site);

// Create javascript object from the repo response
var profile = eval('(' + json + ')');
if(!profile)
{                                                        
   profile = {};
}

// Find the manager for the site
json = remote.call("/api/sites/" + page.url.args.site + "/memberships?rf=SiteManager");
var sitemanagers = eval('(' + json + ')');
if(!sitemanagers)
{
   sitemanagers = new Array();
   sitemanagers[0] = {};
}

// Prepare the model
model.profile = profile;
model.sitemanager = sitemanagers[0];
