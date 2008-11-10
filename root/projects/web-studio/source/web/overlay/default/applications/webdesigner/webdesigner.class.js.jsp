<%
	String overlayPath = org.alfresco.web.studio.OverlayUtil.getOriginalURL(request, "/proxy/alfresco-web-studio/overlay/default");
	String iconsPath = overlayPath + "/images/icons";
%>

WebStudio.Applications.WebDesigner = WebStudio.Applications.Abstract.extend({});

WebStudio.Applications.WebDesigner.prototype.getMenu = function()
{
	if(!this.menu)
	{
		var menu = new WebStudio.MenuNew();
		menu.onItemClick = (function(id){
			this.onMenuItemClick(id);
		}).bind(this);
		menu.activate();
	
		// alfresco menu
		
		var r0 = menu.addRootItem('alfresco', "", "<%=overlayPath%>/images/AlfrescoLogo16.gif");
		//var r0 = menu.addRootItem('alfresco', "", "<%=iconsPath%>/visit_alfresco.jpg");
		r0.addItem("visit-alfresco", "Visit Alfresco", "text", "<%=overlayPath%>/images/AlfrescoLogo16.gif");
		r0.addItem("explore-alfresco-network", "Explore Alfresco Network", "text", "<%=overlayPath%>/images/network_16.gif");
		r0.addItem("separator","item2","separator");
		r0.addItem("about-webstudio", "About Web Studio", "text", "<%=overlayPath%>/images/webstudio_16.gif");
		
		// site menu
		var r1 = menu.addRootItem("webproject", "Web Project", "");
		r1.addItem("site-switch", "Switch Sites...", "text", "<%=iconsPath%>/switch_website.gif");
		r1.addItem("site-import", "Import...", "text", "<%=iconsPath%>/export.gif", { disable: true} );
		r1.addItem("site-export", "Export...", "text", "<%=iconsPath%>/import.gif", { disable: true} );
		r1.addItem("separator2","item2","separator");	
		r1.addItem("site-content-type-associations", "Content Associations...", "text", "<%=iconsPath%>/content_type_associations.gif");
		r1.addItem("separator3","item2","separator");
		r1.addItem("site-view-modified-items", "View Modified Items", "text", "<%=iconsPath%>/dashboard.gif", { disable: true } );
		r1.addItem("site-view-web-project", "View Web Project", "text", "<%=iconsPath%>/dashboard.gif" ); 	
		r1.addItem("site-view-sandbox", "View Sandbox", "text", "<%=iconsPath%>/dashboard.gif", { disable: true } );
		r1.addItem("separator4","item2","separator");
		r1.addItem("site-properties", "Properties...", "text", "<%=iconsPath%>/properties.gif");
		
		// page menu
		var r2 = menu.addRootItem('page', "Current Page", "");
		r2.addItem("page-template-associations-view", "Template Associations...", "text", "<%=iconsPath%>/template_associations.gif");
		r2.addItem("page-template-edit", "Edit Page Template", "text", "<%=iconsPath%>/template_associations.gif");
		r2.addItem("separator1","item2","separator");
		r2.addItem("page-properties", "Properties...", "text", "<%=iconsPath%>/properties.gif");
			
		// options menu
		var r4 = menu.addRootItem('options', 'Options', ''); 
		r4.addItem("refresh-cache", "Refresh Cache", "text", "<%=iconsPath%>/refresh_cache.gif");
		r4.addItem("separator","item2","separator");
		r4.addItem("show-docking-panel", "Toggle Docking Panel", "checkbox", null, {checked : !this.isHideDockingPanel});

		this.menu = menu;
	}
		
	return this.menu;
}

WebStudio.Applications.WebDesigner.prototype.getTabTitle = function()
{
	return "Studio";
}

WebStudio.Applications.WebDesigner.prototype.getTabImageUrl = function()
{
	return "/images/webstudio_surface_16.gif";
}

WebStudio.Applications.WebDesigner.prototype.getSlidersSectorTemplateId = function()
{
	return "SurfaceSlidersSectorTemplate";
}

WebStudio.Applications.WebDesigner.prototype.getSlidersPanelDomId = function()
{
	return "SurfaceSplitterPanel";
}

/*
 * Fired when a menu item is clicked
 */
WebStudio.Applications.WebDesigner.prototype.onMenuItemClick = function(index,data) 
{
	// check that the user is logged in
	if(!WebStudio.app.userAuth())
	{
		return;
	}

	var pageId = context.getCurrentPageId();
	
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
		
			success: (function(oResponse) {
							
				// set up the context
				WebStudio.context.webProjectId = null;
				WebStudio.context.sandboxId = null;
				WebStudio.context.storeId = null;				

				WebStudio.app.sandboxMounted();
				
			}).bind(this)
			,
			failure: function(oResponse) {
			
				// TODO
			}
			,
			timeout: 7000
		});
		
		/*
		var w = new WebStudio.Wizard();
		w.setDefaultJson(
		{
			refreshSession: 'true'
		});
		var url = WebStudio.ws.studio("/wizard/site/switch");
		w.start(url, 'siteswitch');
		*/			
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
		var w = new WebStudio.Wizard();
		w.setDefaultJson(
		{
			refreshSession: 'true'
		});
		var url = WebStudio.ws.studio("/wizard/site/config");
		w.start(url, 'siteproperties');			
	}
	
	// Site - View Modified Items
	if (index == 'site-view-modified-items')
	{
	}
	
	// Site - View Web Project
	if(index == 'site-view-web-project')
	{
		var url = "http://localhost:8080/alfresco/service/webframework/redirect/jsf-client/browse/webproject/" + WebStudio.context.getWebProjectId();
		WebStudio.app.openBrowser("alfresco", url);
	}	
	
	// Site - View Sandbox
	if(index == 'site-view-sandbox')
	{
	}

	// Page - Edit Template
	if (index == 'page-template-edit')
	{
		// TODO: flip open the "templates" slider
		var slider = this.getApplicationSlider("webdesigner", "webtemplates");
		
		// jump to the current template
		this.GoToTemplateDisplay(context.getCurrentTemplateId());
	}
	
	// Page - Template Associations
	if (index == 'page-template-associations-view')
	{
		this.showTemplateAssociationsDialog();
	}
	
	// Page - Page Properties
	if (index == 'page-properties')
	{
	    var pageId = context.getCurrentPageId();
	    
		var w = new WebStudio.Wizard();
		w.setDefaultJson(
		{
			refreshSession: 'true',
			pageId: pageId
		});
		var url = WebStudio.ws.studio("/wizard/navigation/edit");
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
}

WebStudio.Applications.WebDesigner.prototype.hideAllDesigners = function()
{
	this.hidePageEditor();
	this.hideTemplateDesigner();
}

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
}

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
	
	var applet1 = this.getApplet("webcomponents");
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
}

WebStudio.Applications.WebDesigner.prototype.hidePageEditor = function()
{
	if(this.pageEditor)
	{
		this.pageEditor.hideTabItems();
	}
}

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
		this.templateDesigner.show();
	}
}

WebStudio.Applications.WebDesigner.prototype.hideTemplateDesigner = function()
{
	if(this.templateDesigner)
	{
		this.templateDesigner.hide();
	}
}

WebStudio.Applications.WebDesigner.prototype.GoToTemplateDisplay = function(templateId)
{
	// show the "templates" applet
	this.showApplet("templates");

	// show the template designer
	this.showTemplateDesigner();
	
	// tell the "templates designer" about our selection
	this.templateDesigner.selectTemplate(templateId);
	
	// record the selected template
	this.selectedTemplate = templateId;		
}

/**
 * Fired when an item is dropped from a tree view onto the page editor
 */
WebStudio.Applications.WebDesigner.prototype.dropFromTreeView = function(dropDivId, options)
{
	this.dropOntoRegion(this.pageEditor.tabs[dropDivId],null,null,null,options);
}

WebStudio.Applications.WebDesigner.prototype.dropOntoRegion = function(regionTab, nodeData, source, e, options)
{
	// get the kind of thing that was dropped
	var alfType = options.data.alfType;

	// ACTION: they drop a file from the webapp slider
	if ("file" == alfType)
	{
		var path = options.data.path;
		var mimetype = options.data.mimetype;
				
		// FILE: image
		// RESULT: bind in an image component
		if (mimetype != null && mimetype.startsWith("image"))
		{
			var config = this.newConfigBinding();
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
		if (mimetype != null && mimetype == "text/xml")
		{		
			var config = this.newConfigBinding();
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
		if (mimetype != null && 
			(mimetype == "text/html") ||
			(mimetype == "text/shtml"))
		{
			var config = this.newConfigBinding();
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
		if (mimetype != null &&
		     (mimetype.startsWith("video"))
		   )
		{
			var config = this.newConfigBinding();
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
		if (mimetype != null &&
		     (mimetype.startsWith("audio"))
		   )
		{
			var config = this.newConfigBinding();
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
		var path = options.data.path;
		
		var config = this.newConfigBinding();
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
		var params = { };
		params["componentType"] = options.nodeId;
		this.bindToRegion(regionTab, params);
		return true;
	}
	
	
	// ACTION: they drop a web script component type
	// Special Handling for Web Scripts as components
	if ("webscriptComponent" == alfType)
	{
		var config = this.newConfigBinding();
		config["binding"]["componentType"] = "webscript";
		config["properties"]["title"] = "WebScript Component";
		config["properties"]["description"] = options.nodeId;
		config["properties"]["url"] = options.nodeId;;

		this.bindToRegionTab(regionTab, config);

		return true;	
	}


	// ACTION: they dropped a navigation node
	if ("navNode" == alfType)
	{
		var config = this.newConfigBinding();
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
		var cmType = options.data.cmType;
		var nodeRef = options.data.nodeRef;
		
		var nodeString = WebStudio.app.toNodeString(nodeRef);		
		var contentUrl = WebStudio.ws.repo("/api/node/" + nodeString + "/content");
		
		var mimetype = options.data.mimetype;
		if(mimetype == null)
		{
			// TODO: handle this
		}
		else
		{		
			// RESULT: bind in an image component
			if (mimetype != null && mimetype.startsWith("image"))
			{
				var config = this.newConfigBinding();
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
				var config = this.newConfigBinding();
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
			if (mimetype != null && mimetype.startsWith("video"))
			{
				var config = this.newConfigBinding();
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
			if (mimetype != null && mimetype.startsWith("audio"))
			{
				var config = this.newConfigBinding();
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
		var cmType = options.data.cmType;

		// RESULT: display the contents of the space in a list
		var nodeRef = options.data.nodeRef;
		var nodeString = WebStudio.app.toNodeString(nodeRef);
		
		var config = this.newConfigBinding();
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
}

WebStudio.Applications.WebDesigner.prototype.bindToRegionTab = function(regionTab, config)
{
	var regionId = regionTab.regionId;
	var regionScopeId = regionTab.regionScopeId;
	var regionSourceId = regionTab.regionSourceId;

	this.bindToRegion(regionId, regionScopeId, regionSourceId, config);
}

WebStudio.Applications.WebDesigner.prototype.bindToRegion = function(regionId, regionScopeId, regionSourceId, config)
{
	// update binding
	config["binding"]["regionId"] = regionId;
	config["binding"]["regionSourceId"] = regionSourceId;
	config["binding"]["regionScopeId"] = regionScopeId;
	
	// fire the event
	var params = { "json" : Json.toString(config) };
	var url = WebStudio.ws.studio("/incontext/components", params);
	
	var myAjax = new Ajax(url, {
		method: 'get',
		onComplete: (function(data){
			this.RefreshPageRegion(data);
		}).bind(this)
	}).request();
}

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
}

WebStudio.Applications.WebDesigner.prototype.faultRegion = function(regionId, regionScopeId)
{
 	var templateId = context.getCurrentTemplateId();
 	
	var url = WebStudio.url.studio("/region/" + regionId + "/" + regionScopeId + "/" + templateId);
	if(WebStudio.request.queryString != null && WebStudio.request.queryString.length > 0)
	{
		url = url + "&" + WebStudio.request.queryString;
	}
	var myAjax = new Ajax(url, {
		method: 'get',
		onComplete: (function(responseObject) {
			this.faultRegionSuccess(responseObject, regionId, regionScopeId);
		}).bind(this)
	}).request();	
}

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

			// blank out the div
			regionDiv.empty();
									
			// this is the dummyDiv approach to faulting the div			

			// create a dummyDiv
			var dummyDiv = document.createElement("div");
			
			// jump the html into the dummy div
			var parsedHtml = WebStudio.parser.parseHTML(html);
			var htmlToSet = parsedHtml.childNodes[0].innerHTML;		
			dummyDiv.setHTML(htmlToSet);
			
			// drop the dummy div into the body of the document
			dummyDiv.injectInside(document.body);

			// copy regionDiv attributes onto the dummyDiv
			for( var x = 0; x < regionDiv.attributes.length; x++ ) 
			{
				var attribute = regionDiv.attributes[x];
				var name = attribute.nodeName;
				var value = attribute.nodeValue;
				
				// don't copy style
				if(name != "style")
				{
					dummyDiv.setAttribute(name, value);
				}
			}
						
			// replace the region div with the dummy div
			regionDiv.replaceWith(dummyDiv);
			//debugger;
			//regionDiv = regionDiv.parentNode.replaceChild(regionDiv, dummyDiv);
						
			// process any tags
			var x = regionDiv.getElementsByTagName("script");
			for(var a = 0; a < x.length; a++)
			{
				eval(x[a].text);
			}
			
			if(restorePageEditor)
			{
				// TODO: Ideally, this should trigger from completion of tags and load of the DOM element
				// it should be tied to an update event of some kind
				
				// delay
				this.showPageEditor.delay(500, this);
			}			
			
		}
	}
}

WebStudio.Applications.WebDesigner.prototype.newConfigBinding = function()
{
	var config = { };
	config["operation"] = "bindComponent";
	config["binding"] = { };
	config["properties"] = { };
	config["resources"] = { };

	return config;
}

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
}

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
}

WebStudio.Applications.WebDesigner.prototype.resize = function()
{
	if(this.templateDesigner && !this.templateDesigner.isHidden())
	{
		this.templateDesigner.resize();
	}
}


