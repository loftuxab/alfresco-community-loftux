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
	endpoint.setEndpointId(endpointId);
	endpoint.setSetting("protocol", protocol);
	endpoint.setSetting("host", host);
	endpoint.setSetting("port", port);
	endpoint.setSetting("uri", uri);
	endpoint.setSetting("credentials", credentials);
	endpoint.setSetting("authentication", authentication);
	endpoint.setSetting("username", username);
	endpoint.setSetting("password", password);
	save(endpoint);
}
else
{
	wizard.setResponseMessage("Unable to create endpoint - duplicate endpoint id");
}

// finalize things
wizard.setResponseCodeFinish();
