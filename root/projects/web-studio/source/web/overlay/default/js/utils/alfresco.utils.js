if (typeof Alf == "undefined" || !Alf)
{
	Alf = {};
}

Alf.getParentByTag = function(el, tag) 
{
	if ($type(el) == "element") 
	{
		while(true) 
		{
			var p = el.getParent();
			if (p.getTag() == tag)
			{
				return p;
			}
			else if (p.getTag() == 'body')
			{
				return false;
			}
			el = p;
		}
	}
};

Alf.activeSourceLoaders = { };

Alf.sourceLoader = function(index, data) 
{
	this.ID = index;
	
	Alf.activeSourceLoaders[this.ID] = this;
	
	this.data = data;
	this.waitTime = 180000;
	this.checkPeriod = 500;	
	this.jsPath = "/";
	this.cssPath = "/";
	this.idPrefix = 'AlfLoadSources';
	
	this.assets = { };
	this.assetIds = [];
};

Alf.sourceLoader.prototype = {
	load: function() 
	{
		this.recalcProperties();
		this.buildAssets();
		
		this.nowCount = 0;
		this.checkInterval = this.check.periodical(this.checkPeriod, this);		
	},
	onLoad: function() { },
	recalcProperties: function() 
	{
		this.waitTime = this.waitTime.toInt();
		this.checkPeriod = this.checkPeriod.toInt();
		this.checkCount = Alf.parseInt(this.waitTime/this.checkPeriod);
		if (this.checkCount < 1)
		{
			this.checkCount = 1;
		}
	},
	buildAssets: function() 
	{
		var _this = this;
		
		$each(this.data, function(item, index) 
		{
			var type = null;
			if(item.type)
			{
				type = item.type.toLowerCase();
			}
			else
			{
				type = _this.getFileType(item.path);
			}
			if(type == 'jsp')
			{
				// make a better guess
				if(item.path)
				{
					if(item.path.endsWith(".css.jsp"))
					{
						type = "css";
						item.path = item.path + "?contextPath=" + WebStudio.request.contextPath;
					}
					if(item.path.endsWith(".js.jsp"))
					{
						type = "js";
						item.path = item.path + "?contextPath=" + WebStudio.request.contextPath;
					}
				}
			}
					
			var name = item.name;
			var path = "";
			var id = this.idPrefix + name;
			var options = {
				id: id,
				onload: function(e) {
					var sourceLoader = _this;
					sourceLoader.assets[this.id] = this;
				}
			};
			_this.assetIds[_this.assetIds.length] = id;
			
			if ((type == 'js')||(type == 'javascript')||(type == 'jscript')) 
			{
				path = path + _this.jsPath + item.path;
				_this.loadJS(path, options);
			}
			if ((type == 'css')||(type == 'styles')||(type == 'stylesheets')) 
			{
				path = path + _this.cssPath + item.path;
				_this.loadCSS(path, options);
			}
		});
	}
	,
	loadJS: function(url, options)
	{
		// call through using MooTools
		//new Asset.javascript(url, options);
		
		var properties = options;
		var source = url;
		
		properties = $merge({
			'onload': Class.empty
		}, properties);
		
		var script = null;
		
		if(window.ie) 
		{
			script = new Element('script', {'src': source}).inject(document.head);
			script.onreadystatechange = function () {
				if (script.readyState == 'complete' || script.readyState=="loaded") 
				{
					properties.onload();
					script.onreadystatechange = null;
				}
			};
		} 
		else 
		{
			script = new Element('script', {'src': source}).addEvents({
				'load': properties.onload
			});
 
			delete properties.onload;
			return script.setProperties(properties).inject(document.head);
		}
	}
	,
	loadCSS: function(url, options)
	{
		delete options.onLoad;
		
		var asset = new Asset.css(url, options);
		
		var _this = this;
		
		var f = function() {
		
			this.assets[asset.id] = asset;
			
		};
		
		f.delay(1000, this);
	}
	,
	check: function() 
	{	
		if (!this.ID)
		{
			return;
		}
		
		this.nowCount++;
		
		var result = true;		
		for (var i = 0; i < this.assetIds.length; i++) 
		{
			var assetId = this.assetIds[i];
			
			var asset = this.assets[assetId];
			if(!asset)
			{
				result = false;
			}
		}

		if (result === true) 
		{
			$clear(this.checkInterval);
			this.complete();
			return true;
		}
		
		if (this.nowCount > this.checkCount) 
		{
			$clear(this.checkInterval);
			this.failed();
			return false;
		}
		return null;
	},
	complete: function() 
	{
		this.onComplete();
		this.onLoad();
		Alf.activeSourceLoaders[this.ID] = null;
	},
	onComplete: function() 
	{
		//alert('completed load of: ' + this.assetIds);
	},
	failed: function() 
	{
		this.onFailed();
		Alf.activeSourceLoaders[this.ID] = null;
	},
	onFailed: function() 
	{
		alert('failed load of: ' + this.assetIds);
	},
	getFileType: function(fileName) 
	{
		return fileName.substr(fileName.lastIndexOf('.')+1);
	},
	unloadAll: function() 
	{
		for (var i = 0; i < this.assetIds.length; i++) 
		{
			var assetId = this.assetIds[i];
			
			var assetElement = $(assetId);
			if(assetElement)
			{
				assetElement.parentNode.removeChild(assetElement);
			}
		}
	}	
};
