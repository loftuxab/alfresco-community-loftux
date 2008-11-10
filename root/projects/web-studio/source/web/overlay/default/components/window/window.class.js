
if (typeof WebStudio == "undefined")
{
	var WebStudio = {};
}

WebStudio.WindowsZIndex = 1000;

WebStudio.Window = function(index) 
{
	this.defaultContainer = document.body;
	this.injectObject = document.body;

	this.ID = index;

	this.defaultTemplateSelector = 'div[id=AlfrescoWindowTemplate]';
	
	this.minWidth = 220;
	this.minHeight = 27;
	this.maxWidth = 1000;
	this.maxHeight = 800;
	this.slipWidth = 1000;
	this.slipHeight = 400;
	this.slipPx = 10;
	this.minimizeWidth = 220;
	this.minimizeHeight = 27;
	this.maximizeWidth = window.getSize().size.x;
	this.maximizeHeight = window.getSize().size.y;

	this.zIndexUpper = 0;

	this.allowResize = true;

	var _this = this;
	
	this.defaultElementsConfig = {
		titleLayerDiv: {
			selector: 'div[id=aw-title-div]',
			dragWindow: true,
			styles: {
				cursor: 'move'
			},
			events: {
				mousedown: function() {
					_this.setActive();
				}
			},
			fixedWidth: true,
			ownWidthToHide: 50,
			blockSelection: true
		},
		titleLayer: {
			selector: 'td[id=aw-title]',
			styles: {
				cursor: 'move'
			},
			events: {
				dblclick: function() {
					//var sw = WebStudio.Windows[this.getProperty('CID')];
					_this.maximize();
				}
			}
		},
		iconLayer: {
			selector: 'div[id=aw-icon]',
			styles: {
				'font-size': 1,
				'cursor': 'pointer',
				'z-index': 10
			},
			blockSelection: true,
			events: {
				click: function() {
				},
				mouseenter: function() {
					this.removeClass('AWIDefault');
					this.addClass('AWIRollover');
				},
				mouseleave: function() {
					this.removeClass('AWIRollover');
					this.addClass('AWIDefault');
				}
			}
		},
		RBArnor: {
			selector: 'div[id=aw-bottom-right-corner]',
			resizeWindow: 'se-resize',
			styles: {
				cursor: 'se-resize',
				'font-size': 1
			},
			fixedLeftPosition: 'right',
			fixedBottomPosition: true
		},
		LBArnor: {
			selector: 'div[id=aw-bottom-left-corner]',
			resizeWindow: 'sw-resize',
			styles: {
				cursor: 'sw-resize',
				'font-size': 1
			},
			fixedBottomPosition: true
		},
		RTArnor: {
			selector: 'div[id=aw-top-right-corner]',
			resizeWindow: 'ne-resize',
			styles: {
				cursor: 'ne-resize',
				'font-size': 1
			},
			fixedLeftPosition: 'right'
		},
		LTArnor: {
			selector: 'div[id=aw-top-left-corner]',
			resizeWindow: 'nw-resize',
			styles: {
				cursor: 'nw-resize',
				'font-size': 1
			}
		},
		RightBorder: {
			selector: 'div[id=aw-right-border]',
			resizeWindow: 'e-resize',
			styles: {
				cursor: 'e-resize',
				'font-size': 1
			},
			fixedLeftPosition: 'right',
			fixedHeight: true
		},
		LeftBorder: {
			selector: 'div[id=aw-left-border]',
			resizeWindow: 'w-resize',
			styles: {
				cursor: 'w-resize',
				'font-size': 1
			},
			fixedHeight: true
		},
		TopBorder: {
			selector: 'div[id=AWTopBorder]',
			resizeWindow: 'n-resize',
			styles: {
				cursor: 'n-resize',
				'font-size': 1
			},
			fixedWidth: true
		},
		BottomBorder: {
			selector: 'div[id=aw-bottom-border]',
			resizeWindow: 's-resize',
			styles: {
				cursor: 's-resize',
				'font-size': 1
			},
			fixedBottomPosition: true,
			fixedWidth: true
		},
		HeaderBg: {
			selector: 'div[id=aw-header-bg]',
			fixedWidth: true
		},
		Body: {
			selector: 'div[id=aw-body]',
			fixedWidth: true,
			fixedHeight: true
		},
		BodyContent: {
			selector: 'div[id=aw-body-content]'/*,
			fixedWidth: true,
			fixedHeight: true*/
		},
		ButtonClose: {
			selector: 'div[id=aw-button-close]',
			styles: {
				'font-size': 1,
				'display': 'none'
			},
			events: {
				click: function() {
					_this.hide();
					_this.onClose();
				},
				mouseenter: function() {
					this.setProperty('class', 'AWBRollover_cl')
				},
				mouseleave: function() {
					this.setProperty('class', 'AWBDefault_cl')
				},
				mousedown: function() {
					this.setProperty('class', 'AWBPush_cl')
				},
				mouseup: function() {
					this.setProperty('class', 'AWBDefault_cl')
				}
			},
			fixedLeftPosition: 'right'
		},
		ButtonMaximize: {
			selector: 'div[id=aw-button-maximize]',
			fixedLeftPosition: 'right',
			styles: {
				'font-size': 1,
				'display': 'none'				
			},
			events: {
				click: function() {
					_this.maximize();
				},
				mouseenter: function() {
					this.setProperty('class', 'AWBRollover_max')
				},
				mouseleave: function() {
					this.setProperty('class', 'AWBDefault_max')
				},
				mousedown: function() {
					this.setProperty('class', 'AWBPush_max')
				},
				mouseup: function() {
					this.setProperty('class', 'AWBDefault_max')
				}
			}
		},
		ButtonMinimize: {
			selector: 'div[id=aw-button-minimize]',
			fixedLeftPosition: 'right',
			styles: {
				'font-size': 1,
				'display': 'none'				
			},
			events: {
				click: function() {
					_this.minimize()
				},
				mouseenter: function() {
					this.setProperty('class', 'AWBRollover_min')
				},
				mouseleave: function() {
					this.setProperty('class', 'AWBDefault_min')
				},
				mousedown: function() {
					this.setProperty('class', 'AWBPush_min')
				},
				mouseup: function() {
					this.setProperty('class', 'AWBDefault_min')
				}
			}
		}
	};
	
	this.reCalcPositionObjects = {};
	this.reCalcPositionObjectsID = 0;
	this.resetStylesObjects = {};
	this.resetStylesObjectsID = 0;
	this.moveObjects = {};
	this.moveObjectsID = 0;
	
	this.isBlockResize = false;
	this.isBlockMove = false;
	
	this.position = 'free';	
}

WebStudio.Window.prototype = new WebStudio.AbstractTemplater('WebStudio.Window');

WebStudio.Window.prototype.disableResize = function(value)
{
	this.allowResize = !value;
}

WebStudio.Window.prototype.activate = function(templateID) {
	
	this.buildGeneralLayer(templateID);

	if (!WebStudio.WindowsML) {
		document.addListener('mousemove', WebStudio.WindowsMouseMoveListener);
		document.addListener('mouseup', WebStudio.WindowsMouseUpListener);
		WebStudio.WindowsML = true;
	}

	if (!this.minimizeFx) this.minimizeFx = new Fx.Styles(this.generalLayer, {
		duration: 300,
		transition: Fx.Transitions.linear,
		onStart: this.minimizeFxStart.bind(this),
		onComplete: this.minimizeFxComplete.bind(this)
	});

	if (!this.maximizeFx) this.maximizeFx = new Fx.Styles(this.generalLayer, {
		duration: 300,
		transition: Fx.Transitions.linear,
		onStart: this.maximizeFxStart.bind(this),
		onComplete: this.maximizeFxComplete.bind(this)
	});

	/*this.iconLayer.dragReturnFx = [];
	this.iconLayer.dragReturnFx[0] = new Fx.Styles(this.iconLayer.el, {
		duration: 2000,
		transition: Fx.Transitions.Elastic.easeOut
	});
	this.iconLayer.dragReturnFx[1] = new Fx.Styles(this.iconLayer.el, {
		duration: 2000,
		transition: Fx.Transitions.Elastic.easeIn
	});
	this.iconLayer.dragReturnFx[2] = new Fx.Styles(this.iconLayer.el, {
		duration: 2000,
		transition: Fx.Transitions.Elastic.easeInOut
	});*/

	/*this.iconLayer.drag = new Drag.Move(this.iconLayer.el, {
		onStart: function() {

		}/*,
		onComplete: function() {
			this.AW.iconLayer.dragReturnFx[0].start(this.positionCash)
		}
	});*/

	//this.iconLayer.drag.positionCash = {left: this.iconLayer.el.getStyle('left'), top: this.iconLayer.el.getStyle('top')};
	//this.iconLayer.drag.AW = this;

	return this;
}
WebStudio.Window.prototype.setContent = function(html) {
	this.BodyContent.el.setHTML(html);
	return this;
}
WebStudio.Window.prototype.setContentByObject = function(o) {
	this.BodyContent.el.empty();
	o.injectInside(this.BodyContent.el);
	return this;
}
WebStudio.Window.prototype.setTitle = function(text) {

	this.title = text;
	this.titleLayer.el.setHTML(text);
	return this;
}
WebStudio.Window.prototype.minimize = function() {
	if (this.position != 'minimized') {
		if (this.position == 'free')
		this.freePositions = {
			top: this.generalLayer.getStyle('top').toInt(),
			left: this.generalLayer.getStyle('left').toInt(),
			width: this.generalLayer.getStyle('width').toInt(),
			height: this.generalLayer.getStyle('height').toInt()
		}
		this.hideContent();
		this.minimizeFx.start({
			'height': this.minimizeHeight,
			'width': this.minimizeWidth,
			'left': 0,
			'top': 0
		});
		this.isBlockResize = true;
		this.blockMove();
		this.position = 'minimized';
	} else {
		this.minimizeFx.start(this.freePositions);
		this.minimizeFxShowContent = true;
		this.isBlockResize = false;
		this.unblockMove();
		this.position = 'free';
	}
	return this;
}
WebStudio.Window.prototype.minimizeFxComplete = function() {
	$clear(this.minimizeFxInterval);

	if (this.minimizeFxShowContent) {
		this.showContent();
		this.minimizeFxShowContent = false;
	}
	this.resize();
}
WebStudio.Window.prototype.minimizeFxStart = function() {
	this.minimizeFxInterval = this.resize.periodical(10, this);
}
WebStudio.Window.prototype.maximize = function() {
	this.maximizeWidth = this.getWindowSize().w;
	this.maximizeHeight = this.getWindowSize().h;
	if (this.position != 'maximized') {
		if (this.position == 'free')
		this.freePositions = {
			top: this.generalLayer.getStyle('top').toInt(),
			left: this.generalLayer.getStyle('left').toInt(),
			width: this.generalLayer.getStyle('width').toInt(),
			height: this.generalLayer.getStyle('height').toInt()
		}
		this.hideContent();
		this.maximizeFx.start({
			'height': this.maximizeHeight,
			'width': this.maximizeWidth,
			'left': 0,
			'top': 0
		});
		this.isBlockResize = true;
		this.blockMove();
		this.maximizeFxShowContent = true;
		this.position = 'maximized';
	} else {
		this.hideContent();
		this.maximizeFx.start(this.freePositions);
		this.maximizeFxShowContent = true;
		this.isBlockResize = false;
		this.unblockMove();
		this.position = 'free';
	}
	return this;
}
WebStudio.Window.prototype.maximizeFxComplete = function() {
	$clear(this.maximizeFxInterval);

	if (this.maximizeFxShowContent) {
		this.showContent();
		this.maximizeFxShowContent = false;
	}
	this.resize();
}
WebStudio.Window.prototype.maximizeFxStart = function() {
	this.maximizeFxInterval = this.resize.periodical(10, this);
}
WebStudio.Window.prototype.showContent = function() {
	this.BodyContent.el.setStyle('display', '');
	return this;
}
WebStudio.Window.prototype.hideContent = function() {
	this.BodyContent.el.setStyle('display', 'none');
	return this;
}
WebStudio.Window.prototype.blockMove = function() {
	$each(this.moveObjects, (function(item, index) {
		item.object[item.index].dragObject.detach();
	}).bind(this));
	this.isBlockMove = true;
	return this;
}
WebStudio.Window.prototype.unblockMove = function() {
	$each(this.moveObjects, (function(item, index) {
		item.object[item.index].dragObject.attach();
	}).bind(this));
	this.isBlockMove = false;
	return this;
}
WebStudio.Window.prototype.build = function() {
	var _this = this;
	this.generalLayer.set({
		//CID: this.ID,
		events: {
			mousedown: function() {
				//WebStudio.WindowsActiveID = this.getProperty('CID');
				_this.setActive();
			}
		}
	});
}

WebStudio.Window.prototype.onClose = function(){}

WebStudio.Window.prototype.setActive = function() {//Set this window active, bring to front and apply Active styles
	this.generalLayer.setStyle('z-index', WebStudio.WindowsZIndex + this.zIndexUpper);
	WebStudio.WindowsZIndex++;
	WebStudio.WindowsActive = this;
	return this;
}
WebStudio.Window.prototype.setElementConfig = function(item, index, ob, config, oel) {
	var o = ob[index];
	if (config.dragWindow) {
		o.dragObject = new Drag.Move(this.generalLayer, {
			handle: oel
		});

		var mo = this.moveObjects[this.moveObjectsID] = {};
		mo.index = index;
		mo.object = ob;
		mo.elObject = oel;
		o.moveObjectsID = this.moveObjectsID;
		this.moveObjectsID++;
	}

	if (config.resizeWindow) {
		this.setResize(oel, this.generalLayer, config.resizeWindow);
	}

	if (config.fixedLeftPosition) {
		var ro = this.reCalcPositionObjects[this.reCalcPositionObjectsID] = {};
		ro.index = index;
		ro.object = ob;
		ro.elObject = oel;
		ro.type = config.fixedLeftPosition;
		ro.offset = this.generalLayer.getStyle('width').toInt() - oel.getStyle('left').toInt();
		o.reCalcPositionObjectsID = this.reCalcPositionObjectsID;
		this.reCalcPositionObjectsID++;
	}

	if (config.fixedBottomPosition) {
		var ro = this.reCalcPositionObjects[this.reCalcPositionObjectsID] = {};
		ro.index = index;
		ro.object = ob;
		ro.elObject = oel;
		ro.type = 'bottom';
		ro.offset = this.generalLayer.getStyle('height').toInt() - oel.getStyle('top').toInt();
		o.reCalcPositionObjectsID = this.reCalcPositionObjectsID;
		this.reCalcPositionObjectsID++;
	}

	if (config.fixedWidth) {
		var ro = this.reCalcPositionObjects[this.reCalcPositionObjectsID] = {};
		ro.index = index;
		ro.object = ob;
		ro.elObject = oel;
		ro.type = 'width';
		ro.offset = this.generalLayer.getStyle('width').toInt() - oel.getStyle('width').toInt();
		o.reCalcPositionObjectsID = this.reCalcPositionObjectsID;
		this.reCalcPositionObjectsID++;
	}

	if (config.fixedHeight) {
		var ro = this.reCalcPositionObjects[this.reCalcPositionObjectsID] = {};
		ro.index = index;
		ro.object = ob;
		ro.elObject = oel;
		ro.type = 'height';
		ro.offset = this.generalLayer.getStyle('height').toInt() - oel.getStyle('height').toInt();
		o.reCalcPositionObjectsID = this.reCalcPositionObjectsID;
		this.reCalcPositionObjectsID++;
	}

	if (config.windowWidthToHide) {
		var rs = this.resetStylesObjects[this.resetStylesObjectsID] = {};
		rs.index = index;
		rs.object = ob;
		rs.elObject = oel;
		rs.type = 'windowHide';
		rs.width = config.windowWidthToHide;
		if (config.onHide) rs.onHide = config.onHide;
		if (config.onShow) rs.onShow = config.onShow;
		o.resetStylesObjectsID = this.resetStylesObjectsID;
		this.resetStylesObjectsID++;
	}

	if (config.ownWidthToHide) {
		var rs = this.resetStylesObjects[this.resetStylesObjectsID] = {};
		rs.index = index;
		rs.object = ob;
		rs.elObject = oel;
		rs.type = 'ownHide';
		rs.width = config.ownWidthToHide;
		if (config.onHide) rs.onHide = config.onHide;
		if (config.onShow) rs.onShow = config.onShow;
		o.resetStylesObjectsID = this.resetStylesObjectsID;
		this.resetStylesObjectsID++;
	}
}
WebStudio.Window.prototype.resize = function() {
	if (!this.allowResize) return;
	if (!(this.fireEvent('onresize') == false)) {
		this.resizeElements();
		this.resetStyles();
	}
}
WebStudio.Window.prototype.mouseMoveListen = false;
WebStudio.Window.prototype.mouseMoveListener = function() {
	//for external use
}
WebStudio.Window.prototype.resizeElements = function() {
	$each(this.reCalcPositionObjects, (function(item, index) {
		if (!item) return;
		if (item.type == 'right') {
			item.elObject.setStyle('left', this.generalLayer.getStyle('width').toInt() - item.offset);
		} else if (item.type == 'center') {
			var w = parseInt((this.generalLayer.getStyle('width').toInt() - item.elObject.getStyle('width').toInt()) / 2);
			item.elObject.setStyle('left', w);
		} else if (item.type == 'bottom') {
			item.elObject.setStyle('top', this.generalLayer.getStyle('height').toInt() - item.offset);
		} else if (item.type == 'width') {
			var w = this.generalLayer.getStyle('width').toInt() - item.offset
			if (w<0) w=0;
			item.elObject.setStyle('width', w);
		} else if (item.type == 'height') {
			item.elObject.setStyle('height', this.generalLayer.getStyle('height').toInt() - item.offset);
		}
	}).bind(this));
}
WebStudio.Window.prototype.resetStyles = function() {
	$each(this.resetStylesObjects, (function(item, index) {
		if (item.type == 'windowHide') {
			if ((this.generalLayer.getStyle('width').toInt() <= item.width)&&(!item.isHide)) {
				item.elObject.setStyle('display', 'none');
				item.isHide = true;
				if (item.onHide) item.onHide();
			} else if ((this.generalLayer.getStyle('width').toInt() > item.width)&&(item.isHide)) {
				item.elObject.setStyle('display', '');
				item.isHide = false;
				if (item.onShow) item.onShow();
			}
		} else if (item.type == 'ownHide') {
			//this.titleLayer.el.setHTML(item.width)
			if ((item.elObject.getStyle('width').toInt() <= item.width)&&(!item.isHide)) {
				item.elObject.setStyle('display', 'none');
				item.isHide = true;
				if (item.onHide) item.onHide()
			} else if ((item.elObject.getStyle('width').toInt() > item.width)&&(item.isHide)) {
				item.elObject.setStyle('display', '');
				item.isHide = false;
				if (item.onShow) item.onShow();
			}
		}
	}).bind(this));
}
WebStudio.Window.prototype.setResize = function(spyObj, resizeObj, type) {
	var _this = this;
	if(!this.allowResize) return false;  
	 spyObj.set({
		rtype: type,
		events: {
			mousedown: function(event) {

				if(!_this.isMouseLeft(event)) return false;
				var sw = _this;
				event = new Event(event);
				if (document.selection) if (document.selection.empty) document.selection.empty(); //Clear current selection for IE
				sw.mdx = event.client.x;
				sw.mdy = event.client.y;
				
				var coord = sw.generalLayer.getCoordinates();
				sw.mdl = coord.left;
				sw.mdt = coord.top;
				sw.mdw = coord.width;
				sw.mdh = coord.height;
				sw.re = this;

				sw.blockSelection(document.body);

				sw.mouseUpListen = true;
				sw.mouseUpListener = function() {
					this.stopResize();
				}

				sw.mouseMoveListen = true;

				sw.mouseMoveListener = function(event) {

					//debugger;
					var pw = null;
					var el = this.re;
					if (this.isBlockResize) return;
					event = new Event(event);
					event.clientX = event.client.x;
					event.clientY = event.client.y;

					var sl = this.slipPx;
					var type = el.getProperty('rtype');
					if (type == "e-resize") {
						pw = parseInt(this.mdw) + event.clientX - this.mdx;
						if ((pw >= this.slipWidth - sl) && (pw <= this.slipWidth + sl)) pw = this.slipWidth;
						if ((pw <= this.maxWidth) && (pw >= this.minWidth)) this.generalLayer.style.width = pw + 'px'
						else if (pw > this.maxWidth) this.generalLayer.style.width = this.maxWidth + 'px'
						else if (pw < this.minWidth) this.generalLayer.style.width = this.minWidth + 'px';
					} else if (type == "s-resize") {
						pw = parseInt(this.mdh) + event.clientY - this.mdy;
						if ((pw <= this.maxHeight) && (pw >= this.minHeight)) this.generalLayer.style.height = pw + 'px'
						else if (pw > this.maxHeight) this.generalLayer.style.height = this.maxHeight + 'px'
						else if (pw < this.minHeight) this.generalLayer.style.height = this.minHeight + 'px';
					} else if (type == "n-resize") {
						pw = parseInt(this.mdh) - (event.clientY - this.mdy);
						if ((pw <= this.maxHeight) && (pw >= this.minHeight)) {
							this.generalLayer.style.height = pw + 'px';
							this.generalLayer.style.top = parseInt(this.mdt) + (event.clientY - this.mdy) + 'px';
						} else if (pw > this.maxHeight) {
							this.generalLayer.style.height = this.maxHeight + 'px';
							this.generalLayer.style.top = parseInt(this.mdt) + (parseInt(this.mdh) - this.maxHeight) + 'px';
						} else if (pw < this.minHeight) {
							this.generalLayer.style.height = this.minHeight + 'px';
							this.generalLayer.style.top = parseInt(this.mdt) + (parseInt(this.mdh) - this.minHeight) + 'px';
						}
					} else if (type == "w-resize") {
						pw = parseInt(this.mdw) - (event.clientX - this.mdx);
						if ((pw <= this.maxWidth) && (pw >= this.minWidth)) {
							this.generalLayer.style.width = pw + 'px';
							this.generalLayer.style.left = parseInt(this.mdl) + (event.clientX - this.mdx) + 'px';
						} else if (pw > this.maxWidth) {
							this.generalLayer.style.width = this.maxWidth + 'px';
							this.generalLayer.style.left = parseInt(this.mdl) + (parseInt(this.mdw) - this.maxWidth) + 'px';
						} else if (pw < this.minWidth) {
							this.generalLayer.style.width = this.minWidth + 'px';
							this.generalLayer.style.left = parseInt(this.mdl) + (parseInt(this.mdw) - this.minWidth) + 'px';
						}
					} else if (type == "se-resize") {
						pw = parseInt(this.mdw) + event.clientX - this.mdx;
						if ((pw <= this.maxWidth) && (pw >= this.minWidth)) this.generalLayer.style.width = pw + 'px'
						else if (pw > this.maxWidth) this.generalLayer.style.width = this.maxWidth + 'px'
						else if (pw < this.minWidth) this.generalLayer.style.width = this.minWidth + 'px';
						pw = parseInt(this.mdh) + event.clientY - this.mdy;
						if ((pw <= this.maxHeight) && (pw >= this.minHeight)) this.generalLayer.style.height = pw + 'px'
						else if (pw > this.maxHeight) this.generalLayer.style.height = this.maxHeight + 'px'
						else if (pw < this.minHeight) this.generalLayer.style.height = this.minHeight + 'px';
					} else if (type == "ne-resize") {
						pw = parseInt(this.mdh) - (event.clientY - this.mdy);
						if ((pw <= this.maxHeight) && (pw >= this.minHeight)) {
							this.generalLayer.style.height = pw + 'px';
							this.generalLayer.style.top = parseInt(this.mdt) + (event.clientY - this.mdy) + 'px';
						} else if (pw > this.maxHeight) {
							this.generalLayer.style.height = this.maxHeight + 'px';
							this.generalLayer.style.top = parseInt(this.mdt) + (parseInt(this.mdh) - this.maxHeight) + 'px';
						} else if (pw < this.minHeight) {
							this.generalLayer.style.height = this.minHeight + 'px';
							this.generalLayer.style.top = parseInt(this.mdt) + (parseInt(this.mdh) - this.minHeight) + 'px';
						}
						pw = parseInt(this.mdw) + event.clientX - this.mdx;
						if ((pw <= this.maxWidth) && (pw >= this.minWidth)) this.generalLayer.style.width = pw + 'px'
						else if (pw > this.maxWidth) this.generalLayer.style.width = this.maxWidth + 'px'
						else if (pw < this.minWidth) this.generalLayer.style.width = this.minWidth + 'px';
					} else if (type == "sw-resize") {
						pw = parseInt(this.mdh) + event.clientY - this.mdy;
						if ((pw <= this.maxHeight) && (pw >= this.minHeight)) this.generalLayer.style.height = pw + 'px'
						else if (pw > this.maxHeight) this.generalLayer.style.height = this.maxHeight + 'px'
						else if (pw < this.minHeight) this.generalLayer.style.height = this.minHeight + 'px';
						pw = parseInt(this.mdw) - (event.clientX - this.mdx);
						if ((pw <= this.maxWidth) && (pw >= this.minWidth)) {
							this.generalLayer.style.width = pw + 'px';
							this.generalLayer.style.left = parseInt(this.mdl) + (event.clientX - this.mdx) + 'px';
						} else if (pw > this.maxWidth) {
							this.generalLayer.style.width = this.maxWidth + 'px';
							this.generalLayer.style.left = parseInt(this.mdl) + (parseInt(this.mdw) - this.maxWidth) + 'px';
						} else if (pw < this.minWidth) {
							this.generalLayer.style.width = this.minWidth + 'px';
							this.generalLayer.style.left = parseInt(this.mdl) + (parseInt(this.mdw) - this.minWidth) + 'px';
						}
					} else if (type == "nw-resize") {
						pw = parseInt(this.mdw) - (event.clientX - this.mdx);
						if ((pw <= this.maxWidth) && (pw >= this.minWidth)) {
							this.generalLayer.style.width = pw + 'px';
							this.generalLayer.style.left = parseInt(this.mdl) + (event.clientX - this.mdx) + 'px';
						} else if (pw > this.maxWidth) {
							this.generalLayer.style.width = this.maxWidth + 'px';
							this.generalLayer.style.left = parseInt(this.mdl) + (parseInt(this.mdw) - this.maxWidth) + 'px';
						} else if (pw < this.minWidth) {
							this.generalLayer.style.width = this.minWidth + 'px';
							this.generalLayer.style.left = parseInt(this.mdl) + (parseInt(this.mdw) - this.minWidth) + 'px';
						}
						pw = parseInt(this.mdh) - (event.clientY - this.mdy);
						if ((pw <= this.maxHeight) && (pw >= this.minHeight)) {
							this.generalLayer.style.height = pw + 'px';
							this.generalLayer.style.top = parseInt(this.mdt) + (event.clientY - this.mdy) + 'px';
						} else if (pw > this.maxHeight) {
							this.generalLayer.style.height = this.maxHeight + 'px';
							this.generalLayer.style.top = parseInt(this.mdt) + (parseInt(this.mdh) - this.maxHeight) + 'px';
						} else if (pw < this.minHeight) {
							this.generalLayer.style.height = this.minHeight + 'px';
							this.generalLayer.style.top = parseInt(this.mdt) + (parseInt(this.mdh) - this.minHeight) + 'px';
						}
					}

					this.resize();
					//}
				};
				event.preventDefault();
				return false;
			}
		}
	});
}

WebStudio.Window.prototype.stopResize = function() {
	$(document.body).setStyle('cursor', '');
	this.unblockSelection(document.body);
	this.mouseUpListen = false;
	this.mouseMoveListen = false;
}

WebStudio.WindowsMouseMoveListener = function(event) {
	$each(WebStudio.Windows, function(item, index) {
		if (item) {
			if (item.mouseMoveListen) item.mouseMoveListener(event);
		}
	})
}

WebStudio.WindowsMouseUpListener = function(event) {
	$each(WebStudio.Windows, function(item, index) {
		if (item) {
			if (item.mouseMoveListen) item.mouseMoveListener(event);
			if (item.mouseUpListen) item.mouseUpListener(event);
		}
	})
}