WebStudio.Applets.Sandbox = WebStudio.Applets.Abstract.extend({
});

WebStudio.Applets.Sandbox.prototype.getDependenciesConfig = function()
{
	return {
		"sandbox" : {
			"title" : "applet dependencies",
			"loader" : {
				"CSS" : {
					"name" : "CSS",
					"path" : WebStudio.overlayPath + "/applets/sandbox/sandbox.class.css.jsp"
				}
			}
		}
	};
};

WebStudio.Applets.Sandbox.prototype.getTemplateDomId = function()
{
	return "SandboxApplet_Slider";
};

WebStudio.Applets.Sandbox.prototype.bindSliderControl = function(container) 
{
	if(!this.control)
	{
		/*
		var controlTemplate = this.instantiateControlTemplate(this.getId(), 'AlfrescoSearchViewTemplate');
		
		var searchView = new WebStudio.SandboxView('Control_'+this.getId());
		searchView.setTemplate(controlTemplate);
		searchView.setInjectObject(container);
		searchView.activate();
				
		this.control = searchView;
		*/
	}
	
	return this.control;
};

WebStudio.Applets.Sandbox.prototype.onShowApplet = function()
{
	this.getApplication().hideAllDesigners();
	this.getApplication().showPageBlocker();	
};

WebStudio.Applets.Sandbox.prototype.onHideApplet = function()
{
	this.getApplication().hideAllDesigners();
};
