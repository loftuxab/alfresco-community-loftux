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

      // See if current user has Site admin rights
      for(var i = 0; i < sites.length; i++)
      {
         sites[i].isSiteManager = false;
         var siteManagers = sites[i].siteManagers;
         for(var j = 0; siteManagers && j < siteManagers.length; j++)
         {
            if(siteManagers[j] == user.name)
            {
               sites[i].isSiteManager = true;
               break;
            }
         }
      }

      if(sites.length > 0)
      {
         var favouriteSitesToken = "org.alfresco.share.sites.favourites";

         // Find out what sites that are the users favourites
         var fsResult = remote.call("/api/people/" + stringUtils.urlEncode(user.name) + "/preferences?pf=" + favouriteSitesToken);
         if (fsResult.status == 200)
         {
            var obj = eval('(' + fsResult + ')');
            var favourites = null;
            // todo: find a better way of making sure the value exist
            if(obj && obj.org && obj.org.alfresco && obj.org.alfresco.share && obj.org.alfresco.share.sites && obj.org.alfresco.share.sites.favourites)
            {
               favourites = obj.org.alfresco.share.sites.favourites;
            }
            else
            {
               favourites = {};
            }
            for(var i = 0; i < sites.length; i++)
            {
               sites[i].favourite = favourites[sites[i].shortName] ? true : false;
            }
         }
      }

      // Prepare the model for the template
		model.sites = sites;
	}
}

main();