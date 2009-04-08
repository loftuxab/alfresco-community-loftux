/**
   CONTENT EDITOR APPLICATION
**/
WebStudio.Applications.Content = WebStudio.Applications.Abstract.extend({});

WebStudio.Applications.Content.prototype.getSlidersSectorTemplateId = function()
{
	return "ContentApplication_SlidersSectorTemplate";
};

WebStudio.Applications.Content.prototype.getSlidersPanelDomId = function()
{
	return "ContentApplication_SplitterPanel";
};

WebStudio.Applications.Content.prototype.onSlidersPanelHide = function()
{
	var pageEditor = this.getPageEditor();
	
	if (pageEditor)
	{
		this.hidePageEditor();
	}	
};

WebStudio.Applications.Content.prototype.onSlidersPanelShow = function()
{
	var pageEditor = this.getPageEditor();
	
	if (pageEditor)
	{
		this.hidePageEditor();
		this.showPageEditor();
	}	
};

WebStudio.Applications.Content.prototype.onSelected = function()
{
	this.showPageEditor();
};

WebStudio.Applications.Content.prototype.onUnselected = function()
{
	this.hidePageEditor();
};
