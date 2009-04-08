WebStudio.Templates.Model.Column = WebStudio.Templates.Model.Abstract.extend({ });

WebStudio.Templates.Model.Column.prototype.init = function(parent)
{
	// Set the parent object
	this.setParent(parent);

	// Object type	
	this.setObjectType("column");
	
	// Set up CSS
	this.setCSS("TemplateTableColumn");
	this.setTitleCSS("TemplateColumnTitle");
	this.setMenuCSS("TemplateColumnMenu");
	
	// Mouseover CSS
	this.setOnMouseOverCSS("TemplateObjectOnMouseOver");
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
	this.container = container;

	// column (set as root element)
	var column = Alf.createElement('div', this.getId());
	YAHOO.util.Dom.addClass(column, this.getCSS());
    WebStudio.util.injectInside(this.container, column);
    this.setElement(column);
    
    // calculate the width of the column
    var width = (jQuery(container).width() / this.getTotalChildCount());
    width = width - 16;  
	width = Math.floor(width);
    jQuery(column).css( { "width" : width } );
    
    if (this.getTotalChildCount() > 1)
    {
    	jQuery(column).css( { "float" : "left" } );    
	    if (this.getSequenceNumber() == this.getTotalChildCount())
	    {
	    	jQuery(column).css( { "float" : "right" } );
	    }
	}
	
	// calculate the height of the column
	var height = jQuery(container).height() - 32;
	jQuery(column).css( { "height" : height } );
	
	// title div
	var titleElement = Alf.createElement("div", "column_div_" + this.getId());
    YAHOO.util.Dom.addClass(titleElement, this.getTitleCSS());
	WebStudio.util.pushHTML(titleElement, "Column: " + this.getSequenceNumber());
    WebStudio.util.injectInside(column, titleElement);	

	// body div	
	var bodyElement = Alf.createElement("div", "column_div_body_" + this.getId());
	YAHOO.util.Dom.addClass(bodyElement, "TemplateTableColumnBody");
	WebStudio.util.injectInside(column, bodyElement);

	// set up events
	this.setupEvents(titleElement);
	this.setupEvents(bodyElement);
	this.setupEvents();		
		
	// render the columns        
	if (this.getChildCount() > 0)
	{		
		// render child objects
		this.renderChildren(bodyElement);
	} 
	else 
	{	
		// To ensure that the entire template region is active
		WebStudio.util.pushHTML(bodyElement, "&nbsp;&nbsp;");	
	}
};


//////////////////////////////////////
// Type Specific Methods
//////////////////////////////////////

WebStudio.Templates.Model.Column.prototype.addRegion = function(regionObject)
{
	this.addChild(regionObject);
};