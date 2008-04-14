<import resource="/org/alfresco/web/site/include/ads-support.js">


// incoming endpoint id
endpointId = wizard.request("endpointId");
if(endpointId == "")
	endpointId = null;

if(endpointId != null)
{
	// find the endpoint object
	var endpoint = site.findEndpoint(endpointId);
	logger.log("ENDPO: " + endpoint);
	if(endpoint != null)
	{
		// data from the object
		var protocol = endpoint.getSetting("protocol");
		if(protocol == null)
			protocol = "";
		var host = endpoint.getSetting("host");
		if(host == null)
			host = "";
		var port = endpoint.getSetting("port");
		if(port == null)
			port = "";
		var uri = endpoint.getSetting("uri");
		if(uri == null)
			uri = "";

		// add the endpoint object id
		wizard.addHiddenElement("endpointObjectId", endpoint.getProperty("id"));

		// set up form elements
		wizard.addElement("endpointId", endpointId);
		wizard.addElement("protocol", protocol);
		wizard.addElement("host", host);
		wizard.addElement("port", port);
		wizard.addElement("uri", uri);

		wizard.addElementFormat("endpointId", "Identifier", "textfield", 220);
		wizard.addElementFormat("protocol", "Protocol", "combo", 120);
		wizard.addElementFormat("host", "Host", "textfield", 220);
		wizard.addElementFormat("port", "Port", "textfield", 120);
		wizard.addElementFormat("uri", "URI", "textfield", 220);

		//
		// Protocol dropdown
		//
		wizard.addElementSelectionValue("protocol", "http", "HTTP");
		wizard.addElementSelectionValue("protocol", "https", "HTTPS");
		wizard.updateElement("protocol", protocol);
	}
}

