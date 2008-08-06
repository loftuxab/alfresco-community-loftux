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
 * PeopleFinder component.
 * 
 * @namespace Alfresco
 * @class Alfresco.PeopleFinder
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
    * PeopleFinder constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.PeopleFinder} The new PeopleFinder instance
    * @constructor
    */
   Alfresco.PeopleFinder = function(htmlId)
   {
      this.name = "Alfresco.PeopleFinder";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "container", "datasource", "datatable", "json"], this.onComponentsLoaded, this);
   
      /**
       * Decoupled event listeners
       */
      YAHOO.Bubbling.on("personDeselected", this.onPersonDeselected, this);

      return this;
   }
   
   Alfresco.PeopleFinder.prototype =
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
          * Current siteId.
          * 
          * @property siteId
          * @type string
          */
         siteId: ""
      },

      /**
       * Object container for storing YUI widget instances.
       * 
       * @property widgets
       * @type object
       */
      widgets: {},
      
      /**
       * Object container for storing YUI button instances, indexed by username.
       * 
       * @property userSelectButtons
       * @type object
       */
      userSelectButtons: {},
      
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
       * @return {Alfresco.PeopleFinder} returns 'this' for method chaining
       */
      setOptions: function PeopleFinder_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },
      
      /**
       * Set messages for this component.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.PeopleFinder} returns 'this' for method chaining
       */
      setMessages: function PeopleFinder_setMessages(obj)
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
      onComponentsLoaded: function PeopleFinder_onComponentsLoaded()
      {
         Event.onContentReady(this.id, this.onReady, this, true);
      },
   
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function PeopleFinder_onReady()
      {  
         var me = this;

         // Search button
         this.widgets.searchButton = Alfresco.util.createYUIButton(this, "search-button", this.onSearchClick);

         // DataSource definition  
         var peopleSearchUrl = Alfresco.constants.PROXY_URI + "api/people?";
         this.widgets.dataSource = new YAHOO.util.DataSource(peopleSearchUrl);
         this.widgets.dataSource.responseType = YAHOO.util.DataSource.TYPE_JSON;
         this.widgets.dataSource.connXhrMode = "queueRequests";
         this.widgets.dataSource.responseSchema =
         {
             resultsList: "people",
             fields: ["url", "userName", "avatar", "title", "firstName", "lastName", "organisation", "jobtitle", "email"]
         };

         this.widgets.dataSource.doBeforeParseData = function PeopleFinder_doBeforeParseData(oRequest, oFullResponse)
         {
            var updatedResponse = oFullResponse;
               
            if (oFullResponse)
            {
               var items = [];
               
               // Determine list of sites to show
               if (me.searchTerm.length > 0)
               {
                  // Filter the results for the search term
                  var lowerCaseTerm = me.searchTerm.toLowerCase();
                  var personData, firstName, lastName;
                  for (var i = 0, j = oFullResponse.people.length; i < j; i++)
                  {
                     personData = oFullResponse.people[i];
                     firstName = (personData.firstName !== null) ? personData.firstName.toLowerCase() : "";
                     lastName = (personData.lastName !== null) ? personData.lastName.toLowerCase() : "";
                     
                     // Determine if person matches search term
                     if ((firstName.indexOf(lowerCaseTerm) != -1) || (lastName.indexOf(lowerCaseTerm) != -1))
                     {
                        // Add site to list
                        items.push(personData);
                     }
                  }
               }
               
               // we need to wrap the array inside a JSON object so the DataTable is happy
               updatedResponse =
               {
                  "people": items
               };
            }
            
            return updatedResponse;
         }
         
         // Setup the DataTable
         this._setupDataTable();

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
         var onKeyEnter = function PeopleFinder_onKeyEnter(id, keyEvent)
         {
            me.onSearchClick.call(me, keyEvent, null);
         }

         // Enter key listener
         var enterListener = new YAHOO.util.KeyListener(searchText,
         {
            keys: YAHOO.util.KeyListener.KEY.ENTER
         }, onKeyEnter, "keydown");
         enterListener.enable();

         // Show button here to prevent UI artifacts on YUI button decoration
         Dom.setStyle(this.id + "-search-button", "visibility", "visible");
      },
      
      /**
       * Setup the YUI DataTable with custom renderers.
       *
       * @method _setupDataTable
       * @private
       */
      _setupDataTable: function PeopleFinder__setupDataTable()
      {
         /**
          * DataTable Cell Renderers
          *
          * Each cell has a custom renderer defined as a custom function. See YUI documentation for details.
          * These MUST be inline in order to have access to the Alfresco.PeopleFinder class (via the "me" variable).
          */
         var me = this;
          
         /**
          * User avatar custom datacell formatter
          *
          * @method renderCellAvatar
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         var renderCellAvatar = function PeopleFinder_renderCellAvatar(elCell, oRecord, oColumn, oData)
         {
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

            var avatarUrl = Alfresco.constants.URL_CONTEXT + "components/images/no-user-photo-64.png";
            if (oRecord.getData("avatar") !== undefined)
            {
               avatarUrl = Alfresco.constants.PROXY_URI + oRecord.getData("avatar") + "?c=queue&ph=true";
            }

            elCell.innerHTML = '<img src="' + avatarUrl + '" alt="avatar" />';
         };

         /**
          * Description/detail custom datacell formatter
          *
          * @method renderCellDescription
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         var renderCellDescription = function PeopleFinder_renderCellDescription(elCell, oRecord, oColumn, oData)
         {
            // Currently rendering all results the same way
            var name = oRecord.getData("userName");
            var firstName = oRecord.getData("firstName");
            var lastName = oRecord.getData("lastName");
            if ((firstName !== undefined) || (lastName !== undefined))
            {
               name = firstName ? firstName + " " : "";
               name += lastName ? lastName : "";
            }

            var title = oRecord.getData("jobtitle") ? oRecord.getData("jobtitle") : "";
            var organisation = oRecord.getData("organisation") ? oRecord.getData("organisation") : "";
            desc = '<h3 class="itemname">' + $html(name) + '</a></h3>';
            desc += '<div class="detail">' + $html(title) + '</div>';
            desc += '<div class="detail">' + $html(organisation) + '</div>';
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
         var renderCellAddButton = function PeopleFinder_renderCellAddButton(elCell, oRecord, oColumn, oData)
         {
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");
            
            var id = oRecord.getData("userName");
            var desc = '<span id="' + me.id + '-select-' + id + '"></span>';
            elCell.innerHTML = desc;

            // create button
            var button = new YAHOO.widget.Button(
            {
               type: "button",
               label: me._msg("button.add") + " >>",
               name: me.id + "-selectbutton-" + id,
               container: me.id + '-select-' + id,
               onclick:
               {
                  fn: me.onPersonSelect,
                  obj: oRecord,
                  scope: me
               }
            });
            me.userSelectButtons[id] = button;
         };

         // DataTable column defintions
         var columnDefinitions = [
         {
            key: "avatar", label: "Avatar", sortable: false, formatter: renderCellAvatar, width: 70
         },
         {
            key: "person", label: "Description", sortable: false, formatter: renderCellDescription
         },
         {
            key: "actions", label: "Actions", sortable: false, formatter: renderCellAddButton, width: 80
         }];

         // DataTable definition
         YAHOO.widget.DataTable.MSG_EMPTY = "";
         this.widgets.dataTable = new YAHOO.widget.DataTable(this.id + "-results", columnDefinitions, this.widgets.dataSource,
         {
            renderLoopSize: 8,
            initialLoad: false
         });
      },


      /**
       * YUI WIDGET EVENT HANDLERS
       * Handlers for standard events fired from YUI widgets, e.g. "click"
       */

      /**
       * Select person button click handler
       *
       * @method onPersonSelect
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onPersonSelect: function PeopleFinder_onPersonSelect(event, p_obj)
      {
         var userName = p_obj.getData("userName");
         
         // Fire the personSelected bubble event
         YAHOO.Bubbling.fire("personSelected",
         {
            userName: userName,
            firstName: p_obj.getData("firstName"),
            lastName: p_obj.getData("lastName"),
            email: p_obj.getData("email")
         });
         
         // Disable the add button
         this.userSelectButtons[userName].set("disabled", true);
      },

      /**
       * Search button click event handler
       *
       * @method onSearchClick
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onSearchClick: function PeopleFinder_onSearchClick(e, p_obj)
      {
         // fetch the firstname, lastname nad email
         var searchTermElem = Dom.get(this.id + "-search-text");
         var searchTerm = searchTermElem.value;
         searchTerm = $html(searchTerm);
         if (searchTerm.length < 3)
         {
            Alfresco.util.PopupManager.displayMessage(
            {
               text: this._msg("message.minimum-length")
            });
            return;
         }
         
         this._performSearch(searchTerm);
      },


      /**
       * BUBBLING LIBRARY EVENT HANDLERS FOR PAGE EVENTS
       * Disconnected event handlers for inter-component event notification
       */

      /**
       * Person Deselected event handler
       *
       * @method onPersonDeselected
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onPersonDeselected: function DL_onPersonDeselected(layer, args)
      {
         var obj = args[1];
         // Should be person details in the arguments
         if (obj && (obj.userName !== null))
         {
            // Re-enable the add button
            this.userSelectButtons[obj.userName].set("disabled", false);
         }
      },


      /**
       * PRIVATE FUNCTIONS
       */
      
      /**
       * Resets the YUI DataTable errors to our custom messages
       * NOTE: Scope could be YAHOO.widget.DataTable, so can't use "this"
       *
       * @method _setDefaultDataTableErrors
       */
      _setDefaultDataTableErrors: function PeopleFinder__setDefaultDataTableErrors()
      {
         var msg = Alfresco.util.message;
         YAHOO.widget.DataTable.MSG_EMPTY = this._msg("message.empty");
         YAHOO.widget.DataTable.MSG_ERROR = this._msg("message.error");
      },
      
      /**
       * Updates people list by calling data webscript
       *
       * @method _performSearch
       * @param searchTerm {string} Search term from input field
       */
      _performSearch: function PeopleFinder__performSearch(searchTerm)
      {
         // Reset the custom error messages
         this._setDefaultDataTableErrors();
         
         // Don't display any message
         YAHOO.widget.DataTable.MSG_EMPTY = "";
         
         // Empty results table
         this.widgets.dataTable.deleteRows(0, this.widgets.dataTable.getRecordSet().getLength());
         
         var successHandler = function PeopleFinder__pS_successHandler(sRequest, oResponse, oPayload)
         {
            this._setDefaultDataTableErrors();
            this.widgets.dataTable.onDataReturnInitializeTable.call(this.widgets.dataTable, sRequest, oResponse, oPayload);
         }
         
         var failureHandler = function PeopleFinder__pS_failureHandler(sRequest, oResponse)
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
                  YAHOO.widget.DataTable.MSG_ERROR = response.message;
                  this.widgets.dataTable.showTableMessage(response.message, YAHOO.widget.DataTable.CLASS_ERROR);
               }
               catch(e)
               {
                  this._setDefaultDataTableErrors();
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
       * Build URI parameter string for doclist JSON data webscript
       *
       * @method _buildSearchParams
       * @param path {string} Path to query
       */
      _buildSearchParams: function PeopleFinder__buildSearchParams(searchTerm)
      {
         return "filter=" + encodeURIComponent(searchTerm);
      },
      
      /**
       * Gets a custom message
       *
       * @method _msg
       * @param messageId {string} The messageId to retrieve
       * @return {string} The custom message
       * @private
       */
      _msg: function PeopleFinder__msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, "Alfresco.PeopleFinder", Array.prototype.slice.call(arguments).slice(1));
      }
   };
})();
