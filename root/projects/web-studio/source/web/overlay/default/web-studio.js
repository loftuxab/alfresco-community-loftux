// Ensure WebStudio root object exists
if (typeof WebStudio == "undefined" || !WebStudio)
{
	WebStudio = {};
}

// Set up jQuery so that it will not conflict with $ variable
jQuery.noConflict();

// Let jQuery use the $j variable
var $j = jQuery;

/**
 * Register namespace function
 */
WebStudio.registerNS = function(ns)
{
	var nsParts = ns.split(".");
	var root = window;

	for(var i = 0; i < nsParts.length; i++)
	{
		if(typeof root[nsParts[i]] == "undefined")
		{
   			root[nsParts[i]] = {};
   		}

		root = root[nsParts[i]];
	}
};

WebStudio.registerNS("WebStudio.Applications");
WebStudio.registerNS("WebStudio.Applets");
WebStudio.registerNS("WebStudio.Fx");
WebStudio.registerNS("WebStudio.Templates.Model");


/**
 * WebStudio top-level constants namespace.
 * 
 * @namespace WebStudio
 * @class WebStudio.constants
 */
WebStudio.constants = WebStudio.constants || {
	ABC: 1
};

/**
 * WebStudio top-level util namespace.
 * 
 * @namespace WebStudio
 * @class WebStudio.util
 */
WebStudio.util = WebStudio.util ||  {

	stopPropagation : function(e)
	{
		if(window.ie)
		{
			window.event.cancelBubble = true;
		}
		else
		{
			e.stopPropagation();
		}
	}
	,	
	injectInside : function(target, element)
	{
		Alf.injectInside(target, element);
	}
	,
	setStyle : function(targetElement, styleName, value)
	{
		if(window.ie)
		{
			targetElement.style[styleName] = value;
		}
		else
		{
			targetElement.setStyle(styleName, value);
		}
	}
	,
	pushHTML : function(target, html)
	{
		Alf.setHTML(target, html);
	}
	,
	clone: function(el)
	{
		return $(Alf.clone(el));	
	}	
	,	
	setHTML : function(node, html, useChildNodes)
	{
		if(window.ie)
		{
			this._setHTMLIE(node, html, useChildNodes);
		}
		else
		{
			this._setHTML(node, html, useChildNodes);
		}
	}
	,	
	_setHTML : function(node, html, useChildNodes)
	{
		// This method will work on most browsers
		
		var r = node.ownerDocument.createRange();
		r.setStartBefore(node);
		
		var parsedHTML = r.createContextualFragment(html);
		
		//remove all children
		for (var i = 0; i < node.childNodes.length; i++) 
		{
      		node.removeChild(node.childNodes[i]);
		}

		// add the new one
		if(useChildNodes && useChildNodes === true)
		{
			var htmlToSet = parsedHTML.childNodes[0].innerHTML;
			node.setHTML(htmlToSet);
		}
		else
		{
			node.appendChild(parsedHTML);
		}
	}
	,
	_setHTMLIE : function(node, html, useChildNodes)
	{
		// This method will work on IE
		
		node.insertAdjacentHTML('beforeEnd', html);
		
		// remove all children except for the new one
		for (var i = 0; i < node.childNodes.length-1; i++) 
		{
			node.removeChild(node.childNodes[i]);
		}
		
		// pull all the children of the first node up and level and remove this node
		if(useChildNodes && useChildNodes === true)
		{
			var _node = node.childNodes[0];
			
			for (var z = _node.childNodes.length - 1; z >= 0; z--) 
			{
				node.insertBefore(_node.childNodes[z], node.childNodes[0]);
			}
			
			node.removeChild(_node);
		}
	}
	,
	copyAttributes: function(source, dest)
	{
		for( var x = 0; x < source.attributes.length; x++ ) 
		{
			var attribute = source.attributes[x];
			var name = attribute.nodeName;
			var value = attribute.nodeValue;
			
			// don't copy style
			if(name != "style" && name != "id")
			{
				dest.setAttribute(name, value);
			}
		}
	}
	,
	loadDependencies: function(loaderId, parentNode, onLoad)
	{
		var data = { };
		var loader = null;
		
		for(var z = 0; z < parentNode.childNodes.length; z++)
		{
			var el = parentNode.childNodes[z];
			
			var tag = el.nodeName;
			
			var dataId = "Data" + z;
			
			var dataElement = null;
			
			if(tag == "SCRIPT")
			{
				var src = el.getProperty("src");
				if(src)
				{
					dataElement = {
						name: dataId,
						path: src,
						type: 'js'
					};
					data[dataId] = dataElement;
				}
			}
			
			if(tag == "LINK")
			{
				var href = el.getProperty("href");
				if(href)
				{
					dataElement = {
						name: dataId,
						path: href,
						type: 'css'
					};
					data[dataId] = dataElement;	
				}
			}
		}
		
		loader = new Alf.sourceLoader(loaderId, data);
		loader.jsPath = '';
		loader.cssPath = '';	
		loader.onLoad = onLoad;
		loader.load();
		
		return loader;	
	}	
};

/**
 * WebStudio top-level messages namespace.
 * 
 * @namespace WebStudio
 * @class WebStudio.messages
 */
WebStudio.messages = WebStudio.messages || {
};



/**
 * WebStudio top-level forms namespace.
 * 
 * @namespace WebStudio
 * @class WebStudio.forms
 */
WebStudio.forms = WebStudio.forms ||
{
	getFormNames: function()
	{
		var formArray = [];
		formArray[formArray.length] = "article";
		formArray[formArray.length] = "press-release";
		formArray[formArray.length] = "event";
		formArray[formArray.length] = "product";
		return formArray;		
	}
	,
	getFormTitle: function(formName)
	{
		if("article" == formName)
		{
			return "Article";
		}
		if("press-release" == formName)
		{
			return "Press Release";
		}
		if("event" == formName)
		{
			return "Event";
		}
		if("product" == formName)
		{
			return "Product";
		}
		return null;		
	}
	,
	getFormDescription: function(formName)
	{
		if("article" == formName)
		{
			return "Article Description";
		}
		if("press-release" == formName)
		{
			return "Press Release Description";
		}
		if("event" == formName)
		{
			return "Event Description";
		}
		if("product" == formName)
		{
			return "Product Description";
		}
		return null;
	}
};

/**
 * WebStudio top-level themes namespace.
 * 
 * @namespace WebStudio
 * @class WebStudio.themes
 */
WebStudio.themes = WebStudio.themes ||
{
	getThemeIds : function()
	{
		var themeArray = [];
		themeArray[themeArray.length] = "default";
		themeArray[themeArray.length] = "black";
		return themeArray;
	}
	,
	getThemeName : function(themeId)
	{
		return themeId;
	}
};


/**
 * WebStudio top-level icons namespace.
 * 
 * @namespace WebStudio
 * @class WebStudio.icons
 */
WebStudio.icons = WebStudio.icons ||
{
	getFileTypeIconClass16 : function(fileType)
	{
		var iconClass = null;
		
		if("html" == fileType) { iconClass = "icon-filetype-html-16"; }
		if("image" == fileType) { iconClass = "icon-filetype-image-16"; }
		if("xml" == fileType) { iconClass = "icon-filetype-xml-16"; }
		if("pdf" == fileType) { iconClass = "icon-filetype-pdf-16"; }
		if("jsp" == fileType) { iconClass = "icon-filetype-jsp-16"; }
		if("asp" == fileType) { iconClass = "icon-filetype-asp-16"; }
		if("php" == fileType) { iconClass = "icon-filetype-php-16"; }
		if("text" == fileType) { iconClass = "icon-filetype-txt-16"; }
		
		return iconClass;
	}	
};



/**
 * WebStudio top-level url namespace.
 *
 * Consider that there are three machines:
 * Client - the Surf application (for example, port 8180)
 * Studio - the Studio application (for example, port 8280)
 * Repository - the Alfresco Repository (for example, port 8080) 
 * 
 * @namespace WebStudio
 * @class WebStudio.url
 */
WebStudio.url = WebStudio.url ||
{
	client : function(uri, param, bNoParams) {
		return this.uri("client", uri, params, bNoParams);
	}
	,
	studio : function(uri, params, bNoParams) {
		return this.uri("studio", uri, params, bNoParams);
	}
	,
	repo : function(uri, params, bNoParams) {
		return this.uri("repo", uri, params, bNoParams);
	}
	,
	uri: function(tier, uri, params, bNoParams)
	{
		var url = null;
		if("client" == tier)
		{
			url = WebStudio.originalContextPath + uri;
		}
		if("studio" == tier)
		{
			url = WebStudio.proxyStudioPath + uri;
		}
		if("repository" == tier || "repo" == tier)
		{
			url = WebStudio.proxyRepoPath + uri;
		}
		if(!url)
		{
			alert("Unable to resolve tier");
			return null;
		}
			
		var avmStoreId = WebStudio.context.getStoreId();
		var avmWebappId = WebStudio.context.getWebappId();
			
		if(!params)
		{
			params = { };
		}
			
		if(bNoParams !== true)
		{
			// add in our special params
			if(avmStoreId)
			{
				params["alfStoreId"] = avmStoreId;
				if(avmWebappId)
				{
					params["alfWebappId"] = avmWebappId;
				}
			}
				
			// TODO: alfresco ticket, etc?
				
			// add in params
			var first = true;
			for(var key in params)
			{
				if(params.hasOwnProperty(key))
				{
					var value = params[key];
					
					if(first)
					{
						url = url + "?";
					}
					else
					{
						url = url + "&";
					}
					
					value = escape(value);
					
					url += key + "=" + value;
						
					first = false;
				}
			}
		}
						
		return url;	
	}
};


/**
 * WebStudio top-level ws namespace.
 * 
 * @namespace WebStudio
 * @class WebStudio.ws
 */
WebStudio.ws = WebStudio.ws ||
{
	client : function(webscript, params) {
		return this.ws("client", webscript, params);
	}
	,
	studio : function(webscript, params) {
		return this.ws("studio", webscript, params);
	}
	,
	repo : function(webscript, params) {
		return this.ws("repo", webscript, params);
	}
	,
	ws : function(tier, webscript, params)
	{
		var url = null;
		if("client" == tier)
		{
			url = WebStudio.url.client("/service" + webscript, params);
		}
		if("studio" == tier)
		{
			url = WebStudio.url.studio("/service" + webscript, params);
		}
		if("repository" == tier || "repo" == tier)
		{
			url = WebStudio.url.repo(webscript, params);			
		}
		return url;
	}
};

/**
 * General purpose bootstrapper
 *
 * bootstraps = container object for loaded bootstraps (associative map)
 * config = instantiation config 
 *
 *  {
 *    "id" : { 
 *             "title" : ..., 
 *             "description" : ...,
 *             "classname" : ...
 *             "loader" : {
 *               "loader1" : {
 *                 "name" : ...,
 *                 "path" : ...
 *               }
 *             }
 *    }
 *  }
 *
 */
WebStudio.Bootstrap = function(config, bootstraps)
{
	this.config = config;
	this.bootstraps = bootstraps;
	if(!this.bootstraps)
	{
		this.bootstraps = { };
	}
};

WebStudio.Bootstrap.prototype.load = function()
{	
	var _this = this;
	
	// bind to loader
	var bootstrapOnLoad = function(group, index)
	{
		var objectId = this.objectId;
		var objectTitle = this.objectTitle;
		var objectDescription = this.objectDescription;
		var objectClassname = this.objectClassname;
		
		if(objectClassname)
		{		
			// instantiate the bootstrap object
			var evalString = "new " + objectClassname + "('" + objectId + "', '" + objectTitle + "', '" + objectDescription + "')";		 
			var obj = eval(evalString);
			this.bootstrap.bootstraps[objectId] = obj;
			
			// if the object has an initialize method, fire it
			if(obj.init)
			{
				obj.init();
				obj.isInitialized = true;			
			}
		}
		else
		{
			this.bootstrap.bootstraps[objectId] = { isInitialized: true };
		}	
	};
	
	// bind to loader
	var bootstrapOnFailed = function(group, index)
	{
		var bootstrap = this.bootstrap;
		
		if(bootstrap.onFailure)
		{
			bootstrap.onFailure.attempt();
		}					
	};
	
	// walk through the applications
	for(var id in this.config)
	{
		if(this.config.hasOwnProperty(id))
		{
			var classname = this.config[id].classname;
			var title = this.config[id].title;
			var description = this.config[id].description;
			var loaderConfig = this.config[id].loader;
	
			// create the loader
			if(!this.loaders)
			{
				this.loaders = { };
			}
			
			this.loaders[id] = new Alf.sourceLoader('Loader for ' + id, loaderConfig);
			var loader = this.loaders[id];
			loader.jsPath = "";
			loader.cssPath = "";
			loader.objectId = id;
			loader.objectTitle = title;
			loader.objectDescription = description;
			loader.objectClassname = classname;
			
			loader.bootstrap = this;
			
			// loader events
			loader.onLoad = bootstrapOnLoad.bind(loader);		 
			loader.onFailed = bootstrapOnFailed.bind(loader);
	
			// fire the loader
			loader.load();
		}
	}
	
	// set up a timed check for completion
	this.totalWaitTime = 180000; // 180 seconds
	this.checkPeriod = 500; // check every half second
	this.maxCheckCount = this.totalWaitTime / this.checkPeriod;
	this.checkCount = 0;				
	this.checker = this.check.periodical(this.checkPeriod, this);	
};

WebStudio.Bootstrap.prototype.check = function()
{
	this.checkCount++;
	
	// check to see if all of the objects have finished bootstrapping
	var check = true;
	for(var id in this.config)
	{
		if(this.config.hasOwnProperty(id))
		{
			var obj = this.bootstraps[id];
			if(!obj || !obj.isInitialized)
			{
				check = false;
			}
		}
	}			
	if(check)
	{
		$clear(this.checker);
		if(this.onSuccess)
		{
			this.onSuccess.bind(this).attempt();
		}
		return true;				
	}
	if (this.checkCount > this.maxCheckCount) 
	{
		$clear(this.checker);
		if(this.onFailure)
		{
			this.onFailure.bind(this).attempt();
		}				
		return false;
	}
};

WebStudio.configureRegion = function(elId, regionId, regionScopeId, regionSourceId)
{
	var el = $(elId);
	if (el)
	{
		el.setAttribute('regionId', regionId);
		el.setAttribute('regionScopeId', regionScopeId);
		el.setAttribute('regionSourceId', regionSourceId);
	}
};

WebStudio.unconfigureRegion = function(elId)
{
	var el = $(elId);
	if (el)
	{
		el.removeAttribute('regionId');
		el.removeAttribute('regionScopeId');
		el.removeAttribute('regionSourceId');
	}
};

WebStudio.configureComponent = function(elId, componentId, componentTypeId, componentTitle, componentTypeTitle, componentEditorUrl)
{
	var el = $(elId);
	if (el)
	{
		el.setAttribute('componentId', componentId);
		el.setAttribute('componentTypeId', componentTypeId);
		el.setAttribute('componentTitle', componentTitle);
		el.setAttribute('componentTypeTitle', componentTypeTitle);
		el.setAttribute('componentEditorUrl', componentEditorUrl);
	}
};

WebStudio.unconfigureComponent = function(elId)
{
	var el = $(elId);
	if (el)
	{
		el.removeAttribute('componentId');
		el.removeAttribute('componentTypeId');
		el.removeAttribute('componentTitle');
		el.removeAttribute('componentTypeTitle');
		el.removeAttribute('componentEditorUrl');
	}
};
 
/**
 * WebStudio top-level parser namespace.
 *
 * @namespace WebStudio
 * @class WebStudio.parser
 */
WebStudio.parser = WebStudio.parser ||
{
	parseHTML: function(html)
	{
		return new HTMLtoDOM(html);
	}
};

/**
 * WebStudio top-level components namespace.
 *
 * @namespace WebStudio
 * @class WebStudio.components
 */
WebStudio.components = WebStudio.components ||
{
	newBinding: function()
	{
		var config = { };
		
		config["operation"] = "bindComponent";
		config["binding"] = { };
		config["properties"] = { };
		config["resources"] = { };
		
		return config;
	}
	,	
	newImage: function(type, endpoint, path, mimetype)
	{
		var config = this.newBinding();
		config["binding"]["componentType"] = "/component/common/image";
		config["resources"]["source"] = {
			"type" : type,
			"endpoint" : endpoint,
			"value" : path
		};
		config["properties"]["title"] = "Image Component";
		config["properties"]["description"] = path;
		
		return config;	
	}
	,
	newXml: function(type, endpoint, path, mimetype)
	{
		var config = this.newBinding();
		config["binding"]["componentType"] = "/component/common/xmldisplay";
		config["resources"]["source"] = {
			"type" : type,
			"endpoint" : endpoint,
			"value" : path
		};
		config["properties"]["title"] = "XML Display Component";
		config["properties"]["description"] = path;
		
		return config;
	}
	,
	newInclude: function(type, endpoint, path, mimetype)
	{
		var config = this.newBinding();
		config["binding"]["componentType"] = "/component/common/include";
		config["resources"]["source"] = {
			"type" : type,
			"endpoint" : endpoint,
			"value" : path
		};
		config["properties"]["title"] = "Include Component";
		config["properties"]["description"] = path;
		config["properties"]["container"] = "div";
		
		return config;
	}
	,
	newVideo: function(type, endpoint, path, mimetype)
	{
		var config = this.newBinding();
		config["binding"]["componentType"] = "/component/common/video";
		config["resources"]["source"] = {
			"type" : type,
			"endpoint" : endpoint,
			"value" : path
		};
		config["properties"]["title"] = "Video Component";
		config["properties"]["description"] = path;
		config["properties"]["mimetype"] = mimetype;
		
		return config;
	}
	,
	newFlash: function(type, endpoint, path, mimetype)
	{
		var config = this.newVideo(type, endpoint, path, mimetype);
		config["binding"]["componentType"] = "/component/common/jwplayer";
		config["properties"]["title"] = "JW Flash Player";
		
		return config;
	}
	,
	newAudio: function(type, endpoint, path, mimetype)
	{
		var config = this.newBinding();
		config["binding"]["componentType"] = "/component/common/audio";
		config["resources"]["source"] = {
			"type" : type,
			"endpoint" : endpoint,
			"value" : path
		};
		config["properties"]["title"] = "Audio Component";
		config["properties"]["description"] = path;
		config["properties"]["mimetype"] = mimetype;
		
		return config;
	}
	,
	newFlashMP3: function(type, endpoint, path, mimetype)
	{
		var config = this.newAudio(type, endpoint, path, mimetype);
		config["binding"]["componentType"] = "/component/common/flash-mp3";
		config["properties"]["title"] = "Flash MP3";
		
		return config;
	}
	,
	newDisplayItems: function(type, endpoint, path, mimetype)
	{
		var config = this.newBinding();
		config["binding"]["componentType"] = "/component/common/display-items";
		config["resources"]["source"] = {
			"type" : type,
			"endpoint" : endpoint,
			"value" : path
		};
		config["properties"]["title"] = "Display Items Component";
		config["properties"]["container"] = "div";
		
		return config;	
	}	
};

/**
 * WebStudio top-level dd namespace
 *
 * @namespace WebStudio
 * @class WebStudio.dd
 */
WebStudio.dd = WebStudio.dd ||
{
	register: function(options)
	{
		if (!this.draggables)
		{
			this.draggables = { };
		}
		
		var id = "dd" + this.counter();
		options["ddId"] = id;
		
		this.draggables[id] = options;
		
		return id;
	}
	,
	get: function(id)
	{
		if (!this.draggables)
		{
			this.draggables = { };
		}
		
		return this.draggables[id];
	}
	,
	unregister: function(id)
	{
		this.draggables[id] = null;
	}
	,
	counter: function()
	{
		if (!this.ddCounter)
		{
			this.ddCounter = 0;
		}
		
		this.ddCounter++;
		
		return this.ddCounter;
	}
	,
	makeDraggable: function(el, scope, options, imgUrl)
	{
		// fail out if this element is already draggable
		if (el.hasClass("ui-draggable"))
		{
			return;
		}
	
		jQuery(el).draggable({
			helper: function() {
			
				var clone = null;
				
				if (imgUrl)
				{
					// set up the div
					var div = document.createElement("div");
					div.injectInside(document.body);
					
					// set up image in the div
					var img = document.createElement("img");
					img.src = imgUrl;
					img.injectInside(div);		
					
					// get the image height and width
					var height = jQuery(img).height();
					var width = jQuery(img).width();
					
					// set the css on to the div
					jQuery(div).css( {'width': width });	
					jQuery(div).css( {'height': height });
					
					clone = jQuery(div)[0];				
				}
				
				if (!clone)
				{
					clone = jQuery(this).clone()[0];
				}
				
				jQuery(clone).css( {'opacity': 0.7} );
				jQuery(clone).css( {'position': 'absolute' });
				jQuery(clone).css( {'z-index': 10 });
				jQuery(clone).addClass('DragClone');
				
				// ensure the width and height satisfy minimum values
				var _width = jQuery(clone).width();
				if (_width < 200)
				{
					_width = 200;
					jQuery(clone).css( {'width': _width });
				}
				var _height = jQuery(clone).height();
				if (_height < 24)
				{
					_height = 24;
					jQuery(clone).css( {'height': _height });
				}
				
				// register a drop object
				clone.ddId = WebStudio.dd.register(options);
				
				return clone;
			},
			revert: 'invalid',
			cursor: 'move',
			appendTo: 'body',
			cursorAt: { left: 5, top: 5 },
			stop: function(ev, ui) {
			
				//debugger;
				var payload = ui.helper[0];
				jQuery(payload).remove();
				
			}
		});
		
		if (scope)
		{
			jQuery(el).draggable('option', 'scope', scope);
		}
	}
	,
	makeDroppable: function(el, scope, options)
	{
		// fail out if this element is already droppable
		if (el.hasClass("ui-droppable"))
		{
			return;
		}
		
		if (!options)
		{
			options = { };
		}
		
		if (scope)
		{
			options["scope"] = scope;
		}
		
		//options["tolerance"] = "touch";
		options["tolerance"] = "pointer";
		options["greedy"] = true;
		options["drop"] = function(ev, ui) 
		{
 			var payload = ui.helper[0];
 			
 			var ddId = payload.ddId;
 			
 			var draggableOptions = WebStudio.dd.get(ddId);
 			if (draggableOptions)
 			{
 				if (options.onDrop)
 				{
 					options.onDrop(el, draggableOptions);
 				}
			}

 			// unregister
 			WebStudio.dd.unregister(ddId);
		};		
		
		jQuery(el).droppable(options);
	}
};
