if (typeof WebStudio == "undefined" || !WebStudio)
{
	WebStudio = {};
}

WebStudio.MenuNew = function() 
{
	this.defaultContainer = document.body;
	this.injectObject = document.body;

	this.defaultTemplateSelector = 'div[id=AlfMenuTemplate]';

	this.defaultElementsConfig = {
		AlfRootMenuItm:{
			selector:'div[id=AlfRootMenuItm]',
			remove:true
		},
		AlfRootMenuItmCheckBox:{
			selector:'div[id=AlfRootMenuItmCheckBox]',
			remove:true
		},
		AlfRootMenuItmSep:{
			selector:'div[id=AlfRootMenuItmSep]',
			remove:true
		},
		AlfRootMenuItmRadio:{
			selector:'div[id=AlfRootMenuItmRadio]',
            remove:true
		},
		AlfRootMenuItmLink:{
			selector:'div[id=AlfRootMenuItmLink]',
            remove:true
		},
		AlfRootMenuCaptionImg:{
			selector:'img[id=AlfRootMenuCaptionImg]',
            remove:true
		},
		AlfRootMenuItmHolder:{
			selector:'div[id=AlfRootMenuItmHolder]',
            remove:true
        },
		AlfRootMenuHolder:{
			selector:'span[id=AlfRootMenuHolder]',
			remove:true
		}
	};

	this.rootItems = {};
	this.activeRootItem = null;
	this.stretcher = null;
	this._init();
};

WebStudio.MenuNew.prototype = new WebStudio.AbstractTemplater('WebStudio.MenuNew');

WebStudio.MenuNew.prototype._init = function()
{
	var _this = this;
	document.addEvent('click', function() {
		if(_this.activeRootItem)
		{
			_this.activeRootItem.hide();
		}
	});

	this.stretcher = new WebStudio.Stretcher({size: 12,weight: "normal"});
};

WebStudio.MenuNew.prototype.activate = function()
{
	this.buildGeneralLayer();
};

WebStudio.MenuNew.prototype.onItemClick = function(id,data)
{
};

WebStudio.MenuNew.prototype.addRootItem = function(id,value,imgSrc)
{
	var he = this.AlfRootMenuHolder.el.clone();
	he.injectInside(this.generalLayer);
	
	var ch = he.getElementsBySelector('span[id=AlfRootMenuCaptionHolder]')[0];
	ch.setHTML(value);

	if (imgSrc) 
	{
		var ih = this.AlfRootMenuCaptionImg.el.clone();
		var h =  he.getElementsBySelector('span[id=AlfRootMenuCaptionImgholder]')[0];
		ih.injectInside(h);
		ih.src = imgSrc; 
	}
	
	var ri = new WebStudio.MenuRootItem(id,he,this);
	this.rootItems[id] = ri;
	return ri; 
};

WebStudio.MenuNew.prototype.show = function()
{
	this.generalLayer.style.visibility = '';
	this.isHide = false;
	this.isShow = true;
	return this;
};

WebStudio.MenuNew.prototype.showFast = function()
{
	this.generalLayer.style.visibility = '';
	this.isHide = false;
	this.isShow = true;
	return this;
};

WebStudio.MenuNew.prototype.hide = function()
{
	this.generalLayer.style.visibility = 'hidden';
	this.isHide = true;
	this.isShow = false;
	return this;
};

WebStudio.MenuRootItem = function(id,he,menu)
{
	this.id = id;
	this.he = he;
	this.items = {};
	this.menu = menu;
	this.ih = this.menu.AlfRootMenuItmHolder.el.clone();
    this.ih.injectInside(document.body);
    this.ch = this.he.getElementsBySelector('span[id=AlfRootMenuCaptionHolder]')[0];
	this._init();
};

WebStudio.MenuRootItem.prototype._init = function()
{
	var _this = this;

	this.he.addEvent("click",function(e){
		 e = new Event(e);
		_this.onRootItemClick();
		 e.stop();
	});

	this.he.addEvent("mouseover",function(e){
		e = new Event(e);
        if(_this.menu.activeRootItem)
        {
        	_this.onRootItemClick();
        }
        this.addClass("selected");
		e.stop();
	});

	this.he.addEvent("mouseout",function(e){
		e = new Event(e);
        this.removeClass("selected");
		e.stop();
	});
};

WebStudio.MenuRootItem.prototype.onRootItemClick = function()
{
	if(this.menu.activeRootItem)
	{
		this.menu.activeRootItem.hide();
	}
	this.show();
};


WebStudio.MenuRootItem.prototype.hide = function(){
	this.menu.activeRootItem = null;
	this.ih.style.display = "none";
};

WebStudio.MenuRootItem.prototype.show = function(){
    this.ih.style.display = "";
    this.ih.style.top = (this.he.offsetTop + this.menu.generalLayer.offsetHeight)+ "px";
    this.ih.style.left = this.he.offsetLeft + "px";
    this.menu.activeRootItem = this;
};

WebStudio.MenuRootItem.prototype.disableItem = function(id,value)
{
	var im = this.items[id];
	if(!im)
	{
		return;
	}
	
	if(value)
	{
		im.addClass("disable-i");
	}
	else
	{
		im.removeClass("disable-i");
	}
};

WebStudio.MenuRootItem.prototype.addItem = function(id,value,type,imgSrc,data)
{
	var sValue  = this.menu.stretcher.stretchText(value,150); 
	var _this = this;
	type = type||"text";
	data = data||{};
	var im = null;
	var ci = null;
	var img = null;
	switch(type)
	{
		case 'text':
			im = this.menu.AlfRootMenuItm.el.clone();
			ci = im.getElementsBySelector("div[id=AlfRootMenuItmCaption]")[0];
			ci.setHTML(sValue);
			if (imgSrc && imgSrc !== "") 
			{
				img = im.getElementsBySelector("img[id=AlfRootMenuItmIcon]")[0];
				img.src = imgSrc;
			}
			else 
			{
				im.getElementsBySelector("img[id=AlfRootMenuItmIcon]")[0].remove();
			}
			break;
		case 'radio':
			im = this.menu.AlfRootMenuItmRadio.el.clone();
			ci = im.getElementsBySelector("div[id=AlfMenuItmRadioCaption]")[0];
			ci.setHTML(sValue);
			break;
		case 'checkbox':
			im = this.menu.AlfRootMenuItmCheckBox.el.clone();
			ci = im.getElementsBySelector("div[id=AlfMenuItmCheckBoxCaption]")[0];
            im.check = function (checked) {
                this.getElementsBySelector("input[id=AlfMenuItmCheckBox]")[0].checked = checked;
            };
			ci.setHTML(sValue);
			break;
		case 'link':
			im = this.menu.AlfRootMenuItmLink.el.clone();
			img = im.getElementsBySelector("img[id=AlfMenuItmIcon]")[0];
			img.src = imgSrc;
			var li = im.getElementsBySelector("a[id=AlfMenuItmLink]")[0];
			li.href = (data.href ? data.href : "");
			li.innerHTML = sValue;
			break;
		case 'separator':
			im = this.menu.AlfRootMenuItmSep.el.clone();
			break;

	}

    im.injectInside(this.ih);
    if (type == 'checkbox') 
    {
        im.check(data.checked);
    }
    if (type != "separator" && !data.disable) 
    {
		if (type != "link") 
		{
			im.addEvent("click", function(e) {
				//e = new Event(e);
				_this.menu.onItemClick(id, data);
				_this.hide();
            	//e.stop();
			});
		}
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
		im.title = value;

	}
	
	if(data.disable)
	{
		im.addClass("disable-i");
	}
	
	this.items[id] = im;
};