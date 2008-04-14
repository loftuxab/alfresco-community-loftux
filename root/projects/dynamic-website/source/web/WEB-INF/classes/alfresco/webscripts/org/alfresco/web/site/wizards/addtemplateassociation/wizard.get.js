<import resource="/org/alfresco/web/site/include/ads-support.js">


// incomings
var pageId = wizard.request("pageId");
var page = site.getObject(pageId);

var templateTypes = site.getTemplateTypes();

// set up form elements
wizard.addHiddenElement("pageId", pageId);
wizard.addElement("formatId", "");
wizard.addElement("templateTypeId", "");

wizard.addElementFormat("formatId", "Format", "combo", 220);
wizard.addElementFormat("templateTypeId", "Template Type", "combo", 220);



//
// FORMAT ID dropdown
//

wizard.addElementFormatKeyPair("formatId", "emptyText", "Please select a Format ID");
wizard.addElementFormatKeyPair("formatId", "title", "Formats");
wizard.addElementSelectionValue("formatId", "default", "Default");
wizard.addElementSelectionValue("formatId", "print", "Print");
wizard.addElementSelectionValue("formatId", "wap", "Wireless (WAP)");
//wizard.updateElement("formatId", "default");





//
// TEMPLATE TYPE ID dropdown
//

wizard.addElementFormatKeyPair("templateTypeId", "title", "Template Types");
for(var i = 0; i < templateTypes.length; i++)
{
	var templateTypeId = templateTypes[i].getProperty("id");
	var templateTypeName = templateTypes[i].getProperty("name");
	wizard.addElementSelectionValue("templateTypeId", templateTypeId, templateTypeName);
}
//if(templateTypes.length > 0)
//	wizard.updateElement("templateTypeId", templateTypes[0].getProperty("id"));

