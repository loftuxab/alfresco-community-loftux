/**
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing
 */
 
/**
 * DispositionActions component.
 * 
 * @namespace Alfresco
 * @class Alfresco.DispositionActions
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML,
      $combine = Alfresco.util.combinePaths,
      $hasEventInterest = Alfresco.util.hasEventInterest;

   /**
    * DispositionActions constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.DispositionActions} The new DispositionActions instance
    * @constructor
    */
   Alfresco.DispositionActions = function(htmlId)
   {
      // Mandatory properties
      this.name = "Alfresco.DispositionActions";
      this.id = htmlId;

      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "menu", "container", "resize", "datasource", "datatable"], this.onComponentsLoaded, this);

      // Initialise prototype properties
      this.widgets = {};
      this.columns = [];

      return this;
   };
   
   Alfresco.DispositionActions.prototype =
   {
      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       */
      options:
      {
         /**
          * The current value
          *
          * @property currentValue
          * @type string
          */
         currentValue: ""
      },

      /**
       * Object container for storing YUI widget instances.
       * 
       * @property widgets
       * @type object
       */
      widgets: null,

      /**
       * Resizable columns
       * 
       * @property columns
       * @type array
       * @default []
       */
      columns: null,

      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.DispositionActions} returns 'this' for method chaining
       */
      setOptions: function DispositionActions_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },
      
      /**
       * Set messages for this component.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.DispositionActions} returns 'this' for method chaining
       */
      setMessages: function DispositionActions_setMessages(obj)
      {
         Alfresco.util.addMessages(obj, this.name);
         return this;
      },
      
      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function DispositionActions_onComponentsLoaded()
      {
         Event.onContentReady(this.id, this.onReady, this, true);
      },

      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function DispositionActions_onReady()
      {
         this._createControls();
         this._updateItems();
      },
      
      /**
       * PRIVATE FUNCTIONS
       */
      
      /**
       * Creates UI controls
       *
       * @method _createControls
       */
      _createControls: function DispositionActions__createControls()
      {
         var me = this;
         
         // DataSource definition  
         var dispositionActionsUrl = Alfresco.constants.PROXY_URI + "api/forms/dispositionactions";
         this.widgets.dataSource = new YAHOO.util.DataSource(dispositionActionsUrl);
         this.widgets.dataSource.responseType = YAHOO.util.DataSource.TYPE_JSON;
         this.widgets.dataSource.connMethodPost = true;
         this.widgets.dataSource.connMgr.setDefaultPostHeader(false);
         this.widgets.dataSource.connMgr.initHeader("Content-Type", "application/json");
         this.widgets.dataSource.connXhrMode = "queueRequests";
         this.widgets.dataSource.responseSchema =
         {
             resultsList: "data.items",
             fields: ["action", "description", "period", "periodproperty", "event", "trigger", "nodeRef"]
         };
         
         /**
          * Action datacell formatter
          *
          * @method renderItemIcon
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         var renderItemAction = function OR__cC_renderItemIcon(elCell, oRecord, oColumn, oData)
         {
            var actionData = oRecord.getData("action");
            var actionDisplay = "None";
            
            if (actionData === "cutoff")
            {
               actionDisplay = "Cut Off";
            }
            else if (actionData === "destroy")
            {
               actionDisplay = "Destroy";
            }
            else if (actionData === "transfer")
            {
               actionDisplay = "Transfer";
            }
            else if (actionData === "transferNARA")
            {
               actionDisplay = "Transfer to NARA";
            }
            else if (actionData === "retain")
            {
               actionDisplay = "Retain";
            }
            
            elCell.innerHTML = actionDisplay;
         };

         /**
          * Period datacell formatter
          *
          * @method renderItemName
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         var renderItemPeriod = function OR__cC_renderItemName(elCell, oRecord, oColumn, oData)
         {
            var periodData = oRecord.getData("period");
            var periodDisplay = "";
            
            if (periodData !== "")
            {
               var sepIdx = periodData.indexOf("|");
               var whenData = periodData.substring(0, sepIdx);
               var whenDisplay = "None";
               var freq = periodData.substring(sepIdx+1);
               
               if (whenData === "immediately")
               {
                  whenDisplay =  "Immediately";
               }
               else if (whenData === "none")
               {
                  whenDisplay =  "None";
               }
               else if (whenData === "fyend")
               {
                  whenDisplay =  "Financial Year End";
               }
               else if (whenData === "month")
               {
                  whenDisplay =  "Month";
               }
               else if (whenData === "monthend")
               {
                  whenDisplay =  "Month End";
               }
               else if (whenData === "quarterend")
               {
                  whenDisplay =  "Quarter End";
               }
               else if (whenData === "week")
               {
                  whenDisplay =  "Week";
               }
               else if (whenData === "year")
               {
                  whenDisplay =  "Year";
               }
               else if (whenData === "yearend")
               {
                  whenDisplay =  "Year End";
               }
               
               // generate the textual representation of period data
               if (whenData !== "immediately" && whenData !== "none")
               {
                  periodDisplay += "After ";
               }
               if (freq > 0)
               {
                  periodDisplay += freq + " ";
               }
               periodDisplay += whenDisplay;
               if (freq > 1)
               {
                  periodDisplay += "s";
               }
            }
            else
            {
               periodDisplay = "None";
            }
            
            elCell.innerHTML = periodDisplay;
         };
         
         /**
          * Period property datacell formatter
          *
          * @method renderItemIcon
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         var renderItemPeriodProperty = function OR__cC_renderItemIcon(elCell, oRecord, oColumn, oData)
         {
            var ppData = oRecord.getData("periodproperty");
            var ppDisplay = "";
            
            if (ppData === "{http://www.alfresco.org/model/recordsmanagement/1.0}dateFiled")
            {
               ppDisplay = "Date Filed";
            }
            else if (ppData === "{http://www.alfresco.org/model/recordsmanagement/1.0}cutOffDate")
            {
               ppDisplay = "Cut Off Date";
            }
            
            elCell.innerHTML = ppDisplay;
         };

         // DataTable column defintions
         var columnDefinitions = [
         {
            key: "action", label: "Action", sortable: false, formatter: renderItemAction
         },
         {
            key: "description", label: "Description", sortable: false
         },
         {
            key: "period", label: "Period", sortable: false, formatter: renderItemPeriod
         },
         {
            key: "periodproperty", label: "Period Property", sortable: false, formatter: renderItemPeriodProperty
         },
         {
            key: "event", label: "Events", sortable: false
         }];

         this.widgets.dataTable = new YAHOO.widget.DataTable(this.id + "-results", columnDefinitions, this.widgets.dataSource,
         {
            renderLoopSize: 32,
            initialLoad: false,
            MSG_EMPTY: this._msg("form.control.object-picker.items-list.loading")
         });
      },
      
      /**
       * Updates item list by calling data webscript
       *
       * @method _updateItems
       * @param nodeRef {string} Parent nodeRef
       */
      _updateItems: function DispositionActions__updateItems(nodeRef)
      {
         // Empty results table
         this.widgets.dataTable.set("MSG_EMPTY", this._msg("form.control.object-picker.items-list.loading"));
         //this.widgets.dataTable.deleteRows(0, this.widgets.dataTable.getRecordSet().getLength());
         
         var successHandler = function DispositionActions__uI_successHandler(sRequest, oResponse, oPayload)
         {
            //this.options.parentNodeRef = nodeRef;
            this.widgets.dataTable.set("MSG_EMPTY", this._msg("form.control.object-picker.items-list.empty"));
            this.widgets.dataTable.onDataReturnInitializeTable.call(this.widgets.dataTable, sRequest, oResponse, oPayload);
         };
         
         var failureHandler = function DispositionActions__uI_failureHandler(sRequest, oResponse)
         {
            if (oResponse.status == 401)
            {
               // Our session has likely timed-out, so refresh to offer the login page
               window.location.reload();
            }
            else
            {
               try
               {
                  var response = YAHOO.lang.JSON.parse(oResponse.responseText);
                  this.widgets.dataTable.set("MSG_ERROR", response.message);
                  this.widgets.dataTable.showTableMessage(response.message, YAHOO.widget.DataTable.CLASS_ERROR);
               }
               catch(e)
               {
               }
            }
         };
         
         // get the data
         var items = YAHOO.lang.JSON.stringify(this.options.currentValue.split(",")); 
         this.widgets.dataSource.sendRequest("{ items: " + items + " }",
         {
            success: successHandler,
            failure: failureHandler,
            scope: this
         });
      },
      
      /**
       * Gets a custom message
       *
       * @method _msg
       * @param messageId {string} The messageId to retrieve
       * @return {string} The custom message
       * @private
       */
      _msg: function DispositionActions__msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, "Alfresco.DispositionActions", Array.prototype.slice.call(arguments).slice(1));
      }
   };
})();


