<import resource="/org/alfresco/web/site/include/ads-support.js">

// things we receive
var componentId = wizard.request("componentId");
var component = sitedata.getObject(componentId);
if(component != null)
{
	component.setProperty("mediaType", mediaType);
	component.setProperty("mediaUrl", mediaUrl);
	component.setProperty("unsupportedText", unsupportedText);
	component.setProperty("width", width);
	component.setProperty("height", height);
	save(component);
}

// finalize things
wizard.setResponseCodeFinish();
wizard.setResponseMessage("Component settings successfully saved!");
wizard.setBrowserReload(true);
