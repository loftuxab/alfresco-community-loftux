WebStudio.Applets.Templates = WebStudio.Applets.Abstract.extend({
});

WebStudio.Applets.Templates.prototype.getDependenciesConfig = function()
{
	return {
		"templates" : {
			"title" : "applet dependencies",
			"loader" : {
				"CSS" : {
					"name" : "CSS",
					"path" : WebStudio.overlayPath + "/applets/templates/templates.class.css.jsp"
				}							
			}
		}
	};
};

WebStudio.Applets.Templates.prototype.getTemplateDomId = function()
{
	return "TemplatesApplet_Slider";
};

WebStudio.Applets.Templates.prototype.bindSliderControl = function(container) 
{
	if(!this.templatesView)
	{
		var controlTemplate = this.instantiateControlTemplate(this.getId(), 'AlfrescoTemplatesViewTemplate');
		
		this.templatesView = new WebStudio.TemplatesView('Control_' + this.getId());
		this.templatesView.setTemplate(controlTemplate);
		this.templatesView.setInjectObject(container);
		this.templatesView.activate();
		this.templatesView.application = this.getApplication();
		
		var _this = this;
		
		this.templatesView.onTemplateRowClick = function(templateId)
		{
			_this.currentTemplateId = templateId;
			_this.getApplication().GoToTemplateDisplay(templateId);
			
			this.selectTemplate(templateId);			
		};
	}
	
	return this.templatesView;
};

WebStudio.Applets.Templates.prototype.onShowSlider = function()
{
	this.getApplication().hideAllDesigners();

	// show the template designer
	this.getApplication().showTemplateDesigner();
};

WebStudio.Applets.Templates.prototype.onHideSlider = function()
{
	this.getApplication().hideAllDesigners();
};
