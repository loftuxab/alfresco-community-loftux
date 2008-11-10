<import resource="/include/support.js">

// convert json string to javascript object
var inElements = wizard.request("elements");

var formElements = eval('(' + inElements + ')');

var templateId = wizard.request("templateId");
var regionName = getJsonArrayValue(formElements, "name", "regionName");		
var regionId = getJsonArrayValue(formElements, "name", "regionId");
var regionScope = getJsonArrayValue(formElements, "name", "regionScope");
var regionX = getJsonArrayValue(formElements, "name", "regionX");
var regionY = getJsonArrayValue(formElements, "name", "regionY");
var regionWidth = getJsonArrayValue(formElements, "name", "regionWidth");
var regionHeight = getJsonArrayValue(formElements, "name", "regionHeight");
var regionDescription = getJsonArrayValue(formElements, "name", "regionDescription");


var object = sitedata.getObject("template-instance", templateId);

// we found the template
if(object != null) {

	var templateConfig = null;
	
	templateConfig = object.getProperty("config");
	
	var regionsObject = null;
	
	var regionObject = {};			

	if(templateConfig){		

		var tempArray = new Array();
		
		templateConfig = eval('(' + templateConfig + ')');
	
		var regiondFound = false;

		for(var regIndx = 0; regIndx < templateConfig.regions.length; regIndx++){
					
			logger.log("*****************checking for match: " + templateConfig.regions[regIndx].id + " and " + regionId);
		
			if(templateConfig.regions[regIndx].id == regionId){
				logger.log("***************WE FOUND A MATCH!!!!!!!!!!");
						
				var regionObject = {};
				
				regionObject.y = regionY;
				regionObject.id = regionId;
				regionObject.width = regionWidth;
				regionObject.scope = regionScope;
				regionObject.title = regionName;
				regionObject.x = regionX;
				regionObject.height = regionHeight;
				regionObject.description = regionDescription;	
				tempArray.push(regionObject);
			} else {
				tempArray.push(templateConfig.regions[regIndx]);
			}
		}
		
		templateConfig.regions = tempArray;

		var tempString = templateConfig.toJSONString();
		
		object.properties["config"] = tempString;
	
		object.save();		
				
	} else {
		// there was an error retrieving the template's config property		
	}


} else {

	// could not find the template instance.

}

// finalize things
wizard.setResponseCodeFinish();
wizard.setResponseMessage("Successfully added new region");
//wizard.setBrowserReload(true);

