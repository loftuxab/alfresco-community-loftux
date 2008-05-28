var json = remote.call("/api/sites/" + page.url.args.site);
var profile = eval('(' + json + ')');
if(!profile)
{                                                        
   profile = {};
}
model.profile = profile;