/**
 * RM Search WebScript component
 */
function main()
{
   var siteId = page.url.templateArgs.site,
       meta = [],
       searches = [];
   
   var conn = remote.connect("alfresco");
   
   // retrieve the RM custom properties - for display as meta-data fields etc.
   var res = conn.get("/api/classes/rmc_customProperties/properties");
   if (res.status == 200)
   {
      meta = eval('(' + res + ')');
   }
   
   // retrieve the public saved searches
   // TODO: user specific searches?
   res = conn.get("/slingshot/doclib/dod5015/savedsearches/site/" + siteId);
   if (res.status == 200)
   {
      var s, ss = eval('(' + res + ')');
      try
      {
         for each (s in ss.items)
         {
            searches.push(
            {
               id: s.name,
               label: s.name,
               description: s.description
            });
         }
      }
      catch (e)
      {
      }
   }
   
   model.searches = searches;
   model.meta = meta;
}

main();