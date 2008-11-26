WebStudio.Applications.WebDesigner = WebStudio.Applications.Abstract.extend({});

WebStudio.Applications.WebDesigner.prototype.getMenu = function()
{
	var _this = this;
	
	if(!this.menu)
	{
		var menu = new WebStudio.MenuNew();
		menu.onItemClick = function(id){
			_this.onMenuItemClick(id);
		};
		menu.activate();
	
		// alfresco menu
		
		var r0 = menu.addRootItem('alfresco', "", WebStudio.overlayImagesPath + "/AlfrescoLogo16.gif");
		//var r0 = menu.addRootItem('alfresco', "", WebStudio.overlayIconsPath + "/visit_alfresco.jpg");
		r0.addItem("visit-alfresco", "Visit Alfresco", "text", WebStudio.overlayImagesPath + "/AlfrescoLogo16.gif");
		r0.addItem("explore-alfresco-network", "Explore Alfresco Network", "text", WebStudio.overlayImagesPath + "/network_16.gif");
		r0.addItem("separator","item2","separator");
		r0.addItem("about-webstudio", "About Web Studio", "text", WebStudio.overlayImagesPath + "/webstudio_16.gif");
		
		// site menu
		var r1 = menu.addRootItem("webproject", "Web Project", "");
		r1.addItem("site-switch", "Switch Sites...", "text", WebStudio.overlayIconsPath + "/switch_website.gif");
		r1.addItem("site-import", "Import...", "text", WebStudio.overlayIconsPath + "/export.gif", { disable: true} );
		r1.addItem("site-export", "Export...", "text", WebStudio.overlayIconsPath + "/import.gif", { disable: true} );
		r1.addItem("separator2","item2","separator");	
		r1.addItem("site-content-type-associations", "Content Associations...", "text", WebStudio.overlayIconsPath + "/content_type_associations.gif");
		r1.addItem("separator3","item2","separator");
		r1.addItem("site-view-modified-items", "View Modified Items", "text", WebStudio.overlayIconsPath + "/dashboard.gif", { disable: true } );
		r1.addItem("site-view-web-project", "View Web Project", "text", WebStudio.overlayIconsPath + "/dashboard.gif" ); 	
		r1.addItem("site-view-sandbox", "View Sandbox", "text", WebStudio.overlayIconsPath + "/dashboard.gif", { disable: true } );
		r1.addItem("separator4","item2","separator");
		r1.addItem("site-properties", "Properties...", "text", WebStudio.overlayIconsPath + "/properties.gif");
		
		// page menu
		var r2 = menu.addRootItem('page', "Current Page", "");
		r2.addItem("page-template-associations-view", "Template Associations...", "text", WebStudio.overlayIconsPath + "/template_associations.gif");
		r2.addItem("page-template-edit", "Edit Page Template", "text", WebStudio.overlayIconsPath + "/template_associations.gif");
		r2.addItem("separator1","item2","separator");
		r2.addItem("page-properties", "Properties...", "text", WebStudio.overlayIconsPath + "/properties.gif");
			
		// options menu
		var r4 = menu.addRootItem('options', 'Options', ''); 
		r4.addItem("refresh-cache", "Refresh Cache", "text", WebStudio.overlayIconsPath + "/refresh_cache.gif");
		r4.addItem("separator","item2","separator");
		r4.addItem("show-docking-panel", "Toggle Docking Panel", "checkbox", null, {checked : !this.isHideDockingPanel});

		this.menu = menu;
	}
		
	return this.menu;
};

WebStudio.Applications.WebDesigner.prototype.getTabTitle = function()
{
	return "Studio";
};

WebStudio.Applications.WebDesigner.prototype.getTabImageUrl = function()
{
	return "/images/webstudio_surface_16.gif";
};

WebStudio.Applications.WebDesigner.prototype.getSlidersSectorTemplateId = function()
{
	return "SurfaceSlidersSectorTemplate";
};

WebStudio.Applications.WebDesigner.prototype.getSlidersPanelDomId = function()
{
	return "SurfaceSplitterPanel";
};

/*
 * Fired when a menu item is clicked
 */
WebStudio.Applications.WebDesigner.prototype.onMenuItemClick = function(index,data) 
{
	var _this = this;
	var url = null;
	var w = null;
	
	// check that the user is logged in
	if(!WebStudio.app.userAuth())
	{
		return;
	}

	var pageId = Surf.context.getCurrentPageId();
	
	// Alfresco
	
	if(index == 'visit-alfresco')
	{
		WebStudio.app.openBrowser("alfresco.com", "http://www.alfresco.com");
	}
	
	if(index == 'explore-alfresco-network')
	{
		WebStudio.app.openBrowser("network", "http://network.alfresco.com");
	}
	
	if(index == 'about-webstudio')
	{
		WebStudio.app.openBrowser("aboutWebStudio", "http://wiki.alfresco.com/wiki/Web_Studio");
	}
			
	// Site - Content Type Associations
	if (index == 'site-content-type-associations')
	{
		this.showContentTypeAssociationsDialog();
	}
	
	// Site - Switch Web Site
	if (index == 'site-switch')
	{
		WebStudio.app.resetContext({
		
			success: function(oResponse) {
							
				// set up the context
				WebStudio.context.webProjectId = null;
				WebStudio.context.sandboxId = null;
				WebStudio.context.storeId = null;				

				WebStudio.app.sandboxMounted();
				
			}
			,
			failure: function(oResponse) {
			
				// TODO
			}
			,
			timeout: 7000
		});		
	}
	
	// Site - Site Export
	if (index == 'site-export')
	{
	}
	
	// Site - Site Import
	if (index == 'site-import')
	{
	}
	
	// Site - Site Properties
	if (index == 'site-properties')
	{
		w = new WebStudio.Wizard();
		w.setDefaultJson(
		{
			refreshSession: 'true'
		});
		url = WebStudio.ws.studio("/wizard/site/config");
		w.start(url, 'siteproperties');			
	}
	
	// Site - View Modified Items
	if (index == 'site-view-modified-items')
	{
	}
	
	// Site - View Web Project
	if(index == 'site-view-web-project')
	{
		url = "http://localhost:8080/alfresco/service/webframework/redirect/jsf-client/browse/webproject/" + WebStudio.context.getWebProjectId();
		WebStudio.app.openBrowser("alfresco", url);
	}	
	
	// Site - View Sandbox
	if(index == 'site-view-sandbox')
	{
	}

	// Page - Edit Template
	if (index == 'page-template-edit')
	{
		// jump to the current template
		this.GoToTemplateDisplay(Surf.context.getCurrentTemplateId());
	}
	
	// Page - Template Associations
	if (index == 'page-template-associations-view')
	{
		this.showTemplateAssociationsDialog();
	}
	
	// Page - Page Properties
	if (index == 'page-properties')
	{
		w = new WebStudio.Wizard();
		w.setDefaultJson(
		{
			refreshSession: 'true',
			pageId: pageId
		});
		url = WebStudio.ws.studio("/wizard/navigation/edit");
		w.start(url, 'editpage');
	}

	// Options - Cache Refresh
	if (index == 'refresh-cache')
	{
		WebStudio.app.refreshAll();
	}
	
	// Options - Show Docking Panel
	if (index == 'show-docking-panel')
	{
		WebStudio.app.toggleSlidersPanel();
	}	
};

WebStudio.Applications.WebDesigner.prototype.hideAllDesigners = function()
{
	this.hidePageEditor();
	this.hideTemplateDesigner();
};

WebStudio.Applications.WebDesigner.prototype.showPageEditor = function()
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

WebStudio.Applications.WebDesigner.prototype.setupPageEditor = function()
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
	
	var applet1 = this.getApplet("components");
	if(applet1)
	{
		applet1.treeView.setDroppables(this.pageEditor.tabs);
	}

	var applet2 = this.getApplet("webcontent");
	if(applet2)
	{
		applet2.treeView.setDroppables(this.pageEditor.tabs);
	}

	var applet3 = this.getApplet("spaces");
	if(applet3)
	{
		applet3.treeView.setDroppables(this.pageEditor.tabs);
	}

	var applet4 = this.getApplet("sites");
	if(applet4)
	{
		applet4.treeView.setDroppables(this.pageEditor.tabs);
	}	
};

WebStudio.Applications.WebDesigner.prototype.hidePageEditor = function()
{
	if(this.pageEditor)
	{
		this.pageEditor.hideTabItems();
	}
};

WebStudio.Applications.WebDesigner.prototype.showTemplateDesigner = function()
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

WebStudio.Applications.WebDesigner.prototype.hideTemplateDesigner = function()
{
	if(this.templateDesigner)
	{
		this.templateDesigner.hide();
		
		// show overflow for the panel
		$('AlfSplitterPanel2').setStyle('overflow', 'auto');
	}
};

WebStudio.Applications.WebDesigner.prototype.GoToTemplateDisplay = function(templateId)
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
WebStudio.Applications.WebDesigner.prototype.dropFromTreeView = function(dropDivId, options)
{
	var regionTab = this.pageEditor.tabs[dropDivId];
	this.dropOntoRegion(regionTab,null,null,null,options);
};

WebStudio.Applications.WebDesigner.prototype.dropOntoRegion = function(regionTab, nodeData, source, e, options)
{
	// get the kind of thing that was dropped
	var alfType = options.data.alfType;
	var config = null;
	var path = null;
	var mimetype = null;
	
	var cmType = null;
	var nodeRef = null;
	var nodeString = null;

	// ACTION: they drop a file from the webapp slider
	if ("file" == alfType)
	{
		path = options.data.path;
		mimetype = options.data.mimetype;
				
		// FILE: image
		// RESULT: bind in an image component
		if (mimetype && mimetype.startsWith("image"))
		{
			config = this.newConfigBinding();
			config["binding"]["componentType"] = "/component/common/image";
			config["resources"]["source"] = {
				"type" : "webapp",
				"endpoint" : "alfresco",
				"value" : path
			};
			config["properties"]["title"] = "Image Component";
			config["properties"]["description"] = path;

			this.bindToRegionTab(regionTab, config);

			return true;
		}

		// FILE: xml (TODO)
		// RESULT: bind in XML display control
		if (mimetype && mimetype == "text/xml")
		{		
			config = this.newConfigBinding();
			config["binding"]["componentType"] = "/component/common/xmldisplay";
			config["resources"]["source"] = {
				"type" : "webapp",
				"endpoint" : "alfresco",
				"value" : path
			};
			config["properties"]["title"] = "XML Display Component";
			config["properties"]["description"] = path;

			this.bindToRegionTab(regionTab, config);

			return true;
		}

		// FILE: html (TODO)
		// RESULT: bind in an include control
		if (mimetype && 
			(	(mimetype == "text/html") ||
				(mimetype == "text/shtml")	) )
		{
			config = this.newConfigBinding();
			config["binding"]["componentType"] = "/component/common/include";
			config["resources"]["source"] = {
				"type" : "webapp",
				"endpoint" : "alfresco",
				"value" : path
			};
			config["properties"]["title"] = "Include Component";
			config["properties"]["description"] = path;
			config["properties"]["container"] = "div";

			this.bindToRegionTab(regionTab, config);

			return true;
		}

		// FILE: they dropped a video
		// RESULT: bind in a video component
		if (mimetype && mimetype.startsWith("video"))
		{
			config = this.newConfigBinding();
			config["binding"]["componentType"] = "/component/common/video";
			config["resources"]["source"] = {
				"type" : "webapp",
				"endpoint" : "alfresco",
				"value" : path
			};
			config["properties"]["title"] = "Video Component";
			config["properties"]["description"] = path;
			config["properties"]["mimetype"] = mimetype;

			this.bindToRegionTab(regionTab, config);

			return true;
		}

		// FILE: they dropped audio
		// RESULT: bind in a audio component
		if (mimetype && mimetype.startsWith("audio"))
		{
			config = this.newConfigBinding();
			config["binding"]["componentType"] = "/component/common/audio";
			config["resources"]["source"] = {
				"type" : "webapp",
				"endpoint" : "alfresco",
				"value" : path
			};
			config["properties"]["title"] = "Audio Component";
			config["properties"]["description"] = path;
			config["properties"]["mimetype"] = mimetype;

			this.bindToRegionTab(regionTab, config);

			return true;
		}
	}


	// ACTION: they drop a directory from the webapp slider
	if ("directory" == alfType)
	{
		path = options.data.path;
		
		config = this.newConfigBinding();
		config["binding"]["componentType"] = "/component/common/display-items";
		config["resources"]["source"] = {
			"type" : "webapp",
			"endpoint" : "alfresco",
			"value" : path
		};
		config["properties"]["title"] = "Display Items Component";
		config["properties"]["container"] = "div";
	
		this.bindToRegionTab(regionTab, config);
		
		return true;
	}


	// ACTION: they drop a component type
	if ("componentType" == alfType)
	{
		// TODO
		return true;
	}
	
	
	// ACTION: they drop a web script component type
	// Special Handling for Web Scripts as components
	if ("webscriptComponent" == alfType)
	{
		config = this.newConfigBinding();
		config["binding"]["componentType"] = "webscript";
		config["properties"]["title"] = "WebScript Component";
		config["properties"]["description"] = options.nodeId;
		config["properties"]["url"] = options.nodeId;

		this.bindToRegionTab(regionTab, config);

		return true;	
	}


	// ACTION: they dropped a navigation node
	if ("navNode" == alfType)
	{
		config = this.newConfigBinding();
		config["binding"]["componentType"] = "webscript";
		config["properties"]["title"] = "WebScript Component";
		config["properties"]["description"] = options.nodeId;
		config["properties"]["rootNode"] = options.nodeId;
		config["properties"]["renderer"] = "horizontal";

		this.bindToRegionTab(regionTab, config);

		return true;	
	}

	// ACTION: they dropped a dm node
	if ("dmFile" == alfType)
	{
		cmType = options.data.cmType;
		
		nodeRef = options.data.nodeRef;		
		nodeString = WebStudio.app.toNodeString(nodeRef);
				
		var contentUrl = WebStudio.ws.repo("/api/node/" + nodeString + "/content");
		
		mimetype = options.data.mimetype;
		if(!mimetype)
		{
			// TODO: handle this
		}
		else
		{		
			// RESULT: bind in an image component
			if (mimetype && mimetype.startsWith("image"))
			{
				config = this.newConfigBinding();
				config["binding"]["componentType"] = "/component/common/image";
				config["resources"]["source"] = {
					"type" : "space",
					"endpoint" : "alfresco",
					"value" : nodeString
				};
				config["properties"]["title"] = "Image Component";
	
				this.bindToRegionTab(regionTab, config);
	
				return true;
			}
			
			// RESULT: bind in the html
			if (mimetype == "text/html")
			{
				config = this.newConfigBinding();
				config["binding"]["componentType"] = "/component/common/include";
				config["resources"]["source"] = {
					"type" : "space",
					"endpoint" : "alfresco",
					"value" : nodeString
				};
				config["properties"]["title"] = "Include Component";
				config["properties"]["container"] = "div";
	
				this.bindToRegionTab(regionTab, config);
				
				return true;
			} 
			
			// RESULT: bind in a video component
			if (mimetype && mimetype.startsWith("video"))
			{
				config = this.newConfigBinding();
				config["binding"]["componentType"] = "/component/common/video";
				config["resources"]["source"] = {
					"type" : "space",
					"endpoint" : "alfresco",
					"value" : nodeString
				};
				config["properties"]["title"] = "Video Component";
				config["properties"]["mimetype"] = mimetype;
	
				this.bindToRegionTab(regionTab, config);
	
				return true;
			}
	
			// RESULT: bind in a audio component
			if (mimetype && mimetype.startsWith("audio"))
			{
				config = this.newConfigBinding();
				config["binding"]["componentType"] = "/component/common/audio";
				config["resources"]["source"] = {
					"type" : "space",
					"endpoint" : "alfresco",
					"value" : nodeString
				};
				config["properties"]["title"] = "Audio Component";
				config["properties"]["mimetype"] = mimetype;
	
				this.bindToRegionTab(regionTab, config);
	
				return true;
			}
			
		}
	}


	// ACTION: they dropped a dm space
	if ("dmSpace" == alfType)
	{
		cmType = options.data.cmType;

		// RESULT: display the contents of the space in a list
		nodeRef = options.data.nodeRef;
		nodeString = WebStudio.app.toNodeString(nodeRef);
		
		config = this.newConfigBinding();
		config["binding"]["componentType"] = "/component/common/display-items";
		config["resources"]["source"] = {
			"type" : "space",
			"endpoint" : "alfresco",
			"value" : nodeString
		};
		config["properties"]["title"] = "Display Items Component";
		config["properties"]["container"] = "div";

		this.bindToRegionTab(regionTab, config);
		
		return true;
	}

	return false;
};

WebStudio.Applications.WebDesigner.prototype.bindToRegionTab = function(regionTab, config)
{
	var regionId = regionTab.regionId;
	var regionScopeId = regionTab.regionScopeId;
	var regionSourceId = regionTab.regionSourceId;

	this.bindToRegion(regionId, regionScopeId, regionSourceId, config);
};

WebStudio.Applications.WebDesigner.prototype.bindToRegion = function(regionId, regionScopeId, regionSourceId, config)
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

WebStudio.Applications.WebDesigner.prototype.RefreshPageRegion = function(data)
{
	if(data)
	{
		data = Json.evaluate(data);
	}

	// refresh the object cache
	WebStudio.app.refreshObjectCache();

	var componentId = data.componentId;
	var componentTypeId = data.componentTypeId;
	var regionId = data.regionId;
	var regionScopeId = data.regionScopeId;

	this.faultRegion(regionId, regionScopeId);
};

WebStudio.Applications.WebDesigner.prototype.faultRegion = function(regionId, regionScopeId)
{
	var _this = this;
	
 	var templateId = Surf.context.getCurrentTemplateId();
 	
	var url = WebStudio.url.studio("/region/" + regionId + "/" + regionScopeId + "/" + templateId);
	if(WebStudio.request.queryString && WebStudio.request.queryString.length > 0)
	{
		url = url + "&" + WebStudio.request.queryString;
	}
	var myAjax = new Ajax(url, {
		method: 'get',
		onComplete: function(responseObject) {
			_this.faultRegionSuccess(responseObject, regionId, regionScopeId);
		}
	}).request();	
};

WebStudio.Applications.WebDesigner.prototype.faultRegionSuccess = function(html, regionId, regionScopeId)
{
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

			var regionDiv = regions[i];
			
			// replace contents of regionDiv
			WebStudio.util.setHTML(regionDiv, html, true);
									
			// process any tags
			var x = regionDiv.getElementsByTagName("script");
			for(var a = 0; a < x.length; a++)
			{
				if(x[a] && x[a].text)
				{
					try {
						eval(x[a].text);
					}
					catch(err)
					{
						// TODO: explore how and why this could occur
					}
				}
			}
			
			// recompute size of dom element
			for(var a = 0; a < regionDiv.childNodes; a++)
			{
				var tag = regionDiv.childNodes[a].nodeName;
				if(tag)
				{
					if( (tag != "SCRIPT") && (tag != "LINK") )
					{
						regionDiv.style.width = regionDiv.childNodes[a].offsetWidth;
						regionDiv.style.height = regionDiv.childNodes[a].offsetHeight;
					}
				}
			}			

			// restore the page editor (if it was enabled originally)			
			if(restorePageEditor)
			{
				// TODO: Ideally, this should trigger from completion of tags and load of the DOM element
				// it should be tied to an update event of some kind
				
				// delay
				this.showPageEditor.delay(500, this);
			}			
			
		}
	}
};

WebStudio.Applications.WebDesigner.prototype.newConfigBinding = function()
{
	var config = { };
	config["operation"] = "bindComponent";
	config["binding"] = { };
	config["properties"] = { };
	config["resources"] = { };

	return config;
};

WebStudio.Applications.WebDesigner.prototype.showContentTypeAssociationsDialog = function()
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

WebStudio.Applications.WebDesigner.prototype.showTemplateAssociationsDialog = function()
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

WebStudio.Applications.WebDesigner.prototype.resize = function()
{
	if(this.templateDesigner && !this.templateDesigner.isHidden())
	{
		this.templateDesigner.resize();
	}
};

WebStudio.Applications.WebDesigner.prototype.onContentScroll = function(left, top)
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

WebStudio.Applications.WebDesigner.prototype.getPageEditor = function()
{
	return this.pageEditor;
};

WebStudio.Applications.WebDesigner.prototype.getTemplateDesigner = function()
{
	return this.templateDesigner;
};

WebStudio.Applications.WebDesigner.prototype.onPanelsResize = function()
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

WebStudio.Applications.WebDesigner.prototype.onEndEdit = function()
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

WebStudio.Applications.WebDesigner.prototype.onSlidersPanelHide = function()
{
	if(this.pageEditor)
	{
		this.hidePageEditor();
		this.showPageEditor();
	}
};

WebStudio.Applications.WebDesigner.prototype.onSlidersPanelShow = function()
{
	if(this.pageEditor)
	{
		this.hidePageEditor();
		this.showPageEditor();
	}
};