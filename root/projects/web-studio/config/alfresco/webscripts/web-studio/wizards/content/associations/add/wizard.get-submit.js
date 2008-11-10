<import resource="/include/support.js">

var completed = false;

if(contentId != null && assocType != null && templateId != null && formatId != null)
{
	if(assocType == "content-type")
	{
		sitedata.associateContentType(contentId, templateId, formatId);
		completed = true;
	}
	if(assocType == "content")
	{
		sitedata.associateContent(contentId, templateId, formatId);
		completed = true;
	}
}

if(completed)
{
	wizard.setResponseMessage("The template was successfully associated!");
}
else
{
	wizard.setResponseMessage("Unable to associate the template");
}

// finalize things
wizard.setResponseCodeFinish();
