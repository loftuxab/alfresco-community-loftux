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
   if(metadata.data.items[0].isContainer)
   {
      if(metadata.data.items[0].type == "dod:recordCategory")
      {
         nodeType = "record-category";
      }
      else if(metadata.data.items[0].type == "dod:recordSeries")
      {
         nodeType = "record-series";
      }
      else
      {
         nodeType = "folder";
      }
   }
}

model.nodeType = nodeType;
