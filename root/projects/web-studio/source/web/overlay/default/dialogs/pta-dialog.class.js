if (typeof WebStudio == "undefined" || !WebStudio)
{
	WebStudio = {};
}

WebStudio.PTADialog = function(index) 
{
	this.defaultContainer = document.body;
	this.injectObject = document.body;

	this.ID = index;
	
	this.defaultTemplateSelector = '';

	this.defaultElementsConfig = { };	

	this.events = {};	
	this.nodes = {};
	this.droppables = [];
};

WebStudio.PTADialog.prototype = new WebStudio.AbstractTemplater('WebStudio.PTADialog');

WebStudio.PTADialog.prototype.activate = function() 
{	
	var dialog = this;
	this.buildGeneralLayer();
		
	// set up the buttons	
	this.cNewButton = new YAHOO.widget.Button("pta_newbutton", {
		onclick: { 
			fn: function() {

			    var pageId = Surf.context.getCurrentPageId();
			    
				var w = new WebStudio.Wizard();
				w.setDefaultJson(
				{
					refreshSession: 'true',
					pageId: pageId
				});
				w.onComplete = function() 
				{
					dialog.initDataTable();					
				};
				
				var url = WebStudio.ws.studio("/wizard/page/associations/add");
				w.start(url, 'add-template-association');
			 
			}
		}
	});
	
	//this.cEditButton = new YAHOO.widget.Button("pta_editbutton");
	this.cRemoveButton = new YAHOO.widget.Button("pta_removebutton", {

		onclick: { 
			fn: function() {
			
				var rowIds = dialog.cDataTable.getSelectedRows();
				if(rowIds && rowIds.length > 0)
				{
					var rowId = rowIds[0];
					var record = dialog.cDataTable.getRecord(rowId);
					
					var formatId = record.getData("format-id");
					
					var pageId = Surf.context.getCurrentPageId();
					
					var w = new WebStudio.Wizard();
					w.setDefaultJson(
					{
						refreshSession: 'true',
						pageId: pageId,
						formatId: formatId
					});
					w.onComplete = function() 
					{
						dialog.initDataTable();					
					};
					
					var url = WebStudio.ws.studio("/wizard/page/associations/remove");
					w.start(url, 'remove-template-association');
				}	
			}
		}		
	});
	
	this.initDataTable();
};

WebStudio.PTADialog.prototype.initDataTable = function()
{
	var _this = this;
	
	if(this.cDataTable)
	{
		this.cDataTable.destroy();
		this.cDataTable = null;
	}
	
	// the current page id
	var pageId = Surf.context.getCurrentPageId();
	
	// load content associations into the data table	
	var url = WebStudio.ws.studio("/api/page/associations", {pageId: pageId });
	var myAjax = new Ajax(url, 
	{
		method: 'get',
		onComplete: function(response)
		{
			var data = Json.evaluate(response);
			
			var myDataSource = new YAHOO.util.DataSource(data["associations"]);
			myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
			myDataSource.responseSchema = {
				fields: [ "format-id", "template-id", "template-title", "template-description" ]
			};
			
			// Table Column Definitions
			var myColumnDefs = [
		           { key:"format-id", label: "Format", sortable:true, resizeable:true },
		           { key:"template-id", label: "Template", sortable:true, resizeable:true },
		           { key:"template-title", label: "Title", sortable:true, resizeable:true },
		           { key:"template-description", label: "Description", sortable:true, resizeable:true }
			];
			
			_this.cDataTable = new YAHOO.widget.DataTable("PageTemplateAssociationsPanelDataTable", 
				myColumnDefs, 
				myDataSource,
				{
					selectionMode: "single",
					draggableColumns: true 
				}
			);
			
			// Hide the template-id column
			_this.cDataTable.hideColumn("template-id");

			// Hide the template-description column
			_this.cDataTable.hideColumn("template-description");
			
			// Set column widths
			_this.cDataTable.setColumnWidth("format-id", 200);
			_this.cDataTable.setColumnWidth("template-id", 200);
			_this.cDataTable.setColumnWidth("template-title", 200);
									
	        // Subscribe to events for row selection
	        _this.cDataTable.subscribe("rowMouseoverEvent", _this.cDataTable.onEventHighlightRow);
	        _this.cDataTable.subscribe("rowMouseoutEvent", _this.cDataTable.onEventUnhighlightRow);
	        _this.cDataTable.subscribe("rowClickEvent", _this.cDataTable.onEventSelectRow);
			
	        // Programmatically select the first row
	        //_this.cDataTable.selectRow(standardSelectDataTable.getTrEl(0));

	        // Programmatically bring focus to the instance so arrow selection works immediately
	        //_this.cDataTable.focus();
				
	        if(_this.modal)
	        {
	        	_this.modal.center();
	        }
				
		}

	}).request();
};

WebStudio.PTADialog.prototype.popup = function() 
{	
	if(!this.modal)
	{
		var options = {
			fixedcenter: true,
			close: true, 
			draggable: false, 
			modal: true,
			visible: false,
			effect:{effect:YAHOO.widget.ContainerEffect.FADE, duration:0.5}		
		};

		this.modal = new YAHOO.widget.Panel("PageTemplateAssociationsPanel", options);
		
		this.modal.render(document.body);
	}
	else
	{
		this.initDataTable();
	}
	
	this.modal.show();
};

WebStudio.PTADialog.prototype.popout = function()
{	
	this.modal.destroy();
	this.modal.hide();
};
