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
 * ObjectFinder component.
 * 
 * @namespace Alfresco
 * @class Alfresco.ObjectFinder
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Element = YAHOO.util.Element;
   
   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;

   /**
    * ObjectFinder constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.ObjectFinder} The new ObjectFinder instance
    * @constructor
    */
   Alfresco.ObjectFinder = function(htmlId)
   {
      this.name = "Alfresco.ObjectFinder";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "container", "datasource", "datatable", "json"], this.onComponentsLoaded, this);

      return this;
   }
   
   Alfresco.ObjectFinder.prototype =
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
          * The current value of the association
          *
          * @property currentValue
          * @type string
          */
         currentValue: "",
         
         /**
          * The type of the item to find
          *
          * @property itemType
          * @type string
          */
         itemType: "cm:content",

         /**
          * Single Select mode flag
          * 
          * @property singleSelectMode
          * @type boolean
          * @default false
          */
         singleSelectMode: true,
         
         /**
          * Number of characters required for a search.
          * 
          * @property minSearchTermLength
          * @type int
          * @default 3
          */
         minSearchTermLength: 3,
         
         /**
          * Maximum number of items to display in the results list
          * 
          * @property maxSearchResults
          * @type int
          * @default 100
          */
         maxSearchResults: 100
      },

      /**
       * Object container for storing YUI widget instances.
       * 
       * @property widgets
       * @type object
       */
      widgets: {},
      
      /**
       * Object container for storing YUI button instances, indexed by nodeRef.
       * 
       * @property itemSelectButtons
       * @type object
       */
      itemSelectButtons: {},
      
      /**
       * Current search term, obtained from form input field.
       * 
       * @property searchTerm
       * @type string
       */
      searchTerm: "",
      
      /**
       * Single selected item, for when in single select mode
       * 
       * @property singleSelectedItem
       * @type string
       */
      singleSelectedItem: "",

      /**
       * Selected items. Keeps a list of selected items for correct Add button state.
       * 
       * @property selectedItems
       * @type object
       */
      selectedItems: {},

      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.ObjectFinder} returns 'this' for method chaining
       */
      setOptions: function ObjectFinder_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },
      
      /**
       * Set messages for this component.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.ObjectFinder} returns 'this' for method chaining
       */
      setMessages: function ObjectFinder_setMessages(obj)
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
      onComponentsLoaded: function ObjectFinder_onComponentsLoaded()
      {
         Event.onContentReady(this.id, this.onReady, this, true);
      },
   
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function ObjectFinder_onReady()
      {  
         var me = this;
         
         // Search button
         this.widgets.searchButton = Alfresco.util.createYUIButton(this, "search-button", this.onSearchClick);
         
         // Select button
         this.widgets.selectButton = Alfresco.util.createYUIButton(this, "select-button", this.onSelectClick);

         // DataSource definition  
         var itemSearchUrl = Alfresco.constants.PROXY_URI + "api/pickerquery?";
         this.widgets.dataSource = new YAHOO.util.DataSource(itemSearchUrl);
         this.widgets.dataSource.responseType = YAHOO.util.DataSource.TYPE_JSON;
         this.widgets.dataSource.connXhrMode = "queueRequests";
         this.widgets.dataSource.responseSchema =
         {
             resultsList: "data.items",
             fields: ["name", "description", "nodeRef"]
         };
         
         // Setup the DataTable
         this._setupDataTable();
         
         // register the "enter" event on the search text field
         var searchText = Dom.get(this.id + "-search-text");
         
         new YAHOO.util.KeyListener(searchText,
         {
            keys: YAHOO.util.KeyListener.KEY.ENTER
         },
         {
            fn: function() 
            {
               me.onSearchClick();
            },
            scope: this,
            correctScope: true
         }, "keydown").enable();
         
         // Set initial focus
         searchText.focus();
      },
      
      /**
       * Setup the YUI DataTable with custom renderers.
       *
       * @method _setupDataTable
       * @private
       */
      _setupDataTable: function ObjectFinder__setupDataTable()
      {
         /**
          * DataTable Cell Renderers
          *
          * Each cell has a custom renderer defined as a custom function. See YUI documentation for details.
          * These MUST be inline in order to have access to the Alfresco.ObjectFinder class (via the "me" variable).
          */
         var me = this;

         /**
          * Description/detail custom datacell formatter
          *
          * @method renderCellDescription
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         var renderCellDescription = function ObjectFinder_renderCellDescription(elCell, oRecord, oColumn, oData)
         {
            var nameData = oRecord.getData("name");
            var descData = oRecord.getData("description");
            
            var desc = '<h3 class="itemname">' + $html(nameData) + '</h3>';
            if (descData.length != 0)
            {
               desc += '<div class="detail">' + $html(descData) + '</div>';
            }
            
            elCell.innerHTML = desc;
         };
         
         /**
          * Add button datacell formatter
          *
          * @method renderCellAvatar
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         var renderCellAddButton = function ObjectFinder_renderCellAddButton(elCell, oRecord, oColumn, oData)
         {
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");
            Dom.setStyle(elCell.parentNode, "text-align", "right");
            
            var nodeRef = oRecord.getData("nodeRef");
            var nodeId = nodeRef.substring(nodeRef.lastIndexOf("/"));
            var desc = '<span id="' + me.id + '-select-' + nodeId + '"></span>';
            elCell.innerHTML = desc;
            
            // create button if require - it is not required in the plain people list mode
            var button = new YAHOO.widget.Button(
            {
               type: "button",
               label: me._msg("form.control.association.button.select") + " >>",
               name: me.id + "-selectbutton-" + nodeId,
               container: me.id + '-select-' + nodeId,
               onclick:
               {
                  fn: me.onItemSelect,
                  obj: oRecord,
                  scope: me
               }
            });
            me.itemSelectButtons[nodeId] = button;
            
            if ((nodeId in me.selectedItems) || (me.options.singleSelectMode && me.singleSelectedItem != ""))
            {
               me.itemSelectButtons[nodeId].set("disabled", true);
            }
         };

         // DataTable column defintions
         var columnDefinitions = [
         {
            key: "item", label: "Item", sortable: false, formatter: renderCellDescription
         },
         {
            key: "actions", label: "Actions", sortable: false, formatter: renderCellAddButton, width: 100
         }];

         // DataTable definition
         this.widgets.dataTable = new YAHOO.widget.DataTable(this.id + "-results", columnDefinitions, this.widgets.dataSource,
         {
            renderLoopSize: 32,
            initialLoad: false,
            MSG_EMPTY: this._msg("form.control.association.message.instructions")
         });

         this.widgets.dataTable.doBeforeLoadData = function ObjectFinder_doBeforeLoadData(sRequest, oResponse, oPayload)
         {
            if (oResponse.results)
            {
               this.renderLoopSize = oResponse.results.length >> (YAHOO.env.ua.gecko === 1.8) ? 3 : 5;
            }
            return true;
         };
      },
      
      /**
       * Public function to clear the results DataTable
       */
      clearResults: function ObjectFinder_clearResults()
      {
         // Clear results DataTable
         if (this.widgets.dataTable)
         {
            var recordCount = this.widgets.dataTable.getRecordSet().getLength();
            this.widgets.dataTable.deleteRows(0, recordCount);
         }
         Dom.get(this.id + "-search-text").value = "";
         this.singleSelectedItem = "";
         this.selectedItems = {};
      },


      /**
       * YUI WIDGET EVENT HANDLERS
       * Handlers for standard events fired from YUI widgets, e.g. "click"
       */

      /**
       * Select item button click handler
       *
       * @method onItemSelect
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onItemSelect: function ObjectFinder_onItemSelect(event, p_obj)
      {
         var nodeRef = p_obj.getData("nodeRef");
         var nodeId = nodeRef.substring(nodeRef.lastIndexOf("/"));
         
         // Fire the personSelected bubble event
         /*YAHOO.Bubbling.fire("itemSelected",
         {
            nodeRef: nodeRef,
            name: p_obj.getData("name"),
            path: p_obj.getData("path")
         });*/
         
         // Add the nodeId to the selectedItems object
         this.selectedItems[nodeId] = true;
         this.singleSelectedItem = nodeId;
         
         // hide picker
         Dom.setStyle(this.id + "-picker", "display", "none");
         
         // Disable the add button(s)
         /*if (this.options.singleSelectMode)
         {
            for (button in this.itemSelectButtons)
            {
               this.itemSelectButtons[button].set("disabled", true);
            }
         }
         else
         {
            this.itemSelectButtons[nodeId].set("disabled", true);
         }*/
         
         // add the selected item to the appropriate _added hidden field
         Dom.get(this.id + "-added").value = nodeRef;
         
         // change the current value display
         Dom.get(this.id + "-current-value").innerHTML = nodeRef;
      },

      /**
       * Search button click event handler
       *
       * @method onSearchClick
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onSearchClick: function ObjectFinder_onSearchClick(e, p_obj)
      {
         var searchTermElem = Dom.get(this.id + "-search-text");
         var searchTerm = searchTermElem.value;
         searchTerm = $html(searchTerm);
         if (searchTerm.length < this.options.minSearchTermLength)
         {
            Alfresco.util.PopupManager.displayMessage(
            {
               text: this._msg("message.minimum-length", this.options.minSearchTermLength)
            });
            return;
         }
         
         this.itemSelectButtons = {};
         
         this._performSearch(searchTerm);
      },
         
      /**
       * Select button click event handler
       *
       * @method onSelectClick
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onSelectClick: function ObjectFinder_onSelectClick(e, p_obj)
      {
         // show picker
         Dom.setStyle(this.id + "-picker", "display", "block");
      },

      /**
       * PRIVATE FUNCTIONS
       */
      
      /**
       * Resets the YUI DataTable errors to our custom messages
       * NOTE: Scope could be YAHOO.widget.DataTable, so can't use "this"
       *
       * @method _setDefaultDataTableErrors
       * @param dataTable {object} Instance of the DataTable
       */
      _setDefaultDataTableErrors: function ObjectFinder__setDefaultDataTableErrors(dataTable)
      {
         var msg = Alfresco.util.message;
         dataTable.set("MSG_EMPTY", msg("form.control.association.message.empty", "Alfresco.ObjectFinder"));
         dataTable.set("MSG_ERROR", msg("form.control.association.message.error", "Alfresco.ObjectFinder"));
      },
      
      /**
       * Updates people list by calling data webscript
       *
       * @method _performSearch
       * @param searchTerm {string} Search term from input field
       */
      _performSearch: function ObjectFinder__performSearch(searchTerm)
      {
         // Reset the custom error messages
         this._setDefaultDataTableErrors(this.widgets.dataTable);
         
         // Don't display any message
         this.widgets.dataTable.set("MSG_EMPTY", this._msg("form.control.association.message.searching"));
         
         // Empty results table
         this.widgets.dataTable.deleteRows(0, this.widgets.dataTable.getRecordSet().getLength());
         
         var successHandler = function ObjectFinder__pS_successHandler(sRequest, oResponse, oPayload)
         {
            this._setDefaultDataTableErrors(this.widgets.dataTable);
            this.widgets.dataTable.onDataReturnInitializeTable.call(this.widgets.dataTable, sRequest, oResponse, oPayload);
         }
         
         var failureHandler = function ObjectFinder__pS_failureHandler(sRequest, oResponse)
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
                  this._setDefaultDataTableErrors(this.widgets.dataTable);
               }
            }
         }
         
         this.searchTerm = searchTerm;
         this.widgets.dataSource.sendRequest(this._buildSearchParams(searchTerm),
         {
               success: successHandler,
               failure: failureHandler,
               scope: this
         });
      },

      /**
       * Build URI parameter string for People Finder JSON data webscript
       *
       * @method _buildSearchParams
       * @param searchTerm {string} Search terms to query
       */
      _buildSearchParams: function ObjectFinder__buildSearchParams(searchTerm)
      {
         return "type=" + this.options.itemType + "&filter=" + encodeURIComponent(searchTerm) + "&maxResults=" + this.options.maxSearchResults;
      },
      
      /**
       * Gets a custom message
       *
       * @method _msg
       * @param messageId {string} The messageId to retrieve
       * @return {string} The custom message
       * @private
       */
      _msg: function ObjectFinder__msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, "Alfresco.ObjectFinder", Array.prototype.slice.call(arguments).slice(1));
      }
   };
})();