if (typeof Alfresco == "undefined")
{
    var Alfresco = {};
}

Alfresco.TableLayoutRenderer = function(index, selectedTemplateId, templateInstance, templateDesignerEditor, templateDesigner) 
{
    // Id for template renderer
    this.id = index;
    
    // Template instance JS object.
    this.instance = templateInstance;
            
    // Template title
    this.title = this.instance.title;
    
    // Template layout type (e.g. Table Layout)
    this.layoutType = this.instance.templateLayoutType
    
    // Template ID
    this.selectedTemplateId = selectedTemplateId;
    
    // Design Editor. All Templates will be designed in this container.
    this.templateDesignerEditor = templateDesignerEditor;
    
    // Reference to Template Object Model
    this.template = null;
    
    // Reference to the template designer object
    this.templateDesigner = templateDesigner;                              
};

Alfresco.TableLayoutRenderer.prototype.getTemplateDesigner = function()
{
	return this.templateDesigner;
}

Alfresco.TableLayoutRenderer.prototype.activate = function() 
{             
    // Create template object.
    // This will represent the Table container for the template rows.
    this.template = new WebStudio.Templates.Model.DynamicTemplate(this.selectedTemplateId, this.title, this.getTemplateDesigner());

    // Initialize the template. This will 
    // Things things such as CSS classes.
    this.template.init();
    
    // If we have a valid template instance config, let's load the template object model.         
    if(this.instance.config != null && this.instance.config!= 'undefined')
    {
        this.loadObjectModel();                   
    } else {
        // todo: take care of case where there is no valid instance config
    }

};

Alfresco.TableLayoutRenderer.prototype.destroy = function() 
{             
    // Let's destroy the template and all of it's child objects.
	this.template.destroy();
};

Alfresco.TableLayoutRenderer.prototype.loadObjectModel = function() 
{                      
	// Get the rows array from the template instance config.          
    var rowsArray = this.instance.config.rows;
     
    // If we have some rows, let's create a Row object for them.
    if(rowsArray != null && rowsArray != 'undefined' && rowsArray.length > 0)
    {        	
        // While we have rows, let's create a Row object based on the Template Instance Config row.
        for(var rowIndx=0;rowIndx < rowsArray.length;rowIndx++)
        {                                  
            // Let's get the row object from the rows.             
        	var rowObject = rowsArray[rowIndx];

            var rowHeight = rowObject.height;                            
            var rowId = rowObject.id;
                                                    
            // Create a new Row JavaScript Object. 
            var templateRowObject = new WebStudio.Templates.Model.Row(rowId, null, this.getTemplateDesigner());                                                 
               
            // Set the width.
            templateRowObject.setHeight(rowHeight);      
                       
            // Try to access the columns/panels array from the template config rows json.
            var colArray = this.instance.config.rows[rowIndx].panels;
                                
            // If we have columns, we will create Column JS objects.
            if(colArray != null && colArray != 'undefined' && colArray.length > 0)
            {
                for(var colIndx=0;colIndx < colArray.length;colIndx++)
                {
                    // Let's get the column from template config cols array.                          
                    var columnObject = colArray[colIndx];
                                    
                    var columnId = columnObject.id;
                       
                    // Create the new Column object. We will then add it to the JS Row object.
                    var templateColumnObject = new WebStudio.Templates.Model.Column(columnId, null, this.getTemplateDesigner());
                        
                    // Set size for Column object.
                    templateColumnObject.setSize(columnObject.width, columnObject.height);                  
                                                
                    // Get the Regions array from the template config json.                                             
                    var regionArray = colArray[colIndx].regions;

                    // If we have regions, we will create Region objects and store in the Columns object.
                    if(regionArray != null && regionArray != 'undefined' && regionArray.length > 0)
                    {
                        for(var regionIndx=0;regionIndx < regionArray.length;regionIndx++)
                        {
                            // Region object from region json array.
                            var regionObject = regionArray[regionIndx];
                                 
                            var regionId = regionObject.id;
                                                            
                            // Create new Region object.      
                            var templateRegionObject = new WebStudio.Templates.Model.Region(regionId, null, this.getTemplateDesigner());                                                      
                                  
                            templateRegionObject.setTitle(regionObject.name);                                                              
                                  
                            // Set the Region Scope.
                            templateRegionObject.setScope(regionObject.scope);
                                  
                            // Add the Region object to the Column object.
                            templateColumnObject.addRegion(templateRegionObject);                           
                        }                       
                    }                       
                    // Add the Column object to the Row Object.
                    templateRowObject.addColumn(templateColumnObject);                                      
                }                                                                     
            }           
            // Add the Row object to the Template object
            this.template.addRow(templateRowObject);                            
        }
    }
};

Alfresco.TableLayoutRenderer.prototype.cleanup = function() 
{      
    for(var nodeIndx=0; nodeIndx < this.templateDesignerEditor.childNodes.length; nodeIndx++)
    {           
        if(this.templateDesignerEditor.childNodes[nodeIndx].id != null)
        {                                        
            this.templateDesignerEditor.removeChild(this.templateDesignerEditor.childNodes[nodeIndx]);
        }
    }    
};

Alfresco.TableLayoutRenderer.prototype.render = function()
{
    // Remove children from template designer editor.
    this.cleanup();
 
    // Render the current template instance         
    this.template.render(this.templateDesignerEditor);         
}
 
