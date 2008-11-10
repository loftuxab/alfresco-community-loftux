<%
	String overlayPath = org.alfresco.web.studio.OverlayUtil.getOriginalURL(request, "/proxy/alfresco-web-studio/overlay/default");
	String iconsPath = overlayPath + "/images/icons";
%>

WebStudio.Applications.Abstract = new Class({
  initialize: function(id, title, description) {
	this.id = id;
	this.title = title;
	this.description = description;
	
	this.isInitialized = false;
	
	this.applets = { };
  }
});

WebStudio.Applications.Abstract.prototype.getId = function()
{
	return this.id;
}

WebStudio.Applications.Abstract.prototype.getTitle = function()
{
	var title = this.title;
	if(!title)
	{
		title = this.getId();
	}
	return title;
}

WebStudio.Applications.Abstract.prototype.getDescription = function()
{
	var desc = this.description;
	if(!desc)
	{
		desc = this.getTitle();
	}
	return desc;
}

WebStudio.Applications.Abstract.prototype.toString = function()
{
	return this.id + "," + this.title + "," + this.description;
}

/**
 * Tells the web application to initialize
 * This informs the app to load any downstream dependencies
 */
WebStudio.Applications.Abstract.prototype.init = function(onInit)
{
	// load and initialize the applets
	this.bootstrapApplets({
	
		onSuccess: (function() {
		
			// flag that initialization was successful
			this.isInitialized = true;
			
			// call to onInit method
			if(onInit)
			{
				onInit.bind(this).attempt();
			}			
			
		}).bind(this)
		,
		onFailure: (function() {
		
			// TODO
			
		}).bind(this)
	});
}

/**
 * Loads and initializes the applets
 * options - object with methods "onSuccess" and "onFailure"
 */
WebStudio.Applications.Abstract.prototype.bootstrapApplets = function(options)
{
	var config = WebStudio.app.applicationsConfig[this.getId()].applets;
	
	var bootstrap = new WebStudio.Bootstrap(config, this.applets);
	bootstrap.onSuccess = (function() 
	{	
		// set a pointer back to the application
		// mount sliders
		var count = 0;
		for(var id in config)
		{
			var obj = this.applets[id];
			obj.app = this;
			
			if(obj.mountSlider)
			{
				obj.mountSlider(this.slidersSector.Sliders[count], this.slidersSector.Sliders[count].Data.el);
			}
			
			count++;
		}
		
		// mark success
		this.appletsBootstrapped = true;
		
		if(options.onSuccess)
		{
			options.onSuccess.bind(this).attempt();
		}		
				
	}).bind(this);
	bootstrap.onFailure = (function() 
	{	
		this.applicationsBootstrapped = false;
		
		if(options.onFailure)
		{
			options.onFailure.bind(this).attempt();
		}	
		else
		{
			alert('failed to bootstrap');
		}					
	}).bind(this);
	bootstrap.load();
}

WebStudio.Applications.Abstract.prototype.getApplet = function(appletId)
{
	var applet = null;
	for(var key in this.applets)
	{
		if(key == appletId)
		{
			applet = this.applets[key];
			break;
		}
	}
	
	return applet;
}

WebStudio.Applications.Abstract.prototype.showApplet = function(appletId)
{
	var index = -1;
	var count = 0;
	for(var key in this.applets)
	{
		if(key == appletId)
		{
			index = count;
			break;
		}
		count++;	
	}
	
	if (this.slidersSector.SlidersData[index].state != 'show')
	{
		this.slidersSector.toggleSliderData(index);
	}
}

WebStudio.Applications.Abstract.prototype.getMenu = function()
{
	// ABSTRACT
	return null;
}

WebStudio.Applications.Abstract.prototype.getTabTitle = function()
{
	// ABSTRACT
	return null;
}

WebStudio.Applications.Abstract.prototype.getTabImageUrl = function()
{
	// ABSTRACT
	return null;
}

WebStudio.Applications.Abstract.prototype.getSlidersSectorTemplateId = function()
{
	// ABSTRACT
	return null;
}

WebStudio.Applications.Abstract.prototype.getSlidersPanelDomId = function()
{
	// ABSTRACT
	return null;
}

WebStudio.Applications.Abstract.prototype.onMenuItemClick = function(index,data)
{
	// ABSTRACT
	return null;
}

WebStudio.Applications.Abstract.prototype.resize = function()
{
}