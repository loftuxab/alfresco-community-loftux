if (typeof WebStudio == "undefined" || !WebStudio)
{
	WebStudio = {};
}

WebStudio.TemplatesView = function() 
{
	this.defaultContainer = document.body;
	this.injectObject = document.body;

	//this.ID = index;
	
	this.defaultTemplateSelector = 'div[id=AlfrescoTemplatesViewTemplate]';
	
	this.defaultElementsConfig = {
		MenuHolder: {
			selector: 'div[id=ALVMenu]'
		}
		,
		Instances: {
			selector: 'div[id=ALVInstances]'
		}
	};

	this.events = {};
	
	this.INDEX_BUTTON_ADD = 0;
	this.INDEX_BUTTON_EDIT = 1;
	this.INDEX_BUTTON_COPY = 2;
	this.INDEX_BUTTON_DELETE = 3;	
};

WebStudio.TemplatesView.prototype = new WebStudio.AbstractTemplater('WebStudio.TemplatesView');

WebStudio.TemplatesView.prototype.activate = function() 
{	
	var _this = this;
	
	this.buildGeneralLayer();

	if (this.MenuHolder.el) 
	{
		var menuTemplate = $('ALVMenuTemplate');
		
		this.menu = new WebStudio.Menu(this.ID+'Menu');
		this.menu.setConfig({roots: {blockSelection: true}});
		this.menu.setInjectObject(this.MenuHolder.el);
		this.menu.setTemplate(menuTemplate);
		this.menu.activate();
		this.menu.show();

		this.menu.addEvent('click', 'TemplatesViewAddEditDelete', function(group, index) 
		{
			if (group == 'roots') 
			{
				if (index == _this.INDEX_BUTTON_ADD) {
					_this.fireEvent('AddTemplate');
				} else if (index == _this.INDEX_BUTTON_EDIT) {
					_this.fireEvent('EditTemplate');
				} else if (index == _this.INDEX_BUTTON_COPY) {
					_this.fireEvent('CopyTemplate');
				} else if (index == _this.INDEX_BUTTON_DELETE) {
					_this.fireEvent('DeleteTemplate');
				}
			}
		}, this);
	}
	
	// Set up initial state of buttons
	this.menu.setEnabled('roots', this.INDEX_BUTTON_ADD);
	this.menu.setDisabled('roots', this.INDEX_BUTTON_EDIT);
	this.menu.setDisabled('roots', this.INDEX_BUTTON_COPY);
	this.menu.setDisabled('roots', this.INDEX_BUTTON_DELETE);
	
	// Reload Templates listing
	this.reloadTemplatesListing();
		
	// Set up Events
	this.addEvent('AddTemplate', 'add_template', function() 
	{
		var w = new WebStudio.Wizard();
		w.setDefaultJson(
		{
			refreshSession: 'true'
		});
		var url = WebStudio.ws.studio('/wizard/template/add');
		w.start(url, 'addnewtemplate');
		w.onComplete = function() 
		{
			// reload templates when complete
			_this.reloadTemplatesListing();
		};
		
	}, this);

	this.addEvent('EditTemplate', 'edit_template', function() 
	{
		var w = new WebStudio.Wizard();
		w.setDefaultJson(
		{
			templateId: _this.selectedTemplateId,
			refreshSession: 'true'
		});
		var url = WebStudio.ws.studio('/wizard/template/edit');
		w.start(url, 'edittemplate');
		w.onComplete = function() 
		{
			// reload templates when complete
			_this.reloadTemplatesListing();
		};

	}, this);

	this.addEvent('CopyTemplate', 'copy_template', function() 
	{
		var w = new WebStudio.Wizard();
		w.setDefaultJson(
		{
			templateId: _this.selectedTemplateId,
			refreshSession: 'true'
		});
		var url = WebStudio.ws.studio('/wizard/template/copy');
		w.start(url, 'edittemplate');
		w.onComplete = function() 
		{
			// reload templates when complete
			_this.reloadTemplatesListing();
		};

	}, this);

	this.addEvent('DeleteTemplate', 'delete_template', function() 
	{
		var w = new WebStudio.Wizard();
		w.setDefaultJson(
		{
			templateId: _this.selectedTemplateId,
			refreshSession: 'true'
		});
		var url = WebStudio.ws.studio('/wizard/template/remove');
		w.start(url, 'removetemplate');
		w.onComplete = function() 
		{
			// reload templates when complete
			_this.reloadTemplatesListing();
			
			if (this.wizardFinished)
			{
				_this.application.GoToTemplateDisplay(null);
				_this.selectTemplate(null);
			}
		};

	}, this);
	
	return this;
};

WebStudio.TemplatesView.prototype.reloadTemplatesListing = function() 
{
	var _this = this;

	// Do an Ajax call to fetch all of the template instances
	var url = WebStudio.ws.studio("/api/model/list", { type: "template-instance" } );
	
	this.call = YAHOO.util.Connect.asyncRequest('GET', url, 
	{	
		success: function(r) 
		{ 		
			var d = eval('(' + r.responseText + ')');

			var key = null;
			var templateType = null;
			var domId = null;
			
			var html = "<table width='100%' cellpadding='2' cellspacing='2'>";
			for(key in d.results)
			{
				if(d.results.hasOwnProperty(key))
				{
					templateType = d.results[key]["template-type"];
					
					// select image
					var imageUrl = "/images/common/filetypes/default-32.png";
					var colorStyle = " style='color: #ccc' ";
					if("dynamic" == templateType)
					{
						imageUrl = "/images/common/icons/view_properties_large.gif";
						colorStyle = " ";
					}
					
					// write out the row
					domId = _this.getTemplateDomId(key);
					 
					html += "<tr class='TemplateRow' id='" + domId + "'>";
					html += "<td " + colorStyle + " class='TemplateRowCell' id='TemplateCell_" + domId + "'>";
					html += "<p>";
					html += "<img src='" + WebStudio.url.studio(imageUrl) + "' style='float:left'>";
					
					var title = d.results[key].title;
					if(!title)
					{
						title = d.results[key]["template-type"];
					}
					if(!title)
					{
						title = key;
					}
					html += title;

					html += "</p>";
					html += "</td>";
					html += "</tr>";
				}
			}
			html += "</table>";	
			_this.Instances.el.setHTML(html);
					
			var templateRowOnClickHandler = function(a,b)
			{
				_this.onTemplateRowClick(this.templateId);
			};
			
			var templateRowMouseOverHandler = function()
			{
				jQuery(this).addClass("TemplateRowOver");
			};
			
			var templateRowMouseOutHandler = function()
			{
				jQuery(this).removeClass("TemplateRowOver");
			};
				
			// add in click and hover events
			for(key in d.results)
			{
				if(d.results.hasOwnProperty(key))
				{
					templateType = d.results[key]["template-type"];
					if("dynamic" == templateType)
					{
						domId = _this.getTemplateDomId(key);
		
						var cell1 = $('TemplateCell_' + domId);
						cell1.templateId = key;
						jQuery(cell1).click(templateRowOnClickHandler);
						
						// mouse over and out
						jQuery(cell1).mouseover(templateRowMouseOverHandler);
						jQuery(cell1).mouseout(templateRowMouseOutHandler);
					}
				}
			}
			
		}
		,
		failure: function(r)
		{		
			alert("reloadTemplatesListing failed: " + r.responseText);
		}
	});	
};

WebStudio.TemplatesView.prototype.build = function() 
{
	this.generalLayer.set({
		id: this.ID
	});
};

WebStudio.TemplatesView.prototype.selectTemplate = function(templateId) 
{
	var domId = null;
	var domElement = null;
	
	if (this.selectedTemplateId)
	{
		domId = this.getTemplateDomId(this.selectedTemplateId);
		domElement = $(domId);
		if(domElement)
		{
			domElement.removeClass('TemplateRowSelected');
		}
	}
	
	// select
	this.selectedTemplateId = templateId;
	
	if(templateId)
	{	
		domId = this.getTemplateDomId(templateId);
		domElement = $(domId);
		if(domElement)
		{
			domElement.addClass('TemplateRowSelected');
		}
		
		this.menu.setEnabled('roots', this.INDEX_BUTTON_ADD);
		this.menu.setEnabled('roots', this.INDEX_BUTTON_EDIT);
		this.menu.setEnabled('roots', this.INDEX_BUTTON_COPY);
		this.menu.setEnabled('roots', this.INDEX_BUTTON_DELETE);
	}	
};

WebStudio.TemplatesView.prototype.getTemplateDomId = function(templateId)
{
	return "templatesView_template_" + templateId;
};

WebStudio.TemplatesView.prototype.onTemplateRowClick = function(templateId)
{
	// This is to be overridden elsewhere (templates applet most likely)
};
