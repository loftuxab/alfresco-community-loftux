<import resource="/org/alfresco/web/site/include/ads-support.js">


// inputs
var componentId = wizard.request("componentId");
var component = site.getObject(componentId);

// process
if(component != null)
{
	var markupData = wizard.getSafeProperty(component, "markupData");

	// the controls
	markupData = site.decode(markupData);
	wizard.addElement("markupData", markupData);
	wizard.addElementFormat("markupData", null, "textarea", 460, 210);
}

