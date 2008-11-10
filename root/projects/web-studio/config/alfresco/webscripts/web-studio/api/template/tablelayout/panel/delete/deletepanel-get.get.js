<import resource="/include/support.js">

var templateId = args["templateId"];
var rowId = args["rowId"];
var panelId = args["panelId"];


logger.log("about to delete a panel: " + templateId + " " + rowId + " " + panelId);


var object = sitedata.getObject("template-instance", templateId);

if(object != null) 
{
    var templateConfig = null;
    
    templateConfig = object.getProperty("config");
    
    var templateLayoutType = object.getProperty("template-layout-type");   

    templateConfig = eval('(' + templateConfig + ')');        
  
    var rowsArray = templateConfig.rows;
        
    var rowFound = false;  
            
    for(var rowIndx=0;rowIndx<rowsArray.length && !(rowFound);rowIndx++)
    {        
        if(rowsArray[rowIndx].id.match(rowId))
        {
            rowFound = true;                              
                
            var panelsArray = rowsArray[rowIndx].panels;
                
            var tempPanelsArray = new Array();
                
            for(var panelIndx=0;panelIndx<panelsArray.length;panelIndx++)
            {                                
                // let's look loop through the panels, ignoring the one that should be removed.
                if(!panelsArray[panelIndx].id.match(panelId))
                {   
                    tempPanelsArray.push(panelsArray[panelIndx]);                                  
                }                                                                        
            }
            
            rowsArray[rowIndx].panels = tempPanelsArray;            
                
            templateConfig.rows = rowsArray;
                            
            var tempString = templateConfig.toJSONString();
                                                
            object.properties["config"] = tempString;

            object.save();                
        }                       
    }
} else {

}
