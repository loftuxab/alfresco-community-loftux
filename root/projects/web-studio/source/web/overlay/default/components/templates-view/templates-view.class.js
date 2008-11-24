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
		,
		Network: {
			selector: 'div[id=ALVNetwork]'
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

	
	// Network
	var domId = this.getTemplateDomId("network_templates");
	
	var network = "";
	/*
	network += "<table width='100%' border='0'>";
	network += "<tr class='TemplateRow' id='" + domId + "'>";
	network += "<td onClick=\"javascript:WebStudio.app.onMenuItemClick('find_more_templates');\">";
	network += "<img src='" + WebStudio.url.studio('/overlay/default/images/find_more_templates.gif') + "'>";
	network += "</td>";	
	network += "<td width='100%' onClick=\"javascript:WebStudio.app.onMenuItemClick('find_more_templates');\">";
	network += "Find more Templates!";
	network += "</td>";
	network += "</tr>";
	network += "</table";
	*/
	this.Network.el.setHTML(network);
	
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
			
			var html = "<table width='100%' border='0'>";
			for(key in d.results)
			{
				if(d.results.hasOwnProperty(key))
				{
					templateType = d.results[key]["template-type"];
					
					// select image
					var imageUrl = "/overlay/default/images/template.gif";
					var colorStyle = " style='color: gray' ";
					if("dynamic" == templateType)
					{
						imageUrl = "/overlay/default/images/dynamic-template.gif";
						colorStyle = " ";
					}
					
					// write out the row
					domId = _this.getTemplateDomId(key);
					 
					html += "<tr class='TemplateRow' id='" + domId + "'>";
					html += "<td id='TemplateCell1_" + domId + "'>";
					html += "<img src='" + WebStudio.url.studio(imageUrl) + "'>";
					html += "</td>";
					html += "<td width='100%' id='TemplateCell2_" + domId + "' " + colorStyle + " >";				 
					
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
					html += "</td>";
					html += "</tr>";
				}
			}
			html += "</table>";	
			_this.Instances.el.setHTML(html);
					
			var templateRowOnClickHandler = function(group, index)
			{
				_this.onTemplateRowClick(_this.templateId);
			};
				
			// add in click events
			for(key in d.results)
			{
				if(d.results.hasOwnProperty(key))
				{
					templateType = d.results[key]["template-type"];
					if("dynamic" == templateType)
					{
						domId = _this.getTemplateDomId(key);
		
						var cell1 = $('TemplateCell1_' + domId);
						cell1.templateId = key;
						cell1.onclick = templateRowOnClickHandler;
						
						var cell2 = $('TemplateCell2_' + domId);
						cell2.templateId = key;
						cell2.onclick =  templateRowOnClickHandler;
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
	
	if(this.selectedTemplateId)
	{
		domId = this.getTemplateDomId(this.selectedTemplateId);
		domElement = $(domId);
		if(domElement)
		{
			domElement.removeClass('SelectedTemplateRow');
			domElement.addClass('TemplateRow');
		}
	}
	
	// select
	this.selectedTemplateId = templateId;
	
	domId = this.getTemplateDomId(templateId);
	domElement = $(domId);
	if(domElement)
	{
		domElement.removeClass('TemplateRow');
		domElement.addClass('SelectedTemplateRow');
	}
	
	this.menu.setEnabled('roots', this.INDEX_BUTTON_ADD);
	this.menu.setEnabled('roots', this.INDEX_BUTTON_EDIT);
	this.menu.setEnabled('roots', this.INDEX_BUTTON_COPY);
	this.menu.setEnabled('roots', this.INDEX_BUTTON_DELETE);
	
};

WebStudio.TemplatesView.prototype.getTemplateDomId = function(templateId)
{
	return "templatesView_template_" + templateId;
};

WebStudio.TemplatesView.prototype.onTemplateRowClick = function(templateId)
{
	// This is to be overridden elsewhere (templates applet most likely)
};
