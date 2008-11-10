<import resource="/include/support.js">

// set up form elements
wizard.addHiddenElement("assocType", "content-type");

wizard.addElement("contentId", "");
//wizard.addElement("assocType", "");
wizard.addElement("formatId", "");
wizard.addElement("templateId", "");
wizard.addElementFormat("contentId", "Content ID", "textfield", 290);
//wizard.addElementFormat("assocType", "Association Type", "combo", 220);
wizard.addElementFormat("formatId", "Format", "combo", 220);
wizard.addElementFormat("templateId", "Template", "combo", 220);


//
// Association Type
//
//wizard.addElementFormatKeyPair("assocType", "title", "Association Type");
//wizard.addElementSelectionValue("assocType", "content-type", "Content Type");
//wizard.addElementSelectionValue("assocType", "content", "Content");


//
// Format ID
//
//wizard.addElementFormatKeyPair("formatId", "emptyText", "Please select a Format ID");
wizard.addElementFormatKeyPair("formatId", "title", "Formats");
wizard.addElementSelectionValue("formatId", "default", "Default Format");
wizard.addElementSelectionValue("formatId", "wireless", "Wireless Format");
wizard.addElementSelectionValue("formatId", "print", "Print Format");


//
// Template ID
//
//wizard.addElementFormatKeyPair("templateId", "emptyText", "Please select a Template");
wizard.addElementFormatKeyPair("templateId", "title", "Templates");
var templates = sitedata.getTemplates();
for(var i = 0; i < templates.length; i++)
{
	var templateId = templates[i].getId();
	var templateTitle = templates[i].getTitle();
	wizard.addElementSelectionValue("templateId", templateId, templateTitle);
}
