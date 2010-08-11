/**
 * Search Title component GET method
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
   
   // Build the Advanced Search link - construct with args to return here
   if (page.url.args["t"] != null || page.url.args["tag"] != null)
   {
      var query = "st=" + (page.url.args["t"] != null ? encodeURIComponent(page.url.args["t"]) : "") +
                  "&stag=" + (page.url.args["tag"] != null ? encodeURIComponent(page.url.args["tag"]) : "") +
                  "&ss=" + (page.url.args["s"] != null ? encodeURIComponent(page.url.args["s"]) : "") +
                  "&sa=" + (page.url.args["a"] != null ? encodeURIComponent(page.url.args["a"]) : "");
      model.advsearchlink = query;
   }
}

main();