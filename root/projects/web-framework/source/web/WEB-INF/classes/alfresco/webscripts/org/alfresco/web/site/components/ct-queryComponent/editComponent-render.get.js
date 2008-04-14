<import resource="/Company Home/Data Dictionary/Web Scripts Extensions/org/alfresco/website/include/dialog-support.js">
<import resource="/Company Home/Data Dictionary/Web Scripts Extensions/org/alfresco/website/include/avm-support.js">
<import resource="/Company Home/Data Dictionary/Web Scripts Extensions/org/alfresco/website/include/ads-support.js">


// set up buttons
addButton("Cancel", "cancel", true);
addButton("Back", "_start", true);
addButton("Save", "_submit", true);


// set up states
addState("_start", "/ads/components/ct-queryComponent/edit", false);
addState("_render", "/ads/components/ct-queryComponent/edit/_render", false);
addState("_submit", "/ads/components/ct-queryComponent/edit/_submit", true);


// set the page title
setResponseTitle("Query Component: Render Settings");

// things we receive
var componentAssociationId = requestJSON["componentAssociationId"];

// elements
var queryType = getElementValue("queryType");
if(queryType == null)
	queryType = "";	
var queryString = getElementValue("queryString");
if(queryString == null)
	queryString = "";
var endpointId = getElementValue("endpointId");
if(endpointId == null)
	endpointId = "";	
	

// get the component's settings
// these are stored on the component
// todo: rework and expose the configuration manager concept through javascript

var componentAssociation = site.getObject(componentAssociationId);
if(componentAssociation != null)
{
	var componentId = componentAssociation.getProperty("componentId");
	var component = site.getObject(componentId);
	if(component != null)
	{
		// RENDITION TYPE			
		var howToRender = component.getSetting("howToRender");
		if(howToRender == null)
			howToRender = ""
		addElement("howToRender", howToRender);
		addElementFormat("howToRender", "How to Render", "combo", 220);
		addElementSelectionValue("howToRender", "templateTitle", "By Rendition Template Title");
		addElementSelectionValue("howToRender", "templateMimetype", "By Rendition Template Mimetype");
		addElementSelectionValue("howToRender", "xslDocument", "By URL to an XSL document");


		// RENDER DATA
		var renderData = component.getSetting("renderData");
		if(renderData == null)
			renderData = "";
		addElement("renderData", renderData);
		addElementFormat("renderData", "Data", "textfield", 220);


		// Hidden State
		addHiddenElement("queryType", queryType);
		addHiddenElement("queryString", queryString);
		addHiddenElement("endpointId", endpointId);
	}
}

// call the finalize method
finalize();

