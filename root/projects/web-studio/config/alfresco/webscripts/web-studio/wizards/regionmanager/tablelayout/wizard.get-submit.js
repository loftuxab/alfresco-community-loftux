<import resource="/include/support.js">
<import resource="/include/utils.js">

// convert json string to javascript object
var inElements = wizard.request("elements");

var formElements = eval('(' + inElements + ')'); 

var actionFlag = getJsonArrayValue(formElements, "name", "actionFlag");

var rowId = getJsonArrayValue(formElements, "name", "rowId");

var panelId = getJsonArrayValue(formElements, "name", "panelId");

// get template instance for given ID.
var object = sitedata.getObject("template-instance", templateId);

if(object != null) 
{
	var templateConfig = null;
	
	// Get config json.
	templateConfig = object.getProperty("config");	

	// Get new values from form.
    var regionName = getJsonArrayValue(formElements, "name", "regionName");
    var regionScope = getJsonArrayValue(formElements, "name", "regionScope");
    var regionHeight = getJsonArrayValue(formElements, "name", "regionHeight");    

	// Convert to JS object.
    templateConfig = eval('(' + templateConfig + ')');        

		// Get rows array.
        rowsArray = templateConfig.rows;
        
        var rowFound = false; 

		// Look for row that will be updated.            
        for(var rowIndx=0;rowIndx<rowsArray.length && !(rowFound);rowIndx++)
        {        
            if(rowsArray[rowIndx].id == rowId)
            {
                rowFound = true;

				// Get panels/columns array.                
                panelsArray = rowsArray[rowIndx].panels;
                
                var panelFound = false;
                
				// Look for panel that will be updated.                
		        for(var panelIndx=0;panelIndx<panelsArray.length && !(panelFound);panelIndx++)
		        {		        		        
	               // let's look for the correct panel to add our new region to.
		            if(panelsArray[panelIndx].id == panelId)
		            {		            
                        panelFound = true;

						// Get panel object.                        		                  	                    
		                panelObject = panelsArray[panelIndx];

						// Determine if we need to add a region, or update one.
    					if(actionFlag == "addRegion")
    					{		    
    						
    						var regionsArray = panelObject.regions;
    						
    						// Check for array. If not found,
    						// let's create one.
    						if(!regionsArray )
    						{						    							
    							// create a new regions array                        
								regionsArray = new Array();									
    						}
    							
							// create region object
							regionObject = { };
							
							// set region attributes
							regionObject.id = panelIndx + generateID();							
							regionObject.name = regionName;
							regionObject.scope = regionScope;
							regionObject.height = regionHeight;

							// add new region to region array
							regionsArray.push(regionObject);		                    

							// add region array to panelObject
							panelObject.regions = regionsArray;

							// add panelObject back to panelsArray
							panelsArray[panelIndx] = panelObject;							
						} else if(actionFlag == "editRegion") {
						
							// Get region id from form element.
							var regionId = getJsonArrayValue(formElements, "name", "regionId");
						
							// Get regions array from panel object.
							regionsArray = panelObject.regions;
							
							 var regionFound = false;
						
							// Let's look for the region we need to update.
							for(var regionIndx=0;regionIndx<regionsArray.length && !(regionFound);regionIndx++)
							{		        	
								// Look for region to be updated.
								if(regionsArray[regionIndx].id == regionId)
								{		            								
									regionFound = true;								
									
									// create region object
									regionObject = regionsArray[regionIndx];
									
									// Set new attribute values.
									regionObject.name = regionName;
									regionObject.scope = regionScope; 
									regionObject.height = regionHeight;									
									
									// Add update region back to regions array
									regionsArray[regionIndx] = regionObject;
									
									// Add regions array back to panel object.
									panelObject.regions = regionsArray;			
                
									// add panelObject back to panelsArray						                					                					
									panelsArray[panelIndx] = panelObject;
								}
							}										
						}
		            } 		                                                                                            
                }
                
		        // Add panels array back to templateConfig.
                templateConfig.rows.panels = panelsArray;		        
                            
                // Prepare for sending.
                var tempString = templateConfig.toJSONString();              

                // Set string
                object.properties["config"] = tempString;

                // Save updated template config.
                object.save();                
            }            
        }                                                             
} else {
	// could not find the template instance.
}

// finalize things
wizard.setResponseCodeFinish();
wizard.setResponseMessage("Successfully modified panels");
//wizard.setBrowserReload(true);

