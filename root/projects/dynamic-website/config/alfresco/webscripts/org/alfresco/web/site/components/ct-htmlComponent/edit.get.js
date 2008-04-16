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
	wizard.addElement("markupData", markupData);
	wizard.addElementFormat("markupData", null, "htmleditor", 460, 210);
}

