<import resource="/org/alfresco/web/site/include/ads-support.js">

// things we receive
var componentId = wizard.request("componentId");
var component = site.getObject(componentId);
if(component != null)
{
	component.setSetting("endpointId", endpointId);
	component.setSetting("webscript", webscript);
	component.setSetting("container", container);
	component.save();
}

// finalize things
wizard.setResponseCodeFinish();
wizard.setResponseMessage("Component settings successfully saved!");
wizard.setBrowserReload(true);
