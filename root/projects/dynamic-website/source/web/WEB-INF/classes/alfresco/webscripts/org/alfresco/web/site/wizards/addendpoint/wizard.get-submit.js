<import resource="/org/alfresco/web/site/include/ads-support.js">



var okay = true;
var endpoints = site.getEndpoints();
for(var x = 0; x < endpoints.length; x++)
{
	var _endpointId = endpoints[x].getProperty("endpointId");
	if(_endpointId != null && _endpointId == endpointId)
		okay = false;
}

wizard.setResponseMessage("Successfully created endpoint.");

if(okay)
{
	var endpoint = site.newEndpoint();
	
	endpoint.setProperty("endpoint-id", endpointId);
	
	endpoint.setProperty("connector-id", connectorId);
	endpoint.setProperty("endpoint-url", endpointUrl);
	endpoint.setProperty("default-uri", defaultUri);
	endpoint.setProperty("auth-id", authId);
	endpoint.setProperty("identity", identity);

	endpoint.setProperty("username", username);
	endpoint.setProperty("password", password);
	
	save(endpoint);
}
else
{
	wizard.setResponseMessage("Unable to create endpoint - duplicate endpoint id");
}

// finalize things
wizard.setResponseCodeFinish();
