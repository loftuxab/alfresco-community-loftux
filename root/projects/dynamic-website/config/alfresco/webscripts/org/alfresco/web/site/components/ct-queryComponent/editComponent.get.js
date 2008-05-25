<import resource="/Company Home/Data Dictionary/Web Scripts Extensions/org/alfresco/website/include/dialog-support.js">
<import resource="/Company Home/Data Dictionary/Web Scripts Extensions/org/alfresco/website/include/avm-support.js">
<import resource="/Company Home/Data Dictionary/Web Scripts Extensions/org/alfresco/website/include/ads-support.js">


// set up buttons
addButton("Cancel", "cancel", true);
addButton("Next", "_render", true);


// set up states
addState("_render", "/ads/components/ct-queryComponent/edit/_render", false);
addState("_submit", "/ads/components/ct-queryComponent/edit/_submit", true);

// set the page title
setResponseTitle("Query Component: Query Settings");

// things we receive
var componentAssociationId = requestJSON["componentAssociationId"];

// get the component's settings
// these are stored on the component
// todo: rework and expose the configuration manager concept through javascript

var componentAssociation = sitedata.getObject(componentAssociationId);
if(componentAssociation != null)
{
	var componentId = componentAssociation.getProperty("componentId");
	var component = sitedata.getObject(componentId);
	if(component != null)
	{
		var queryType = component.getProperty("queryType");
		if(queryType == null)
			queryType = "";

		// QUERY TYPE
		addElement("queryType", "");
		addElementFormat("queryType", "Type", "combo", 220);
		addElementSelectionValue("queryType", "ALFRESCO-LUCENE", "Alfresco (Lucene)");
		addElementSelectionValue("queryType", "ALFRESCO-OPENSEARCH", "Alfresco (OpenSearch)");
		addElementSelectionValue("queryType", "ICMS", "ICMS");		
		if(queryType != null)
			updateElement("queryType", queryType);

		// QUERY STRING
		var queryString = component.getProperty("queryString");
		if(queryString == null)
			queryString = "";
		queryString = escape(queryString);
		addElement("queryString", queryString);
		addElementFormat("queryString", "String", "textarea", 340);
		
		// ENDPOINT
		var endpointId = component.getProperty("endpointId");
		if(endpointId == null)
			endpointId = "alfresco-webuser";
		// endpoint selector combo
		addElement("endpointId", endpointId);
		addElementFormat("endpointId", "Endpoint ID", "combo", 220);
		addElementSelectionValue("endpointId", "none", "None");
		var endpoints = sitedata.getEndpointIds();
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
