/**
   PAGE BUILDER APPLICATION
**/
WebStudio.Applications.Presentation = WebStudio.Applications.Abstract.extend({});

WebStudio.Applications.Presentation.prototype.getSlidersSectorTemplateId = function()
{
	return "PresentationApplication_SlidersSectorTemplate";
};

WebStudio.Applications.Presentation.prototype.getSlidersPanelDomId = function()
{
	return "PresentationApplication_SplitterPanel";
};

WebStudio.Applications.Presentation.prototype.hideAllDesigners = function()
{
	this.hidePageEditor();
	this.hideTemplateDesigner();
};

WebStudio.Applications.Presentation.prototype.showPageEditor = function()
{
	if(!this.pageEditor)
	{
		this.pageEditor = new WebStudio.PageEditor();
		this.pageEditor.application = this;
		this.pageEditor.activate();
	}
	if(this.pageEditor)
	{
		this.setupPageEditor();
		this.pageEditor.showTabItems();
	}
};

WebStudio.Applications.Presentation.prototype.setupPageEditor = function()
{
	// Remove any existing overlays
	this.pageEditor.removeTabItems();
	
	// Wraps overlays on top of all of the regions
	var regions = WebStudio.app.panels.secondPanel.getElementsByTagName("div");
	for(var i = 0; i < regions.length; i++)
	{
		// get the region properties
		var regionId = regions[i].getAttribute("regionId");
		var regionScopeId = regions[i].getAttribute("regionScopeId");
		var regionSourceId = regions[i].getAttribute("regionSourceId");

		if(regionScopeId && regionId)
		{
			var el = $(regions[i]);
			this.pageEditor.addTabItem(el);
		}		
	}
	
	// get the selected applet
	var selectedAppletId = this.getActiveAppletId();
	var applet = this.getApplet(selectedAppletId);
	if(applet)
	{
		if(applet.bindPageEditor)
		{
			applet.bindPageEditor(this.pageEditor);
		}
	}	
};

WebStudio.Applications.Presentation.prototype.hidePageEditor = function()
{
	if(this.pageEditor)
	{
		//this.pageEditor.hideTabItems();
		this.pageEditor.restoreAllTabItems();
	}
};

WebStudio.Applications.Presentation.prototype.showTemplateDesigner = function()
{
	if(!this.templateDesigner)
	{
		// start the template designer
		this.templateDesigner = new WebStudio.TemplateDesigner('TemplateDesigner');
		this.templateDesigner.injectObject = $('AlfSplitterPanel2');
		this.templateDesigner.activate();
		this.templateDesigner.application = this;
	}
	
	if(this.templateDesigner)
	{
		// show the template designer
		this.templateDesigner.show();
		
		// hide overflow for the panel
		$('AlfSplitterPanel2').setStyle('overflow', 'hidden');		
	}
};

WebStudio.Applications.Presentation.prototype.hideTemplateDesigner = function()
{
	if(this.templateDesigner)
	{
		this.templateDesigner.hide();
		
		// show overflow for the panel
		$('AlfSplitterPanel2').setStyle('overflow', 'auto');
	}
};

WebStudio.Applications.Presentation.prototype.GoToTemplateDisplay = function(templateId)
{
	// hide everything
	this.hideAllDesigners();
	
	// show the "templates" applet
	this.showApplet("templates");

	// show the template designer
	this.showTemplateDesigner();
	
	// tell the "templates designer" about our selection
	this.templateDesigner.selectTemplate(templateId);
	
	// record the selected template
	this.selectedTemplate = templateId;		
};

/**
 * Fired when an item is dropped from a tree view onto the page editor
 */
WebStudio.Applications.Presentation.prototype.dropFromTreeView = function(dropDivId, options)
{
	var regionTab = this.pageEditor.tabs[dropDivId];
	this.dropOntoRegion(regionTab,null,null,null,options);
};

WebStudio.Applications.Presentation.prototype.dropOntoRegion = function(regionTab, nodeData, source, e, options)
{
	// get the kind of thing that was dropped
	var alfType = options.data.alfType;
	var config = null;
	
	var cmType = null;
	var nodeRef = null;
	var nodeString = null;
	
	var mimetype = null;
	var filePath = null;
	var sourcePath = null;
	var sourceType = null;
	var sourceEndpoint = "alfresco";
	var isContainer = false;
	
	// SHORTCUT ACTION: they drop a web script component type
	// Special Handling for Web Scripts as components
	// Do binding and exit
	if ("webscriptComponent" == alfType)
	{
		config = WebStudio.components.newBinding();
		config["binding"]["componentType"] = "webscript";
		config["properties"]["title"] = "WebScript Component";
		config["properties"]["description"] = options.nodeId;
		config["properties"]["url"] = options.nodeId;
		this.bindToRegionTab(regionTab, config);
		return true;
	}

	/** WEB APPLICATION: FILE **/
	if ("file" == alfType)
	{
		sourcePath = options.data.path;
		sourceType = "webapp";
		filePath = options.data.path;
		mimetype = options.data.mimetype;
	}

	/** WEB APPLICATION: FOLDER **/
	if ("directory" == alfType)
	{
		sourcePath = options.data.path;
		sourceType = "webapp";
		mimetype = null;
		filePath = options.data.path;
		isContainer = true;
	}
	
	/** SPACES: FILE **/
	if ("dmFile" == alfType)
	{
		cmType = options.data.cmType;		
		nodeRef = options.data.nodeRef;		
		nodeString = WebStudio.app.toNodeString(nodeRef);
		filePath = options.data.path;		
		mimetype = options.data.mimetype;
		
		sourcePath = nodeString;
		sourceType = "space";
	}
	
	/** SPACES: FOLDER **/
	if ("dmSpace" == alfType)
	{	
		cmType = options.data.cmType;
		nodeRef = options.data.nodeRef;
		nodeString = WebStudio.app.toNodeString(nodeRef);
		filePath = options.data.path;
		
		sourcePath = nodeString;
		sourceType = "space";
		isContainer = true;
	}

	/** SITES: FILE **/
	if ("siteFile" == alfType)
	{
		cmType = options.data.cmType;		
		nodeString = "workspace/SpacesStore/" + options.data.nodeID;
		filePath = options.data.path;		
		mimetype = options.data.mimetype;
		
		sourcePath = nodeString;
		sourceType = "space";
	}
	
	/** SITES: FOLDER **/
	if ("siteSpace" == alfType)
	{	
		cmType = options.data.cmType;
		nodeString = "workspace/SpacesStore/" + options.data.nodeID;
		filePath = options.data.path;
		
		sourcePath = nodeString;
		sourceType = "space";
		isContainer = true;
	}
	
	/** COMPONENT TYPE **/
	if ("componentType" == alfType)
	{
		// TODO
		mimetype = null;
	}


	//////////////////////////////////////////
	// Now process the bindings
	//////////////////////////////////////////

	
	/** LIST VIEW **/
	if(isContainer)
	{
		// right now, the only way we have to display containers
		// is to bind to a "display items" component
		config = WebStudio.components.newDisplayItems(sourceType, sourceEndpoint, sourcePath, null);		
	}
		
	/** FILE BINDINGS **/
	if(!isContainer)
	{
		if(mimetype)
		{
			/** IMAGE **/
			if (mimetype.startsWith("image"))
			{
				// stock image component
				config = WebStudio.components.newImage(sourceType, sourceEndpoint, sourcePath, mimetype);
			}
			
			/** XML **/
			if (mimetype == "text/xml")
			{		
				// TODO: configurable XML display
				// compatibility with Alfresco WCM Web Forms
				//config = WebStudio.components.newXml("webapp", "alfresco", path, mimetype);
			}
			
			/** HTML **/
			if (mimetype == "text/html" && mimetype == "text/shtml") 
			{
				config = WebStudio.components.newInclude(sourceType, sourceEndpoint, sourcePath, mimetype);
			}
	
			/** VIDEO **/
			if (mimetype.startsWith("video"))
			{
				config = WebStudio.components.newVideo(sourceType, sourceEndpoint, sourcePath, mimetype);
				if(mimetype == "video/quicktime")
				{
					config["properties"]["player"] = "quicktime";
				}
				else
				{
					if(window.ie)
					{
						config["properties"]["player"] = "windowsmedia";
					}
					else
					{
						config["properties"]["player"] = "quicktime";
					}
				}
			}
	
			/** AUDIO **/
			if (mimetype.startsWith("audio"))
			{
				config = WebStudio.components.newAudio(sourceType, sourceEndpoint, sourcePath, mimetype);
			}
			
			if (mimetype == "audio/x-mpeg")
			{
				config = WebStudio.components.newFlashMP3(sourceType, sourceEndpoint, sourcePath, mimetype);
			}
									
			if (mimetype == "application/x-shockwave-flash")
			{
				config = WebStudio.components.newFlash(sourceType, sourceEndpoint, sourcePath, mimetype);
			}

			if (mimetype == "application/octet-stream")
			{
				if(filePath && filePath.endsWith(".flv"))
				{
					config = WebStudio.components.newFlash(sourceType, sourceEndpoint, sourcePath, mimetype);
					config["properties"]["fileext"] = "flv";
				}
			}

			if (mimetype == "video/mp4")
			{
				config = WebStudio.components.newFlash(sourceType, sourceEndpoint, sourcePath, mimetype);
				config["properties"]["fileext"] = "mp4";
			}

		}
		else
		{
			// an object of some kind...
		}
	}
	
	if(config)
	{
		this.bindToRegionTab(regionTab, config);
		return true;	
	}

	return false;
};

WebStudio.Applications.Presentation.prototype.bindToRegionTab = function(regionTab, config)
{
	var regionId = regionTab.regionId;
	var regionScopeId = regionTab.regionScopeId;
	var regionSourceId = regionTab.regionSourceId;

	this.bindToRegion(regionId, regionScopeId, regionSourceId, config);
};

WebStudio.Applications.Presentation.prototype.bindToRegion = function(regionId, regionScopeId, regionSourceId, config)
{
	var _this = this;
	
	// update binding
	config["binding"]["regionId"] = regionId;
	config["binding"]["regionSourceId"] = regionSourceId;
	config["binding"]["regionScopeId"] = regionScopeId;
	
	// fire the event
	var params = { "json" : Json.toString(config) };
	var url = WebStudio.ws.studio("/incontext/components", params);
	
	var myAjax = new Ajax(url, {
		method: 'get',
		onComplete: function(data){
			_this.RefreshPageRegion(data);
		}
	}).request();
};

WebStudio.Applications.Presentation.prototype.RefreshPageRegion = function(data)
{
	if(data)
	{
		data = Json.evaluate(data);
	}

	// refresh the object cache
	WebStudio.app.refreshObjectCache();

	var binding = { };
	
	// region data
	binding["regionId"] = data.regionId;
	binding["regionScopeId"] = data.regionScopeId;
	binding["regionSourceId"] = data.regionSourceId;
	
	// component binding data
	binding["componentId"] = data.componentId;
	binding["componentTypeId"] = data.componentTypeId;
	binding["componentTitle"] = data.componentTitle;
	binding["componentTypeTitle"] = data.componentTypeTitle;
	binding["componentEditorUrl"] = data.componentEditorUrl;

	this.faultRegion(binding);
};

WebStudio.Applications.Presentation.prototype.faultRegion = function(binding)
{
	var _this = this;
	
	var regionId = binding["regionId"];
	var regionScopeId = binding["regionScopeId"];
	
 	var templateId = Surf.context.getCurrentTemplateId();
 	
	var url = WebStudio.url.studio("/region/" + regionId + "/" + regionScopeId + "/" + templateId);
	if(WebStudio.request.queryString && WebStudio.request.queryString.length > 0)
	{
		url = url + "&" + WebStudio.request.queryString;
	}
	var myAjax = new Ajax(url, {
		method: 'get',
		onComplete: function(responseObject) {
			_this.faultRegionSuccess(responseObject, binding);
		}
	}).request();	
};

WebStudio.Applications.Presentation.prototype.faultRegionSuccess = function(html, binding)
{
	var _this = this;
	
	// region binding data
	var regionId = binding["regionId"];
	var regionScopeId = binding["regionScopeId"];
	var regionSourceId = binding["regionSourceId"];

	// component binding data
	var componentId = binding["componentId"];
	var componentTypeId = binding["componentTypeId"];
	var componentTitle = binding["componentTitle"];
	var componentTypeTitle = binding["componentTypeTitle"];
	var componentEditorUrl = binding["componentEditorUrl"];	
	
	// this is the function that we call to do final processing
	var finalProcessing = function(scriptText, regionDiv, restorePageEditor)
	{
		// set up new component binding information
		if(componentId)
		{
			WebStudio.configureComponent(regionDiv.id, componentId, componentTypeId, componentTitle, componentTypeTitle, componentEditorUrl);
		}

		// process scripts and other dependencies
		for(var st = 0; st < scriptText.length; st++)
		{
			// evaluate the script
			try {
				var t = scriptText[st];
				if(t)
				{
					Alf.evaluate(t);
				}
			}
			catch(err)
			{
				alert("ERROR: " + err);
				// TODO: explore how and why this could occur
			}				
		}
		
		// resize the element to the size of its children
		// this removes excess white space
		Alf.resizeToChildren(regionDiv);
		
		// fire the load event
		Alf.fireEvent(regionDiv, "load");				

		// restore the page editor (if it was enabled originally)			
		if(restorePageEditor)
		{
			// TODO: Ideally, this should trigger from completion of tags and load of the DOM element
			// it should be tied to an update event of some kind
			
			// delay
			_this.showPageEditor.delay(500, _this);
		}						
	};
	
	var delayFinalProcessing = function(scriptText, regionDiv, restorePageEditor)
	{
		var f = function()
		{
			var g = finalProcessing.bind(_this);
			g(scriptText, regionDiv, restorePageEditor);
		};
		
		f.bind(this).delay(500);
	};
	
	var processHtml = function(regionDiv, restorePageEditor)
	{
		// any script text that we need to process
		var scriptText = [];			
		
		// deal with SCRIPT and LINK tags in the retrieved content
		var hasDependencies = false;
		var x = regionDiv.getElementsByTagName("script");
		if(x)
		{
			for(var x1 = 0; x1 < x.length; x1++)
			{
				// gather up script text
				if(x[x1] && x[x1].text && x[x1].text !== "")
				{
					// store for execution later
					scriptText[scriptText.length] = x[x1].text;
				}
				
				// flag if there are script imports to process
				if(x[x1] && x[x1].src && x[x1].src !== "")
				{
					hasDependencies = true;
				}
			}
		}
		var y = regionDiv.getElementsByTagName("link");
		if(y)
		{
			for(var y1 = 0; y1 < y.length; y1++)
			{
				// flag if there are link imports to process
				if(y[y1] && y[y1].src && y[y1].src !== "")
				{
					requiresLoading = true;
				}
			}
		}

		if(!hasDependencies)
		{
			// if we don't require any loading
			// execute the final processing
			finalProcessing(scriptText, regionDiv, restorePageEditor);
		}
		else
		{
			// load all dependencies and do final processing on
			// completion of the load
			var zFunc = delayFinalProcessing.pass([scriptText, regionDiv, restorePageEditor], _this); 
			_this.tempLoader = WebStudio.util.loadDependencies(regionDiv.id, regionDiv, zFunc);
		}
	};
		
	// walk through all of the divs and find the one that matches this
	var regions = WebStudio.app.panels.secondPanel.getElementsByTagName("div");
	for(var i = 0; i < regions.length; i++)
	{
		// get the region properties
		var divRegionId = regions[i].getAttribute("regionId");
		var divRegionScopeId = regions[i].getAttribute("regionScopeId");
		var divRegionSourceId = regions[i].getAttribute("regionSourceId");
		
		if(divRegionId == regionId && divRegionScopeId == regionScopeId)
		{
			var restorePageEditor = false;
			if(this.pageEditor)
			{
				this.hidePageEditor();
				
				this.pageEditor.hideTabItems();
				this.pageEditor.removeTabItems();
				this.pageEditor = null;
				
				restorePageEditor = true;
			} 			

			var regionDiv = $(regions[i]);
			
			
			// blank out the existing region div
			Alf.setHTML(regionDiv, "");
			WebStudio.unconfigureComponent(regionDiv.id);
			
			
			// manually parse out the region div
			var origHtml = html;
			var i1 = html.indexOf("<div");
			var i2 = html.lastIndexOf("</div>");
			//var i2 = html.indexOf("</div>", i1);
			html = html.substring(i1,i2+6);
			
			// replace contents of regionDiv
			// this sets the child nodes into the DIV
			WebStudio.util.setHTML(regionDiv, html, true);
			
			// ideally, we want to now wait until the HTML finishes loading
			var delayedProcessHtml = processHtml.pass([regionDiv, restorePageEditor], _this);
			delayedProcessHtml.delay(1000); 
		}
	}
};

WebStudio.Applications.Presentation.prototype.showContentTypeAssociationsDialog = function()
{
	// show the cta control
	if(!this.ctaDialog)
	{
		this.ctaDialog = new WebStudio.CTADialog();
	
		// activate and pop up
		this.ctaDialog.activate();
	}
	
	this.ctaDialog.popup();
};

WebStudio.Applications.Presentation.prototype.showTemplateAssociationsDialog = function()
{
	// show the pta control
	if(!this.ptaDialog)
	{
		this.ptaDialog = new WebStudio.PTADialog();
	
		// activate and pop up
		this.ptaDialog.activate();
	}
	
	this.ptaDialog.popup();
};

WebStudio.Applications.Presentation.prototype.resize = function()
{
	if(this.templateDesigner && !this.templateDesigner.isHidden())
	{
		this.templateDesigner.resize();
	}
};

WebStudio.Applications.Presentation.prototype.onContentScroll = function(left, top)
{
	// tell the page editor to resize all of its colored region overlays
	if(this.pageEditor)
	{
		this.pageEditor.onScroll(left, top);
	}
	
	if(this.templateDesigner)
	{
		this.templateDesigner.onScroll(left, top);
	}
};

WebStudio.Applications.Presentation.prototype.getPageEditor = function()
{
	return this.pageEditor;
};

WebStudio.Applications.Presentation.prototype.getTemplateDesigner = function()
{
	return this.templateDesigner;
};

WebStudio.Applications.Presentation.prototype.onPanelsResize = function()
{
	// tell the page editor to resize all of its colored region overlays
	if(this.pageEditor)
	{
		this.pageEditor.resizeTabItems();
	}
	
	if(this.templateDesigner)
	{
		this.templateDesigner.resize();
	}
};

WebStudio.Applications.Presentation.prototype.onEndEdit = function()
{
	// close all applets
	var appletIds = this.getAppletIds();
	for(var i = 0; i < appletIds.length; i++)
	{
		var appletId = appletIds[i];
		var applet = this.getApplet(appletId);
		
		applet.onClose();
	}
	
	
	// hide all designers	
	this.hideAllDesigners();
	
	// destroy the page editor
	if(this.pageEditor)
	{
		this.pageEditor.removeTabItems();
		this.pageEditor.destroy();
		this.pageEditor = null;
	}
	
	// destroy the template designer
	if(this.templateDesigner)
	{
		this.templateDesigner.cleanup();
		this.templateDesigner.destroy();
		this.templateDesigner = null;
	}
};

WebStudio.Applications.Presentation.prototype.onSlidersPanelHide = function()
{
	if(this.pageEditor)
	{
		this.hidePageEditor();
		this.showPageEditor();
	}
};

WebStudio.Applications.Presentation.prototype.onSlidersPanelShow = function()
{
	if(this.pageEditor)
	{
		this.hidePageEditor();
		this.showPageEditor();
	}
};

WebStudio.Applications.Presentation.prototype.onSelected = function()
{
	this.showPageEditor();
};

WebStudio.Applications.Presentation.prototype.onUnselected = function()
{
	this.removePageEditor();
};
