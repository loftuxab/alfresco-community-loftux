/**
 * RM Search WebScript component
 */
function main()
{
   var siteId = page.url.templateArgs.site,
       meta = [];
   
   var conn = remote.connect("alfresco");
   
   // retrieve the RM custom properties - for display as meta-data fields etc.
   var res = conn.get("/api/classes/rmc_customProperties/properties");
   if (res.status == 200)
   {
      meta = eval('(' + res + ')');
   }
   
   model.meta = meta;
}

main();