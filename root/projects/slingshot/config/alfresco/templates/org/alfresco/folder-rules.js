<import resource="classpath:/alfresco/templates/org/alfresco/documentlibrary.js">

function main()
{
   var nodeRef = page.url.args.nodeRef,
      connector = remote.connect("alfresco");

   // Load folder info
   var result = connector.post("/api/forms/picker/items", '{"items": ["' + page.url.args.nodeRef + '"]}', "application/json");
   if (result.status == 200)
   {
      var folderDetails = eval('(' + result + ')').data.items[0];
      model.folder = {
         name: folderDetails.name,
         path: folderDetails.displayPath
      };
   }

   // Load rules
   result = connector.get("/api/node/" + page.url.args.nodeRef.replace("://", "/") + "/ruleset");
   if (result.status == 200)
   {
      var ruleset = eval('(' + result + ')').data;
      if (!ruleset)
      {
         ruleset = {};
      }
      model.ruleset = ruleset;
   }
}

main();
