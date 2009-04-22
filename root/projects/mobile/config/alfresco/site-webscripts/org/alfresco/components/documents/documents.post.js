<import resource="classpath:alfresco/site-webscripts/org/alfresco/utils.js">
//POST http://localhost:8081/share/proxy/alfresco/slingshot/doclib/action/assign-workflow
//RESULTS
// {
//    "totalResults": 1,
//    "overallSuccess": true,
//    "successCount": 1,
//    "failureCount": 0,
//    "results":
//    [
//       {
//          "action": "assignWorkflow",
//          "nodeRef": "Node Type: {http://www.alfresco.org/model/content/1.0}content Node Ref: workspace
// ://SpacesStore/fdb8e091-b063-42a7-9565-4ef92247701c",
//          "type": "document",
//          "success": true,
//          "id": "background.png"
//       }
//    ]
// }

function assignWorkflow() 
{
   var args = page.url.args;
   var params = 
   {
     "date" :         new Date(Date.parse(args.date)).toString(),
     "description" :  args.description,
     "nodeRefs" :     [args.nodeRef],
     "people" :       [args.user],
     "type" :         args.type
   }.toSource();
   var connector = remote.connect("alfresco");
   //send post, not forgetting to strip wrapped rounded brackets that toSource() adds.
   var result = connector.post('/slingshot/doclib/action/assign-workflow',params.slice(1,params.length-1) , "application/json");
   return result;
}

model.recentDocs = getDocuments(page.url.args.site,'documentLibrary','recentlyModified',3).items;
model.allDocs = getDocuments(page.url.args.site,'documentLibrary','all',30).items;
model.myDocs = getDocuments(page.url.args.site,'documentLibrary','editingMe',3).items;
model.backButton = true;
var workflowResult = assignWorkflow();
if (workflowResult.overallSuccess)
{
   model.workflowResult = true;
}
else
{
   model.workflowResult = false;
}