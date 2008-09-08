<import resource="classpath:alfresco/site-webscripts/org/alfresco/callutils.js">

// Check whether the current user is a member of the site first and then if they are
// the role of the user - until there is a method of doing this check on the web tier 
// we have to make a call back to the repo to get this information.

var obj = doGetCall("/api/sites/" + page.url.templateArgs.site + "/memberships");

var currentUser = user.name;
var role = null;

// iterate round the members of the site and see if current user is present
// TODO: when we have a pattern for doing isXXX checks this should be refined
for (var x = 0; x < obj.length; x++)
{
   if (obj[x].person.userName == currentUser)
   {
      role = obj[x].role;
   }
}

// set role appropriately
if (role !== null)
{
   model.role = role;
}
else
{
   model.role = "Consumer"; // default to safe option
}
