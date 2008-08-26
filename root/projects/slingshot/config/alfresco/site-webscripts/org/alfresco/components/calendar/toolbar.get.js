<import resource="classpath:alfresco/site-webscripts/org/alfresco/callutils.js">

// Check the role of the user - until there is a method of doing this check
// on the web tier we have to make a call back to the repo to get this information.
var obj = doGetCall("/api/sites/" + page.url.templateArgs.site + "/memberships/" + user.name);
if (obj)
{
   model.role = obj.role;
}
else
{
   model.role = "Consumer"; // default to safe option
}
