<import resource="classpath:alfresco/site-webscripts/org/alfresco/callutils.js">

model.isManager = false;
    
// Check the role of the user - only SiteManagers are allowed to invite people/view invites
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

// set the isManager flag if appropriate
if (role !== null && role == 'SiteManager')
{
   model.isManager = true;
}
