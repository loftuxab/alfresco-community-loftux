<import resource="/org/alfresco/web/site/include/ads-support.js">


// inputs
var componentId = wizard.request("componentId");
var component = sitedata.getObject("component", componentId);

// process
if(component != null)
{
	var endpointId = wizard.getSafeProperty(component, "endpointId");
	var webscript = wizard.getSafeProperty(component, "webscript");
	var container = wizard.getSafeProperty(component, "container");

	// endpoint selector combo
	wizard.addElement("endpointId", endpointId);
	wizard.addElementFormat("endpointId", "Endpoint ID", "combo", 220);
	wizard.addElementSelectionValue("endpointId", "none", "None");
	var endpoints = remote.getEndpointIds();
	for(var z = 0; z < endpoints.length; z++)
	{
		var _endpointId = endpoints[z];
		var _endpointName = remote.getEndpointName(_endpointId);
		var _endpointDescription = remote.getEndpointDescription(_endpointId);	
		wizard.addElementSelectionValue("endpointId", _endpointId, _endpointDescription);
	}
	wizard.updateElement("endpointId", endpointId);

	// web script uri		
	wizard.addElement("webscript", webscript);
	wizard.addElementFormat("webscript", "Web Script URI", "textfield", 290);

	// container
	wizard.addElement("container", container);
	wizard.addElementFormat("container", "Container", "combo", 220);
	wizard.addElementSelectionValue("container", "div", "DIV");
	wizard.addElementSelectionValue("container", "iframe", "IFrame");
	wizard.updateElement("container", container);
}

