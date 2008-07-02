<import resource="/org/alfresco/web/site/include/ads-support.js">


// inputs
var componentId = wizard.request("componentId");
var component = sitedata.getObject("component", componentId);

// process
if(component != null)
{
	var uri = component.getProperty("uri");
	if(uri == null)
		uri = "";			

	// the controls
	wizard.addElement("uri", uri);
	wizard.addElementFormat("uri", "URI", "textfield", 290);
}

