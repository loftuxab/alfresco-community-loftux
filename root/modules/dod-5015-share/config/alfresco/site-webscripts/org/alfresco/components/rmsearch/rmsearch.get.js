/**
 * RM Search WebScript component
 */
function main()
{
   var siteId = page.url.templateArgs.site,
       meta = [];
   
   var conn = remote.connect("alfresco");
   
   // retrieve the RM custom properties - for display as meta-data fields etc.
   var res = conn.get("/api/rma/admin/custompropertydefinitions?element=record");
   if (res.status == 200)
   {
      var props = eval('(' + res + ')').data.customProperties;
      for (var id in props)
      {
         var prop = props[id];
         meta.push(
         {
            name: id,
            title: prop.title,
            dataType: prop.dataType
         });
      }
   }
   
   model.meta = meta;
}

main();