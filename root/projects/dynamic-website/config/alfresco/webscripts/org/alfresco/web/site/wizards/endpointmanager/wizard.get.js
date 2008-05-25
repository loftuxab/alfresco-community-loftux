<import resource="/org/alfresco/web/site/include/ads-support.js">



wizard.addGridColumn("endpointId", "Endpoint ID");
wizard.addGridColumn("url", "URL");
wizard.addGridColumn("identity", "Identity");

wizard.addGridColumnFormat("endpointId", 100, true);
wizard.addGridColumnFormat("url", 420, true);
wizard.addGridColumnFormat("identity", 80, true);

wizard.addGridToolbar("add_endpoint", "New Endpoint", "New Endpoint", "add");
wizard.addGridToolbarSpacer();
wizard.addGridToolbar("edit_endpoint", "Edit Endpoint", "Edit Endpoint", "edit");
wizard.addGridToolbarSpacer();
wizard.addGridToolbar("remove_endpoint", "Remove Endpoint", "Remove Endpoint", "delete");

wizard.addGridNoDataMessage("There are no endpoints currently defined.");


// get all of the endpoints
var endpoints = sitedata.getEndpoints();
for(var i = 0; i < endpoints.length; i++)
{
	var endpointId = endpoints[i].getProperty("endpoint-id");

	// other data
	var endpointUrl = endpoints[i].getProperty("endpoint-url");
	var defaultUri = endpoints[i].getProperty("default-uri");
	if(defaultUri == null)
		defaultUri = "";
	var identity = endpoints[i].getProperty("identity");
	
	var array = new Array();
	array[0] = endpointId;
	array[1] = endpointUrl + defaultUri;
	array[2] = identity;
			
	wizard.addGridData(array);
}
