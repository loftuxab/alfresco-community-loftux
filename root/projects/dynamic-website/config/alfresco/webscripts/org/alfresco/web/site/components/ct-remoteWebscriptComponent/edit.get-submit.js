<import resource="/org/alfresco/web/site/include/ads-support.js">

// things we receive
var componentId = wizard.request("componentId");
var component = site.getObject(componentId);
if(component != null)
{
	component.setProperty("endpointId", endpointId);
	component.setProperty("webscript", webscript);
	component.setProperty("container", container);
	component.save();
}

// finalize things
wizard.setResponseCodeFinish();
wizard.setResponseMessage("Component settings successfully saved!");
wizard.setBrowserReload(true);
