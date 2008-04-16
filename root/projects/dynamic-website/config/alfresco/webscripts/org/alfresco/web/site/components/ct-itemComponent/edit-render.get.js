<import resource="/Company Home/Data Dictionary/Web Scripts Extensions/org/alfresco/website/include/component-support.js">

// set up the page
setResponseTitle("Item Component: Render Settings");
addButton("Cancel", "cancel", true);
addButton("Back", "_start", true);
addButton("Save", "_submit", true);
addState("_start", "/ads/components/ct-itemComponent/edit", false);
addState("_submit", "/ads/components/ct-itemComponent/edit/_submit", true);

// incoming values
var itemType = getSafeElementValue("itemType");
var itemPath = getSafeElementValue("itemPath");
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
		var howToRender = getSafeComponentSetting(component, "howToRender");
		addElement("howToRender", howToRender);
		addElementFormat("howToRender", "How to Render", "combo", 220);
		addElementSelectionValue("howToRender", "templateTitle", "By Rendition Template Title");
		addElementSelectionValue("howToRender", "templateMimetype", "By Rendition Template Mimetype");
		addElementSelectionValue("howToRender", "xslDocument", "By URL to an XSL document");
		addElementSelectionValue("howToRender", "direct", "File contents only");


		var renderData = getSafeComponentSetting(component, "renderData");
		addElement("renderData", renderData);
		addElementFormat("renderData", "Data", "textfield", 220);


		// Hidden State
		addHiddenElement("itemType", itemType);
		addHiddenElement("itemPath", itemPath);
		addHiddenElement("endpointId", endpointId);
	}
}

// call the finalize method
finalize();

