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
	// ABSTRACT
	return null;
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

WebStudio.Applications.Abstract.prototype.onMenuItemClick = function(index,data)
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

WebStudio.Applications.Abstract.prototype.onContentScroll = function(left, top)
{
};

WebStudio.Applications.Abstract.prototype.onSelected = function()
{
};

WebStudio.Applications.Abstract.prototype.onPanelsResize = function()
{
};

WebStudio.Applications.Abstract.prototype.onEndEdit = function()
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