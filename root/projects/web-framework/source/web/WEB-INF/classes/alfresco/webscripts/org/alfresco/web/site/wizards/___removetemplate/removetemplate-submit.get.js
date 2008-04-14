<import resource="/Company Home/Data Dictionary/Web Scripts Extensions/org/alfresco/website/include/dialog-support.js">
<import resource="/Company Home/Data Dictionary/Web Scripts Extensions/org/alfresco/website/include/avm-support.js">
<import resource="/Company Home/Data Dictionary/Web Scripts Extensions/org/alfresco/website/include/ads-support.js">

// elements that we're interested in
var completed = false;
var templateId = requestJSON["templateId"];
if(templateId != null)
{
	// we need to remove the template, the layout (1-1)	
	var template = site.getObject(templateId);
	var layout = site.getObject(template.getProperty("layoutId"));
	var i = 0;
	
	// find any page associations
	var pageAssociations = site.findPageAssociations(templateId, null, null);
	for(i = 0; i < pageAssociations.length; i++)
	{
		var page = site.getObject(pageAssociations[i].getProperty("pageId"));
		
		// remove page object
		remove(page);
		
		// remove page association object
		remove(pageAssociations[i]);		
	}
	
	// find any template associations
	var templateAssociations = site.findTemplateAssociations(null, templateId, null);
	for(i = 0; i < templateAssociations.length; i++)
	{
		// clean up template association
		remove(templateAssociations[i]);
	}
	
	// final removes
	remove(template);
	remove(layout);
	
	completed = true;
}

if(completed)
{
	setResponseMessage("Successfully removed template!");
}
else
{
	setResponseMessage("Unable to remove template");
}

// finalize things
setResponseCodeFinish();

finalize();
