WebStudio.Applications.Abstract = new Class({
  initialize: function(id, title, description) {
	this.id = id;
	this.title = title;
	this.description = description;
	
	this.isInitialized = false;
	
	this.activeAppletId = null;
	
	this.applets = { };
  }
});

WebStudio.Applications.Abstract.prototype.getId = function()
{
	return this.id;
};

WebStudio.Applications.Abstract.prototype.getTitle = function()
{
	var title = this.title;
	
	if(!title)
	{
		title = this.getId();
	}
	
	return title;
};

WebStudio.Applications.Abstract.prototype.getDescription = function()
{
	var desc = this.description;
	
	if(!desc)
	{
		desc = this.getTitle();
	}
	
	return desc;
};

WebStudio.Applications.Abstract.prototype.toString = function()
{
	return this.id + "," + this.title + "," + this.description;
};

/**
 * Tells the web application to initialize
 * This informs the app to load any downstream dependencies
 */
WebStudio.Applications.Abstract.prototype.init = function(onInit)
{
	var _this = this;
	
	// load and initialize the applets
	this.bootstrapApplets({
	
		onSuccess: function() {
		
			// flag that initialization was successful
			_this.isInitialized = true;
			
			// call to onInit method
			if(onInit)
			{
				onInit.bind(_this).attempt();
			}			
			
		}
		,
		onFailure: function() {
		
			// TODO
			
		}
		
	});
};

/**
 * Loads and initializes the applets
 * options - object with methods "onSuccess" and "onFailure"
 */
WebStudio.Applications.Abstract.prototype.bootstrapApplets = function(options)
{
	var _this = this;
	
	var config = WebStudio.app.applicationsConfig[this.getId()].applets;
	
	var bootstrap = new WebStudio.Bootstrap(config, this.applets);
	bootstrap.onSuccess = function() 
	{	
		// set a pointer back to the application
		// mount sliders
		var count = 0;
		for(var id in config)
		{
			if(config.hasOwnProperty(id))
			{
				var obj = _this.applets[id];
				obj.app = _this;
				
				if(obj.mountSlider)
				{
					obj.mountSlider(_this.slidersSector.Sliders[count], _this.slidersSector.Sliders[count].Data.el);
				}
				
				count++;
			}
		}
		
		// mark success
		_this.appletsBootstrapped = true;
		
		if(options.onSuccess)
		{
			options.onSuccess.bind(_this).attempt();
		}		
				
	};
	bootstrap.onFailure = function() 
	{	
		_this.applicationsBootstrapped = false;
		
		if(options.onFailure)
		{
			options.onFailure.bind(_this).attempt();
		}	
		else
		{
			alert('failed to bootstrap');
		}					
	};
	bootstrap.load();
};

WebStudio.Applications.Abstract.prototype.getAppletIds = function()
{
	var config = WebStudio.app.applicationsConfig[this.getId()].applets;
	
	var idArray = [];
	
	for(var id in config)
	{
		if(config.hasOwnProperty(id))
		{
			idArray[idArray.length] = id;
		}
	}
	
	return idArray;
};

WebStudio.Applications.Abstract.prototype.getApplet = function(appletId)
{
	var applet = null;
	var _applets = this.applets;
	
	for(var key in _applets)
	{
		if(_applets.hasOwnProperty(key))
		{	
			if(key == appletId)
			{
				applet = this.applets[key];
				break;
			}
		}
	}
	
	return applet;
};

WebStudio.Applications.Abstract.prototype.showApplet = function(appletId)
{
	var index = -1;
	var count = 0;
	
	var _applets = this.applets;

	for(var key in _applets)
	{
		if(_applets.hasOwnProperty(key))
		{
			if(key == appletId)
			{
				index = count;
				break;
			}
			count++;
		}
	}
	
	if (this.slidersSector.SlidersData[index].state != 'show')
	{
		this.slidersSector.toggleSliderData(index);
	}
	
	this.setActiveAppletId(appletId);
};

WebStudio.Applications.Abstract.prototype.getMenu = function()
{
	// default behaviour
	if(!this.menu)
	{
		this.menu = this.getRootMenu();
	}
	
	return this.menu;
};

WebStudio.Applications.Abstract.prototype.getRootMenu = function()
{
	var _this = this;

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
	r1.addItem("separator-1","item-1","separator");
	r1.addItem("site-import", "Import...", "text", WebStudio.overlayIconsPath + "/export.gif", { disable: true} );
	r1.addItem("site-export", "Export...", "text", WebStudio.overlayIconsPath + "/import.gif", { disable: true} );
	r1.addItem("separator2","item2","separator");	
	r1.addItem("site-content-type-associations", "Content Associations...", "text", WebStudio.overlayIconsPath + "/content_type_associations.gif");
	r1.addItem("separator3","item2","separator");
	r1.addItem("site-view-web-project", "View in Explorer", "text", WebStudio.overlayIconsPath + "/dashboard.gif" );
	r1.addItem("site-view-web-project-webdav", "View in WebDAV", "text", WebStudio.overlayIconsPath + "/dashboard.gif" );
	r1.addItem("site-view-web-project-cifs", "View in CIFS", "text", WebStudio.overlayIconsPath + "/dashboard.gif", { disable: true } );
	r1.addItem("site-view-web-project-ftp", "View in FTP", "text", WebStudio.overlayIconsPath + "/dashboard.gif" );
		
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
	
	return menu;
};

WebStudio.Applications.Abstract.prototype.onMenuItemClick = function(index,data) 
{
	this.onRootMenuItemClick(index, data);
};

WebStudio.Applications.Abstract.prototype.onRootMenuItemClick = function(index,data) 
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
		WebStudio.app.openBrowser("alfresco", "http://www.alfresco.com");
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
		WebStudio.app.showContentTypeAssociationsDialog();
		//this.showContentTypeAssociationsDialog();
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

	// Site - View Sandbox
	if(index == 'site-view-sandbox')
	{
	}
	
	// Site - View Web Project (Alfresco Explorer)
	if(index == 'site-view-web-project')
	{
		url = "http://localhost:8080/alfresco/service/webframework/redirect/jsf-client/browse/webproject/" + WebStudio.context.getWebProjectId();
		WebStudio.app.openBrowser("alfresco", url);
	}	

	// Site - View Web Project (WebDAV)
	if(index == 'site-view-web-project-webdav')
	{
		url = "http://localhost:8080/alfresco/webdav/Web Projects/" + WebStudio.context.getWebProjectId();
		WebStudio.app.openBrowser("webdav", url);
	}	

	// Site - View Web Project (CIFS)
	if(index == 'site-view-web-project-cifs')
	{
	}	

	// Site - View Web Project (FTP)
	if(index == 'site-view-web-project-ftp')
	{
		url = "ftp://localhost/AVM/";
		url += WebStudio.context.getStoreId();
		url += "/HEAD/DATA/www/avm_webapps/";
		url += WebStudio.context.getWebappId();
		url += "/";
		WebStudio.app.openBrowser("ftp", url);
	}	
	
	// Page - Edit Template
	if (index == 'page-template-edit')
	{
		// jump to the current template
		//this.GoToTemplateDisplay(Surf.context.getCurrentTemplateId());
		WebStudio.app.GoToTemplateDisplay(Surf.context.getCurrentTemplateId());
	}
	
	// Page - Template Associations
	if (index == 'page-template-associations-view')
	{
		WebStudio.app.showTemplateAssociationsDialog();
		//this.showTemplateAssociationsDialog();		
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
};


WebStudio.Applications.Abstract.prototype.getTabTitle = function()
{
	// ABSTRACT
	return null;
};

WebStudio.Applications.Abstract.prototype.getTabImageUrl = function()
{
	// ABSTRACT
	return null;
};

WebStudio.Applications.Abstract.prototype.getSlidersSectorTemplateId = function()
{
	// ABSTRACT
	return null;
};

WebStudio.Applications.Abstract.prototype.getSlidersPanelDomId = function()
{
	// ABSTRACT
	return null;
};

/*
 * EVENT HANDLERS
 */
WebStudio.Applications.Abstract.prototype.onResizeWindow = function()
{
};

/*
WebStudio.Applications.Abstract.prototype.onContentScroll = function(left, top)
{
};
*/

WebStudio.Applications.Abstract.prototype.onSelected = function()
{
};

WebStudio.Applications.Abstract.prototype.onUnselected = function()
{
};

/*
WebStudio.Applications.Abstract.prototype.onPanelsResize = function()
{
};
*/

WebStudio.Applications.Abstract.prototype.onEndEdit = function()
{
	// close all applets
	var appletIds = this.getAppletIds();
	for(var i = 0; i < appletIds.length; i++)
	{
		var appletId = appletIds[i];
		var applet = this.getApplet(appletId);
		
		applet.onClose();
	}
};

WebStudio.Applications.Abstract.prototype.onStartEdit = function()
{
};

WebStudio.Applications.Abstract.prototype.onSlidersPanelHide = function()
{
};

WebStudio.Applications.Abstract.prototype.onSlidersPanelShow = function()
{
};

WebStudio.Applications.Abstract.prototype.setActiveAppletId = function(appletId)
{
	this.activeAppletId = appletId;
};

WebStudio.Applications.Abstract.prototype.getActiveAppletId = function()
{
	return this.activeAppletId;
};

WebStudio.Applications.Abstract.prototype.isEditMode = function()
{
	return WebStudio.app.isEditMode();
};


// general methods for working with designers

WebStudio.Applications.Abstract.prototype.addDesigner = function(id, instance)
{
	if (!this.designers)
	{
		this.designers = { };
	}
	
	this.designers[id] = instance;
};

WebStudio.Applications.Abstract.prototype.removeDesigner = function(id)
{
	if (!this.designers)
	{
		this.designers = { };
	}
	
	delete this.designers[id];
};

WebStudio.Applications.Abstract.prototype.getDesigner = function(id)
{
	if (!this.designers)
	{
		this.designers = { };
	}

	return this.designers[id];
};

WebStudio.Applications.Abstract.prototype.hideAllDesigners = function()
{
	if (this.designers)
	{
		for(var id in this.designers)
		{
			if(this.designers.hasOwnProperty(id))
			{
				var designer = this.designers[id];
				if (!designer.isHidden())
				{
					designer.hide();
				}
			}
		}	
	}
};

WebStudio.Applications.Abstract.prototype.showAllDesigners = function()
{
	if (this.designers)
	{
		for(var id in this.designers)
		{
			if(this.designers.hasOwnProperty(id))
			{
				var designer = this.designers[id];
				if (designer.isHidden())
				{
					designer.show();
				}
			}
		}	
	}
};


WebStudio.Applications.Abstract.prototype.showPageBlocker = function()
{
	var pageBlocker = this.getPageBlocker();
	if (!pageBlocker)
	{
		pageBlocker = new WebStudio.PageBlocker('PageBlocker');
		pageBlocker.injectObject = $('AlfSplitterPanel2');
		pageBlocker.activate();
		this.addDesigner("PageBlocker", pageBlocker);
	}
	
	pageBlocker.show();
};

WebStudio.Applications.Abstract.prototype.hidePageBlocker = function()
{
	var pageBlocker = this.getPageBlocker();
	if (pageBlocker)
	{
		pageBlocker.hide();
	}

	this.checkAllHidden();		
};

WebStudio.Applications.Abstract.prototype.checkAllHidden = function()
{
	// if all panels are hidden, then show the overflow
	var allHidden = true;
	if (this.designers)
	{
		for(var id in this.designers)
		{
			if(this.designers.hasOwnProperty(id))
			{
				var designer = this.designers[id];
				if (designer && !designer.isHidden())
				{
					allHidden = false;
				}
			}
		}	
	}
	
	if (allHidden)
	{
		$('AlfSplitterPanel2').setStyle('overflow', 'auto');
	}
};

WebStudio.Applications.Abstract.prototype.getPageBlocker = function()
{
	return this.getDesigner("PageBlocker");
};

WebStudio.Applications.Abstract.prototype.resize = function()
{
	if (this.designers)
	{
		for(var id in this.designers)
		{
			if(this.designers.hasOwnProperty(id))
			{
				var designer = this.designers[id];
				if (designer && !designer.isHidden())
				{
					designer.resize();
				}
			}
		}	
	}
};

WebStudio.Applications.Abstract.prototype.onContentScroll = function(left, top)
{
	if (this.designers)
	{
		for(var id in this.designers)
		{
			if(this.designers.hasOwnProperty(id))
			{
				var designer = this.designers[id];
				if (designer && !designer.isHidden())
				{
					designer.onScroll(left, top);
				}
			}
		}	
	}
};

WebStudio.Applications.Abstract.prototype.onPanelsResize = function()
{
	this.onDesignersRefresh();
};

WebStudio.Applications.Abstract.prototype.onDesignersRefresh = function()
{
	if (this.designers)
	{
		for(var id in this.designers)
		{
			if(this.designers.hasOwnProperty(id))
			{
				var designer = this.designers[id];
				if (designer && !designer.isHidden())
				{
					designer.resize();
				}
			}
		}	
	}
};

/**
 PAGE EDITOR
**/
WebStudio.Applications.Abstract.prototype.getPageEditor = function()
{
	return this.getDesigner("PageEditor");
};

WebStudio.Applications.Abstract.prototype.showPageEditor = function()
{
	var pageEditor = this.getPageEditor();
	if (!pageEditor)
	{
		pageEditor = new WebStudio.PageEditor();
		pageEditor.application = this;
		pageEditor.activate();
		this.addDesigner("PageEditor", pageEditor);
		
		this.setupPageEditor();
	}
	
	// show the page editor
	pageEditor.show();
	
	// hide overflow for the panel
	$('AlfSplitterPanel2').setStyle('overflow', 'hidden');	
};

WebStudio.Applications.Abstract.prototype.hidePageEditor = function()
{
	var pageEditor = this.getPageEditor();
	if (pageEditor)
	{
		pageEditor.restoreAllTabItems();
	}

	this.checkAllHidden();		
};

WebStudio.Applications.Abstract.prototype.removePageEditor = function()
{
	var pageEditor = this.getPageEditor();
	if (pageEditor)
	{
		pageEditor.hideTabItems();
		pageEditor.removeTabItems();
		this.removeDesigner(pageEditor);
	}
	
	this.checkAllHidden();
};


WebStudio.Applications.Abstract.prototype.setupPageEditor = function()
{
	var pageEditor = this.getPageEditor();
	
	// Remove any existing overlays
	pageEditor.removeTabItems();
	
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
			pageEditor.addTabItem(el);
		}		
	}
	
	// get the selected applet
	var selectedAppletId = this.getActiveAppletId();
	var applet = this.getApplet(selectedAppletId);
	if(applet)
	{
		if(applet.bindPageEditor)
		{
			applet.bindPageEditor(pageEditor);
		}
	}	
};
