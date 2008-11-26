WebStudio.Templates.Model.Row = WebStudio.Templates.Model.Abstract.extend({ });

WebStudio.Templates.Model.Row.prototype.init = function(parent)
{
	// Set the parent object
	this.setParent(parent);

	// Object type	
	this.setObjectType("row");

	// Set up CSS
	this.setCSS("TemplateTableRow");
	this.setOnMouseOverCSS("RowOnMouseOver");
	this.setOnMouseOutCSS("TemplateTableRow");
	
	this.setTitleCSS("TemplateRowTitle");	
	this.setTitleOnMouseOverCSS("TemplateRowTitleOnMouseOver");	
	this.setTitleOnMouseOutCSS("TemplateRowTitle");
	
	// CSS for menu div.
	this.setMenuCSS("TemplateRowMenu");	
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

	/**
	 * Row container element for this row object.
	 */
	var tr = document.createElement('tr');	
	tr.setAttribute("id", "tr_" + this.getId());
		
	// Set the height of the row.
	// The width will be 100%		    
	WebStudio.util.setStyle(tr, 'height', this.getHeight() + "%");
    
    WebStudio.util.injectInside(this.container, tr);
			
	var td = document.createElement('td');
	td.setAttribute("id", "td_" + this.getId());
	
    WebStudio.util.injectInside(tr, td);	
	
	// root div
	var root = document.createElement('div');
	root.setAttribute("id", this.getId());	
	WebStudio.util.setStyle(root, "height", "100%");		
	WebStudio.util.setStyle(root, "border", "1px dashed black");		
    
	this.setElement(root);
    WebStudio.util.injectInside(td, root);	

	// title element
	var titleElement = document.createElement('div');
    YAHOO.util.Dom.addClass(titleElement, this.getTitleCSS());   		
	titleElement.setAttribute("id", "row_div_" + this.getId());		
	WebStudio.util.pushHTML(titleElement, "Row: " + this.getSequenceNumber());

    WebStudio.util.injectInside(root, titleElement);	
		
	// register mouse events
	this.setupEvents(titleElement);		

	/**
	 * Container element for all columns in current row.
	 * This table should be at 100% for the height and the width.
	 */
	var columnsTable = document.createElement("table");
	columnsTable.setAttribute("id", "table_" + this.getId());
	WebStudio.util.setStyle(columnsTable, "width", "100%");	
	columnsTable.setAttribute("cellspacing", "0px");
	
	// This needs to be at 100%, for the table
	// to take up the entire height of the 
	// parent div.		
	WebStudio.util.setStyle(columnsTable, "height", "93%");	
		
    // Inject the table that will contain all columsn
    // into the root element.
	WebStudio.util.injectInside(root, columnsTable);

	/**
     * A TBODY element is required for IE 
     */
    var tableBody = document.createElement('tbody');       
    tableBody.setAttribute("id", "columns_table_tbody" + this.getId());
	YAHOO.util.Dom.addClass(tableBody, "TemplateTableTBodyRow");
	
	WebStudio.util.injectInside(columnsTable, tableBody);
	
    /**
     * This row element will contain all of the 
     * column elements for current row object.
     */
    var columnsRow = document.createElement('tr');       
    columnsRow.setAttribute("id", "inner_tr_" + this.getId());
    	
    this.setElement(tableBody);
    
    /**
     * Add row element to columns table element.
     */    
    WebStudio.util.injectInside(tableBody, columnsRow);    

	// register mouse events for tbody.
	this.setupEvents(tableBody);
        
	if (this.getChildCount() > 0)
	{		
		// render child objects
		this.renderChildren(columnsRow);
	} 
	else 
	{	
		// We will not be able to highlight an empty row.
		// Let's add a placeholder column, until
		// the row contains child elements.
		var placeHolderColumn = document.createElement("td");
		
		placeHolderColumn.setAttribute("id", "placeHolderColumn_" + this.getId());
		
		var rowDimensions = "&nbsp; Height: " + Math.round(this.getHeight()) + "%<br>&nbsp;Width: " + "100%";
				
		WebStudio.util.pushHTML(placeHolderColumn, rowDimensions);
		
		// Inject inside the row element that will
		// contain all columns for this object.
		WebStudio.util.injectInside(columnsRow, placeHolderColumn);
		
		// Setup mouse event listeners for this
		// td element.
		this.setupEvents(placeHolderColumn);

		// Let's make this element the root element,
		// for properly processing event notifications.
		// This will only be the case of the row does 
		// not contain any child elements.
		this.setElement(placeHolderColumn);
	}
};


// ////////////////////////////////////
// Type Specific Methods
// ////////////////////////////////////

WebStudio.Templates.Model.Row.prototype.addColumn = function(columnObject)
{
	this.addChild(columnObject);
};