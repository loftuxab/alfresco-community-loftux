<import resource="/Company Home/Data Dictionary/Web Scripts Extensions/org/alfresco/website/include/dialog-support.js">
<import resource="/Company Home/Data Dictionary/Web Scripts Extensions/org/alfresco/website/include/avm-support.js">
<import resource="/Company Home/Data Dictionary/Web Scripts Extensions/org/alfresco/website/include/ads-support.js">

// elements that we're interested in
var completed = false;
var associationId = requestJSON["associationId"];
if(associationId != null)
{
	var association = site.getObject(associationId);
	remove(association);
	completed = true;
}

if(completed)
{
	setResponseMessage("Successfully removed association!");
}
else
{
	setResponseMessage("Unable to remove association");
}

// finalize things
setResponseCodeFinish();

finalize();
