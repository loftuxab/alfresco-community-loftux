<import resource="classpath:alfresco/site-webscripts/org/alfresco/callutils.js">

var uri = "/calendar/events/user";
model.eventList = doGetCall(uri).events;