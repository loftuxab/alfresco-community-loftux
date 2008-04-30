<import resource="/org/alfresco/web/site/include/ads-support.js">


// inputs
var componentId = wizard.request("componentId");
var component = site.getObject(componentId);

// process
if(component != null)
{
	var endpointId = wizard.getSafeSetting(component, "endpointId");
	var webscript = wizard.getSafeSetting(component, "webscript");
	var container = wizard.getSafeSetting(component, "container");

	// endpoint selector combo
	wizard.addElement("endpointId", endpointId);
	wizard.addElementFormat("endpointId", "Endpoint ID", "combo", 220);
	wizard.addElementSelectionValue("endpointId", "none", "None");
	var endpoints = site.getEndpoints();
	for(var z = 0; z < endpoints.length; z++)
	{
		var _endpointId = endpoints[z].getProperty("endpointId");
		var _endpointDescription = _endpointId;			
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

