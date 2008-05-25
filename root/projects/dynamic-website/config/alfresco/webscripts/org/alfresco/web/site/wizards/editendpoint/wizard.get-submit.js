<import resource="/org/alfresco/web/site/include/ads-support.js">

// incoming endpoint id
if(endpointObjectId != null && endpointObjectId != "")
{
	var endpoint = sitedata.getObject(endpointObjectId);
	
	endpoint.setProperty("endpoint-id", endpointId);
	
	endpoint.setProperty("connector-id", connectorId);
	endpoint.setProperty("endpoint-url", endpointUrl);
	endpoint.setProperty("default-uri", defaultUri);
	endpoint.setProperty("auth-id", authId);
	endpoint.setProperty("identity", identity);

	endpoint.setProperty("username", username);
	endpoint.setProperty("password", password);
	save(endpoint);

	wizard.setResponseMessage("Successfully updated endpoint.");
}
else
{
	wizard.setResponseMessage("Unable to find endpoint");
}

// finalize things
wizard.setResponseCodeFinish();
