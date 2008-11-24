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
};

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
};

/**
 * Context Menu
 * Event Handler
 */ 
WebStudio.Templates.Model.DynamicTemplate.prototype.addTemplateRows = function(eventType, eventArgs, parmsObj) 
{         
	var _this = this;	
   
    var target = YAHOO.util.Event.getTarget(eventArgs[0]);
    
    var e = eventArgs[0];

    WebStudio.util.stopPropagation(e);    

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
    w.onComplete = function() 
    {
        _this.templateDesigner.refresh();     
    };                     
};

/**
 * Context Menu
 * Event Handler
 * (not currently implemented)
 */
WebStudio.Templates.Model.DynamicTemplate.prototype.deleteTemplateRows = function(eventType, eventArgs, parmsObj) 
{         
	var templateId = this.getId();
	
	// TODO: incomplete method?
};

/**
 * Context Menu
 * Event Handler
 */
WebStudio.Templates.Model.DynamicTemplate.prototype.editTemplateRowSizes = function(eventType, eventArgs, parmsObj) 
{     
	var _this = this;
	            
    // Current target.
    var target = YAHOO.util.Event.getTarget(eventArgs[0]);
    
    // Get the current Event        
    var e = eventArgs[0];

    // Stop event propagation.
    WebStudio.util.stopPropagation(e);    

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
    w.onComplete = function() {
        _this.templateDesigner.refresh();     
    };  
};

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

	// set as root element for this object.
	this.setElement(root);
	
	// Add into DOM element.
	WebStudio.util.injectInside(this.container, root);
				   
	// title element
	var titleElement = document.createElement('div');
	titleElement.setAttribute("id", "template_div_" + this.getId());	
	YAHOO.util.Dom.addClass(titleElement, this.getTitleCSS());	
	WebStudio.util.pushHTML(titleElement, "Template: " + this.getTitle());
	
	WebStudio.util.injectInside(root, titleElement);	

	// register mouse events for given element.
	this.setupEvents(titleElement);
	
	// Template Table.
	var table = document.createElement('table');
	YAHOO.util.Dom.addClass(table, this.getCSS());	
	table.setAttribute("id", "templateTable");	
	table.setAttribute("cellspacing", "5px");		
	table.setAttribute("cellpadding", "5px");	

	// Template Table.
	var tableBody = document.createElement('tbody');
	tableBody.setAttribute("id", "templateTableBody");
	
	// Add to DOM.
	WebStudio.util.injectInside(root, table);

	WebStudio.util.injectInside(table, tableBody);
	
	// register mouse events, for root element.
	this.setupEvents();

	if(this.getChildCount() > 0)
	{
		// render child objects		
		this.renderChildren(tableBody);		
	}
	else
	{
		// To ensure that the entire template region is active,
		// - even if there are no rows -
		// let's increase the size of the title element to
		// have it cover 100% of the template area.
		
		var tr = document.createElement("tr");
		
		var td = document.createElement("td");
		td.setAttribute("id", "empty_template_td_" + this.getId());
		YAHOO.util.Dom.addClass(td, this.getCSS());	
		
		this.setElement(td);
		this.setupEvents(td);
		
		WebStudio.util.pushHTML(td, "&nbsp;&nbsp;");
		
		WebStudio.util.injectInside(tr, td);
		WebStudio.util.injectInside(tableBody, tr);
	}
};


//////////////////////////////////////
// Type Specific Methods
//////////////////////////////////////

WebStudio.Templates.Model.DynamicTemplate.prototype.addRow = function(rowObject)
{
	this.addChild(rowObject);
};