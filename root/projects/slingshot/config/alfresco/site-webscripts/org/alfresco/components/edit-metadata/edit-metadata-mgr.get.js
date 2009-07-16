function fromContainerType(repoType)
{
   var type = "folder";
   switch (String(repoType))
   {
      case "dod:recordSeries":
         type = "record-series";
         break;

      case "dod:recordCategory":
         type = "record-series";
         break;

      case "dod:recordFolder":
         type = "record-series";
         break;
   }
   return type;
}

function main()
{
   // Need to know what type of node this is - document or folder
   var query =
   {
      items: [page.url.args.nodeRef]
   };

   var connector = remote.connect("alfresco");
   result = connector.post("/api/forms/picker/items", jsonUtils.toJSONString(query), "application/json");

   // Assume document
   var nodeType = "document";

   if (result.status == 200)
   {
      var metadata = eval('(' + result + ')');
      if (metadata.data.items[0].isContainer)
      {
         nodeType = fromContainerType(metadata.data.items[0].type);
      }
   }

   model.nodeType = nodeType;
}

main();