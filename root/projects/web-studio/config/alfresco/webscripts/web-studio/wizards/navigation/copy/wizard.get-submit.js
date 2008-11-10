<import resource="/include/support.js">

// get the page
var originalPage = sitedata.getPage(pageId);

// clone the page
var newPage = originalPage.clone();

// set properties
newPage.setTitle(pageName);
newPage.setDescription(pageDescription);

// save
newPage.save();

// look up if there is a parent-child association
var assocs = sitedata.findChildPageAssociations(parentId, pageId);
for(var i = 0; i < assocs.length; i++)
{
	// clone the association
	var newAssociation = assocs[i].clone();
	newAssociation.setProperty("dest-id", newPage.getId());
	newAssociation.save();
}

// finalize things
wizard.setResponseCodeFinish();
