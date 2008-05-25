<import resource="/org/alfresco/web/site/include/ads-support.js">


// incoming
var pageId = wizard.request("pageId");
var formatId = wizard.request("formatId");
if(formatId == null)
	formatId = "default"; // TODO CHANGE THIS

// load the page
var page = sitedata.getObject(pageId);

// remove the template
var completed = false;
if(page != null)
{
	sitedata.removeTemplate(pageId, formatId);
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

