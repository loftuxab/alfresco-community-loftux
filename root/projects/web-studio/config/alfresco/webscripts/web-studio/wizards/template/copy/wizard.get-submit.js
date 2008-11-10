<import resource="/include/support.js">

// get the existing template
var template = sitedata.getObject("template-instance", templateId);
if(template != null)
{
	// clone the template
	var newTemplate = template.clone();
	
	// set properties
	newTemplate.setProperty("title", templateName);
	newTemplate.setProperty("description", templateDescription);
	
	// save
	newTemplate.save();		
}

// finalize things
wizard.setResponseCodeFinish();