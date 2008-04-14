<import resource="/org/alfresco/web/site/include/ads-support.js">


// binding: pageId
// binding: formatId
// binding: templateTypeId


var page = site.getObject(pageId);
var templateType = site.getObject(templateTypeId);

logger.log("pageId: " + pageId);
logger.log("formatId: " + formatId);
logger.log("templateTypeId: " + templateTypeId);
	
var completed = false;
if(pageId != null && templateTypeId != null && formatId != null)
{
	// create a new template
	var template = site.newTemplate();
	template.setProperty("template-type", templateTypeId);
	template.save();
	
	// associate template to page
	var templateId = template.getId();
	site.associateTemplate(templateId, pageId, formatId);
	completed = true;
}

if(completed)
{
	wizard.setResponseMessage("The template was successfully associated!");
}
else
{
	wizard.setResponseMessage("Unable to associate the template");
}

wizard.setResponseCodeFinish();
