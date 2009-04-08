/**
   SURF SITE APPLICATION
**/
WebStudio.Applications.SurfSite = WebStudio.Applications.Abstract.extend({});

WebStudio.Applications.SurfSite.prototype.getSlidersSectorTemplateId = function()
{
	return "SurfSiteApplication_SlidersSectorTemplate";
};

WebStudio.Applications.SurfSite.prototype.getSlidersPanelDomId = function()
{
	return "SurfSiteApplication_SplitterPanel";
};

/**
 * TEMPLATE DESIGNER
 */
WebStudio.Applications.SurfSite.prototype.getTemplateDesigner = function()
{
	return this.getDesigner("TemplateDesigner");
};

WebStudio.Applications.SurfSite.prototype.showTemplateDesigner = function()
{
	var templateDesigner = this.getTemplateDesigner();
	if (!templateDesigner)
	{
		// start the template designer
		templateDesigner = new WebStudio.TemplateDesigner('TemplateDesigner');
		templateDesigner.injectObject = $('AlfSplitterPanel2');
		templateDesigner.activate();
		templateDesigner.application = this;
		this.addDesigner("TemplateDesigner", templateDesigner);
	}
	
	// show the template designer
	templateDesigner.show();
		
	// hide overflow for the panel
	$('AlfSplitterPanel2').setStyle('overflow', 'hidden');		
};

WebStudio.Applications.SurfSite.prototype.hideTemplateDesigner = function()
{
	var templateDesigner = this.getTemplateDesigner();
	if (templateDesigner)
	{
		templateDesigner.hide();
	}

	this.checkAllHidden();		
};

/**
 FUNCTIONS
**/
WebStudio.Applications.SurfSite.prototype.GoToTemplateDisplay = function(templateId)
{
	// hide everything
	this.hideAllDesigners();
	
	// show the "templates" applet
	this.showApplet("templates");

	// show the template designer
	this.showTemplateDesigner();
	
	// tell the "templates designer" about our selection
	var templateDesigner = this.getTemplateDesigner();
	templateDesigner.selectTemplate(templateId);
	
	// record the selected template
	this.selectedTemplate = templateId;		
};

WebStudio.Applications.SurfSite.prototype.onStartEdit = function()
{
};

WebStudio.Applications.SurfSite.prototype.onEndEdit = function()
{
	// close all applets
	var appletIds = this.getAppletIds();
	for(var i = 0; i < appletIds.length; i++)
	{
		var appletId = appletIds[i];
		var applet = this.getApplet(appletId);
		
		applet.onClose();
	}
	
	// get the template designer
	var templateDesigner = this.getTemplateDesigner();
	if (templateDesigner)
	{
		this.hideTemplateDesigner();
	
		templateDesigner.cleanup();
		templateDesigner.destroy();
		
		this.removeDesigner(templateDesigner);
	}	
};

WebStudio.Applications.SurfSite.prototype.onSlidersPanelHide = function()
{
	var pageEditor = this.getPageEditor();
	
	if (pageEditor)
	{
		this.hidePageEditor();
	}	
};

WebStudio.Applications.SurfSite.prototype.onSlidersPanelShow = function()
{
	var pageEditor = this.getPageEditor();
	
	if (pageEditor)
	{
		this.hidePageEditor();
		this.showPageEditor();
	}	
};

WebStudio.Applications.SurfSite.prototype.onSelected = function()
{
	this.showPageEditor();
};

WebStudio.Applications.SurfSite.prototype.onUnselected = function()
{
	this.hidePageEditor();
};
