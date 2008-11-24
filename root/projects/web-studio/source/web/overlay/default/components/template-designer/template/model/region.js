WebStudio.Templates.Model.Region = WebStudio.Templates.Model.Abstract.extend({ });

WebStudio.Templates.Model.Region.prototype.init = function(parent)
{
	// Set the parent object
	this.setParent(parent);
	
	// Object type
	this.setObjectType("region");
	
	// Set up CSS
	this.setCSS("TemplateRegion");
	this.setOnMouseOverCSS("RegionOnMouseOver");
	this.setOnMouseOutCSS("TemplateRegion");
	
	this.setTitleCSS("TemplateRegionTitle");	
	this.setTitleOnMouseOverCSS("TemplateRegionTitleOnMouseOver");	
	this.setTitleOnMouseOutCSS("TemplateRegionTitle");

	// CSS for menu div.
	this.setMenuCSS("TemplateRegionMenu");
};


/**
 * Specifies the configuration of the context menu
 */
WebStudio.Templates.Model.Region.prototype.getMenuItemsConfig = function()
{
	var menuItems = [ ];

	menuItems[menuItems.length] = {
		id: 'deleteTemplateRegion', 
		text: "Delete Region", 
		onclick: { 
			fn: this.deleteTemplateRegion.bind(this)
		}
	};

	menuItems[menuItems.length] = {
		id: 'editTemplateRegion', 
		text: "Edit Region", 
		onclick: { 
			fn: this.editTemplateRegion.bind(this)
		}
	};
                 
	return menuItems;
};

/**
 * Context Menu
 * Event Handler
 */ 
WebStudio.Templates.Model.Region.prototype.editTemplateRegion = function(eventType, eventArgs, parmsObj) 
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
        panelId: bindingProperties["column"],
        regionId: this.getId(),
        actionFlag: 'editRegion'
    });                 

    w.start(WebStudio.ws.studio("/wizard/regionmanager/tablelayout"), 'tablelayout');
    
    w.onComplete = function() 
    {
    	_this.templateDesigner.refresh();     
    };
};

WebStudio.Templates.Model.Region.prototype.deleteTemplateRegion = function(eventType, eventArgs, parmsObj) 
{   
	var _this = this;
	
    // Get Event
    var e = eventArgs[0];

    // Stop event propagation.
    WebStudio.util.stopPropagation(e);

	/**
	 * Get the binding properties for parent objects.
	 * Those properties will serve as object id's for
	 * updating the column info in the repo.
	 */
	var bindingProperties = this.getBindingProperties();
    
    var url = WebStudio.ws.studio("/api/template/tablelayout/region/delete/get", 
    							 { 
    								type: "template-table", 
    						        templateId: bindingProperties["template"],
    						        rowId: bindingProperties["row"],
    						        panelId: bindingProperties["column"],
    						        regionId: this.getId(),
                                    actionFlag: 'deleteRegion'
                                   } 
    							);
    
    call = YAHOO.util.Connect.asyncRequest('GET', url, {   
        success: function(r) {
        	_this.templateDesigner.refresh();
        }
        ,
        failure: function(r) {      
        //error deleting region
        }
    });                                                              
};

/**
 * Renders the template to a given container
 */
WebStudio.Templates.Model.Region.prototype.render = function(container)
{
	this.container = container;

	// Create the div element that will contain
	// the region.
	var regionDiv = document.createElement('div');
	regionDiv.setAttribute("id", this.getId());
	
	// Default CSS class for region div.
	YAHOO.util.Dom.addClass(regionDiv, this.getCSS());
	
	var regionHeight = null;
	
	// If a height was specified on creation of
	// region object let's get it.
	if(this.getHeight())
	{
		regionHeight = this.getHeight() + "%";
		regionHeight = (this.getHeight() - 15) + "%";		
	}
	else 
	{
		// If not, let's assign a height.
		var childCount = this.getParent().getChildCount();
		regionHeight = ((100/childCount) - 15) + "%";		
	}
	
	// Set the height
	WebStudio.util.setStyle(regionDiv, "height", regionHeight);	
	
	// Add region div to parent container.
	WebStudio.util.injectInside(container, regionDiv);
	
	// Make the region div the root
	// element for this object.
	this.setElement(regionDiv);
	
	// title element
	var titleElement = document.createElement('div');
	
	// Set default title css for region.
	YAHOO.util.Dom.addClass(titleElement, this.getTitleCSS());
	
	// Set ID. This will later be used for mouse over events.
	titleElement.setAttribute("id", "region_div_" + this.getId());
	
	// Set Title text.
	WebStudio.util.pushHTML(titleElement, "Region Name: " + this.getTitle());
	
	// Add to region div element.
	WebStudio.util.injectInside(regionDiv, titleElement);
	
	// register mouse events for title.
	this.setupEvents(titleElement);
	
	// register mouse events for root element.
	this.setupEvents();		
};

WebStudio.Templates.Model.Region.prototype.setScope = function(scope)
{
	this.scope = scope;
};

WebStudio.Templates.Model.Region.prototype.getScope = function()
{
	return this.scope;
};