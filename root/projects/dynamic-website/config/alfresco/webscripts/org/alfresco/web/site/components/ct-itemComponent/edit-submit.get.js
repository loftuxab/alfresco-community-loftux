<import resource="/Company Home/Data Dictionary/Web Scripts Extensions/org/alfresco/website/include/component-support.js">

// incoming values
var itemType = getSafeElementValue("itemType");
var itemPath = getSafeElementValue("itemPath");
var howToRender = getSafeElementValue("howToRender");
var renderData = getSafeElementValue("renderData");
var endpointId = getSafeElementValue("endpointId");

// process
var componentAssociationId = requestJSON["componentAssociationId"];
var componentAssociation = sitedata.getObject(componentAssociationId);
if(componentAssociation != null)
{
	var componentId = componentAssociation.getProperty("componentId");
	var component = sitedata.getObject(componentId);
	if(component != null)
	{
		component.setProperty("itemType", itemType);
		component.setProperty("itemPath", itemPath);
		component.setProperty("howToRender", howToRender);
		component.setProperty("renderData", renderData);
		component.setProperty("endpointId", endpointId);
		save(component);
	}
}

// finalize things
setResponseCodeFinish();
setResponseMessage("Component settings successfully saved!");
setBrowserReload(true);
finalize();
