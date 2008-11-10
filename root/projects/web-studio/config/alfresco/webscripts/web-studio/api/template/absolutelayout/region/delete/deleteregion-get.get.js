<import resource="/include/support.js">

var templateId = args["templateId"];
var regionId = args["regionId"];

// get template instance for given ID.
var object = sitedata.getObject("template-instance", templateId);

if(object != null) 
{
    var templateConfig = null;
    
    templateConfig = object.getProperty("config");
    
    templateConfig = eval('(' + templateConfig + ')');        
  
    var regionsArray = templateConfig.regions;
        
    var tempRegionsArray = new Array();
    
    for(var regionIndx=0;regionIndx<regionsArray.length;regionIndx++)
    {        
        if(regionsArray[regionIndx].id != regionId)
        {           
            tempRegionsArray.push(regionsArray[regionIndx]);
        }    
    }                              
      
    templateConfig.regions = tempRegionsArray;
                            
    var tempString = templateConfig.toJSONString();                         
                                                
    object.properties["config"] = tempString;

    object.save();           
}