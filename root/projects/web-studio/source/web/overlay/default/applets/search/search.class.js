WebStudio.Applets.Search = WebStudio.Applets.Abstract.extend({
	dependencies: {
		"CSS" : {
			"name" : "CSS",
			"path" : WebStudio.overlayPath + "/applets/search/search.class.css.jsp"
		}
	}
});

WebStudio.Applets.Search.prototype.getTemplateDomId = function()
{
	return "SurfaceSearchSlider";
};

WebStudio.Applets.Search.prototype.bindSliderControl = function(container) 
{
	if(!this.control)
	{
		var controlTemplate = this.instantiateControlTemplate(this.getId(), 'AlfrescoSearchViewTemplate');
		
		var searchView = new WebStudio.SearchView('Control_'+this.getId());
		searchView.setTemplate(controlTemplate);
		searchView.setInjectObject(container);
		searchView.activate();
				
		this.control = searchView;
	}
	
	return this.control;
};

WebStudio.Applets.Search.prototype.onShowSlider = function()
{
	// hide all designers
	this.getApplication().hideAllDesigners();
};

WebStudio.Applets.Search.prototype.onHideSlider = function()
{
	// hide the template designer
	this.getApplication().hideTemplateDesigner();
};

