
if (typeof WebStudio == "undefined")
{
	var WebStudio = {};
}

WebStudio.PageEditor = function() 
{
	this.defaultContainer = document.body;
	this.injectObject = document.body;

	this.defaultTemplateSelector = 'div[id=AlfrescoPageEditorTemplate]';

	this.defaultElementsConfig = {
		PageEditorTab:{
			selector:'div[id=PageEditorTab]',
			remove:true
		}
	}

	this.tabs = {};

	// Page Scope Colors (green)
	this.bps_overlay_color1_high = "#00DD00";
	this.bps_overlay_color1 = "#00AA00";
	this.bps_overlay_color1_low = "#003300";

	// Template Scope Colors (blue)
	this.bts_overlay_color1_high = "#0000DD";
	this.bts_overlay_color1 = "#0000AA";
	this.bts_overlay_color1_low = "#000033";

	// Global Scope Colors (reddish)
	this.bgs_overlay_color1_high = "#DD0000";
	this.bgs_overlay_color1 = "#AA0000";
	this.bgs_overlay_color1_low = "#330000";

}

WebStudio.PageEditor.prototype = new WebStudio.AbstractTemplater('WebStudio.PageEditor');

WebStudio.PageEditor.prototype.activate = function()
{
	this.buildGeneralLayer();
}

WebStudio.PageEditor.prototype.onTabItemClick = function(id, data)
{
}

WebStudio.PageEditor.prototype.resizeTabItems = function()
{
	for (var tn in this.tabs)
	{
		this.tabs[tn].resize();
	}
}

WebStudio.PageEditor.prototype.addTabItem = function(el) 
{
	var whiteOverlay = this.PageEditorTab.el.clone();
	var colorOverlay = this.PageEditorTab.el.clone();

	var ti = new WebStudio.PETabItem(el, whiteOverlay, colorOverlay, this);
	this.tabs[ti.id] = ti;
	return ti;
}

WebStudio.PageEditor.prototype.removeTabItems = function()
{
	for (var tn in this.tabs)
	{
		var whiteOverlay = this.tabs[tn].whiteOverlay
		if(whiteOverlay && whiteOverlay.parentNode)
		{
			whiteOverlay.parentNode.removeChild(whiteOverlay);
		}

		var colorOverlay = this.tabs[tn].colorOverlay
		if(colorOverlay && colorOverlay.parentNode)
		{
			colorOverlay.parentNode.removeChild(colorOverlay);
		}
		
		var infoOverlay = this.tabs[tn].infoOverlay;
		if(infoOverlay && infoOverlay.parentNode)
		{
			infoOverlay.parentNode.removeChild(infoOverlay);
		}
	}
	
	this.tabs = {};
}

WebStudio.PageEditor.prototype.show = function()
{
	this.generalLayer.style.visibility = '';
	this.isHide = false;
	this.isShow = true;
	return this;
}

WebStudio.PageEditor.prototype.showFast = function()
{
	this.generalLayer.style.visibility = '';
	this.isHide = false;
	this.isShow = true;
	return this;
}

WebStudio.PageEditor.prototype.hide = function()
{
	this.generalLayer.style.visibility = 'hidden';
	this.isHide = true;
	this.isShow = false;
	return this;
}

WebStudio.PageEditor.prototype.hideTabItems = function()
{
	for (var tn in this.tabs) 
	{
		this.tabs[tn].whiteOverlay.setStyle('display', 'none');
		this.tabs[tn].colorOverlay.setStyle('display', 'none');
	}
}

WebStudio.PageEditor.prototype.showTabItems = function()
{
	for (var tn in this.tabs)
	{
		this.tabs[tn].whiteOverlay.setStyle('display', 'block');
		this.tabs[tn].colorOverlay.setStyle('display', 'block');
	}
}

WebStudio.PETabItem = function(el, whiteOverlay, colorOverlay, pageEditor)
{
	this.el = el;
	this.id = "region-overlay-" + this.el.getAttribute("regionId");
	this.whiteOverlay = whiteOverlay;
	this.colorOverlay = colorOverlay;
	this.pageEditor = pageEditor;
	this.editWnd = null;
	this.w = -1;
	this.h = -1;

	// information about region
	this.regionId = this.el.getAttribute("regionId");
	this.regionSourceId = this.el.getAttribute("regionSourceId");
	this.regionScopeId = this.el.getAttribute("regionScopeId");
	
	// information about component binding	
	this.componentId = this.el.getAttribute("componentId");
	this.componentTypeId = this.el.getAttribute("componentTypeId");
	this.componentTitle = this.el.getAttribute("componentTitle");
	this.componentTypeTitle = this.el.getAttribute("componentTypeTitle");
	this.componentEditorUrl = this.el.getAttribute("componentEditorUrl");
	
	// copy information into white overlay
	this.whiteOverlay.setAttribute("regionId", this.regionId);
	this.whiteOverlay.setAttribute("regionSourceId", this.regionSourceId);
	this.whiteOverlay.setAttribute("regionScopeId", this.regionScopeId);
	this.whiteOverlay.setAttribute("regionScopeId", this.regionScopeId);
	this.whiteOverlay.setAttribute("componentId", this.componentId);
	this.whiteOverlay.setAttribute("componentTypeId", this.componentTypeId);
	this.whiteOverlay.setAttribute("componentEditorUrl", this.componentEditorUrl);

	// copy information into color overlay
	this.colorOverlay.setAttribute("regionId", this.regionId);
	this.colorOverlay.setAttribute("regionSourceId", this.regionSourceId);
	this.colorOverlay.setAttribute("regionScopeId", this.regionScopeId);
	this.colorOverlay.setAttribute("regionScopeId", this.regionScopeId);
	this.colorOverlay.setAttribute("componentId", this.componentId);
	this.colorOverlay.setAttribute("componentTypeId", this.componentTypeId);
	this.colorOverlay.setAttribute("componentEditorUrl", this.componentEditorUrl);

	// Set up the White Overlay
	this.whiteOverlay.id = "white-overlay-" + this.regionId + "-" + this.regionScopeId + "-" + this.regionSourceId;
	this.whiteOverlay.innerHTML = "&nbsp;";
	this.whiteOverlay.setOpacity(0.15);
	this.whiteOverlay.style.position = "absolute";
	this.whiteOverlay.style.backgroundColor = "white";
	this.whiteOverlay.injectInside(document.body);
	
	// figure out how to format the display
	var colorHigh = this.pageEditor.bps_overlay_color1_high;
	var colorLow = this.pageEditor.bps_overlay_color1_low;
	var color = this.pageEditor.bps_overlay_color1;
	if ("template" == this.regionScopeId)
	{
		colorHigh = this.pageEditor.bts_overlay_color1_high;
		colorLow = this.pageEditor.bts_overlay_color1_low;
		color = this.pageEditor.bts_overlay_color1;
	}
	if ("global" == this.regionScopeId)
	{
		colorHigh = this.pageEditor.bgs_overlay_color1_high;
		colorLow = this.pageEditor.bgs_overlay_color1_low;
		color = this.pageEditor.bgs_overlay_color1;		
	}
	
	// Set up the Color Overlay
	this.colorOverlay.id = "color-overlay-" + this.regionId + "-" + this.regionScopeId + "-" + this.regionSourceId;
	this.colorOverlay.innerHTML = "&nbsp;";
	this.colorOverlay.style.position = "absolute";
	this.colorOverlay.style.backgroundColor = color;
	this.colorOverlay.style.cssText += ";border:1px black dotted;";
	this.colorOverlay.injectInside(document.body);
	this.colorOverlay.setOpacity(0.2);


	//var totalWindowWidth = document.body.offsetWidth;
	//var totalWindowHeight = document.body.offsetHeight;
	this.w = this.el.offsetWidth;
	this.h = this.el.offsetHeight;
	this.resize();

	// add color overlay events
	this.colorOverlay.addEvent("click", (function(e) {
		e = new Event(e);
				
		this.expand();
		
		e.stop();
	}).bind(this));

	this.colorOverlay.addEvent("mouseover", (function(e) {
		e = new Event(e);
		this.colorOverlay.setOpacity(0.4, true);
		
		e.stop();
	}).bind(this));

	this.colorOverlay.addEvent("mouseout", (function(e) {
		e = new Event(e);
		this.colorOverlay.setOpacity(0.2, true);
		
		e.stop();
	}).bind(this));	
}

WebStudio.PETabItem.prototype.expand = function(f)
{
	this.originalWidth = this.el.offsetWidth;
	this.originalHeight = this.el.offsetHeight;
	
	// create a text div
	this.infoOverlay = this.pageEditor.PageEditorTab.el.clone();
	this.infoOverlay.id = "info-overlay-" + this.regionId + "-" + this.regionScopeId + "-" + this.regionSourceId;
	this.infoOverlay.innerHTML = this.generateHtml();
	this.infoOverlay.style.position = "absolute";
	this.infoOverlay.injectInside(document.body);
	
	// minimum width = 480
	// minimum height = 360
	var width = this.el.offsetWidth;
	var height = this.el.offsetHeight;
	if(width < 480) {
		width = 480;
	}
	if(height < 360) {
		height = 360;
	}
	
	//var transition = Fx.Transitions.linear;
	//var transition = Fx.Transitions.Elastic.easeOut;
	var transition = Fx.Transitions.Cubic.easeInOut;

	var config = {
		duration : 250,
		transition: transition,
		wait: false,
		onStart: (function()
		{
			this.pageEditor.hideTabItems();
		}).bind(this),
		onComplete: (function()
		{
			this.pageEditor.showTabItems();
			this.resizeTab(width, height);
			this.openEWnd();		
		}).bind(this)
	};
	
	var myEffects = new Fx.Styles(this.el, config);
	myEffects.start({
    	'height': [this.el.style.height, height],
    	'width': [this.el.style.width, width]
	});
}

WebStudio.PETabItem.prototype.restore = function()
{
	if(this.infoOverlay && this.infoOverlay.parentNode)
	{
		this.infoOverlay.parentNode.removeChild(this.infoOverlay);
	}

	//var transition = Fx.Transitions.Elastic.easeOut;
	var transition = Fx.Transitions.Cubic.easeInOut;
	
	var height = this.originalHeight;
	var width = this.originalWidth;
	
	var config = {
		duration : 250,
		transition: transition,
		wait: false,
		onStart: (function()
		{
			this.pageEditor.hideTabItems();
		}).bind(this),		
		onComplete: (function()				
		{
			this.pageEditor.showTabItems();
			this.resizeTab(width, height);
		}).bind(this)
	}
	
	var myEffects = new Fx.Styles(this.el, config);
	myEffects.start({
    	'height': [this.el.style.height, height],
    	'width': [this.el.style.width, width]
	});
}

WebStudio.PETabItem.prototype.generateHtml = function()
{
	// figure out how to format the display
	var regionImageUrl = WebStudio.overlayImagesPath + "/icons/region_scope_page_large.gif";
	if ("template" == this.regionScopeId)
	{
		regionImageUrl = WebStudio.overlayImagesPath + "/icons/region_scope_template_large.gif";
	}
	if ("global" == this.regionScopeId)
	{
		regionImageUrl = WebStudio.overlayImagesPath + "/icons/region_scope_site_large.gif";
	}

	// figure out the html
	var html = "<table width='100%'>";
	html += "<tr>";
	html += "<td>";	
	html += "<img src='" + regionImageUrl + "'/>";
	html += "</td>";
	html += "<td style='color:black' width='100%'>" + this.regionId + "</td>";
	html += "</tr>";
	if(this.componentId)
	{
		var componentImageUrl = WebStudio.overlayImagesPath + "/icons/component_large.gif";
		html += "<tr>";
		html += "<td>";	
		html += "<img src='" + componentImageUrl + "'/>";
		html += "</td>";
		html += "<td style='color:black' width='100%'>" + this.componentTitle + " <i>(" + this.componentTypeTitle + ")</i></td>";
		html += "</tr>";
	}
	html += "</table>";

	return html;
}

WebStudio.PETabItem.prototype.resize = function()
{
	this.whiteOverlay.style.width = (this.el.offsetWidth) + "px";
	this.whiteOverlay.style.height = (this.el.offsetHeight) + "px";
	this.whiteOverlay.style.top = this.el.getTop() + "px";
	this.whiteOverlay.style.left = this.el.getLeft() + "px";

	this.colorOverlay.style.width = (this.el.offsetWidth) + "px";
	this.colorOverlay.style.height = (this.el.offsetHeight) + "px";
	this.colorOverlay.style.top = this.el.getTop() + "px";
	this.colorOverlay.style.left = this.el.getLeft() + "px";
	
	
	if(this.infoOverlay)
	{
	/*
		this.infoOverlay.style.width = (this.el.offsetWidth) + "px";
		this.infoOverlay.style.height = (this.el.offsetHeight) + "px";
		this.infoOverlay.style.top = this.el.getTop() + "px";
		this.infoOverlay.style.left = this.el.getLeft() + "px";
 	*/		
	}		
}

WebStudio.PETabItem.prototype.resizeTab = function(w, h)
{
	this.el.style.width = w + "px";
	this.el.style.height = h + "px";
	this.pageEditor.resizeTabItems();
}

WebStudio.PETabItem.prototype.onAddClickEWnd = function()
{
	var w = new WebStudio.Wizard();
	w.setDefaultJson( {
		regionId: this.regionId,
		regionScopeId: this.regionScopeId,
		regionSourceId: this.regionSourceId,
		refreshSession:'true'
	});
	w.start(WebStudio.ws.studio('/wizard/addnewcomponent'), 'addnewcomponent');

}

WebStudio.PETabItem.prototype.onDeleteClickEWnd = function()
{
	var w = new WebStudio.Wizard();
	w.setDefaultJson({
		regionId: this.regionId,
		regionScopeId: this.regionScopeId,
		regionSourceId: this.regionSourceId,
		componentId: this.componentId,
		componentTypeId: this.componentTypeId
	});
	w.start(WebStudio.ws.studio('/wizard/removecomponent'), 'removecomponent');
}

WebStudio.PETabItem.prototype.onEditClickEWnd = function()
{
	var w = new WebStudio.Wizard();
	w.setDefaultJson( {
		regionId: this.regionId,
		regionScopeId: this.regionScopeId,
		regionSourceId: this.regionSourceId,
		componentId: this.componentId,
		componentTypeId: this.componentTypeId,
		refreshSession:true
	});
	w.start(WebStudio.ws.studio('/component/' + this.componentTypeId + '/edit'), 'editcomponent');
}

WebStudio.PETabItem.prototype.onCloseEWnd = function()
{
	this.restore();
}

WebStudio.PETabItem.prototype.openEWnd = function()
{
	this.editWnd = new WebStudio.Region("WebStudio.Region");
	this.editWnd.injectObject = document.body;
	
	// set all properties
	this.editWnd.regionId = this.regionId;
	this.editWnd.regionScopeId = this.regionScopeId;
	this.editWnd.regionSourceId = this.regionSourceId;
	this.editWnd.componentId = this.componentId;
	this.editWnd.componentTypeId = this.componentTypeId;
	this.editWnd.componentTitle = this.componentTitle;
	this.editWnd.componentTypeTitle = this.componentTypeTitle;
	this.editWnd.componentEditorUrl = this.componentEditorUrl;
	
	this.editWnd.activate();

	this.editWnd.generalLayer.setStyle("background", "white");
	//this.editWnd.generalLayer.setStyle("background", "transparent none repeat scroll 0%");

	var theClazz = "region-window-body-unconfigured-region";
	if (this.componentId != null)
	{
		//theClazz = "region-window-body-configured-" + this.regionScopeId + "-region";
		//this.editWnd.Body.el.addClass(theClazz);
	}
	
	this.editWnd.Body.el.setOpacity(1.0);

	this.editWnd.onAddClick = this.onAddClickEWnd;
	this.editWnd.onDeleteClick = this.onDeleteClickEWnd;
	this.editWnd.onEditClick = this.onEditClickEWnd;
	this.editWnd.onClose = this.onCloseEWnd.bind(this);

	this.editWnd.setWidth($(this.colorOverlay).offsetWidth);
	this.editWnd.setHeight($(this.colorOverlay).offsetHeight);

	this.editWnd.setCoords($(this.colorOverlay).offsetLeft, $(this.colorOverlay).offsetTop);
	
	if(!this.componentId) 
	{
		this.editWnd.hideButton("delete");
	}
	
	// if there is already something here
	var url = null;
	this.editWnd.application = this.pageEditor.application;
	if(this.componentId)
	{
		// point at the component wizard
		url = WebStudio.url.studio(this.editWnd.componentEditorUrl);
		this.editWnd.loadEditor(url);
		
		// add an override to the successload event
		this.editWnd.saveFormSuccess = (function(r)
		{
			this.application.faultRegion(this.regionId, this.regionScopeId);
			
			this.shutdown();
			
		}).bind(this.editWnd);		
	}
	else
	{
		// show region information
		url = WebStudio.url.studio("/app/regionInfoOverlay.jsp?regionId=" + this.regionId + "&regionScopeId=" + this.regionScopeId + "&regionSourceId=" + this.regionSourceId);
		this.editWnd.load(url);
	}
		
	this.resizeTab(this.editWnd.generalLayer.offsetWidth, this.editWnd.generalLayer.offsetHeight);
}