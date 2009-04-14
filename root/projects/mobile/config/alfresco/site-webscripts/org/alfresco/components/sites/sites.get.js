const PREF_FAVOURITE_SITES = "org.alfresco.share.sites.favourites";

// http://localhost:8080/alfresco/service/api/sites
function getAllSites() {
  var data  = remote.call("/api/sites");
  return eval('('+ data+')');
}
//http://localhost:8080/alfresco/service/api/people/admin/sites?size={pagesize?}&pos={position?}
function getUserSites() {
  // var data = remote.call("/api/people/"+ +"/sites");
  //   return eval('('+ data+')');
// Call the repo for sites the user is a member of
   var result = remote.call("/api/people/" + stringUtils.urlEncode(user.name) + "/sites");
   if (result.status == 200)
   {
      var i, ii, j, jj;

      // Create javascript objects from the server response
      var sites = eval('(' + result + ')'), site, favourites = {},userfavs = [];

      if (sites.length > 0)
      {
         // Call the repo for the user's favourite sites
         result = remote.call("/api/people/" + stringUtils.urlEncode(user.name) + "/preferences?pf=" + PREF_FAVOURITE_SITES);
         if (result.status == 200 && result != "{}")
         {
            var prefs = eval('(' + result + ')');
            
            // Populate the favourites object literal for easy look-up later
            favourites = eval('(prefs.' + PREF_FAVOURITE_SITES + ')');
            if (typeof favourites != "object")
            {
               favourites = {};
            }
         }

         for (i = 0, ii = sites.length; i < ii; i++)
         {
            site = sites[i];
            
            // Is this site a user favourite?
            if (favourites[site.shortName]) 
            {
              site.isFavourite = true;
              userfavs.push(site);
            }
            site.isFavourite = !!(favourites[site.shortName]);
         }
        var userSites = {
          sites : sites,
          favSites: userfavs
        }
        return userSites;
      }
      return null;
   }  
}

var userSites = getUserSites();
model.sites = userSites.sites;
model.favSites=userSites.favSites;
model.allSites = getAllSites();
model.pageTitle = 'Sites';
model.backButton = true
