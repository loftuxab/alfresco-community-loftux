<import resource="/org/alfresco/web/site/include/ads-support.js">


// inputs
var componentId = wizard.request("componentId");
var component = site.getObject(componentId);

// process
if(component != null)
{
	var mediaType = wizard.getSafeSetting(component, "mediaType");
	var mediaUrl = wizard.getSafeSetting(component, "mediaUrl");
	var unsupportedText = wizard.getSafeSetting(component, "unsupportedText");
	var width = wizard.getSafeSetting(component, "width");
	var height = wizard.getSafeSetting(component, "height");
	
	// the controls
	wizard.addElement("mediaType", mediaType);
	wizard.addElement("mediaUrl", mediaUrl);
	wizard.addElement("unsupportedText", unsupportedText);
	wizard.addElement("width", width);
	wizard.addElement("height", height);
	
	wizard.addElementFormat("mediaType", "Media Type", "combo", 290);
	wizard.addElementFormat("mediaUrl", "URL", "textfield", 290);
	wizard.addElementFormat("unsupportedText", "Unsupported Text", "textarea", 290);
	wizard.addElementFormat("width", "Width", "textfield", 290);	
	wizard.addElementFormat("height", "Height", "textfield", 290);	
	
	//
	// media types
	//
	wizard.addElementFormatKeyPair("mediaType", "title", "Media Type");
	
	// video
	wizard.addElementSelectionValue("mediaType", "video", "Video");
	wizard.addElementSelectionValue("mediaType", "audio", "Audio");
	wizard.addElementSelectionValue("mediaType", "pdf", "PDF");
	wizard.addElementSelectionValue("mediaType", "flash", "Flash");
}

