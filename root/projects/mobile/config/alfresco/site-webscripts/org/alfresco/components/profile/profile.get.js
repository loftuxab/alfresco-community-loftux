<import resource="classpath:alfresco/site-webscripts/org/alfresco/utils.js">
model.backButton = true;

var profileId = page.url.args['person'];
var userObj = user.getUser(profileId);

if (userObj != null)
{
   model.profile = userObj;
}
else {
   model.profile = {};
}
var userSites = getUserSites(profileId);
model.userSites = userSites;
model.numUserSites = userSites.sites.length;