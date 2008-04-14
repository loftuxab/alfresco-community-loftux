<import resource="/org/alfresco/web/site/include/ads-support.js">


// incoming endpoint id
endpointId = wizard.request("endpointId");
if(endpointId == "")
	endpointId = null;

if(endpointId != null)
{
	// find the endpoint object
	var endpoint = site.findEndpoint(endpointId);
	if(endpoint != null)
	{
		// data from the object
		var connectorId = wizard.getSafeProperty(endpoint, "connector-id");
		var endpointUrl = wizard.getSafeProperty(endpoint, "endpoint-url");
		var defaultUri = wizard.getSafeProperty(endpoint, "default-uri");

		// add the endpoint object id
		wizard.addHiddenElement("endpointObjectId", endpoint.getProperty("id"));

		// set up form elements
		wizard.addElement("endpointId", endpointId);
		wizard.addElement("connectorId", connectorId);
		wizard.addElement("endpointUrl", endpointUrl);
		wizard.addElement("defaultUri", defaultUri);

		wizard.addElementFormat("endpointId", "Identifier", "textfield", 220);
		wizard.addElementFormat("connectorId", "Connector", "combo", 220);
		wizard.addElementFormat("endpointUrl", "URL", "textfield", 220);
		wizard.addElementFormat("defaultUri", "Service Uri", "textfield", 220);

		//
		// Connector dropdown
		//
		wizard.addElementSelectionValue("connectorId", "http", "HTTP Connector");
		wizard.addElementSelectionValue("connectorId", "web", "Web Connector");
		wizard.addElementSelectionValue("connectorId", "alfresco", "Alfresco Connector");
		wizard.updateElement("connectorId", connectorId);
	}
}

