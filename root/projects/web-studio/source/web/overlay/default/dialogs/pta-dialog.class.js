if (typeof WebStudio == "undefined")
{
	var WebStudio = {};
}

WebStudio.PTADialog = function(index) 
{
	this.defaultContainer = document.body;
	this.injectObject = document.body;

	this.ID = index;
	
	//this.defaultTemplateSelector = 'div[id=PageTemplateAssociationsDialog]';
	this.defaultTemplateSelector = '';

	this.defaultElementsConfig = { };	

	this.events = {};	
	this.nodes = {};
	this.droppables = [];
}

WebStudio.PTADialog.prototype = new WebStudio.AbstractTemplater('WebStudio.PTADialog');

WebStudio.PTADialog.prototype.activate = function() 
{	
	var dialog = this;
	this.buildGeneralLayer();
		
	// set up the buttons	
	this.cNewButton = new YAHOO.widget.Button("pta_newbutton", {
		onclick: { 
			fn: function() {

			    var pageId = context.getCurrentPageId();
			    
				var w = new WebStudio.Wizard();
				w.setDefaultJson(
				{
					refreshSession: 'true',
					pageId: pageId
				});
				w.onComplete = (function() 
				{
					this.initDataTable();					
				}).bind(dialog);
				
				var url = WebStudio.ws.studio("/wizard/page/associations/add");
				w.start(url, 'add-template-association');
			 
			}
		}
	});
	//this.cEditButton = new YAHOO.widget.Button("pta_editbutton");
	this.cRemoveButton = new YAHOO.widget.Button("pta_removebutton", {

		onclick: { 
			fn: (function() {
			
				var rowIds = this.cDataTable.getSelectedRows();
				if(rowIds != null && rowIds.length > 0)
				{
					var rowId = rowIds[0];
					var record = this.cDataTable.getRecord(rowId);
					
					var formatId = record.getData("format-id");
					
					var pageId = context.getCurrentPageId();
					
					var w = new WebStudio.Wizard();
					w.setDefaultJson(
					{
						refreshSession: 'true',
						pageId: pageId,
						formatId: formatId
					});
					w.onComplete = (function() 
					{
						this.initDataTable();					
					}).bind(dialog);
					
					var url = WebStudio.ws.studio("/wizard/page/associations/remove");
					w.start(url, 'remove-template-association');
				}	
								 
			}).bind(dialog)
		}
		
	});
	
	this.initDataTable();
}

WebStudio.PTADialog.prototype.initDataTable = function()
{
	if(this.cDataTable)
	{
		this.cDataTable.destroy();
		this.cDataTable = null;
	}
	
	// the current page id
	var pageId = context.getCurrentPageId();
	
	// load content associations into the data table	
	var url = WebStudio.ws.studio("/api/page/associations", {pageId: pageId });
	var myAjax = new Ajax(url, 
	{
		method: 'get',
		onComplete: (function(response)
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
			
			this.cDataTable = new YAHOO.widget.DataTable("PageTemplateAssociationsPanelDataTable", 
				myColumnDefs, myDataSource,
				{
					selectionMode: "single",
					draggableColumns: true 
				}
			);
			
			// Hide the template-id column
			this.cDataTable.hideColumn("template-id");

			// Hide the template-description column
			this.cDataTable.hideColumn("template-description");
			
			// Set column widths
			this.cDataTable.setColumnWidth("format-id", 200);
			this.cDataTable.setColumnWidth("template-id", 200);
			this.cDataTable.setColumnWidth("template-title", 200);
									
	        // Subscribe to events for row selection
	        this.cDataTable.subscribe("rowMouseoverEvent", this.cDataTable.onEventHighlightRow);
	        this.cDataTable.subscribe("rowMouseoutEvent", this.cDataTable.onEventUnhighlightRow);
	        this.cDataTable.subscribe("rowClickEvent", this.cDataTable.onEventSelectRow);
			
	        // Programmatically select the first row
	        //this.cDataTable.selectRow(standardSelectDataTable.getTrEl(0));

	        // Programmatically bring focus to the instance so arrow selection works immediately
	        //this.cDataTable.focus();
				
	        if(this.modal)
	        {
	        	this.modal.center();
	        }
				
		}).bind(this)

	}).request();
}

WebStudio.PTADialog.prototype.popup = function() 
{	
	if(!this.modal)
	{
		this.modal = new YAHOO.widget.Panel("PageTemplateAssociationsPanel", {
			fixedcenter: true,
			close: true, 
			draggable: false, 
			modal: true,
			visible: false,
			effect:{effect:YAHOO.widget.ContainerEffect.FADE, duration:0.5} 			
		});
		
		this.modal.render(document.body);
	}
	else
	{
		this.initDataTable();
	}
	
	this.modal.show();
}

WebStudio.PTADialog.prototype.popout = function()
{	
	this.modal.destroy();
	this.modal.hide();
}
