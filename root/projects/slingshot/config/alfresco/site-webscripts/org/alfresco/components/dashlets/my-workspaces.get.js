function sortByTitle(site1, site2)
{
   return (site1.title > site2.title) ? 1 : (site1.title < site2.title) ? -1 : 0;
}

function main()
{
   // Call the repo for sites the user is a member of
   var result = remote.call("/api/people/" + stringUtils.urlEncode(user.name) + "/sites");
   if (result.status == 200)
   {
      // Create javascript objects from the server response
      var sites = eval('(' + result + ')');

      // Sort the sites by title
      sites.sort(sortByTitle);
      
      for (i = 0, ii = sites.length; i < ii; i++)
      {
         site = sites[i];
         
         // Is current user a Site Manager for this site?
         site.isSiteManager = false;
         if (site.siteManagers)
         {
            managers = site.siteManagers;
            for (j = 0, jj = managers.length; j < jj; j++)
            {
               if (managers[j] == user.name)
               {
                  site.isSiteManager = true;
                  break;
               }
            }
         }
      }
      model.numSites = sites.length
      // Prepare the model for the template
      model.sites = sites;
   }
}

main();