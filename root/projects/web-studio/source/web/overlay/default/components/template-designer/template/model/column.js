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
}


/**
 * Specifies the configuration of the context menu
 */
WebStudio.Templates.Model.Column.prototype.getMenuItemsConfig = function()
{
	var menuItems = [ ];

	/**
	 *  Prep the template, row id's
	 *  that we will need for the column menu commands.
	 */
	
	/**
	 * Get the parent of the column object.
	 * We we will need the id to update appropriate row.
	 */
	var parentRow = this.getParent();
	
	/**
	 * Get the parent of the row object.
	 * We will need the id update appropriate template.
	 */
	var parentTemplate = parentRow.getParent();			
	
	menuItems[menuItems.length] = {
		id: 'deleteTemplateColumn',
		text: "Delete Column", 
		onclick: { 
			fn: this.deleteTemplateColumn.bind(this), 
			obj: [
			      	this.getId(), 
			      	parentRow.getId(), 
			      	parentTemplate.getId()
			] 
		}
	};
	                 
    if(this.getChildCount() == 0)
    {        
		menuItems[menuItems.length] = {
			id: 'addTemplateRegion', 
            text: "Add Region", 
            onclick: { fn: this.addTemplateRegion.bind(this), 
				obj: [
				      	this.getId(), 
				      	parentRow.getId(), 
				      	parentTemplate.getId()
				]
			}
		};
	}
	return menuItems;
}

/**
 * Context Menu
 * Event Handler
 */ 
WebStudio.Templates.Model.Column.prototype.deleteTemplateColumn = function(eventType, eventArgs, parmsObj)
{
    // Get the current Event        
    var e = eventArgs[0];

    // Stop event propagation.
    e.stopPropagation();

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
        success: (function(r) {       
            this.templateDesigner.refresh();       	        	
        }).bind(this),
        failure: function(r) {      
            //error deleting region
        }
    });
}

/**
 * Context Menu
 * Event Handler
 */ 
WebStudio.Templates.Model.Column.prototype.addTemplateRegion = function(eventType, eventArgs, parmsObj)
{
    // Get the current Event        
    var e = eventArgs[0];

    // Stop event propagation.
    e.stopPropagation();
    
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
    
    w.onComplete = (function() 
    {
        this.templateDesigner.refresh();     
    }).bind(this);    
}

/**
 * Renders the template to a given container
 */
WebStudio.Templates.Model.Column.prototype.render = function(container)
{
	
	this.container = container;

	var td = document.createElement('td');	
	td.setAttribute("id", this.getId());	
	td.setAttribute("class", this.getCSS());
	
	td.setStyle("width", this.getWidth() + "%");	

	td.injectInside(this.container);
	
	var columnDiv = document.createElement('div');
	columnDiv.setAttribute("id", "col_div_" + this.getId());
	columnDiv.setAttribute("class", this.getCSS());	
	columnDiv.setStyle("height", "100%");
	columnDiv.setStyle("border", "1px dashed black");
	
	this.setElement(columnDiv);
	
	columnDiv.injectInside(td);

	this.setupEvents(columnDiv)
	
	// title element
	var titleElement = document.createElement('div');
	titleElement.setAttribute("class", this.getTitleCSS());	
	titleElement.setAttribute("id", "column_div_" + this.getId());
	titleElement.setHTML("Column: " + this.getId());	
	titleElement.injectInside(columnDiv);

	// register mouse events
	this.setupEvents(titleElement);
	
	// register mouse events
	this.setupEvents();
	
	// render child objects
	this.renderChildren(columnDiv);
}


//////////////////////////////////////
// Type Specific Methods
//////////////////////////////////////

WebStudio.Templates.Model.Column.prototype.addRegion = function(regionObject)
{
	this.addChild(regionObject);
}