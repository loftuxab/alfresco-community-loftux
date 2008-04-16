<import resource="/org/alfresco/web/site/include/ads-support.js">


// incoming template id
var templateId = wizard.request("templateId");
if(templateId == "")
	templateId = null;
	
if(templateId != null)
{
	var template = site.getObject(templateId);

	// default values
	var templateName = template.getProperty("name");
	if(templateName == null)
		templateName = "";
	var templateDescription = template.getProperty("description");
	if(templateDescription == null)
		templateDescription = "";
	
	wizard.setResponseTitle("Edit Template: " + templateName);

	// set up form elements
	wizard.addHiddenElement("templateId", templateId);
	wizard.addElement("templateName", templateName);
	wizard.addElement("templateDescription", templateDescription);
	wizard.addElementFormat("templateName", "Name", "textfield", 290);
	wizard.addElementFormat("templateDescription", "Description", "textarea", 290);
}

