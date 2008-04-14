<import resource="/Company Home/Data Dictionary/Web Scripts Extensions/org/alfresco/website/include/dialog-support.js">
<import resource="/Company Home/Data Dictionary/Web Scripts Extensions/org/alfresco/website/include/avm-support.js">
<import resource="/Company Home/Data Dictionary/Web Scripts Extensions/org/alfresco/website/include/ads-support.js">

// things we receive
var componentAssociationId = requestJSON["componentAssociationId"];

// elements
var webscript = getElementValue("webscript");
if(webscript == null)
	webscript = "";	
var endpointId = getElementValue("endpointId");
if(endpointId == null)
	endpointId = "";	
var container = getElementValue("container");
if(container == null)
	container = "";	

var componentAssociation = site.getObject(componentAssociationId);
if(componentAssociation != null)
{
	var componentId = componentAssociation.getProperty("componentId");
	var component = site.getObject(componentId);
	if(component != null)
	{
		component.setSetting("endpointId", endpointId);
		component.setSetting("webscript", webscript);
		component.setSetting("container", container);
		save(component);
	}
}

// finalize things
setResponseCodeFinish();
setResponseMessage("Component settings successfully saved!");
setBrowserReload(true);
finalize();
