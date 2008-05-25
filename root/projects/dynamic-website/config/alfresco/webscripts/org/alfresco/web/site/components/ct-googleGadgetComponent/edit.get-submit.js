<import resource="/org/alfresco/web/site/include/ads-support.js">

// things we receive
var componentId = wizard.request("componentId");
var component = sitedata.getObject(componentId);
if(component != null)
{
	markupData = sitedata.encode(markupData);
	component.setProperty("markupData", markupData);
	save(component);
}

// finalize things
wizard.setResponseCodeFinish();
wizard.setResponseMessage("Component settings successfully saved!");
wizard.setBrowserReload(true);
