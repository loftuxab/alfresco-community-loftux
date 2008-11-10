<import resource="/include/support.js">

// convert json string to javascript object
var inElements = wizard.request("elements");

var formElements = eval('(' + inElements + ')');

var templateId = wizard.request("templateId");

var templateId = wizard.request("templateId");
var regionName = getJsonArrayValue(formElements, "name", "regionName");		
var regionScope = getJsonArrayValue(formElements, "name", "regionScope");
var regionX = getJsonArrayValue(formElements, "name", "regionX");
var regionY = getJsonArrayValue(formElements, "name", "regionY");
var regionWidth = getJsonArrayValue(formElements, "name", "regionWidth");
var regionHeight = getJsonArrayValue(formElements, "name", "regionHeight");
var regionDescription = getJsonArrayValue(formElements, "name", "regionDescription");

// get template instance for given ID.
var object = sitedata.getObject("template-instance", templateId);

// we found the template
if(object != null) {

	var templateConfig = null;
	
	templateConfig = object.getProperty("config");
	
	// will contain array of all regions
	var regionsObject = null;
	
	// javascript object for the new region
	var regionObject = {};			

	// the template currently contains a config property value
	if(templateConfig){		

		// let's create a javascript object out of the 	json string
		templateConfig = eval('(' + templateConfig + ')');
		
		// get current regions array
		regionsObject = templateConfig.regions;
		
	} else {
		
		// create new javascript object for new template config, because one did not exist.
		templateConfig = { };						
		
		// create new javascript array that will contain the regions
		regionsObject = new Array();		
	}

	// on the new region object, set the properties that were passed in by the wizard framework.		
	regionObject.id = templateId + regionName;		
	regionObject.title = regionName;		
	regionObject.x = regionX;
	regionObject.y = regionY;
	regionObject.width = regionWidth;
	regionObject.height = regionHeight;
	regionObject.scope = regionScope;
	regionObject.description = regionDescription;
		
	// push the region object onto the regions array
	regionsObject.push(regionObject);
		
	// set the templateConfig's regions array to the newly created array
	templateConfig.regions = regionsObject;		

	// convert template config object to a json string for storage as a property on the template instance.
	var tempString = templateConfig.toJSONString();

	// set config property on template instance
	object.properties["config"] = tempString;

	// and, finally save the new setting
	object.save();

	// finalize things
	wizard.setResponseCodeFinish();
	wizard.setResponseMessage("Successfully added new region");
	
} else {

	// finalize things
	wizard.setResponseCodeFinish();
	wizard.setResponseMessage("Unable to add new Region. Template not found.");
	
}

