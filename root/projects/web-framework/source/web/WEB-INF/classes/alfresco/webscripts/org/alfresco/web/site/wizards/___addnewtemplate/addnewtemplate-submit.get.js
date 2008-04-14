<import resource="/Company Home/Data Dictionary/Web Scripts Extensions/org/alfresco/website/include/dialog-support.js">
<import resource="/Company Home/Data Dictionary/Web Scripts Extensions/org/alfresco/website/include/avm-support.js">
<import resource="/Company Home/Data Dictionary/Web Scripts Extensions/org/alfresco/website/include/ads-support.js">

// elements that we're interested in
var templateName = getElementValue("templateName");
if(templateName == "")
	templateName = null
var templateDescription = getElementValue("templateDescription");
if(templateDescription == null)
	templateDescription = "";
var layoutTypeId = getElementValue("templateLayoutTypeId");
if(layoutTypeId == "")
	layoutTypeId = null;
logger.log(layoutTypeId);
	
var completed = false;
if(templateName != null && layoutTypeId != null)
{
	// get the layout type
	var layoutType = site.getObject(layoutTypeId);
	if(layoutType != null)
	{
		// create layout
		var layout = site.newLayout();
		layout.setProperty("layoutTypeId", layoutTypeId);
		save(layout);
		
		// create the template
		var template = site.newTemplate();
		template.setProperty("name", templateName);
		if(templateDescription != null)
			template.setProperty("description", templateDescription);
		template.setProperty("layoutId", layout.getProperty("id"));
		save(template);

		completed = true;
	}
}

if(completed)
{
	setResponseMessage("Successfully create new template!");
}
else
{
	setResponseMessage("Unable to create new template");
}

// finalize things
setResponseCodeFinish();

finalize();
