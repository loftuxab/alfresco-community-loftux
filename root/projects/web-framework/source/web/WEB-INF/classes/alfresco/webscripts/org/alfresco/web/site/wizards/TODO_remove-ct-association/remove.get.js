<import resource="/Company Home/Data Dictionary/Web Scripts Extensions/org/alfresco/website/include/dialog-support.js">
<import resource="/Company Home/Data Dictionary/Web Scripts Extensions/org/alfresco/website/include/avm-support.js">
<import resource="/Company Home/Data Dictionary/Web Scripts Extensions/org/alfresco/website/include/ads-support.js">


// set up buttons
addButton("Remove", "_submit", true);
addButton("Cancel", "cancel", true);

// set up states
addState("_submit", "/ads/wizard/remove-ct-association/_submit", true);

// set the page title
setResponseTitle("Remove Template Association");



// incoming
var associationId = requestJSON["associationId"];
if(associationId != null)
{
	var association = site.getObject(associationId);

	// default values
	var templateId = association.getProperty("templateId");
	var formatId = association.getProperty("formatId");
	var xformId = association.getProperty("sourceId");
	
	var template = site.getObject(templateId);
	var templateName = template.getProperty("name");
	var templateDescription = template.getProperty("description");
	
	setResponseTitle("Remove Association for " + xformId + " (" + formatId + ")");

	setDialogHTML("Are you sure that you want to remove the following template association?<br/><br/><b>"+templateName+"</b><br/>"+templateDescription+"<br/>");
}

// call the finalize method
finalize();

