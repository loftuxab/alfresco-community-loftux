<import resource="/Company Home/Data Dictionary/Web Scripts Extensions/org/alfresco/website/include/dialog-support.js">
<import resource="/Company Home/Data Dictionary/Web Scripts Extensions/org/alfresco/website/include/avm-support.js">
<import resource="/Company Home/Data Dictionary/Web Scripts Extensions/org/alfresco/website/include/ads-support.js">


// set up buttons
addButton("Save", "_submit", true);
addButton("Cancel", "cancel", true);

// set up states
addState("_submit", "/ads/wizard/addnewtemplate/_submit", true);

// set the page title
setResponseTitle("Add New Template");

// default values
var templateName = "";
var templateDescription = "";
var templateLayoutTypeId = "";

// set up form elements
addElement("templateName", templateName);
addElement("templateDescription", templateDescription);
addElement("templateLayoutTypeId", templateLayoutTypeId);
addElementFormat("templateName", "Name", "textfield", 290);
addElementFormat("templateDescription", "Description", "textarea", 290);
addElementFormat("templateLayoutTypeId", "Layout", "combo", 290);

// additional formatting for the dropdown
addElementFormatKeyPair("templateLayoutTypeId", "title", "Layout Types");

// add selection values for drop down
var layoutTypes = site.getLayoutTypes();
for(var i = 0; i < layoutTypes.length; i++)	
{
	var layoutTypeId = layoutTypes[i].getProperty("id");
	var layoutTypeName = layoutTypes[i].getProperty("name");

	var valueString = layoutTypeName + " (" + layoutTypeId + ")";
	addElementSelectionValue("templateLayoutTypeId", layoutTypeId, valueString);
}



// call the finalize method
finalize();

