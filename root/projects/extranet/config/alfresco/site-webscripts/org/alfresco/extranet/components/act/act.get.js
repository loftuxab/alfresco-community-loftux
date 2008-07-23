var endpoint = "support.alfresco.com";
model.endpoint = endpoint;

if(sitedata.getCredentialVault().hasCredentials(endpoint))
{
	// they have some credentials, so lets try to proxy (SSO)
	model.endpointUrl = url.context + "/proxy/" + endpoint + "";
}
else
{
	// they do not have credentials, so lets go direct
	model.endpointUrl = remote.getEndpointURL("support.alfresco.com");
	if(model.endpointUrl == null)
	{
		model.endpointUrl = "http://support.alfresco.com";
	}
}
