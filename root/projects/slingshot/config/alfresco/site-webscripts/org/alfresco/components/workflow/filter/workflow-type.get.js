<import resource="classpath:alfresco/site-webscripts/org/alfresco/components/workflow/workflow.lib.js">
var workflowDefinitions = getWorkflowTypes(),
   filters = [];
filters.push(
{
   id: "workflowType",
   data: "",
   label: "link.any"
});
if (workflowDefinitions)
{
   for (var i = 0, il = workflowDefinitions.length; i < il; i++)
   {
      filters.push(
      {
         id: "workflowType",
         data: "definitionId=" + workflowDefinitions[i].id,
         label: workflowDefinitions[i].title
      });
   }
}
model.filters = filters;
