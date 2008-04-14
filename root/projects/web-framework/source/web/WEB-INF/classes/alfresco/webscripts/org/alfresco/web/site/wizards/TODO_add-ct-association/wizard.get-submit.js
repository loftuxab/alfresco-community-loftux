<import resource="/org/alfresco/web/site/include/ads-support.js">


var completed = false;
if(xformId != null && templateId != null && formatId != null)
{
	site.associateContent(xformId, templateId, formatId);
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

// finalize things
wizard.setResponseCodeFinish();
