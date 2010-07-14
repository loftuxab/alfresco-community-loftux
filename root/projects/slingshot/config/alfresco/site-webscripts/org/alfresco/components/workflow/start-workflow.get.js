var connector = remote.connect("alfresco"),
   result = connector.get("/api/workflow-definitions");
if (result.status == 200)
{
   var filteredWorkflows = [],
      workflows = eval('(' + result + ')').data,
      hiddenWorkflows = config.scoped["Workflow"]["hidden-workflows"].childrenMap["workflow"],
      hiddenWorkflowName;
   for (var i = 0, il = workflows.length; i < il; i++)
   {
      try
      {
         if (hiddenWorkflows)
         {
            for (var hi = 0, hil = hiddenWorkflows.size(); hi < hil; hi++)
            {
               hiddenWorkflowName = hiddenWorkflows.get(hi).attributes["name"];
               if (workflows[i].name == hiddenWorkflowName)
               {
                  break;
               }
            }
            if (hi == hil)
            {
               filteredWorkflows.push(workflows[i]);
            }
         }
      }
      catch (e)
      {
      }
   }
   model.workflowDefinitions = filteredWorkflows;
}
