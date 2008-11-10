<import resource="/include/support.js">

// get the existing template
var completed = false;
var template = sitedata.getObject("template-instance", templateId);
if(template != null)
{	
	// do updates
	template.setProperty("title", templateName);
	template.setProperty("description", templateDescription);
    template.setProperty("template-layout-type", templateLayoutType);
    	
	// do saves
	save(template);
	completed = true;
}

if(completed)
{
	wizard.setResponseMessage("Successfully updated template!");
}
else
{
	wizard.setResponseMessage("Unable to update template");
}

// finalize things
wizard.setResponseCodeFinish();