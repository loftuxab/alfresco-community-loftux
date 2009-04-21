// var json = remote.call("/api/sites/stuff/memberships/admin");
// var membership = eval('(' + json + ')');

//http://localhost:8080/alfresco/service/slingshot/dashlets/my-tasks?filter={filter?}&date={date?}
function getUserTasks()
{
  var data = remote.call("/slingshot/dashlets/my-tasks");
  return eval('(' + data + ')');      
}
//http://localhost:8080/alfresco/service/calendar/events/user
function getUserEvents()
{
  var data = remote.call("/calendar/events/user");
  return eval('(' + data + ')');
}

var userTasks = getUserTasks().tasks;
var userEvents = getUserEvents().events;

model.role=page.url.templateArgs.site;
model.numTasks = userTasks.length;
model.tasks = userTasks;
model.numEvents = userEvents.length;
model.events = userEvents;
model.pageTitle = 'Sites';
model.backButton = true;