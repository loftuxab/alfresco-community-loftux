<import resource="/Company Home/Data Dictionary/Web Scripts Extensions/org/alfresco/website/include/dialog-support.js">
<import resource="/Company Home/Data Dictionary/Web Scripts Extensions/org/alfresco/website/include/avm-support.js">
<import resource="/Company Home/Data Dictionary/Web Scripts Extensions/org/alfresco/website/include/ads-support.js">


// set up buttons
addButton("Save", "_submit", true);
addButton("Cancel", "cancel", true);

// set up states
addState("_submit", "/ads/components/ct-webscriptComponent/edit/_submit", true);

// set the page title
setResponseTitle("Webscript Component Properties");

// things we receive
var componentAssociationId = requestJSON["componentAssociationId"];

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
		var endpointId = component.getSetting("endpointId");
		if(endpointId == null)
			endpointId = "";
		var webscript = component.getSetting("webscript");
		if(webscript == null)
			webscript = "";
		var container = component.getSetting("container");
		if(container == null)
			container = "div";


		// endpoint selector combo
		addElement("endpointId", endpointId);
		addElementFormat("endpointId", "Endpoint ID", "combo", 220);
		addElementSelectionValue("endpointId", "none", "None");
		var endpoints = site.getAllEndpoints();
		for(var z = 0; z < endpoints.length; z++)
		{
			var _endpointId = endpoints[z].getProperty("endpointId");
			var _endpointDescription = _endpointId;			
			addElementSelectionValue("endpointId", _endpointId, _endpointDescription);
		}
		updateElement("endpointId", endpointId);

		// web script uri		
		addElement("webscript", webscript);
		addElementFormat("webscript", "Web Script URI", "textfield", 290);

		// container
		addElement("container", container);
		addElementFormat("container", "Container", "combo", 220);
		addElementSelectionValue("container", "div", "DIV");
		addElementSelectionValue("container", "iframe", "IFrame");
		updateElement("container", container);

	}
}

// call the finalize method
finalize();

