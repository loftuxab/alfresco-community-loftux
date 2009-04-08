WebStudio.Applets.Abstract = new Class({
  initialize: function(id, title, description) {
	this.id = id;
	this.title = title;
	this.description = description;
	
	this.isInitialized = false;
  }
});

WebStudio.Applets.Abstract.prototype.getDependenciesConfig = function()
{
	return {};
};

WebStudio.Applets.Abstract.prototype.getId = function()
{
	return this.id;
};

WebStudio.Applets.Abstract.prototype.getTitle = function()
{
	var title = this.title;
	
	if(!title)
	{
		title = this.getId();
	}
	
	return title;
};

WebStudio.Applets.Abstract.prototype.getDescription = function()
{
	var desc = this.description;
	
	if(!desc)
	{
		desc = this.getTitle();
	}
	
	return desc;
};

WebStudio.Applets.Abstract.prototype.toString = function()
{
	return this.id + "," + this.title + "," + this.description;
};

WebStudio.Applets.Abstract.prototype.init = function(onInit)
{
	var dependenciesConfig = this.getDependenciesConfig();
	
	if(!dependenciesConfig)
	{
		// skip this
		this.isInitialized = true;
	}
	else
	{
		// load all dependencies
		var _this = this;
		this.bootstrapDependencies(
		{		
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
			onFailure: function() 
			{			
				// TODO
				
			}
		});
	}
};

/**
 * Loads all applet dependencies
 */
WebStudio.Applets.Abstract.prototype.bootstrapDependencies = function(options)
{
	var dependenciesConfig = this.getDependenciesConfig();
	
	var _this = this;
	
	var bootstrap = new WebStudio.Bootstrap(dependenciesConfig);
	bootstrap.onSuccess = function() 
	{	
		// mark success
		_this.dependenciesBootstrapped = true;
		
		if(options.onSuccess)
		{
			options.onSuccess.bind(_this).attempt();
		}		
						
	};
	bootstrap.onFailure = function() 
	{	
		// mark failure
		_this.dependenciesBootstrapped = false;
		
		if(options.onFailure)
		{
			options.onFailure.bind(_this).attempt();
		}	
		else
		{
			alert('failed to bootstrap applet dependencies');
		}					
	};
	
	bootstrap.load();
};


WebStudio.Applets.Abstract.prototype.mountSlider = function(slider, container) 
{
	this.container = container;
	
	this.container.setStyle('overflow', 'auto');
	this.container.setHTML('');

	var tElement = this.container.getParent().getElementsBySelector(".ASSHeaderTitle");
	if(tElement)
	{
		tElement.setHTML(this.getTitle());
	}
	
	// bind in the control to the container;
	try
	{
		this.bindSliderControl(container);
	}
	catch(err)
	{
		alert("error while mounting applet '" + this.getId() + "' : " + err);
	}	
	
	// bind onShowSlider
	if(this.onShowSlider)
	{
		slider.onShowSlider = this.onShowSlider.bind(this);
	}

	// bind onHideSlider
	if(this.onHideSlider)
	{
		slider.onHideSlider = this.onHideSlider.bind(this);
	}
};

// useful for creating new dom control templates
WebStudio.Applets.Abstract.prototype.instantiateControlTemplate = function(controlId, templateId)
{
	// clone a template
	var newTemplate = $(templateId).clone(true);
	newTemplate.id = templateId + "_" + controlId;
	newTemplate.injectInside($('ControlInstances'));

	return newTemplate;
};

WebStudio.Applets.Abstract.prototype.bindSliderControl = function(container)
{
	// TODO: implement
};

WebStudio.Applets.Abstract.prototype.getApplication = function()
{
	var theApplication = null;
	
	if (this.app)
	{
		theApplication = this.app;
	}
	else
	{
		theApplication = WebStudio.app.getCurrentApplication();
	}
	
	return theApplication;
};

WebStudio.Applets.Abstract.prototype.getContainer = function()
{
	return WebStudio.app;
};

/*
 * EVENTS
 */
WebStudio.Applets.Abstract.prototype.onClose = function()
{
};

WebStudio.Applets.Abstract.prototype.onShowSlider = function()
{
	this.getApplication().setActiveAppletId(this.getId());
	
	if(this.onShowApplet)
	{
		this.onShowApplet();
	}
};

WebStudio.Applets.Abstract.prototype.onHideSlider = function()
{
	if(this.onHideApplet)
	{
		this.onHideApplet();
	}
	
	this.getApplication().setActiveAppletId(null);
};
