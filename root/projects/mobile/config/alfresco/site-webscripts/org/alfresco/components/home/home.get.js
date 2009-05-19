<import resource="classpath:alfresco/site-webscripts/org/alfresco/utils.js">

var userTasks = getUserTasks('all').tasks;
var overdueTasks = getUserTasks('overdue').tasks;

var userEvents = getUserEvents().events;

model.role=page.url.templateArgs.site;
model.numTasks = userTasks.length;
model.numOverdueTasks = overdueTasks.length
model.numEvents = userEvents.length;
model.backButton = true;