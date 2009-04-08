WebStudio.Templates.Model.DynamicTemplate = WebStudio.Templates.Model.Abstract.extend({ });

WebStudio.Templates.Model.DynamicTemplate.prototype.init = function(parent)
{	
	// Set the parent object
	this.setParent(parent);

	// Object type	
	this.objectType = "template";
	
	// Set CSS
	this.setCSS("TemplateTable");
	this.setTitleCSS("TemplateTableTitle");
	this.setMenuCSS("TemplateTableMenu");
	
	// Mouse event CSS for template table.
	this.setOnMouseOverCSS("TemplateObjectOnMouseOver");
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

	// template div (set as root element)
	var root = Alf.createElement('div', this.getId());
	YAHOO.util.Dom.addClass(root, this.getCSS());
	WebStudio.util.injectInside(this.container, root);
	this.setElement(root);
	
	// calculate the full width of the template drawing space
	var width = jQuery(container).width();
	jQuery(root).css({ 'width' : width });
	
	// calculate the full height of the template drawing space
	var height = jQuery(container).height();
	jQuery(root).css({ 'height' : height });
		
	// title div
	var titleElement = Alf.createElement('div', "template_div_" + this.getId());	
	YAHOO.util.Dom.addClass(titleElement, this.getTitleCSS());	
	WebStudio.util.pushHTML(titleElement, "Template: " + this.getTitle());	
	WebStudio.util.injectInside(root, titleElement);
	
	// body div	
	var bodyElement = Alf.createElement('div', "template_div_body_" + this.getId());
	YAHOO.util.Dom.addClass(bodyElement, "TemplateTableBody");
	WebStudio.util.injectInside(root, bodyElement);

	// set up events
	this.setupEvents(titleElement);
	this.setupEvents(bodyElement);
	this.setupEvents();

	// render the rows	
	if(this.getChildCount() > 0)
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

WebStudio.Templates.Model.DynamicTemplate.prototype.addRow = function(rowObject)
{
	this.addChild(rowObject);
};