/**
   SURF ASSEMBLE APPLICATION
**/
WebStudio.Applications.SurfAssemble = WebStudio.Applications.Abstract.extend({});

WebStudio.Applications.SurfAssemble.prototype.getSlidersSectorTemplateId = function()
{
	return "SurfAssembleApplication_SlidersSectorTemplate";
};

WebStudio.Applications.SurfAssemble.prototype.getSlidersPanelDomId = function()
{
	return "SurfAssembleApplication_SplitterPanel";
};

WebStudio.Applications.SurfAssemble.prototype.onSlidersPanelHide = function()
{
	var pageEditor = this.getPageEditor();
	
	if (pageEditor)
	{
		this.hidePageEditor();
	}	
};

WebStudio.Applications.SurfAssemble.prototype.onSlidersPanelShow = function()
{
	var pageEditor = this.getPageEditor();
	
	if (pageEditor)
	{
		this.hidePageEditor();
		this.showPageEditor();
	}	
};

WebStudio.Applications.SurfAssemble.prototype.onSelected = function()
{
	this.showPageEditor();
};

WebStudio.Applications.SurfAssemble.prototype.onUnselected = function()
{
	this.hidePageEditor();
};
