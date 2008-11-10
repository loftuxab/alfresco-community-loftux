if (typeof Alfresco == "undefined")
{
    var Alfresco = {};
};

Alfresco.AbsolutePositionRenderer = function(index, selectedTemplateId, templateInstance, templateDesignerEditor, templateDesigner) 
{
    // Renderere Id
    this.id = index;
    
    // Template instance object.
    this.instance = templateInstance;
            
    // Template instance title.
    this.templateTitle = this.instance.title;
    
    // Currently selected template Id 
    this.selectedTemplateId = selectedTemplateId;   

    // Container where the template renderer will inject template instance.
    this.templateDesignerEditor = templateDesignerEditor;

    // Div container for the template.
    this.templateContainerDiv = null;
    
    // Reference to div that the template menu will be attached to.
    this.menuContainerDiv = null;            
        
    // Reference to object model Template.
    this.template = null;
    
    // Reference to template designer
    this.templateDesigner = templateDesigner;
};

Alfresco.AbsolutePositionRenderer.prototype.getTemplateDesigner = function()
{
	return this.templateDesigner;
};

Alfresco.AbsolutePositionRenderer.prototype.activate = function() 
{
	// Create new instance of Template class
    this.template = new Alfresco.AbsolutePositionRenderer.Template(this.selectedTemplateId, this.templateTitle, this.instance, this.templateDesigner);

	// Initialize template container div element.
    this.template.init();
                
    // If we have a valid template instance config, let's load the template object model.         
    if(this.instance.config != null && this.instance.config != 'undefined')
    {
        this.loadObjectModel();
    }

};

Alfresco.AbsolutePositionRenderer.prototype.destroy = function() 
{
    // Let's destroy the Template and its child objects.
	this.template.destroy();
};

Alfresco.AbsolutePositionRenderer.prototype.cleanup = function() 
{ 
    for(var nodeIndx=0; nodeIndx<this.templateDesignerEditor.childNodes.length; nodeIndx++)
    {           
        if(this.templateDesignerEditor.childNodes[nodeIndx].id != null)
        {                                        
            this.templateDesignerEditor.removeChild(this.templateDesignerEditor.childNodes[nodeIndx]);
        }
    }
};

Alfresco.AbsolutePositionRenderer.prototype.loadObjectModel = function()
{
	// If we have an instance config, let's check for regions
    if(this.instance.config != null)
    {                          
	    if(this.instance.config.regions && this.instance.config.regions.length > 0)
	    {	    	
	    	// Found some regions. Let's create objects for them 
	    	//and add them to the Template collection of regions.
	        for(var regIndx = 0; regIndx < this.instance.config.regions.length; regIndx++)
	        {	        	
	            var regionConfig = this.instance.config.regions[regIndx];	            

	        	// Create new instance of Region class
	            var region = new Alfresco.AbsolutePositionRenderer.Region(regionConfig.id, 
	            														  regionConfig.title, 
	            														  this.instance,
	            														  this.templateDesigner);	            
	            region.setX(regionConfig.x);
	            region.setY(regionConfig.y);
	            region.setWidth(regionConfig.width);
	            region.setHeight(regionConfig.height);
	            
	            // Let's add this region object to the template region array.
	            this.template.addRegion(region);	            
	        }
		}
	}
};    

Alfresco.AbsolutePositionRenderer.prototype.render = function() 
{
    // let's cleanup the templateDesignEditor in the event that there are some lingering elements from a
    // previous load of this template type.
    this.cleanup();

	// Let's render the template.
	this.template.render(this.templateDesignerEditor);        
};



Alfresco.AbsolutePositionRenderer.prototype.purgeDOMElement = function(element) 
{       
    // If we have an DOM element   
    if(element != null && element != 'undefined')
    {
        // Purge the element, it's child nodes, of all event listeners.
        YAHOO.util.Event.purgeElement(element, true);
    	
    	// Delete the DOM element and its child nodes.    
        element.parentNode.removeChild(element);                                
   }                          
};

////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////

Alfresco.AbsolutePositionRenderer.Template = function(id, title, templateConfig, templateDesigner)
{
    // Template object id.
    this.id = id;
    this.title = title;
	
    // JavaScript object for template object config 
    this.templateConfig = templateConfig;        
        
    // Array for template region objects.        
    var regions = new Array();
    
    // Add regions arrar member to templateConfig.
    this.templateConfig.regions = regions;
    
    // Container DOM element for Template.
    this.templateContainerDiv = null;
    
    // Reference to Template Designer object.
    this.templateDesigner = templateDesigner;
};

Alfresco.AbsolutePositionRenderer.Template.prototype.init = function()
{
	//Root DOM Element for Template.
    this.templateContainerDiv = document.createElement('div');
    this.templateContainerDiv.setAttribute("id", this.getId());
    this.templateContainerDiv.setStyle('width', '100%');
    this.templateContainerDiv.setStyle('height', '100%');            
    this.templateContainerDiv.setHTML("Template: " + this.title);                         
    
    // This div will be the container for the YUI menu.
    this.menuContainerDiv = document.createElement("div");        
    this.menuContainerDiv.setAttribute("id", "menuContainerDiv");        
    this.menuContainerDiv.setAttribute('class', 'yui-skin-sam');        
    this.menuContainerDiv.injectInside(this.templateContainerDiv);
    
    YAHOO.util.Event.addListener(this.templateContainerDiv, 
            					"click", 
            					this.templateOnClick, 
            					this.templateContainerDiv, 
            					this);                                
    
};

Alfresco.AbsolutePositionRenderer.Template.prototype.render = function(templateDesignerEditor)
{		
    this.templateContainerDiv.injectInside(templateDesignerEditor);     
 
    if(this.getRegionCount() > 0)
    {
        this.renderRegions(templateDesignerEditor);
    }    	                      	
};

Alfresco.AbsolutePositionRenderer.Template.prototype.templateOnClick = function(e, element) 
{ 	
    // Get click target.
    var target = YAHOO.util.Event.getTarget(e);    

 	var menuContainerDiv = document.getElementById("menuContainerDiv");

 	if(target.id == element.id)
 	{
 		e.stopPropagation();
 		
	 	this.purgeElement(menuContainerDiv);
				
	    // Create a clean context menu container div and insert into the Template object's root element.
	    menuContainerDiv = document.createElement('div');
	    menuContainerDiv.setAttribute('id', 'menuContainerDiv');
	    menuContainerDiv.setAttribute('class', 'yui-skin-sam');
	              
	    menuContainerDiv = document.createElement('div');
	    menuContainerDiv.setAttribute("id", "menuContainerDiv");    
	    menuContainerDiv.setAttribute('class', 'yui-skin-sam');
	    menuContainerDiv.injectInside(this.templateContainerDiv);  	
			
	    var objContextMenu = new YAHOO.widget.Menu("templateContextMenu", { fixedcenter: false });   
		        
	    objContextMenu.addItems([
	                             {id: 'addRegion', 
	                             text: "Add Region",
	                             onclick: { fn: this.addTemplateRegion.bind(this), obj: [element.id] }
	                            }]);
		                                
	    // Attach menu to contextMenuDiv.
	    objContextMenu.render(menuContainerDiv);
	        
	    // Make menu visibie.
	    objContextMenu.show();
	        
	    // Get DOM element for menu.
	    var contextMenuElement = document.getElementById('templateContextMenu');
	
	    // Set position to absolute.        
	    contextMenuElement.setStyle('position', 'absolute');
	        
	    // Set X,Y coordinates.
	    // todo: use the YAHOO.util.Event utility to get exact mouse position.              
	    contextMenuElement.setStyle('top', (e.clientY - 18));
	    contextMenuElement.setStyle('left', (e.clientX - 269));
 	}
};

Alfresco.AbsolutePositionRenderer.Template.prototype.purgeElement = function(element)
{
	// Let's purge the DOM element, child and their event listeners.
	if(element != null && element != 'undefined')
	{
	    // Purge the element, it's child nodes, of all event listeners.
	    YAHOO.util.Event.purgeElement(element, true);
		
		// Delete the DOM element and its child nodes.    
	    element.parentNode.removeChild(element);                                
	}                          	
};

Alfresco.AbsolutePositionRenderer.Template.prototype.addTemplateRegion = function (eventType, eventArgs, parmsObj) 
{   
    var templateId = parmsObj[0];
    
    var w = new WebStudio.Wizard();
    w.setDefaultJson(
    {
        refreshSession: 'true',
        templateId: templateId
    });
    
    w.start(WebStudio.ws.studio("/wizard/regionmanager/absoluteposition"), 'addnewregion');

    w.onComplete = (function() 
    {
    	this.templateDesigner.refresh();     
    }).bind(this);    	                             
};

Alfresco.AbsolutePositionRenderer.Template.prototype.getId = function()
{
	return this.id;
};    

Alfresco.AbsolutePositionRenderer.Template.prototype.setId = function(id)
{
	this.id = id;
};    

Alfresco.AbsolutePositionRenderer.Template.prototype.addRegion = function(region)
{
	this.templateConfig.regions.push(region);
};

Alfresco.AbsolutePositionRenderer.Template.prototype.getRegions = function()
{
	return this.templateConfig.regions;
};

Alfresco.AbsolutePositionRenderer.Template.prototype.renderRegions = function()
{
	// Get regions array
	var regions = this.getRegions();
	
	// Loop through regions and call render() on them.
	for(var regionIndx=0;regionIndx<regions.length;regionIndx++)
	{
		// Get region.
		var region = regions[regionIndx];

		// Call init() on row before calling render();
		region.init(this.templateContainerDiv);
		
		// Call render() on region.
		region.render();
	}
};    


Alfresco.AbsolutePositionRenderer.Template.prototype.destroy = function()
{
    //Let's go through row objects and call the destroy method on them.
	
    if (this.getRegionCount() > 0)
    {
        var regions = this.getRegions();
        
        for (var regionIndx=0;regionIndx<regions.length;regionIndx++)
        {
            var region = regions[regionIndx];
            
            region.destroy(); 
        }
    }

    // Cleanup DOM elements on this template object.
    if(this.templateContainerDiv != null && this.templateContainerDiv != 'undefined')
    {    
        // Remove all listeners and recurse children 
        YAHOO.util.Event.purgeElement(this.templateContainerDiv, true);
        
        // Remove the element itself from the DOM. This should remove the children as well.
        this.templateContainerDiv.parentNode.removeChild(this.templateContainerDiv);
   }                      
   
   // Check for a menu for this object in the menu manager
   var objContextMenu = YAHOO.widget.MenuManager.getMenu("templateContextMenu");
   
    if(objContextMenu != null && objContextMenu != 'undefined'){
        // Remove listeners for menu and all child notes.
        YAHOO.util.Event.purgeElement(objContextMenu, true);
     
        // Remove menu from the menu manager.   
        YAHOO.widget.MenuManager.removeMenu(objContextMenu);        
    }
  	
};    
  
Alfresco.AbsolutePositionRenderer.Template.prototype.getRegionCount = function()
{
	if (this.templateConfig.regions != null && this.templateConfig.regions != 'undefined')
	{
		return this.templateConfig.regions.length;
	}	
};

Alfresco.AbsolutePositionRenderer.Region = function(id, title, templateConfig, templateDesigner)
{
	// Template object id.
	this.id = id;
	this.title = title;
	this.x = null;
	this.y = null;
	this.width = null;
	this.height = null;
	this.scope = null;
	this.description = null;
	
	// Container div for template instance
	this.templateContainerDiv = null;

	// Container div for template instance
	this.regionDiv = null;
	
	// Reference to DOM element for menu.
	this.contextMenuDiv = null;	
	
	// JS object for template object config 
	this.templateConfig = templateConfig;      

	// Template instance object.
	this.instance = { };
	
	// Template config object.
	this.instance.config = this.templateConfig.config;	
	
	// Reference to the templateDesigner.
	// We will need this in order to refresh
	// the template designer.
	this.templateDesigner = templateDesigner;
};

Alfresco.AbsolutePositionRenderer.Region.prototype.init = function(templateContainerDiv)
{
	this.templateContainerDiv = templateContainerDiv;	
};

Alfresco.AbsolutePositionRenderer.Region.prototype.render = function()
{
    this.regionDiv = document.createElement('div');	            
    this.regionDiv.setAttribute('id', this.getId());
    this.regionDiv.setAttribute('templateId', this.templateContainerDiv.id);	            
	
    this.regionDiv.setStyle('position', 'absolute');                
    this.regionDiv.setStyle('border', '1px dashed black');                                              
    this.regionDiv.setStyle('display', 'block');    
    this.regionDiv.setStyle('left', this.getX());             
    this.regionDiv.setStyle('top', this.getY());              
    this.regionDiv.setStyle('width', this.getWidth());                
    this.regionDiv.setStyle('height', this.getHeight());
    
    var regionTitleHandle = document.createElement("a");
    regionTitleHandle.setAttribute('class', 'AbsoluotePositioningAnchor');
    regionTitleHandle.setAttribute('templateId', this.templateContainerDiv.id);
    regionTitleHandle.setAttribute('href', 'javascript:;');
    regionTitleHandle.setAttribute('regionId', this.getId());                                               
    regionTitleHandle.setHTML("&nbsp;&nbsp;Region: " + this.getTitle());
    regionTitleHandle.injectInside(this.regionDiv);
     
    //Inject regionDiv into Editor element                                  
    this.regionDiv.injectInside(this.templateContainerDiv);    

    //Attach onmouseover event to regionDiv                                 
    this.attachRegionOnMouseOver(this.regionDiv);
                
    //Attach onmouseout event to regionDiv
    this.attachRegionOnMouseOut(this.regionDiv);
                          
    //Attach region drag drop capability to regionDiv
    this.attachRegionDragDrop(this.regionDiv, this.templateContainerDiv.id);
                
    //Attach region resize capability to regionDiv
    this.attachRegionResize(this.regionDiv);                                
    
    // One click listener.
    YAHOO.util.Event.addListener(this.regionDiv, 
                                 "click", 
                                 this.regionOnClick, 
                                 this.regionDiv,
                                 this
                                 );                                	
};

Alfresco.AbsolutePositionRenderer.Region.prototype.destroy = function()
{	
    // Cleanup DOM elements on this column object.
    if(this.regionDiv != null && this.regionDiv != 'undefined')
    {    
        // Remove all listeners and recurse children 
        YAHOO.util.Event.purgeElement(this.regionDiv, true);        
   }                      
   
   // Check for a menu for this object in the menu manager
   var objContextMenu = YAHOO.widget.MenuManager.getMenu("regionContextMenu");   
   
    if(objContextMenu != null && objContextMenu != 'undefined')
    {    
        // Remove listeners for menu and all child notes.
        YAHOO.util.Event.purgeElement(objContextMenu, true);
     
        // Remove menu from the menu manager.   
        YAHOO.widget.MenuManager.removeMenu(objContextMenu);
        
        var tempElement = document.getElementById("regionContextMenu");
    }	
};

Alfresco.AbsolutePositionRenderer.Region.prototype.getRegionCount = function()
{
	if (this.templateConfig.regions != null && this.templateConfig.regions != 'undefined')
	{
		return this.templateConfig.regions.length;
	}	
};

Alfresco.AbsolutePositionRenderer.Region.prototype.setId = function(id)
{
	this.id = id;			
};

Alfresco.AbsolutePositionRenderer.Region.prototype.getId = function()
{
	return this.id;
};

Alfresco.AbsolutePositionRenderer.Region.prototype.setTitle = function(title)
{
	this.title = title;	
};

Alfresco.AbsolutePositionRenderer.Region.prototype.getTitle = function()
{
	return this.title;	
};

Alfresco.AbsolutePositionRenderer.Region.prototype.setX = function(x)
{
	this.x = x;		
};

Alfresco.AbsolutePositionRenderer.Region.prototype.getX = function()
{
	return this.x;
};

Alfresco.AbsolutePositionRenderer.Region.prototype.setY = function(y)
{
	this.y = y;	
};

Alfresco.AbsolutePositionRenderer.Region.prototype.getY = function()
{
	return this.y;	
};

Alfresco.AbsolutePositionRenderer.Region.prototype.setHeight = function(height)
{
	this.height = height;	
};

Alfresco.AbsolutePositionRenderer.Region.prototype.getHeight = function(height)
{
	return this.height;	
};

Alfresco.AbsolutePositionRenderer.Region.prototype.setWidth = function(width)
{
	this.width = width;	
};

Alfresco.AbsolutePositionRenderer.Region.prototype.getWidth = function(width)
{
	return this.width;		
};

Alfresco.AbsolutePositionRenderer.Region.prototype.setScope = function(scope)
{
	this.scope = scope;
};

Alfresco.AbsolutePositionRenderer.Region.prototype.getScope = function(scope)
{
	return this.scope;			
};

Alfresco.AbsolutePositionRenderer.Region.prototype.attachRegionOnMouseOver = function(regionDiv) {
    
    regionDiv.onmouseover = function(){ 
        regionDiv.setStyle('border', '1px dashed blue');
        regionDiv.setStyle('background-color', '4F94CD');
    }           
};

Alfresco.AbsolutePositionRenderer.Region.prototype.attachRegionOnMouseOut = function(regionDiv) {
    
    regionDiv.onmouseout = function(){  
        regionDiv.setStyle('border', '1px dashed black');
        regionDiv.setStyle('background-color', 'FFF8DC');
    }           
};

Alfresco.AbsolutePositionRenderer.Region.prototype.attachRegionResize = function(regionDiv) {

    var resize = new YAHOO.util.Resize(regionDiv.id, {
                 handles: 'br,l,r,b',
                 autoRatio: false,
                 minWidth: 200,
                 minHeight: 100,
                 status: true,
                 proxy: true
    });
        
    resize.on('resize', (function(ev) {    	
        this.updateDynamicTemplateInstance(regionDiv);
    }).bind(this), resize, true);            
};

Alfresco.AbsolutePositionRenderer.Region.prototype.attachRegionDragDrop = function(regionDiv, templateId) 
{   
    var dd1 = new YAHOO.util.DD(regionDiv.id);  
    var tempTemplateId = templateId;

    dd1.on('endDragEvent', (function(ev) {
        this.updateDynamicTemplateInstance(regionDiv, tempTemplateId);  
    }).bind(this), dd1, true);      
};

Alfresco.AbsolutePositionRenderer.Region.prototype.updateDynamicTemplateInstance = function(regionDiv) 
{	
    // get the properties
    var x = regionDiv.getStyle('left');
    var y = regionDiv.getStyle('top');
    var width = regionDiv.getStyle('width');
    var height = regionDiv.getStyle('height');
    var regionId = regionDiv.id;
 	
    // prepare config
    if(!this.instance.config)
    {
        this.instance.config = { };
    }
    if(!this.instance.config.regions)
    {
        this.instance.config.regions = { };
    }  
    
    // find previous region 
    var configRegion = null;
    
    for(var regIndx = 0; regIndx < this.instance.config.regions.length; regIndx++)
    {
    
        var regionConfig = this.instance.config.regions[regIndx];                
        if(regionConfig.id == regionId)
        {
            configRegion = regionConfig;
        }
    }
    
    // if no previous region was found, build a new one
    if(!configRegion)
    {
        configRegion = { };
    }
    
    // assign       
    configRegion.x = x;
    configRegion.y = y;
    configRegion.width = width;
    configRegion.height = height;
    
    // place into map
    this.instance.config.regions[regionId] = configRegion;
    
    // save the template
    this.saveTemplateInstance();
};

//saves the current template instance back
Alfresco.AbsolutePositionRenderer.Region.prototype.saveTemplateInstance = function()
{
    //make ajax call to update template layout config
    var url = WebStudio.ws.studio("/api/model/put", { type: "template-instance",  id: this.templateContainerDiv.id, config: Json.toString(this.instance.config) });
        
    call = YAHOO.util.Connect.asyncRequest('GET', url, {   
        success: (function(r) {                    
            var data = eval('(' + r.responseText + ')');
        }).bind(this)
        ,
        failure: function(r) {
           // alert("saveTemplateInstance failed: " + r.responseText);
        }
    });
};

Alfresco.AbsolutePositionRenderer.Region.prototype.regionOnClick = function(e, element)
{
	
    // Get actual click event target.      
    var target = YAHOO.util.Event.getTarget(e);    
            
        e.stopPropagation();
        
        // Create template menu. 
        var objContextMenu = this.createMenu()  
        
        // Attach menu to contextMenuDiv.
        objContextMenu.render(this.contextMenuDiv);
        
        // Make menu visibie.
        objContextMenu.show();
        
        // Get DOM element for menu.
        var contextMenuElement = document.getElementById("regionContextMenu");
        
        // Set position to absolute.        
        contextMenuElement.setStyle('position', 'absolute');
        
        // Set X,Y coordinates.
        // todo: use the YAHOO.util.Event utility to get exact mouse position.              
        contextMenuElement.setStyle('top', (e.clientY - 18));
        contextMenuElement.setStyle('left', (e.clientX - 269));                        	
};

Alfresco.AbsolutePositionRenderer.Region.prototype.createMenu = function() 
{            
	
        // If the menu div container exists, let's remove it along with its children nodes.                                                                   
        if(this.contextMenuDiv != null && this.contextMenuDiv != 'undefined')
        {
           this.purgeDOMElement(this.contextMenuDiv);
        }
                        
        // Create a clean context menu container div and insert into the Template object's root element.
        this.contextMenuDiv = document.createElement('div');
        this.contextMenuDiv.setAttribute('id', 'contextMenuDiv');                
        this.contextMenuDiv.setAttribute('class', 'yui-skin-sam');   
        
        this.contextMenuDiv.injectInside(this.templateContainerDiv);          

        // Reference to context menu obj.                                            
        var objContextMenu = null;      
        
        // Let's look for a previous template context menu in the DOM.
        var contextMenuElement = document.getElementById("regionContextMenu");
        
        // If we find one, let's make sure it is defined and then we can try to retrieve it from the menu manager
        if(contextMenuElement != null && contextMenuElement != 'undefined')
        {
            // Get menu from menu manager.
            objContextMenu = YAHOO.widget.MenuManager.getMenu("regionContextMenu");

            // Check for valid menu.
            if(objContextMenu != null && objContextMenu != 'undefined')
            {
                // Clear all menu content.
                objContextMenu.clearContent();                    
            } else {
                // Create menu object.            
                objContextMenu = new YAHOO.widget.Menu("regionContextMenu", { fixedcenter: false });                       
             
                // Added to menu manager.
                YAHOO.widget.MenuManager.addMenu(objContextMenu);            
            }             
            // Add menu items.
            this.addMenuItems(objContextMenu);  
        } else {
            // Let's create new menu object
            objContextMenu = new YAHOO.widget.Menu("regionContextMenu", { fixedcenter: false });                       
        
            // Add menu items
            this.addMenuItems(objContextMenu);
                    
            // Add menu to menu manager
            YAHOO.widget.MenuManager.addMenu(objContextMenu);     
        }          
        return objContextMenu;        
};       

Alfresco.AbsolutePositionRenderer.Region.prototype.addMenuItems = function(objContextMenu) 
{

    var templateId = this.regionDiv.getAttribute("templateId");   

    objContextMenu.addItems([
                             {id: 'editRegion', 
                              text: "Edit Region", 
                              onclick: { fn: this.editTemplateRegion.bind(this), 
                              obj: [this.getId(), templateId] }
                             }]);

    objContextMenu.addItems([
                             {id: 'deleteRegion', 
                              text: "Delete Region", 
                              onclick: { fn: this.deleteTemplateRegion.bind(this), 
                              obj: [this.getId(), templateId] }
                             }]);
    
};

Alfresco.AbsolutePositionRenderer.Region.prototype.editTemplateRegion = function (eventType, eventArgs, parmsObj) 
{ 
    // Get the current Event        
    var e = eventArgs[0];

    // Stop event propagation.
	e.stopPropagation();
	
	// Get region id
	var regionId = parmsObj[0];
	
	// Get template id    
	var templateId = parmsObj[1];	
	
    var w = new WebStudio.Wizard();
    
    w.setDefaultJson(
    {
        refreshSession: 'true',
        templateId: templateId,
        regionId: regionId
    });

    w.start(WebStudio.ws.studio("/wizard/editregion"), 'editregion');
    w.onComplete = (function() 
    {
    	this.templateDesigner.refresh();    	
    }).bind(this);    
};

Alfresco.AbsolutePositionRenderer.Region.prototype.deleteTemplateRegion = function (eventType, eventArgs, parmsObj) 
{   
	
    var regionId = parmsObj[0];
    
    var templateId = parmsObj[1];

    var url = WebStudio.ws.studio("/api/template/absolutelayout/region/delete/get", 
    							 { type: "template-absolute", 
        						   templateId: templateId,
        						   regionId: regionId,
        						   actionFlag: 'deleteRegion'} );                              

    call = YAHOO.util.Connect.asyncRequest('GET', url,	{   
		success: (function(r) {
  	        this.templateDesigner.refresh();  	        
		}).bind(this)
		,
		failure: function(r) {      
		//error deleting region
		}
    });               
};

Alfresco.AbsolutePositionRenderer.Region.prototype.purgeDOMElement = function(element) 
{       
    // If we have an DOM element   
    if(element != null && element != 'undefined')
    {
        // Purge the element, it's child nodes, of all event listeners.
        YAHOO.util.Event.purgeElement(element, true);
    	
    	// Delete the DOM element and its child nodes.    
        element.parentNode.removeChild(element);                                
   }                          
};
