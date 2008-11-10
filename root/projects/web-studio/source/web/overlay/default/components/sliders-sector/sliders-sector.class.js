
if (typeof WebStudio == "undefined")
{
	var WebStudio = {};
}

WebStudio.SlidersSectors = {}

WebStudio.SlidersSectorsID = 0

WebStudio.SlidersSector = function(index) 
{
	this.defaultContainer = document.body;
	this.injectObject = document.body;

	if (index)
	{
		this.ID = index;
	}
	else
	{
		this.ID = WebStudio.SlidersSectorsID++;
	}
	WebStudio.SlidersSectors[this.ID] = this;
	
	this.defaultContainer = document.body //default dom object for search elements by selector inside it
	this.injectObject = document.body //default dom object for Alfresco.SlidersSector instance inject
	
	this.dmP = 'WebStudio.SlidersSector report: ' //debug messages prefix
	this.dmErrorTemplate = '<font color="red">$</font>' //template for error messages
	this.dmSuccessTemplate = '<font color="green">$</font>' //template for success messages
	this.isAlertDM = true //alert debug messages? true/false
	
	this.defaultTemplateSelector = 'div[id=AlfrescoSlidersSectorTemplate]'
	
	this.defaultElementsConfig = {
		Sliders: {
			selector: '.ASSSlider',
			objects: {
				Header: {
					selector: '.ASSSliderHeader',
					dragDrop: true
				},
				Content: {
					selector: '.ASSSliderContent'
				},
				Data: {
					selector: '.ASSSliderData',
					styles: {
						display: 'none'
					}
            },
				Dropper: {
					selector: '.ASSSliderDropper'
				}
         	},
			setNumber: 'ASSSliderIndex',
			events: {
				over: function(el, obj) {
					var ass = WebStudio.SlidersSectors[this.getProperty('ASSID')]
					var slider = ass.Sliders[this.getProperty('ASSSliderIndex')]
					if (slider)	if (slider.Dropper) if (slider.Dropper.el) slider.Dropper.el.setStyle('display', 'block')
				},
				leave: function(el, obj) {
					var ass = WebStudio.SlidersSectors[this.getProperty('ASSID')]
					var slider = ass.Sliders[this.getProperty('ASSSliderIndex')]
					if (slider)	if (slider.Dropper)	if (slider.Dropper.el) slider.Dropper.el.setStyle('display', 'none')
				},
				drop: function(el, obj) {
					var ass = WebStudio.SlidersSectors[this.getProperty('ASSID')]
					ass.dropSlider(el, obj, this)
					var slider = ass.Sliders[this.getProperty('ASSSliderIndex')]
					if (slider)	if (slider.Dropper)	if (slider.Dropper.el) slider.Dropper.el.setStyle('display', 'none')
				}
			},
			topMultiplier: 25
		},
		SliderTogglers: {
			selector: '.ASSToggleImage',
			events: {
				mousedown: function(event) {
					var event = new Event(event)
					event.stopPropagation()
				},
				mouseenter: function() {
					this.addClass('Selected1')
				},
				mouseleave: function() {
					this.removeClass('Selected1')
				},
				click: function() {
				
					var ass = WebStudio.SlidersSectors[this.getProperty('ASSID')]
					var sliderSectorNumber = this.getProperty('ASSSectorNumber');
					ass.toggleSliderData(sliderSectorNumber);
							
					/*		
					// now call the sector's display method
					if(ass.Sliders[sliderSectorNumber].control)
					{
						// fire events to the bound controls
						// to tell them when they are being displayed
						if(ass.SlidersData[sliderSectorNumber].state == 'show')
						{
							if(ass.Sliders[sliderSectorNumber].control.showControl)
							{
								ass.Sliders[sliderSectorNumber].control.showControl();
							}
						}

						// fire events to the bound controls
						// to tell them when they are being closed
						if(ass.SlidersData[sliderSectorNumber].state == 'hide')
						{
							if(ass.Sliders[sliderSectorNumber].control.hideControl)
							{
								ass.Sliders[sliderSectorNumber].control.hideControl();
							}
						}
					}
					*/

					// fire events to the bound controls
					// to tell them when they are being displayed
					if(ass.SlidersData[sliderSectorNumber].state == 'show')
					{
						if(ass.Sliders[sliderSectorNumber].onShowSlider)
						{
							ass.Sliders[sliderSectorNumber].onShowSlider();
						}
					}

					// fire events to the bound controls
					// to tell them when they are being closed
					if(ass.SlidersData[sliderSectorNumber].state == 'hide')
					{
						if(ass.Sliders[sliderSectorNumber].onHideSlider)
						{
							ass.Sliders[sliderSectorNumber].onHideSlider();
						}
					}
				}
			},
			blockSelection: true,
			setNumber: 'ASSSectorNumber'
		},
		SlidersData: {
			selector: '.ASSSliderData',
			hideFx: true         
      },
		SliderDroppers: {
			selector: '.ASSSliderDropper',
			events: {
				
			}
		}
	}
	
	this.events = {}
	
	this.setConfig({})
}

WebStudio.SlidersSector.prototype = {
	activate: function() {
		this.build()
		
		if (!WebStudio.SlidersSectorsML) {
			document.addListener('mousemove', WebStudio.SlidersSectorsMouseMoveListener)
			document.addListener('mouseup', WebStudio.SlidersSectorsMouseUpListener)
			WebStudio.SlidersSectorsML = true
		}
		
		this.hideAllSlidersData()
		return this;
	},
	toggleSliderData: function(index) {
		if (this.blockSliderToggle)
		{
			return false;
		}
		if (this.SlidersData[index])
		{
			if (this.SlidersData[index].state == 'show') 
			{
				this.hideSliderData(index);
				this.SlidersData[index].state = 'hide';
				this.SliderTogglers[index].el.removeClass('Show');
			} 
			else 
			{
				if (!(this.activeSliderIndex === false)) 
				{
					var ind = this.activeSliderIndex;
					this.hideSliderData(ind, true);
					this.showSliderData(index, this.SlidersData[ind].el.getCoordinates().height);
				} 
				else 
				{
					this.showSliderData(index);
				}
				this.SlidersData[index].state = 'show';
				this.SliderTogglers[index].el.addClass('Show');
			}
		}
		return this;
	},
	showSliderData: function(index, h) {
		if (this.SlidersData[index])
		{
			if (!h)
			{
				var h = this.getHeight().toInt() - this.getAllSlidersHeight().toInt() - 27;
			}
			this.blockSliderToggle = true;
			this.activeSliderIndex = index;
			this.SlidersData[index].el.setStyle('display', '');
			this.SlidersData[index].hideFx.start({height: h});
		}
		return this;
	},
	hideSliderData: function(index, no_block_toggler) {
		if (this.SlidersData[index]) 
		{
			if (!no_block_toggler)
			{
				this.blockSliderToggle = true;
			}
			this.activeSliderIndex = false;
			this.SlidersData[index].hideFx.start({height: 0});
			this.SlidersData[index].state = 'hide';
			this.SliderTogglers[index].el.removeClass('Show');
		}
		return this
	},
	getHeight: function() {
		var h = this.generalLayer.getParent().getSize().size.y
		//TODO: 81px?
		if (window.ie) h += 81;
		if (window.ie6) h += 19;
		return h
	},
	reOpenActiveSlider: function() {
		//TODO: Start animation from current position, not from height = 0
		if (this.activeSliderIndex) {
			this.SlidersData[this.activeSliderIndex].el.setStyle('height', 0)
			this.showSliderData(this.activeSliderIndex)
		}
	},
	toggleSliderFxComplete: function() {
		this.blockSliderToggle = false
		var i = 0
		while(this.SlidersData[i]) {
			var h = this.SlidersData[i].el.getStyle('height').toInt()
			if (h == 0) {
				this.SlidersData[i].el.setStyle('display', 'none')
			}
			i++
		}
	},
	hideAllSlidersData: function() {
		$each(this.Sliders, (function(item, index) {
			this.SlidersData[index].el.setStyle('height', 0)
			this.SlidersData[index].state = 'hide'
			this.SliderTogglers[index].el.removeClass('Show')
		}).bind(this))
		this.activeSliderIndex = false
	},
	fireEvent: function(type) {
		this._tempResult = true
		if (this.events[type]) {
			$each(this.events[type], (function(item, index) {
				var result = item.call();
				if (result == false) this._tempResult = false
			}).bind(this))
		}
		return this._tempResult
	},
	addEvent: function(type, index, func, bind) {
		var type = type.toLowerCase()
		if (!this.events[type]) this.events[type] = {}
		if (bind) func = func.bind(bind)
		this.events[type][index] = func
	},
	removeEvent: function(type, index) {
		var type = type.toLowerCase()
		if (!this.events[type]) return false;
		if (!this.events[type][index]) return false;
		this.events[type][index] = null
		return true;
	},
	dropSlider: function(el, obj, dropper) {
		el.remove()
		var slider = this.Sliders[this.dragSliderIndex]
		if (slider) {
			slider.el.injectBefore(dropper)
		}
	},
	getAllSlidersHeight: function() {
		this._tempH = 0
		$each(this.Sliders, (function(item, index) {
			this._tempH += item.el.getSize().size.y
		}).bind(this))
		return this._tempH
	},
	build: function() {
		var _this = this;
		if (!this.templateObject)
		{
			this.setTemplateBySelector(this.defaultTemplateSelector);
		}
		this.generalLayer = this.templateObject.clone()
		this.generalLayer.set({
			id: 'AlfrescoSlidersSector'
		})
		this.generalLayer.injectInside(this.injectObject)
		
		this.elementsConfig = $merge(this.defaultElementsConfig, this.elementsConfig)
		this.applyElementsConfig();

/*
      var oPushButton2 = new YAHOO.widget.Button("pushbutton2", { onclick: { fn: function(){
      var time = new Date();
      new Ajax("/dynamic-website/service/ads/search/lucene?avmStoreId=" + getStoreId() + "&_dc=" + time.getTime(), {method: 'get', onFailure:_this.onFailure.bind(this)}).request();
      } } });
*/      
     },
	applyElementsConfig: function() {
		$each(this.elementsConfig, (function(cItem, cIndex) {
			this[cIndex] = []
			if (cItem.selector) {
				var els = this.generalLayer.getElementsBySelector(cItem.selector)
				if (els[0]) this[cIndex].el = els[0];
				this[cIndex].els = els
				$each(els, (function(elItem, elIndex) {
					this.setElementProperties(elItem, elIndex, cItem, cIndex, this)
				}).bind(this))
			}
		}).bind(this))
		return this;
	},
	setElementProperties: function(item, index, config, parentIndex, parent) { //'item' = html dom element, 'index' = name of array of 'item's; ob = js container for 'index'; config = js object with configuration for item
		var ob = parent[parentIndex]
		ob[index] = {}
		ob[index].index = index
		ob[index].parentIndex = parentIndex
		ob[index].parent = parent
		var o = ob[index] // for example o = this.Sliders[0] 
		o.el = item // o.el = html element finding by selector specified in this.elementsConfig/this.defaultElementsConfig
		if (o.el) {
			if (config.styles) o.el.setStyles(config.styles)
			
			if (config.topMultiplier) o.el.set({
				styles: {
					top: index*config.topMultiplier
				}
			})
			
			if (config.events) {
				o.el.set({
					ASSID: this.ID, 
					events: config.events
				})
			}
			
			if (config.setNumber) {
				var p = {}
				p[config.setNumber] = index
				o.el.set(p)
			}
			
			if (config.dragDrop) {
				this.setDragDrop(o.el, parent.index)
			}
			
			if (config.hideFx) {
				o.hideFx = new Fx.Styles(o.el, {duration: 400, transition: Fx.Transitions.Cubic.easeOut, wait: true})
				o.hideFx.addEvent('onComplete', (this.toggleSliderFxComplete).bind(this))
			}
			
			if (config.blockSelection) this.blockSelection(o.el)
			
			if (config.objects) {
				$each(config.objects, (function(cItem, cIndex) {
					o[cIndex] = []
					if (cItem.selector) {
						var els = o.el.getElementsBySelector(cItem.selector)
						if (els[0]) o[cIndex].el = els[0];
						o[cIndex].els = els;
						$each(els, (function(elItem, elIndex) {
							this.setElementProperties(elItem, elIndex, cItem, cIndex, o);
						}).bind(this));
					}
				}).bind(this));
			}
		}
	},
	setDragDrop: function(el, index) {
		el.set({
			ASSID: this.ID,
			ASSSliderIndex: index
		})
		el.addEvent('mousedown', function(e) {
			e = new Event(e).stop();
			var ass = WebStudio.SlidersSectors[this.getProperty('ASSID')]
	 		ass.dragSliderIndex = this.getProperty('ASSSliderIndex')
			var clone = this.getParent().clone()
			.setStyles(this.getParent().getCoordinates())
			.setStyles({
				'opacity': 0.7, 
				'position': 'absolute',
                'left' : '0px'
			})
			.addEvent('emptydrop', function() {
				this.remove();
			}).inject(WebStudio.App.generalLayer);

			var drag = clone.makeDraggable({droppables:ass.Sliders.els});
			drag.start(e);
		});
	},
	setConfig: function(object) {
		this.elementsConfig = object
		return this;
	},
	setTemplateBySelector: function(selector) { //Set template of the window by selector(string)
		this.templateSelector = selector
		this.templateObject = $(this.defaultContainer).getElementsBySelector(this.templateSelector)[0]
		if (!this.templateObject) {
			this.templateObject = this.defaultContainer.getElementsBySelector(this.defaultTemplateSelector)[0]
			if (this.templateObject) {
				this.templateSelector = this.defaultTemplateSelector
				this.templateObject.remove()
			} else {
				this.addDebugMessages('error', 'Template object not found')
			}
		} else {
			this.templateObject.remove()
		}
		return this;
	},
	setTemplateByHTML: function(html) { //Set template of the window by html code
		this.templateObject = new Element('div', {})
		this.templateObject.setHTML(html)
		return this;
	},
	mouseMoveListen: true,
	mouseMoveListener: function() {
		this.fireEvent('documentMouseMove')
	},
	mouseUpListen: true,
	mouseUpListener: function() {
		//this.fireEvent('documentMouseUp')
	},
	mouseDownListen: true,
	mouseDownListener: function() {
		//this.fireEvent('documentMouseDown')
	},
	blockSelection: function(object) {
		object = $(object);
		if (typeof(object) == 'object') {
			object.onselectstart = function(event) {
				var event = new Event(event);
				event.preventDefault();
				return false;
			}
			object.setStyles({
				'-moz-user-select': 'none',
				'-khtml-user-select': 'none',
				'user-select': 'none'
			})
		}
	},
	unblockSelection: function(object) {
		object = $(object);
		if (typeof(object) == 'object') {
			object.onselectstart = null
			object.setStyles({
				'-moz-user-select': '',
				'-khtml-user-select': '',
				'user-select': ''
			})
		}
	},
	addDebugMessages: function(type, text, more) { //Add debug message in dojo debug container or alert it
		if (this.isAlertDM) alert(text)
		
		if (type == 'error') {
			var mess = this.dmErrorTemplate.replace('$', text)
		} else if (type == 'success') {
			var mess = this.dmSuccessTemplate.replace('$', text)
		} else {
			var mess = text
		}
	},
	onFailure : function(data)
	{
		WebStudio.App.onFailure(data);
	}
}

WebStudio.SlidersSectorsMouseMoveListener = function(event) {
	$each(WebStudio.SlidersSectors, function(item, index) {
		if (item) {
			if (item.mouseMoveListen) item.mouseMoveListener(event)
		}
	})
}

WebStudio.SlidersSectorsMouseUpListener = function(event) {
	$each(WebStudio.SlidersSectors, function(item, index) {
		if (item) {
			if (item.mouseUpListen) item.mouseUpListener(event)
		}
	})
}