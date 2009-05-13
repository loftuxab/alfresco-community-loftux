WebStudio.Applets.WebForms = WebStudio.Applets.Abstract.extend({
});

WebStudio.Applets.WebForms.prototype.getDependenciesConfig = function()
{
	return {
		"webforms" : {
			"title" : "applet dependencies",
			"loader" : {
				"CSS" : {
					"name" : "CSS",
					"path" : WebStudio.overlayPath + "/applets/webforms/webforms.class.css.jsp"
				}
			}
		}
	};
};

WebStudio.Applets.WebForms.prototype.getTemplateDomId = function()
{
	return "WebFormsApplet_Slider";
};

WebStudio.Applets.WebForms.prototype.bindSliderControl = function(container) 
{
	if(!this.control)
	{
		/*
		var controlTemplate = this.instantiateControlTemplate(this.getId(), 'AlfrescoSearchViewTemplate');
		
		var searchView = new WebStudio.WebFormsView('Control_'+this.getId());
		searchView.setTemplate(controlTemplate);
		searchView.setInjectObject(container);
		searchView.activate();
				
		this.control = searchView;
		*/
	}
	
	return this.control;
};

WebStudio.Applets.WebForms.prototype.onShowApplet = function()
{
	this.getApplication().hideAllDesigners();
	this.getApplication().showPageBlocker();	
};

WebStudio.Applets.WebForms.prototype.onHideApplet = function()
{
	this.getApplication().hideAllDesigners();
};
