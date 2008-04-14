<import resource="/org/alfresco/web/site/include/ads-support.js">

// incoming endpoint id
if(endpointObjectId != null && endpointObjectId != "")
{
	var endpoint = site.getObject(endpointObjectId);
	
	endpoint.setProperty("endpointId", endpointId);
	endpoint.setSetting("protocol", protocol);
	endpoint.setSetting("host", host);
	endpoint.setSetting("port", port);
	endpoint.setSetting("uri", uri);
	endpoint.setSetting("credentials", credentials);
	endpoint.setSetting("authentication", authentication);
	endpoint.setSetting("username", username);
	endpoint.setSetting("password", password);
	save(endpoint);

	wizard.setResponseMessage("Successfully updated endpoint.");
}
else
{
	wizard.setResponseMessage("Unable to find endpoint");
}

// finalize things
wizard.setResponseCodeFinish();
