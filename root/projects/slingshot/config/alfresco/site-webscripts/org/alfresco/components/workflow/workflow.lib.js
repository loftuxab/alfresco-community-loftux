function getHiddenTaskTypes()
{
   var hiddenTaskTypes = [],
      hiddenTasks = config.scoped["Workflow"]["hidden-tasks"].childrenMap["task"];
   if (hiddenTasks)
   {
      for (var hi = 0, hil = hiddenTasks.size(); hi < hil; hi++)
      {
         hiddenTaskTypes.push(hiddenTasks.get(hi).attributes["type"]);
      }
   }
   return hiddenTaskTypes;
}

function getHiddenWorkflowNames()
{
   var hiddenWorkflowNames = [],
      hiddenWorkflows = config.scoped["Workflow"]["hidden-workflows"].childrenMap["workflow"];
   if (hiddenWorkflows)
   {
      for (var hi = 0, hil = hiddenWorkflows.size(); hi < hil; hi++)
      {
         hiddenWorkflowNames.push(hiddenWorkflows.get(hi).attributes["name"]);
      }
   }
   return hiddenWorkflowNames;
}

function getWorkflowDefinitions()
{
   var hiddenWorkflowNames = getHiddenWorkflowNames(),
      connector = remote.connect("alfresco"),
      result = connector.get("/api/workflow-definitions?exclude=" + hiddenWorkflowNames.join(","));
   if (result.status == 200)
   {
      return eval('(' + result + ')').data;         
   }
   return [];
}
