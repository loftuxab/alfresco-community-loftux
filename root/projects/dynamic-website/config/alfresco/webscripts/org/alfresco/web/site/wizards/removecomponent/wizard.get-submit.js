<import resource="/org/alfresco/web/site/include/ads-support.js">

// things we receive
var componentId = wizard.request("componentId");
if(componentId != null)
{
	var component = sitedata.getObject(componentId);
	if(component != null)
	{
		sitedata.unassociateComponent(componentId);
	}
}

wizard.setResponseMessage("The component was successfully unassociated");
wizard.setBrowserReload(true);	

wizard.setResponseCodeFinish();

