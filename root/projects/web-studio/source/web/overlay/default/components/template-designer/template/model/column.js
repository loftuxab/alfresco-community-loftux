WebStudio.Templates.Model.Column = WebStudio.Templates.Model.Abstract.extend({ });

WebStudio.Templates.Model.Column.prototype.init = function(parent)
{
	// Set the parent object
	this.setParent(parent);

	// Object type	
	this.setObjectType("column");
	
	// Set up CSS
	this.setCSS("TemplateTableColumn");
	this.setOnMouseOverCSS("ColumnOnMouseOver");
	this.setOnMouseOutCSS("TemplateTableColumn");
	
	this.setTitleCSS("TemplateColumnTitle");	
	this.setTitleOnMouseOverCSS("TemplateColumnTitleOnMouseOver");	
	this.setTitleOnMouseOutCSS("TemplateColumnTitle");
	
	// CSS for menu div.
	this.setMenuCSS("TemplateColumnMenu");	
};


/**
 * Specifies the configuration of the context menu
 */
WebStudio.Templates.Model.Column.prototype.getMenuItemsConfig = function()
{
	var menuItems = [ ];

	/**
	 * Get the binding properties for parent objects.
	 * Those properties will serve as object id's for
	 * updating the column info in the repo.
	 */
	var bindingProperties = this.getBindingProperties();

	/**
	 * Get the parent of the column object.
	 * We we will need the id to update appropriate row.
	 */
	var parent = this.getParent();		
		
	menuItems[menuItems.length] = {
		id: 'deleteTemplateColumn',
		text: "Delete Column", 
		onclick: { 
			fn: this.deleteTemplateColumn.bind(this), 
			obj: [
			      	this.getId(), 
			      	bindingProperties["row"], 
			      	bindingProperties["template"]
			] 
		}
	};
	                 
	menuItems[menuItems.length] = {
		id: 'addTemplateRegion', 
        text: "Add Region", 
        onclick: { fn: this.addTemplateRegion.bind(this), 
		obj: [
		      	this.getId(), 
		      	bindingProperties["row"], 
		      	bindingProperties["template"]
		      ]
		}
	};
	
	return menuItems;
};


WebStudio.Templates.Model.Column.prototype.editTemplateRegionSizes = function(eventType, eventArgs, parmsObj)
{
	var _this = this;
	
    // Get the current Event        
    var e = eventArgs[0];

    // Stop event propagation.
    WebStudio.util.stopPropagation(e);
    
	/**
	 * Get the binding properties for parent objects.
	 * Those properties will serve as object id's for
	 * updating the column info in the repo.
	 */
	var bindingProperties = this.getBindingProperties();
    
    var w = new WebStudio.Wizard();

    w.setDefaultJson(
    {
        refreshSession: 'true',
        templateId: bindingProperties["template"],        
        rowId: bindingProperties["row"],
        panelId: this.getId(),
        actionFlag: 'editRegionSizes'
    });                 
    
    w.start(WebStudio.ws.studio("/wizard/regionmanager/tablelayout"), 'tablelayout');
    
    w.onComplete = function() 
    {
        _this.templateDesigner.refresh();     
    };
};


/**
 * Context Menu
 * Event Handler
 */ 
WebStudio.Templates.Model.Column.prototype.deleteTemplateColumn = function(eventType, eventArgs, parmsObj)
{
	var _this = this;
	
    // Get the current Event        
    var e = eventArgs[0];

    // Stop event propagation.
    WebStudio.util.stopPropagation(e);

    // args will be coming in this order [columnId, rowId, templateId]

	/**
	 * Get the binding properties for parent objects.
	 * Those properties will serve as object id's for
	 * updating the column info in the repo.
	 */
	var bindingProperties = this.getBindingProperties();
    
    // Get the template id
    var templateId = parmsObj[2];   

    var url = WebStudio.ws.studio("/api/template/tablelayout/panel/delete/get", 
    							{   type: "template-table", 
                                    templateId: bindingProperties["template"],
                                    rowId: bindingProperties["row"],
                                    panelId: this.getId(),
                                    actionFlag: 'deletePanel'} );
    
    call = YAHOO.util.Connect.asyncRequest('GET', url, {   
        success: function(r) {       
            _this.templateDesigner.refresh();       	        	
        },
        failure: function(r) {      
            //error deleting region
        }
    });
};

/**
 * Context Menu
 * Event Handler
 */ 
WebStudio.Templates.Model.Column.prototype.addTemplateRegion = function(eventType, eventArgs, parmsObj)
{
	var _this = this;
	
    // Get the current Event        
    var e = eventArgs[0];

    // Stop event propagation.
    WebStudio.util.stopPropagation(e);
    
	/**
	 * Get the binding properties for parent objects.
	 * Those properties will serve as object id's for
	 * updating the column info in the repo.
	 */
	var bindingProperties = this.getBindingProperties();
    
    var w = new WebStudio.Wizard();
    
    w.setDefaultJson(
    {
        refreshSession: 'true',
        templateId: bindingProperties["template"],        
        rowId: bindingProperties["row"],
        panelId: this.getId(),
        actionFlag: 'addRegion'
    });                 
    
    w.start(WebStudio.ws.studio("/wizard/regionmanager/tablelayout"), 'tablelayout');
    
    w.onComplete = function() 
    {
        _this.templateDesigner.refresh();     
    };  
};

/**
 * Renders the template to a given container
 */
WebStudio.Templates.Model.Column.prototype.render = function(container)
{
	
	// Parent container.
	this.container = container;

	// Create TD that will contain root Div element.
	var td = document.createElement('td');	
	td.setAttribute("id", this.getId());		
	
	// If a width was specified on creation
	// of column, let's set it here.
	if(this.getWidth())
	{
		WebStudio.util.setStyle(td, "width", this.getWidth() + "%");
	}

	// Add TD to parent container.
	WebStudio.util.injectInside(this.container, td);	
	
	// Div element that represents column object.
	var columnDiv = document.createElement('div');
	columnDiv.setAttribute("id", "col_div_" + this.getId());
	YAHOO.util.Dom.addClass(columnDiv, this.getCSS());	
	WebStudio.util.setStyle(columnDiv, "height", "93%");
	
	// Set this element as the root
	// element for this column object.
	this.setElement(columnDiv);
		
	// Add to the TD
	WebStudio.util.injectInside(td, columnDiv);
	
	// Setup mouse events
	this.setupEvents(columnDiv);
	
	// Div element that will represent column title.
	var titleElement = document.createElement('div');
	YAHOO.util.Dom.addClass(titleElement, this.getTitleCSS());	
	titleElement.setAttribute("id", "column_div_" + this.getId());	
	WebStudio.util.pushHTML(titleElement, "Column: " + this.getSequenceNumber());
	
	// Add column div.
	WebStudio.util.injectInside(columnDiv, titleElement);	

	// register mouse events
	this.setupEvents(titleElement);
	
	if (this.getChildCount() > 0)
	{		
		// render child objects
		this.renderChildren(columnDiv);
	} 
};


//////////////////////////////////////
// Type Specific Methods
//////////////////////////////////////

WebStudio.Templates.Model.Column.prototype.addRegion = function(regionObject)
{
	this.addChild(regionObject);
};