if (typeof WebStudio == "undefined" || !WebStudio)
{
	WebStudio = {};
}

WebStudio.TemplateDesigner = function(index) 
{
	this.defaultContainer = document.body;
	this.injectObject = document.body;

	this.ID = index;
	
	this.defaultTemplateSelector = 'div[id=_AlfrescoTemplateDesigner]';
	
	this.defaultElementsConfig = {
		Frame: {
			selector: 'div[id=_AlfrescoTemplateDesignerFrame]'
		}
		,
		Editor: {
			selector: 'div[id=_AlfrescoTemplateDesignerEditor]'
		}
	};

	this.events = {};
	
	this.nodes = {};
	this.droppables = [];
	
	this.instance = { };
	
	// Reference to the template that was previously loaded.
	this.previousTemplateId = null;
	
	this.templateRenderer = null;
	
	this.TABLE_LAYOUT = "Table Layout";	
};

WebStudio.TemplateDesigner.prototype = new WebStudio.AbstractTemplater('WebStudio.TemplateDesigner');

WebStudio.TemplateDesigner.prototype.activate = function() 
{	
	this.buildGeneralLayer();
	
	// set up the frame
	this.Frame.el.id = "AlfrescoTemplateDesignerFrame";

	WebStudio.util.setStyle(this.Frame.el, 'display', 'block');
	
	// set up the editor	
	this.Editor.id = "AlfrescoTemplateDesignerEditor";

	WebStudio.util.setStyle(this.Editor.el, 'display', 'block');	
	
	this.resize();

	return this;
};

WebStudio.TemplateDesigner.prototype.resize = function()
{
	if(this.Frame.el)
	{

		WebStudio.util.setStyle(this.Frame.el, 'left', 0);
		WebStudio.util.setStyle(this.Frame.el, 'top', 0);		
		WebStudio.util.setStyle(this.Frame.el, 'width', this.injectObject.offsetWidth);
		WebStudio.util.setStyle(this.Frame.el, 'height', this.injectObject.offsetHeight);		
	}
	
	if(this.Editor.el)
	{

		WebStudio.util.setStyle(this.Editor.el, 'left', '7px');		
		WebStudio.util.setStyle(this.Editor.el, 'top', '7px');		
		WebStudio.util.setStyle(this.Editor.el, 'width', this.injectObject.offsetWidth - 16);		
		WebStudio.util.setStyle(this.Editor.el, 'height', this.injectObject.offsetHeight - 17);		
	}
};

WebStudio.TemplateDesigner.prototype.selectTemplate = function(templateId)
{   
	this.selectedTemplateId = templateId;
		
	if(this.previousTemplateId)
	{
        // We found a template that was previously loaded. Let's destroy it!
        var previousTemplateTable = document.getElementById(this.previousTemplateId);
        
        // Check if you actually found something in the DOM.
        if(previousTemplateTable)
        {        
            if(this.templateRenderer)
            {            	                
            	// Let's destroy the Template Instance associated with the previous renderer                
                this.templateRenderer.destroy();                
            }                                                 
        }                                                
	} 
		
	this.previousTemplateId = this.selectedTemplateId;
    
    this.loadTemplateInstance();   
};

WebStudio.TemplateDesigner.prototype.build = function() 
{
	this.generalLayer.set({
		id: this.ID
	});
};

WebStudio.TemplateDesigner.prototype.loadTemplateInstance = function() 
{
	var _this = this;
	
    if(this.selectedTemplateId)
    {
		var url = WebStudio.ws.studio("/api/model/get", { type: "template-instance", id: this.selectedTemplateId} );
		
		this.call = YAHOO.util.Connect.asyncRequest('GET', url, {	
			success: function(r) {	
				var data = eval('(' + r.responseText + ')');					
				_this.setupTemplateInstance(data);		
			}
			,
			failure: function(r) {		
				alert("reloadTemplatesListing failed: " + r.responseText);
			}
		});
    }
    else 
    {
        //alert("there was an issue reloading the template");         
    }
};

// populates the current template instance from json data
WebStudio.TemplateDesigner.prototype.setupTemplateInstance = function(data)
{
    var templateRenderer = null;
    
	// basic properties
	this.instance.type = data["template-type"];
	this.instance.title = data["title"];	
	this.instance.description = data["description"];
	this.instance.height = data["height"];
	this.instance.width = data["width"];	
	this.instance.templateLayoutType = data["template-layout-type"];
    
	if(this.instance.type == "dynamic")
	{  
        if(data["config"])
        {
            this.instance.config = Json.evaluate(data["config"]);                
        }
        else 
        {
            this.instance.config = null;
        }
	
        // determine which template renderer to use.
        if(this.instance.templateLayoutType && 
            (this.instance.templateLayoutType == "Absolute Positioning"))
        {
              
            this.templateRenderer = new Alfresco.AbsolutePositionRenderer('AbsolutePositionRenderer', this.selectedTemplateId, this.instance, this.Editor.el, this);
                  
        } else if(this.instance.templateLayoutType && 
                  (this.instance.templateLayoutType == "Table Layout")) {
                               
            this.templateRenderer = new Alfresco.TableLayoutRenderer('TableLayoutRenderer', this.selectedTemplateId, this.instance, this.Editor.el, this);
            
        }       
            
        this.templateRenderer.cleanup();
        this.templateRenderer.activate();                
        this.templateRenderer.render();
	}
};

WebStudio.TemplateDesigner.prototype.getApplication = function()
{
	return this.application;
};

WebStudio.TemplateDesigner.prototype.refresh = function()
{
	this.application.GoToTemplateDisplay(this.selectedTemplateId);
};

WebStudio.TemplateDesigner.prototype.onScroll = function(left, top)
{
	// TODO
};

WebStudio.TemplateDesigner.prototype.cleanup = function()
{
	if(this.templateRenderer)
	{
		this.templateRenderer.cleanup();
	}
};