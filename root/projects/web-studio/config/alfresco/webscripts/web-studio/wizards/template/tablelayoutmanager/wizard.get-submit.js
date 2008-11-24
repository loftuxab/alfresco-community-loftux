<import resource="/include/support.js">
<import resource="/include/utils.js">

// convert json string to javascript object
var inElements = wizard.request("elements");

var formElements = eval('(' + inElements + ')'); 

var actionFlag = getJsonArrayValue(formElements, "name", "actionFlag");

// get template instance for given ID.
var object = sitedata.getObject("template-instance", templateId);

if(object != null) {

	var templateConfig = null;
	
	templateConfig = object.getProperty("config");
	
	var templateLayoutType = object.getProperty("template-layout-type");

	// Let's create some panels/columns for the row.
    if(actionFlag == "createRowPanelsConfig") 
    {
    	// Get rowId from the incoming form elements.
        var rowId = getJsonArrayValue(formElements, "name", "rowId");
   
        // Get number of columns from the form elements.
        var numberPanels = getJsonArrayValue(formElements, "name", "numberPanels");
            
        // Create object from json string.
        templateConfig = eval('(' + templateConfig + ')');
        
        // Get the array of rows in template config.
        rowsArray = templateConfig.rows;     
        
        if(rowsArray != null)
        {
            tempRowsArray = new Array();
            
            for(var rowIndx=0; rowIndx<rowsArray.length; rowIndx++)
            {                
                if(rowsArray[rowIndx].id == rowId)
                {
                    rowObject = rowsArray[rowIndx];
                    
                    panelsArray = new Array();
                                
                    var avgWidth = 25;                                        
                   
                    // Currently we are only dealing with Percentage sizes.
                    // Let's 100 by the number of columns/panels to get the average.
                    // Todo: allow for pixels sizes.
                    if(numberPanels > 0)
                    {
                        avgWidth = (100/numberPanels);
                        Math.round(avgWidth);
                    }
                    
                    for(var panelIndx=0;panelIndx<numberPanels;panelIndx++)
	                {
	                    var panelObject = { };
	                               
	                    panelObject.id = panelIndx + generateID();	                    
	                          
	                    panelObject.width = avgWidth;
	                    panelObject.height = "100";
	                    panelsArray.push(panelObject);                                              
	                }                    
                    rowObject.panels = panelsArray;
                    
                    tempRowsArray.push(rowObject);
                }
                else 
                {
                    tempRowsArray.push(rowsArray[rowIndx]);
                }
            }            
            templateConfig.rows = tempRowsArray;
            
            var tempString = templateConfig.toJSONString();               
            
            object.properties["config"] = tempString;
        
            object.save();
        }
    // Let's create some rows for this template config.
    } 
    else if(actionFlag == "createTemplateRowsConfig") 
    {       
	
		templateConfig = { };						
			
		rowsArray = new Array();
        
        var avgRowHeight = 25;
        
        if(numberRows > 0)        
        {
            avgRowHeight = (100/numberRows);    
            
            Math.round(avgRowHeight);
        }
        
        for(var rowIndx=0;rowIndx<numberRows;rowIndx++)
        {       
	
	        var panelsArray = new Array();
	
            var elementName = "name";
	            
            var matchValue = "row" + (rowIndx + 1);
	            
            var rowNumPanels = getJsonArrayValue(formElements, elementName, matchValue);	                                   
	            
            for(var panelIndx=0;panelIndx<rowNumPanels;panelIndx++)
            {
                var panelObject = { };
	            	            
	            panelObject.id = panelIndx + generateId();                
		                  
		        panelObject.width = "100";
		        panelObject.height = "100";
                panelsArray.push(panelObject);                              		        
		    }
			    	
	        var rowObject = { };
	
	        // Set row attributes.
	        rowObject.id = rowIndx + generateID();        
            rowObject.height = avgRowHeight;
		                
            // Update row object 
	        rowObject.panels = panelsArray;
		
	        rowsArray.push(rowObject);        	        
	
	        templateConfig.rows = rowsArray;
	          
	        var tempString = templateConfig.toJSONString();               
	        
	        object.properties["config"] = tempString;
	    
	        object.save();
		}
    } 
    else if(actionFlag == "updateTemplateRowsConfig") 
    {                            
        templateConfig = eval('(' + templateConfig + ')');
        
        var rowsArray = templateConfig.rows;
        
        var newRowsArray = new Array();
        
        for(var rowIndx=0;rowIndx<rowsArray.length;rowIndx++)
        {
            var rowId = rowsArray[rowIndx].id;     
                   
             rowsArray[rowIndx].height = getJsonArrayValue(formElements, "name", rowId + "height");
        }
        
        templateConfig.rows = rowsArray;
        
        var tempString = templateConfig.toJSONString();               
            
        object.properties["config"] = tempString;
        
        object.save();                                   
    } 
    else if(actionFlag == "updateRowPanelSizes") 
    {             
        templateConfig = eval('(' + templateConfig + ')');
        
        var rowsArray = templateConfig.rows;        
        
        var rowFoundFlag = false;
  
        for(var rowIndx=0;(rowIndx<rowsArray.length) && !(rowFoundFlag);rowIndx++)
        {
            var currentRowId = rowsArray[rowIndx].id;

            if(rowId == currentRowId)
            {
                rowFoundFlag = true;
                
                var panelsArray = rowsArray[rowIndx].panels;

                for(var panelIndx=0;panelIndx<panelsArray.length;panelIndx++)
                {
                    var panelId =  panelsArray[panelIndx].id
                    
                    var panelWidth = getJsonArrayValue(formElements, "name", rowId + panelId + "width");
                    
                    panelsArray[panelIndx].width =  panelWidth;                                      
                }                                
                rowsArray[rowIndx].panels = panelsArray;
                
                templateConfig.rows = rowsArray;
       
                var tempString = templateConfig.toJSONString();               
                            
                object.properties["config"] = tempString;        
                object.save();                                         
            }                                    
        }      
    } 
} 
else 
{
	// could not find the template instance.
}
// finalize things
wizard.setResponseCodeFinish();
wizard.setResponseMessage("Successfully modified panels");
wizard.setBrowserReload(false);