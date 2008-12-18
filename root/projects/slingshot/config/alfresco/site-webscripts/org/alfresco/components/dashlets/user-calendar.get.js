<import resource="classpath:alfresco/site-webscripts/org/alfresco/callutils.js">

var now = new Date();
var fromDate = now.getFullYear() + "/" + (now.getMonth() + 1) + "/" + now.getDate();
var uri = "/calendar/events/user?from=" + encodeURIComponent(fromDate);

var data = doGetCall(uri);
if (data !== null)
{
   model.eventList = data.events;
}