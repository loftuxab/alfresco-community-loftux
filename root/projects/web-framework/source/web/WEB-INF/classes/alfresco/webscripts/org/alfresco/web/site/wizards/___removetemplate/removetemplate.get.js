<import resource="/Company Home/Data Dictionary/Web Scripts Extensions/org/alfresco/website/include/dialog-support.js">
<import resource="/Company Home/Data Dictionary/Web Scripts Extensions/org/alfresco/website/include/avm-support.js">
<import resource="/Company Home/Data Dictionary/Web Scripts Extensions/org/alfresco/website/include/ads-support.js">


// set up buttons
addButton("Remove", "_submit", true);
addButton("Cancel", "cancel", true);

// set up states
addState("_submit", "/ads/wizard/removetemplate/_submit", true);

// set the page title
setResponseTitle("Remove Template");



// incoming template id
var templateId = requestJSON["templateId"];
if(templateId == "")
	templateId = null
	
if(templateId != null)
{
	var template = site.getObject(templateId);

	// default values
	var templateName = template.getProperty("name");
	var templateDescription = template.getProperty("description");
	var templateLayoutId = template.getProperty("layoutId");
	
	setResponseTitle("Remove Template: " + templateName);

	setDialogHTML("Are you sure that you want to remove the following template?<br/><br/><b>"+templateName+"</b><br/>"+templateDescription+"<br/>");
}

// call the finalize method
finalize();

