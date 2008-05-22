var json = remote.call("/api/sites/" + page.url.args.site);
var profile = eval('(' + json + ')');
logger.log("PROFILE:" + profile);
if(!profile)
{                                                        
   profile = {};
}
model.profile = profile;

json = remote.call("/api/sites/" + page.url.args.site + "/memberships?rf=SiteManager");
var sitemanagers = eval('(' + json + ')');
logger.log("MANAGERS:" + sitemanagers.length);
if(!sitemanagers)
{
   sitemanagers = new Array();
   sitemanagers[0] = {};
}
model.sitemanager = sitemanagers[0];
