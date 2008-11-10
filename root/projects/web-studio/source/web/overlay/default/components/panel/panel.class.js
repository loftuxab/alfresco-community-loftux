
if (typeof WebStudio == "undefined")
{
	var WebStudio = {};
}

WebStudio.Panels = {};

WebStudio.PanelsID = 0;

WebStudio.Panel = function(index) 
{
	this.defaultContainer = document.body;
	this.injectObject = document.body;

	if (index)
	{
		this.ID = index;
	}
	else
	{
		this.ID = WebStudio.PanelsID++;
	}
	WebStudio.Panels[this.ID] = this;
	
	this.defaultContainer = document.body; //default dom object for search elements by selector inside it
	this.injectObject = document.body; //default dom object for WebStudio.Panel instance inject
	
	this.dmP = 'WebStudio.Panel report: '; //debug messages prefix
	this.dmErrorTemplate = '<font color="red">$</font>'; //template for error messages
	this.dmSuccessTemplate = '<font color="green">$</font>'; //template for success messages
	this.isAlertDM = true; //alert debug messages? true/false
	
	/*if (typeof WebStudio.Menu == "undefined") {
		this.addDebugMessages('error', 'WebStudio.Menu class is needed for build WebStudio.Panel')
		return null
	}*/
	
	this.defaultTemplateSelector = 'div[id=AlfrescoPanelTemplate]';
	
	this.minWidth = 1;
	this.maxWidth = 10000;
	this.slipWidth = 500;
	this.slipPx = 10;
	
	this.minContentHeight = 30;
	
	this.defaultElementsConfig = {
		PanelsTable: {
			selector: 'table[id=AlfrescoTwoPanelsResizer]'
		},
		Holder: {
			selector: 'td[id=ATP]'
		},
		PanelsResizeSeparator: {
			selector: 'div[id=ATPResizer]',
			resizeHandler: true,
			resizeObject: 'ATPLeftDiv',
			events: {
				contextmenu: function(event) {
					var event = new Event(event);
					var sp = WebStudio.Panels[this.getProperty('APID')];
					
					sp.showResizerMenu(event);
					event.preventDefault();
				}
			}
		},
		ATPLeftDiv: {
			selector: 'div[id=ATPLeftDiv]'
		},
		ATPRightDiv: {
			selector: 'div[id=ATPRightDiv]'
		},
		ATPTop: {
			selector: 'td[id=ATPTop]'
		}
	};
	
	this.panels = {left:{},right:{}};
};

WebStudio.Panel.prototype = {
	activate: function() {
		
		this.build();
		
		if (!WebStudio.PanelsML) {
			document.addListener('mousemove', WebStudio.PanelsMouseMoveListener);
			document.addListener('mouseup', WebStudio.PanelsMouseUpListener);
			WebStudio.PanelsML = true;
		}
		
		this.resizerMenu = new WebStudio.Menu();
		this.resizerMenu.setTemplateBySelector('div[id=AlfrescoPanelResizerMenuTemplate]');
		this.resizerMenu.activate();
		this.resizerMenu.addEvent('click', 'resizerSelect', this.resizerMenuSelect, this);
		this.resizerMenu.generalLayer.setProperty('id', 'AlfrescoPanelResizerMenu');
		this.resizerMenu.generalLayer.setStyle('z-index', 99999);
		this.resizerMenu.hide();

		this._offsetPanelsTablecontent = 0;
		
		this.setPanelSizes(50);
		
		return this;
	},
	collapseToRight: function() { //Show only right Panel
		this.ATPLeftDiv.el.setStyle('display', 'none');
		this.PanelsResizeSeparator.el.setStyle('display', 'none');
		return this;
	},
	collapseToLeft: function() { //Show only left Panel
		this.ATPRightDiv.el.setStyle('display', 'none');
		this.PanelsResizeSeparator.el.setStyle('display', 'none');
		return this;
	},
	showPanels: function() { //Show both Panels
		this.ATPRightDiv.el.setStyle('display', '');
		this.PanelsResizeSeparator.el.setStyle('display', '');
		this.ATPLeftDiv.el.setStyle('display', '');
		return this;
	},
	setHeight: function(h) {
		this.PanelsTable.el.setStyle('height', h);
		this.setPanelsContentHeight(parseInt(h) - this._offsetPanelsTablecontent);
		return this;
	},
	getHeight: function() {
		return this.PanelsTable.el.getStyle('height').toInt();
	},
	getWidth: function() {
		return this.PanelsTable.el.getCoordinates().width;
	},
	setPanelsContentHeight: function(h) {
		h = parseInt(h);
		if (h<this.minContentHeight)
		{
			h = this.minContentHeight;
		}
		this.ATPLeftDiv.el.setStyle('height', h);
		this.ATPRightDiv.el.setStyle('height', h);
		return this;
	},
	getPanelsContentHeight: function() {
		return this.STPLeftDiv.el.getStyle('height').toInt();
	},
	resizerMenuSelect: function(group, index) {
		//group == 'roots'
		if (index == 0) {
			this.setPanelSizes(20);
		} else if (index == 1) {
			this.setPanelSizes(30);
		} else if (index == 2) {
			this.setPanelSizes(40);
		} else if (index == 3) {
			this.setPanelSizes(50);
		} else if (index == 4) {
			this.setPanelSizes(60);
		} else if (index == 5) {
			this.setPanelSizes(70);
		} else if (index == 6) {
			this.setPanelSizes(80);
		} else if (index == 7) {
			this.setPanelSizes(90);
		} else if (index == 8) {
			this.setPanelSizes(100);
		}
		this.hideResizerMenu();
	},
	showResizerMenu: function(event) {
		var left = event.client.x;
		var top = event.client.y;
		if (this.left.pSize > 70)
		{
			left -= this.resizerMenu.generalLayer.getSize().size.x;
		}
		this.resizerMenu.setPosition(left, top + 20);
		this.resizerMenu.show();
		this.isResizerMenuShow = true;
	},
	hideResizerMenu: function() {
		this.resizerMenu.hide();
		this.isResizerMenuShow = false;
	},
	setPanelSizes: function(leftSize) {
		var rightSize = 100 - leftSize;
		
		var allw = this.PanelsTable.el.getCoordinates().width;
		var lw = parseInt(allw * (leftSize / 100));
		
		this.left.pSize = leftSize;
		this.right.pSize = rightSize;
		
		this.ATPLeftDiv.el.setStyle('width', lw);
		
		this.updatePanelDivsWidth();
	},
	build: function() {
		if (!this.templateObject)
		{
			this.setTemplateBySelector(this.defaultTemplateSelector);
		}
		this.generalLayer = this.templateObject.clone();
		this.generalLayer.set({
			id: 'SoroPanel',
			APID: this.ID,
			events: {
				click: function() {
					var sp = WebStudio.Panels[this.getProperty('APID')];
					sp.hideResizerMenu();
				}
			}
		});
		
		this.generalLayer.injectInside(this.injectObject);
		this.elementsConfig = $merge(this.defaultElementsConfig, this.elementsConfig);
		this.applyElementsConfig();
		
		this.left = this.ATPLeftDiv;
		this.right = this.ATPRightDiv;
		
		this.slipWidth = parseInt(this.PanelsTable.el.getCoordinates().width / 2);
	},
	applyElementsConfig: function() {
		$each(this.elementsConfig, (function(item, index) {
			this.setElementConfig(item, index);
		}).bind(this));
		
		return this;
	},
	setElementConfig: function(item, index) {
		this[index] = [];
		if (item.selector) 
		{
			var els = this.generalLayer.getElementsBySelector(item.selector);
			
			if (els[0])
			{
				this[index].el = els[0];
			}
			
			this._tempIndex = index;
			this._tempItem = item;
			
			$each(els, (function(item, index) {
				var ob = this[this._tempIndex];
				ob[index] = {};
				ob[index].el = item;
				if (ob[index].el) 
				{
					ob[index].state = 'default';
					
					if (this._tempItem.styles)
					{
						ob[index].el.setStyles(this._tempItem.styles);
					}
					
					if (this._tempItem.events) {
						ob[index].el.set({
							'APID': this.ID, 
							'Group': this._tempIndex,
							'Index': index,
							events: this._tempItem.events
						});
					}
					
					if (this._tempItem.blockSelection)
					{
						this.blockSelection(ob[index].el);
					}
					
					if (this._tempItem.resizeHandler)
					{
						if (this._tempItem.resizeObject)
						{
							this.setResize(ob[index].el, this._tempItem.resizeObject, 'e-resize', 'updatePanelDivsWidth');
						}
					}
					
				}
			}).bind(this));
		}
	},
	setConfig: function(object) {
		this.elementsConfig = object;
		return this;
	},
	setResize: function(spyObj, resizeObjID, type, onResizeFuncName) {
		spyObj.set({
			APID: this.ID,
			rtype: type,
			ROID: resizeObjID,
			ORFN: onResizeFuncName,
			styles: {
				cursor: 'col-resize'
			},
			events: {
				mousedown: function(event) {
					var sc = WebStudio.Panels[this.getProperty('APID')];
					var roID = this.getProperty('ROID');
					var event = new Event(event);
					if (document.selection)
					{
						if (document.selection.empty)
						{
							document.selection.empty(); //Clear current selection for IE
						}
					}
					sc.mdx = event.client.x;
					sc.mdy = event.client.y;
					var cr = sc[roID].el.getCoordinates();
					sc.mdl = cr.left;
					sc.mdt = cr.top;
					sc.mdw = cr.width;
					sc.mdh = cr.height; 
					sc.re = this;
					sc.resizerIsMoved = false;
					
					sc.blockSelection(document.body);
					
					sc.mouseUpListen = true;
					sc.mouseUpListener = function() {
						this.stopResize();
					};
					
					sc._onResize = sc[this.getProperty('ORFN')];
					
					sc.mouseMoveListen = true;
					sc.mouseMoveListener = function(event) {
						var el = this.re;
						if (this.isBlockResize)
						{
							return;
						}
						
						this.generalLayer.setStyle('cursor', 'col-resize');
						
						event = new Event(event);
						event.clientX = event.client.x;
						event.clientY = event.client.y;
						if (!this.isMouseLeft(event)) 
						{
							this.stopResize();
						} 
						else if (this.isMouseLeft(event)) 
						{
							var sl = this.slipPx;
							var type = el.getProperty('rtype');
							if (type=="e-resize") 
							{
								this.resizerIsMoved = true;
								pw = parseInt(this.mdw) + event.clientX - this.mdx;
								if ((pw>=this.slipWidth - sl)&&(pw<=this.slipWidth + sl))
								{
									pw = this.slipWidth;
								}
								if ((pw <= this.maxWidth)&&(pw >= this.minWidth))
								{
									this[roID].el.style.width = pw + 'px';
								}
								else if (pw > this.maxWidth)
								{
									this[roID].el.style.width = this.maxWidth + 'px';
								}
								else if (pw < this.minWidth)
								{
									this[roID].el.style.width = this.minWidth + 'px';
								}
								
								this._onResize(this[roID].el.style.width);
							}
						}
					};
					
					event.preventDefault();
					
					return false;
				}
			}
		});
	},
	_onResize: function(width) {
		//this is a system function
	},
	updatePanelDivsWidth: function() {
		var wdth = this.ATPLeftDiv.el.getStyle('width').toInt() - 2;
		this.ATPLeftDiv.el.setStyle('width', Math.max(wdth,1));
	},
	restorePanelsProportion: function() {
		var leftSize = this.left.pSize;
		var rightSize = 100 - leftSize;
		this.left.pSize = leftSize;
		this.right.pSize = rightSize;
		var allw = this.PanelsTable.el.getCoordinates().width;
		var lw = allw * (leftSize / 100);
		this.ATPLeftDiv.el.setStyle('width', lw);
		
		this.updatePanelDivsWidth();
	},
	stopResize: function() {
		document.body.setStyle('cursor', '');
		this.generalLayer.setStyle('cursor', '');
		this.unblockSelection(document.body);
		this.mouseUpListen = false;
		this.mouseMoveListen = false;
		
		if (this.resizerIsMoved) 
		{
			var allw = this.PanelsTable.el.getCoordinates().width;
			var lw = this.ATPLeftDiv.el.getCoordinates().width.toInt() - 2;
			var leftSize = (lw/allw)*100;
			var rightSize = 100 - leftSize;
			
			this.left.pSize = leftSize;
			this.right.pSize = rightSize;
		}
	},
	setTemplateBySelector: function(selector) { //Set template of the window by selector(string)
		this.templateSelector = selector;
		this.templateObject = $(this.defaultContainer).getElementsBySelector(this.templateSelector)[0];
		if (!this.templateObject) 
		{
			this.templateObject = this.defaultContainer.getElementsBySelector(this.defaultTemplateSelector)[0];
			if (this.templateObject) 
			{
				this.templateSelector = this.defaultTemplateSelector;
				this.templateObject.remove();
			} 
			else 
			{
				this.addDebugMessages('error', 'Template object not found');
			}
		} 
		else 
		{
			this.templateObject.remove();
		}
		return this;
	},
	setTemplateByHTML: function(html) { //Set template of the window by html code
		this.templateObject = new Element('div', {});
		this.templateObject.setHTML(html);
		return this;
	},
	mouseMoveListen: false,
	mouseMoveListener: function() {
		//this is a system function
	},
	mouseUpListen: false,
	mouseUpListener: function() {
		//this is a system function
	},
	mouseDownListen: false,
	mouseDownListener: function() {
		//this is a system function
	},
	blockSelection: function(object) {
		object = $(object);
		if (typeof(object) == 'object') 
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
	},
	unblockSelection: function(object) {
		object = $(object);
		if (typeof(object) == 'object') 
		{
			object.onselectstart = null;
			object.setStyles({
				'-moz-user-select': '',
				'-khtml-user-select': '',
				'user-select': ''
			});
		}
	},
	isMouseLeft: function(event) {
		if (window.ie) 
		{
			if (event.event.button == 1)
			{
				return true;
			}
			else
			{
				return false;
			}
		} 
		else 
		{
			if (event.event.which == 1)
			{
				return true;
			}
			else
			{
				return false;
			}
		}
	},
	addDebugMessages: function(type, text, more) { //Add debug message in dojo debug container or alert it
		if (this.isAlertDM)
		{
			alert(text);
		}
		
		if (type == 'error') 
		{
			var mess = this.dmErrorTemplate.replace('$', text);
		} 
		else if (type == 'success') 
		{
			var mess = this.dmSuccessTemplate.replace('$', text);
		} 
		else 
		{
			var mess = text;
		}
	}
};

WebStudio.PanelsMouseMoveListener = function(event) 
{
	$each(WebStudio.Panels, function(item, index) {
		if (item) 
		{
			if (item.mouseMoveListen)
			{
				item.mouseMoveListener(event);
			}
		}
	});
};

WebStudio.PanelsMouseUpListener = function(event) 
{
	$each(WebStudio.Panels, function(item, index) {
		if (item) 
		{
			if (item.mouseUpListen)
			{
				item.mouseUpListener(event);
			}
		}
	});
};