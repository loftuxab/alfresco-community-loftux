<import resource="/Company Home/Data Dictionary/Web Scripts Extensions/org/alfresco/website/include/component-support.js">

// incoming values
var itemType = getSafeElementValue("itemType");
var itemPath = getSafeElementValue("itemPath");
var howToRender = getSafeElementValue("howToRender");
var renderData = getSafeElementValue("renderData");
var endpointId = getSafeElementValue("endpointId");

// process
var componentAssociationId = requestJSON["componentAssociationId"];
var componentAssociation = site.getObject(componentAssociationId);
if(componentAssociation != null)
{
	var componentId = componentAssociation.getProperty("componentId");
	var component = site.getObject(componentId);
	if(component != null)
	{
		component.setSetting("itemType", itemType);
		component.setSetting("itemPath", itemPath);
		component.setSetting("howToRender", howToRender);
		component.setSetting("renderData", renderData);
		component.setSetting("endpointId", endpointId);
		save(component);
	}
}

// finalize things
setResponseCodeFinish();
setResponseMessage("Component settings successfully saved!");
setBrowserReload(true);
finalize();
