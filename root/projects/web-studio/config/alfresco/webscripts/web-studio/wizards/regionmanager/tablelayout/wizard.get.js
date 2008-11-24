<import resource="/include/support.js">

var templateId = wizard.request("templateId");

var actionFlag = wizard.request("actionFlag");

var rowId = wizard.request("rowId");

var panelId = wizard.request("panelId");

wizard.addHiddenElement("actionFlag", actionFlag);    
wizard.addHiddenElement("templateId", templateId);
wizard.addHiddenElement("rowId", rowId);
wizard.addHiddenElement("panelId", panelId);    

if (actionFlag == "addRegion") {
    wizard.addElement("regionName", "");
    wizard.addElementFormat("regionName", "Region Name", "textfield", 20);

    wizard.addElement("regionHeight", "");
    wizard.addElementFormat("regionHeight", "Height", "textfield", 20);
    
    wizard.addElement("regionScope", regionScope);
    wizard.addElementFormat("regionScope", "Scope", "combo", 290);

    wizard.addElementFormatKeyPair("regionScope", "emptyText", "Scope");
    wizard.addElementFormatKeyPair("regionScope", "title", "Scope");

    wizard.addElementSelectionValue("regionScope", "global", "Global");
    wizard.addElementSelectionValue("regionScope", "template", "Template");
    wizard.addElementSelectionValue("regionScope", "page", "Page");
} 
else if (actionFlag == "editRegionSizes") 
{

	// Get regions for template/row/column.
	// parms (templateId, rowId, columnId
		
	// Render form with current region sizes.
	
} 
else if (actionFlag == "editRegion") 
{

	var regionId = wizard.request("regionId");

	// get template instance for given ID.
	var object = sitedata.getObject("template-instance", templateId);

	// Retrieve the region that will get updated		
	if(object != null) 
	{
		var templateConfig = null;

		// Get config json.
		templateConfig = object.getProperty("config");	

		// Convert to JavaScript object.
        templateConfig = eval('(' + templateConfig + ')');        
  
  		// Get rows array.
        rowsArray = templateConfig.rows;
        
        var rowFound = false; 
  
  		// Look for row that contains the panel and region that we will update.
        for(var rowIndx=0;rowIndx<rowsArray.length && !(rowFound);rowIndx++)
        {        
            if(rowsArray[rowIndx].id == rowId)
            {
                rowFound = true;
  
  				// Get panels array.
                panelsArray = rowsArray[rowIndx].panels;
                
                var panelFound = false;

				// Look for panel that contains the region we will update.
		        for(var panelIndx=0;panelIndx<panelsArray.length && !(panelFound);panelIndx++)
		        {		        		        
	               // let's look for the correct panel to add our new region to.
		            if(panelsArray[panelIndx].id == panelId)
		            {		            
                        panelFound = true;

						// Get panel object.                        		                  	                    
		                panelObject = panelsArray[panelIndx];
								                
                        // Get regions array                        
	                    regionsArray = panelObject.regions;
	                    
	                    var regionFound = false;

						// Look for region we will update.
						for(var regionIndx=0;regionIndx<regionsArray.length && !(regionFound);regionIndx++)
						{		        		        
						   // Look for region to be updated.
							if(regionsArray[regionIndx].id == regionId)
							{		            
								regionFound = true;								
								
								// Get region object
								var regionObject = regionsArray[regionIndx];

								// Layout out form elements with region data.
								wizard.addHiddenElement("regionId", regionId);							

								wizard.addElement("regionName", regionObject.name);
    							wizard.addElementFormat("regionName", "Region Name", "textfield", 20);

    						    wizard.addElement("regionHeight", regionObject.height);
    						    wizard.addElementFormat("regionHeight", "Height", "textfield", 20);    							

								wizard.addElement("regionScope", regionScope);
								wizard.addElementFormat("regionScope", "Scope", "combo", 290);

								wizard.addElementFormatKeyPair("regionScope", "emptyText", "Scope");
								wizard.addElementFormatKeyPair("regionScope", "title", "Scope");

								wizard.addElementSelectionValue("regionScope", "global", "Global");
								wizard.addElementSelectionValue("regionScope", "template", "Template");
								wizard.addElementSelectionValue("regionScope", "page", "Page");									
							}
						}		
					}
				}
			}			
		}
	}			
}