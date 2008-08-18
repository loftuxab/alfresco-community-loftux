var siteId = page.url.templateArgs.site;

// get the membership info for the current user in the current site
var url = "/api/sites/" + siteId + "/memberships/" + user.id;
var json = remote.call(url);
var membership = eval('(' + json + ')');

// add the role to the model
model.currentUserRole = membership.role;

