<import resource="/org/alfresco/web/site/include/ads-support.js">


// things we receive
var componentId = wizard.request("componentId");
var component = sitedata.getObject("component", componentId);
if(component != null)
{
	component.setProperty("orientation", orientation);
	component.setProperty("style", style);
	save(component);
}

// finalize things
wizard.setResponseCodeFinish();
wizard.setResponseMessage("Component settings successfully saved!");
wizard.setBrowserReload(true);
