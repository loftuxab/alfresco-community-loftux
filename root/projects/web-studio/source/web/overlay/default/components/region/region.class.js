
if (typeof WebStudio == "undefined")
{
	var WebStudio = {};
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
					if (this.disabled) return;
					_this.onDeleteClick();
				}
			}
		},
		RegionOverlayTitle : {
			selector : 'span[id=region-overlay-title]'
		}
	};

}

WebStudio.Region.prototype = new WebStudio.AbstractTemplater('WebStudio.Region');

WebStudio.Region.prototype.activate = function(templateID) {
	this.buildGeneralLayer(templateID);

	[this.DeleteButton.el].each(function(item) {
		$(item).addEvents({
			mouseover : function () {
				if (this.disabled) return;
				$(this.rows[0].cells[0]).addClass("btn_left_selected");
				$(this.rows[0].cells[1]).addClass("btn_center_selected");
				$(this.rows[0].cells[2]).addClass("btn_right_selected");
			},
			mouseout: function () {
				if (this.disabled) return;
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
	var title = 'Region ' + '\"' + this.regionId + '\" in the \"' + this.regionScopeId + '\" scope';
	
	this.RegionOverlayTitle.el.setHTML(title);

	return this;
}

WebStudio.Region.prototype.load = function(url) {
	new Ajax(url, {
		method : 'get',
		onComplete: (function(response) 
		{
			this.Body.el.setHTML(response);
			
		}).bind(this)
	}).request();
}

WebStudio.Region.prototype.loadEditor = function(url) {
	new Ajax(url, {
		method : 'get', 
		onComplete : this.onLoadComplete.bind(this), 
		onFailure: this.onFailure.bind(this),
		evalScripts: true
	}).request();
}

WebStudio.Region.prototype.onFailure = function(data)
{
	WebStudio.app.onFailure(data);
}

WebStudio.Region.prototype.onLoadComplete = function (response) 
{
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
			onComplete : (function(response) {

				// TODO: this works, got the header
				// now parse it and set up data object
				
				// created parsed html dom element
				var dummy = WebStudio.parser.parseHTML(response);

				// set up a source loader to go after any dependencies
				var data = { };
				
				// walk the dummy
				for(var z = 0; z < dummy.childNodes.length; z++)
				{
					var el = dummy.childNodes[z];
					
					var tag = el.nodeName;
					
					var dataId = "Data" + z;
					
					if(tag == "SCRIPT")
					{
						var src = el.getProperty("src");
						if(src.indexOf("/studio") == 0)
						{
							src = src.substring(7);
						}
						
						var dataElement = {
							name: dataId,
							path: src
						};
						data[dataId] = dataElement;				
					}
					
					if(tag == "LINK")
					{
						var href = el.getProperty("href");
						if(href.indexOf("/studio") == 0)
						{
							href = href.substring(7);
						}

						var dataElement = {
							name: dataId,
							path: href
						};
						data[dataId] = dataElement;										
					}
				}
				
				this.loader = new Alf.sourceLoader('Form Assets', data);
				this.loader.jsPath = WebStudio.url.studio('', null, true);
				this.loader.cssPath = WebStudio.url.studio('', null, true);	
				this.loader.onLoad = this.setupForm.bind(this);
				this.loader.load();	
			}).bind(this)
		}).request();
	}	
}

WebStudio.Region.prototype.setupForm = function() 
{
	var formId = "form" + this.regionId + this.regionSourceId;

	// componentEditorUrl = /c/edit/global.7451619de7region1
	var url = WebStudio.url.studio(this.componentEditorUrl);
	
	var html = "<form id=\"" + formId + "\" method=\"post\" action=\"" + url + "\">";	
	html += this.formElementsHtml;	
	html += "<br/>";
	html += "<input type=\"submit\" value=\"Save\" />";	
	html += "</form>";
	
	this.Body.el.setHTML(html);
	
	// bind in events (on-click)
	$(formId).addEvent('submit', (function(e) {
	
		// stop the event
		new Event(e).stop();
	
		// post the form in the background	
		$(formId).send({
			onSuccess: this.saveFormSuccess,
			onFailure: this.saveFormError,
			evalScripts: true,
			headers: {
				'Content-Type' : 'multipart/form-data',
				'encoding': 'utf-8'			
			}
		});

	}).bind(this));	
}

WebStudio.Region.prototype.build = function() 
{
	var _this = this;
	this.generalLayer.set({
		events: {
			mouseleave: (function() 
			{
				if(!this.checkInterval)
				{
					var waitTime = 1000*1.5;
					var checkPeriod = 200;
					
					this.checkTotal = waitTime / checkPeriod;
					this.checkCount = 0;				
					this.checkInterval = this.checkCloseWindow.periodical(checkPeriod, this);		
				}
				
			}).bind(this)
			,
			mouseenter: (function() 
			{			
				if(this.checkInterval)
				{
					$clear(this.checkInterval);
					this.checkInterval = null;
				}
				
			}).bind(this)
		}
	});
}

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
}

WebStudio.Region.prototype.onClose = function()
{

}

WebStudio.Region.prototype.setWidth = function(w) 
{
	this.Body.el.setStyle('width', w - 8);
	this.BottomBorder.el.setStyle('width', w - 8);
	this.HeaderBg.el.setStyle('width', w - 12);
	this.RBArnor.el.setStyle('left', w - 4);
	this.RightBorder.el.setStyle('left', w - 4);
	this.RTArnor.el.setStyle('left', w - 6);
	this.generalLayer.setStyle('width', w);
}

WebStudio.Region.prototype.setHeight = function(h) 
{
	this.Body.el.setStyle('height', h - 32);
	this.BottomBorder.el.setStyle('top', h - 4);
	this.LBArnor.el.setStyle('top', h - 4);
	this.LeftBorder.el.setStyle('height', h - 32);
	this.RBArnor.el.setStyle('top', h - 4);
	this.RightBorder.el.setStyle('height', h - 32);
	this.generalLayer.setStyle('height', h);
}

WebStudio.Region.prototype.setCoords = function(x, y) 
{
	if (this.generalLayer)
	{
		this.generalLayer.setStyles({left : x, top : y});
	}
}

WebStudio.Region.prototype.disableButton = function (but, disabled) 
{
	but = but.capitalize();
	if (!(but in {"Delete" : 1}))
	{
		return;
	}
	
	this[but + 'Button'].el.fireEvent("disable", disabled);
}

WebStudio.Region.prototype.hideButton = function (but)
{
	but = but.capitalize();
	if (!(but in {"Delete" : 1}))
	{
		return;
	}
	
	this[but + 'Button'].el.setStyle("display", "none");
}

WebStudio.Region.prototype.onEditClick = function() {
}

WebStudio.Region.prototype.saveFormError = function(r)
{
	alert("An error occurred while saving this form: " + r.statusText);
}

WebStudio.Region.prototype.saveFormSuccess = function(r)
{
	this.shutdown();
}

WebStudio.Region.prototype.shutdown = function()
{
	// unload any assets
	if(this.loader)
	{
		this.loader.unloadAll();		
	}
	
	this.destroy();
	this.onClose();
}
