<import resource="/include/support.js">

var templateId = args["templateId"];
var rowId = args["rowId"];
var panelId = args["panelId"];
var regionId = args["regionId"];

// get template instance for given ID.
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
        if(rowsArray[rowIndx].id == rowId)
        {
            rowFound = true;                              
                
            var panelsArray = rowsArray[rowIndx].panels;
                
            var panelFound = false;
                
            var tempPanelsArray = new Array();
                
            for(var panelIndx=0;panelIndx<panelsArray.length && !(panelFound);panelIndx++)
            {
                                
                // let's look for the correct panel that will get the region removed from
                if(panelsArray[panelIndx].id.match(panelId))
                {                   
                    panelFound = true;
                                                                        
                    var panelObject = panelsArray[panelIndx];
                            
                    // let's check to see if the panel has any regions currently in it
                    if(panelObject.regions)
                    {                    
                        var regionsArray = panelObject.regions;                        
                        
                        var tempRegionsArray = new Array();
                        
                        var regionFound = false;
                        
                        for(var regionIndx=0;regionIndx<regionsArray.length && !(regionFound);regionIndx++)
                        {                      
			                if(regionsArray[regionIndx].id == regionId)
			                {                   
			                    regionFound = true;
			                    
			                } else {
			                    tempRegionsArray.push(regionsArray[regionIndx]);
			                }                          
                        }                        
                        panelsArray[panelIndx].regions = tempRegionsArray;                                                                                                                                                               
                    }                    
                    rowsArray[rowIndx].panels = panelsArray;                    
                } else {
                }                                                                        
            }
                
            templateConfig.rows = rowsArray;
                            
            var tempString = templateConfig.toJSONString();                         
                                                
            object.properties["config"] = tempString;

            object.save();                
        }                       
    }
} else {

}
