<import resource="/include/support.js">

// incomings
var pageId = wizard.request("pageId");
var page = sitedata.getObject("page", pageId);

// get the template instances
var templates = sitedata.getTemplates();

// set up form elements
wizard.addHiddenElement("pageId", pageId);
wizard.addElement("formatId", "");
wizard.addElement("templateId", "");
wizard.addElementFormat("formatId", "Format", "combo", 220);
wizard.addElementFormat("templateId", "Template", "combo", 220);

//
// FORMAT ID dropdown
//
wizard.addElementFormatKeyPair("formatId", "emptyText", "Please select a Format ID");
wizard.addElementFormatKeyPair("formatId", "title", "Formats");
wizard.addElementSelectionValue("formatId", "default", "Default");
wizard.addElementSelectionValue("formatId", "print", "Print");
wizard.addElementSelectionValue("formatId", "wap", "Wireless (WAP)");

//
// TEMPLATE ID dropdown
//
wizard.addElementFormatKeyPair("templateId", "title", "Templates");
for(var i = 0; i < templates.length; i++)
{
	var templateId = templates[i].getId();
	var templateTitle = templates[i].getTitle();
	if(templateTitle == null)
	{
		templateTitle = templates[i].getId();
	}
	
	wizard.addElementSelectionValue("templateId", templateId, templateTitle);
}

