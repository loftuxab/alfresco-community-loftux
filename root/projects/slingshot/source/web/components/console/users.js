/**
 * Copyright (C) 2005-2008 Alfresco Software Limited.
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
 * ConsoleUsers tool component.
 * 
 * @namespace Alfresco
 * @class Alfresco.ConsoleUsers
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
    * ConsoleUsers constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.ConsoleUsers} The new ConsoleUsers instance
    * @constructor
    */
   Alfresco.ConsoleUsers = function(htmlId)
   {
      this.name = "Alfresco.ConsoleUsers";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);
      
      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "container", "datasource", "datatable", "json"], this.onComponentsLoaded, this);
      
      return this;
   }
   
   Alfresco.ConsoleUsers.prototype =
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
       * Current search term, obtained from form input field.
       * 
       * @property searchTerm
       * @type string
       */
      searchTerm: "",

      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.ConsoleUsers} returns 'this' for method chaining
       */
      setOptions: function ConsoleUsers_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },
      
      /**
       * Set messages for this component.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.ConsoleUsers} returns 'this' for method chaining
       */
      setMessages: function ConsoleUsers_setMessages(obj)
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
      onComponentsLoaded: function ConsoleUsers_onComponentsLoaded()
      {
         Event.onContentReady(this.id, this.onReady, this, true);
      },
   
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function ConsoleUsers_onReady()
      {  
         var me = this;
         
         // Search button
         this.widgets.searchButton = Alfresco.util.createYUIButton(this, "search-button", this.onSearchClick);
         // New User button
         this.widgets.newuserButton = Alfresco.util.createYUIButton(this, "newuser-button", this.onNewUserClick);
         
         //
         // TODO: datatable and datasource setup
         //
         
         // Set initial focus
         var searchText = Dom.get(this.id + "-search-text");
         searchText.focus();
         
         /*
          * Enter key listener function needs to be enclosed due to having "window" scope
          *
          * @method: onKeyEnter
          * @param id
          * @param keyEvent {object} The key event details
          */
         var onKeyEnter = function ConsoleUsers_onKeyEnter(id, keyEvent)
         {
            me.onSearchClick.call(me, keyEvent, null);
            return false;
         }

         // Enter key listener
         var enterListener = new YAHOO.util.KeyListener(searchText,
         {
            keys: YAHOO.util.KeyListener.KEY.ENTER
         }, onKeyEnter, "keyup");
         
         enterListener.enable();
      },
      
      /**
       * Setup the YUI DataTable with custom renderers.
       *
       * @method _setupDataTable
       * @private
       */
      _setupDataTable: function ConsoleUsers__setupDataTable()
      {
         /**
          * DataTable Cell Renderers
          *
          * Each cell has a custom renderer defined as a custom function. See YUI documentation for details.
          * These MUST be inline in order to have access to the Alfresco.ConsoleUsers class (via the "me" variable).
          */
         var me = this;
         
         // TODO: add datatable renderers
      },

      /**
       * YUI WIDGET EVENT HANDLERS
       * Handlers for standard events fired from YUI widgets, e.g. "click"
       */

      /**
       * Search button click event handler
       *
       * @method onSearchClick
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onSearchClick: function ConsoleUsers_onSearchClick(e, p_obj)
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
         
         this._performSearch(searchTerm);
      },
      
      /**
       * New User button click event handler
       *
       * @method onNewUserClick
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onNewUserClick: function ConsoleUsers_onNewUserClick(e, p_obj)
      {
         
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
      _setDefaultDataTableErrors: function ConsoleUsers__setDefaultDataTableErrors(dataTable)
      {
         var msg = Alfresco.util.message;
         dataTable.set("MSG_EMPTY", msg("message.empty", "Alfresco.ConsoleUsers"));
         dataTable.set("MSG_ERROR", msg("message.error", "Alfresco.ConsoleUsers"));
      },
      
      /**
       * Updates user list by calling data webscript
       *
       * @method _performSearch
       * @param searchTerm {string} Search term from input field
       */
      _performSearch: function ConsoleUsers__performSearch(searchTerm)
      {
         // Reset the custom error messages
         this._setDefaultDataTableErrors(this.widgets.dataTable);
         
         // Don't display any message
         this.widgets.dataTable.set("MSG_EMPTY", this._msg("message.searching"));
         
         // Empty results table
         this.widgets.dataTable.deleteRows(0, this.widgets.dataTable.getRecordSet().getLength());
         
         var successHandler = function ConsoleUsers__ps_successHandler(sRequest, oResponse, oPayload)
         {
            this._setDefaultDataTableErrors(this.widgets.dataTable);
            this.widgets.dataTable.onDataReturnInitializeTable.call(this.widgets.dataTable, sRequest, oResponse, oPayload);
         }
         
         var failureHandler = function ConsoleUsers__ps_failureHandler(sRequest, oResponse)
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
       * Build URI parameters for doclist JSON data webscript
       *
       * @method _buildSearchParams
       * @param path {string} Path to query
       */
      _buildSearchParams: function ConsoleUsers__buildSearchParams(searchTerm)
      {
         return "q=" + encodeURIComponent(searchTerm) + "&maxResults=" + this.options.maxSearchResults;
      },
      
      /**
       * Gets a custom message
       *
       * @method _msg
       * @param messageId {string} The messageId to retrieve
       * @return {string} The custom message
       * @private
       */
      _msg: function ConsoleUsers__msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, "Alfresco.ConsoleUsers", Array.prototype.slice.call(arguments).slice(1));
      }
   };
})();