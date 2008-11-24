if (typeof WebStudio == "undefined" || !WebStudio)
{
	WebStudio = {};
}

WebStudio.DropDown = function() 
{
	this.defaultTemplateSelector = 'div[id=AlfDropDownTempalte]';
	
	this.defaultContainer = document.body;
	this.injectObject = document.body;	

	this.defaultElementsConfig = {
		AlfDropDownItm:{
			selector:'div[id=AlfDropDownItm]',
			remove:true
		},
		AlfDropDownCaptionHolder:{
			selector:'span[id=AlfDropDownCaptionHolder]'
		},
		AlfDropDownItmHolder:{
			selector:'div[id=AlfDropDownItmHolder]',
			remove:true
		},
		AlfDropDownHolder:{
			selector:'span[id=AlfDropDownHolder]'
		}
	};
	this._init();
	this.container = null;
};

WebStudio.DropDown.prototype = new WebStudio.AbstractTemplater('WebStudio.DropDown');

WebStudio.DropDown.prototype._init = function()
{
	var _this = this;
	
	document.addEvent('click', function() {
		if (_this.container)
		{
			_this.container.hide();
		}
	});
};

WebStudio.DropDown.prototype.addItem = function(id,value)
{
	this.container.addItem(id,value);
};

WebStudio.DropDown.prototype.activate = function()
{
	this.buildGeneralLayer();
	this.container = this.buildRootItem("","");
};

WebStudio.DropDown.prototype.selectItem = function(id)
{
	var im = this.container.items[id];
	var ci = im.getElementsBySelector("div[id=AlfDropDownItmCaption]")[0];
	this.AlfDropDownCaptionHolder.el.setHTML(ci.innerHTML);
	this.onItemClick(id);
};

WebStudio.DropDown.prototype.onItemClick = function(id,data)
{
};

WebStudio.DropDown.prototype.buildRootItem = function(id,value)
{
	var he = this.AlfDropDownHolder.el;
	this.AlfDropDownCaptionHolder.el.setHTML(value);
	var ri = new WebStudio.DropDownRootItem(he,this);

	var ch = this.AlfDropDownHolder.el;
		ch.addEvent("mouseover", function(e) {
            e = new Event(e);
			this.addClass("selected");
            e.stop();
        });
        ch.addEvent("mouseout", function(e) {
            e = new Event(e);
			this.removeClass("selected");
            e.stop();
        });

	return ri;
};

WebStudio.DropDown.prototype.show = function()
{
	this.generalLayer.style.display = "";
	var fs = new Fx.Styles(this.generalLayer, {
		duration: 0,
		transition: Fx.Transitions.linear
	});
	fs.start({'opacity': 1});
	this.isHide = false;
	this.isShow = true;
	return this;
};

WebStudio.DropDown.prototype.showFast = function()
{
	this.generalLayer.setOpacity(1);
	this.generalLayer.style.visibility = '';
	this.isHide = false;
	this.isShow = true;
	return this;
};

WebStudio.DropDown.prototype.hide = function()
{
	var fs = new Fx.Styles(this.generalLayer, {
		duration: 0,
		transition: Fx.Transitions.linear
	});
	fs.start({'opacity': 0});

	this.isHide = true;
	this.isShow = false;
	return this;
};

WebStudio.DropDownRootItem = function(he,menu)
{

	this.he = he;
	this.items = {};
	this.menu = menu;
	this.ih = this.menu.AlfDropDownItmHolder.el.clone();
    this.ih.injectInside(document.body);
    this.ch = this.he.getElementsBySelector('span[id=AlfDropDownCaptionHolder]')[0];
	this._init();
};

WebStudio.DropDownRootItem.prototype._init = function()
{
	var _this = this;

	this.he.addEvent("click",function(e){
		 e = new Event(e);
		_this.onRootItemClick();
		 e.stop();
	});
};

WebStudio.DropDownRootItem.prototype.onRootItemClick = function()
{
	if(this.menu.activeRootItem)
	{
		this.menu.activeRootItem.hide();
	}
	this.show();
};

WebStudio.DropDownRootItem.prototype.hide = function(){
	this.ih.style.display = "none";
};

WebStudio.DropDownRootItem.prototype.show = function(){
	this.ih.style.display = "";
    this.ih.style.top = (this.he.offsetTop + this.menu.generalLayer.offsetHeight)+ "px";
    this.ih.style.left = this.menu.generalLayer.offsetLeft + "px";
};

WebStudio.DropDownRootItem.prototype.addItem = function(id,value)
{
	var _this = this;

	var im = this.menu.AlfDropDownItm.el.clone();
	var ci = im.getElementsBySelector("div[id=AlfDropDownItmCaption]")[0];
	ci.setHTML(value);
	var img = im.getElementsBySelector("img[id=AlfDropDownItmIcon]")[0];

    im.injectInside(this.ih);

        im.addEvent("click", function(e) {
			_this.menu.AlfDropDownCaptionHolder.el.setHTML(value);
			_this.menu.onItemClick(id);
            _this.hide();
        });
        im.addEvent("mouseover", function(e) {
            e = new Event(e);
            this.addClass("selected-i");
            e.stop();
        });
        im.addEvent("mouseout", function(e) {
            e = new Event(e);
            this.removeClass("selected-i");
            e.stop();
        });

    this.items[id] = im; 
};