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
}


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
}

/**
 * Context Menu
 * Event Handler
 */ 
WebStudio.Templates.Model.Region.prototype.editTemplateRegion = function(eventType, eventArgs, parmsObj) 
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
        panelId: bindingProperties["column"],
        regionId: this.getId(),
        actionFlag: 'editRegion'
    });                 

    w.start(WebStudio.ws.studio("/wizard/regionmanager/tablelayout"), 'tablelayout');
    
    w.onComplete = (function() 
    {
    	this.templateDesigner.refresh();     
    }).bind(this);

}

WebStudio.Templates.Model.Region.prototype.deleteTemplateRegion = function(eventType, eventArgs, parmsObj) 
{   
    // Get Event
    var e = eventArgs[0];

    // Stop event propagation.
    e.stopPropagation();

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
        success: (function(r) {
        	this.templateDesigner.refresh();
        }).bind(this),
        failure: function(r) {      
        //error deleting region
        }
    });                                                              
}

/**
 * Renders the template to a given container
 */
WebStudio.Templates.Model.Region.prototype.render = function(container)
{
	this.container = container;
	
	var regionDiv = document.createElement('div');
	regionDiv.setAttribute("id", this.getId());	
	regionDiv.setAttribute("class", this.getCSS());
	regionDiv.injectInside(container);
	this.setElement(regionDiv);
	
	// title element
	var titleElement = document.createElement('div');
	titleElement.setAttribute("class", this.getTitleCSS());	
	titleElement.setAttribute("id", "region_div_" + this.getId());
	titleElement.setHTML("Region " + this.getTitle());	
	titleElement.injectInside(regionDiv);

	// register mouse events
	this.setupEvents(titleElement);
	
	// register mouse events
	this.setupEvents();		
}


WebStudio.Templates.Model.Region.prototype.setScope = function(scope)
{
	this.scope = scope;
}

WebStudio.Templates.Model.Region.prototype.getScope = function()
{
	return this.scope;
}