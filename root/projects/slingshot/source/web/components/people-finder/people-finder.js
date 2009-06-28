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
      
      // Initialise prototype properties
      this.widgets = {};
      this.userSelectButtons = {};
      this.searchTerm = "";
      this.singleSelectedUser = "";
      this.selectedUsers = {};

      // Register this component
      Alfresco.util.ComponentManager.register(this);

      // Load YUI Components
      Alfresco.util.YUILoaderHelper.require(["button", "container", "datasource", "datatable", "json"], this.onComponentsLoaded, this);
   
      /**
       * Decoupled event listeners
       */
      YAHOO.Bubbling.on("personDeselected", this.onPersonDeselected, this);

      return this;
   };
   
   YAHOO.lang.augmentObject(Alfresco.PeopleFinder,
   {
      VIEW_MODE_DEFAULT: "",
      VIEW_MODE_COMPACT: "COMPACT",
      VIEW_MODE_FULLPAGE: "FULLPAGE"
   });
   
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
         siteId: "",

         /**
          * View mode
          * 
          * @property viewMode
          * @type string
          * @default Alfresco.PeopleFinder.VIEW_MODE_DEFAULT
          */
         viewMode: Alfresco.PeopleFinder.VIEW_MODE_DEFAULT,

         /**
          * Single Select mode flag
          * 
          * @property singleSelectMode
          * @type boolean
          * @default false
          */
         singleSelectMode: false,
         
         /**
          * Whether we show the current user or not flag
          * 
          * @property showSelf
          * @type boolean
          * @default false
          */
         showSelf: false,
         
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
         maxSearchResults: 100,

         /**
          * Whether to set UI focus to this component or not
          * 
          * @property setFocus
          * @type boolean
          * @default false
          */
         setFocus: false,

         /**
          * Suffix to add button label.
          *
          * @property addButtonSuffix
          * @type string
          */
         addButtonSuffix: ""
      },

      /**
       * Object container for storing YUI widget instances.
       * 
       * @property widgets
       * @type object
       */
      widgets: null,
      
      /**
       * Object container for storing YUI button instances, indexed by username.
       * 
       * @property userSelectButtons
       * @type object
       */
      userSelectButtons: null,
      
      /**
       * Current search term, obtained from form input field.
       * 
       * @property searchTerm
       * @type string
       */
      searchTerm: null,
      
      /**
       * Single selected user, for when in single select mode
       * 
       * @property singleSelectedUser
       * @type string
       */
      singleSelectedUser: null,

      /**
       * Selected users. Keeps a list of selected users for correct Add button state.
       * 
       * @property selectedUsers
       * @type object
       */
      selectedUsers: null,

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
         
         // View mode
         if (this.options.viewMode == Alfresco.PeopleFinder.VIEW_MODE_COMPACT)
         {
            Dom.addClass(this.id + "-body", "compact");
         }
         else if (this.options.viewMode == Alfresco.PeopleFinder.VIEW_MODE_FULLPAGE)
         {
            Dom.setStyle(this.id + "-results", "height", "auto");
         }
         else
         {
            Dom.setStyle(this.id + "-results", "height", "300px");
         }
         
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
             fields: ["userName", "avatar", "firstName", "lastName", "organisation", "jobtitle", "email"]
         };

         this.widgets.dataSource.doBeforeParseData = function PeopleFinder_doBeforeParseData(oRequest, oFullResponse)
         {
            var updatedResponse = oFullResponse;
            
            if (oFullResponse)
            {
               var items = oFullResponse.people;

               // crop item list to max length if required
               if (items.length > me.options.maxSearchResults)
               {
                  items = items.slice(0, me.options.maxSearchResults-1);
               }

               // Remove the current user form the list?
               if (!me.options.showSelf)
               {
                  for (var i = 0; i < items.length; i++)
                  {
                      if (items[i].userName == Alfresco.constants.USERNAME)
                      {
                         items.splice(i, 1);
                         break;
                      }
                  }
               }

               // Sort the user list by name
               items.sort(function (user1, user2){
                  var name1 = user1.firstName + user1.lastName,
                     name2 = user2.firstName + user2.lastName;
                  return (name1 > name2) ? 1 : (name1 < name2) ? -1 : 0;
               });

               // we need to wrap the array inside a JSON object so the DataTable is happy
               updatedResponse =
               {
                  people: items
               };
            }
            
            return updatedResponse;
         };
         
         // Setup the DataTable
         this._setupDataTable();
         
         // register the "enter" event on the search text field
         var searchText = Dom.get(this.id + "-search-text");
         
         // declare variable to keep JSLint and YUI Compressor happy
         var enterListener = new YAHOO.util.KeyListener(searchText,
         {
            keys: YAHOO.util.KeyListener.KEY.ENTER
         },
         {
            fn: function(eventName, event, obj)
            {
               me.onSearchClick();
               Event.stopEvent(event[1]);
               return false;
            },
            scope: this,
            correctScope: true
         }, YAHOO.env.ua.ie > 0 ? YAHOO.util.KeyListener.KEYDOWN : "keypress");
         enterListener.enable();
         
         // Set initial focus?
         if (this.options.setFocus)
         {
            searchText.focus();
         }
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

            elCell.innerHTML = '<img class="avatar" src="' + avatarUrl + '" alt="avatar" />';
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
            var userName = oRecord.getData("userName"),
               name = userName,
               firstName = oRecord.getData("firstName"),
               lastName = oRecord.getData("lastName");
            
            if ((firstName !== undefined) || (lastName !== undefined))
            {
               name = firstName ? firstName + " " : "";
               name += lastName ? lastName : "";
            }
            
            var title = oRecord.getData("jobtitle") ? oRecord.getData("jobtitle") : "",
               organisation = oRecord.getData("organisation") ? oRecord.getData("organisation") : "";
            
            var profileUrl = Alfresco.util.uriTemplate("userprofilepage",
            {
               userid: userName
            });
            var desc = '<h3 class="itemname"><a href=' + encodeURI(profileUrl) + ' class="theme-color-1">' + $html(name) + '</a> <span class="lighter">(' + $html(userName) + ')</span></h3>';
            if (title.length !== 0)
            {
               if (me.options.viewMode == Alfresco.PeopleFinder.VIEW_MODE_COMPACT)
               {
                  desc += '<div class="detail">' + $html(title) + '</div>';
               }
               else
               {
                  desc += '<div class="detail"><span>' + me._msg("label.title") + ":</span> " + $html(title) + '</div>';
               }
            }
            if (organisation.length !== 0)
            {
               if (me.options.viewMode == Alfresco.PeopleFinder.VIEW_MODE_COMPACT)
               {
                  desc += '<div class="detail">&nbsp;(' + $html(organisation) + ')</div>';
               }
               else
               {
                  desc += '<div class="detail"><span>' + me._msg("label.company") + ":</span> " + $html(organisation) + '</div>';
               }
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
         var renderCellAddButton = function PeopleFinder_renderCellAddButton(elCell, oRecord, oColumn, oData)
         {
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");
            Dom.setStyle(elCell.parentNode, "text-align", "right");
            
            var userName = oRecord.getData("userName");
            var desc = '<span id="' + me.id + '-select-' + userName + '"></span>';
            elCell.innerHTML = desc;
            
            // create button if require - it is not required in the plain people list mode
            if (me.options.viewMode !== Alfresco.PeopleFinder.VIEW_MODE_FULLPAGE)
            {
               var button = new YAHOO.widget.Button(
               {
                  type: "button",
                  label: me._msg("button.add") + me.options.addButtonSuffix,
                  name: me.id + "-selectbutton-" + userName,
                  container: me.id + '-select-' + userName,
                  onclick:
                  {
                     fn: me.onPersonSelect,
                     obj: oRecord,
                     scope: me
                  }
               });
               me.userSelectButtons[userName] = button;
               
               if ((userName in me.selectedUsers) || (me.options.singleSelectMode && me.singleSelectedUser !== ""))
               {
                  me.userSelectButtons[userName].set("disabled", true);
               }
            }
         };

         // DataTable column defintions
         var columnDefinitions = [
         {
            key: "avatar", label: "Avatar", sortable: false, formatter: renderCellAvatar, width: this.options.viewMode == Alfresco.PeopleFinder.VIEW_MODE_COMPACT ? 36 : 70
         },
         {
            key: "person", label: "Description", sortable: false, formatter: renderCellDescription
         },
         {
            key: "actions", label: "Actions", sortable: false, formatter: renderCellAddButton, width: 80
         }];

         // DataTable definition
         this.widgets.dataTable = new YAHOO.widget.DataTable(this.id + "-results", columnDefinitions, this.widgets.dataSource,
         {
            renderLoopSize: 32,
            initialLoad: false,
            MSG_EMPTY: this._msg("message.instructions")
         });

         this.widgets.dataTable.doBeforeLoadData = function PeopleFinder_doBeforeLoadData(sRequest, oResponse, oPayload)
         {
            if (oResponse.results)
            {
               this.renderLoopSize = oResponse.results.length >> (YAHOO.env.ua.gecko === 1.8) ? 3 : 5;
            }
            return true;
         };

         // Enable row highlighting
         this.widgets.dataTable.subscribe("rowMouseoverEvent", this.widgets.dataTable.onEventHighlightRow);
         this.widgets.dataTable.subscribe("rowMouseoutEvent", this.widgets.dataTable.onEventUnhighlightRow);
      },
      
      /**
       * Public function to clear the results DataTable
       */
      clearResults: function PeopleFinder_clearResults()
      {
         // Clear results DataTable
         if (this.widgets.dataTable)
         {
            var recordCount = this.widgets.dataTable.getRecordSet().getLength();
            this.widgets.dataTable.deleteRows(0, recordCount);
         }
         Dom.get(this.id + "-search-text").value = "";
         this.singleSelectedUser = "";
         this.selectedUsers = {};
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
         
         // Add the userName to the selectedUsers object
         this.selectedUsers[userName] = true;
         this.singleSelectedUser = userName;
         
         // Disable the add button(s)
         if (this.options.singleSelectMode)
         {
            for (var button in this.userSelectButtons)
            {
               if (this.userSelectButtons.hasOwnProperty(button))
               {
                  this.userSelectButtons[button].set("disabled", true);
               }
            }
         }
         else
         {
            this.userSelectButtons[userName].set("disabled", true);
         }
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
         var searchTerm = Dom.get(this.id + "-search-text").value;
         if (searchTerm.length < this.options.minSearchTermLength)
         {
            Alfresco.util.PopupManager.displayMessage(
            {
               text: this._msg("message.minimum-length", this.options.minSearchTermLength)
            });
            return;
         }
         
         this.userSelectButtons = {};
         
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
            delete this.selectedUsers[obj.userName];
            this.singleSelectedUser = "";
            // Re-enable the add button(s)
            if (this.options.singleSelectMode)
            {
               for (var button in this.userSelectButtons)
               {
                  if (this.userSelectButtons.hasOwnProperty(button))
                  {
                     this.userSelectButtons[button].set("disabled", false);
                  }
               }
            }
            else
            {
               this.userSelectButtons[obj.userName].set("disabled", false);
            }
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
       * @param dataTable {object} Instance of the DataTable
       */
      _setDefaultDataTableErrors: function PeopleFinder__setDefaultDataTableErrors(dataTable)
      {
         var msg = Alfresco.util.message;
         dataTable.set("MSG_EMPTY", msg("message.empty", "Alfresco.PeopleFinder"));
         dataTable.set("MSG_ERROR", msg("message.error", "Alfresco.PeopleFinder"));
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
         this._setDefaultDataTableErrors(this.widgets.dataTable);
         
         // Don't display any message
         this.widgets.dataTable.set("MSG_EMPTY", this._msg("message.searching"));
         
         // Empty results table
         this.widgets.dataTable.deleteRows(0, this.widgets.dataTable.getRecordSet().getLength());
         
         var successHandler = function PeopleFinder__pS_successHandler(sRequest, oResponse, oPayload)
         {
            this._setDefaultDataTableErrors(this.widgets.dataTable);
            this.widgets.dataTable.onDataReturnInitializeTable.call(this.widgets.dataTable, sRequest, oResponse, oPayload);
         };
         
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
                  this.widgets.dataTable.set("MSG_ERROR", response.message);
                  this.widgets.dataTable.showTableMessage(response.message, YAHOO.widget.DataTable.CLASS_ERROR);
               }
               catch(e)
               {
                  this._setDefaultDataTableErrors(this.widgets.dataTable);
               }
            }
         };
         
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
      _buildSearchParams: function PeopleFinder__buildSearchParams(searchTerm)
      {
         return "filter=" + encodeURIComponent(searchTerm) + "&maxResults=" + this.options.maxSearchResults;
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