<import resource="/org/alfresco/web/site/include/ads-support.js">


// set up form elements
wizard.addElement("xformId", "");
wizard.addElement("formatId", "");
wizard.addElement("templateId", "");
wizard.addElementFormat("xformId", "Form", "combo", 220);
wizard.addElementFormat("formatId", "Format", "combo", 220);
wizard.addElementFormat("templateId", "Template", "combo", 220);



//
// XFORM dropdown
//
wizard.addElementFormatKeyPair("xformId", "emptyText", "Please select a Form");
wizard.addElementFormatKeyPair("xformId", "title", "Forms");

var xformIds = getNonSystemContentFormNames();
for(var i = 0; i < xformIds.length; i++)
{
	var xId = xformIds[i];
	var xName = getContentFormTitle(xId);
	wizard.addElementSelectionValue("xformId", xId, xName);
}





//
// FORMAT ID dropdown
//
wizard.addElementFormatKeyPair("formatId", "emptyText", "Please select a Format ID");
wizard.addElementFormatKeyPair("formatId", "title", "Formats");
/*
var formats = site.getFormatIds();
for(var i = 0; i < formats.length; i++)
{
	var formatId = formats[i];
	var formatName = site.getFormatName(formatId);
	wizard.addElementSelectionValue("formatId", formatId, formatName);
}
var defaultFormatId = site.getDefaultFormatId();
wizard.updateElement("formatId", defaultFormatId);
*/





//
// TEMPLATE ID dropdown
//

wizard.addElementFormatKeyPair("templateId", "emptyText", "Please select a Template");
wizard.addElementFormatKeyPair("templateId", "title", "Templates");
var templates = site.getTemplates();
for(var i = 0; i < templates.length; i++)
{
	var templateId = templates[i].getProperty("id");
	var templateName = templates[i].getProperty("name");
	wizard.addElementSelectionValue("templateId", templateId, templateName);
}
