<import resource="/Company Home/Data Dictionary/Web Scripts Extensions/org/alfresco/website/include/component-support.js">

// set up the dialog
setResponseTitle("Item Component: Settings");
addButton("Cancel", "cancel", true);
addButton("Next", "_render", true);
addState("_render", "/ads/components/ct-itemComponent/edit/_render", false);

// process
var componentAssociationId = requestJSON["componentAssociationId"];
var componentAssociation = sitedata.getObject(componentAssociationId);
if(componentAssociation != null)
{
	var componentId = componentAssociation.getProperty("componentId");
	var component = sitedata.getObject(componentId);
	if(component != null)
	{
		var itemType = getSafeComponentSetting(component, "itemType");

		// ITEM TYPE
		addElement("itemType", "");
		addElementFormat("itemType", "Type", "combo", 220);
		addElementSelectionValue("itemType", "current", "Show the currently selected item");
		addElementSelectionValue("itemType", "specific", "Show a specific item");
		if(itemType != null)
			updateElement("itemType", itemType);

		// ITEM PATH
		var itemPath = getSafeComponentSetting(component, "itemPath");
		itemPath = escape(itemPath);
		addElement("itemPath", itemPath);
		addElementFormat("itemPath", "Path", "textfield", 220);
		
		// ENDPOINT
		var endpointId = component.getProperty("endpointId");
		if(endpointId == null)
			endpointId = "alfresco-webuser";
		addElement("endpointId", endpointId);
		addElementFormat("endpointId", "Endpoint ID", "combo", 220);
		addElementSelectionValue("endpointId", "none", "None");
		var endpoints = remote.getEndpointIds();
		for(var z = 0; z < endpoints.length; z++)
		{
			var _endpointId = endpoints[z];
			var _endpointName = remote.getEndpointName(_endpointId);
			var _endpointDescription = remote.getEndpointDescription(_endpointId);
			addElementSelectionValue("endpointId", _endpointId, _endpointDescription);
		}
		updateElement("endpointId", endpointId);
		
	}
}

// call the finalize method
finalize();
