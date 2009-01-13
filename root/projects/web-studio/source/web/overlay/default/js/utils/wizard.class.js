if (typeof WebStudio == "undefined" || !WebStudio)
{
	WebStudio = {};
}

WebStudio.Wizard = function()
{
	this.pages = [];
	this.isEnd = false;
	this.isStart = false;
	this.activewPage = {};
	this.waitWindow = null;
	this._init();
	this.config = null;
	
	this.wizardFinished = false;
};

WebStudio.Wizard.prototype._init = function()
{
	this.buildWaitWindow();
};

WebStudio.Wizard.prototype.onComplete = function()
{
};

WebStudio.Wizard.prototype.detach = function()
{
	for(var i = 0; i < this.pages.length;i++){
		this.pages[i].window.unblock();
		this.pages[i].window.destroy();
		this.pages[i] = null;
	}
};

WebStudio.Wizard.prototype.start = function(url,id)
{
	this.isEnd = false;
	this.isStart = true;
	var data = this.getDefaultJSONRequest(id,this.defaultJson);
	this.loadFormData(url,data);
};

WebStudio.Wizard.prototype.stop = function()
{
	this.isEnd = true;
	this.isStart = false;
	this.onComplete();
	this.detach();
};

WebStudio.Wizard.prototype.loadFormData = function(url, json)
{
	var _this = this;
	
	var _json = Json.toString(json);
	_json = Alf.urlEncode(_json);
	
	// TODO: Not happy with this, need to fix the way that
	// URLs are constructed
	if(url.indexOf("?") > -1)
	{
		url = url + "&";
	}
	else
	{
		url = url + "?";
	}
	url = url + "json=" + _json;
	
	this.call = YAHOO.util.Connect.asyncRequest('GET', url, {
	
		success: function(r) {
			
			var d = eval('(' + r.responseText + ')');
			
			_this.closeWaitWindow();
			if(_this.activewPage.window)
			{
				_this.activewPage.window.destroy();
			}
			
			var proceed = true;
			
			if(d.current)
			{
				if(d.current.code == "finish")
				{
					if(d.current.cacheInvalidateAll)
					{
						WebStudio.app.refreshObjectCache();
					}
					if(d.current.reload)
					{
						window.location.reload(true);
					}
					
					_this.wizardFinished = true;
					
					proceed = false;
				}
			}
			
			if(proceed)
			{
				_this.buildPage(d);
			}
			else
			{
        		_this.stop();
        		_this.closeWaitWindow();			
			}
		}
		,
		failure: function(r) {
		
			alert("loadFormData failed: " + r.responseText);
		}
	});
};

WebStudio.Wizard.prototype.onFailure = function(data)
{
	this.closeWaitWindow();
	WebStudio.App.onFailure(data);
};

WebStudio.Wizard.prototype.getDefaultJSONRequest = function(windowId, defaultJson)
{
	var json = {};
	json["windowId"] = windowId;
	json["schema"] = "adw10";

	if(defaultJson)
	{
		for(var key in defaultJson)
		{
			if(defaultJson.hasOwnProperty(key))
			{
				var val = defaultJson[key];
				json[key] = val;
			}
		}
	}

	return json;
};

WebStudio.Wizard.prototype.setDefaultJson = function(defaultJson)
{
	this.defaultJson = 	defaultJson;
};

WebStudio.Wizard.prototype.buildPage = function(data)
{
	var p = new WebStudio.WizardPage(data, this);	
	this.activewPage = p;

	if(this.pages.length > 0)
	{
		var pp = this.pages[this.pages.length - 1];
		pp.nextPage = p;
		p.previousPage = pp;
	}

	this.pages.push(p);
	p.id = this.pages.length -1;
	this.onCreate(p.id);
};

WebStudio.Wizard.prototype.onCreate = function (id) {
};

WebStudio.WizardPage = function(data, wizard)
{
	 this.url = "";
	 this.wizard = wizard;
	 this.nextPage = null;
	 this.previousPage = null;
	 this.data = data;
	 this.config = null;
	 this.formConfig = null;
	 this.id = -1;
	 this.window = null;
	 this.form = null;
	 this._init(data);

};

WebStudio.WizardPage.prototype.onButtonClick = function(action,data)
{
	var _this = this;
	if (action == "transition") 
	{	
		var o = this.wizard.getDefaultJSONRequest(this.wizard.config.windowId,this.wizard.defaultJson);
		o.elements = this.form.getFormData();
		o["currentPageId"] = this.config.id;
		o["requestedPageId"] = data||"";

		var url = WebStudio.ws.studio(this.config.uri);
		this.wizard.showWaitWindow();
		this.wizard.loadFormData(url, o);
	}
	else if (action == "cancel")
	{
		this.wizard.stop();

	}
	else if (action == "close")
	{
        	this.wizard.stop();
        	this.wizard.closeWaitWindow();
  	}
	
	if(this.config.cacheInvalidateAll)
	{
		WebStudio.app.refreshObjectCache();
	}

	if(this.config.reload)
	{
		window.location.reload(true);
	}
};

WebStudio.WizardPage.prototype.parseFormData = function(formData) 
{
	var _this = this;
	var elements = formData.elementformats || [];
	var property = formData.elements || [];
	var buttons = formData.buttons || [];
	var data = {};
	
	for(var i=0;i<elements.length;i++)
    {
		var el = elements[i];
		var elementType = this.getElementFormatValue(formData, el.name, "type");
		var elementValue = el.value;
		elementValue = unescape(elementValue);
		data[el.name] = {};
		var d = data[el.name];
		d.controls = [];

		if ("radio" == elementType)
		{
			d["type"] = "radio";
			d["content"] = "body";
			d["name"] = el.name;
			var radioData = this.getElementSelectionValues(formData, el.name);
			for(var xx = 0; xx < radioData.length; xx++)
			{
				var rb = {};
				var radioRowValue = radioData[xx][0];
				var radioRowText = radioData[xx][1];

				rb["value"] = radioRowValue;
				rb["label"] = radioRowText;
				rb["name"] = el.name;

				if (elementValue == radioRowValue)
				{
					d["checked"] = true;
				}
				d.controls.push(rb);
			}
		 }
		 if ("combo" == elementType)
		 {
			var comboData = this.getElementSelectionValues(formData, el.name);         	      	
			d["value"] = comboData;         	
			d["width"] = el.width;
			d["name"] = el.name;
			d["title"] = el.title;
			d["text"] = "text";
			d["type"] = "combo";
			d["content"] = "body";
			d["selectedValue"] = property[i].value;		
			var emptyText = this.getElementFormatValue(formData, el.name, "emptyText");
			if(emptyText)
			{
				d["emptytext"] = emptyText;
			}
		}
		else
		{
			d["type"] = (el.type == "textfield" ? "text" : (el.type || "hidden"));
			d["value"] = property[i].value;
			d["label"] = (el.label ? el.label + ":" : "");
			d["content"] = "body";
			d["name"] = el.name;
			d["style"] = {};
			var s = d["style"];
			s["width"] = (el.width?el.width:290);	
		}       
   	}

	this.parseFooterControls(data, buttons);

	return data;
};

WebStudio.WizardPage.prototype.getElementFormatValue = function(data, name, propertyName)
{
	var array = data.elementformats;
	if(array)
	{
		for(var i = 0; i < array.length; i++)
		{
			var _name = array[i].name;
			if(_name == name)
			{
				return array[i][propertyName];
			}
		}
	}
	return null;
};

WebStudio.WizardPage.prototype.getElementSelectionValues = function(data, elementId)
{
	var array = data.elementvalues;
	if(array)
	{
		var values = array[elementId];
		return values;
	}
	return null;
};

WebStudio.WizardPage.prototype.parseFooterControls = function(data, footerControls)
{
	var _this = this;
	
	var f = function(action, data)
	{
		_this.onButtonClick(action,data);
	};
	
	for(var i=0;i<footerControls.length;i++)
	{
		var b = footerControls[i];
		data[b.id] = {};
		var el = data[b.id];
		el["type"] = "button";
		el["action"] = b.action;
		el["enabled"] = b.enabled;
		el["hidden"] = b.hidden;
		el["text"] = b.text;
		el["content"] = "footer";
		el["data"] = (b.data?b.data:"");
		el["events"] = {};
		var ev = el["events"];
		ev["click"] = f;
	}
};

WebStudio.WizardPage.prototype.parsePageData = function(data)
{
	var config =  (data.current?data.current:null);
	return config;
};

WebStudio.WizardPage.prototype.parseWizardConfig = function(data)
{
	var config = {};
	config.windowId = data.windowId;
	config.schema = data.schema;
	config.pages = data.pages;

	return config;
};

WebStudio.WizardPage.prototype.parseURLData = function(data)
{
	var _this = this;
	var config = {};
	
	var cf = {};
	cf.type = "url";
	cf.content = "body";
	
	config["url"] = cf;
	
	var buttons = data.buttons||[];
	
	this.parseFooterControls(config, buttons);
	return config;
};

WebStudio.WizardPage.prototype.parseCheckboxGridData = function(data)
{
	var _this = this;
	var config = {};
	var cf = {};
	
	cf.type = "grid";
	cf.content = "body";
	
	config["grid"] = cf;

	var columnFormats = data.grid.columnformats||[];
	var toolbar = data.grid.toolbar||null;
	var columns = data.grid.columns||[];
	var dataArr = data.grid.griddata||null;
	var nodatamessage = data.grid.nodatamessage||null;
	var buttons = data.buttons||[];
	
	if (toolbar)
	{
		cf.toolbar = toolbar.filter(function(item, index){return "-" != item ;});
		cf.toolbarhandler = WebStudio.App.onMenuItemClick;
	} 
	
	cf.nodatamessage = nodatamessage;
	
	if (dataArr)
	{
		cf.dataSource = dataArr;
		cf.columnFormats = columnFormats;
		cf.columns = columns;
	} 
	else
	{
		cf.dataSource = null;
	}	
	
	this.parseFooterControls(config, buttons);
	
	return config;
};

WebStudio.Wizard.prototype.closeWaitWindow = function () 
{
    var _this = this;
    _this.waitWindow.unblock();
    _this.waitWindow.hide();
};

WebStudio.Wizard.prototype.buildWaitWindow = function() 
{
   var _this = this;
   this.waitWindow = new WebStudio.Window();

   this.waitWindow.setConfig({
      AWMessageContainerPB:{
         selector: 'div[id=alf-mess-box-pb-content]'
      }
   });

   this.waitWindow.setTemplateByDOMObject(WebStudio.App.AlfrescoMessageBoxProgressBar.el);
   this.waitWindow.activate();
   WebStudio.util.pushHTML(this.waitWindow.AWMessageContainerPB.el, "Please wait...");
   this.waitWindow.hide();
};

WebStudio.Wizard.prototype.showWaitWindow = function () 
{
	if (!this.waitWindow)
	{
		return;
	}
	this.waitWindow.show();
	this.waitWindow.centered();
	this.waitWindow.zIndexUpper = 2000;
	this.waitWindow.setActive();
	this.waitWindow.block();
};

WebStudio.WizardPage.prototype._init = function(data) 
{
	var _this = this;
	this.config = this.parsePageData(data);
	this.wizard.config = this.parseWizardConfig(data);

	this.window = new WebStudio.Window(this.config.id);
	this.window.disableResize(false);

	this.window.onClose = function(){
		_this.onButtonClick('cancel');
	};
	
	var ud = null;

	if(this.config.dialogtype == "form")
	{
		var fd =  this.parseFormData(data);

		if(this.config.message)
		{
			this.window.setConfig({
				ButtonContainer:{
					selector: 'table[id=AWButtonContainer]'
				},
				MessageContainer:{
					selector: 'td[id=AWMessageContainer]'
				}
			});

			this.window.setTemplateByDOMObject(WebStudio.App.AlfrescoMessageBoxTmplate.el);
			this.window.activate();
			
			WebStudio.util.pushHTML(this.window.MessageContainer.el, this.config.message);			
			
			this.window.setTitle(this.config.title);
			this.window.zIndexUpper = 2000;
			var btnyui = new YAHOO.widget.Button("AWButtonOk", {onclick: { fn: this.onButtonClick.pass(['close'],this) } } );

		}
		else
		{

			this.form = new WebStudio.Form();
			this.form.setTemplateByDOMObject(WebStudio.App.FormTemplate.el);
			this.form.activate();
			this.form.buildFormFields(fd);

			this.window.setTemplate("#AlfrescoWindowTemplate","AlfrescoWindowTemplate");
 			this.window.activate("AlfrescoWindowTemplate");
			this.window.setTitle(this.config.title);
			this.window.setContentByObject(this.form.generalLayer).show();
			
			if(this.form.focusElement)
			{
				this.form.focusElement.focus();
			}
		}

	}

	if("checkboxgrid" == this.config.dialogtype)
	{
		var cgd = this.parseCheckboxGridData(data);

		this.form = new WebStudio.Form();
		this.form.setTemplateByDOMObject(WebStudio.App.FormTemplate.el);
		this.form.activate();
		this.form.buildFormFields(cgd);

		this.window.setTemplate("#AlfrescoWindowTemplate","AlfrescoWindowTemplate");
		this.window.activate("AlfrescoWindowTemplate");
		this.window.setTitle(this.config.title);
		this.window.setContentByObject(this.form.generalLayer);
		this.window.show();
	}

	if("url" == this.config.dialogtype)
	{
		ud = {};
		ud[this.config.id] = {};
		var d = ud[this.config.id];
		d.type = "url";
		d.content = "body";
		d.url = this.config.url;
		var buttons = data.buttons||[];
	
		this.parseFooterControls(ud, buttons);
		
		this.form = new WebStudio.Form();
		this.form.setTemplateByDOMObject(WebStudio.App.FormTemplate.el);
		this.form.activate();
		this.form.buildFormFields(ud);
		
		this.window.setTemplate("#AlfrescoWindowTemplate","AlfrescoWindowTemplate");
		this.window.activate("AlfrescoWindowTemplate");
		this.window.setTitle(this.config.title);
		this.window.setContentByObject(this.form.generalLayer);
		this.window.show();
	}

	if("html" == this.config.dialogtype)
	{
		ud = {};
		ud[this.config.id] = {};
		var da = ud[this.config.id];
		da.type = "html";
		da.content = "body";
		da.html = this.config.html;
	
		var buttonsB = data.buttons||[];

		this.parseFooterControls(ud, buttonsB);

		this.form = new WebStudio.Form();
		this.form.setTemplateByDOMObject(WebStudio.App.FormTemplate.el);
		this.form.activate();
		this.form.buildFormFields(ud);

		this.window.setTemplate("#AlfrescoWindowTemplate","AlfrescoWindowTemplate");
		this.window.activate("AlfrescoWindowTemplate");
		this.window.setTitle(this.config.title);
		this.window.setContentByObject(this.form.generalLayer);
		this.window.show();
	}

	if(this.config.cacheInvalidateAll)
	{
		WebStudio.app.refreshObjectCache();
	}

	if(this.window.generalLayer)
	{
		this.window.centered();
		this.window.setActive();
		this.window.block();
	}

	return this;
};