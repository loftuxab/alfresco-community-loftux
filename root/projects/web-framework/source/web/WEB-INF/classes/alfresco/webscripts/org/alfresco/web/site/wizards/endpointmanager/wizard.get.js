<import resource="/org/alfresco/web/site/include/ads-support.js">



wizard.addGridColumn("endpointId", "Endpoint ID");
wizard.addGridColumn("host", "Host");
wizard.addGridColumn("port", "Port");
wizard.addGridColumn("uri", "URI");

wizard.addGridColumnFormat("endpointId", 120, true);
wizard.addGridColumnFormat("host", 120, true);
wizard.addGridColumnFormat("port", 60, false);
wizard.addGridColumnFormat("uri", 220, true);

wizard.addGridToolbar("add_endpoint", "New Endpoint", "New Endpoint", "add");
wizard.addGridToolbarSpacer();
wizard.addGridToolbar("edit_endpoint", "Edit Endpoint", "Edit Endpoint", "edit");
wizard.addGridToolbarSpacer();
wizard.addGridToolbar("remove_endpoint", "Remove Endpoint", "Remove Endpoint", "delete");

wizard.addGridNoDataMessage("There are no endpoints currently defined.");


// get all of the endpoints
var endpoints = site.getEndpoints();
for(var i = 0; i < endpoints.length; i++)
{
	var endpointId = endpoints[i].getProperty("endpoint-id");

	// other data
	var host = endpoints[i].getSetting("host");
	var port = endpoints[i].getSetting("port");
	var uri = endpoints[i].getSetting("uri");
	
	var array = new Array();
	array[0] = endpointId;
	array[1] = host;
	array[2] = port;
	array[3] = uri;
			
	wizard.addGridData(array);
}
