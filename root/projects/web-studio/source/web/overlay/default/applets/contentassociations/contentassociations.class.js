WebStudio.Applets.ContentAssociations = WebStudio.Applets.Abstract.extend({
});

WebStudio.Applets.ContentAssociations.prototype.getDependenciesConfig = function()
{
	return {
		"contentassociations" : {
			"title" : "applet dependencies",
			"loader" : {
				"CSS" : {
					"name" : "CSS",
					"path" : WebStudio.overlayPath + "/applets/contentassociations/contentassociations.class.css.jsp"
				}
			}
		}
	};
};

WebStudio.Applets.ContentAssociations.prototype.getTemplateDomId = function()
{
	return "SandboxApplet_Slider";
};

WebStudio.Applets.ContentAssociations.prototype.bindSliderControl = function(container) 
{
	if(!this.control)
	{
		/*
		var controlTemplate = this.instantiateControlTemplate(this.getId(), 'AlfrescoSearchViewTemplate');
		
		var searchView = new WebStudio.ContentAssociationsView('Control_'+this.getId());
		searchView.setTemplate(controlTemplate);
		searchView.setInjectObject(container);
		searchView.activate();
				
		this.control = searchView;
		*/
	}
	
	return this.control;
};

WebStudio.Applets.ContentAssociations.prototype.onShowApplet = function()
{
	this.getApplication().hideAllDesigners();
	this.getApplication().showPageBlocker();	
};

WebStudio.Applets.ContentAssociations.prototype.onHideApplet = function()
{
	this.getApplication().hideAllDesigners();
};
