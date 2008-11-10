if (typeof Alf == "undefined")
{
	var Alf = {};
}

Alf.getParentByTag = function(el, tag) 
{
	if ($type(el) == "element") 
	{
		while(true) 
		{
			var p = el.getParent();
			if (p.getTag() == tag) return p;
			else if (p.getTag() == 'body') return false;
			el = p;
		}
	}
}

Alf.activeSourceLoaders = { }; //Only active loaders hire!!!

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
	this.assetIds = new Array();
}

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
		this.checkCount = parseInt(this.waitTime/this.checkPeriod);
		if (this.checkCount < 1)
		{
			this.checkCount = 1;
		}
	},
	buildAssets: function() 
	{
		if(this.pauser)
		{
			debugger;
		}
		
		var _this = this;
		
		$each(this.data, (function(item, index) 
		{
			var type = null;
			if(item.type)
			{
				type = item.type.toLowerCase();
			}
			else
			{
				type = this.getFileType(item.path);
			}
			if(type == 'jsp')
			{
				// make a better guess
				if(item.path != null)
				{
					if(item.path.endsWith(".css.jsp"))
					{
						var type = "css";
						item.path = item.path + "?contextPath=" + WebStudio.request.contextPath;
					}
					if(item.path.endsWith(".js.jsp"))
					{
						var type = "js";
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
			this.assetIds[this.assetIds.length] = id;
			
			if ((type == 'js')||(type == 'javascript')||(type == 'jscript')) 
			{
				path = path + this.jsPath + item.path;
				this.loadJS(path, options);
			};
			if ((type == 'css')||(type == 'styles')||(type == 'stylesheets')) 
			{
				path = path + this.cssPath + item.path;
				this.loadCSS(path, options);
			};
			
		}).bind(this));
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
		
		if(window.ie) 
		{
			var script = new Element('script', {'src': source}).inject(document.head);
				script.onreadystatechange = function () {
				if (script.readyState == 'complete' || script.readyState=="loaded") {
					properties.onload();
					script.onreadystatechange = null;
				}
			}
		} 
		else 
		{
			var script = new Element('script', {'src': source}).addEvents({
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
		
		var f = (function() {
		
			this.assets[asset.id] = asset;
			
		}).delay(1000, this);
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

		if (result == true) 
		{
			$clear(this.checkInterval);
			this.complete();
			return true;
		};
		
		if (this.nowCount > this.checkCount) 
		{
			$clear(this.checkInterval);
			this.failed();
			return false;
		};
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
}

Alf.initializeCSSMorph = function() 
{
	Fx.Morph = Fx.Styles.extend({
		start: function(className){
			var to = {};

			$each(document.styleSheets, function(style){
				var rules = style.rules || style.cssRules;
				$each(rules, function(rule){
					if (!rule.selectorText.test('\.' + className + '$')) return;
					Fx.CSS.Styles.each(function(style){
						if (!rule.style || !rule.style[style]) return;
						var ruleStyle = rule.style[style];
						to[style] = (style.test(/color/i) && ruleStyle.test(/^rgb/)) ? ruleStyle.rgbToHex() : ruleStyle;
					});
				});
			});
			return this.parent(to);
		}
	});

	Fx.CSS.Styles = ["backgroundColor", "backgroundPosition", "color", "width", "height", "left", "top", "bottom", "right", "fontSize", "letterSpacing", "lineHeight", "textIndent", "opacity"];

	Fx.CSS.Styles.extend(Element.Styles.padding);
	Fx.CSS.Styles.extend(Element.Styles.margin);

	Element.Styles.border.each(function(border){
		['Width', 'Color'].each(function(property){
			Fx.CSS.Styles.push(border + property);
		});
	});
}