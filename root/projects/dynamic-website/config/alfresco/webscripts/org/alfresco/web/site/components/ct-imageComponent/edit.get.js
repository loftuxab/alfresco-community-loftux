<import resource="/org/alfresco/web/site/include/ads-support.js">


// inputs
var componentId = wizard.request("componentId");
var component = site.getObject(componentId);

// process
if(component != null)
{
	var imageLocation = component.getProperty("imageLocation");
	if(imageLocation == null)
		imageLocation = "";			
	var width = component.getProperty("width");
	if(width == null)
		width = "";
	var height = component.getProperty("height");
	if(height == null)
		height = "";			
	var alt = component.getProperty("alt");
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

