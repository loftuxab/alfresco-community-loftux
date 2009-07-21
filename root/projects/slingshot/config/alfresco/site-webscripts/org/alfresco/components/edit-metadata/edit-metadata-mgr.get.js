function fromContainerType(repoType)
{
   var type = "folder";
   switch (String(repoType))
   {
      case "dod:recordSeries":
         type = "record-series";
         break;

      case "dod:recordCategory":
         type = "record-category";
         break;

      case "dod:recordFolder":
         type = "record-folder";
         break;
   }
   return type;
}

function fromRMAType(repoType, nodeRef, connector)
{
   var containerType, backLinkNodeRef;
   switch (String(repoType))
   {
      case "rma:dispositionSchedule":
         containerType = "record-category";
         backLinkNodeRef = getParentNodeRef(nodeRef, connector);
         break;
   }
   return [containerType, backLinkNodeRef];
}

function getParentNodeRef(childNodeRef, connector)
{
   var result = connector.get("/api/forms/picker/node/" + childNodeRef.replace(":/", "") + "/children");
   var resultJson = eval('(' + result + ')');
   return resultJson.data.parent.parent.nodeRef;
}

function main()
{
   // Need to know what type of node this is - document or folder
   var nodeRef = page.url.args.nodeRef;
   var query =
   {
      items: [nodeRef]
   };

   var connector = remote.connect("alfresco");
   result = connector.post("/api/forms/picker/items", jsonUtils.toJSONString(query), "application/json");

   // Assume document
   var nodeType = "document", backLinkNodeRef = "";

   if (result.status == 200)
   {
      var metadata = eval('(' + result + ')');
      if (metadata.data.items[0].isContainer)
      {
         nodeType = fromContainerType(metadata.data.items[0].type);
         backLinkNodeRef = nodeRef;
      }
      else if(metadata.data.items[0].type.indexOf("rma:") == 0)
      {
         var typeAndBackLink = fromRMAType(metadata.data.items[0].type, nodeRef, connector);
         nodeType = typeAndBackLink[0];
         backLinkNodeRef = typeAndBackLink[1];
      }
   }
   model.nodeRef = nodeRef;
   model.nodeType = nodeType;
   model.backLinkNodeRef = backLinkNodeRef;
}

main();