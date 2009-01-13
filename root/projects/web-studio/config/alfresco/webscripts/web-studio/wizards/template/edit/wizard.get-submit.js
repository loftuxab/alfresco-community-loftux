<import resource="/include/support.js">

// incoming template id
var templateId = wizard.request("templateId");

// get the existing template
var completed = false;
var template = sitedata.getObject("template-instance", templateId);
if(template != null)
{	
	// do updates
	if(templateName != null && templateName != "")
	{
		template.setProperty("title", templateName);
	}
	if(templateDescription != null && templateDescription != "")
	{
		template.setProperty("description", templateDescription);
	}
	if(templateLayoutType != null && templateLayoutType != "")
	{
    	template.setProperty("template-layout-type", templateLayoutType);
    }
    if(templateHeight != null && templateHeight != "")
    {
    	template.setProperty("height", templateHeight);
    }
    if(templateWidth != null && templateWidth != "")
    {
    	template.setProperty("width", templateWidth);
    }    
    	
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