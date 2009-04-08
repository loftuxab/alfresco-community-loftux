WebStudio.Applets.Search = WebStudio.Applets.Abstract.extend({
});

WebStudio.Applets.Navigation.prototype.getDependenciesConfig = function()
{
	return {
		"search" : {
			"title" : "applet dependencies",
			"loader" : {
				"CSS" : {
					"name" : "CSS",
					"path" : WebStudio.overlayPath + "/applets/search/search.class.css.jsp"
				}
			}
		}
	};
};

WebStudio.Applets.Search.prototype.getTemplateDomId = function()
{
	return "SearchApplet_Slider";
};

WebStudio.Applets.Search.prototype.bindSliderControl = function(container) 
{
	if(!this.control)
	{
		/*
		var controlTemplate = this.instantiateControlTemplate(this.getId(), 'AlfrescoSearchViewTemplate');
		
		var searchView = new WebStudio.SearchView('Control_'+this.getId());
		searchView.setTemplate(controlTemplate);
		searchView.setInjectObject(container);
		searchView.activate();
				
		this.control = searchView;
		*/
	}
	
	return this.control;
};

WebStudio.Applets.Search.prototype.onShowSlider = function()
{
};

WebStudio.Applets.Search.prototype.onHideSlider = function()
{
};

