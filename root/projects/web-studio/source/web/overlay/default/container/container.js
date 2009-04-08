if (typeof WebStudio == "undefined" || !WebStudio)
{
	WebStudio = {};
}

/**
 * Constructs a new Alfresco Application instance.
 *
 * There should only be one Alfresco Application instance active in
 * the browser at a time.
 *
 * @return Alfresco application instance
 */
WebStudio.Application = function() 
{
	if (typeof WebStudio.app != "undefined") 
	{
		this.addDebugMessages('error', 'WebStudio.app already exist');
		return null;
	}

	WebStudio.app = this;

	this.defaultContainer = $('templateContainer');
	this.defaultTemplateSelector = 'div[id=AlfrescoApplicationTemplate]';

	this.defaultElementsConfig = {
	
		FloatingMenuControl: {
			selector: 'div[id=FloatingMenuControl]',
			blockSelection: true,
			objects: {
				FloatingMenuInfo: {
					selector: '.FloatingMenuInfo'
				},
				FloatingMenuTitle: {
					selector: '.FloatingMenuTitle',
					events: {
						click: function(e) {
							e = new Event(e);
							if(WebStudio.app.currentApplicationId)
							{
								WebStudio.app.toggleEdit();
							}
							e.stop();
						}					
					}
				},
				FloatingMenuSandboxId: {
					selector: '.FloatingMenuSandboxId'
				}
			}
		}
		,
		PanelsHolder: {
			selector: 'div[id=AATwoPanels]'
		}
		,
		FormTemplate: {
			selector: 'div[id=AlfrescoForm]',
			remove: true
		}
		,
		AlfrescoMessageBoxTmplate: {
			selector: 'div[id=AlfrescoMessageBoxTmplate]',
			remove: true
		}
		,
		AlfrescoMessageBoxProgressBar: {
			selector: 'div[id=AlfrescoMessageBoxProgressBar]',
			remove: true
		}
		,
		FloatingMenuSelector: {
			selector: 'div[id=FloatingMenuSelector]',
			blockSelection: true
		}
    };

	// the loaded applications container object
	this.applications = { };

	// set up initial state
	this.incontextMode = 'view'; //'view' or 'edit'
    this.isHideDockingPanel = false;
	this.activeMenu = null;	
};

WebStudio.Application.prototype = new WebStudio.AbstractTemplater('WebStudio.Application');

/**
 * Loads application resources for the Alfresco application.
 */
WebStudio.Application.prototype.init = function() 
{
	var _this = this;
	
	this.activate();
	
	// set up the floating menu icon
	var floatingMenuIcon = $('FloatingMenuIcon');
	floatingMenuIcon.addEvent("click", function(e) {
		e = new Event(e);
		WebStudio.app.toggleApplicationSelector();
		e.stop();		
	});
	
	if(WebStudio.context.getSandboxId())
	{	
		this.initApps();
	}
};

/**
 * Begins initialization of applications
 */
WebStudio.Application.prototype.initApps = function()
{
	var _this = this;
	
	if(!this.applicationsBootstrapped)
	{
		var postFunction = function()
		{
			_this.postInitApps();
		};
	
		this.startApplications(postFunction);
	}
};

/**
 * Called after the application resources have been loaded
 */
WebStudio.Application.prototype.postInitApps = function()
{
	// selects the default 'preview' application
	this.selectApplication('preview');

	// other things
	this.resizeWindow();
};

/**
 * Called after resource loading is completed.
 *
 * This initializes the Alfresco application.
 */
WebStudio.Application.prototype.activate = function() 
{
	// builds all dom elements for the container
	this.buildGeneralLayer();

	// moves all body elements into the body container
	WebStudio.bodyContainer.getChildren().injectInside(WebStudio.app.panels.secondPanel);
	
	var a = WebStudio.app.panels.secondPanel;
	a.setStyle("overflow", "auto");
	
	// tells the application to listen "resize" events of the window
	window.addEvent('resize', function() 
	{
		WebStudio.app.resize();
	});
	
	// TODO: tell the application to listen to scrolling events?
	window.addEvent('scroll', function() {
	});
	
	if(WebStudio.app.panels.secondPanel.addEventListener)
	{
		WebStudio.app.panels.secondPanel.addEventListener('scroll', this.onContentScroll.bind(WebStudio.app.panels.secondPanel), false);
	}
	else
	{
		WebStudio.app.panels.secondPanel.addEvent('scroll', this.onContentScroll.bind(WebStudio.app.panels.secondPanel));	
	}
	
	// initial set up of the panels
	this.panels.minLeftWidth = 230;
	this.panels.minRightWidth = 400;
	
	// updates the floating menu position
	this.updateFloatingMenu();

	// initializes the application overlays
	this.showFloatingMenu();
};

/**
 * Builds the application and applets
 */
WebStudio.Application.prototype.build = function() 
{
	var _this = this;
	
	this.generalLayer.set({
		id: 'AlfrescoApplication'
	});

	this.panels = new WebStudio.Splitter('AlfrescoAppGeneralPanel');
	this.panels.onPanelsResize = function(fs,sz)
	{
		_this.onPanelsResize(fs,sz);
	};
    
    this.panels.injectObject = this.PanelsHolder.el;
	this.panels.activate();
	
	// hide the panels
	if(this.panels)
	{
		this.panels.hidePanel(true);
	}	
	
	// hide the menu
	if(this.activeMenu)
	{
		this.activeMenu.hide();
	}
};




















///////////////////////////////////////////////////////////////////
//
// Embedded Applications
//
///////////////////////////////////////////////////////////////////

WebStudio.Application.prototype.getApplications = function()
{
	return this.applications;
};

WebStudio.Application.prototype.getApplication = function(id)
{
	return this.getApplications()[id];
};

WebStudio.Application.prototype.getApplicationCount = function()
{
	var count = 0;

	for(var appId in this.applicationsConfig)
	{
		if(this.applicationsConfig.hasOwnProperty(appId))
		{
			count++;
		}
	}
	
	return count;
};

WebStudio.Application.prototype.getDefaultApplication = function()
{
	return this.getApplication(this.getDefaultApplicationId());
};

WebStudio.Application.prototype.getCurrentApplication = function()
{
	return this.getApplication(this.currentApplicationId);
};

WebStudio.Application.prototype.getDefaultApplicationId = function()
{
	if (!this.defaultApplicationId)
	{
		this.defaultApplicationId = 'preview';
	}
	
	return this.defaultApplicationId;
};

WebStudio.Application.prototype.setDefaultApplicationId = function(defaultApplicationId)
{
	this.defaultApplicationId = defaultApplicationId;
};

/**
 * Called when a user clicks on a new icon in the
 * applications elector
 **/
WebStudio.Application.prototype.selectApplication = function(appId) 
{
	var _this = this;
	
	if(!appId)
	{
		appId = "off";
	}
	
	if(this.currentApplicationId && this.currentApplicationId == appId)
	{
		// we're already viewing this application, so skip
		return;
	}
	
	// if we're exiting, let's make sure to switch out of edit mode
	if(appId == "off" || appId == "preview")
	{
		if(this.isEditMode())
		{
			this.endEdit();
		}
	}	
	
	// fire the old application unselected method
	var oldApplicationId = this.currentApplicationId;	
	if (oldApplicationId)
	{
		var oldApplication = this.applications[oldApplicationId];
		if (oldApplication && oldApplication.onUnselected)
		{
			oldApplication.onUnselected();
		}
	}	

	// if there is a current application
	// make sure all of its overlay pieces are hidden
	if(this.currentApplicationId && this.isEditMode())
	{
		this.panels.hidePanel(true);
		this.activeMenu.hide();
		
		// move old panel to application panel holding space
		$('ApplicationSplitterPanelHolder').appendChild(this.panels.firstPanel);
	}

	// exit point for switching to preview/off mode
	if(appId == "off" || appId == "preview")
	{
		this.currentApplicationId = null;
		return;	
	}	
	
	// select the application
	var application = this.applications[appId];	
	this.currentApplicationId = appId;

	// build the application overlay pieces
	// but keep them hidden	
	this.slidersSector = application.slidersSector;
	this.activeMenu = application.getMenu();
	this.activeMenu.hide();
	
	// move our panel into the splitter
	// make sure it remains hidden
	this.panels.firstPanel = application["firstPanel"];
	$('AlfSplitterContainer').appendChild(this.panels.firstPanel);
	this.panels.hidePanel(true);	
	
	// show the menu?
	var editMode = this.isEditMode();
	if (editMode)
	{
		// show the menu
		this.activeMenu.show();
		
		// Set up the panel which was newly mounted
		this.panels.firstPanelSize = this.panels.minLeftWidth;
		this.panels.setPanelsSize(this.panels.minLeftWidth);
		//this.panels.firstPanelSize = this.panels.firstPanel.offsetWidth;

		// Show Panels
		this.showSlidersPanel();
	
		// Resize the panel	
		this.resizePanel();		
	}
	
	// fire the application selected method
	application.onSelected();
	
	// refresh all of the designers
	this.onDesignersRefresh();	
};	









WebStudio.Application.prototype.setElementConfig = function(item, index, ob, config, oel)
{
	//'item' = html dom element, 'index' = name of array of 'item's; ob = js container for 'index'; config = js object with configuration for item
	if (config.setNumber) {
		var p = {};
		p[config.setNumber] = index;
		o.el.set(p);
	}

	if (config.dragHandler)
	{
		if (this[config.dragObject]) 
		{
			var x = new Drag.Move(this[config.dragObject].el, {handle: oel});
		}
	}
};

/**
 * Checks whether the user is authenticated to Web Studio and, if not,
 * redirects them to the login page.
 */
WebStudio.Application.prototype.userAuth = function()
{
	var userAuth = WebStudio.context.isAuthenticated();
	
	// ensure the user is authenticated
	if(!userAuth)
	{
		// show the login control
		this.loginDialog = new WebStudio.LoginDialog();
		
		// set up login handler
		this.loginDialog.setLoginHandler(this.loginHandler.bind(this));
		
		// activate and pop up
		this.loginDialog.activate();
		this.loginDialog.popup();
	}
	
	return userAuth;
};

WebStudio.Application.prototype.loginHandler = function(e)
{
	var _this = this;
	
	// call over to login web script
	var url = WebStudio.ws.studio("/api/login", { "u" : this.loginDialog.Username.el.value, "pw" : this.loginDialog.Password.el.value } );
	this.call = YAHOO.util.Connect.asyncRequest('GET', url, {	
		success: function(r) {
		
			var responseText = r.responseText;
			if("AUTHENTICATED" == responseText)
			{
				// they successfully authenticated to web studio
				_this.loginDialog.popout();
				
				// mark that the user signed in
				WebStudio.context.username = _this.loginDialog.Username.el.value;
				
				// re-init the floating menu
				_this.showFloatingMenu();
			}
		
		}
		,
		failure: function(r) {
		
			// TODO: display some kind of message?
			
		}
	});		
};

WebStudio.Application.prototype.sandboxMounted = function()
{
	var sandboxMounted = WebStudio.context.isSandboxMounted();
	
	// ensure the user sandbox is mounted
	if(!sandboxMounted)
	{
		// show the sandbox dialog
		this.sandboxDialog = new WebStudio.SandboxDialog();
		
		// set up the sandbox dialog handlers
		this.sandboxDialog.setWebSiteCreateHandler(this.webSiteCreateHandler.bind(this)); 
		this.sandboxDialog.setWebSiteLoadHandler(this.webSiteLoadHandler.bind(this));
		
		// activate and pop up
		this.sandboxDialog.activate();
		this.sandboxDialog.popup();
	
	}
	
	return sandboxMounted;	
};

WebStudio.Application.prototype.webSiteCreateHandler = function(e)
{
	var _this = this;
	
	var webSiteName = this.sandboxDialog.ToolCreateWebSiteName.el.value;
	var webSiteDescription = webSiteName;
	
	var webSiteBasedOn = "none";
	var select = $('sandbox-create-template-selector');
	if(select)
	{
		webSiteBasedOn = select.value;
	}
	
	// TODO: figure a nice, easy DNS name for this web project
	// We use some voodoo assumptions here...
	var webSiteId = webSiteName.split(' ').join('');
	
	if(webSiteName && webSiteBasedOn && webSiteId)
	{
		// TODO: Change these assumptions
		// Ideally, they should be fetched or computed in a pluggable way
		var _userSandboxId = webSiteId + "--" + WebStudio.context.getCurrentUserId();
		var _userStoreId = webSiteId + "--" + WebStudio.context.getCurrentUserId();		
		
		// call web studio to tell it to create the web project
		var createWebSiteUrl = WebStudio.ws.studio("/api/site/create", { 
			id: webSiteId, 
			name: webSiteName, 
			description: webSiteDescription, 
			basedOn: webSiteBasedOn,
			storeId: _userStoreId,
			sandboxId: _userSandboxId 
		});
		
		var refreshCacheOnComplete = function() 
		{
			// callback once cache finishes refreshing
			var fCallback = function()
			{
				// refresh the page
				_this.refreshBrowser();
				
			};
			
			// refresh all caches
			_this.refreshAll(fCallback);
			
		};
		
		var refreshCacheOnFailure = function() 
		{				
			_this.sandboxDialog.popout();
			
			// TODO			
		};
		
		var allDone = function()
		{
			_this.setContext(WebStudio.context.webProjectId, WebStudio.context.sandboxId, WebStudio.context.storeId, refreshCacheOnComplete, refreshCacheOnFailure);
		};		
		
		// hide the sandbox dialog
		this.sandboxDialog.popout();
		
		// launch the modal
		this.blockNotify("Creating Web Site...");
		
		this.call = YAHOO.util.Connect.asyncRequest('GET', createWebSiteUrl, 
		{	
			success: function(r) 
			{
				// at this point, the web project creation completed
				// the web studio script either created a blank web site or
				// else it kicked off an importer activity.
				//
				// if an import task was kicked off, then we've been given back
				// a ticket and we will need to query for updates until the
				// import has completed
				var d = Json.evaluate(r.responseText);
				
				if(d.status == 'completed')
				{
					// TODO: implement a call to Mark's Web Projects sandbox services
					// retrieve this since implementation of naming conventional may shift

					// set context
					WebStudio.context.webProjectId = webSiteId;
					WebStudio.context.sandboxId = _userSandboxId;
					WebStudio.context.storeId = _userStoreId;

					// finalize and refresh
					allDone();
				}
				else if(d.status == 'importing')
				{
					// TODO: implement a call to Mark's Web Projects sandbox services
					// retrieve this since implementation of naming conventional may shift

					// set context
					WebStudio.context.webProjectId = webSiteId;
					WebStudio.context.sandboxId = _userSandboxId;
					WebStudio.context.storeId = _userStoreId;

					// the task id
					var taskId = d.taskId;

					// url to check the status of the task
					var taskCheckUrl = WebStudio.ws.studio("/api/importer/statuscheck", { taskId: taskId }); 
					
					// we need to wait until the import either succeeds or it fails
					var xf = function()
					{
						var xfCall = YAHOO.util.Connect.asyncRequest('GET', taskCheckUrl,
						{
							success: function(r2)
							{
								var d2 = Json.evaluate(r2.responseText);
								
								var d2_isError = d2.isError;
								var d2_isSuccess = d2.isSuccess;
								var d2_isFinished = d2.isFinished;
								var d2_isRunning = d2.isRunning;
								var d2_status = d2.status;
								
								if(d2_isRunning)
								{
									_this.blockNotify(d2_status);
								}
								
								if(d2_isFinished === true)
								{
									if(d2_isError === true)
									{
										alert("Import ended in an error state");
									}
									if(d2_isSuccess === true)
									{
										// get rid of the periodical
										$clear(xf);
										
										// finalize and refresh
										allDone();										
									}
								}
							}
						});
					};
					
					// set up the xf function to run every 500 ms
					xf.periodical(500);					
				}
				else
				{
					// TODO: uh oh case
				}
			}
			,
			failure: function(r) {
			
				alert('create failed');
						
			}
		});		
	}
};

WebStudio.Application.prototype.webSiteLoadHandler = function(e)
{
	var _this = this;
	
	// selected project id
	var selectedWebSiteId = this.sandboxDialog.ToolLoadWebSiteSelectedId.el.value;	
	if(selectedWebSiteId)
	{
		// TODO: Change these assumptions
		// Ideally, they should be fetched or computed in a pluggable way
		var _userSandboxId = selectedWebSiteId + "--" + WebStudio.context.getCurrentUserId();
		var _userStoreId = selectedWebSiteId + "--" + WebStudio.context.getCurrentUserId();		
	
		// call over and fetch metadata about this web project
		// we must determine it's web project id, its staging sandbox id and its store id
		var webProjectRef = selectedWebSiteId; // dns name
		var url = WebStudio.ws.repo("/api/wcm/webprojects/" + webProjectRef);
	
		var callback = {
	
			success: function(oResponse) {
				
				var d = Json.evaluate(oResponse.responseText);
				var webProjectId = d.data.webprojectref;

				// use our assumed values								
				var sandboxId = _userSandboxId;
				var storeId = _userStoreId;
				
				var _onComplete = function() {

					// activate and pop up
					_this.sandboxDialog.popout();
					
					// callback once cache finishes refreshing
					var fCallback = function()
					{
						// refresh the page
						_this.refreshBrowser();
						
					};
					
					// refresh all caches
					_this.refreshAll(fCallback);
				};
				
				var _onFailure = function() {
				
					_this.sandboxDialog.popout();
					
					// TODO
					
				};

				// do another call to set the context
				_this.setContext(webProjectId, sandboxId, storeId, _onComplete, _onFailure);

			}
			,
			failure: function(oResponse) {
		
				// TODO
			}
			,
			timeout: 7000
		};
		YAHOO.util.Connect.asyncRequest('GET', url, callback);
	}	
};

WebStudio.Application.prototype.setContext = function(webProjectId, sandboxId, storeId, onComplete, onFailure)
{
	var _this = this;
	
	var url = WebStudio.ws.studio("/api/context/set", { webProjectId : webProjectId, sandboxId : sandboxId, storeId : storeId } );

	var callback = {
	
		success: function(oResponse) {
				
			// set up the context
			WebStudio.context.webProjectId = webProjectId;
			WebStudio.context.sandboxId = sandboxId;
			WebStudio.context.storeId = storeId;				
			
			if(onComplete)
			{
				onComplete();
			}

		}
		,
		failure: function(oResponse) {
		
			if(onFailure)
			{
				onFailure();
			}
			else
			{
				alert('set context fail: ' + oResponse.responseText);
			}
		}
		,
		timeout: 7000
	};
	YAHOO.util.Connect.asyncRequest('GET', url, callback);
};

WebStudio.Application.prototype.resetContext = function(callback)
{
	var url = WebStudio.ws.studio("/api/context/set", { webProjectId : "", sandboxId : "", storeId : "" } );
	YAHOO.util.Connect.asyncRequest('GET', url, callback);
};


WebStudio.Application.prototype.loadPage = function(pageId)
{
	window.location.href = "/studio/page?p=" + pageId;
};

WebStudio.Application.prototype.toNodeString = function(nodeRef)
{
	var nodeString = "";
	
	var i = nodeRef.indexOf(":");
	nodeString += nodeRef.substring(0,i);
	nodeString += "/";
	
	var cdr = nodeRef.substring(i+3);
	var i2 = cdr.indexOf("/");
	nodeString += cdr.substring(0, i2);
	nodeString += "/";
	nodeString += cdr.substring(i2+1); 
	
	return nodeString;
};

WebStudio.Application.prototype.getTopOffset = function() 
{
   return 20;
};
















WebStudio.Application.prototype.startApplications = function(postFunction)
{
	var _this = this;
	
	// notify the user with a blocked wait that we're loading apps
	this.blockNotify("Loading Applications...");
	
	// bootstrap all of the applications
	this.bootstrapApplications({

		onSuccess: function() {
		
			_this.mountApplications({
			
				onSuccess: function() {

					_this.clearBlockNotify();
															
					// call back
					if(postFunction)
					{
						postFunction();
					}
					
				}
				,
				onFailure: function() {

					_this.clearBlockNotify();				
					alert("mount applications failed");
					
				}
				
			});
		
		}
		,
		onFailure: function() {
		
			_this.clearBlockNotify();
			alert("Failed to start Applications - bootstrap called failed");
		
		}
		
	});
};

/**
 * Loads and initializes the applications
 * options - object with methods "onSuccess" and "onFailure"
 */
WebStudio.Application.prototype.bootstrapApplications = function(options)
{
	var _this = this;
	
	var config = this.applicationsConfig;
	
	var bootstrap = new WebStudio.Bootstrap(config, this.applications);
	bootstrap.onSuccess = function() 
	{	
		// copy in applets config
		for(var id in config)
		{
			if(config.hasOwnProperty(id))
			{
				var obj = _this.applications[id];
				obj.appletsConfig = WebStudio.app.applicationsConfig[id].applets;
			}			
		}
		
		// mark success
		_this.applicationsBootstrapped = true;
		
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

WebStudio.Application.prototype.mountApplications = function(options)
{
	for(var id in this.applications)
	{
		if(this.applications.hasOwnProperty(id))
		{
			// update notification to the end user
			this.blockNotify("Loading Application: " + this.applications[id].title);	
			this.mountApplication(id);
		}
	}
	
	// set up a timed check for completion
	var totalWaitTime = 180000; // 180 seconds
	var checkPeriod = 500; // check every half second
	this.maxMountCheckCount = totalWaitTime / checkPeriod;
	this.mountCheckCount = 0;
	this.mountCheckerOptions = options;				
	this.mountChecker = this.mountCheck.periodical(checkPeriod, this);		
};

WebStudio.Application.prototype.mountCheck = function()
{
	this.mountCheckCount++;
	
	// check to see if all of the objects have finished bootstrapping
	var check = true;
	for(var id in this.applications)
	{
		if(this.applications.hasOwnProperty(id))
		{
			var app = this.applications[id];
			if(!app.isMounted)
			{
				check = false;
			}
		}
	}			
	
	if(check)
	{
		$clear(this.mountChecker);
		if(this.mountCheckerOptions.onSuccess)
		{
			this.mountCheckerOptions.onSuccess.bind(this).attempt();
		}
		return true;				
	}
	
	if (this.mountCheckCount > this.maxMountCheckCount) 
	{
		$clear(this.mountChecker);
		if(this.mountCheckerOptions.onFailure)
		{
			this.mountCheckerOptions.onFailure.bind(this).attempt();
		}				
		return false;
	}
};
	

WebStudio.Application.prototype.mountApplication = function(appId)
{
	var app = this.applications[appId];
	if(app)
	{
		// store the application config onto the app
		app.config = WebStudio.app.applicationsConfig[appId];
		app.appletsConfig = app.config["applets"];
		
		// configure the application's slider
		var slidersPanelDomId = app.getSlidersPanelDomId();
		var panel = $(slidersPanelDomId);
		app.firstPanel = panel;
		
		// build the body of the application slider
		app.slidersSector = new WebStudio.SlidersSector('AlfrescoAppGeneralSlidersSector_' + appId);
		app.slidersSector.defaultTemplateSelector = 'div[id=' + app.getSlidersSectorTemplateId() + ']';
		app.slidersSector.injectObject = app.firstPanel;
		app.slidersSector.activate();
		
		// mark as mounted
		app.isMounted = true;		
	}
};










/**
 *
 * Event Handlers
 *
 */
 
/**
 * Fired when the application designers need to be refreshed
 */
WebStudio.Application.prototype.onDesignersRefresh = function()
{
	var app = WebStudio.app.getCurrentApplication();
	if (app)
	{
		app.onDesignersRefresh();
	}
};

/**
 * Fired when the panels are resized (via the divider)
 */
WebStudio.Application.prototype.onPanelsResize = function(fs,sz)
{
    var app = this.getCurrentApplication();
    if (app)
    {
    	app.onPanelsResize();
    }
};

/**
 * Common error handler for AJAX processes.
 * @param data {object} the server response.
 */
WebStudio.Application.prototype.onFailure = function(data)
{
	alert("Status: " + data.status + "\n" + "Status text: " + data.statusText);
};



 
 
 
 
 
 
 
 




/**
 *
 * General Action Handlers
 *
 * These methods can either be called directly or they are used
 * by other parts of the application to orchestrate things
 *
 */
 
WebStudio.Application.prototype.refreshAll = function(onComplete, onFailure)
{
	var config = { };
	config.webscripts = true;
	config.objects = true;
	
	if(onComplete)
	{
		config.onComplete = onComplete.bind(this);
	}
	
	if(onFailure)
	{
		config.onFailure = onFailure.bind(this);
	}
	
	this.refresh(config);
};

WebStudio.Application.prototype.refreshObjectCache = function(onComplete, onFailure)
{
	var config = { };
	config.webscripts = false;
	config.objects = true;
	config.silent = true;
	
	if(onComplete)
	{
		config.onComplete = onComplete;
	}
	
	if(onFailure)
	{
		config.onFailure = onFailure;
	}
	
	this.refresh(config);
};

WebStudio.Application.prototype.refreshWebScripts = function(onComplete, onFailure)
{
	var config = { };
	config.webscripts = true;
	config.objects = false;
	
	if(onComplete)
	{
		config.onComplete = onComplete;
	}
	
	if(onFailure)
	{
		config.onFailure = onFailure;
	}
	
	this.refresh(config);
};

WebStudio.Application.prototype.refresh = function(options)
{
	var _this = this;
	
	var webscripts = options.webscripts;
	var objects = options.objects;
		
	// deep copy object
	var newOptions = { };
	newOptions.webscripts = options.webscripts;
	newOptions.objects = options.objects;
	newOptions.onComplete = options.onComplete;
	newOptions.onFailure = options.onFailure;

	// redirect back to this method
	var myOnComplete = this.refresh.bind(this);
	
	var myOnFailure = function()
	{
		// unblock
		_this.clearBlockNotify();
		
		if(options.onFailure)
		{
			options.onFailure();
		}
		
	};

	if(webscripts)
	{
		if(!options.silent)
		{
			this.blockNotify("Refreshing Web Scripts...");
		}
		
		newOptions.webscripts = null;
		
		this._refreshWebScripts(newOptions, myOnComplete, myOnFailure);
	}
	else if(objects)
	{
		if(!options.silent)
		{
			this.blockNotify("Refreshing Object Cache...");
		}
		
		newOptions.objects = null;
		
		this._refreshObjects(newOptions, myOnComplete, myOnFailure);
	}
	else {

		// otherwise, we have completed
		this.clearBlockNotify();
		
		if(options.onComplete)
		{
			options.onComplete();
		}
	}
};

WebStudio.Application.prototype._refreshWebScripts = function(options, onComplete, onFailure)
{
	var _this = this;
	
	var time = new Date();
	
	var url = WebStudio.url.studio("/control/webscripts/reset", { '_dc' : time.getTime() } );
	var ajax = new Ajax(url, 
	{
		method: 'get', 
		onComplete: function(data)
		{
			onComplete(options);
		}
		,
		onFailure: function() 
		{
			onFailure(options);
		}
	}).request();	
};

WebStudio.Application.prototype._refreshObjects = function(options, onComplete, onFailure)
{
	var _this = this;
	var time = new Date();
	
	var url = WebStudio.url.studio("/control/cache/invalidate", { '_dc' : time.getTime() } );
	var ajax = new Ajax(url, 
	{
		method: 'get', 
		onComplete: function(data)
		{
			onComplete(options);
		}
		,
		onFailure: function() 
		{
			onFailure(options);
		}
	}).request();
};





/**
 * Repositions the Alfresco Content Edit floating menu
 */
WebStudio.Application.prototype.updateFloatingMenu = function() 
{
	var top = this.getWindowSize().h - 60;
	var left = 20;
	
	// if we're in "edit mode", then reposition the floating menu to mount
	// to the lower left-hand corner
	if(this.isEditMode())
	{
		top = this.getWindowSize().h - 60;
		left = 20;
	}
	
	this.FloatingMenuControl.el.setStyles({
		top: top,
		left: left
	});
	
	var floatingMenuIcon = $('FloatingMenuIcon');	
		
	// set up the floating caption
	this.updateFloatingMenuCaption();
		
	// set up the floating icon
	var iconPath = "/images/floatingmenu/webstudio-preview-64.png";
	if(this.currentApplicationId)
	{
		iconPath = WebStudio.app.applicationsConfig[this.currentApplicationId].imageUrl;
	}
	floatingMenuIcon.setStyle("background-repeat", "no-repeat");
	floatingMenuIcon.setStyle("background-image", "url(" + WebStudio.overlayPath + iconPath +")");
	
	if(this.isEditMode())
	{
		floatingMenuIcon.removeClass("FloatingMenuIconView");
		floatingMenuIcon.addClass("FloatingMenuIconEdit");
	}
	else
	{
		floatingMenuIcon.removeClass("FloatingMenuIconEdit");
		floatingMenuIcon.addClass("FloatingMenuIconView");
	}
	
	// position the icon
	floatingMenuIcon.setStyles({
		top: top - 15,
		left: left - 15,
		width: '64px',
		height: '64px'
	});
	
	// stupid IE tricks
	// set up some widths (for ie)
	if(window.ie)
	{
		var _w = this.FloatingMenuControl[0].FloatingMenuTitle.el.offsetWidth;
		if(this.FloatingMenuControl[0].FloatingMenuSandboxId.el.offsetWidth > _w)
		{
			_w = this.FloatingMenuControl[0].FloatingMenuSandboxId.el.offsetWidth;
		}
		this.FloatingMenuControl.el.setStyle("width", _w + 64);
	}
	
	// update the z-index to bring it to the top
	this.FloatingMenuControl.el.setStyle('z-index', WebStudio.WindowsZIndex + 1);
	WebStudio.WindowsZIndex++;					
	floatingMenuIcon.setStyle('z-index', WebStudio.WindowsZIndex + 1);
	WebStudio.WindowsZIndex++;
};

/**
 * Initializes the floating menu display
 */
WebStudio.Application.prototype.showFloatingMenu = function()
{
	// default, hide stuff
	this.FloatingMenuControl.el.style.display = "none";
	$('FloatingMenuIcon').style.display = "none";
	
	// Check whether the user is authenticated
	if(this.userAuth())
	{		
		// Check whether we've picked a sandbox
		if(this.sandboxMounted())
		{
			this.FloatingMenuControl.el.style.display = "";
			$('FloatingMenuIcon').style.display = "block";
		}
	}
};

/**
 * Toggles between editing mode and non-editing mode
 */
WebStudio.Application.prototype.toggleEdit = function() 
{
	if (this.incontextMode == 'view')
	{
		this.startEdit();
	}
	else
	{
		this.endEdit();
	}
};

/**
 * Flips the application into 'editing' mode
 * This brings up the application overlays
 */
WebStudio.Application.prototype.startEdit = function() 
{
	var _this = this;
	
	if (!this.currentApplicationId)
	{
		return;
	}

	/**
	 * Called after the panels slide into place
	 */	
	var callback = function()
	{
		// show the menu
		_this.activeMenu.show();

		// set to edit mode
		_this.incontextMode = 'edit';

		// set up the panels
		_this.panels.generalLayer.setStyle('margin-top', _this.getTopOffset());
		_this.panels.setHeight(_this.panels.getHeight() - _this.getTopOffset());		
		_this.panels.firstPanelSize = _this.panels.minLeftWidth;
		_this.panels.setPanelsSize(_this.panels.minLeftWidth);
		_this.onPanelsResize(WebStudio.app.panels.firstPanel.offsetWidth, WebStudio.app.panels.secondPanel.offsetWidth);		
				
		// update the floating menu
		_this.updateFloatingMenu();	
		
		// fire the 'start edit' method if available
		var application = _this.getCurrentApplication();
		if (application && application.onStartEdit)
		{
			application.onStartEdit();
		}	
		
		// refresh designers
		_this.onDesignersRefresh();
	};

	
	this.slideInPanels(callback);
};

/**
 * Flips the application into 'non-editing' mode
 * This turns off the application overlays
 */
WebStudio.Application.prototype.endEdit = function() 
{
	var _this = this;
	
	/**
	 * Called after the panels slide out of place
	 */		
	var callback = function()
	{
		// hide the menu
		_this.activeMenu.hide();

		// set to view mode
		_this.incontextMode = 'view';
		
		// set up the panels		
		_this.panels.generalLayer.setStyle('margin-top', 0);
		_this.panels.setHeight(_this.panels.getHeight() + _this.getTopOffset());
		_this.PanelsHolder.el.style.top = "0px";
		_this.resizePanel();
			
		// update floating menu
		_this.updateFloatingMenu();
	
		// fire the 'end edit' method if available
		var application = _this.getCurrentApplication();
		if (application && application.onEndEdit)
		{
			application.onEndEdit();
		}	
		
		// refresh designers
		_this.onDesignersRefresh();		
	};
	
	this.slideOutPanels(callback);
};

/**
 * Sets the text for the edit control
 */
WebStudio.Application.prototype.updateFloatingMenuCaption = function() 
{
	// update the web project id
	var webProjectId = WebStudio.context.getWebProjectId();
	this.FloatingMenuControl[0].FloatingMenuSandboxId.el.setHTML(webProjectId);

	// figure out what we're talking about
	var thing = null;
	if(this.currentApplicationId == "content")
	{
		thing = "Content Editor";
	}
	else if(this.currentApplicationId == "surfassemble")
	{
		thing = "Page Assembly Tools";
	}
	else if(this.currentApplicationId == "surfsite")
	{
		thing = "Surf Administrator";
	}
		
	// update the title
	var title = null;
	if (thing)
	{
		title = "<img src='" + WebStudio.overlayImagesPath + "/dt-arrow-up.png'/>";
		if(this.isViewMode())
		{
			title += "Show " + thing;
		}
		else
		{
			title += "Hide " + thing;
		}
		
		this.FloatingMenuControl[0].FloatingMenuTitle.el.setStyle("cursor", "pointer");
		this.FloatingMenuControl[0].FloatingMenuTitle.el.setStyle("color", "#000000");
		this.FloatingMenuControl[0].FloatingMenuTitle.el.setStyle("cursor", "pointer");
	}
	else
	{
		title = "Preview";
		this.FloatingMenuControl[0].FloatingMenuTitle.el.setStyle("color", "#555555");
	}
	this.FloatingMenuControl[0].FloatingMenuTitle.el.setHTML(title);
	
	// update the info
	var sandboxId = WebStudio.context.getSandboxId();
	if(sandboxId)
	{
		var infoText = "Staging";
		if(sandboxId.indexOf("--") > -1)
		{
			infoText = "User Sandbox";
		}
		this.FloatingMenuControl[0].FloatingMenuInfo.el.setHTML(infoText);
	}
};

/**
 * Toggles the display of the left-hand panel
 */
WebStudio.Application.prototype.toggleSlidersPanel = function() 
{
	if (!this.isHideDockingPanel) {
		this.hideSlidersPanel();
	} else {
		this.showSlidersPanel();
	}

	this.onPanelsResize(WebStudio.app.panels.firstPanel.offsetWidth,WebStudio.app.panels.secondPanel.offsetWidth);
	return this;
};

/**
 * Hides the left-hand panel
 */
WebStudio.Application.prototype.hideSlidersPanel = function() 
{
	this.panels.hidePanel(true);
	this.isHideDockingPanel = true;
	
	// inform the current application
	var app = this.getCurrentApplication();
	if (app)
	{
		app.onSlidersPanelHide();
	}
    
	// updates the floating menu position
	this.updateFloatingMenu();
    		
	return this;
};

/**
 * Shows the left-hand panel
 */
WebStudio.Application.prototype.showSlidersPanel = function() 
{
	this.panels.showPanels();
	this.isHideDockingPanel = false;

	// inform current application
	var app = this.getCurrentApplication();
	if (app)
	{
		app.onSlidersPanelShow();
	}
    
    // refresh the floating menu
    this.showFloatingMenu();    

	return this;
};

WebStudio.Application.prototype.isDockingPanelHidden = function()
{
	return this.isHideDockingPanel;
};

/**
 * Tells the application to recalculate all resize dependencies
 */
WebStudio.Application.prototype.resize = function() 
{
	this.resizePanel();
	this.resizeSlidersSector();
	this.updateFloatingMenu();
    this.resizeWindow();
};

WebStudio.Application.prototype.resizeWindow = function () 
{
    WebStudio.app.generalLayer.setStyles({
        'width' : this.getWindowSize().w,
        'height' : this.getWindowSize().h
    });
    
    // inform the current application
    var app = this.getCurrentApplication();
    if (app)
    {
    	app.onResizeWindow();
    }
};

WebStudio.Application.prototype.resizePanel = function() 
{
    var h = this.getWindowSize().h;
    var w = this.getWindowSize().w;

    this.panels.setWidth(w);
	this.panels.setHeight(h);
	
	this.onPanelsResize(WebStudio.app.panels.firstPanel.offsetWidth,WebStudio.app.panels.secondPanel.offsetWidth);
        
    return this;
};

WebStudio.Application.prototype.resizeSlidersSector = function() 
{
	if(this.slidersSector)
	{
		this.slidersSector.reOpenActiveSlider();
	}
};

WebStudio.Application.prototype.refreshBrowser = function()
{
	window.location.reload(true);
};

WebStudio.Application.prototype.blockNotify = function(msg)
{
	if(!this.blockNotifyModal)
	{
		this.blockNotifyModal = new YAHOO.widget.Panel("blockNotify", {
			fixedcenter: true,
			close: false, 
			draggable: false,
			width: 250,
			height: 0,
			modal: true,
			visible: false,
			effect:{effect:YAHOO.widget.ContainerEffect.FADE, duration:0.5} 			
		});
	
		this.blockNotifyModal.setHeader(msg);
	    this.blockNotifyModal.setBody("<img src='" + WebStudio.overlayImagesPath + "/rel_interstitial_loading.gif'/>");
	    this.blockNotifyModal.render($(document.body));
	}
	else
	{
		this.blockNotifyModal.setHeader(msg);
	}
	
	// move to center
	this.blockNotifyModal.center();
	this.blockNotifyModal.show();
};

WebStudio.Application.prototype.clearBlockNotify = function()
{
	if(this.blockNotifyModal)
	{
		this.blockNotifyModal.hide();
	}
};

WebStudio.Application.prototype.onMenuItemClick = function(appId, id)
{
	var _this = this;
	
	if(!this.applicationsBootstrapped)
	{
		var postFunction = function()
		{
			_this.startEdit();
			
			var app = _this.getApplication(appId);
			if(app)
			{
				app.onMenuItemClick(id);
			}	
		};

		this.startApplications(postFunction);
	}
	else
	{
		var app = this.getApplication(appId);
		if(app)
		{
			app.onMenuItemClick(id);
		}	
	}
};

WebStudio.Application.prototype.openBrowser = function(name, url)
{
	Alf.openBrowser(name, url);
};

WebStudio.Application.prototype.onContentScroll = function(ev)
{
	var left = 0;
	var top = 0;
	
	if(window.ie)
	{
		left = this.scrollLeft;
		top = this.scrollTop;
	}
	else
	{	
		left = ev.explicitOriginalTarget.scrollLeft;
		top = ev.explicitOriginalTarget.scrollTop;
	}

	// get the current application
	var app = WebStudio.app.getCurrentApplication();
	if (app)
	{
		app.onContentScroll(left, top);
	}	
};

WebStudio.Application.prototype.getApplicationSelector = function()
{
	var el = $('FloatingMenuSelector');
		
	if(!el)
	{
		var floatingMenuControl = $('FloatingMenuControl');
		
		// the total number of applications
		var totalAppCount = WebStudio.app.getApplicationCount() + 1;

		// create the container element
		el = Alf.createElement("div", "FloatingMenuSelector");
		Alf.injectInside($(document.body), el);		
			
		var html = "<table border='0' cellpadding='0' cellspacing='0'>";
		html += "<tr>";
		
		var buildIconHtml = function(appId, appTitle, appImageUrl)
		{
			var imageSize = 56;
			var cellSize = 60;
			
			var _html = "<td width='" + cellSize + "px'>";
			_html += "<div id='mag_app_" + appId + "' style='height: " + cellSize + "px'>";
			_html += "<img id='app_" + appId + "' src='" + appImageUrl + "' width='" + imageSize + "px' height='" + imageSize + "px' title='" + appTitle + "' />";
			_html += "</div>";
			_html += "</td>";
		
			return _html;
		};
		
		// add in applications
		for(var appId in WebStudio.app.applicationsConfig)
		{
			if(WebStudio.app.applicationsConfig.hasOwnProperty(appId))
			{
				var appConfig = WebStudio.app.applicationsConfig[appId];
				
				var appTitle = appConfig.title;
				var appDescription = appConfig.description;
				var appImageUrl = WebStudio.overlayPath + appConfig.imageUrl;
				
				html += buildIconHtml(appId, appTitle, appImageUrl);
			}
		}
		
		// add in the preview item
		html += buildIconHtml("preview", "Preview", WebStudio.overlayPath + "/images/floatingmenu/webstudio-preview-64.png");
				
		html += "</tr>";
		html += "</table>";
		
		el.setHTML(html);

		// move the element into place
		var floatingMenuIcon = $('FloatingMenuIcon');		
		el.setStyle('top', floatingMenuIcon.style.top);
		el.setStyle('left', floatingMenuIcon.style.left);
				
		// helper function to init magnifiers
		var initMagnifier = function(appId, appTitle)
		{
			jQuery("#app_" + appId).hoverpulse({
				size: 20,
				speed: 400
			});

			jQuery("#mag_app_" + appId).click(function(e)
			{
	   			e = new Event(e);
	   		
	   			var selectedAppId = this.id.substring(8);	

   				// hide the application selector
   				WebStudio.app.hideApplicationSelector();

   				// select the new application
   				WebStudio.app.selectApplication(selectedAppId);
   				
   				// make sure the icon stays hidden
   				var floatingMenuIcon = $('FloatingMenuIcon');
   				floatingMenuIcon.setStyles({ display: "none" });
   				
   				// update the floating menu
   				WebStudio.app.updateFloatingMenu();
	   			
				e.stop();    			
	   		});
		   	
		   	jQuery("#mag_app_" + appId).mouseover(function(){
		   		
		   		// create a text div if it doesn't already exist
		   		var textDiv = jQuery("#map_app_" + appId + "_text");
		   		if (textDiv.length === 0)
		   		{
		   			var textDivEl = Alf.createElement("div", "map_app_" + appId + "_text");
		   			Alf.setHTML(textDivEl, appTitle);
		   			Alf.injectInside($(document.body), textDivEl);
		   			
		   			//var left = jQuery(this).offset().left;
		   			var left = jQuery("#FloatingMenuControl").offset().left;
		   			var top = jQuery("#FloatingMenuControl").offset().top - 60;
		   				   			
		   			textDiv = jQuery(textDivEl);
		   			textDiv.css({ display : "block" });
		   			textDiv.css({ position : "absolute" });
		   			textDiv.css({ left : left });
		   			textDiv.css({ top : top });	   			
		   			textDiv.css({ "z-index" : 99999});
		   			textDiv.css({ "font-size" : "18px" });
		   			textDiv.css({ "font-family" : "tahoma,verdana,helvetica" });
		   			textDiv.css({ "font-weight" : "bold" });
		   		}
		   	});
	
		   	jQuery("#mag_app_" + appId).mouseout(function(){
		   		
		   		var textDiv = jQuery("#map_app_" + appId + "_text");
		   		if (textDiv.length > 0)
		   		{
		   			textDiv.remove();
		   		}
		   	});

		};
		
		// init magnifiers on application selector
		el.setStyles({ display: "block" });
		for(var _appId in WebStudio.app.applicationsConfig)
		{
			if(WebStudio.app.applicationsConfig.hasOwnProperty(_appId))
			{
				var _appDescription = WebStudio.app.applicationsConfig[_appId].description;
				initMagnifier(_appId, _appDescription);
			}
		}	
		
		// init the preview magnifier
		initMagnifier("preview", "Switch to Preview Mode (Off)");
		el.setStyles({ display: "none" });
	}
	
	return el;
};

WebStudio.Application.prototype.getApplicationSelectorWidth = function()
{
	var totalAppCount = WebStudio.app.getApplicationCount() + 1;
	return totalAppCount * 60;
};

WebStudio.Application.prototype.showApplicationSelector = function()
{
	// hide the floating menu icon
	var floatingMenuIcon = $('FloatingMenuIcon');
	floatingMenuIcon.setStyles({ display: "none" });
	
	// get the application selector
	var appSelector = $('FloatingMenuSelector');
	
	// get the floating menu control
	var floatingMenuControl = $('FloatingMenuControl');
		
	// widen the floating menu
	floatingMenuControl.setStyles({ display: "block" });	
	var fmChange = new Fx.Style($('FloatingMenuIconSpacer'), 'width', {
		duration: 300,
		onComplete: function() {
		}
	});
	fmChange.start(48, this.getApplicationSelectorWidth());
	
	// widen the application selector
	appSelector.setStyles({ width: 0 });
	appSelector.setStyles({ display: "block" });
	var asChange = new Fx.Style($(appSelector), 'width', {
		duration: 300,
		onComplete: function() {

			// show the application selector
			appSelector.setStyles({ display: "block" });
			
		}
	});
	asChange.start(0, this.getApplicationSelectorWidth());
};

WebStudio.Application.prototype.hideApplicationSelector = function(doUpdateFloatingMenu)
{
	// the application selector
	var appSelector = this.getApplicationSelector();

	// the floating menu icon	
	var floatingMenuIcon = $('FloatingMenuIcon');

	// helper function to remove magnifiers	
	var removeMagnifier = function(appId)
	{
		jQuery("#mag_app_" + appId).magnifier("destroy");
	};
	
	// hide the application selector
	appSelector.setStyles({ display: "none" });
	
	var totalAppCount = WebStudio.app.getApplicationCount() + 1;
	
	// shrink the floating menu
	var fmChange = new Fx.Style($('FloatingMenuIconSpacer'), 'width', {
		duration: 300,
		onComplete: function() {
		}
	});
	fmChange.start(this.getApplicationSelectorWidth(), 48);
	
	// shrink the application selector
	appSelector.setStyles({ display: "block" });
	var asChange = new Fx.Style($(appSelector), 'width', {
		duration: 300,
		onComplete: function() {

			// hide the application selector
			appSelector.setStyles({ display: "none" });
			
			// show the floating menu icon
			floatingMenuIcon.setStyles({ display: "block" });					
		}
	});
	asChange.start(this.getApplicationSelectorWidth(), 0);
	
};

WebStudio.Application.prototype.toggleApplicationSelector = function()
{
	var el = this.getApplicationSelector();
	
	if(el.style.display == "block")
	{
		// hide it
		this.hideApplicationSelector();
	}
	else
	{
		// show it
		this.showApplicationSelector();
	}
};

WebStudio.Application.prototype.isEditMode = function()
{
	return (this.incontextMode == 'edit');
};

WebStudio.Application.prototype.isViewMode = function()
{
	return (this.incontextMode == 'view');
};


WebStudio.Application.prototype.slideInPanels = function (callback) 
{
	var _this = this;
	
	var cb = function()
	{
		_this.showSlidersPanel();		
		_this.onPanelsResize();
		
		callback();
	};
	
	_this.hideSlidersPanel();
	
	jQuery(this.panels.firstPanel).animate({ 
		width: this.panels.firstPanelSize 
	}, 300, "swing", cb);
};

WebStudio.Application.prototype.slideOutPanels = function (callback) 
{
	var _this = this;
	
	var cb = function()
	{
		_this.hideSlidersPanel();

		_this.onPanelsResize();
		
		callback();
	};
	
	_this.showSlidersPanel();
	
	jQuery(this.panels.firstPanel).animate({ 
		width: 0 
	}, 300, "swing", cb);	
};

WebStudio.Application.prototype.getMenuHeight = function()
{
	var height = 0;
	
	if (this.isEditMode())
	{
		height = $('AlfMenuTemplate').offsetHeight;
	}
	
	return height;
};







/**
 TO BE REMOVED
 **/
 
WebStudio.Application.prototype.showContentTypeAssociationsDialog = function()
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

WebStudio.Application.prototype.showTemplateAssociationsDialog = function()
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

WebStudio.Application.prototype.GoToTemplateDisplay = function(templateId)
{
	// switch to the "surfsite" app
	this.selectApplication("surfsite");
	
	// get the current app
	var app = this.getCurrentApplication();
	
	// go to the template display
	app.GoToTemplateDisplay(templateId);
};
