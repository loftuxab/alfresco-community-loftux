
if (typeof WebStudio == 'undefined')
{
	WebStudio = {};
}

WebStudio.Menu = function(index) 
{
	this.defaultContainer = document.body;
	this.injectObject = document.body;
	
	this.ID = index;
	this.defaultTemplateSelector = 'div[id=AlfrescoMenuTemplate]';

	this.defaultElementsConfig = {
		roots: {
			selector: '.AMRoot',
			holder: 'div[id=AMHolder]',
			events: {
				mouseenter: function() {
					var am = WebStudio.Menus[this.getProperty('ACID')];
					this.removeClass('PushItemRoot');
					if (!this.hasClass('Disabled')) this.addClass('RolloverItemRoot');
					if (am.isOpenHolder) {
						am.showHolderFast(this.getProperty('Group'), this.getProperty('Index'));
                    }
				},
				mouseleave: function() {
					this.removeClass('PushItemRoot');
					this.removeClass('RolloverItemRoot');
				},
				mousedown: function() {
					if (!this.hasClass('Disabled'))this.addClass('PushItemRoot');
					this.removeClass('RolloverItemRoot');
				},
				mouseup: function() {
					this.removeClass('PushItemRoot');
					if (!this.hasClass('Disabled'))this.addClass('RolloverItemRoot');
				},
				click: function(event) {
					var event = new Event(event);
					var am = WebStudio.Menus[this.getProperty('ACID')];
					var ob = am[this.getProperty('Group')][this.getProperty('Index')];
					am.deselectAll(this.getProperty('Group'));
					if (!this.hasClass('Disabled')) {
						ob.state = 'selected';
						am.select(this.getProperty('Group'), this.getProperty('Index'));
						am.showHolderFast(this.getProperty('Group'), this.getProperty('Index'));
					}
				}
			},
			objects: {
				Caption: {
					selector: '.AMItemCaption'
				}
			}
		},
		levels: {
			selector: '.AMItem',
			holder: 'div[id=AMHolder]',
			events: {
				mouseenter: function() {
					this.removeClass('PushItem');
					if(!this.hasClass('Disabled')) this.addClass('RolloverItem');
				},
				mouseleave: function() {
					this.removeClass('PushItem')
					if(!this.hasClass('Disabled')) this.removeClass('RolloverItem')
				},
				mousedown: function() {
                    this.removeClass('RolloverItem');
                    if(!this.hasClass('Disabled')) this.addClass('PushItem');
				},
				mouseup: function() {
					this.removeClass('PushItem');
					if(!this.hasClass('Disabled')) this.addClass('RolloverItem');
				}
            }
		}
	}

	this.events = {};
}

WebStudio.Menu.prototype = new WebStudio.AbstractTemplater('WebStudio.Menu');

WebStudio.Menu.prototype.activate = function(templateID) 
{
	this.buildGeneralLayer(templateID);

	this.isHide = true;
	this.isShow = false;

	this.showFx = new Fx.Styles(this.generalLayer, {
		duration: 400,
		transition: Fx.Transitions.linear
	});

	this.hideFx = new Fx.Styles(this.generalLayer, {
		duration: 0,
		transition: Fx.Transitions.linear
	});

	document.addEvent('click', (function() {
		this.isOpenHolder = false
		this.deselectAll('roots');
		this.deselectAll('rootsEditingMode');
		this.deselectAll('rootsEditingModeArrow');
	}).bind(this))

	return this
}
WebStudio.Menu.prototype.hide = function() 
{
	var fs = new Fx.Styles(this.generalLayer, {
		duration: 0,
		transition: Fx.Transitions.linear
	});
	fs.start({'opacity': 0});
	//this.generalLayer.style.visibility = 'hidden';
	this.isHide = true;
	this.isShow = false;
	return this;
}
WebStudio.Menu.prototype.show = function() 
{
	this.showFx.start({'opacity': 1});
	this.isHide = false;
	this.isShow = true;
	return this;
}
WebStudio.Menu.prototype.showFast = function() 
{
	this.generalLayer.setOpacity(1);
	this.isHide = false;
	this.isShow = true;
	return this;
}
WebStudio.Menu.prototype.setDisabled = function(group, index) {
	var m = this[group][index];
	if(m) {
		m.el.addClass('Disabled');
		m.el.removeClass('RolloverItem');
		m.el.removeClass('PushItem');
		m.el.setOpacity(0.4);
	}
	return this;
}
WebStudio.Menu.prototype.setEnabled = function(group, index) {
	var m = this[group][index];
	if(m) {
		m.el.removeClass('Disabled');
		m.el.setOpacity(1);
	}
	return this;
}
WebStudio.Menu.prototype.showHolderFast = function(group, index) {
	var ob = this[group][index];
	if (ob) if (ob.holder) {
		this.deselectAll(group);
		ob.holder.setOpacity(1);
	}
	this.isOpenHolder = true;
}
WebStudio.Menu.prototype.hideHolder = function(el) {
	var fx = new Fx.Styles(el, {wait: false, duration: 100})
	fx.start({'opacity': 0})
	this.isOpenHolder = false
}
WebStudio.Menu.prototype.select = function(group, index) {
	this.fireEvent('click', [group, index])
}
WebStudio.Menu.prototype.setElements = function() {

}
WebStudio.Menu.prototype.deselectAll = function(group) {
	if (this[group]) {
		$each(this[group], (function(item, index) {
			if (item.holder) item.holder.setOpacity(0);
		}).bind(this))
	}
	return this;
}
WebStudio.Menu.prototype.closeAll = function(group) {
	this.isOpenHolder = false;
	this.deselectAll(group);
	return this;
}
WebStudio.Menu.prototype.build = function(templateID) {
	this.generalLayer.set({
		id: this.ID + 'Default',
		ACID: this.ID,
		styles: {
			'opacity': 0
		},
		events: {
			click: function(event) {
				var event = new Event(event)
				event.stopPropagation()
			}
		}
	})
}
WebStudio.Menu.prototype.setElementConfig = function(item, index, ob, config, oel, configIndex) {
//'item' = html dom element, 'index' = name of array of 'item's; ob = js container for 'index';
//config = js object with configuration for item
	var o = ob[index];
	if(config.events) {
		oel.set({
			'Group': configIndex,
			'Index': index
		});
		oel.addEvents(config.events);
	}

	if (config.holder) {
		var h = oel.getElementsBySelector(config.holder)[0];
		if (h) {
			h.setOpacity(0);
			o.holder = h;
			o.holderMorph = new Fx.Styles(o.holder, {wait: false, duration: 100});
			o.holderMorph.holder = h;
		}
	}
}


