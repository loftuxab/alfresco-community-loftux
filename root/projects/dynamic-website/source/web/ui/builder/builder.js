

// Page Scope Colors
var BUILDER_PAGE_SCOPE_OVERLAY_COLOR1_HIGH = "#6666AA";
var BUILDER_PAGE_SCOPE_OVERLAY_COLOR1 = "#000022";
var BUILDER_PAGE_SCOPE_OVERLAY_COLOR1_LOW = "#555599";

// Template Scope Colors
var BUILDER_TEMPLATE_SCOPE_OVERLAY_COLOR1_HIGH = "#AA6666";
var BUILDER_TEMPLATE_SCOPE_OVERLAY_COLOR1 = "#220000";
var BUILDER_TEMPLATE_SCOPE_OVERLAY_COLOR1_LOW = "#995555";

// Site Scope Colors
var BUILDER_SITE_SCOPE_OVERLAY_COLOR1_HIGH = "#66AA66";
var BUILDER_SITE_SCOPE_OVERLAY_COLOR1 = "#002200";
var BUILDER_SITE_SCOPE_OVERLAY_COLOR1_LOW = "#559955";


var buttonHandler = applicationButtonHandler;




// DRAG AND DROP CAPABILITIES
Ext.override(Ext.dd.DropZone, {
	onNodeDrop: function(nodeData, source, e, data ) {
	
		// are they dropping onto a region?
		if(this.receiverType == "region")
		{
			return dropOntoRegion(this, nodeData, source, e, data);			
		}
	}
});

function doWindowReload()
{
	window.location.reload(true);
}

function onWindowResize()
{
	// repaint all (toolbar, editors, windows)
	repaintInContext(true, true, true, true);
}

function onTextResize()
{
	// repaint all (toolbar, editors, windows)
	repaintInContext(true, true, true, true);
}

function showViewportRegion(regionId, animate)
{
	var viewport = Ext.ComponentMgr.get("mainViewport");
	if(viewport != null)
	{
		var component = viewport.getComponent(regionId+"Panel");
		if(animate)
		{
			var div = Ext.get(regionId+"Container");
			if(div != null)
			{
				// TODO: Figure this out
				/*
				div.fadeIn({ endOpacity: 0.75, duration: 0.3 });
				div.slideIn('t', { duration: 2 } );
				var delayedTask = new Ext.util.DelayedTask(function() { component.show(); viewport.doLayout(); }, null, null);
				delayedTask.delay(300);
				*/
			}
		}
		
		component.show();
		viewport.doLayout();
	}
}


var dockingPanelEnabled = true;

function isDockingPanelEnabled()
{
	return dockingPanelEnabled;
}

function toggleDockingPanel()
{
	dockingPanelEnabled = !dockingPanelEnabled;
	if(dockingPanelEnabled)
		showDockContainer();
	else
		hideDockContainer();
}

function hideViewportRegion(regionId, animate)
{
	var viewport = Ext.ComponentMgr.get("mainViewport");
	if(viewport != null)
	{
		var component = viewport.getComponent(regionId+"Panel");
		if(animate)
		{
			var div = Ext.get(regionId+"Container");
			if(div != null)
			{
				// TODO: Figure this out
				/*
				div.fadeOut({ endOpacity: .25, duration: .3, remove: false, useDisplay: true });				
				div.slideOut('t', { duration: 2 } );
				var delayedTask = new Ext.util.DelayedTask(function() { component.hide(); viewport.doLayout(); }, null, null);
				delayedTask.delay(300);
				*/
			}
		}

		component.hide();
		viewport.doLayout();
	}
}

function hideInContextViewport(animate)
{
	hideViewportRegion("dock", animate);
	hideViewportRegion("toolbar", animate);
}

function showInContextViewport(animate)
{
	// prepare the dock with panels
	if(!didInitViewport)
	{
		// initialize any incontext panels/windows
		initializeInContextElements();

		Ext.ComponentMgr.get("dockPanel").insert(1, Ext.ComponentMgr.get("web_content_window_panel"));
		Ext.ComponentMgr.get("dockPanel").insert(2, Ext.ComponentMgr.get("search_window_panel"));
		Ext.ComponentMgr.get("dockPanel").insert(3, Ext.ComponentMgr.get("navigation_window_panel"));
		Ext.ComponentMgr.get("dockPanel").insert(4, Ext.ComponentMgr.get("web_components_window_panel"));
		Ext.ComponentMgr.get("dockPanel").insert(5, Ext.ComponentMgr.get("spaces_window_panel"));
	}
	
	// show
	if(isDockingPanelEnabled())
		showViewportRegion("dock", animate);
	showViewportRegion("toolbar", animate);
}
var didInitViewport = false;

function initializeInContextElements()
{
	// walk through all of the incontext stuff
	var ids = getInContextElementIds();
	for(var x = 0; x < ids.length; x++)
	{
		var theType = getInContextElementType(ids[x]);
		if(theType == "window")
			initializeWindow(ids[x]);
			
		if(theType == "editor")
			initializeEditor(ids[x]);
	}
}

function initializeViewport()
{
	// body container
	var bodyContainer = new Ext.Element(document.createElement("div"));
	bodyContainer.dom.id = "bodyContainer";
	bodyContainer.appendTo(Ext.getBody());
	
	// dock container
	var dockContainer = new Ext.Element(document.createElement("div"));
	dockContainer.dom.id = "dockContainer";
	dockContainer.appendTo(Ext.getBody());
	
	// toolbar container
	var toolbarContainer = new Ext.Element(document.createElement("div"));
	toolbarContainer.dom.id = "toolbarContainer";
	toolbarContainer.appendTo(Ext.getBody());
	
	
	//
	// copy all of the body dom children into the new body container
	// walk backwards since document.body.childNodes is a real-time array
	// push things over into body container
	var nodes = document.body.childNodes;
	var len = nodes.length;
	for(var i = len-1; i >= 0; i--)
	{
		var add = true;
		
		var id = nodes[i].id;
		if(id == "bodyContainer" || id == "dockContainer" || id == "toolbarContainer" || id == "divFloatingMenu")
			add = false;

		if(add)
		{
			//bodyContainer.dom.appendChild(nodes[i]);			
			bodyContainer.dom.insertBefore(nodes[i], bodyContainer.dom.firstChild);
		}
	}	
	

	// now snap the viewport into place
	
	//top, right, bottom, left	
	// initialize the viewport
	var viewport = new Ext.Viewport({
		id: 'mainViewport',
		layout:'border',
		items:[
			{
				region: 'north',
				contentEl: 'toolbarContainer',
				id: 'toolbarPanel',
				margins: '0 0 0 0',
				split: false,
				height: 26,
				minHeight: 26,
				maxHeight: 26,
				
				xtype: 'toolbar'
			}
			,
			{
				region: 'west',
				contentEl: 'dockContainer',
				id: 'dockPanel',
				margins: '0 0 0 0',
				split: true,
				width: 200,
				title: 'Site Builder',
				header: true,
				border: false,
				layout: 'accordion',
				layoutConfig: {
					titleCollapse: false,
					animate: true,
					activeOnTop: true
				}			
			}
			,
			{
				region: 'center',
				contentEl: 'bodyContainer',
				id: 'bodyPanel',
				margins: '0 0 0 0',
				split: true,
				autoScroll: true
			}
		]
	});

	// TODO: We need to register a handler to capture clicks and slides on the body
	//Ext.ComponentMgr.get("bodyPanel").on("afterlayout", bodyPanelScrollHandler);
	
	Ext.ComponentMgr.get("bodyPanel").body.on('scroll', bodyPanelScrollHandler);
		
	return viewport;
}

function inPreviewMode()
{
	return true;
}

function adwInitialize()
{	
	// initialize any dynamic layout
	if(initializeDynamicLayout != null)
		initializeDynamicLayout();
		
	// if we're in in-context mode
	if(inPreviewMode())
	{
		// initialize viewport container regions
		var viewport = initializeViewport();

		// set up the toolbar buttons
		initializeMainToolbarButtons();
				
		// repaint any and all incontext elements	
		repaintInContext(true, true, true, true);  // repaint all (toolbar, editors, windows, docks)	

		// register listeners for the body panel
		Ext.ComponentMgr.get("bodyPanel").on("resize", bodyPanelResizeHandler);
	}

	// signal that the framework has initialized
	initComplete = true;
}



// bootstrap function
var initComplete = false;
function isFrameworkInitialized()
{
	return initComplete;
}

Ext.onReady(function() {

	Ext.QuickTips.init();
	Ext.EventManager.onWindowResize(onWindowResize);
	Ext.EventManager.onTextResize(onTextResize);	
	
	//Ext.EventManager.onDocumentReady(onDocumentReady, this, true);
	adwInitialize();
	
});

var initializeDynamicLayout = null;
var redrawDynamicLayout = null;

var bodyBox = null;
function bodyPanelResizeHandler()
{
	if(isFrameworkInitialized())
	{
		var repaint = false;
		if(bodyBox == null)
			repaint = true;
		else
		{
			var box = Ext.ComponentMgr.get("bodyPanel").getBox();
			if(box["x"] != bodyBox["x"])
				repaint = true;
			if(box["y"] != bodyBox["y"])
				repaint = true;
			if(box["width"] != bodyBox["width"])
				repaint = true;
			if(box["height"] != bodyBox["height"])
				repaint = true;
		}
		
		if(repaint == true)
		{
			repaintInContext(true, true, true, true);
		}
			
		bodyBox = Ext.ComponentMgr.get("bodyPanel").getBox();
	}
}

function bodyPanelScrollHandler()
{
	if(isFrameworkInitialized())
	{
		///repaintInContext(true, true, true, true);
		repaintInContext(false, true, false, false);
	}
}

function hideDockContainer()
{
	hideViewportRegion("dock", true);
}

function showDockContainer()
{
	showViewportRegion("dock", true);
}

function hideToolbarContainer()
{
	hideViewportRegion("toolbar", true);
}

function showToolbarContainer()
{
	showViewportRegion("toolbar", true);
}

function setMainToolbarMode(selectedEditor)
{
	var toolbar = Ext.ComponentMgr.get("toolbarPanel");

	// hide all of the menus
	Ext.ComponentMgr.get("maintoolbar-site-menu").hide();
	Ext.ComponentMgr.get("maintoolbar-content-menu").hide();
	Ext.ComponentMgr.get("maintoolbar-workflow-menu1").hide();
	Ext.ComponentMgr.get("maintoolbar-page-menu").hide();	
	Ext.ComponentMgr.get("maintoolbar-workflow-menu2").hide();
	Ext.ComponentMgr.get("maintoolbar-layout-menu").hide();
	Ext.ComponentMgr.get("maintoolbar-preferences-menu").hide();
	
	// menus for "Content Editing Mode"
	if("content_editor" == selectedEditor)
	{
		Ext.ComponentMgr.get("maintoolbar-site-menu").show();
		Ext.ComponentMgr.get("maintoolbar-content-menu").show();
		Ext.ComponentMgr.get("maintoolbar-workflow-menu1").show();
	}
	
	// menus for "Page Editing Mode"
	if("page_editor" == selectedEditor)
	{		
		Ext.ComponentMgr.get("maintoolbar-site-menu").show();
		Ext.ComponentMgr.get("maintoolbar-page-menu").show();
		Ext.ComponentMgr.get("maintoolbar-workflow-menu2").show();
	}
	
	// menus for "Layout Editing Mode"
	if("layout_editor" == selectedEditor)
	{
		Ext.ComponentMgr.get("maintoolbar-site-menu").show();
		Ext.ComponentMgr.get("maintoolbar-layout-menu").show();
	}

	// preferences menu
	Ext.ComponentMgr.get("maintoolbar-preferences-menu").show();
	
	// push the combo box into the right state
	Ext.ComponentMgr.get("maintoolbar-mode-selector").setValue(selectedEditor);
}

function initializeMainToolbarButtons()
{
	var alfrescoMenu = new Ext.menu.Menu({
		id: "alfrescoMenu",
		items: [
			{
				id: 'view_sandbox',
				text: 'View Sandbox',
				iconCls: 'maintoolbar-icon-view-sandbox',
				handler: buttonHandler
			}
			,
			{
				id: 'view_dashboard',
				text: 'My Dashboard',
				iconCls: 'maintoolbar-icon-view-dashboard',
				handler: buttonHandler
			}
			,
			'-'
			,
			{
				id: 'getting_started',
				text: 'Getting Started',
				iconCls: 'maintoolbar-icon-getting-started',
				handler: buttonHandler
			}
			,
			{
				id: 'watch_tutorials',
				text: 'Watch Tutorials',
				iconCls: 'maintoolbar-icon-watch-tutorials',
				handler: buttonHandler
			}
			,
			{
				id: 'learn_about_alfresco',
				text: 'Visit Alfresco',
				iconCls: 'maintoolbar-icon-learn-about-alfresco',
				handler: buttonHandler
			}			
		       ]
	});	

	var contentMenu = new Ext.menu.Menu({
		id: "contentMenu"
	});	
	var forms = getFormNames();
	for(var u = 0; u < forms.length; u++)
	{
		var formName = forms[u];
		var formTitle = getFormTitle(formName);
		var item = contentMenu.add({
				id: '_alfcm_' + formName,
				text: 'Add New ' + formTitle,
				iconCls: 'maintoolbar-icon-add-web-content',
				handler: buttonHandler
		});
	}
	Ext.ComponentMgr.register(contentMenu);


	

	// workflow menu for content editors
	var workflowMenu1 = new Ext.menu.Menu({
		id: "workflowMenu1"
	});
	workflowMenu1.add({
				id: '_alfcm_workflow1',
				text: 'Request a new Article',
				iconCls: 'maintoolbar-icon-start-workflow',
				handler: buttonHandler
	});
	workflowMenu1.add({
				id: '_alfcm_workflow2',
				text: 'Request a new Press Release',
				iconCls: 'maintoolbar-icon-start-workflow',
				handler: buttonHandler
	});
	workflowMenu1.add('-');
	workflowMenu1.add({
				id: '_alfcm_workflow3',
				text: 'Flag content for revision',
				iconCls: 'maintoolbar-icon-start-workflow',
				handler: buttonHandler
	});
	Ext.ComponentMgr.register(workflowMenu1);





	
	





	var siteMenu = new Ext.menu.Menu({
		id: "siteMenu",
		items: [
			{
				id: 'configure_web_site',
				text: 'Configure Web Site',
				iconCls: 'maintoolbar-icon-configure-web-site',
				handler: buttonHandler
			}
			,
			'-'
			,
			{
				id: 'configure_endpoints',
				text: 'Configure Endpoints...',
				iconCls: 'maintoolbar-icon-configure-endpoints',
				handler: buttonHandler
			}
			,
			'-'
			,
			{ 
				id: 'manage_templates',
				text: 'Manage Site Templates...',
				iconCls: 'maintoolbar-icon-manage-site-templates',
				handler: buttonHandler
			}
			,
			{ 
				id: 'manage_content_presentation',
				text: 'Manage Content Presentation...',
				iconCls: 'maintoolbar-icon-manage-content-presentation',
				handler: buttonHandler
			}				
			,			
			{ 
				id: 'manage_layouts',
				text: 'Manage Site Layouts...',
				iconCls: 'maintoolbar-icon-manage-site-layouts',
				handler: buttonHandler,
				disabled: true
			}
		       ]
	});
	Ext.ComponentMgr.register(siteMenu);
	
	
	
	

	var pageMenu = new Ext.menu.Menu({
		id: "pageMenu",
		items: [
			{ 
				id: 'copy_nav_node',
				text: 'Copy Page',
				iconCls: 'maintoolbar-icon-copy-page',
				handler: buttonHandler,
				disabled: true
			}
			,
			{ 
				id: 'paste_nav_node',
				text: 'Paste Page',
				iconCls: 'maintoolbar-icon-paste-page',
				handler: buttonHandler,
				disabled: true
			}
			,
			'-'
			,
			{
				id: 'associate_templates',
				text: 'Template Associations...',
				iconCls: 'maintoolbar-icon-associate-templates',
				handler: buttonHandler
			}
			,
			{
				id: 'open_template',
				text: 'Open Template',
				iconCls: 'maintoolbar-icon-open-template',
				handler: buttonHandler
			}
		       ]
	});
	Ext.ComponentMgr.register(pageMenu);
	
	
	// workflow menu for page editors
	var workflowMenu2 = new Ext.menu.Menu({
		id: "workflowMenu2"
	});	

	workflowMenu2.add({
				id: '_alfcm_workflow2_1',
				text: 'Publish this page',
				iconCls: 'maintoolbar-icon-start-workflow',
				handler: buttonHandler
	});
	workflowMenu2.add({
				id: '_alfcm_workflow2_2',
				text: 'Request changes to this Page',
				iconCls: 'maintoolbar-icon-start-workflow',
				handler: buttonHandler
	});
	Ext.ComponentMgr.register(workflowMenu2);



	




	var layoutMenu = new Ext.menu.Menu({
		id: "layoutMenu",
		items: [
			{ 
				id: 'add_new_region',
				text: 'Add New Region',
				iconCls: 'maintoolbar-icon-add-new-region',
				handler: buttonHandler,
				disabled: true
			}
			,
			{ 
				id: 'clone_layout',
				text: 'Clone Layout',
				iconCls: 'maintoolbar-icon-clone-layout',
				handler: buttonHandler,
				disabled: true
			}
			,
			{
				id: 'save_layout',
				text: 'Save Layout',
				iconCls: 'maintoolbar-icon-save-layout',
				handler: buttonHandler
			}
		       ]
	});	
	Ext.ComponentMgr.register(layoutMenu);




        var toggleDockingPanelItem = new Ext.menu.CheckItem({
			    id: 'toggle_docking_panel',
		            text: 'Show Docking Panel',
		            checkHandler: buttonHandler,
		            checked: dockingPanelEnabled
        		});
	Ext.ComponentMgr.register(toggleDockingPanelItem);





	var optionsMenu = new Ext.menu.Menu({
		id: "optionsMenu",
		items: [
			{
				id: 'refresh_cache',
				text: 'Refresh Cache',
				iconCls: 'maintoolbar-icon-refresh-cache',
				handler: buttonHandler
			}
			,
			'-'
			,
			toggleDockingPanelItem
			,
			'-'
		       ]
	});	
	var themes = getThemeIds();
	for(var uu = 0; uu < themes.length; uu++)
	{
		var themeId = themes[uu];
		var themeName = getThemeName(themeId);
		var item = optionsMenu.add({
				id: 'theme-' + themeId,
				themeId: themeId,
				text: themeName,
				iconCls: 'maintoolbar-icon-select-theme',
				handler: buttonHandler
		});
	}
	Ext.ComponentMgr.register(optionsMenu);













	// begin to configure the toolbar
	var toolbar = Ext.ComponentMgr.get("toolbarPanel");
	
	// add the buttons
	Ext.ComponentMgr.register(
		toolbar.addButton({ id: 'maintoolbar-icon-left', iconCls: 'maintoolbar-icon-left', menu: alfrescoMenu })
	);
	Ext.ComponentMgr.register(
		toolbar.addButton({ id: 'maintoolbar-site-menu', text: "Site Configuration", menu: siteMenu })
	);
	Ext.ComponentMgr.register(
		toolbar.addButton({ id: 'maintoolbar-content-menu', text: "Content Editor", menu: contentMenu })
	);
	Ext.ComponentMgr.register(
		toolbar.addButton({ id: 'maintoolbar-workflow-menu1', text: "Workflow", menu: workflowMenu1 })
	);
	Ext.ComponentMgr.register(
		toolbar.addButton({ id: 'maintoolbar-page-menu', text: "Page Editor", menu: pageMenu })
	);
	Ext.ComponentMgr.register(
		toolbar.addButton({ id: 'maintoolbar-workflow-menu2', text: "Workflow", menu: workflowMenu2 })
	);
	Ext.ComponentMgr.register(
		toolbar.addButton({ id: 'maintoolbar-layout-menu', text: "Layout Editor", menu: layoutMenu })
	);
	Ext.ComponentMgr.register(
		toolbar.addButton({ id: 'maintoolbar-preferences-menu', text: "Preferences", menu: optionsMenu })
	);
	
	toolbar.addFill();
	toolbar.add('Editing Mode:    ');
		
	// add a combo drop down (themes)
        var incontextModeData = [
            ['off','Off'],
            ['content_editor',getInContextElementName('content_editor')],
            ['page_editor',getInContextElementName('page_editor')],
            ['layout_editor',getInContextElementName('layout_editor')]
        ];
       	var incontextModeStore = new Ext.data.SimpleStore({
		fields: ['id', 'title'],
		data: incontextModeData
	});	
	var incontextModeSelector = new Ext.form.ComboBox({
		id: 'maintoolbar-mode-selector',
		store: incontextModeStore,
		displayField: 'title',
		valueField: 'id',
		mode: 'local',
		typeAhead: false,
		editable: false
	});	
	Ext.ComponentMgr.register(incontextModeSelector);
	incontextModeSelector.on("beforequery", function(queryEvent) { queryEvent.query = ""; queryEvent.forceAll = true; });
	incontextModeSelector.on("select", function(combo, record, index) 
	{ 
		var newEditorId = record.data["id"];		
		if("off" != newEditorId)
		{
			doSelectInContextEditor(newEditorId, mainToolbarSelectedEditor);
		
			// ensure a lazy init of this editor
			initializeEditor(newEditorId);
		
			// repaint the toolbar and editors
			repaintInContext(true, true, false, false);
		}
		else
			toggleInContextMode();
	});
	toolbar.add(incontextModeSelector);
}






















/****************************************
 **  DIALOG TOOLBAR MENU HANDLER
 ****************************************/
// QUERTY
/*
function dialogToolbarClickHandler(item, e) 
{
}
*/




/****************************************
 **  TOOLBAR MENU HANDLER
 ****************************************/
// QUERTY 
function buttonHandler(item, e) 
{
}
























/****************************************
 **  DELAYED TASKS
 ****************************************/

var delayedTasks = new Object();
function addDelayedTask(id, delayedTask)
{
	delayedTasks[id] = delayedTask;
}
function clearDelayedTask(id)
{
	if(delayedTasks[id] != null)
		delayedTasks[id].cancel();
	delayedTasks[id] = null;
}
function getDelayedTask(id)
{
	return delayedTasks[id];
}



















function refreshCache()
{
	Ext.Ajax.request({
		url: "/cache/?command=invalidateAll",
		method: 'GET',
		scriptTag: true
	});
}







































function renderIntoIFrame(url, divId)
{
	var panel = new Ext.ux.ManagedIframePanel({
		renderTo: divId,
		layout: 'fit'
	});
	panel.show();
	panel.setHeight(400);

	
	// kick off an update
	panel.getFrame().setSrc(url, false, iframeHandler);
}

function iframeHandler(frame)
{
	var frameWindow = frame.getWindow();
	var div = Ext.get(frame.dom.id);
	//div.autoHeight();
	//frame.getEl().autoHeight();
	
	//var doc = this.getDocument();
	//alert(doc.body.scrollHeight);
	
	//doc.defaultView.innerWidth, innerHeight, outerWidth, outerHeight
	//panel.setHeight(500);
}



function renderURL(url, divId)
{
	var remoteURL = getLocalProtocolHostPort() + "/proxy?endpoint=" + url;
	directRenderURL(remoteURL, divId);
}


function directRenderURL(remoteURL, divId, cbFunction)
{
	var cb = cbFunction;
	if(cb == null)
		cb = directRenderURLSuccess;
		
	var divEl = Ext.get(divId);
	if(divEl != null)
	{	
		// load webscript into div
		try {
			var updater = divEl.getUpdater();
			updater.update({
				url: remoteURL,
				text: "Loading...",
				callback: cb
			});			
		}
		catch(err) {
		}
	}
}

function directRenderURLSuccess(el, success, response, options)
{
	// NOTE: We do this in a delayed task due to timing of the DOM updates
	var delayedTask = new Ext.util.DelayedTask(function() {
		
		repaintInContext(false, true, false, false);

	}, null, null);
	delayedTask.delay(400);
}






// TODO: rework window toggling
// dock vs floating window
/*
function toggleWindow(windowId)
{
	doToggleInContextEnabled(windowId);
	
	// if the window is now active, bring it to the 'front'
	if(getInContextElementEnabled(windowId))
	{
		// Ensure Window is initialized
		initializeWindow(windowId);
		
		// Bring the window to the foreground
		//Ext.WindowMgr.bringToFront(windowId);
	}
	else
	{
		// store away session state for the next time they open it up
		doPersistElementSessionState(windowId);
	}	
	
	// repaint the windows
	//repaintInContext(false, false, true, false);
}
*/

// turns an editor on and off
function toggleEditor(editorId)
{
	doToggleInContextEnabled(editorId);
	doPersistElementSessionState(editorId);
	
	// if the editor is now active...
	if(getInContextElementEnabled(editorId))
	{
		// Ensure Editor is initialized
		initializeEditor(editorId);		
	}
		
	// repaint the toolbar and the editors
	repaintInContext(true, true, false, false);
}

function toggleInContextMode()
{
	var dockedX = 0;
	if(isDockingPanelEnabled())
	{
		var dockPanel = Ext.ComponentMgr.get("dockPanel");
		dockedX = dockPanel.getSize()["width"];
		if(dockedX == 0)
			dockedX = dockPanel.lastSize.width + 6;
	}
		
	if(isInContextEnabled())
	{
		// we are about to turn off in-context mode

		// turn off the viewport
		hideInContextViewport(true);
						
		// reset page position
		var bc = Ext.get("bodyContainer");
		bc.setLocation(dockedX,26);
		
		// toggle the editor
		toggleEditor("incontext");		

		// slide the body into place
		bc.setLocation(1, 0, true);
	}
	else
	{
		// we are about to turn on in-context mode
		
		// slide the body into place
		var bc = Ext.get("bodyContainer");
		bc.shift({
			x: dockedX, 
			y: 26, 
			duration: 0.1, 
			callback: function() 
			{
				showInContextViewport(false);
				bc.setLocation(dockedX,26);
				toggleEditor("incontext"); 
			}
		});
	}

	
}










///
// drop zone stuff for region
///

function dropOntoRegion(region, nodeData, source, e, data)
{
	var retVal = false;
	
	// dropped from the tree
	if (data.node instanceof Ext.tree.TreeNode)
	{
		// something was dropped here (and it was a tree node)
		var alfType = data.node.attributes["alfType"];  // file, directory, component, componentType, etc
		var alfFileType = data.node.attributes["alfFileType"];
		var alfFileExtension = data.node.attributes["alfFileExtension"];
		
		// they dropped a file
		if("file" == alfType)
		{
			var relativePath = getRelativePathForTreeNode(data.node);

			// they dropped an image
			if("image" == alfFileType)
			{
				// create image component params
				var params = { };
				params["componentType"] = "ct-imageComponent";
				params["_imageLocation"] = relativePath;
				
				// bounce component into place
				plugComponentIntoRegion(region, params);
				
				retVal = true;
			}

			// they dropped an xml form
			if("xform" == alfFileType)
			{
				// TODO
				retVal = true;
			}

			// they dropped an html document
			if("html" == alfFileType)
			{
				// create a markup component params
				var params = { };
				params["componentType"] = "ct-itemComponent";
				params["_itemType"] = "specific";
				params["_itemPath"] = relativePath;
				params["_endpointId"] = "alfresco-webuser";
				params["_howToRender"] = "direct";
				params["_renderData"] = "";
				
				// bounce component into place
				plugComponentIntoRegion(region, params);
				
				retVal = true;
			}
			
			// they dropped a video
			if("video" == alfFileType || "audio" == alfFileType)
			{
				// create media component params
				var params = { };
				params["componentType"] = "ct-mediaComponent";
				params["_mediaType"] = alfFileType;
				params["_url"] = relativePath;
				
				// bounce component into place
				plugComponentIntoRegion(region, params);
				
				retVal = true;
			}


			
		}
		
		// they dropped a directory
		if("directory" == alfType)
		{
			// TODO: What does it mean to drop a directory into a region?
			var relativePath = getRelativePathForTreeNode(data.node);
			retVal = true;
		}
		
		
		
		
		// they dropped a component type
		if("componentType" == alfType)
		{
			var componentTypeId = data.node.attributes["nodeId"];

			// bounce component into place
			var params = { };
			params["componentType"] = componentTypeId;
			plugComponentIntoRegion(region, params);

			retVal = true;
		}

		// they dropped a webscript component
		if("webscriptComponent" == alfType)
		{
			var uri = data.node.attributes["uri"];
			
			// create a webscript component params
			var params = { };
			params["componentType"] = "ct-webscriptComponent";
			params["_uri"] = uri;

			// bounce component into place
			plugComponentIntoRegion(region, params);

			retVal = true;
		}


		// they dropped a web script
		if("webscript" == alfType)
		{
			// TODO: Configure a web script component
			// Easy enough but requires a web script tree source or something like that
			retVal = true;
		}

		// they dropped a navigation node
		if("navNode" == alfType)
		{
			// TODO:  what does it mean to do this?
			retVal = true;
		}
		
		// they dropped a dm node (an Alfresco DM file)
		if("dmFile" == alfType)
		{
		}
		
		// they dropped a dm space (an Alfresco DM space)
		if("dmSpace" == alfType)
		{
			var cmType = data.node.attributes["cmType"];
			
			// PROJECT FOLDER
			if(cmType == "{http://www.alfresco.org/model/application/1.0}projectfolder")
			{
				var spaceRef = data.node.attributes["nodeId"];

				// bounce component into place
				var params = { };
				params["componentType"] = "ct-webscriptComponent";
				params["_endpointId"] = "alfresco";
				params["_webscript"] = "/collaboration/projectSpace?nodeRef=workspace://SpacesStore/" + spaceRef;
				params["_container"] = "iframe";
				plugComponentIntoRegion(region, params);
				retVal = true;							
			}
			
			// FOLDER
			if(cmType == "{http://www.alfresco.org/model/content/1.0}folder")
			{
				if(data.node.text == "Image Gallery")
				{
					var spaceRef = data.node.attributes["nodeId"];

					// bounce component into place
					var params = { };
					params["componentType"] = "ct-webscriptComponent";
					params["_endpointId"] = "alfresco";
					params["_webscript"] = "/collaboration/gallery/view/workspace://SpacesStore/" + spaceRef;
					params["_container"] = "iframe";
					plugComponentIntoRegion(region, params);
					retVal = true;								
				}
				else
				{
					var spaceRef = data.node.attributes["nodeId"];

					// bounce component into place
					var params = { };
					params["componentType"] = "ct-webscriptComponent";
					params["_endpointId"] = "alfresco";
					params["_webscript"] = "/collaboration/docLibrary?nodeRef=workspace://SpacesStore/" + spaceRef;
					params["_container"] = "iframe";
					plugComponentIntoRegion(region, params);
					retVal = true;				
				}
			}
			
			// FORUMS
			if(cmType == "{http://www.alfresco.org/model/content/1.0}forums")
			{
					var spaceRef = data.node.attributes["nodeId"];

					// bounce component into place
					var params = { };
					params["componentType"] = "ct-webscriptComponent";
					params["_endpointId"] = "alfresco";
					params["_webscript"] = "/collaboration/forumSummary?nodeRef=workspace://SpacesStore/" + spaceRef;
					params["_container"] = "iframe";
					plugComponentIntoRegion(region, params);
					retVal = true;				
			}
						
			// CALENDAR			
			if(cmType == "{com.infoaxon.alfresco.calendar}calendar")
			{
				var spaceRef = data.node.attributes["nodeId"];
				
				// bounce component into place
				var params = { };
				params["componentType"] = "ct-webscriptComponent";
				params["_endpointId"] = "alfresco";
				params["_webscript"] = "/collaboration/calendar?nodeRef=workspace://SpacesStore/" + spaceRef;
				params["_container"] = "iframe";
				plugComponentIntoRegion(region, params);
				retVal = true;
			}
		}

		// they dropped a google gadget
		// TODO
	}
	return retVal;
}


function plugComponentIntoRegion(region, params)
{
	// what region did they drop it on?
	var regionId = region.regionId;
	
	// some things about the current scope				
	var currentTemplateId = Ext.get("renderingTemplateId").dom.innerHTML;
	var currentPageId = Ext.get("renderingPageId").dom.innerHTML;

	// select the source id
	var regionScopeId = region.regionScopeId;
	var sourceId = "site";
	if("template" == regionScopeId)
		sourceId = currentTemplateId;
	if("page" == regionScopeId)
		sourceId = currentPageId;
		
	// update params to include things we need in order to use this web script
	params["regionId"] = regionId;
	params["regionSourceId"] = sourceId;
	params["regionScopeId"] = regionScopeId;

	// fire the event
	var proxiedURL = getInContextWebScriptURL("/incontext/components");
	Ext.Ajax.request({
		url: proxiedURL,
		method: 'GET',
		scriptTag: true,
		params: params,
		success: RefreshPageRegion
	});
}

function RefreshPageRegion(responseObject)
{
	var data = responseObject.responseText.parseJSON();
	
	// do cache invalidate
	refreshCache();
	
	var componentId = data.componentId;
	var componentTypeId = data.componentTypeId;
	var regionId = data.regionId;
	var regionScopeId = data.regionScopeId;
	
	// get the region div
	//var divEl = Ext.get(regionId);
	
	// update to the region div
	//bindComponentToRegion(regionId, componentAssociationId, componentId, componentTypeId);	
		
	// tell it to reload from the region renderer
	//var host = getLocalProtocolHostPort();
	//var url = host + "/region/?" + getQueryString() + "&regionId="+regionId+"&regionScopeId="+regionScopeId;
	//directRenderURL(url, divEl.dom.id);	
	faultRegion(regionId, regionScopeId);
}

function faultRegion(regionId, regionScopeId)
{
	var host = getLocalProtocolHostPort();
	var url = host + "/region/?" + getQueryString() + "&regionId="+regionId+"&regionScopeId="+regionScopeId;

	Ext.Ajax.request({
			url: url,
			disableCaching: true,
			method: 'GET',
			params: {
				'regionId' : regionId,
				'regionScopeId' : regionScopeId
			},
			success: faultRegionSuccess			
	});
}

function faultRegionSuccess(responseObject, options)
{
	var regionId = options["params"]["regionId"];
	var regionScopeId = options["params"]["regionScopeId"];
	
	// load in the html
	var html = responseObject.responseText;
	
	// get the parent of the region
	var el = Ext.get(regionId);
	var parent = el.parent();
	
	// insert a dummy
	var dummy = Ext.DomHelper.insertBefore(el, "<div id='dummyEl'/>", true);
	
	// remove the region node
	el.remove();
	
	// append the html into the right place
	//var _el = Ext.DomHelper.append(parent, html, true);	
	var _el = Ext.DomHelper.insertBefore(dummy, html, true);
	
	// remove the dummy
	dummy.remove();
	
	repaintInContext(false, true, false, false);
}






// configuration of regions
function configureRegion(regionId, regionScopeId, regionSourceId)
{
	Ext.get(regionId).dom.setAttribute('regionScopeId', regionScopeId);
	Ext.get(regionId).dom.setAttribute('regionSourceId', regionSourceId);
}

// binding of components to regions
function bindComponentToRegion(regionId, componentId, componentTypeId)
{
	Ext.get(regionId).dom.setAttribute('componentId', componentId);
	Ext.get(regionId).dom.setAttribute('componentTypeId', componentTypeId);
}









function switchTheme(themeId)
{
	Ext.util.CSS.swapStyleSheet('extjs-theme-link', '/ui/themes/extjs/css/xtheme-'+themeId+'.css');
	Ext.util.CSS.swapStyleSheet('builder-theme-link', '/ui/themes/builder/css/builder-'+themeId+'.css');
	
	// post ajax update
	Ext.Ajax.request({
		url: "/theme/?themeId=" + themeId,
		method: 'GET',
		scriptTag: true
	});
	
	currentThemeId = themeId;
}




























/****************************************
 **  EDITOR AND WINDOW DEFINITIONS
 ****************************************/
 
var inContextElementInits = new Object();

function initializeEditor(editorId)
{
	var hasBeenInitialized = inContextElementInits[editorId];
	if(hasBeenInitialized == "true")
		return;
		
	if("content_editor" == editorId)
	{
		// nothing to do here	
	}
	if("layout_editor" == editorId)
	{
		// nothing to do here
	}
	if("page_editor" == editorId)
	{
		setupPageEditor(editorId);
	}
	
	inContextElementInits[editorId] = "true";
}

function setupPageEditor(elementId)
{
	// Wraps overlays on top of all of the regions
	var selectString = "div[regionScopeId!='fakestring']";
	var divNodes = Ext.DomQuery.select(selectString);
	for(var z = 0; z < divNodes.length; z++)
	{
		var regionScopeId = divNodes[z].getAttribute("regionScopeId");
		if(regionScopeId != null)
		{
			var el = Ext.get(divNodes[z]);
			
			// region properties (always there)
			var regionId = Ext.get(el).dom.id;
			var regionSourceId = Ext.get(el).dom.getAttribute("regionSourceId");
			var layoutId = Ext.get(el).dom.getAttribute("layoutId");
			var componentId = Ext.get(el).dom.getAttribute("componentId");
			var componentTypeId = Ext.get(el).dom.getAttribute("componentTypeId");
						
			// Set up the Color Overlay
			var colorOverlay = new Ext.Element(document.createElement("div"));
			colorOverlay.dom.id = "color-overlay-" + Ext.get(el).dom.id;
			colorOverlay.dom.regionId = Ext.get(el).dom.id;
			document.body.appendChild(colorOverlay.dom);
			
			// format the color overlay
			formatColorOverlay(el, colorOverlay);
			var mouseOutHandler = function(event, el) 
			{
				// color-overlay
				var id = el.id.substring(14, el.id.length);				
				var cEl = Ext.get("color-overlay-"+id);
				cEl.setOpacity(0.2, true);

			}
			var mouseOverHandler = function(event, el) 
			{			
				// color-overlay
				var id = el.id.substring(14, el.id.length);
				var cEl = Ext.get("color-overlay-"+id);
				
				//Ext.get(el).puff();
				//Ext.get(el).switchOff();
				//Ext.get(el).frame("ff0000", 3, { duration: 3 });
				//Ext.get(el).frame("C3DAF9", 1, { duration: 0.5 });
				//Ext.get(el).frame("C3DAF9", 1, { duration: 0.3 });
				//Ext.get(el).highlight();
				
				cEl.setOpacity(0.3, true);
			}
			colorOverlay.on("mouseout", mouseOutHandler);
			colorOverlay.on("mouseover", mouseOverHandler);
			colorOverlay.on("click", regionColorOverlayClick, this);
			
			// display
			colorOverlay.show();
			
			
			
			
			//
			// Set the color overlay to be a "drag and drop" listener zone
			// This listens to "drop events" from the various editor windows
			//
			Ext.dd.Registry.register(colorOverlay.dom.id);
			var ddDropZone = new Ext.dd.DropZone(colorOverlay.dom, { 
				receiverType: 'region',
				regionId: regionId,
				regionScopeId: regionScopeId,
				regionSourceId: regionSourceId,
				layoutId: layoutId,
				componentId: componentId,
				componentTypeId: componentTypeId
			});
			ddDropZone.addToGroup('spacestree_ddgroup');
			ddDropZone.addToGroup('contenttree_ddgroup');
			ddDropZone.addToGroup('navtree_ddgroup');
			ddDropZone.addToGroup('comptree_ddgroup');
			ddDropZone.addToGroup('searchpanel_ddgroup');
			
			
		}
	}
}

function initializeWindow(windowId)
{
	var hasBeenInitialized = inContextElementInits[windowId];
	if(hasBeenInitialized == "true")
		return;

	var panel = null;
	var panelId = windowId + "_panel";
	if("web_content_window" == windowId)
		panel = setupWebContentPanel(panelId);
	if("search_window" == windowId)
		panel = setupSearchPanel(panelId);
	if("navigation_window" == windowId)
		panel = setupNavigationPanel(panelId);
	if("web_components_window" == windowId)
		panel = setupWebComponentsPanel(panelId);
	if("spaces_window" == windowId)
		panel = setupSpacesPanel(panelId);
	
	if(panel != null)
	{
		Ext.ComponentMgr.register(panel);
	
		// TODO:
		// If these are 'detached windows', then set them up as floating windows
		//var win = setupBasicInContextWindow(windowId, panel);
	
		inContextElementInits[windowId] = "true";
	}
}

function setupWebContentPanel(panelId)
{
	return makeContentTreePanel(panelId);	
}

function setupSearchPanel(panelId)
{
	return makeSearchPanel(panelId);
}

function setupNavigationPanel(panelId)
{
	return makeNavTreePanel(panelId);
}

function setupWebComponentsPanel(panelId)
{
	return makeComponentTreePanel(panelId);
}

function setupSpacesPanel(panelId)
{
	return makeSpacesTreePanel(panelId);
}












/****************************************
 **  SPACES TREE PANEL
 ****************************************/

function makeSpacesTreePanel(panelId)
{
	var remoteURL = getInContextWebScriptURL("/tree/spaces", true);
	var spacesTreeLoader = new Ext.tree.TreeLoader({
		dataUrl   : remoteURL
	});
	var spacesTreePanel = new Ext.tree.TreePanel({
		id: panelId,
		animate: true,
		enableDrag: true,
		autoScroll: true,
		title: 'Spaces',
		draggable: true,
		dragConfig: {
			ddGroup: 'spacestree_ddgroup',
			appendOnly: true
		},
		loader: spacesTreeLoader
	});
	new Ext.tree.TreeSorter(spacesTreePanel, {folderSort:true});
	spacesTreeLoader.on('beforeload', function(loader, node) {	
		var loaderUrl = remoteURL;
		var relativePath = getRelativePathForTreeNode(node);
		loaderUrl = loaderUrl + "?path=" + relativePath;
		loader.dataUrl = loaderUrl;
		loader.requestMethod = 'GET';
	});
	spacesTreeLoader.on('load', function(loader, node, response) {
		var childNodes = node.childNodes;
		for(var i = 0; i < childNodes.length; i++)
		{
			childNodes[i].on('dblclick', spacesTreeNodeDoubleClickHandler);
		}
	});	
	var spacesTreeRootNode = new Ext.tree.AsyncTreeNode({ 
		text: 'Company Home',
		draggable: false,
		iconCls: 'spacestree-icon-companyhome',
		expandable: true
	});
	spacesTreePanel.setRootNode(spacesTreeRootNode);
	spacesTreeRootNode.expand(false);
	
	return spacesTreePanel;
}

function spacesTreeNodeDoubleClickHandler(node, e)
{
	var url = getHttpHostPort() + node.attributes.url	
	window.open(url);
}








/****************************************
 **  CONTENT TREE PANEL
 ****************************************/

function makeContentTreePanel(panelId)
{
	var remoteURL = getInContextWebScriptURL("/tree/content");
	var contentTreeLoader = new Ext.tree.TreeLoader({
		dataUrl   : remoteURL
	});
	var contentTreePanel = new Ext.tree.TreePanel({
		id: panelId,
		animate: true,
		enableDrag: true,
		draggable: true,
		autoScroll: true,
		title: 'Web Content',
		dragConfig: {
			ddGroup: 'contenttree_ddgroup',
			appendOnly: true
		},
		loader: contentTreeLoader
	});
	new Ext.tree.TreeSorter(contentTreePanel, {folderSort:true});
	contentTreeLoader.on('beforeload', function(loader, node) {	
		var loaderUrl = remoteURL;
		var relativePath = getRelativePathForTreeNode(node);
		loaderUrl = loaderUrl + "&path=" + relativePath;
		loader.dataUrl = loaderUrl;
		loader.requestMethod = 'GET';
	});
	var contentTreeRootNode = new Ext.tree.AsyncTreeNode({ 
		text: 'Web Application',
		draggable: false,
		iconCls: 'tree-icon-webapplicationroot',
		expandable: true
	});
	contentTreePanel.setRootNode(contentTreeRootNode);
	contentTreeRootNode.expand(false);

	return contentTreePanel;
}

function getRelativePathForTreeNode(node)
{
	var relativePath = "";
	var n = node;
	while(n != null)
	{
		if(n != null && n.getDepth() != 0)
		{
			relativePath = n.text + relativePath;
		}
		if(n.getDepth() != 0)
			relativePath = "/" + relativePath;
		n = n.parentNode;
	}
	if("" == relativePath)
		relativePath = "/";
	return relativePath;
}




/****************************************
 **  SEARCH PANEL
 ****************************************/

function makeSearchPanel(panelId)
{
	var searchQueryField = new Ext.form.TextField({
				id: 'searchQueryField',
				xtype: 'textfield',
				value: ''
	});
	Ext.ComponentMgr.register(searchQueryField);
	var searchStartButton = new Ext.Button({
				id: 'searchStartButton',
				xtype: 'button',
				text: 'Go!',
				handler: startSearchHandler
	});	
	Ext.ComponentMgr.register(searchStartButton);	
	
	var searchContentTypeCombo = new Ext.form.ComboBox({
	    fieldLabel: 'Type of Content',
	    store: new Ext.data.SimpleStore({
		fields: ['id', 'name'],
		data : [ ['article', 'Article'],['press_release','Press Release'] ]
	    }),
	    displayField:'name',
	    typeAhead: true,
	    mode: 'local',
	    id: 'searchContentType'
        });
	
	var searchPanel = new Ext.Panel({
		id: panelId,
		title: 'Search',
		draggable: true,
		layout: 'form',
		labelAlign: 'top',
		items: [
			searchContentTypeCombo
			,
			{
				id: 'searchTerms',
				width: 190,
				fieldLabel: 'Containing',
				xtype: 'textfield'
			}
			,
			searchStartButton
			,
			{
				id: 'searchResults'
			}
		
		]
	});
	
	return searchPanel;
}

function startSearchHandler(item, e)
{
	var searchQueryField = Ext.ComponentMgr.get('searchQueryField');
	alert(searchQueryField.getValue());
	
	var webscriptUri = "/ads/search/lucene";
	var proxiedURL = getAdsWebScriptURL(webscriptUri);

	Ext.Ajax.request({
			url: proxiedURL,
			method: 'GET',
			scriptTag: true,
			success: displaySearchResultsHandler
	});
}

function displaySearchResultsHandler(responseObject)
{
	var data = responseObject.responseText.parseJSON();
	alert(data.toJSONString());
}







/****************************************
 **  NAVIGATION TREE PANEL
 ****************************************/

function makeNavTreePanel(panelId)
{
	var remoteURL = getInContextWebScriptURL("/tree/navigation");
	var navTreeLoader = new Ext.tree.TreeLoader({
		dataUrl   : remoteURL
	});
	var button1 = new Ext.Button(
		{	
			id: 'navAddButton',
			iconCls: 'tree-icon-navtree-addbutton',
			tooltip: 'Add a new node',
			text: 'Add',
			cls: "x-btn-text-icon",
			handler: navTreeButtonClickHandler,
			disabled: true
		}
	);
	Ext.ComponentMgr.register(button1);

	var button2 = new Ext.Button(
		{	
			id: 'navEditButton',
			iconCls: 'tree-icon-navtree-editbutton',
			tooltip: 'Edit this node',
			text: 'Edit',
			cls: "x-btn-text-icon",
			handler: navTreeButtonClickHandler,
			disabled: true
		}
	);	
	Ext.ComponentMgr.register(button2);
	
	var button3 = new Ext.Button(
		{	
			id: 'navRemoveButton',
			iconCls: 'tree-icon-navtree-deletebutton',
			tooltip: 'Remove this node',
			text: 'Remove',
			cls: "x-btn-text-icon",
			handler: navTreeButtonClickHandler,
			disabled: true
		}
	);
	Ext.ComponentMgr.register(button3);
	
	var navTreePanel = new Ext.tree.TreePanel({
		id: panelId,
		title: 'Navigation',
		animate: true,
		autoScroll: true,
		enableDrag: true,
		draggable: true,
		tbar: [button1, '-', button2, '-', button3],
		dragConfig: {
			ddGroup: 'navtree_ddgroup',
			appendOnly: true
		},
		loader: navTreeLoader
	});
	Ext.ComponentMgr.register(navTreePanel);
	new Ext.tree.TreeSorter(navTreePanel, {folderSort:true});
	navTreeLoader.on('beforeload', function(loader, node) {	
		var loaderUrl = remoteURL;
		loaderUrl = loaderUrl + "&pageId=" + node.attributes["pageId"];
		loader.dataUrl = loaderUrl;
		loader.requestMethod = 'GET';
	});
	navTreeLoader.on('load', function(loader, node, response) {
		var childNodes = node.childNodes;
		for(var i = 0; i < childNodes.length; i++)
		{
			childNodes[i].on('click', navTreeNodeClickHandler);
			childNodes[i].on('dblclick', navTreeNodeDoubleClickHandler);
		}
	});
	var navTreeRootNode = new Ext.tree.AsyncTreeNode({ 
		text: 'Navigation',
		draggable: true,
		iconCls: 'tree-icon-rootnode',
		expandable: true,
		pageId: getRootPageId()
	});
	navTreeRootNode.on('click', navTreeNodeClickHandler);
	navTreeRootNode.on('dblclick', navTreeNodeDoubleClickHandler);
	navTreePanel.setRootNode(navTreeRootNode);
	navTreeRootNode.expand(false);
	
	updateNavTreeButtons(navTreeRootNode);

	return navTreePanel;
}

function navTreeNodeClickHandler(node, e)
{
	updateNavTreeButtons(node);
}

function navTreeNodeDoubleClickHandler(node, e)
{
	// TODO: hit the layout dispatcher and fault in a new layout via ajax
	// for now, redirect
	var nodeId = node.attributes.nodeId;
	window.location.href = "/?f=default&n="+nodeId;
}

function updateNavTreeButtons(node)
{
	var nodeId = node.attributes.nodeId;

	var navAddButton = Ext.ComponentMgr.get('navAddButton');
	var navEditButton = Ext.ComponentMgr.get('navEditButton');
	var navRemoveButton = Ext.ComponentMgr.get('navRemoveButton');
	
	navRemoveButton.setDisabled(true);
	var childNodes = node.childNodes;
	if(childNodes.length == 0) {
		navRemoveButton.setDisabled(false);  // show remove button
	}
	
	// show the add button
	navAddButton.setDisabled(false);
	
	// show the edit button
	navEditButton.setDisabled(false);
}

function navTreeButtonClickHandler(item, e) 
{
	var id = item.id;
	var treePanel = Ext.ComponentMgr.get('navigation_window_panel');
	var node = treePanel.getSelectionModel().getSelectedNode();

	if('navAddButton' == id)
	{
		addNavigationNode(node);	
	}
	if('navEditButton' == id)
	{
		editNavigationNode(node);
	}
	if('navRemoveButton' == id)
	{
		removeNavigationNode(node);	
	}
}





/****************************************
 **  COMPONENT TREE PANEL
 ****************************************/

function makeComponentTreePanel(panelId)
{
	var remoteURL = getInContextWebScriptURL("/tree/components");
	var compTreeLoader = new Ext.tree.TreeLoader({
		dataUrl   : remoteURL
	});	
	compTreeLoader.on('beforeload', function(loader, node) {	
		var loaderUrl = remoteURL;
		loaderUrl = loaderUrl + "&nodeId=" + node.attributes["nodeId"];
		loader.dataUrl = loaderUrl;
		loader.requestMethod = 'GET';
	});
	var compTreePanel = new Ext.tree.TreePanel({
		id: panelId,
		title: 'Web Components',
		animate: true,
		autoScroll: true,
		draggable: true,
		enableDrag: true,
		dragConfig: {
			ddGroup: 'comptree_ddgroup',
			appendOnly: true
		},
		loader: compTreeLoader
	});
	Ext.ComponentMgr.register(compTreePanel);
	new Ext.tree.TreeSorter(compTreePanel, {folderSort:true});
	var compTreeRootNode = new Ext.tree.AsyncTreeNode({ 
		text: 'Component Library',
		draggable: true,
		iconCls: 'tree-icon-componenttree-root',
		expandable: true,
		nodeId: "root"
	});
	compTreePanel.setRootNode(compTreeRootNode);
	//compTreeRootNode.expand(true);
	
	return compTreePanel;
}






// The user tapped 'escape' and closed the window
// We capture this so that we can "gracefully" close the window
// This saves state in the session and lets the user reopen it later
function escapeWindow(a,b,c)
{
	// unchecking the menu automatically triggers everything we want
	var checkItem = Ext.ComponentMgr.get("toggle_"+this.id);
	if(checkItem != null)
		checkItem.setChecked(false);
}

// The user clicked the 'close' button on a window
// We capture this so that we can "gracefully" close the window
// This saves state in the session and lets the user reopen it later
function beforeCloseWindow(obj)
{
	var checkItem = Ext.ComponentMgr.get("toggle_"+this.id);
	if(checkItem != null)
		checkItem.setChecked(false);
}

// This saves state in the session (server side)
function onMoveWindow(obj)
{
	doPersistElementSessionState(this.id);
}

// This saves state in the session (server side)
function onResizeWindow(obj)
{
	doPersistElementSessionState(this.id);
}


function setupBasicInContextWindow(elementId, panel, _x, _y, _width, _height, center)
{
	if(_x == null)
		_x = 0;
	if(_y == null)
		_y = 26;
	if(_height == null)
		_height = 500;
	if(_width == null)
		_width = 200;
		
	var height = getInContextElementStateProperty(elementId, "height", _height);
	var width = getInContextElementStateProperty(elementId, "width", _width);
	var x = getInContextElementStateProperty(elementId, "x", _x);
	var y = getInContextElementStateProperty(elementId, "y", _y);
	
	var windowTitle = getInContextElementName(elementId);
	var win = new Ext.Window({
		id: elementId,
		title: windowTitle,
		resizable: true,
		closable: true,
		height: height,
		width: width,
		onEsc: escapeWindow,
    		items: panel
	});
	win.on("beforeclose", beforeCloseWindow);
	win.on("move", onMoveWindow);
	win.on("resize", onResizeWindow);
	panel.setHeight(height);
	win.setPagePosition(x,y);

	win.show();

	// add it to the component manager
	Ext.ComponentMgr.register(win);
	
	if(center)
		win.center();
		
	return win;
}





function regionColorOverlayClick(event, overlay, el)
{
	var eventRegionId = event.target.regionId;
	var overlayRegionId = overlay.regionId;
	if(eventRegionId == overlayRegionId)
	{
		// if they're in page mode
		if(getInContextElementEnabled("page_editor"))
		{
			showRegionPageEditorOverlay(event, overlay);
		}		
	}
}

function showRegionPageEditorOverlay(event, overlay)
{
	// "color-overlay-"
	var id = overlay.id.substring(14, overlay.id.length);
	var el = Ext.get(id);
	
	var theClazz = "region-window-body-unconfigured-region";
	if(el.dom.getAttribute("componentId") != null)
		theClazz = "region-window-body-configured-" + el.dom.getAttribute("regionScopeId") + "-region";
	
	// the window id
	var winId = "region-overlay-" + id;
	var win = Ext.ComponentMgr.get(winId);
	if(win == null)
	{	
		// tools
		var button1 = new Ext.Button(
				{	
					id: 'add_new_component',
					iconCls: 'regiontoolbar-icon-add-new-component',
					tooltip: 'Configure a new component for this region',
					tooltipType: 'title',
					handler: buttonHandler,

					regionId: id,
					regionScopeId: el.dom.getAttribute("regionScopeId"),
					regionSourceId: el.dom.getAttribute("regionSourceId"),
					layoutId: el.dom.getAttribute("layoutId"),

					componentId: el.dom.getAttribute("componentId"),
					componentTypeId: el.dom.getAttribute("componentTypeId")
				}
		);
		var button3 = new Ext.Button(
				{	
					id: 'configure_existing_component',
					iconCls: 'regiontoolbar-icon-edit-component',
					tooltip: 'Configure this component',
					tooltipType: 'title',
					handler: buttonHandler,

					regionId: id,
					regionScopeId: el.dom.getAttribute("regionScopeId"),
					regionSourceId: el.dom.getAttribute("regionSourceId"),
					layoutId: el.dom.getAttribute("layoutId"),

					componentId: el.dom.getAttribute("componentId"),
					componentTypeId: el.dom.getAttribute("componentTypeId")
				}
		);
		var button4 = new Ext.Button(
				{	
					id: 'remove_existing_component',
					iconCls: 'regiontoolbar-icon-delete-component',
					tooltip: 'Remove Component',
					tooltipType: 'title',
					handler: buttonHandler,

					regionId: id,
					regionScopeId: el.dom.getAttribute("regionScopeId"),
					regionSourceId: el.dom.getAttribute("regionSourceId"),
					layoutId: el.dom.getAttribute("layoutId"),

					componentId: el.dom.getAttribute("componentId"),
					componentTypeId: el.dom.getAttribute("componentTypeId")
				}
		);

		var tbarArray = new Array();
		tbarArray[tbarArray.length] = button3;
		tbarArray[tbarArray.length] = button1;
		tbarArray[tbarArray.length] = '-';
		tbarArray[tbarArray.length] = button4;


		//
		// build the information overlay window
		//
		win = new Ext.Window({
			id: winId,
			regionId: id,
			winType: 'regionOverlay',
			resizable: false,
			closable: false,
			animateTarget: true,
			draggable: false,
			height: el.getHeight(),
			width: el.getWidth(),
			tbar: tbarArray,
			bodyBorder: false,
			hideBorders: true,
			frame: true,
			shadow: true,
			cls: theClazz
		});
// TODO: unhide frame to bring back grips		
		win.setPagePosition(el.getLeft(), el.getTop());
		Ext.ComponentMgr.register(win);
		


		//
		// Enable and Disable buttons based on the region configuration
		//
		if(el.dom.getAttribute("componentId") == null)
		{
			// no component is bound
			button3.setDisabled(true); // configure component
			button4.setDisabled(true); // remove
		}
		else
		{
			// a component is not bound
			button1.setDisabled(true); // add component
		}

				
		
		//
		// Render the window and give it a transparent body
		//		
		win.show();
		Ext.WindowMgr.sendToBack(win);
		win.body.parent().setStyle("background", "transparent none repeat scroll 0%");
		
		
		
		// test
		win.body.parent().setStyle("border-width", "0");
		// end test
	

		//
		// Set up handlers on the windows so that they auto-remove when mouseout occurs
		//
		var mouseOutHandler = function(event, regionOverlay, el) 
		{
			// set up a delayed task (to close the region overlay)
			var delayedTask = new Ext.util.DelayedTask(removeRegionOverlay, null, [ this.id ]);
			delayedTask.delay(50);
			addDelayedTask(this.id, delayedTask);
			
		}
		var mouseOverHandler = function(event, regionOverlay) 
		{
			// check whether the target of this event is something
			// in the body of the window
			if(event.within(Ext.get(this.id),true))
			{
				var delayedTask = getDelayedTask(this.id);
				if(delayedTask != null)
					delayedTask.cancel();
				clearDelayedTask(this.id);
			}
		}
		win.getEl().on("mouseout", mouseOutHandler);
		win.getEl().on("mouseover", mouseOverHandler);




		//
		// Tell the region overlay to load in region information
		//
		var url = "/ui/misc/regionPageOverlay.jsp?regionId=" + id + "&regionScopeId=" + el.dom.getAttribute("regionScopeId") + "&regionSourceId=" + el.dom.getAttribute("regionSourceId");
		if(el.dom.getAttribute("componentId")  != null)
			url += "&componentId=" + el.dom.getAttribute("componentId");
		win.load(url);

		
		//
		// Perform temporary adjustments to underlying dom for presentation purposes
		//
		var minWidth = 300;
		var minHeight = 150;
		if(	(win.getSize()["height"] < minHeight) ||
			(win.getSize()["width"] < minWidth)	)
		{
			// stash away the original region height
			Ext.get(id).dom.setAttribute("didOverlayResize", "true");
			Ext.get(id).dom.setAttribute("originalWidth", Ext.get(id).getSize()["width"]);
			Ext.get(id).dom.setAttribute("originalHeight", Ext.get(id).getSize()["height"]);
			
			// adjustments
			if(win.getSize()["width"] > minWidth)
				minWidth = win.getSize()["width"];
			if(win.getSize()["height"] > minHeight)
				minHeight = win.getSize()["height"];
			
			// adjust a few things
			Ext.get(id).setSize(minWidth, minHeight, false);
			Ext.get("color-overlay-" + id).setSize(minWidth, minHeight, true);

			// set height to window
			win.setSize(minWidth, minHeight, true);

			// delayed task to repaint incontext elements
			var delayedTask = new Ext.util.DelayedTask(function() {
				repaintInContext(false, true, false);
			}, null, [ this.id ]);
			delayedTask.delay(400);
		}
	}
}

function removeRegionOverlay(overlayWindowId)
{
	var win = Ext.ComponentMgr.get(overlayWindowId);
	if(win != null)
	{
		var regionId = win.regionId;
		
		// dismantle the region overlay
		win.hide();		
		Ext.ComponentMgr.unregister(win);
		win.destroy();		
		
		// resize the underlying region to its original size (if needed)
		var didOverlayResize = Ext.get(win.regionId).dom.getAttribute("didOverlayResize");
		if("true" == didOverlayResize)
		{
			var originalHeight = Ext.get(win.regionId).dom.getAttribute("originalHeight");
			var originalWidth = Ext.get(win.regionId).dom.getAttribute("originalWidth");
			
			// adjust a few things
			Ext.get(regionId).setSize(originalWidth, originalHeight, false);
			Ext.get("color-overlay-" + regionId).setSize(originalWidth, originalHeight, false);

			// delayed task to repaint incontext elements
			var delayedTask = new Ext.util.DelayedTask(function() {
				repaintInContext(false, true, false);
				resizeAllRegionOverlays();
			}, null, [ this.id ]);
			delayedTask.delay(400);
			
		}
	}
}

function removeAllRegionOverlays()
{
	Ext.WindowMgr.each(function(window) {
		if(window.winType == "regionOverlay")
		{
			removeRegionOverlay(window.id);
		}
	});
}

function resizeRegionOverlay(overlayWindowId)
{
	var win = Ext.ComponentMgr.get(overlayWindowId);
	if(win != null)
	{
		var regionId = window.regionId;
		var regionDiv = Ext.get(regionId);
		if(regionDiv != null)
		{		
			var x = regionDiv.getBox()["x"];
			var y = regionDiv.getBox()["y"];
			var width = regionDiv.getBox()["width"];
			var height = regionDiv.getBox()["height"];

			win.setPosition(x,y);
			win.setSize(width,height);
		}
	}
}

function resizeAllRegionOverlays()
{
	Ext.WindowMgr.each(function(window) {
		if(window.winType == "regionOverlay")
		{
			resizeRegionOverlay(window.id);
		}
	});
}





var bRepaintToolbar = false;
var bRepaintEditors = false;
var bRepaintWindows = false;
var bRepaintDocks = false;
function repaintInContext(bToolbar, bEditors, bWindows, bDocks)
{
	clearDelayedTask("global_repaint");
	
	if(bToolbar)
		bRepaintToolbar = true;
	if(bEditors)
		bRepaintEditors = true;
	if(bWindows)
		bRepaintWindows = true;
	if(bDocks)
		bRepaintDocks = true;

	var delayedTask = new Ext.util.DelayedTask(function() 
	{
		doRepaintInContext(bRepaintToolbar, bRepaintEditors, bRepaintWindows, bRepaintDocks);
		
		bRepaintToolbar = false;
		bRepaintEditors = false;
		bRepaintWindows = false;
		bRepaintDocks = false;

	}, null, null);
	delayedTask.delay(50);
	addDelayedTask("global_repaint", delayedTask);
}

function _repaintInContext(bToolbar, bEditors, bWindows, bDocks)
{
	if(bToolbar)
		bRepaintToolbar = true;
	if(bEditors)
		bRepaintEditors = true;
	if(bWindows)
		bRepaintWindows = true;
	if(bDocks)
		bRepaintDocks = true;
	doRepaintInContext();
	bRepaintToolbar = false;
	bRepaintEditors = false;
	bRepaintWindows = false;
	bRepaintDocks = false;
}


function doRepaintInContext()
{
	if(bRepaintDocks)
	{
		// if we're not in in-context mode, hide the dock, otherwise show it
		if(isInContextEnabled())
		{
			showInContextViewport(false);
		}
		else
		{
			hideInContextViewport(false);
		}
	}


	//
	// Fundamentally, the toolbar should appear whenever we have in-context editing
	// turned on.  Otherwise, it should be removed
	//
	if(bRepaintToolbar)
	{
		if(isInContextEnabled())
		{
			if(mainToolbarSelectedEditor == null)
				mainToolbarSelectedEditor = "content_editor";
			setMainToolbarMode(mainToolbarSelectedEditor);
			showToolbarContainer();
		}
		else
		{
			hideToolbarContainer();
		}
	}
	
	
	




	// START OF bEDITORS
	if(bRepaintEditors)
	{

		//
		// In Content Editor mode, all of the "in-place" tools should be activated
		// This allows for in-context content editing
		//
		// If we're not in Content Editor mode, we should hide all of the "in-place"
		// content authoring tools
		//
		var selectString = "*[id^=ipe-]";
		var nodes = Ext.DomQuery.select(selectString);
		if(getInContextElementEnabled("content_editor"))
		{
			// make sure they're visible
			for(var z = 0; z < nodes.length; z++)
			{
				Ext.get(nodes[z]).setVisible(true);
			}
		}
		else
		{
			// make sure they're hidden
			for(var z = 0; z < nodes.length; z++)
			{
				Ext.get(nodes[z]).setVisibilityMode(Ext.Element.DISPLAY);
				Ext.get(nodes[z]).hide();
			}
		}





		//
		// If we're in Page Builder mode, we want to introduce overlays onto all of the page
		// regions.  These overlays allow you to snap in and configure "web parts".
		// Here, we walk through all of the overlays and hide the ones we don't want to show
		//
		var selectString = "div[regionScopeId!='fakestring']";
		var divNodes = Ext.DomQuery.select(selectString);
		for(var z = 0; z < divNodes.length; z++)
		{
			var rId = divNodes[z].regionId;
			if(rId != null)
			{
				var regionEl = Ext.get(rId);
				if(regionEl != null)
				{
					// region layout overlay
					var rWin = Ext.ComponentMgr.get("region-layout-overlay-" + rId);
					if(rWin != null)
					{
						if(getInContextElementEnabled("layout_editor"))
						{
							rWin.show()
						}
						else
						{
							rWin.hide()
						}
					}					

					// color overlay
					var cEl = Ext.get("color-overlay-" + rId);
					if(cEl != null)
					{
						formatColorOverlay(regionEl, cEl);

						if(getInContextElementEnabled("page_editor"))
						{
							cEl.setVisibilityMode(Ext.Element.DISPLAY);
							cEl.show();
						}
						else
						{
							cEl.setVisibilityMode(Ext.Element.DISPLAY);
							cEl.hide();
						}
					}
					
					
				}
			}
		}






		//
		// TODO: Layout Editor
		// If they're in the Layout Editor, we should snap in a page-wide overlay that lets you
		// drag and resize the layout for the entire page.  The idea is not to configure individual
		// regions but to resize regions, introduce new regions and change region scopes.
		//









		//
		// If we're not in page editing mode, shut down any latent region overlays
		//
		if(!getInContextElementEnabled("page_editor"))
		{
			removeAllRegionOverlays();
		}
		
		
	}
	// END OF bEDITORS


	
	

	// START OF bWINDOWS	
	if(bRepaintWindows)
	{
	
		//
		// Now redraw any tools (windows) which have been opened up
		//
		var allElementIds = getInContextElementIds();
		for(var a = 0; a < allElementIds.length; a++)
		{
			var _type = getInContextElementType(allElementIds[a]);
			if("window" == _type)
			{
				var _window = Ext.ComponentMgr.get(allElementIds[a]);
				if(_window != null)
				{
					if(getInContextElementEnabled(allElementIds[a]))
					{
						_window.show();
					}
					else
					{
						_window.hide();
					}
				}
			}
		}
		
	}	
	
	
	// Refresh the floating menu
	refreshFloatingMenu();
	
	if(redrawDynamicLayout != null)
		redrawDynamicLayout();
}


function formatColorOverlay(regionElement, overlayElement)
{
	// offset of the bodyContainer
	var body = Ext.ComponentMgr.get("bodyPanel").body;
	var bodyEl = Ext.get(body);
		
	var scroll = bodyEl.getScroll();
	var scrollTop = scroll.top;
	var scrollLeft = scroll.left;	
	// end of offsets
	
	var regionScopeId = regionElement.dom.getAttribute("regionScopeId");
	
	var colorHigh = BUILDER_PAGE_SCOPE_OVERLAY_COLOR1_HIGH;
	var colorLow = BUILDER_PAGE_SCOPE_OVERLAY_COLOR1_LOW;
	var color = BUILDER_PAGE_SCOPE_OVERLAY_COLOR1;
	if("template" == regionScopeId)
	{
		colorHigh = BUILDER_TEMPLATE_SCOPE_OVERLAY_COLOR1_HIGH;
		colorLow = BUILDER_TEMPLATE_SCOPE_OVERLAY_COLOR1_LOW;
		color = BUILDER_TEMPLATE_SCOPE_OVERLAY_COLOR1;
	}
	if("site" == regionScopeId)
	{
		colorHigh = BUILDER_SITE_SCOPE_OVERLAY_COLOR1_HIGH;
		colorLow = BUILDER_SITE_SCOPE_OVERLAY_COLOR1_LOW;
		color = BUILDER_SITE_SCOPE_OVERLAY_COLOR1;
	}

	// determine the total window width and height
	var totalWindowWidth = window.innerWidth;
	var totalWindowHeight = window.innerHeight;	
	if(Ext.isIE) {
		totalWindowWidth = document.body.clientWidth;
		totalWindowHeight = document.body.clientHeight;
	}
	
	// determine the maximum right and bottom positions
	var maxRight = totalWindowWidth;
	var maxBottom = totalWindowHeight;
	var maxLeft = Ext.ComponentMgr.get("dockPanel").getSize().width + 6;
	var maxTop = 26; // toolbar
	// if scrollable, knock it down a bit
	var isScrollable = Ext.get("bodyContainer").isScrollable();	
	if(isScrollable || !isScrollable)
	{
		maxRight = maxRight - 18;
		maxBottom = maxBottom - 18;		
	}
	
	// determine the total body width and height
	var totalBodyWidth = maxRight - Ext.get("bodyContainer").getLeft();
	var totalBodyHeight = maxBottom - Ext.get("bodyContainer").getTop();
		
	// the width and height of the overlay
	var newWidth = regionElement.getWidth();
	var newHeight = regionElement.getHeight();
	var newTop = regionElement.getTop();
	var newLeft = regionElement.getLeft();
	
	// check whether we need to cut back on width and height for the overlay
	var showRightBorder = true;
	var showBottomBorder = true;
	var showTopBorder = true;
	var showLeftBorder = true;
	if(regionElement.getLeft() + newWidth >= maxRight)
	{
		newWidth = maxRight - regionElement.getLeft();
		showRightBorder = false;
	}
	if(regionElement.getTop() + newHeight >= maxBottom)
	{
		newHeight = maxBottom - regionElement.getTop();
		showBottomBorder = false;
	}
	if(newLeft < maxLeft)
	{
		newWidth = newWidth + (newLeft - maxLeft);
		newLeft = maxLeft;
		showLeftBorder = false;
	}
	if(newTop < maxTop)
	{
		newHeight = newHeight + (newTop - maxTop);
		newTop = maxTop;
		showTopBorder = false;
	}
	
	
	// set the height and width
	overlayElement.setHeight(newHeight);
	overlayElement.setWidth(newWidth);	

	// set the top left
	overlayElement.setTop(newTop);
	overlayElement.setLeft(newLeft);


	// and style (color)
	overlayElement.setStyle("position", "absolute");
	overlayElement.setStyle("background-color", color);
	
	
	// set the borders
	var borderStringHigh = "2px " + colorHigh + " dotted";
	var borderStringLow = "2px " + colorLow + " dotted";			
	if(showRightBorder)
		overlayElement.setStyle("border-right", borderStringHigh);
	else
		overlayElement.setStyle("border-right", "");
	if(showBottomBorder)
		overlayElement.setStyle("border-bottom", borderStringHigh);
	else
		overlayElement.setStyle("border-bottom", "");
	if(showTopBorder)
		overlayElement.setStyle("border-top", borderStringLow);
	else
		overlayElement.setStyle("border-top", "");
	if(showLeftBorder)
		overlayElement.setStyle("border-left", borderStringLow);
	else
		overlayElement.setStyle("border-left", "");
	overlayElement.setOpacity(0.2);


	// check whether this region overextends and if it does, just hide it
	if(overlayElement.getLeft() >= maxRight)
		overlayElement.setVisible(false);
	if(overlayElement.getTop() >= maxBottom)
		overlayElement.setVisible(false);
}




function setupRegionLayoutEditorOverlay(regionId)
{
	var el = Ext.get(regionId);
		
	// the window id
	var winId = "region-layout-overlay-" + regionId;
	var win = Ext.ComponentMgr.get(winId);
	if(win == null)
	{
		var button1 = new Ext.Button(
				{	
					id: 'configure_region',
					iconCls: 'regiontoolbar-icon-edit-component',
					tooltip: 'Configure this Region',
					tooltipType: 'title',
					handler: buttonHandler,

					regionId: regionId,
					regionScopeId: el.dom.getAttribute("regionScopeId"),
					regionSourceId: el.dom.getAttribute("regionSourceId")
				}
		);
		var button2 = new Ext.Button(
				{	
					id: 'remove_region',
					iconCls: 'regiontoolbar-icon-delete-component',
					tooltip: 'Remove Region',
					tooltipType: 'title',
					handler: buttonHandler,

					regionId: regionId,
					regionScopeId: el.dom.getAttribute("regionScopeId"),
					regionSourceId: el.dom.getAttribute("regionSourceId")
				}
		);

		var tbarArray = new Array();
		tbarArray[tbarArray.length] = button1;
		tbarArray[tbarArray.length] = '-';
		tbarArray[tbarArray.length] = button2;


		//
		// build the information overlay window
		//
		win = new Ext.Window({
			id: winId,
			regionId: regionId,
			resizable: true,
			closable: false,
			draggable: true,
			tbar: tbarArray,
			shadow:true,
			title: regionId
		});
		win.setPagePosition(el.getLeft(), el.getTop());
		win.setWidth(el.getWidth());
		win.setHeight(el.getHeight());
		//alert(win.getSize()["width"]);
		
		Ext.ComponentMgr.register(win);	
		win.show();
		
		//
		// Render the window and give it a transparent body
		//		
		win.body.parent().setStyle("background", "transparent none repeat scroll 0%");
		
		// test
		win.body.parent().setStyle("border-width", "0");
		// end test

		//
		// Tell the region overlay to load in region information
		//
		//var url = "/ui/misc/regionLayoutOverlay.jsp?regionId=" + regionId + "&regionScopeId=" + el.dom.getAttribute("regionScopeId") + "&layoutId=" + el.dom.getAttribute("layoutId") + "&regionSourceId=" + el.dom.getAttribute("regionSourceId");
		//win.load(url);
		
		
		// handlers
		win.on("resize", layoutRegionResizeHandler);
		win.on("move", layoutRegionMoveHandler);
	}
	return win;
}

function layoutRegionResizeHandler()
{
	var width = this.width;
	var height = this.height;
	updateLayoutPanelSize(this.regionId, width, height);
}

function layoutRegionMoveHandler()
{
	var x = this.x;
	var y = this.y;
	updateLayoutPanelPosition(this.regionId, x, y);
}

function updateLayoutPanelPosition(regionId, x, y)
{
	// TODO: Fire an Ajax event back that updates properties stored on the Layout Instance
	
	Ext.get(regionId).setLocation(x,y,true);
}

function updateLayoutPanelSize(regionId, width, height)
{
	// TODO: Fire an Ajax event back that updates properties stored on the Layout Instance
	
	Ext.get(regionId).setSize(width,height,true);
}