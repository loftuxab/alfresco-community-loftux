<import resource="/org/alfresco/web/site/include/ads-support.js">


// elements that we're interested in
var completed = false;
var endpointId = wizard.request("endpointId");
if(endpointId != null)
{
	var endpoint = sitedata.findEndpoint(endpointId);
	if(endpoint != null)
	{
		remove(endpoint);
		completed = true;
	}
}

if(completed)
{
	wizard.setResponseMessage("Successfully removed endpoint!");
}
else
{
	wizard.setResponseMessage("Unable to remove endpoint");
}

// finalize things
wizard.setResponseCodeFinish();

