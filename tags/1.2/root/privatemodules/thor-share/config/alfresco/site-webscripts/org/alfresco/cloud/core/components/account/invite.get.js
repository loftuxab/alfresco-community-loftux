<import resource="classpath:/alfresco/templates/org/alfresco/import/alfresco-util.js">

function main()
{
   var roles = AlfrescoUtil.getSiteRoles(args.site);
   for (var i = 0; i < roles.length; i++)
   {
      if (roles[i].id == "SiteManager")
      {
         roles.splice(i, 1);
      }
   }
   model.roles = roles;
   model.site = AlfrescoUtil.getSite(args.site);
}

main();