<import resource="/include/support.js">

// incoming
var contentId = wizard.request("contentId");
var assocType = wizard.request("assocType");
var templateId = wizard.request("templateId");
var formatId = wizard.request("formatId");

var completed = false;
if(assocType == "content")
{
	sitedata.unassociateContent(contentId, templateId, formatId);
	completed = true;
}
if(assocType == "content-type")
{
	sitedata.unassociateContentType(contentId, templateId, formatId);
	completed = true;
}

if(completed)
{
	wizard.setResponseMessage("Successfully removed association!");
}
else
{
	wizard.setResponseMessage("Unable to remove association");
}

// finalize things
wizard.setResponseCodeFinish();

