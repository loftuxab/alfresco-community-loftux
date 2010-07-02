/**
 * Search component GET method
 */

function main()
{
   // fetch the request params required by the search component template
   var siteId = (page.url.templateArgs["site"] != null) ? page.url.templateArgs["site"] : "";
   var siteTitle = null;
   if (siteId.length != 0)
   {
      // look for request scoped cached site title
      siteTitle = context.properties["site-title"];
      if (siteTitle == null)
      {
         // Call the repository for the site profile
         var json = remote.call("/api/sites/" + siteId);
         if (json.status == 200)
         {
            // Create javascript objects from the repo response
            var obj = eval('(' + json + ')');
            if (obj)
            {
               siteTitle = (obj.title.length != 0) ? obj.title : obj.shortName;
            }
         }
      }
   }
   
   // Prepare the model
   model.siteId = siteId;
   model.siteTitle = (siteTitle != null ? siteTitle : "");
   model.searchTerm = (page.url.args["t"] != null) ? page.url.args["t"] : "";
   model.searchTag = (page.url.args["tag"] != null) ? page.url.args["tag"] : "";
   model.searchSort = (page.url.args["s"] != null) ? page.url.args["s"] : "";
   model.searchAll = (page.url.args["a"] == "true");
}

main();