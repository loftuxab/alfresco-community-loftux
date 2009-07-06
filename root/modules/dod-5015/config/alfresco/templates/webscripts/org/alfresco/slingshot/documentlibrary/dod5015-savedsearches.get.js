function main()
{
   var savedSearches = [],
      siteId = url.templateArgs.site,
      siteNode = siteService.getSite(siteId);

   if (siteNode === null)
   {
      status.setCode(status.STATUS_NOT_FOUND, "Site not found: '" + siteId + "'");
      return null;
   }
   
   var searchNode = siteNode.getContainer("Saved Searches");
   if (searchNode != null)
   {
      var kids = searchNode.children,
         ssNode;
      
      for (var i = 0, ii = kids.length; i < ii; i++)
      {
         ssNode = kids[i];
         savedSearches.push(
         {
            name: ssNode.name,
            description: ssNode.properties.description
         });
      }
   }
   
   model.savedSearches = savedSearches;
}

main();