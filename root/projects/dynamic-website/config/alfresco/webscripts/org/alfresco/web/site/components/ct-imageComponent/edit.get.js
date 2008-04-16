<import resource="/org/alfresco/web/site/include/ads-support.js">


// inputs
var componentId = wizard.request("componentId");
var component = site.getObject(componentId);

// process
if(component != null)
{
	var imageLocation = component.getSetting("imageLocation");
	if(imageLocation == null)
		imageLocation = "";			
	var width = component.getSetting("width");
	if(width == null)
		width = "";
	var height = component.getSetting("height");
	if(height == null)
		height = "";			
	var alt = component.getSetting("alt");
	if(alt == null)
		alt = "";

	// the controls
	wizard.addElement("imageURL", imageLocation);
	wizard.addElement("imageWidth", width);
	wizard.addElement("imageHeight", height);
	wizard.addElement("imageAlt", alt);
	wizard.addElementFormat("imageURL", "Image URL", "textfield", 290);
	wizard.addElementFormat("imageWidth", "Width", "textfield", 290);
	wizard.addElementFormat("imageHeight", "Height", "textfield", 290);
	wizard.addElementFormat("imageAlt", "Alt Text", "textfield", 290);	
}

