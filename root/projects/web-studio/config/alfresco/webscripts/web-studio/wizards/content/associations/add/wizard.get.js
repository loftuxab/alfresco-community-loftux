<import resource="/include/support.js">

// set up form elements
wizard.addHiddenElement("assocType", "content-type");

wizard.addElement("contentId", "");
wizard.addElement("formatId", "");
wizard.addElement("templateId", "");
wizard.addElementFormat("contentId", "Identifier", "textfield", 290);
wizard.addElementFormat("formatId", "Format", "combo", 220);
wizard.addElementFormat("templateId", "Template", "combo", 220);


//
// Format ID
//
wizard.addElementFormatKeyPair("formatId", "title", "Formats");
wizard.addElementSelectionValue("formatId", "default", "Default Format");
wizard.addElementSelectionValue("formatId", "wireless", "Wireless Format");
wizard.addElementSelectionValue("formatId", "print", "Print Format");


//
// Template ID
//
wizard.addElementFormatKeyPair("templateId", "title", "Templates");
var templates = sitedata.getTemplates();
for(var i = 0; i < templates.length; i++)
{
	var templateId = templates[i].getId();
	var templateTitle = templates[i].getTitle();
	if(templateTitle == null)
	{
		templateTitle = templateId;
	}
	wizard.addElementSelectionValue("templateId", templateId, templateTitle);
}
