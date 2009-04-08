WebStudio.Templates.Model.Region = WebStudio.Templates.Model.Abstract.extend({ });

WebStudio.Templates.Model.Region.prototype.init = function(parent)
{
	// Set the parent object
	this.setParent(parent);
	
	// Object type
	this.setObjectType("region");
	
	// Set up CSS
	this.setCSS("TemplateRegion");
	this.setTitleCSS("TemplateRegionTitle");
	this.setMenuCSS("TemplateRegionMenu");
	
	// Set mouseover css	
	this.setOnMouseOverCSS("TemplateObjectOnMouseOver");
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
	// the region
	var region = Alf.createElement("div", this.getId());
	YAHOO.util.Dom.addClass(region, this.getCSS());
	WebStudio.util.injectInside(container, region);
	this.setElement(region);
	
	// TODO: figure out the height of the region
	/*	
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
	*/	
	
	// title element
	var titleElement = Alf.createElement("div", "region_div_" + this.getId());
	YAHOO.util.Dom.addClass(titleElement, this.getTitleCSS());
	WebStudio.util.pushHTML(titleElement, "Region: " + this.getTitle());
	WebStudio.util.injectInside(region, titleElement);
	
	// body div	
	var bodyElement = Alf.createElement("div", "region_div_body_" + this.getId());
	YAHOO.util.Dom.addClass(bodyElement, "TemplateRegionBody");
	WebStudio.util.pushHTML(bodyElement, "&nbsp;&nbsp;");
	WebStudio.util.injectInside(region, bodyElement);
	
	// calculate the height of the region
	var height = jQuery(container).height() - 32;
	jQuery(region).css( { "height" : height } );		
	
	// set up events
	this.setupEvents(titleElement);
	this.setupEvents(bodyElement);
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