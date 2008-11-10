<import resource="/include/support.js">

var templateId = wizard.request("templateId");

var actionFlag = wizard.request("actionFlag");

if (actionFlag == "updateRowPanelSizes") 
{
    var rowId = wizard.request("rowId");    
    wizard.addHiddenElement("templateId", templateId);
    wizard.addHiddenElement("actionFlag", actionFlag);
    wizard.addHiddenElement("rowId", rowId);

    var object = sitedata.getObject("template-instance", templateId);
    
    if(object != null) {    
        var templateConfig = null;
        
        templateConfig = object.getProperty("config");
    
        templateConfig = eval('(' + templateConfig + ')');
        
        var rowsArray = templateConfig.rows;               
        
        if((rowsArray != null) && (rowsArray != 'undefined') && (rowsArray.length > 0))
        {                   
            wizard.addHiddenElement("numberRows", rowsArray.length);                                
            for(var rowIndx=0;rowIndx<rowsArray.length;rowIndx++)
            {	            
                if(rowsArray[rowIndx].id == rowId)
	        {	                                        
                    panelsArray = rowsArray[rowIndx].panels;                                                                           		
	                        
	            if((panelsArray != null) && (panelsArray != 'undefined') && (panelsArray.length > 0))
	            {                                    
	                for(var panelIndx=0;panelIndx<panelsArray.length;panelIndx++)
		        {            
	                    var panelObject = panelsArray[panelIndx];
		                      
		            	var panelId = panelObject.id;                        
                        var panelHeight = panelObject.height;
                        var panelWidth = panelObject.width;
                            		                      	                        
                    	wizard.addElement(rowId + panelId + "width", panelWidth);                     
		            	wizard.addElementFormat(rowId + panelId + "width", panelId + " Width", "textfield", 10);                  
		        }
                    }                                              
	        }
            }
        }
    }    
} else if(actionFlag == "createRowPanelsConfig") {    
    var rowId = wizard.request("rowId");
    
    wizard.addHiddenElement("templateId", templateId);
    wizard.addHiddenElement("rowId", rowId);    
    wizard.addHiddenElement("actionFlag", "createRowPanelsConfig");
    
    wizard.addElement("numberPanels", "");
    wizard.addElementFormat("numberPanels", "Number of Columns", "textfield", 10);        
} else if(actionFlag == "createTemplateRowsConfig") {	
	wizard.addHiddenElement("templateId", templateId);
    wizard.addHiddenElement("actionFlag", actionFlag);
    
	wizard.addElement("numberRows", "");
	wizard.addElementFormat("numberRows", "Number of Rows", "textfield", 10);		
} else if(actionFlag == "updateTemplateRowsConfig") {
    wizard.addHiddenElement("templateId", templateId);
    wizard.addHiddenElement("actionFlag", actionFlag);

    // get template instance for given ID.
    var object = sitedata.getObject("template-instance", templateId);
	
    if(object != null) {	
        var templateConfig = null;
	    
	templateConfig = object.getProperty("config");
    
        templateConfig = eval('(' + templateConfig + ')');
        
        var rowsArray = templateConfig.rows;               
        
        if(rowsArray != null && rowsArray.length > 0)
        {                    
            wizard.addHiddenElement("numberRows", rowsArray.length);
                
            for(var rowIndx=0;rowIndx<rowsArray.length;rowIndx++)
            {            
                var rowId = rowsArray[rowIndx].id;
                var rowHeight = rowsArray[rowIndx].height;
                
    	        wizard.addElement(rowId + "height", rowHeight);
			    wizard.addElementFormat(rowId + "height", rowId + " Height", "textfield", 10);            
            }
        }
    }
}