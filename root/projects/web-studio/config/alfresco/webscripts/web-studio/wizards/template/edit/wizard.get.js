<import resource="/include/support.js">

// incoming template id
var templateId = wizard.request("templateId");
if(templateId == "")
	templateId = null;
	
if(templateId != null)
{
	var template = sitedata.getObject("template-instance", templateId);

	// default values
	var templateName = template.getTitle();	
	if(templateName == null)
	{
		templateName = "";
	}
		
	var templateDescription = template.getDescription();	
	if(templateDescription == null)
	{
		templateDescription = "";
	}
	
	var templateLayoutType = template.getProperty("template-layout-type");
	if(templateLayoutType == null)
	{
		templateLayoutType = "";
	}

	var templateHeight = template.getProperty("height");
	if(templateHeight == null)
	{
		templateHeight = "";
	}
	
	var templateWidth = template.getProperty("width");
	if(templateWidth == null)
	{
		templateWidth = "";
	}	
	
	
	// Form Title
	wizard.setResponseTitle("Edit Template: " + templateName);
	
	// Form Element: template id
	wizard.addHiddenElement("templateId", templateId);
	
	// Form Element: template name
	wizard.addElement("templateName", templateName);
	wizard.addElementFormat("templateName", "Name", "textfield", 290);
	
	// Form Element: template description	
	wizard.addElement("templateDescription", templateDescription);	
	wizard.addElementFormat("templateDescription", "Description", "textarea", 290);

	// Form Element: template height	
	wizard.addElement("templateHeight", templateHeight);	
	wizard.addElementFormat("templateHeight", "Height", "textfield", 5);

	// Form Element: template description	
	wizard.addElement("templateWidth", templateWidth);	
	wizard.addElementFormat("templateWidth", "Width", "textfield", 5);	
	
	// Form Element: template layout type
	wizard.addElement("templateLayoutType", templateLayoutType);
	wizard.addElementFormat("templateLayoutType", "Layout", "combo", 290);	
	wizard.addElementFormatKeyPair("templateLayoutType", "emptyText", "Choose...");
	wizard.addElementFormatKeyPair("templateLayoutType", "title", "Layout Types");

	// options	
	wizard.addElementSelectionValue("templateLayoutType", "Absolute Positioning", "Absolute Positioning");
	wizard.addElementSelectionValue("templateLayoutType", "Table Layout", "Table Layout");	

	// set to value	
	wizard.updateElement("templateLayoutType", templateLayoutType);
}

