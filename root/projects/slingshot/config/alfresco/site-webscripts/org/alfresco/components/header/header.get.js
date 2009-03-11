const PREF_FAVOURITE_SITES = "org.alfresco.share.sites.favourites";

function sortByTitle(site1, site2)
{
   return (site1.title > site2.title) ? 1 : (site1.title < site2.title) ? -1 : 0;
}

function main()
{
   var favouriteSites = [];
   
   // Call the repo for the user's favourite sites
   // TODO: Clean-up old favourites here?
   var result = remote.call("/api/people/" + stringUtils.urlEncode(user.name) + "/preferences?pf=" + PREF_FAVOURITE_SITES);
   if (result.status == 200 && result != "{}")
   {
      var prefs = eval('(' + result + ')');
      
      // Populate the favourites object literal for easy look-up later
      favourites = eval('(prefs.' + PREF_FAVOURITE_SITES + ')');
      if (typeof favourites != "object")
      {
         favourites = {};
      }
      
      // Call the repo for sites the user is a member of - we only want to show favourites that the user is a current member of
      // TODO: Replace with API to only return a specific list of site metadata i.e. those in the fav list
      //       and check if the current user is a member of each before adding to fav list
      result = remote.call("/api/people/" + stringUtils.urlEncode(user.name) + "/sites");
      if (result.status == 200)
      {
         var i, ii;
         
         // Create javascript objects from the server response
         var sites = eval('(' + result + ')'), site;
         
         if (sites.length != 0)
         {
            // Sort the sites by title
            sites.sort(sortByTitle);
            
            for (i = 0, ii = sites.length; i < ii; i++)
            {
               site = sites[i];
               
               // Is this site a user favourite?
               if (!!(favourites[site.shortName]))
               {
                  favouriteSites.push(site);
               }
            }
         }
      }
   }
   
   // Prepare the model for the template
   model.favouriteSites = favouriteSites;
}

main();