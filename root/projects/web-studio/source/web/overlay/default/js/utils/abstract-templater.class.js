if (typeof WebStudio == "undefined")
{
	var WebStudio = {};
}

WebStudio.AbstractTemplater = function(constructor) 
{
	this._constructorName = constructor;
	
	this.defaultContainer = document.body;
	this.injectObject = document.body;
	
	this.dmPrefix = 'Unknown Soro Component report: '; //debug messages prefix
	this.dmErrorTemplate = '<font color="red">$</font>'; //template for error messages
	this.dmSuccessTemplate = '<font color="green">$</font>'; //template for success messages
	this.dmMessageTemplate = '$'; //template for default messages
	this.isAlertDM = true; //alert debug messages? true/false

	this.defaultTemplateSelector = '';

	this.defaultElementsConfig = {};

	this.templateID = 0;
	this.templateSelectors = {};
	this.templateObjects = {};

	this.events = {};

	this._setGlobalObjectLinks();

	this.setConfig({});
	this.isShow = true;
	this.isHide = false;
	
	this._init();	
}

WebStudio.AbstractTemplater.prototype = {

	_init: function() {
		this._replacerLayer = new Element('div', {
			styles: {display: 'none'}
		});

		/*
		if (Fx)
		{
			if (!Fx.Morph)
			{
				Alf.initializeCSSMorph();
			}
		}
		*/		
	},
	setWidth: function(value) {
		this.generalLayer.setStyle('width', value);
		if (this.resize)
		{
			this.resize();
		}
		return this;
	},
	getWidth: function() {
		return this.generalLayer.getCoordinates().width;
	},
	setHeight: function(int) {
		this.generalLayer.setStyle('height', int);
		if (this.resize)
		{
			this.resize();
		}
		return this;
	},
	getHeight: function() {
		if (this.isHide)
		{
			return 0;
		}
		return this.generalLayer.getCoordinates().height;
	},
	setDimension: function(width, height) {
		if (width) 
		{
			this.generalLayer.setStyle('width', width);
		}
		if (height)
		{
			this.generalLayer.setStyle('height', height);
		}
		if (this.resize)
		{
			this.resize();
		}
		return this;
	},
	getDimension: function() {
		var coor = this.generalLayer.getCoordinates();
		return {width: coor.width, height: coor.height};
	},
	setMaxDimension: function(width, height) {
		if (width)
		{
			this.maxWidth = width;
		}
		if (height)
		{
			this.maxHeight = height;
		}
		return this;
	},
	setMinDimension: function(width, height) {
		if (width)
		{
			this.minWidth = width;
		}
		if (height)
		{
			this.minHeight = height;
		}
		return this;
	},
	setPosition: function(left, top) {
		if (left)
		{
			this.generalLayer.setStyle('left', left);
		}
		if (top)
		{
			this.generalLayer.setStyle('top', top);
		}
		return this;
	},
	getPosition: function() {
		var coor = this.generalLayer.getCoordinates();
		return {left: coor.left, top: coor.top};
	},
	getCoordinates: function() {
		return this.generalLayer.getCoordinates();
	},
	setHorizontalMiddle: function() {
		var ch = this.getHeight();
		var wh = window.getSize().scrollSize.y;
		var top = ((wh - ch) / 2).toInt();
		this.generalLayer.setStyles({top: top});
		return this;
	},
	setVerticalMiddle: function() {
		var cw = this.getWidth();
		var ww = window.getSize().scrollSize.x;
		var left = ((ww - cw) / 2).toInt();
		this.generalLayer.setStyles({left: left});
		return this;
	},
	centered: function() {
		this.setVerticalMiddle();
		this.setHorizontalMiddle();
		return this;
	},
	destroy: function() {
		try {
			this.generalLayer.remove();
			this.generalLayer = null;
			this._replacerLayer.remove();
			this._replacerLayer = null;
		} catch(e) {}
		this._brothers[this.ID] = undefined;
	},
	hide: function() {
		if(this.isShow){
			this.generalLayer.remove();
			this.isHide = true;
			this.isShow = false;
		}
		return this;
	},
	show: function() {
		if(this.isHide)
		{	this.generalLayer.injectInside(this.injectObject);
			this.isHide = false;
			this.isShow = true;
		}
		return this;
	},
	remove: function() {
		this.generalLayer.remove();
		return this;
	},
	setInjectObject: function(DOMObject) {
		this.injectObject = DOMObject;
		return this;
	},
	fireEvent: function(_type, _args) {
		var type = _type.toLowerCase();
		var args = _args;
		this._tempResult = true;
		if (this.events[type]) 
		{
			$each(this.events[type], (function(item, index) {
				var result = item.attempt(args);
				if (result == false)
				{
					this._tempResult = false;
				}
			}).bind(this));
		}
		return this._tempResult;
	},
	fireEventByIndex: function(_type, index) {
		var type = _type.toLowerCase();
		if (this.events[type])
		{
			if (this.events[type][index])
			{
				return this.events[type][index].call();
			}
		}
		return false;
	},
	addEvent: function(_type, index, func, bind, isDontAddIfExist) {
		var type = _type.toLowerCase();
		if (!this.events[type])
		{
			this.events[type] = {};
		}
		if (bind)
		{
			func = func.bind(bind);
		}
		if (this.events[type][index]) 
		{
			if (!isDontAddIfExist)
			{
				this.events[type][index] = func;
			}
			this.addDebugMessages('message', 'Warning! '+type+' event with '+index+' index already exist in observer.', 'You use .addEvent method with type and index of already exist event. The old event has been replaced! You can use .addEventIfNotExist method if you don\'t want replace it.');
		} 
		else 
		{
			this.events[type][index] = func;
		}
		return this;
	},
	addEventIfNotExist: function(type, index, func, bind) {
		this.addEvent(type, index, func, bind, true);
	},
	removeEvents: function(_type) {
		var type = _type.toLowerCase();
		if (!this.events[type])
		{
			return false;
		}
		$each(this.events[type], (function(item, index) {
			this.events[type][index] = null;
		}).bind(this));
		this.events[type] = null;
		return true;
	},
	removeEventByIndex: function(_type, index) {
		var type = _type.toLowerCase();
		if (!this.events[type])
		{
			return false;
		}
		if (!this.events[type][index])
		{
			return false;
		}
		this.events[type][index] = null;
		return true;
	},
	removeEvent: function(type, index) {
		if (this.events[type])
		{
			if (index)
			{
				this.removeEventByIndex(type, index);
			}
			else
			{
				this.removeEvents(type);
			}
		}
		return null;
	},
	setConfig: function(obj) {
		this.elementsConfig = obj;
		this.elementsConfig = $merge(this.defaultElementsConfig, this.elementsConfig);
		return this;
	},
	buildGeneralLayer: function(templateID) {
		if (!this.ID)
		{
			this.ID = this._brothersID++;
		}
		this._brothers[this.ID] = this;
		
		if (this.templateObjects[templateID]) 
		{
			var id = templateID;
		} 
		else 
		{
			var id = 0;
			if (!this.templateObjects[id])
			{
				$each(this.templateObjects, function(item, index) {
					if ($type(id) == 'undefined')
					{
						id = index;
					}
				});
			}
		}
		if (!this.templateObjects[id])
		{
			this.setTemplateBySelector(this.defaultTemplateSelector, id);
		}
		
		if (!this.templateObjects[id]) 
		{
			this.addDebugMessages('error', 'Template object not found!');
			return false;
		}
		
		this.generalLayer = this.templateObjects[id].clone();
		if(this.injectObject)
		{
			this.generalLayer.injectInside(this.injectObject);
		}

		this.elementsConfig = $merge(this.defaultElementsConfig, this.elementsConfig);

		this.applyElementsConfig();

		if (this.build)
		{
			this.build(templateID);
		}
	},
	rebuild: function(templateID) {
		this.generalLayer.remove();
		this.generalLayer = null;
		this.buildGeneralLayer(templateID);
	},
	applyElementsConfig: function() {
		$each(this.elementsConfig, (function(cItem, cIndex) {
			this[cIndex] = [];
			if (cItem.selector) 
			{		
				var els = this.generalLayer.getElementsBySelector(cItem.selector);
				if (els[0])
				{
					this[cIndex].el = els[0];
				}
				$each(els, (function(elItem, elIndex) {
					this.setGlobalElementConfig(elItem, elIndex, this[cIndex], cItem, cIndex);
				}).bind(this));
			}
		}).bind(this));
		return this;
	},
	setGlobalElementConfig: function(item, index, ob, config, configIndex) {
	//'item' = html dom element, 'index' = number of 'item's element; ob = js container for 'index';
	//config = js object with configuration for item
		ob[index] = {};
		var o = ob[index]; // for example o = this.Sliders[0]
		o.el = item; // o.el = html element finding by selector specified in this.elementsConfig/this.defaultElementsConfig
		if (o.el) 
		{
			var oel = o.el;
			if (config.styles)
			{
				oel.setStyles(config.styles);
			}
			if (config.IEStyles)
			{
				if (window.ie)
				{
					oel.setStyles(config.IEStyles);
				}
			}
			if (config.IE6Styles)
			{
				if (window.ie6)
				{
					oel.setStyles(config.IE6Styles);
				}
			}
			if (config.IE7Styles)
			{
				if (window.ie7)
				{
					oel.setStyles(config.IE7Styles);
				}
			}
			if (config.geckoStyles)
			{
				if (window.gecko)
				{
					oel.setStyles(config.geckoStyles);
				}
			}
			if (config.webkitStyles)
			{
				if (window.webkit)
				{
					oel.setStyles(config.webkitStyles);
				}
			}
			if (config.webkit419Styles)
			{
				if (window.webkit419)
				{
					oel.setStyles(config.webkit419Styles);
				}
			}
			if (config.webkit420Styles)
			{
				if (window.webkit420)
				{
					oel.setStyles(config.webkit420Styles);
				}
			}
			if (config.operaStyles)
			{
				if (window.opera)
				{
					oel.setStyles(config.operaStyles);
				}
			}
			
			if (config.events) {
				oel.set({
					ACID: this.ID,
					events: config.events
				});
			};

			if (config.attributes) 
			{
				oel.set(config.attributes);
			}

			if (config.blockSelection)
			{
				this.blockSelection(oel);
			}

			if (config.methods) 
			{
				$each(config.methods,(function(mItem, mIndex){
					o[mIndex] = mItem.bind(this);
				}).bind(this));
			}

			if (config.propertyToInnerHTML)
			{
				if (this[config.propertyToInnerHTML])
				{
					oel.setHTML(this[config.propertyToInnerHTML]);
				}
			}

			if (config.blockMouseWheelPropagation) 
			{
				oel.addEvent('mousewheel', function(event) {
					event = new Event(event);
					if (this.getSize().scrollSize.y > this.getSize().size.y) 
					{
						this.scrollTo(this.getSize().scroll.x, this.getSize().scroll.y - event.wheel * 50);
						event.stop();
					}
				});
			}

			if (config.remove)
			{
				oel.remove();
			}

			if (config._constructor)
			{
				item._constructor.attempt([item, index, ob, config, oel, configIndex], this);
			}

			if (this.setElementConfig)
			{
				this.setElementConfig(item, index, ob, config, oel, configIndex);
			}

            		if (config.objects) 
            		{
				$each(config.objects, (function(cItem, cIndex) {
					o[cIndex] = [];
					if (cItem.selector) {
						var els = o.el.getElementsBySelector(cItem.selector);

						if (els[0]) o[cIndex].el = els[0];

						$each(els, (function(elItem, elIndex) {
							this.setGlobalElementConfig(elItem, elIndex, o[cIndex], cItem);
						}).bind(this));
					}
				}).bind(this));
			}
		}
	},
	setTemplate: function(value, index) { //dom object, css selector or html code as argument
		if ($type(value) == 'element') {
			this.setTemplateByDOMObject(value, index);
		} else if (($type(value) == 'string')&&(value.length > 200)) {
			this.setTemplateByHTML(value, index);
		} else if (($type(value) == 'string')&&(value.length <= 200)) {
			this.setTemplateBySelector(value, index);
		} else {
			this.addDebugMessages('error', 'Unknown type of template object! See .setTemplate method.');
		}
		return this;
	},
	setTemplateBySelector: function(selector, index) {
		
		if (index)
		{
			var id = index;
		}
		else
		{
			var id = this.templateID++;
		}

		if (this._templates[index]) 
		{
			this.templateObjects[id] = this._templates[index];
			return this;
		}

		this.templateSelectors[id] = selector;
		this.templateSelector = selector;
		
		this.templateObjects[id] = $(this.defaultContainer).getElementsBySelector(selector)[0];
		if (!this.templateObjects[id]) 
		{
			this.templateObjects[id] = this.defaultContainer.getElementsBySelector(this.defaultTemplateSelector)[0];
			if (this.templateObjects[id]) 
			{
				this.templateSelectors[id] = this.defaultTemplateSelector;
				this.templateObjects[id].remove();
			}
			else 
			{
				this.addDebugMessages('error', 'Template object not found');
				return false;
			}
		}
		else
		{

			this.templateObjects[id] = $(this.templateObjects[id]);
			this.templateObjects[id].remove();
		}
		if (this._templates[id])
		{
			this.addDebugMessages('message', 'Warning! The global link in '+this._templatesName+' object on template with index = '+id+' has been replaced!');
		}
		this._templates[id] = this.templateObjects[id];
		return this;
	},
	setTemplateByHTML: function(html, index) { //Set template by html code
		if (index)
		{
			var id = index;
		}
		else
		{
			var id = this.templateID++;
		}
		this.templateObjects[id] = new Element('div', {});
		this.templateObjects[id].setHTML(html);
		if (this._templates[id])
		{
			this.addDebugMessages('message', 'Warning! The global link in '+this._templatesName+' object on template with index = '+id+' has been replaced!');
		}
		this._templates[id] = this.templateObjects[id];
		return this;
	},
	setTemplateByDOMObject: function(object, index) {
		if (index)
		{
			var id = index;
		}
		else
		{
			var id = this.templateID++;
		}

		this.templateObjects[id] = object.clone();
		
		if (this._templates[id])
		{
			this.addDebugMessages('message', 'Warning! The global link in '+this._templatesName+' object on template with index = '+id+' has been replaced!');
		}
		this._templates[id] = this.templateObjects[id];
		return this;
	},
	blockSelection: function(object) {
		object = $(object);
		if ($type(object) == 'element') 
		{
			object.onselectstart = function(event) {
				var event = new Event(event);
				event.preventDefault();
				return false;
			};
			object.setStyles({
				'-moz-user-select': 'none',
				'-khtml-user-select': 'none',
				'user-select': 'none'
			});
		}
		return this;
	},
	unblockSelection: function(object) {
		object = $(object);
		if ($type(object) == 'element') 
		{
			object.onselectstart = null;
			object.setStyles({
				'-moz-user-select': '',
				'-khtml-user-select': '',
				'user-select': ''
			});
		}
		return this;
	},
	isMouseLeft: function(event) {
		if (window.ie) 
		{
			if (event.button == 1)
			{
				return true;
			}
			else
			{
				return false;
			}
		} else {
			if (event.which == 1)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
	},
	addDebugMessages: function(type, text, more) {
		if (type == 'error') {
			var mess = this.dmErrorTemplate.replace('$', text);
		} else if (type == 'success') {
			var mess = this.dmSuccessTemplate.replace('$', text);
		} else  if (type == 'message') {
			var mess = this.dmSuccessTemplate.replace('$', text);
		} else {
			var mess = text;
		}
		mess = this.dmPrefix + mess;
		
		return this;
	},  
	block: function() 
	{
		if (!this.blockLayer)
		{
			this.createBlockLayer();
		}
		this.blockLayer.injectInside(document.body);
		this.resizeBlockLayer();
		return this;
	},
	unblock: function() {
		try {
			this.blockLayer.remove();
		} catch(e) {}
		return this;
	},
	createBlockLayer: function() 
	{
		if (this.blockLayer)
		{
			this.blockLayer.remove();
		}
		this.blockLayer = new Element('div', 
		{
		      styles: {
			   backgroundColor: '#111111',
			   position: 'absolute',
			   width: window.getSize().scrollSize.x,
			   height: window.getSize().scrollSize.y,
			   left: 0,
			   top: 0,
			   opacity: 0.5
		      }
        });
        //'z-index': this.generalLayer.getStyle('z-index') - 1
        
        window.addEvent('resize', this.resizeBlockLayer.bind(this));
	},
	resizeBlockLayer: function() {
		 this.blockLayer.set({
		      styles: {
			   width: this.getWindowSize().w,
			   height: this.getWindowSize().h
		      }
		 });
	}, 
	_setGlobalObjectLinks: function() {
		if (this._constructorName) 
		{
			this.dmPrefix = this._constructorName+' report: '; //change debug messages prefix

			this._brothersName = this._constructorName + 's';
			this._brothersIDName = this._constructorName + 'sID';
			this._templatesName = this._constructorName + 'Templates';

			if (this._constructorName.indexOf('.') > 0) {
				this._isChild = true;
				for (var i=this._constructorName.length; i>0; i--) {
					if (this._constructorName.charAt(i) == '.')
					{
						break;
					}
				}
				this._constructorParentName = this._constructorName.substr(0, i);
				this._constructorFunctionName = this._constructorName.substr(i+1);

				var evaler = 'this._constructorParent = '+this._constructorParentName;
				eval(evaler);

				this._constructorParent[this._constructorFunctionName+'s'] = {};
				this._constructorParent[this._constructorFunctionName+'sID'] = 0;
				this._constructorParent[this._constructorFunctionName+'Templates'] = {};
			} else {
				this._isChild = false;
				var evaler = 'var '+this._brothersName+' = {}';
				eval(evaler);
				var evaler = 'var '+this._brothersIDName+' = 0';
				eval(evaler);
				var evaler = 'var '+this._templatesName+' = {}';
				eval(evaler);
			}

			var evaler = 'this._constructor = '+this._constructorName;
			eval(evaler);

			if ($type(this._constructor) == 'function') {
				var evaler = 'this._brothers = '+this._brothersName;
				eval(evaler);
				var evaler = 'this._brothersID = '+this._brothersIDName;
				eval(evaler);
				var evaler = 'this._templates = '+this._templatesName;
				eval(evaler);
			} else {
				this.addDebugMessages('error', 'Constructor is not a function!', 'You use Alfresco.AbstractTemplater with unknown type of required parameter. This parameter is a string full name of constructor <b>function</b>.<br><br> For example:<br><br> Alfresco.NewComponent = function(){};<br>Alfresco.NewComponent.prototype = new Alfresco.AbstractTemplater(&quot;<b>Alfresco.NewComponent</b>&quot;)');
			}
		} else {
			this.addDebugMessages('error', 'Constructor name is required!', 'You use Alfresco.AbstractTemplater without one required parameter. This parameter is a string full name of constructor function.<br><br> For example:<br><br> Alfresco.NewComponent = function(){};<br>Alfresco.NewComponent.prototype = new Alfresco.AbstractTemplater(&quot;<b>Alfresco.NewComponent</b>&quot;)');
		}

	},
	getWindowSize : function () {
		return {
		    w: Math.max(window.ie ? (document.body.offsetWidth - 15) : window.innerWidth, 1),
		    h: Math.max(window.ie ? document.body.offsetHeight : window.innerHeight, 1)
		};
	},
	isHidden: function () {
		return this.isHide;
	}
};