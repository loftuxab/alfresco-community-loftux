if (typeof WebStudio == "undefined")
{
	var WebStudio = {};
}

WebStudio.CTADialog = function(index) 
{
	this.defaultContainer = document.body;
	this.injectObject = document.body;

	this.ID = index;
	
	this.defaultTemplateSelector = 'div[id=ContentTypeAssociationsDialog]';

	this.defaultElementsConfig = { };	

	this.events = {};	
	this.nodes = {};
	this.droppables = [];
}

WebStudio.CTADialog.prototype = new WebStudio.AbstractTemplater('WebStudio.CTADialog');

WebStudio.CTADialog.prototype.activate = function() 
{	
	var dialog = this;
	this.buildGeneralLayer();
		
	// set up the buttons	
	this.cNewButton = new YAHOO.widget.Button("newassociationbutton", {
		onclick: { 
			fn: function() {

				var w = new WebStudio.Wizard();
				w.setDefaultJson(
				{
					refreshSession: 'true'
				});
				var url = WebStudio.ws.studio("/wizard/content/associations/add");
				w.start(url, 'content-associations');

				w.onComplete = (function() 
				{
					this.initDataTable();					
				}).bind(dialog);
			 
			}
		}
	});
	//this.cEditButton = new YAHOO.widget.Button("editassociationbutton");
	this.cRemoveButton = new YAHOO.widget.Button("removeassociationbutton", {

		onclick: { 
			fn: (function() {
			
				var rowIds = this.cDataTable.getSelectedRows();
				if(rowIds != null && rowIds.length > 0)
				{
					var rowId = rowIds[0];
					var record = this.cDataTable.getRecord(rowId);
					
					var sourceId = record.getData("source-id");
					var destId = record.getData("dest-id");
					var assocType = record.getData("assoc-type");
					var formatId = record.getData("format-id");					
					
					var w = new WebStudio.Wizard();
					w.setDefaultJson(
					{
						refreshSession: 'true',
						contentId: sourceId,
						assocType: assocType,
						templateId: destId,
						formatId: formatId
					});
					w.onComplete = (function() 
					{
						this.initDataTable();					
					}).bind(dialog);
					
					var url = WebStudio.ws.studio("/wizard/content/associations/remove");
					w.start(url, 'content-associations');
				}	
								 
			}).bind(dialog)
		}
		
	});
	
	this.initDataTable();
}

WebStudio.CTADialog.prototype.initDataTable = function()
{
	if(this.cDataTable)
	{
		this.cDataTable.destroy();
		this.cDataTable = null;
	}
	
	// load content associations into the data table	
	var url = WebStudio.ws.studio("/api/content/associations");
	var myAjax = new Ajax(url, 
	{
		method: 'get',
		onComplete: (function(response)
		{
			var data = Json.evaluate(response);
			
			var myDataSource = new YAHOO.util.DataSource(data["content-associations"]);
			myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
			myDataSource.responseSchema = {
				fields: ["id", "source-id", "dest-id", "assoc-type", "format-id"]
			};
			
			// Table Column Definitions
			var myColumnDefs = [
		           { key:"id", label: "ID", sortable:true, resizeable:true },
		           { key:"source-id", label: "Content Type", sortable:true, resizeable:true },
		           { key:"dest-id", label: "Template", sortable:true, resizeable:true },
		           { key:"assoc-type", label: "Association Type", sortable:true, resizeable:true },
		           { key:"format-id", label: "Format", sortable:true, resizeable:true }
			];
			
			this.cDataTable = new YAHOO.widget.DataTable("ContentTypeAssociationsPanelDataTable", 
				myColumnDefs, myDataSource,
				{
					selectionMode: "single",
					draggableColumns: true 
				}
			);
			
			// Hide the id column
			this.cDataTable.hideColumn("id");
			
			// Hide the association type column
			this.cDataTable.hideColumn("assoc-type");
			
			// Set column widths
			this.cDataTable.setColumnWidth("source-id", 300);
			this.cDataTable.setColumnWidth("dest-id", 200);
									
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
	        	this.modal.show();
	        }
				
		}).bind(this)

	}).request();
}

WebStudio.CTADialog.prototype.popup = function() 
{	
	if(!this.modal)
	{
		this.modal = new YAHOO.widget.Panel("ContentTypeAssociationsPanel", {
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
}

WebStudio.CTADialog.prototype.popout = function()
{	
	this.modal.destroy();
	this.modal.hide();
}
