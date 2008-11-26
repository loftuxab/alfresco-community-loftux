if (typeof WebStudio == "undefined" || !WebStudio)
{
	WebStudio = {};
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
	};

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
	
	// set initial scroll info
	this.scrollLeft = 0;
	this.scrollTop = 0;
};

WebStudio.PageEditor.prototype = new WebStudio.AbstractTemplater('WebStudio.PageEditor');

WebStudio.PageEditor.prototype.activate = function()
{
	this.buildGeneralLayer();
};

WebStudio.PageEditor.prototype.onTabItemClick = function(id, data)
{
};

WebStudio.PageEditor.prototype.resizeTabItems = function()
{
	for (var tn in this.tabs)
	{
		if(this.tabs.hasOwnProperty(tn))
		{
			this.tabs[tn].resize();
		}
	}
};

WebStudio.PageEditor.prototype.addTabItem = function(el) 
{
	var whiteOverlay = this.PageEditorTab.el.clone();
	var colorOverlay = this.PageEditorTab.el.clone();
	var backPanelOverlay = this.PageEditorTab.el.clone();
	var optionsOverlay = this.PageEditorTab.el.clone();

	var ti = new WebStudio.PETabItem(el, whiteOverlay, colorOverlay, backPanelOverlay, optionsOverlay, this);
	this.tabs[ti.id] = ti;
	return ti;
};

WebStudio.PageEditor.prototype.removeTabItems = function()
{
	for (var tn in this.tabs)
	{
		if(this.tabs.hasOwnProperty(tn))
		{
			var whiteOverlay = this.tabs[tn].whiteOverlay;
			if(whiteOverlay && whiteOverlay.parentNode)
			{
				whiteOverlay.parentNode.removeChild(whiteOverlay);
			}
	
			var colorOverlay = this.tabs[tn].colorOverlay;
			if(colorOverlay && colorOverlay.parentNode)
			{
				colorOverlay.parentNode.removeChild(colorOverlay);
			}		
	
			var backPanelOverlay = this.tabs[tn].backPanelOverlay;
			if(backPanelOverlay && backPanelOverlay.parentNode)
			{
				backPanelOverlay.parentNode.removeChild(backPanelOverlay);
			}		
	
			var optionsOverlay = this.tabs[tn].optionsOverlay;
			if(optionsOverlay && optionsOverlay.parentNode)
			{
				optionsOverlay.parentNode.removeChild(optionsOverlay);
			}
		}
	}
	
	this.tabs = {};
};

WebStudio.PageEditor.prototype.show = function()
{
	this.generalLayer.style.visibility = '';
	this.isHide = false;
	this.isShow = true;
	return this;
};

WebStudio.PageEditor.prototype.showFast = function()
{
	this.generalLayer.style.visibility = '';
	this.isHide = false;
	this.isShow = true;
	return this;
};

WebStudio.PageEditor.prototype.hide = function()
{
	this.generalLayer.style.visibility = 'hidden';
	this.isHide = true;
	this.isShow = false;
	return this;
};

WebStudio.PageEditor.prototype.hideTabItems = function()
{
	for (var tn in this.tabs) 
	{
		if(this.tabs.hasOwnProperty(tn))
		{
			this.tabs[tn].whiteOverlay.setStyle('display', 'none');
			this.tabs[tn].colorOverlay.setStyle('display', 'none');
			this.tabs[tn].backPanelOverlay.setStyle('display', 'none');
			this.tabs[tn].optionsOverlay.setStyle('display', 'none');
		}
	}
};

WebStudio.PageEditor.prototype.showTabItems = function()
{
	for (var tn in this.tabs)
	{
		if(this.tabs.hasOwnProperty(tn))
		{
			this.tabs[tn].whiteOverlay.setStyle('display', 'block');
			this.tabs[tn].colorOverlay.setStyle('display', 'block');
			//this.tabs[tn].backPanelOverlay.setStyle('display', 'block');
			//this.tabs[tn].optionsOverlay.setStyle('display', 'block');
		}
	}
};

WebStudio.PETabItem = function(el, whiteOverlay, colorOverlay, backPanelOverlay, optionsOverlay, pageEditor)
{
	var _this = this;
	
	this.el = el;
	this.id = "region-overlay-" + this.el.getAttribute("regionId");
	this.whiteOverlay = whiteOverlay;
	this.colorOverlay = colorOverlay;
	this.backPanelOverlay = backPanelOverlay;
	this.optionsOverlay = optionsOverlay;
	this.pageEditor = pageEditor;
	this.editWnd = null;
	this.w = -1;
	this.h = -1;
	
	this.originalBackgroundColor = this.el.getStyle("background-color");

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

	// copy information into backpanel overlay
	this.backPanelOverlay.setAttribute("regionId", this.regionId);
	this.backPanelOverlay.setAttribute("regionSourceId", this.regionSourceId);
	this.backPanelOverlay.setAttribute("regionScopeId", this.regionScopeId);
	this.backPanelOverlay.setAttribute("regionScopeId", this.regionScopeId);
	this.backPanelOverlay.setAttribute("componentId", this.componentId);
	this.backPanelOverlay.setAttribute("componentTypeId", this.componentTypeId);
	this.backPanelOverlay.setAttribute("componentEditorUrl", this.componentEditorUrl);

	// copy information into options overlay
	this.optionsOverlay.setAttribute("regionId", this.regionId);
	this.optionsOverlay.setAttribute("regionSourceId", this.regionSourceId);
	this.optionsOverlay.setAttribute("regionScopeId", this.regionScopeId);
	this.optionsOverlay.setAttribute("regionScopeId", this.regionScopeId);
	this.optionsOverlay.setAttribute("componentId", this.componentId);
	this.optionsOverlay.setAttribute("componentTypeId", this.componentTypeId);
	this.optionsOverlay.setAttribute("componentEditorUrl", this.componentEditorUrl);

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

	// Set up the BackPanel Overlay
	this.backPanelOverlay.id = "options-background-overlay-" + this.regionId + "-" + this.regionScopeId + "-" + this.regionSourceId;
	//this.backPanelOverlay.innerHTML = "<img src='/studio/overlay/default/images/backpanel.png' width='100%' height='100%'>";
	this.backPanelOverlay.innerHTML = "<img src='/studio/overlay/default/images/backpanel_silver.png' width='100%' height='100%'>";
	this.backPanelOverlay.style.position = "absolute";
	this.backPanelOverlay.injectInside(document.body);
	this.backPanelOverlay.setOpacity(1);
	this.backPanelOverlay.setStyle('display', 'none');
	this.backPanelOverlay.setStyle('border-top', '1px black solid');
	this.backPanelOverlay.setStyle('border-left', '1px black solid');
	this.backPanelOverlay.setStyle('border-right', '1px black solid');
	this.backPanelOverlay.setStyle('border-bottom', '1px black solid');
	//this.backPanelOverlay.setStyle('zIndex', this.colorOverlay.zIndex + 1);

	// Set up the Options Overlay
	this.optionsOverlay.id = "options-overlay-" + this.regionId + "-" + this.regionScopeId + "-" + this.regionSourceId;
	this.optionsOverlay.innerHTML = this.generateOptionsHtml(this.optionsOverlay.id);
	this.optionsOverlay.style.position = "absolute";
	this.optionsOverlay.injectInside(document.body);
	/*
	this.optionsOverlay.setStyle('border-top', '1px white solid');
	this.optionsOverlay.setStyle('border-left', '1px white solid');
	this.optionsOverlay.setStyle('border-right', '1px gray solid');
	this.optionsOverlay.setStyle('border-bottom', '1px gray solid');
	this.optionsOverlay.setStyle('margin', '1px');
	*/
	this.optionsOverlay.setStyle('display', 'none');
	//this.optionsOverlay.setStyle('zIndex', this.backPanelOverlay.zIndex + 1);
	
	this.optionsOverlay.addEvent("click", function(e) {
		e = new Event(e);
		
		_this.restore();

		e.stop();
		
	});

	// add color overlay events
	this.colorOverlay.addEvent("click", function(e) {
		e = new Event(e);
				
		_this.expand();
		
		e.stop();
	});

	var myFx = new Fx.Style(this.colorOverlay, 'opacity', {
		wait: false,
		duration: 300,
		transition: Fx.Transitions.Quart.easeInOut
	});
	this.colorOverlay.addEvent('mouseenter', function() { myFx.start(0.2, 0.5); });
	this.colorOverlay.addEvent('mouseleave', function() { myFx.start(0.5, 0.2); });
	
	// full resize
	this.resize();	
};

WebStudio.PETabItem.prototype.checkCloseOptionsOverlay = function()
{
	this.checkCount++;
	if(this.checkCount > this.checkTotal)
	{
		$clear(this.checkInterval);
		this.checkInterval = null;
		
		// close the window
		this.restore();		
	}
};

WebStudio.PETabItem.prototype.expand = function()
{
	var _this = this;
	
	this.originalWidth = this.el.offsetWidth;
	this.originalHeight = this.el.offsetHeight;
	
	// enforce minimum widths and heights	
	var width = this.el.offsetWidth;
	var height = this.el.offsetHeight;
	if(width < 300) {
		width = 300;
	}
	if(height < 150) {
		height = 150;
	}

	this.resizeTab(width, height);
	
	var unflippedColor = this.colorOverlay.style.backgroundColor;

	// flip it	
	jQuery(this.el).flip({ direction: 'tb', bgColor: unflippedColor, color: '#333333', speed: 400 });
	
	var f = function(){
	
		// show the back panel div
		_this.backPanelOverlay.setStyle('display', 'block');
		
		// show the options div
		_this.optionsOverlay.setStyle('display', 'block');
		
		// launch magnifier
    	jQuery("#magnifier_" + _this.optionsOverlay.id).magnifier({
    		overlap: true,
    		distance: 50,
    		click: function(e, ui){
    		
    			e = new Event(e);
    			
    			var elem = ui.current;
    			if(elem.id)
    			{
    				if(elem.id.substring(0,5) == "edit_")
    				{
    					_this.loadForm(_this.el.id);
    				}
    				if(elem.id.substring(0,11) == "standalone_")
    				{
    					var url = WebStudio.url.studio("/c/view/" + _this.componentId);
    					Alf.openBrowser('component', url);
    				}
    				if(elem.id.substring(0,7) == "remove_")
    				{
    					_this.onDeleteClickEWnd();
    				}
    			}
    			
				e.stop();
    			
    		}
    	});
    	
    	// mark as "expanded"
    	_this.expanded = true;
		
	};
	
	// wait 750 ms, then fire function
	f.delay(750);
};

WebStudio.PETabItem.prototype.restore = function()
{
	var _this = this;
	
	// hide the options div
	this.optionsOverlay.setStyle('display', 'none');
	
	// hide the back panel div
	this.backPanelOverlay.setStyle('display', 'none');

	var unflippedColor = this.colorOverlay.style.backgroundColor;
	
	// flip it	
	jQuery(this.el).flip({ direction: 'bt', bgColor: '#333333', color: unflippedColor, speed: 400 });

	var f = function(){
	
		var height = _this.originalHeight;
		var width = _this.originalWidth;
			
    	// unmark as "expanded"
    	_this.expanded = false;

		// force tab to resize to underlying element
		Alf.resizeToChildren(_this.el);
		    	
    	// resize tabs to their underlying dom element
    	_this.pageEditor.resizeTabItems();			
	};
	
	// wait 750 ms, then fire function
	f.delay(750);
};

WebStudio.PETabItem.prototype.generateOptionsHtml = function(uid)
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
	var html = "<table width='100%' class='optionsOverlay' cellpadding='2' cellspacing='2'>";
	if(this.componentId)
	{
		// if we have a bound component, we'll do it this way
		var componentImageUrl = WebStudio.overlayImagesPath + "/icons/component_large.gif";
		html += "<tr>";
		html += "<td><img src='" + componentImageUrl + "'/></td>";
		html += "<td width='100%'>";
		html += "<b>Component:</b> " + this.componentTitle;
		html += "<br/>";
		html += "<b>Type:</b> " + this.componentTypeTitle;
		html += "</tr>";		
		
		// information about the region
		html += "<tr>";
		html += "<td><img src='" + regionImageUrl + "'/></td>";
		html += "<td>";
		html += "<b>Region:</b> " + this.regionId;
		html += "<br/>";
		html += "<b>Scope:</b> " + this.regionScopeId;
		html += "</td>";
		html += "</tr>";
		
		// magnifiers
		html += "<tr>";
		html += "<td colspan='2'>";
		
		html += "<img height='6px' src='" + WebStudio.url.studio("/overlay/default/images/spacer.gif") + "'/>";

		// magnifier test block
		html += "<div id='magnifier_" + uid + "' style='height: 32px; padding-left: 4px'>";
	
		// Edit Command
		html += "<img id='edit_"+uid+"' src='" + WebStudio.url.studio("/overlay/default/images/componentedit/paper&pencil_48.png") + "' width='48' height='48' style='padding: 8px' title='Edit the properties for this component' />";
	
		// View Standalone
		html += "<img id='standalone_"+uid+"' src='" + WebStudio.url.studio("/overlay/default/images/componentedit/computer_48.png") + "' width='48' height='48' style='padding: 8px' title='View this component standalone (in a new window)' />";
	
		// Remove
		html += "<img id='remove_"+uid+"' src='" + WebStudio.url.studio("/overlay/default/images/componentedit/cancel_48.png") + "' width='48' height='48' style='padding: 8px' title='Remove this component' />";
	
		html += "</div>";
		
		html += "</td>";
		html += "</tr>";
	}
	else
	{
		// otherwise, we'll just show information about the region
		html += "<tr>";
		html += "<td><img src='" + regionImageUrl + "'/></td>";
		html += "<td>";
		html += this.regionId;
		html += "<br/>";
		html += this.regionScopeId;
		html += "</td>";
		html += "</tr>";
	}
	
	html += "</table>";
	
	return html;
};

WebStudio.PETabItem.prototype.resize = function()
{
	var top = this.el.getTop();
	var left = this.el.getLeft();
	
	// absolute mount points
	var absX = 0;
	if(!WebStudio.app.isDockingPanelHidden())
	{
		absX = absX + $('SurfaceSplitterPanel').offsetWidth + $('AlfSplitterDivider').offsetWidth;
	}
	var absY = $('AlfMenuTemplate').offsetHeight;
	
	// the total amount of horizontal dead space
	var horzCrud = absX + Alf.getScrollerSize().w;
	
	// the amount of vertical crud space
	var vertCrud = absY + Alf.getScrollerSize().h;
	
	// compute the normalized top and left, adjust for scroll
	// the result can be negative if it has been scrolled off the page
	top = top - absY - this.pageEditor.scrollTop;
	left = left - absX - this.pageEditor.scrollLeft;
	
	// compute the relative maximum width of the viewing area
	var elWidth = this.el.offsetWidth;
	var maxWidth = document.body.offsetWidth - horzCrud;
	if(left + elWidth > maxWidth)
	{
		elWidth = maxWidth - left;
	}
	
	// compute the relative maximum height
	var elHeight = this.el.offsetHeight;
	var maxHeight = document.body.offsetHeight - vertCrud;
	if(top + elHeight > maxHeight)
	{
		elHeight = maxHeight - top;
	}
	
	// make sure it is in valid range to be drawn on the screen
	var valid = true;
	if(elWidth <= 0)
	{
		valid = false;
	}
	if(elHeight <= 0)
	{
		valid = false;
	}
	if(top + elHeight <= 0)
	{
		valid = false;
	}
	if(left + elWidth <= 0)
	{
		valid = false;
	}
	
	if(valid)
	{
		// if this is true, then we have some "area" to draw
		// if the top or left are less than zero, set back to zero
		// for drawing purposes
		if(top < 0 || left <0)
		{
			if(top < 0)
			{
				elHeight = elHeight + top;
				top = 0;
			}
			if(left < 0)
			{
				elWidth = elWidth + left;
				left = 0;
			}
			
			// we have to hide the "backpanel and options" in this case
			if(this.backPanelOverlay)
			{
				this.backPanelOverlay.style.display = "none";
			}		
			if(this.optionsOverlay)
			{
				this.optionsOverlay.style.display = "none";
			}						
		}
		else
		{
			// valid to show the "backpanel and options"
			if(this.expanded)
			{
				if(this.backPanelOverlay)
				{
					this.backPanelOverlay.style.display = "block";
				}		
				if(this.optionsOverlay)
				{
					this.optionsOverlay.style.display = "block";
				}
			}
		}						

		// SET UP all coordinates
				
		if(window.ie)
		{
			this.whiteOverlay.style.width = (elWidth) + "px";
			this.whiteOverlay.style.height = (elHeight+4) + "px";
			this.whiteOverlay.style.top = (absY + top) + "px";
			this.whiteOverlay.style.left = (absX + left) + "px";
		
			this.colorOverlay.style.width = (elWidth) + "px";
			this.colorOverlay.style.height = (elHeight+4) + "px";
			this.colorOverlay.style.top = (absY + top) + "px";
			this.colorOverlay.style.left = (absX + left) + "px";
			
			if(this.backPanelOverlay)
			{
				this.backPanelOverlay.style.width = (elWidth) + "px";
				this.backPanelOverlay.style.height = (elHeight+4) + "px";
				this.backPanelOverlay.style.top = (absY + top) + "px";
				this.backPanelOverlay.style.left = (absX + left) + "px";
			}	
		
			if(this.optionsOverlay)
			{
				this.optionsOverlay.style.width = (elWidth) + "px";
				this.optionsOverlay.style.height = (elHeight+4) + "px";
				this.optionsOverlay.style.top = (absY + top) + "px";
				this.optionsOverlay.style.left = (absX + left) + "px";
			}			
		}
		else
		{
			// small variances in offset for FireFox
			
			this.whiteOverlay.style.width = (elWidth-1) + "px";
			this.whiteOverlay.style.height = (elHeight+1) + "px";
			this.whiteOverlay.style.top = (absY + top) + "px";
			this.whiteOverlay.style.left = (absX + left) + "px";
		
			this.colorOverlay.style.width = (elWidth-1) + "px";
			this.colorOverlay.style.height = (elHeight+1) + "px";
			this.colorOverlay.style.top = (absY + top) + "px";
			this.colorOverlay.style.left = (absX + left) + "px";
			
			if(this.backPanelOverlay)
			{
				this.backPanelOverlay.style.width = (elWidth-1) + "px";
				this.backPanelOverlay.style.height = (elHeight-1) + "px";
				this.backPanelOverlay.style.top = (absY + top) + "px";
				this.backPanelOverlay.style.left = (absX + left) + "px";
			}	
		
			if(this.optionsOverlay)
			{
				this.optionsOverlay.style.width = (elWidth-1) + "px";
				this.optionsOverlay.style.height = (elHeight-1) + "px";
				this.optionsOverlay.style.top = (absY + top) + "px";
				this.optionsOverlay.style.left = (absX + left) + "px";
			}	
		}
		
		// ensure these overlays show up
		this.whiteOverlay.style.display = "block";
		this.colorOverlay.style.display = "block";		
	}
	else
	{
		// hide the unseen overlays
		this.whiteOverlay.style.display = "none";
		this.colorOverlay.style.display = "none";
		if(this.backPanelOverlay)
		{
			this.backPanelOverlay.style.display = "none";
		}		
		if(this.optionsOverlay)
		{
			this.optionsOverlay.style.display = "none";
		}			
	}	
};

WebStudio.PETabItem.prototype.resizeTab = function(w, h)
{
	this.el.style.width = w + "px";
	this.el.style.height = h + "px";
	this.pageEditor.resizeTabItems();
};

WebStudio.PETabItem.prototype.onAddClickEWnd = function()
{
	/*
	var w = new WebStudio.Wizard();
	w.setDefaultJson( {
		regionId: this.regionId,
		regionScopeId: this.regionScopeId,
		regionSourceId: this.regionSourceId,
		refreshSession:'true'
	});
	w.start(WebStudio.ws.studio('/wizard/addnewcomponent'), 'addnewcomponent');
	*/
};

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
};

WebStudio.PETabItem.prototype.onEditClickEWnd = function()
{
	/*
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
	*/
};

WebStudio.PETabItem.prototype.onCloseEWnd = function()
{
	this.editWnd.popout();

	this.restore();
};


WebStudio.PETabItem.prototype.loadForm = function(id)
{
	var _this = this;
	
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
	this.editWnd.hide();

	this.editWnd.generalLayer.setStyle("background-color", "white");
	
	this.editWnd.Body.el.setOpacity(1.0);

	this.editWnd.onAddClick = this.onAddClickEWnd;
	this.editWnd.onDeleteClick = this.onDeleteClickEWnd;
	this.editWnd.onEditClick = this.onEditClickEWnd;
	this.editWnd.onClose = this.onCloseEWnd.bind(this);

	// scaling factors and centering calculations
	var totalWidth = document.body.offsetWidth;
	var totalHeight = document.body.offsetHeight;
	var scaleX = 0.6;
	var scaleY = 0.75;
	this.editWnd.setWidth(totalWidth * scaleX);
	this.editWnd.setHeight(totalHeight * scaleY);

	var x = (totalWidth * ((1-scaleX) * 0.5));
	var y = (totalHeight * ((1-scaleY) * 0.5));
	
	this.editWnd.setCoords(x, y);

	// lets just do away with the delete button	
	this.editWnd.hideButton("delete");
	
	// reference to application
	this.editWnd.application = this.pageEditor.application;

	// add an override to the successload event
	var _editWnd = this.editWnd;
	this.editWnd.saveFormSuccess = function(r)
	{
		_editWnd.application.faultRegion(_editWnd.regionId, _editWnd.regionScopeId);
		
		_editWnd.shutdown();
		
	};
	
	// load up the component's "edit" mode
	var url = WebStudio.url.studio(this.editWnd.componentEditorUrl);
	this.editWnd.loadEditor(url);
			
	// do the flyout effect
	this.flyout(id);
};

WebStudio.PETabItem.prototype.flyout = function(id)
{
	var largeImageUrl = WebStudio.url.studio("/overlay/default/images/componentedit/empty_form_large.png");
	//var smallImageUrl = WebStudio.url.studio("/overlay/default/images/componentedit/paper_48.png");
	var smallImageUrl = WebStudio.url.studio("/overlay/default/images/spacer.gif");
	 
	// do a little flyout effect
	var zEl = $(id);
	
	var tempA = new Element("A", { href: largeImageUrl } );
	tempA.setStyle("position", "absolute");
	tempA.setStyle("width", zEl.offsetWidth);
	tempA.setStyle("height", zEl.offsetHeight);
	tempA.setStyle("top", zEl.getTop());
	tempA.setStyle("left", zEl.getLeft());
	tempA.setStyle("display", "block");
	$(tempA).injectInside($(document.body));

	var tempImg = new Element("IMG", { src: smallImageUrl, border: 0 } );
	tempImg.setStyle("position", "absolute");
	tempImg.setStyle("display", "block");
	$(tempImg).injectInside($(tempA));

	var _this = this;
	
	this._tempA = tempA;
	this._tempImg = tempImg;
	
	var startWidth = zEl.offsetWidth;
	var startHeight = zEl.offsetHeight;
	
	jQuery(tempA).flyout({
		flyOutFinish: function(it){

			// TODO: check for editWnd.isFormLoaded()			
			_this.editWnd.popup();			

			$('loader').remove();
			
			// remove placeholders
			$(_this._tempImg).remove();
			$(_this._tempA).remove();			
		},
		inSpeed: 300,
		startWidth: startWidth,
		startHeight: startHeight
	});
	
	jQuery(tempA).click();
};

WebStudio.PageEditor.prototype.onScroll = function(left, top)
{
	this.scrollLeft = left;
	this.scrollTop = top;
	
	this.resizeTabItems();
};
