/**
 * Advanced Search Title component GET method
 */

function main()
{
   if (page.url.templateArgs.site != null)
   {
      // look for request scoped cached site title
      var siteTitle = context.properties["site-title"];
      if (siteTitle == null)
      {
         // Call the repository for the site profile
         var json = remote.call("/api/sites/" + page.url.templateArgs.site);
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
      
      // Prepare the model
      model.siteTitle = (siteTitle != null ? siteTitle : "");
   }
   
   // Build search results back link from supplied args
   if (page.url.args["st"] != null || page.url.args["stag"] != null)
   {
      var query = "t=" + (page.url.args["st"] != null ? encodeURIComponent(page.url.args["st"]) : "") +
                  "&tag=" + (page.url.args["stag"] != null ? encodeURIComponent(page.url.args["stag"]) : "") +
                  "&s=" + (page.url.args["ss"] != null ? encodeURIComponent(page.url.args["ss"]) : "") +
                  "&a=" + (page.url.args["sa"] != null ? encodeURIComponent(page.url.args["sa"]) : "");
      model.backlink = query;
   }
}

main();