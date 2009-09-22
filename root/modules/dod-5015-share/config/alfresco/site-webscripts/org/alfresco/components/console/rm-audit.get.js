function main()
{
   var meta = [];
   
   var conn = remote.connect("alfresco");
   
   // retrieve the RM custom properties - for display as meta-data fields etc.
   var elements = ["record", "recordFolder", "recordCategory", "recordSeries"];
   for each (var el in elements)
   {
      retrieveMetadataForElement(conn, meta, el);
   }
   model.meta = meta;
   model.events = retrieveAuditEvents(conn);
   model.eventsStr = model.events.toSource();
   
}

function retrieveMetadataForElement(conn, meta, el)
{
   var res = conn.get("/api/rma/admin/custompropertydefinitions?element=" + el);
   if (res.status == 200)
   {
      var props = eval('(' + res + ')').data.customProperties;
      for (var id in props)
      {
         var prop = props[id];
         meta.push(
         {
            name: id,
            title: prop.label,
            dataType: prop.dataType
         });
      }
   }
}

function retrieveAuditEvents(conn)
{
   var res = conn.get("/api/rma/admin/listofvalues");
   if (res.status == 200)
   {
      return eval('(' + res + ')').data.auditEvents.items;
   }
   else
   {
      return [];
   }
}

main();