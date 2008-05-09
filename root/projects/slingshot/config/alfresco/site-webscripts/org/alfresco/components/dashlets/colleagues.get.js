//var json = '[{"name":"Dave Smith", "role":"CONTRIBUTOR", "loggedIn":"09:37 pm", "lastActivity":"12:37 pm"},{"name":"Jeff Potts", "role":"CONTRIBUTOR", "loggedIn":"09:38 pm", "lastActivity":"12:38 pm"},{"name":"Kevin Roast", "role":"CONTRIBUTOR", "loggedIn":"09:39 pm", "lastActivity":"12:39 pm"}]';

//page.url.args.site
var json = remote.call("/api/sites/" + page.url.args.site + "/memberships");
var memberships = eval('(' + json + ')');
if(!memberships)
{
   memberships = new Array();
}
model.memberships = memberships;
