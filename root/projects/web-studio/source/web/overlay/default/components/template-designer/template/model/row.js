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
}


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
}

/**
 * Context Menu Event Handler
 */ 
WebStudio.Templates.Model.Row.prototype.deleteTableRow = function(eventType, eventArgs, parmsObj) 
{

    // Get Event
    var e = eventArgs[0];

    // Let's handle the event here. Stop event propagation.
    e.stopPropagation();

    var url = WebStudio.ws.studio("/api/template/tablelayout/row/delete/get", { type: "template-table", 
                                                                                templateId: this.getParent().getId(),
                                                                                rowId: this.getId(),
                                                                                actionFlag: 'deleteRow'} );                              
    call = YAHOO.util.Connect.asyncRequest('GET', url, {   
        success: (function(r) {
            this.templateDesigner.refresh();        	
        }).bind(this)
        ,
        failure: function(r) {      
            // error deleting region
        }
    });  
}

/**
 * Context Menu Event Handler
 */ 
WebStudio.Templates.Model.Row.prototype.editRowColumnSizes = function(eventType, eventArgs, parmsObj) 
{
    // Get Event
    var e = eventArgs[0];

    // Let's handle the event here. Stop event propagation.
    e.stopPropagation();
    
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
    w.onComplete = (function() 
    {
        this.templateDesigner.refresh();     
    }).bind(this);

}

/**
 * Context Menu Event Handler
 */ 
WebStudio.Templates.Model.Row.prototype.addTableColumns = function(eventType, eventArgs, parmsObj) 
{
    // Get Event.
    var e = eventArgs[0];

    // Stop event propagation.
    e.stopPropagation();
               
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
    w.onComplete = (function() 
    {
        this.templateDesigner.refresh();     
    }).bind(this);
    
}


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
	tr.setAttribute("id", "tr_" + this.getId())
	YAHOO.util.Dom.addClass(tr, this.getCSS());
		
	// Set the height of the row.
	// The width will be 100%
    // todo: allow ability to use pixels as well as % for size.		    
    tr.setStyle('height', this.getHeight() + "%");	
    
	tr.injectInside(this.container);	
			
	var td = document.createElement('td');
	td.setAttribute("id", "td_" + this.getId())
	td.injectInside(tr);
	
	// root div
	var root = document.createElement('div');
	root.setAttribute("id", this.getId());	
	YAHOO.util.Dom.addClass(root, this.getCSS());
	root.setStyle("border", "1px dashed black");
    
	this.setElement(root);
	root.injectInside(td);

	// title element
	var titleElement = document.createElement('div');
    YAHOO.util.Dom.addClass(titleElement, this.getTitleCSS());   		
	titleElement.setAttribute("id", "row_div_" + this.getId());
	titleElement.setHTML("Row: " + this.getId());		
	titleElement.injectInside(root);	
		
	// register mouse events
	this.setupEvents(titleElement);		

	/**
	 * Container element for all columns in current row.
	 * This table should be at 100% for the height and the width.
	 */
	var columnsTable = document.createElement('table');
	columnsTable.setAttribute("id", "table_" + this.getId());			
	columnsTable.setStyle('width', "100%");
	// This needs to be at 100%, for the table
	// to take up the entire height of the 
	// parent div.	
	columnsTable.setStyle('height', "100%");		
		
    // Inject the table that will contain all columsn
    // into the root element.
    columnsTable.injectInside(root);
        
    /**
     * This row element will contain all of the 
     * column elements for current row object.
     */
    var columnsRow = document.createElement('tr');       
    columnsRow.setAttribute("id", "inner_tr_" + this.getId());
    	
    // Register mouse events for columnts table.
    this.setupEvents(columnsRow); 
	
    /**
     * Add row element to columns table element.
     */
	columnsRow.injectInside(columnsTable);

	// register mouse events
	this.setupEvents();

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
		placeHolderColumn.setAttribute("id", "placeHolderColumn");
		placeHolderColumn.setHTML("&nbsp;");
		placeHolderColumn.injectInside(columnsRow);
		this.setupEvents(placeHolderColumn);

		// Let's make this element the root element,
		// for properly processing event notifications.
		this.setElement(placeHolderColumn);
	}
}


// ////////////////////////////////////
// Type Specific Methods
// ////////////////////////////////////

WebStudio.Templates.Model.Row.prototype.addColumn = function(columnObject)
{
	this.addChild(columnObject);
}