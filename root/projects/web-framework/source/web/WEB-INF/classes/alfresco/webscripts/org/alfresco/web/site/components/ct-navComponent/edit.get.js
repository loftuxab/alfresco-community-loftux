<import resource="/org/alfresco/web/site/include/ads-support.js">


// things we receive
var componentId = wizard.request("componentId");
var component = site.getObject(componentId);
if(component != null)
{
	var orientation = wizard.getSafeSetting(component, "orientation");
	var style = wizard.getSafeSetting(component, "style");

	// set up form elements
	wizard.addElement("orientation", "");
	wizard.addElement("style", "");
	wizard.addElementFormat("orientation", "Orientation", "combo", 220);
	wizard.addElementFormat("style", "Style", "combo", 320);

	//
	// orientation dropdown
	//
	wizard.addElementFormatKeyPair("orientation", "title", "Orientation");
	wizard.addElementSelectionValue("orientation", "horizontal", "Horizontal");
	wizard.addElementSelectionValue("orientation", "vertical", "Vertical");
	if(orientation != null)
		wizard.updateElement("orientation", orientation);


	//
	// style dropdown
	//
	wizard.addElementSelectionValue("style", "0", "Full navigation tree");
	wizard.addElementSelectionValue("style", "1", "Show children of current node");
	if(style != null)
		wizard.updateElement("style", style);
}

