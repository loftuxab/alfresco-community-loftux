const PREF_FAVOURITE_SITES = "org.alfresco.share.sites.favourites";

function sortByTitle(site1, site2)
{
   return (site1.title > site2.title) ? 1 : (site1.title < site2.title) ? -1 : 0;
}

function main()
{
   var favouriteSites = [],
      currentSiteIsFav = false;
   
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
      
      // Call the repo to return a specific list of site metadata i.e. those in the fav list
      // and ensure the current user is a member of each before adding to fav list
      var query =
      {
         "shortName" :
         {
            "match" : "exact",
            "values" : []
         }
      };
      var shortName;
      for (shortName in favourites)
      {
         if (favourites[shortName])
         {
            query.shortName.values.push(shortName);
         }
      }
      
      var connector = remote.connect("alfresco");
      result = connector.post("/api/sites/query", jsonUtils.toJSONString(query), "application/json");
      
      if (result.status == 200)
      {
         var i, ii, currentSite = page.url.templateArgs.site || "";
         
         // Create javascript objects from the server response
         // Each item is a favourite site that the user is a member of
         var sites = eval('(' + result + ')');
         
         if (sites.length != 0)
         {
            // Sort the sites by title
            sites.sort(sortByTitle);
            
            for (i = 0, ii = sites.length; i < ii; i++)
            {
               if (sites[i].shortName == currentSite)
               {
                  currentSiteIsFav = true;
               }
               favouriteSites.push(sites[i]);
            }
         }
      }
   }
   // Prepare the model for the template
   model.currentSiteIsFav = currentSiteIsFav;
   model.favouriteSites = favouriteSites;
}

main();