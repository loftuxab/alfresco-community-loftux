WebStudio.Templates.Model.Row = WebStudio.Templates.Model.Abstract.extend({ });

WebStudio.Templates.Model.Row.prototype.init = function(parent)
{
	// Set the parent object
	this.setParent(parent);

	// Object type	
	this.setObjectType("row");

	// Set up CSS
	this.setCSS("TemplateTableRow");
	this.setTitleCSS("TemplateRowTitle");
	this.setMenuCSS("TemplateRowMenu");
	
	// Set mouseover CSS	
	this.setOnMouseOverCSS("TemplateObjectOnMouseOver");
};

/**
 * Specifies the configuration of the context menu
 */
WebStudio.Templates.Model.Row.prototype.getMenuItemsConfig = function()
{
	var menuItems = [ ];

	menuItems[menuItems.length] = {
		id: 'deleteTableRow', 
		text: "Delete Row", 
		onclick: { 
			fn: this.deleteTableRow.bind(this), 
			obj: [
		          	this.getParent().getId(), 
		           	this.getId()
		    ]
		}
	};
          
    // If 1 or more rows, allow edit and delete commands.
    if(this.getChildCount() > 0)
    {        
    	menuItems[menuItems.length] = {
    		id: 'editColumnSizes', 
			text: "Edit Column Sizes", 
			onclick: { 
    			fn: this.editRowColumnSizes.bind(this), 
    			obj: [
    			     	this.getParent().getId(), 
    			      	this.getId()
    		   ]
    		}
		};
    } 
    else 
    {
    	menuItems[menuItems.length] = {
			id: 'addTableColumns', 
			text: "Add Columns", 
			onclick: { 
    			fn: this.addTableColumns.bind(this), 
    			obj: [
    			      	this.getParent().getId(), 
    			      	this.getId()
    		   ]
    		}
		};
    }	
	return menuItems;	
};

/**
 * Context Menu Event Handler
 */ 
WebStudio.Templates.Model.Row.prototype.deleteTableRow = function(eventType, eventArgs, parmsObj) 
{
	var _this = this;

    // Get Event
    var e = eventArgs[0];

    // Let's handle the event here. Stop event propagation.
    WebStudio.util.stopPropagation(e);

    var url = WebStudio.ws.studio("/api/template/tablelayout/row/delete/get", { type: "template-table", 
                                                                                templateId: this.getParent().getId(),
                                                                                rowId: this.getId(),
                                                                                actionFlag: 'deleteRow'} );                              
    call = YAHOO.util.Connect.asyncRequest('GET', url, {   
        success: function(r) {
            _this.templateDesigner.refresh();        	
        }
        ,
        failure: function(r) {      
            // error deleting region
        }
    });  
};

/**
 * Context Menu Event Handler
 */ 
WebStudio.Templates.Model.Row.prototype.editRowColumnSizes = function(eventType, eventArgs, parmsObj) 
{
	var _this = this;
	
    // Get Event
    var e = eventArgs[0];

    // Let's handle the event here. Stop event propagation.
    WebStudio.util.stopPropagation(e);
    
    var templateId = parmsObj[0];
    
    var rowId = parmsObj[1];        
                      
    var w = new WebStudio.Wizard();
        
    w.setDefaultJson(
    {
        refreshSession: 'true',
        templateId: templateId,
        rowId: rowId,
        actionFlag: 'updateRowPanelSizes'
    });                 
    w.start(WebStudio.ws.studio("/wizard/template/tablelayoutmanager"), 'tablelayoutmanager');
    w.onComplete = function() 
    {
        _this.templateDesigner.refresh();     
    };

};

/**
 * Context Menu Event Handler
 */ 
WebStudio.Templates.Model.Row.prototype.addTableColumns = function(eventType, eventArgs, parmsObj) 
{
	var _this = this;
	
    // Get Event.
    var e = eventArgs[0];
    
    // Stop event propagation.   
    WebStudio.util.stopPropagation(e);
    
    // Get template id
    templateId = parmsObj[0];   
    
    // Get row id parms object.
    rowId = parmsObj[1];
        
    var wizardActionFlag = 'createRowPanelsConfig';
                      
    var w = new WebStudio.Wizard();
        
    w.setDefaultJson(
    {
        refreshSession: 'true',
        templateId: templateId,
        rowId: rowId,
        actionFlag: wizardActionFlag
    });                 
    w.start(WebStudio.ws.studio("/wizard/template/tablelayoutmanager"), 'tablelayoutmanager');
    w.onComplete = function() 
    {
        _this.templateDesigner.refresh();     
    };    
};


/**
 * Renders the row to a given container
 */
WebStudio.Templates.Model.Row.prototype.render = function(container)
{
	this.container = container;

	// row div (set as root element)
	var row = Alf.createElement('div', this.getId());
	YAHOO.util.Dom.addClass(row, this.getCSS());
    WebStudio.util.injectInside(this.container, row);
    this.setElement(row);
    
    // calculate the width of the row
	var width = jQuery(container).width();
	jQuery(row).css({ 'width' : width - 16});
	
	// calculate the height of the row
	var height = (jQuery(container).height() - 16) / this.getTotalChildCount();
	height = height - 16;
	height = Math.floor(height);
    jQuery(row).css( { "height" : height } );	    

	// title div
	var titleElement = Alf.createElement("div", "row_div_" + this.getId());
    YAHOO.util.Dom.addClass(titleElement, this.getTitleCSS());
	WebStudio.util.pushHTML(titleElement, "Row: " + this.getSequenceNumber());
    WebStudio.util.injectInside(row, titleElement);	

	// body div	
	var bodyElement = Alf.createElement("div", "row_div_body_" + this.getId());
	YAHOO.util.Dom.addClass(bodyElement, "TemplateTableRowBody");
	WebStudio.util.injectInside(row, bodyElement);	

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


// ////////////////////////////////////
// Type Specific Methods
// ////////////////////////////////////

WebStudio.Templates.Model.Row.prototype.addColumn = function(columnObject)
{
	this.addChild(columnObject);
};