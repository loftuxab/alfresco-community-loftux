/**
 * DataList component.
 * 
 * Displays a datalist
 * 
 * @namespace Alfresco
 * @class Alfresco.DataList
 */
(function()
{
    
   /**
   * YUI Library aliases
   */
   var Dom = YAHOO.util.Dom,
       Event = YAHOO.util.Event,
       Selector = YAHOO.util.Selector,
       Bubbling = YAHOO.Bubbling;

   /**
    * DataList constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.DataList} The new DataList instance
    * @constructor
    */
   Alfresco.DataList = function(htmlId)
   {
      Alfresco.DataList.superclass.constructor.call(this, "Alfresco.DataList", htmlId, ["button", "container", "datasource", "datatable", "calendar","paginator","animation"]);
      
      this.initEvents();
      return this;
   };
   
   YAHOO.extend(Alfresco.DataList, Alfresco.component.Base,
   {
      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       */
      options:
      {
         
      },

      /**
       * Fired by YUI when parent element is available for scripting
       * @method onReady
       */
      onReady: function DataList_onReady()
      {
         this.getDataListData();
         this.initDataTableSchema(this.options.columnDefs,this.options.schema);
         this.initDataTable();
      },
      
      onInteractionEvent: function onInteractionEvent(e)
      {
         var evtName = e,
            evtArgs = Array.prototype.slice.apply(arguments);
            elTarget = Event.getTarget(e);
         if (e.type==='click') 
         {
            if ( Selector.test(elTarget, 'button#'+this.id+'-editRowBtn-button') )
            {
               this.onEditRowBtnHandler.apply(this, evtArgs);
            }
            else if ( Selector.test(elTarget, 'button#'+this.id+'-deleteRowBtn-button') )
            {
               this.onDeleteRowBtnHandler.apply(this, evtArgs);
            }

         } 
      },
      
      initEvents: function initEvents()
      {
         Event.on(this.id,'click',this.onInteractionEvent,this, true);
         Bubbling.on("dataListLoad", this.onDataListLoad, this);
      },
      
      /**
       * initDataTableSchema
       * 
       * @method Initialises data grid schema
       * 
       * Sets up data source for datatable. It adds a select column and an action column so
       * data does not need to have this fields in the schema when this component is loaded or 
       * within the data too. 
       */
      initDataTableSchema: function initDataTableSchema(columnDefs, schema)
      {
         //initialise column definitions - add select and action columns
         this.modules.columndefs = [{key:'select', label:'Select', formatter:'checkbox'}].concat(columnDefs);
         this.modules.columndefs.push({key:"action", label:'Action'});
         //initialise schema - add select and action columns
         schema.fields = [{key:'select'}].concat(schema.fields);
         schema.fields.push({key:"action"});
         this.modules.schema = schema;
         // this.modules.datasource = new YAHOO.util.DataSource('datalist-data.json?id='+this.options.datalistId);
         this.modules.datasource = new YAHOO.util.DataSource(this.modules.data.formatting);
         this.modules.datasource.responseType = YAHOO.util.DataSource.TYPE_JSON;
         this.modules.datasource.responseSchema = this.modules.schema;
         // this.modules.datasource.connXhrMode = "queueRequests";         
         this.createActionsCellHTML();
      },
      
      /**
       * Initialises datatable
       * @method initDataTable
       * 
       */
      initDataTable : function initDataTable()
      {
         //initialise datatable
         var datatable = this.widgets.datatable = new YAHOO.widget.DataTable(this.id+'-grid', this.modules.columndefs, this.modules.datasource, {
            draggableColumns:true,
            paginator : new YAHOO.widget.Paginator({ 
	            rowsPerPage    : 11 
	         })
         }); 
         //patch datatable to restrict ordering of columns
         this._patchReorderColumn();
         //row highlight
         // datatable.subscribe("rowMouseoverEvent", datatable.onEventHighlightRow);
         //          datatable.subscribe("rowMouseoutEvent", datatable.onEventUnhighlightRow);
         //          datatable.subscribe("rowClickEvent", datatable.onEventSelectRow);
         //          
         // cell highlight
         var highlightEditableCell = function(oArgs) {
            var elCell = oArgs.target;
            if(Dom.hasClass(elCell, "yui-dt-editable")) {
                this.highlightCell(elCell);
            }
         };
         datatable.set("selectionMode","single");
         datatable.subscribe("rowClickEvent", this.onDatatableSelectRow, this, true);
         datatable.subscribe('tableMouseoutEvent', this.onHideItemActions, this, true);
         datatable.subscribe("rowMouseoverEvent", this.onShowItemActions, this, true);
         datatable.subscribe("rowMouseoutEvent", this.onHideItemActions, this, true);
         datatable.subscribe("cellMouseoverEvent", highlightEditableCell);
         datatable.subscribe("cellMouseoutEvent", datatable.onEventUnhighlightCell);
         datatable.subscribe("cellClickEvent", datatable.onEventShowCellEditor);
         datatable.subscribe("columnReorderEvent", this.onColumnReorder, this, true);
         datatable.subscribe("rowDeleteEvent", this.onDeleteRowEvent, this, true);
         datatable.subscribe("rowUpdateEvent", this.onUpdateRowEvent, this, true);         
         // this.getDataListData();         
         // Programmatically select the first row
         // this.widgets.datatable.selectRow(this.widgets.datatable.getTrEl(0));
         
      },
      
      /**
       * Restricts ordering of columns so columns cannot be moved to the first or last
       * columns and also that the last and first columns do not (drag and) drop.
       * 
       * @method _patchReorderColumn
       * 
       *  
       */
      _patchReorderColumn: function _patchReorderColumn()
      {
         var origReorderColumnFunc = this.widgets.datatable.reorderColumn;
             numColumns = this.modules.columndefs.length;

         this.widgets.datatable.reorderColumn = function newReorder(oColumn, index)
         {
            var colIndex = oColumn.getIndex();
            if ( ((colIndex!=0) && colIndex!=numColumns-1) && (index !=0 && index!=(numColumns-1)) )
            {
               return origReorderColumnFunc.apply(this, arguments);
            }
            return false;
         };       
           
      },
      
      /**
       * getDataListData
       * 
       * Retrieves datalist
       * 
       */
      getDataListData: function getDataListData(){
         Bubbling.fire('dataListLoad', {
               formatting: {
                 items: [
                    { title: "bananas", duedate:new Date(2006, 10, 1), priority:1, action:"" },
                    { title: "apples", duedate:new Date(2009, 9, 1), priority:2, action:"" },
                    { title: "orange", duedate:new Date(2010, 29, 1), priority:3, action:"" },
                    { title: "papaya", duedate:new Date(2008, 30, 1), priority:5, action:"" },
                    { title: "kiwi", duedate:new Date(2005, 14, 1), priority:4, action:"" },
                    { title: "pineapple", duedate:new Date(2003, 23, 1), priority:1, action:"" },
                    { title: "pear", duedate:new Date(2006, 32, 1), priority:2, action:"" },
                    { title: "grapes", duedate:new Date(1997, 12, 1), priority:5, action:"" },
                    { title: "strawberry", duedate:new Date(2007, 1, 1), priority:3, action:"" },
                    { title: "melon", duedate:new Date(2002, 1, 1), priority:2, action:"" },
                    { title: "mango", duedate:new Date(2001, 11, 1), priority:3, action:"" },
                    { title: "watermelon", duedate:new Date(2008, 26, 1), priority:1, action:"" },
                    { title: "passion fruit", duedate:new Date(2007, 28, 1), priority:3, action:"" }                    
                 ]
               }
            }
         );         
         // this.modules.datasource.sendRequest("http://localhost:8000/datalist-data.json?id="+this.options.datalistId, {
         //    success: this.onDataListLoad,
         //    failure: this.onDataListLoad,
         //    scope: this
         // });
      },
      
      
      /**
       * createActionsCellHTML
       * 
       * Create the HTML for the action cell. This are based on YUI buttons and are reused for multiple rows
       */
      createActionsCellHTML: function createActionsCellHTML(){
         this.widgets.editRowBtn= new YAHOO.widget.Button({ title:this.msg('label.edit-list-item'), id:this.id+'-editRowBtn', container:this.id+"-grid-row-actions"});
         this.widgets.editRowBtn.addClass('editRowBtn');
         this.widgets.deleteRowBtn= new YAHOO.widget.Button({ title:this.msg('label.delete-list-item'), id:this.id+'-deleteRowBtn', container:this.id+"-grid-row-actions"});
         this.widgets.deleteRowBtn.addClass('deleteRowBtn');
         this.widgets.rowActions = Dom.get(this.id+'-grid-row-actions');
      },
      
      onShowItemActions: function onShowItemActions(oArgs)
      {
         var target = oArgs.target;
         var rowActions = this.widgets.rowActions.parentNode.removeChild(this.widgets.rowActions);
         target.lastChild.firstChild.appendChild(rowActions);
         this.widgets.rowActions.style.visibility='visible';
      },
      
      onHideItemActions: function onHideItemActions(oArgs)
      {
         this.widgets.rowActions.style.visibility='hidden';            
      },
      
      onDataListLoad: function onDataListLoad(e, data)
      {
         this.modules.data = data[1];
      },
      
      /**
       * onEditRowBtnHandler
       * 
       * Handler for delete list item button
       * @method onEditRowBtnHandler
       */
      onEditRowBtnHandler: function onEditRowBtnHandler(e)
      {
         //most of this will handled by forms UI
         if (!this.widgets.dialog)
         {
            this.widgets.dialog = Alfresco.util.createYUIPanel('template-datalist-dialog', {
               width:'350px'
            }, {
               render:true,
               type:YAHOO.widget.Dialog
            });            
         }
         this.widgets.dialog.show();
         this.widgets.dialogFormSubmit = new YAHOO.widget.Button("template-datalist-form-submit");
         this.widgets.dialogFormCancel= new YAHOO.widget.Button("template-datalist-form-cancel" );

         Event.addListener( 'template-datalist-form','submit', function(e) {
            var target = Event.getTarget(e);
            Event.stopEvent(e);
            var title = document.getElementsByName('prop_cm_title')[0].value,
                duedate = document.getElementsByName('prop_cm_duedate')[0].value,
                priority = document.getElementsByName('prop_cm_priority')[0].value;
            
            this.widgets.datatable.updateRow(this.currentRow, {
               title: title,
               priority: priority,
               duedate:new Date(2009,1,1),
               action:"" // have to add this as otherwise whitespace is added to the div.liner creating a larger row
            });

            this.widgets.dialog.hide();                
         },this, true);
         
         Event.addListener( 'template-datalist-form-cancel','click', function(e) {
            this.widgets.dialog.hide();
         },this,true);
      },
      
      /**
       * onDeleteRowBtnHandler
       * 
       * Handler for delete list item button
       * @method onDeleteRowBtnHandler
       */
      onDeleteRowBtnHandler: function onDeleteRowBtnHandler(e)
      {
         var me = this;
         Alfresco.util.PopupManager.displayPrompt(
         {
            title:'Delete item?',
            text: 'Are you sure you want to delete this item?',
            buttons: [
               {
                  text: 'Ok',
                  handler: function()
                  {
                     // Dom.addClass(me.currentRow, 'deleting');
                     var fadeAnim = new YAHOO.util.Anim(me.currentRow, {
                        opacity:{to:0},
                        duration:1.5
                     });
                     fadeAnim.animate();
                     YAHOO.lang.later(1000, me, function() { this.widgets.datatable.deleteRow(this.currentRow);});
                     this.hide();
                  },
                  isDefault: true
               },
               {
                  text: 'Cancel',
                  handler: function()
                  {
                     this.hide();
                  },
                  isDefault: false
               }            
            ]
         });
      },
      
      onColumnReorder: function onColumnReorder(e)
      {

      },
      
      onDatatableSelectRow: function onDatatableSelectRow(e)
      {
         this.currentRow = Event.getTarget(e);
         this.widgets.datatable.onEventSelectRow(e);
      },
      
      onUpdateRowEvent: function onRowUpdateEvent(e)
      {
        Alfresco.util.PopupManager.displayMessage({
               text: 'Item updated'
         });
      },
      
      onDeleteRowEvent: function onDeleteRowEvent(e)
      {
         Alfresco.util.PopupManager.displayMessage({
               text: 'Item Deleted'
         });
      }
       
   });
})();