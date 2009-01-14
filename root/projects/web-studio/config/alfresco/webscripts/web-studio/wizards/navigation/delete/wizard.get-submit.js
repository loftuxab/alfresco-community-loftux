<import resource="/include/support.js">

// inputs
var parentId = wizard.request("parentId");
var pageId = wizard.request("pageId");

var success = false;

// get the page
var page = sitedata.getObject("page", pageId);
if(page != null)
{
	// check whether the page has children
	var children = sitedata.findChildPages(page);
	if(children.length == 0)
	{
		// remove child page
		removeChildPage(parentId, pageId, false);
		success = true;
	}
}

if(!success)
{
	wizard.setResponseMessage("Unable to remove page unless all child pages are first removed.");
}

// finalize things
wizard.setResponseCodeFinish();
