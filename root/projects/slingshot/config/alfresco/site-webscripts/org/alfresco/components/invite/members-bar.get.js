<import resource="classpath:alfresco/site-webscripts/org/alfresco/callutils.js">

model.isManager = false;
    
// Check the role of the user - only SiteManagers are allowed to invite people/view invites
var obj = doGetCall("/api/sites/" + page.url.templateArgs.site + "/memberships/" + user.name);
if (obj)
{
   if (obj.role == 'SiteManager')
   {
      model.isManager = true;
   }
}
