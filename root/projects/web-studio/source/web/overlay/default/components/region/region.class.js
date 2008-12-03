if (typeof WebStudio == "undefined" || !WebStudio)
{
	WebStudio = {};
}

WebStudio.Region = function(index) 
{
	this.defaultContainer = document.body;
	this.injectObject = document.body;

	this.ID = index;

	this.defaultTemplateSelector = 'div[id=AlfrescoRegionTemplate]';

	var _this = this;

	this.defaultElementsConfig = {
		RBArnor: {
			selector: 'div[id=ar-bottom-right-corner]',
			styles: {
				'font-size': 1
			}
		},
		LBArnor: {
			selector: 'div[id=ar-bottom-left-corner]',
			styles: {
				'font-size': 1
			}
		},
		RTArnor: {
			selector: 'div[id=ar-top-right-corner]',
			styles: {
				'font-size': 1
			}
		},
		LTArnor: {
			selector: 'div[id=ar-top-left-corner]',
			styles: {
				'font-size': 1
			}
		},
		RightBorder: {
			selector: 'div[id=ar-right-border]',
			styles: {
				'font-size': 1
			}
		},
		LeftBorder: {
			selector: 'div[id=ar-left-border]',
			styles: {
				'font-size': 1
			}
		},
		BottomBorder: {
			selector: 'div[id=ar-bottom-border]',
			styles: {
				'font-size': 1
			}
		},
		HeaderBg: {
			selector: 'div[id=ar-header-bg]'
		},
		Body: {
			selector: 'div[id=ar-body]'
		},
		DeleteButton : {
			selector : 'table[id=button-delete]',
			events : {
				click : function() {
					if (this.disabled)
					{
						return;
					}
					_this.onDeleteClick();
				}
			}
		},
		RegionOverlayTitle : {
			selector : 'span[id=region-overlay-title]'
		}
	};

	this.formLoaded = false;
};

WebStudio.Region.prototype = new WebStudio.AbstractTemplater('WebStudio.Region');

WebStudio.Region.prototype.activate = function(templateID) 
{
	this.buildGeneralLayer(templateID);

	[this.DeleteButton.el].each(function(item) {
		$(item).addEvents({
			mouseover : function () {
				if (this.disabled)
				{
					return;
				}
				$(this.rows[0].cells[0]).addClass("btn_left_selected");
				$(this.rows[0].cells[1]).addClass("btn_center_selected");
				$(this.rows[0].cells[2]).addClass("btn_right_selected");
			},
			mouseout: function () {
				if (this.disabled)
				{
					return;
				}
				$(this).fireEvent("removeselection");
			},
			removeselection : function() {
				$(this.rows[0].cells[0]).removeClass("btn_left_selected");
				$(this.rows[0].cells[1]).removeClass("btn_center_selected");
				$(this.rows[0].cells[2]).removeClass("btn_right_selected");
			},
			disable: function(disabled) {
				var opacity = disabled ? 0.5 : 1;
				$(this).setStyle('cursor', disabled ? "default" : "pointer");
				$(this).fireEvent("removeselection");
				$(this).setStyle('opacity', opacity);
				this.disabled = disabled;
			}
		});
	});
	
	// set up our title
	var title = "<B>" + this.componentTitle + "</B>";
	
	this.RegionOverlayTitle.el.setHTML(title);

	return this;
};

WebStudio.Region.prototype.loadEditor = function(url) 
{
	this.formLoaded = false;
	
	var ajax = new Ajax(url, {
		method : 'get', 
		onComplete : this.onLoadComplete.bind(this), 
		onFailure: this.onFailure.bind(this),
		evalScripts: true
	}).request();
};

WebStudio.Region.prototype.onFailure = function(data)
{
	WebStudio.app.onFailure(data);
};

WebStudio.Region.prototype.isFormLoaded = function()
{
	return this.formLoaded;
};

WebStudio.Region.prototype.onLoadComplete = function (response) 
{
	var _this = this;
	
	this.formElementsHtml = response;
	
	var editorHeadUrl = this.componentEditorUrl;
	var i = editorHeadUrl.indexOf("edit");
	if(i > -1)
	{
		i = i + 4;
		editorHeadUrl = editorHeadUrl.substring(0,i) + "/header" + editorHeadUrl.substring(i);
		editorHeadUrl = WebStudio.url.studio(editorHeadUrl);
	
		var ajax = new Ajax(editorHeadUrl, 
		{
			method : 'get', 
			onComplete : function(response)
			{
				// take off any leading comments
				var zz = response.indexOf("-->");
				if(zz > -1)
				{
					response = response.substring(zz+3);
				}
				
				// build the dummy
				var dummy = document.createElement('div');
				dummy.innerHTML = response;
				
				// load dependencies off the dummy
				var data = { };
				for(var z = 0; z < dummy.childNodes.length; z++)
				{
					var el = dummy.childNodes[z];
					
					var tag = el.nodeName;
					
					var dataId = "Data" + z;
					
					var dataElement = null;
					
					if(tag == "SCRIPT")
					{
						var src = el.getProperty("src");
						if(src)
						{
							if(src.indexOf("/studio") === 0)
							{
								src = src.substring(7);
							}
							
							dataElement = {
								name: dataId,
								path: src
							};
							data[dataId] = dataElement;
						}
					}
					
					if(tag == "LINK")
					{
						var href = el.getProperty("href");
						if(href)
						{
							if(href.indexOf("/studio") === 0)
							{
								href = href.substring(7);
							}
	
							dataElement = {
								name: dataId,
								path: href
							};
							data[dataId] = dataElement;	
						}
					}
				}
				
				_this.loader = new Alf.sourceLoader('Form Assets', data);
				_this.loader.jsPath = WebStudio.url.studio('', null, true);
				_this.loader.cssPath = WebStudio.url.studio('', null, true);	
				_this.loader.onLoad = _this.setupForm.bind(_this);
				_this.loader.load();	
				
			}
		}).request();
	}	
};

WebStudio.Region.prototype.setupForm = function() 
{
	var _this = this;
	
	var formId = "form" + this.regionId + this.regionSourceId;

	// componentEditorUrl = /c/edit/global.7451619de7region1
	var url = WebStudio.url.studio(this.componentEditorUrl);
	
	var html = "<form id=\"" + formId + "\" method=\"post\" action=\"" + url + "\">";	
	html += this.formElementsHtml;	
	html += "<br/>";
	html += "<div width='100%' align='center'>";
	html += "<input id=\"formCancelButton\" type=\"button\" value=\"Cancel\" />";
	html += "&nbsp;&nbsp;";
	html += "<input type=\"submit\" value=\"Save\" />";
	html += "</div>";		
	html += "</form>";
	
	//this.Body.el.setHTML(html);
	Alf.setHTML(this.Body.el, html);
	
	// bind in events (on-click)
	$(formId).addEvent('submit', function(e) {
	
		// stop the event
		e = new Event(e).stop();
	
		// post the form in the background	
		$(formId).send({
			onSuccess: _this.saveFormSuccess,
			onFailure: _this.saveFormError,
			evalScripts: true,
			headers: {
				'Content-Type' : 'multipart/form-data',
				'encoding': 'utf-8'			
			}
		});
	});
	
	$('formCancelButton').addEvent('click', function(e) {

		// stop the event
		e = new Event(e).stop();

		// close the window
		_this.shutdown();
	});
	
	this.formLoaded = true;	
};

WebStudio.Region.prototype.build = function() 
{
	var _this = this;

	/*	
	this.generalLayer.set({
		events: {
			mouseleave: function() 
			{
				if(!_this.checkInterval)
				{
					var waitTime = 1000*1.5;
					var checkPeriod = 200;
					
					_this.checkTotal = waitTime / checkPeriod;
					_this.checkCount = 0;				
					_this.checkInterval = _this.checkCloseWindow.periodical(checkPeriod, _this);		
				}
				
			}
			,
			mouseenter: function() 
			{			
				if(_this.checkInterval)
				{
					$clear(_this.checkInterval);
					_this.checkInterval = null;
				}
				
			}
		}
	});
	*/
};

/*
WebStudio.Region.prototype.checkCloseWindow = function()
{
	this.checkCount++;
	if(this.checkCount > this.checkTotal)
	{
		$clear(this.checkInterval);
		this.checkInterval = null;
		
		// close the window
		this.shutdown();		
	}
};
*/

WebStudio.Region.prototype.onClose = function()
{
};

WebStudio.Region.prototype.setWidth = function(w) 
{
	this.Body.el.setStyle('width', w - 8);
	this.BottomBorder.el.setStyle('width', w - 8);
	this.HeaderBg.el.setStyle('width', w - 12);
	this.RBArnor.el.setStyle('left', w - 4);
	this.RightBorder.el.setStyle('left', w - 4);
	this.RTArnor.el.setStyle('left', w - 6);
	this.generalLayer.setStyle('width', w);
};

WebStudio.Region.prototype.setHeight = function(h) 
{
	this.Body.el.setStyle('height', h - 32);
	this.BottomBorder.el.setStyle('top', h - 4);
	this.LBArnor.el.setStyle('top', h - 4);
	this.LeftBorder.el.setStyle('height', h - 32);
	this.RBArnor.el.setStyle('top', h - 4);
	this.RightBorder.el.setStyle('height', h - 32);
	this.generalLayer.setStyle('height', h);
};

WebStudio.Region.prototype.setCoords = function(x, y) 
{
	if (this.generalLayer)
	{
		this.generalLayer.setStyles({left : x, top : y});
	}
};

WebStudio.Region.prototype.disableButton = function (but, disabled) 
{
	but = but.capitalize();
	if (!(but in {"Delete" : 1}))
	{
		return;
	}
	
	this[but + 'Button'].el.fireEvent("disable", disabled);
};

WebStudio.Region.prototype.hideButton = function (but)
{
	but = but.capitalize();
	if (!(but in {"Delete" : 1}))
	{
		return;
	}
	
	this[but + 'Button'].el.setStyle("display", "none");
};

WebStudio.Region.prototype.onEditClick = function()
{
};

WebStudio.Region.prototype.saveFormError = function(r)
{
	alert("An error occurred while saving this form: " + r.statusText);
};

WebStudio.Region.prototype.saveFormSuccess = function(r)
{
	this.shutdown();
};

WebStudio.Region.prototype.shutdown = function()
{
	this.popout();
	
	// unload any assets
	if(this.loader)
	{
		this.loader.unloadAll();		
	}
	
	this.destroy();
	this.onClose();
};

WebStudio.Region.prototype.setActive = function() 
{
	//Set this window active, bring to front and apply Active styles
	this.generalLayer.setStyle('z-index', WebStudio.WindowsZIndex + this.zIndexUpper);
	WebStudio.WindowsZIndex++;
	WebStudio.WindowsActive = this;
	return this;
};

WebStudio.Region.prototype.popup = function() 
{	
	this.block();
	this.show();
	this.centered();
	this.zIndexUpper = 2000;
	this.setActive();
};

WebStudio.Region.prototype.popout = function()
{	
    this.hide();
    this.unblock();
};