<import resource="/Company Home/Data Dictionary/Web Scripts Extensions/org/alfresco/website/include/dialog-support.js">
<import resource="/Company Home/Data Dictionary/Web Scripts Extensions/org/alfresco/website/include/avm-support.js">
<import resource="/Company Home/Data Dictionary/Web Scripts Extensions/org/alfresco/website/include/ads-support.js">

// things we receive
var componentAssociationId = requestJSON["componentAssociationId"];

// elements
var queryType = getElementValue("queryType");
if(queryType == null)
	queryType = "";	
var queryString = getElementValue("queryString");
if(queryString == null)
	queryString = "";	
var howToRender = getElementValue("howToRender");
if(howToRender == null)
	howToRender = "";	
var renderData = getElementValue("renderData");
if(renderData == null)
	renderData = "";	
var endpointId = getElementValue("endpointId");
if(endpointId == null)
	endpointId = "";		

var componentAssociation = sitedata.getObject(componentAssociationId);
if(componentAssociation != null)
{
	var componentId = componentAssociation.getProperty("componentId");
	var component = sitedata.getObject(componentId);
	if(component != null)
	{
		component.setProperty("queryType", queryType);
		component.setProperty("queryString", queryString);
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
