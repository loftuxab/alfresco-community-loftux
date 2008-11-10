WebStudio.Templates.Model.DynamicTemplate = WebStudio.Templates.Model.Abstract.extend({ });

WebStudio.Templates.Model.DynamicTemplate.prototype.init = function(parent)
{	
	// Set the parent object
	this.setParent(parent);

	// Object type	
	this.objectType = "template";
	
	// Set up default tamplate table CSS.
	this.setCSS("TemplateTable");
	
	// Mouse event CSS for template table.
	this.setOnMouseOverCSS("TemplateTableOnMouseOver");
	this.setOnMouseOutCSS("TemplateTable");
	
	// Default Title div CSS.
	this.setTitleCSS("TemplateTableTitle");
	
	// Mouse event CSS for Title div.
	this.setTitleOnMouseOverCSS("TemplateTitleOnMouseOver");	
	this.setTitleOnMouseOutCSS("TemplateTableTitle");
	
	// CSS for menu div.
	this.setMenuCSS("TemplateTableMenu");	
}

/**
 * Specifies the configuration of the context menu
 */
WebStudio.Templates.Model.DynamicTemplate.prototype.getMenuItemsConfig = function()
{
	var menuItems = [ ];
	
	if(this.getChildCount() > 0)
	{
		menuItems = [
			{
				id: 'editRowSizes', 
				text: "Edit Row Sizes", 
				onclick: { 
					fn: this.editTemplateRowSizes.bind(this), 
					obj: [this.getId()] 
				}
			}
		];
	}
	else
	{
		menuItems = [
			{
				id: 'addRows', 
				text: "Add Rows", 
				onclick: { 
					fn: this.addTemplateRows.bind(this), 
					obj: [this.getId()] 
				}
			}
		];	
	}
	
	return menuItems;
}

/**
 * Context Menu
 * Event Handler
 */ 
WebStudio.Templates.Model.DynamicTemplate.prototype.addTemplateRows = function(eventType, eventArgs, parmsObj) 
{         	
   
    var target = YAHOO.util.Event.getTarget(eventArgs[0]);
    
    var e = eventArgs[0];

    e.stopPropagation();

    //var templateId = parmsObj[0];
    var templateId = this.getId();
     
    var w = new WebStudio.Wizard();
        
    w.setDefaultJson(
    {
        refreshSession: 'true',
        templateId: templateId,                
        actionFlag: 'createTemplateRowsConfig'
    });                 
    
    w.start(WebStudio.ws.studio("/wizard/template/tablelayoutmanager"), 'tablelayoutmanager');
    w.onComplete = (function() 
    {
        this.templateDesigner.refresh();     
    }).bind(this);
                         
};

/**
 * Context Menu
 * Event Handler
 * (not currently implemented)
 */
WebStudio.Templates.Model.DynamicTemplate.prototype.deleteTemplateRows = function(eventType, eventArgs, parmsObj) 
{         
	var templateId = this.getId();

}

/**
 * Context Menu
 * Event Handler
 */
WebStudio.Templates.Model.DynamicTemplate.prototype.editTemplateRowSizes = function(eventType, eventArgs, parmsObj) 
{                 
    // Current target.
    var target = YAHOO.util.Event.getTarget(eventArgs[0]);
    
    // Get the current Event        
    var e = eventArgs[0];

    // Stop event propagation.
    e.stopPropagation();

    // Get the template id
    //var templateId = parmsObj[0];
    var templateId = this.getId();   
              
    var w = new WebStudio.Wizard();
        
    w.setDefaultJson(
    {
        refreshSession: 'true',
        templateId: templateId,                
        actionFlag: 'updateTemplateRowsConfig'
    });                 
    
    w.start(WebStudio.ws.studio("/wizard/template/tablelayoutmanager"), 'tablelayoutmanager');
    w.onComplete = (function() 
    {
        this.templateDesigner.refresh();     
    }).bind(this);
    
}

/**
 * Renders the template to a given container
 */
WebStudio.Templates.Model.DynamicTemplate.prototype.render = function(container)
{
	// special case for top node
	if(!container)
	{	
		 container = this.getEditor();			 
	}
	
	this.container = container;	 

	// root div
	var root = document.createElement('div');        
	root.setAttribute("id", this.getId());
	
	// Set CSS.
    YAHOO.util.Dom.addClass(root, 'yui-skin-sam');
    YAHOO.util.Dom.addClass(root, this.getMenuCSS());   	
	
    // set as root element for this object.
	this.setElement(root);
	
	// Add into DOM element.
	root.injectInside(this.container);
	
	// title element
	var titleElement = document.createElement('div');
	titleElement.setAttribute("id", "template_div_" + this.getId());	
	YAHOO.util.Dom.addClass(titleElement, this.getTitleCSS());	
	titleElement.setHTML("Template: " + this.getTitle());
	titleElement.injectInside(root);	
	
	// register mouse events for given element.
	this.setupEvents(titleElement);
	
	// Template Table.
	var table = document.createElement('table');
	table.setAttribute("id", "templateTable")
	YAHOO.util.Dom.addClass(table, this.getCSS());
		
	// Add to DOM.
    table.injectInside(root);
	
	// register mouse events, for root element.
	this.setupEvents();
	
	// render child objects
	this.renderChildren(table);	
}


//////////////////////////////////////
// Type Specific Methods
//////////////////////////////////////

WebStudio.Templates.Model.DynamicTemplate.prototype.addRow = function(rowObject)
{
	this.addChild(rowObject);
}