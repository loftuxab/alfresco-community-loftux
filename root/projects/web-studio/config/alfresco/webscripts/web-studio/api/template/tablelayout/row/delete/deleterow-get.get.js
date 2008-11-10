<import resource="/include/support.js">

var templateId = args["templateId"];
var rowId = args["rowId"];

// get template instance for given ID.
var object = sitedata.getObject("template-instance", templateId);

if(object != null) 
{
    var templateConfig = null;
    
    templateConfig = object.getProperty("config");
    
    var templateLayoutType = object.getProperty("template-layout-type");   

    templateConfig = eval('(' + templateConfig + ')');        
  
    var rowsArray = templateConfig.rows;
        
    var tempRowsArray = new Array();
            
    for(var rowIndx=0;rowIndx<rowsArray.length;rowIndx++)
    {        
        if(!rowsArray[rowIndx].id.match(rowId))
        {   
            tempRowsArray.push(rowsArray[rowIndx]);
        }
    }   
             
    templateConfig.rows = tempRowsArray;
                            
    var tempString = templateConfig.toJSONString();                         
 
 logger.log("tempString: " + tempString);
                                                
    object.properties["config"] = tempString;

    object.save();                

} else {

}
