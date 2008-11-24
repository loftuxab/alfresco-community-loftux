<import resource="/include/support.js">

var templateId = args["templateId"];
var rowId = args["rowId"];
var panelId = args["panelId"];
var regionId = args["regionId"];

// get template instance for given ID.
var object = sitedata.getObject("template-instance", templateId);

// Make sure we ahve a template object.
if(object != null) 
{
    var templateConfig = null;
    
    // Get template config json string.
    templateConfig = object.getProperty("config");
    
    // Create JavaScript object from json.
    templateConfig = eval('(' + templateConfig + ')');        
  
    // Get rows array from template config.
    var rowsArray = templateConfig.rows;
        
    // Setup flag that we will use
    // to exit for loop if row is found.
    var rowFound = false;  
            
    // Search for the row that we need to remove the region from.
    for(var rowIndx=0;rowIndx<rowsArray.length && !(rowFound);rowIndx++)
    {        
    	// Check for match.
        if(rowsArray[rowIndx].id == rowId)
        {
        	// We found our row.
            rowFound = true;                              
                
            // Get the column/panel array for this row.
            var panelsArray = rowsArray[rowIndx].panels;
                
            // Setup flag we will us
            // to exit for loop if column found.
            var panelFound = false;                
                
            // Search for the panel/column that contains the region we need to remove.
            for(var panelIndx=0;panelIndx<panelsArray.length && !(panelFound);panelIndx++)
            {
                if(panelsArray[panelIndx].id == panelId)
                {                   
                	// We found our panel.
                    panelFound = true;
                    
                    // Get panel/column object.
                    var panelObject = panelsArray[panelIndx];
                            
                    // Make sure panel actually has some regions.
                    if(panelObject.regions)
                    {                    
                    	// Get regions array.
                        var regionsArray = panelObject.regions;                        
                        
                        // Look for the region that we will delete.
                        for(var regionIndx=0;regionIndx<regionsArray.length;regionIndx++)                        
                        {                                             	
                        	// Check for matching id's.
			                if(regionsArray[regionIndx].id == regionId)
			                {                   			                	
			                    // Remove region object at given index.
			                    regionsArray.splice(regionIndx,1);
			                }			                			                
                        }                                              
                        // Add the modified array back into the panelsArray.
                        panelsArray[panelIndx].regions = regionsArray;                        
                    }                    
                    // Add the panelsArray back into the rowsArray.
                    rowsArray[rowIndx].panels = panelsArray;                    
                }                                                                        
            }
                
            // Add the rowsArray back into the template config.
            templateConfig.rows = rowsArray;
                            
            // Convert to string.
            var tempString = templateConfig.toJSONString();                         
                                 
            // Set the config property on our template object with the 
            // updated json string.
            object.properties["config"] = tempString;

            // Save modified template object back to repo.
            object.save();                
        }                       
    }
}