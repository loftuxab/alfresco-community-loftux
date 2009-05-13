if (typeof WebStudio == "undefined" || !WebStudio)
{
	WebStudio = {};
}

WebStudio.PageBlocker = function() 
{
	this.defaultContainer = document.body;
	this.injectObject = document.body;

	this.defaultTemplateSelector = 'div[id=_AlfrescoPageBlocker]';
	
	this.defaultElementsConfig = {
		Frame: {
			selector: 'div[id=_AlfrescoPageBlockerFrame]'
		}
		,
		Editor: {
			selector: 'div[id=_AlfrescoPageBlockerEditor]'
		}
	};

	this.events = {};
	
	this.nodes = {};
	this.droppables = [];
	
	this.instance = { };
	
	this.showEditor = true;	
};

WebStudio.PageBlocker.prototype = new WebStudio.AbstractTemplater('WebStudio.PageBlocker');

WebStudio.PageBlocker.prototype.activate = function() 
{	
	this.buildGeneralLayer();
	
	// set up the frame
	this.Frame.el.id = "AlfrescoPageBlockerFrame";
	
	// set up the editor	
	this.Editor.id = "AlfrescoPageBlockerEditor";
	
	WebStudio.util.setStyle(this.Frame.el, 'display', 'block');
	WebStudio.util.setStyle(this.Editor.el, 'display', 'block');
	
	this.resize();

	return this;
};

WebStudio.PageBlocker.prototype.resize = function()
{
	if(this.Frame.el)
	{
		WebStudio.util.setStyle(this.Frame.el, 'left', 0);
		WebStudio.util.setStyle(this.Frame.el, 'top', 0);		
		WebStudio.util.setStyle(this.Frame.el, 'width', this.injectObject.offsetWidth);
		WebStudio.util.setStyle(this.Frame.el, 'height', this.injectObject.offsetHeight);		
	}
	
	if(this.Editor.el && this.showEditor)
	{
		WebStudio.util.setStyle(this.Editor.el, 'left', '7px');		
		WebStudio.util.setStyle(this.Editor.el, 'top', '7px');		
		WebStudio.util.setStyle(this.Editor.el, 'width', this.injectObject.offsetWidth - 16);		
		WebStudio.util.setStyle(this.Editor.el, 'height', this.injectObject.offsetHeight - 17);
	}
};

WebStudio.PageBlocker.prototype.build = function() 
{
	this.generalLayer.set({
		id: this.ID
	});
};

WebStudio.PageBlocker.prototype.onScroll = function(left, top)
{
	// TODO
};

