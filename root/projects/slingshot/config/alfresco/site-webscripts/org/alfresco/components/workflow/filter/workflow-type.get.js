<import resource="classpath:alfresco/site-webscripts/org/alfresco/components/workflow/workflow.lib.js">
var workflowDefinitions = getWorkflowDefinitions(),
   filters = [];
if (workflowDefinitions)
{
   for (var i = 0, il = workflowDefinitions.length; i < il; i++)
   {
      filters.push(
      {
         id: "workflowType",
         data: workflowDefinitions[i].id,
         label: workflowDefinitions[i].title
      });
   }
}
model.filters = filters;
