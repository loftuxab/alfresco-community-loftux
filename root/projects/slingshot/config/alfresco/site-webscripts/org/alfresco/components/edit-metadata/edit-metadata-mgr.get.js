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
   nodeType = metadata.data.items[0].isContainer ? "folder" : "document";
}

model.nodeType = nodeType;
