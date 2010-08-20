
var hiddenTaskTypes = [],
   hiddenTasks = config.scoped["Workflow"]["hidden-tasks"].childrenMap["task"];
if (hiddenTasks)
{
   for (var hi = 0, hil = hiddenTasks.size(); hi < hil; hi++)
   {
      hiddenTaskTypes.push(hiddenTasks.get(hi).attributes["type"]);
   }
}
model.hiddenTaskTypes = hiddenTaskTypes;