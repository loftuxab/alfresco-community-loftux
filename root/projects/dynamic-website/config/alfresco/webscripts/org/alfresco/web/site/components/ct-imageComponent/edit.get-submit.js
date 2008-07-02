<import resource="/org/alfresco/web/site/include/ads-support.js">

// things we receive
var componentId = wizard.request("componentId");
var component = sitedata.getObject("component", componentId);
if(component != null)
{
	component.setProperty("imageLocation", imageURL);
	component.setProperty("width", imageWidth);
	component.setProperty("height", imageHeight);
	component.setProperty("alt", imageAlt);
	save(component);
}

// finalize things
wizard.setResponseCodeFinish();
wizard.setResponseMessage("Component settings successfully saved!");
wizard.setBrowserReload(true);
