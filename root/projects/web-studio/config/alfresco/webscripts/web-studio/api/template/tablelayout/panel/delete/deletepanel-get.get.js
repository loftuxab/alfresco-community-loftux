<import resource="/include/support.js">

var templateId = args["templateId"];
var rowId = args["rowId"];
var panelId = args["panelId"];

var object = sitedata.getObject("template-instance", templateId);

if(object != null) 
{
    var templateConfig = null;

    // Get template config json.
    templateConfig = object.getProperty("config");

    // Create javascript object.
    templateConfig = eval('(' + templateConfig + ')');        
  
    // Get rows array.
    var rowsArray = templateConfig.rows;
        
    var rowFound = false;  
            
    // Look for row containing column we are going to remove.
    for(var rowIndx=0;rowIndx<rowsArray.length && !(rowFound);rowIndx++)
    {        
    	// Found our row.
        if(rowsArray[rowIndx].id == (rowId))
        {
            rowFound = true;                              
                
            var panelsArray = rowsArray[rowIndx].panels;
                
            // Look for our column.
            for(var panelIndx=0;panelIndx<panelsArray.length;panelIndx++)
            {                                
            	// Found our column.
                if(panelsArray[panelIndx].id == (panelId))
                {   
                	// Remove object from array.
                	panelsArray.splice(panelIndx, 1);                   
                }                                                                        
            }
            
            // Add updated columns/panels array 
            // back to the rows array.
            rowsArray[rowIndx].panels = panelsArray;            
                
            // Add rows array back to templateConfig object.
            templateConfig.rows = rowsArray;
                            
            // Prepare for sending.
            var tempString = templateConfig.toJSONString();
                                   
            // Set
            object.properties["config"] = tempString;

            // Save
            object.save();                
        }                       
    }
} else {

}
