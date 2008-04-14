<import resource="/org/alfresco/web/site/include/ads-support.js">


// inputs
var componentId = wizard.request("componentId");
var component = site.getObject(componentId);

// process
if(component != null)
{
	var markupData = wizard.getSafeSetting(component, "markupData");
	markupData = site.decode(markupData);

	// the controls
	markupData = site.encode(markupData);
	wizard.addElement("markupData", markupData);
	wizard.addElementFormat("markupData", null, "textarea", 460, 210);
}

