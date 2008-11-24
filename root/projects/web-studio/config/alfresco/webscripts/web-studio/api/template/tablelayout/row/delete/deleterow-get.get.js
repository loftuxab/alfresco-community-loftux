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
            
    for(var rowIndx=0;rowIndx<rowsArray.length;rowIndx++)
    {            	 
        if(String(rowsArray[rowIndx].id) == String(rowId))
        {

        	rowsArray.splice(rowIndx,1);
        }
    }   

    templateConfig.rows = rowsArray;
                            
    var tempString = templateConfig.toJSONString();                         
                                                
    object.properties["config"] = tempString;

    object.save();                

} else {

}
