<import resource="/include/support.js">

var templateId = wizard.request("templateId");
var regionId = wizard.request("regionId");

var object = sitedata.getObject("template-instance", templateId);

if(object != null) {

	var templateConfig = null;
	
	templateConfig = object.getProperty("config");
	
	var regionsObject = null;
		
	var regionObject = {};
		
	if(templateConfig){		
	
		templateConfig = eval('(' + templateConfig + ')');

		regionsObject = templateConfig.regions;
	
		var regiondFound = false;

		for(var regIndx = 0; regIndx < templateConfig.regions.length && !(regiondFound); regIndx++){
						
			if(templateConfig.regions[regIndx].id == regionId){
				
				regionFound = true;
		
				var regionId = templateConfig.regions[regIndx].id;
				var regionY = templateConfig.regions[regIndx].y;
				var regionWidth = templateConfig.regions[regIndx].width;
				var regionScope = templateConfig.regions[regIndx].scope;
				var regionName = templateConfig.regions[regIndx].title;				
				var regionX = templateConfig.regions[regIndx].x;
				var regionHeight = templateConfig.regions[regIndx].height;
				var regionDescription = templateConfig.regions[regIndx].description;
						
				wizard.addHiddenElement("templateId", templateId);
				wizard.addHiddenElement("regionId", regionId);
				
				wizard.addElement("regionName", regionName);
				wizard.addElementFormat("regionName", "Name", "textfield", 290);
				
				wizard.addElement("regionDescription", regionDescription);
				wizard.addElementFormat("regionDescription", "Description", "textarea", 290);
				
				wizard.addElement("regionScope", regionScope);
				wizard.addElementFormat("regionScope", "Scope", "combo", 290);

				wizard.addElementFormatKeyPair("regionScope", "emptyText", "Scope");
				wizard.addElementFormatKeyPair("regionScope", "title", "Scope");

				wizard.addElementSelectionValue("regionScope", "Global", "Global");
				wizard.addElementSelectionValue("regionScope", "Template", "Template");
				wizard.addElementSelectionValue("regionScope", "Page", "Page");
				
				wizard.addElement("regionX", regionX);
				wizard.addElementFormat("regionX", "X", "textfield", 10);
				
				wizard.addElement("regionY", regionY);
				wizard.addElementFormat("regionY", "Y", "textfield", 10);
				
				wizard.addElement("regionWidth", regionWidth);
				wizard.addElementFormat("regionWidth", "Width", "textfield", 10);
				
				wizard.addElement("regionHeight", regionHeight);
				wizard.addElementFormat("regionHeight", "Height", "textfield", 10);
					
			}
		}
	} else {
		logger.log("%%%%%%%we did not find a config property for this template");	
	}		
} else {
	logger.log("We could not retrieve the templateId");
}
