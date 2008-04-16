<import resource="/org/alfresco/web/site/include/ads-support.js">


// incomings
var endpointId = wizard.request("endpointId");
if(endpointId != null)
{
	var endpoint = site.findEndpoint(endpointId);
	if(endpoint != null)
	{
		wizard.setResponseTitle("Remove endpoint: " + endpointId);
		wizard.setDialogHTML("Are you sure that you want to remove this endpoint?");
	}
}


