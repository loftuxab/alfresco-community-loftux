<import resource="/include/support.js">


// binding: pageId
// binding: formatId
// binding: templateId

var page = sitedata.getObject("page", pageId);
var template = sitedata.getObject("template-instance", templateId);
	
var completed = false;
if(page != null && template != null && formatId != null)
{
	// associate template to page
	sitedata.associateTemplate(templateId, pageId, formatId);
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
