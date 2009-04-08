WebStudio.Templates.Model.Abstract = new Class({
  initialize: function(id, title, templateDesigner) {
	this.id = id;
	
	/**
	 * Type of object that will extend
	 * this class. (template, row, column,
	 * and region are current possible values.
	 */
	this.objectType = null;
	
	this.title = title;
	this.height = null;
	this.width = null;
	
	// Initial CSS class name for element.
	this.css = null;
	
	// CSS for mouse events.
	this.onMouseOverCSS = null;
	
	// Initial CSS class name for Title div.
	this.titleCSS = null;
		
	// Menu CSS class name.
	this.menuCSS = null;	
	
	// Reference to current object.
	this.el = null;
	
	// Reference to parent object.
	this.parent = null;
	
	this.sequenceNumber = null;
	
	/**
	 * Reference to Template designer, which is the 
	 * the object that instantiated the tempate renderer.
	 */
	this.templateDesigner = templateDesigner;
	
	this.children = [];
	
	this.CONTEXT_MENU_DIV = "templateContextMenuDiv";
	this.CONTEXT_MENU_NAME = "templateContextMenu";
	
  }
});

/** TO OVERRIDE **/
WebStudio.Templates.Model.Abstract.prototype.init = function()
{
};

/** TO OVERRIDE **/
WebStudio.Templates.Model.Abstract.prototype.getMenuConfiguration = function()
{
	// return null for no context menu
	return null;
};

/** @OVERRIDE **/
WebStudio.Templates.Model.Abstract.prototype.render = function(container)
{
};

WebStudio.Templates.Model.Abstract.prototype.destroy = function()
{
	// Walk through all of the children and call destroy on them
    if (this.getChildCount() > 0)
    {
        var children = this.getChildren();
        for (var i = 0; i < children.length; i++)
        {
        	var child = children[i];
        	child.destroy();
        }
    }
    
    if(this.getElement())
    {
        // Remove all listeners and recurse children 
        YAHOO.util.Event.purgeElement(this.getElement(), true);
        
        // Remove the element itself from the DOM. This should remove the children as well.
        if(this.getElement().parentNode)
        {
        	this.getElement().parentNode.removeChild(this.getElement());
        }
   }                      
  
    // Destroy object's context menu.
    this.destroyMenu();    
};

WebStudio.Templates.Model.Abstract.prototype.destroyMenu = function(element)
{
	// Look for existing context menu div in the DOM.
	var contextMenuDiv = document.getElementById(this.CONTEXT_MENU_DIV);
	
	// Let's purge it, along with child nodes.
	this.purgeElement(contextMenuDiv);

   // Check for a menu for this object in the menu manager
   var objContextMenu = YAHOO.widget.MenuManager.getMenu(this.CONTEXT_MENU_NAME);
   if(objContextMenu && objContextMenu != 'undefined')
   {
		// Remove listeners for menu and all child notes.
		YAHOO.util.Event.purgeElement(objContextMenu, true);
	     
        // Remove menu from the menu manager.   
        YAHOO.widget.MenuManager.removeMenu(objContextMenu);
   }	
};

/*
 *  Register event handlers for DOM element.
 */
WebStudio.Templates.Model.Abstract.prototype.setupEvents = function(el)
{
	/**
	 * DOM element that we will add event listeners to.
	 * If the param element is null or undefined,
	 * let's add the listeners to the root element.
	 */

	if (!el)
	{
		el = this.getElement();
	}
	
	YAHOO.util.Event.addListener(el, 
                                 "mouseover", 
                                 this.onMouseOver, 
                                 el, 
                                 this);
        
    YAHOO.util.Event.addListener(el, 
                                 "mouseout", 
                                 this.onMouseOut, 
                                 el, 
                                 this);
    
    YAHOO.util.Event.addListener(el, 
                                 "click", 
                                 this.onClick, 
                                 el, 
                                 this);
};

WebStudio.Templates.Model.Abstract.prototype.getEditor = function()
{
	return this.templateDesigner.Editor.el;
};

WebStudio.Templates.Model.Abstract.prototype.getBindingProperties = function()
{
	/*
	 *  Set of object Id's that will be used
	 *  for updating child objects in repository.
	 */ 
	var bindingProperties = null;

	/**
	 * If the current object has a parent
	 * let's call the parent to get the 
	 * Id for it.
	 */
		
	if(this.getParent())
	{		
		/**
		 * Since we have a parent, let's get the 
		 * binding properties for it and add it to
		 * the bindings for the current object.
		 */
		bindingProperties = this.getParent().getBindingProperties();
		
		/**
		 * Add the binding for current object.
		 */
		bindingProperties[this.getObjectType()] = this.getId();		
	} 
	else 
	{
		/**
		 * Let's creat the initial binding properties object.
		 */
		bindingProperties = { };
		
		// Let's add the binding for the current object.
		bindingProperties[this.getObjectType()] = this.getId();				
	}	
	return bindingProperties;
};

WebStudio.Templates.Model.Abstract.prototype.getParent = function()
{
	return this.parent;
};

WebStudio.Templates.Model.Abstract.prototype.setParent = function(parent)
{
	this.parent = parent;
};

WebStudio.Templates.Model.Abstract.prototype.getObjectType = function()
{
	return this.objectType;
};

WebStudio.Templates.Model.Abstract.prototype.setObjectType = function(objectType)
{
	this.objectType = objectType;
};

WebStudio.Templates.Model.Abstract.prototype.getElement = function()
{
	return this.el;
};

WebStudio.Templates.Model.Abstract.prototype.setElement = function(el)
{
	this.el = el;
};

WebStudio.Templates.Model.Abstract.prototype.getChildren = function()
{
	if(!this.children)
	{
		this.children = [];
	}
	return this.children;
};

/**
 * Adds a child
 */
WebStudio.Templates.Model.Abstract.prototype.addChild = function(child)
{
	if(!this.children)
	{
		this.children = [];
	}
	this.children[this.children.length] = child;   
};

/**
 * Retrieves a child by id
 */
WebStudio.Templates.Model.Abstract.prototype.getChild = function(id)
{
	var _child = null;
	
	if(!this.children)
	{
		this.children = [];
	}
	for(var i = 0; i < this.children.length; i++)
	{
		var child = this.children[i];
		if(child)
		{
			if(child.getId() == id)
			{
				_child = child;
			}
		}
	}

	return _child;    
};

WebStudio.Templates.Model.Abstract.prototype.renderChildren = function(parentElement)
{
	var children = this.getChildren();
	if(children)
	{	
		for(var i = 0; i < children.length; i++)
		{			
			var child = children[i];
			
			// Initialize child object.
			// Pass it the parent object.
			child.init(this);	
			
			// This will be used as the label for 
			// components that don't have names,
			// like rows and columns.
			child.setSequenceNumber(i+1);

			// Total number of elements
			child.setTotalChildCount(children.length);
			
			// Call render method on child
			// and pass in the container element.
			child.render(parentElement);
		} 
    }
};

WebStudio.Templates.Model.Abstract.prototype.getChildCount = function()
{
	var size = 0;
	
	var children = this.getChildren();
	if(children)
	{
		size = children.length;
	}
	
	return size;
};

WebStudio.Templates.Model.Abstract.prototype.getId = function()
{
    return this.id;    
};

WebStudio.Templates.Model.Abstract.prototype.setTitle = function(title)
{
	this.title = title;
};

WebStudio.Templates.Model.Abstract.prototype.getTitle = function()
{
    return this.title;
};

WebStudio.Templates.Model.Abstract.prototype.setDescription = function(description)
{
    this.description = description;
};

WebStudio.Templates.Model.Abstract.prototype.getDescription = function()
{
    return this.description;
};

WebStudio.Templates.Model.Abstract.prototype.setHeight = function(height)
{
	this.height = height;
};

WebStudio.Templates.Model.Abstract.prototype.getHeight = function()
{
    return this.height;
};

WebStudio.Templates.Model.Abstract.prototype.setWidth = function(width)
{
	this.width = width;
};

WebStudio.Templates.Model.Abstract.prototype.getWidth = function()
{
    return this.width;
};

WebStudio.Templates.Model.Abstract.prototype.getSequenceNumber = function()
{
    return this.sequenceNumber;
};

WebStudio.Templates.Model.Abstract.prototype.setSequenceNumber = function(sequenceNumber)
{
	this.sequenceNumber = sequenceNumber;
};

WebStudio.Templates.Model.Abstract.prototype.getTotalChildCount = function()
{
	return this.totalChildCount;
};

WebStudio.Templates.Model.Abstract.prototype.setTotalChildCount = function(count)
{
	this.totalChildCount = count;
};

WebStudio.Templates.Model.Abstract.prototype.setCSS = function(css)
{
    this.css = css;
};

WebStudio.Templates.Model.Abstract.prototype.getCSS = function()
{
	return this.css;
};

WebStudio.Templates.Model.Abstract.prototype.setOnMouseOverCSS = function(css)
{
    this.onMouseOverCSS = css;
};

WebStudio.Templates.Model.Abstract.prototype.getOnMouseOverCSS = function()
{
	return this.onMouseOverCSS;
};

WebStudio.Templates.Model.Abstract.prototype.setTitleCSS = function(css)
{
    this.titleCSS = css;
};

WebStudio.Templates.Model.Abstract.prototype.getTitleCSS = function()
{
	return this.titleCSS;
};

WebStudio.Templates.Model.Abstract.prototype.setMenuCSS = function(css)
{
    this.menuCSS = css;
};

WebStudio.Templates.Model.Abstract.prototype.getMenuCSS = function()
{
	return this.menuCSS;
};

WebStudio.Templates.Model.Abstract.prototype.onMouseOver = function(e, element)
{    	
	if (WebStudio.Templates.hasContextMenu)
	{
		return;
	}
	
    var target = YAHOO.util.Event.getTarget(e);    
	     
    // Determine title div name for this object.
    var titleDivName = this.getObjectType() + "_div_" + this.getId();
    var bodyDivName = this.getObjectType() + "_div_body_" + this.getId();
	     
    // Check target to see if it is the title div.    
    if (target.id == titleDivName ||
        target.id == bodyDivName ||
        target.id == element.id)    
    {
        // Stop event from propagation to other DOM elements
        WebStudio.util.stopPropagation(e);
        
        this.startMouseOver();
    }
};

WebStudio.Templates.Model.Abstract.prototype.onMouseOut = function(e, element)
{
	if (WebStudio.Templates.hasContextMenu)
	{
		return;
	}
	
	// Get the target element.
	// We will use to to determine what 
	// UI actions to take.
	var target = YAHOO.util.Event.getTarget(e);
	
    // Determine title div name for this object.
    var titleDivName = this.getObjectType() + "_div_" + this.getId();
    var bodyDivName = this.getObjectType() + "_div_body_" + this.getId();

    // Check target to see if it is the title div.    
    if (target.id == titleDivName ||
        target.id == bodyDivName ||
        target.id == element.id)    
    {
        // Stop event from propagation to other DOM elements
        WebStudio.util.stopPropagation(e);
        
        this.clearMouseOver();
    }
};    	

WebStudio.Templates.Model.Abstract.prototype.clearMouseOver = function()
{
    var titleDivName = this.getObjectType() + "_div_" + this.getId();
    var bodyDivName = this.getObjectType() + "_div_body_" + this.getId();

   	// Update root element's css
   	jQuery(this.getElement()).removeClass(this.getOnMouseOverCSS());

	// Update title div
	titleDiv = document.getElementById(titleDivName);
	jQuery(titleDiv).removeClass(this.getOnMouseOverCSS());

	// Update body div
	bodyDiv = document.getElementById(bodyDivName);
	jQuery(bodyDiv).removeClass(this.getOnMouseOverCSS());
};

WebStudio.Templates.Model.Abstract.prototype.startMouseOver = function()
{
    var titleDivName = this.getObjectType() + "_div_" + this.getId();
    var bodyDivName = this.getObjectType() + "_div_body_" + this.getId();

   	// Update root element's css
   	jQuery(this.getElement()).addClass(this.getOnMouseOverCSS());

	// Update title div
	titleDiv = document.getElementById(titleDivName);
	jQuery(titleDiv).addClass(this.getOnMouseOverCSS());

	// Update body div
	bodyDiv = document.getElementById(bodyDivName);
	jQuery(bodyDiv).addClass(this.getOnMouseOverCSS());
};

WebStudio.Templates.Model.Abstract.prototype.getContextMenuDivId = function()
{
	return "contextMenu_" + this.getId();
};

WebStudio.Templates.Model.Abstract.prototype._createContextMenu = function()
{
	// Let's make sure that that previous
	// menu components are removed from the DOM.
	this.destroyMenu();
		                
	/**
	 *  Create a clean context menu container div 
	 *  and insert into the Template object's root element.
	 */
	var contextMenuDiv = document.createElement('div');	
	contextMenuDiv.setAttribute('id', this.CONTEXT_MENU_DIV);
    YAHOO.util.Dom.addClass(contextMenuDiv, 'yui-skin-sam');
    YAHOO.util.Dom.addClass(contextMenuDiv, this.getMenuCSS());    
    
	/**
	 * Add menu div container to 
	 * current object's DOM element.
	 */
//	contextMenuDiv.injectInside(this.getElement());
    WebStudio.util.injectInside(this.getElement(), contextMenuDiv);
	
	// Reference to context menu obj.                                            
	var objContextMenu = null;      
	
	// Let's look for a previous template context menu in the DOM.
	var contextMenuElement = document.getElementById(this.CONTEXT_MENU_NAME);
	
	/**
	 * If we find on, let's make sure it is defined
	 * and then we can try to retrieve it from the YUI MenuManager
	 */
	if(contextMenuElement && contextMenuElement != 'undefined')
	{
	    // Get menu from menu manager.
	    objContextMenu = YAHOO.widget.MenuManager.getMenu(this.CONTEXT_MENU_NAME);
	
	    // Check for valid menu.
	    if(objContextMenu && objContextMenu != 'undefined')
	    {
	        // Clear all menu content.
	        objContextMenu.clearContent();                    
	    } else {
	        // Create menu object.            
	        objContextMenu = new YAHOO.widget.Menu(this.CONTEXT_MENU_NAME, 
	        										{ 
	        											fixedcenter: false 
	        										});                       
	     
	        // Added to menu manager.
	        YAHOO.widget.MenuManager.addMenu(objContextMenu);            
	    }             
	    // Add menu items.
	    this._addMenuItems(objContextMenu);  
	} else {
	    // Let's create new menu object
	    objContextMenu = new YAHOO.widget.Menu(this.CONTEXT_MENU_NAME, 
	    									    {
	    											fixedcenter: false 
	    										});                       
	
	    // Add menu items
	    this._addMenuItems(objContextMenu);
	            
	    // Add menu to menu manager
	    YAHOO.widget.MenuManager.addMenu(objContextMenu);     
	}    
	
	return objContextMenu;
};

WebStudio.Templates.Model.Abstract.prototype._addMenuItems = function(objContextMenu) 
{
	var menuItems = this.getMenuItemsConfig();
	
	objContextMenu.addItems(menuItems);    
};

WebStudio.Templates.Model.Abstract.prototype.onClick = function(e, element)
{
    // Get click target.
    var target = YAHOO.util.Event.getTarget(e);    
    
    // Let's make sure that the onclick event was fired on the template.
    if((target.id !== '') && (target.id == element.id))
    {
        WebStudio.util.stopPropagation(e);
        
        // Create template menu. 
        var objContextMenu = this._createContextMenu();                  
        
        // Attach menu to contextMenuDiv.
        objContextMenu.render(this.CONTEXT_MENU_DIV);
        
        // mark that the context menu is in effect
		if (objContextMenu)
		{
			WebStudio.Templates.hasContextMenu = true;
		}
        
        // subscribe to hide event
        var _this = this;
        objContextMenu.hideEvent.subscribe(function() {
        	_this.clearMouseOver();
        	WebStudio.Templates.hasContextMenu = false;
        });

        // Make menu visibie.
        objContextMenu.show();

        // Get DOM element for menu.
        var contextMenuElement = document.getElementById(this.CONTEXT_MENU_NAME);

        // Set position to absolute.        
        WebStudio.util.setStyle(contextMenuElement, "position", "absolute");
        
        // Set X,Y coordinates.              
        WebStudio.util.setStyle(contextMenuElement, 'top', (e.clientY - 18));
        
        WebStudio.util.setStyle(contextMenuElement, 'left', (e.clientX - 269));        
    }
};

WebStudio.Templates.Model.Abstract.prototype.purgeElement = function(element) 
{         
    if(element)
    {
    	// Let's purge listeners from child elements.
        YAHOO.util.Event.purgeElement(element, true);
        
        // Let's remove the element from the DOM.
    	element.parentNode.removeChild(element);
    }                    
};

WebStudio.Templates.Model.Abstract.prototype.setHeight = function(height)
{
	this.height = height;
};

WebStudio.Templates.Model.Abstract.prototype.setWidth = function(width)
{
	this.width = width;
};

WebStudio.Templates.Model.Abstract.prototype.setSize = function(width, height)
{
	this.width = width;
	this.height = height;
};
