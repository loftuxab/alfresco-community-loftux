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
      Alfresco.util.YUILoaderHelper.require(["button", "container", "datasource", "datatable", "json", "history"], this.onComponentsLoaded, this);
      
      /* Decoupled event listeners */
      YAHOO.Bubbling.on("viewUserClick", this.onViewUserClick, this);
      
      /* History navigation events */
      YAHOO.Bubbling.on("searchChanged", this.onSearchChanged, this);
      YAHOO.Bubbling.on("panelChanged", this.onPanelChanged, this);
      YAHOO.Bubbling.on("userIdChanged", this.onUserIdChanged, this);
      
      /* UI panels - keep up-to-date with top level div IDs */
      this.panels = ["search", "view"];
      
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
       * List of the available UI panels; such as Search, View, Edit etc.
       * 
       * @property panels
       * @type array
       */
      panels: [],
      
      /**
       * The current UI panel on display
       * 
       * @property currentPanel
       * @type string
       */
      currentPanel: "",
      
      /**
       * Current user id for an action.
       * 
       * @property currentUserId
       * @type string
       */
      currentUserId: "",
      
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
         // YUI History
         var bookmarkedSearch = YAHOO.util.History.getBookmarkedState("search") || "";
         var bookmarkedPanel = YAHOO.util.History.getBookmarkedState("panel") || "search";
         var bookmarkedUserId = YAHOO.util.History.getBookmarkedState("userid") || "";
         
         // init from the history
         // note that the current panel is initialised and stored later
         this.searchTerm = bookmarkedSearch;
         this.currentUserId = bookmarkedUserId;
         
         // Register History Manager callbacks
         YAHOO.util.History.register("panel", bookmarkedPanel, function CU_onHistoryManagerPanelChanged(newPanel)
         {
            YAHOO.Bubbling.fire("panelChanged",
            {
               panel: newPanel
            });
         });
         YAHOO.util.History.register("search", bookmarkedSearch, function CU_onHistoryManagerSearchChanged(newTerm)
         {
            YAHOO.Bubbling.fire("searchChanged",
            {
               search: newTerm
            });
         });
         YAHOO.util.History.register("userid", bookmarkedUserId, function CU_onHistoryManagerUserIdChanged(newUserId)
         {
            YAHOO.Bubbling.fire("userIdChanged",
            {
               userid: newUserId
            });
         });
         
         // Continue only when History Manager fires its onReady event
         YAHOO.util.History.onReady(this.onHistoryManagerReady, this, true);
         
         // Initialize the browser history management library
         try
         {
             YAHOO.util.History.initialize("yui-history-field", "yui-history-iframe");
         }
         catch(e)
         {
            /*
             * The only exception that gets thrown here is when the browser is
             * not supported (Opera, or not A-grade)
             */
            Alfresco.logger.debug("Alfresco.ConsoleUsers: Couldn't initialize HistoryManager.", e.toString());
            this.onHistoryManagerReady();
         }
      },
   
      /**
       * Fired by YUI when History Manager is initialised and available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onHistoryManagerReady
       */
      onHistoryManagerReady: function ConsoleUsers_onHistoryManagerReady()
      {
         var me = this;
         
         // Buttons
         this.widgets.searchButton = Alfresco.util.createYUIButton(this, "search-button", this.onSearchClick);
         this.widgets.newuserButton = Alfresco.util.createYUIButton(this, "newuser-button", this.onNewUserClick);
         this.widgets.deleteuserButton = Alfresco.util.createYUIButton(this, "deleteuser-button", this.onDeleteUserClick);
         this.widgets.edituserButton = Alfresco.util.createYUIButton(this, "edituser-button", this.onEditUserClick);
         
         // DataTable and DataSource setup
         this.widgets.dataSource = new YAHOO.util.DataSource(Alfresco.constants.PROXY_URI + "api/people");
         this.widgets.dataSource.responseType = YAHOO.util.DataSource.TYPE_JSON;
         this.widgets.dataSource.responseSchema =
         {
            resultsList: "people",
            fields:
            [
               "avatar", "userName", "firstName", "lastName", "jobtitle", "email", "quota", "sizeCurrent"
            ],
            metaFields:
            {
               recordOffset: "startIndex",
               totalRecords: "totalRecords"
            }
         };
         
         // Work to be performed after data has been queried but before display by the DataTable
         this.widgets.dataSource.doBeforeParseData = function PeopleFinder_doBeforeParseData(oRequest, oFullResponse)
         {
            var updatedResponse = oFullResponse;
            
            if (oFullResponse)
            {
               var items = oFullResponse.people;
               
               // remove GUEST(s)
               for (var i = 0; i < items.length; i++)
               {
                   if (items[i].userName == "guest" || items[i].userName.indexOf("guest&") == 0)
                   {
                      items.splice(i, 1);
                   }
               }
               
               // initial sort by username field
               items.sort(function(a, b)
               {
                  return (a.userName > b.userName);
               });
               
               // we need to wrap the array inside a JSON object so the DataTable gets the object it expects
               updatedResponse =
               {
                  "people": items
               };
            }
            
            // update Results Bar message with number of results found
            if (items.length < me.options.maxSearchResults)
            {
               me._setResultsMessage("message.results", $html(me.searchTerm), items.length);
            }
            else
            {
               me._setResultsMessage("message.maxresults", me.options.maxSearchResults);
            }
            
            return updatedResponse;
         }
         
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
         
         // display the initial panel based on history or default (which is "search")
         var bookmarkedPanel = YAHOO.util.History.getBookmarkedState("panel") || "search";
         YAHOO.Bubbling.fire("panelChanged",
         {
            panel: bookmarkedPanel
         });
         
         //
         // TODO: add panel handlers and this section can be removed... should happen in panelChanged?
         //
         if (bookmarkedPanel === "search" && this.searchTerm !== "")
         {
            YAHOO.Bubbling.fire("searchChanged",
            {
               search: this.searchTerm
            });
         }
         if (bookmarkedPanel === "view" && this.currentUserId !== "")
         {
            YAHOO.Bubbling.fire("userIdChanged",
            {
               userid: this.currentUserId
            });
         }
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
         
         /**
          * User avatar custom datacell formatter
          *
          * @method renderCellAvatar
          */
         var renderCellAvatar = function ConsoleUsers_renderCellAvatar(elCell, oRecord, oColumn, oData)
         {
            Dom.setStyle(elCell, "min-height", "64px");
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");
            Dom.setStyle(elCell.parentNode, "border-right", "1px solid #D7D7D7");
            
            var avatarUrl = Alfresco.constants.URL_CONTEXT + "components/images/no-user-photo-64.png";
            if (oRecord.getData("avatar") !== undefined)
            {
               avatarUrl = Alfresco.constants.PROXY_URI + oRecord.getData("avatar") + "?c=queue&ph=true";
            }
            
            elCell.innerHTML = '<img class="avatar" src="' + avatarUrl + '" alt="' + $html(oRecord.getData("userName")) + '" />';
         };
         
         /**
          * User full name custom datacell formatter
          *
          * @method renderCellFullName
          */
         var renderCellFullName = function ConsoleUsers_renderCellFullName(elCell, oRecord, oColumn, oData)
         {
            var firstName = oRecord.getData("firstName");
            var lastName = oRecord.getData("lastName");
            var name = firstName + ' ' + (lastName ? lastName : "");
            
            // fire the 'viewUserClick' event when the selected user in the list has changed
            elCell.innerHTML = "<a href='#' onclick=\"YAHOO.Bubbling.fire('viewUserClick', {username: '" + oRecord.getData("userName") + "'}); return false;\">" + $html(name) + "</a>";
         };
         
         /**
          * Quota custom datacell formatter
          *
          * @method renderCellQuota
          */
         var renderCellQuota = function ConsoleUsers_renderCellQuota(elCell, oRecord, oColumn, oData)
         {
            var quota = oRecord.getData("quota");
            var display = (quota > 0 ? Alfresco.util.formatFileSize(quota) : "");
            elCell.innerHTML = display;
         };
         
         /**
          * Usage custom datacell formatter
          *
          * @method renderCellUsage
          */
         var renderCellUsage = function ConsoleUsers_renderCellQuota(elCell, oRecord, oColumn, oData)
         {
            elCell.innerHTML = Alfresco.util.formatFileSize(oRecord.getData("sizeCurrent"));
         };
         
         /**
          * Generic HTML-safe custom datacell formatter
          */
         var renderCellSafeHTML = function ConsoleUsers_renderCellSafeHTML(elCell, oRecord, oColumn, oData)
         {
            elCell.innerHTML = $html(oData);
         };
         
         /**
          * Usage custom datacell sorter
          */
         var sortCellUsage = function ConsoleUsers_sortCellUsage(a, b, desc)
         {
            var numA = a.getData("sizeCurrent"),
                numB = b.getData("sizeCurrent");
            
            if (desc)
            {
               return (numA < numB ? 1 : (numA > numB ? -1 : 0));
            }
            return (numA < numB ? -1 : (numA > numB ? 1 : 0));
         };
         
         /**
          * Quota custom datacell sorter
          */
         var sortCellQuota = function ConsoleUsers_sortCellQuota(a, b, desc)
         {
            var numA = a.getData("quota"),
                numB = b.getData("quota");
            
            if (desc)
            {
               return (numA < numB ? 1 : (numA > numB ? -1 : 0));
            }
            return (numA < numB ? -1 : (numA > numB ? 1 : 0));
         };
         
         // DataTable column defintions
         var columnDefinitions =
         [
            { key: "avatar", label: "", sortable: false, formatter: renderCellAvatar, width: 70 },
            { key: "fullName", label: this._msg("label.name"), sortable: true, formatter: renderCellFullName },
            { key: "userName", label: this._msg("label.username"), sortable: true, formatter: renderCellSafeHTML },
            { key: "jobtitle", label: this._msg("label.jobtitle"), sortable: true, formatter: renderCellSafeHTML },
            { key: "email", label: this._msg("label.email"), sortable: true, formatter: renderCellSafeHTML },
            { key: "usage", label: this._msg("label.usage"), sortable: true, sortOptions: {sortFunction: sortCellUsage}, formatter: renderCellUsage },
            { key: "quota", label: this._msg("label.quota"), sortable: true, sortOptions: {sortFunction: sortCellQuota}, formatter: renderCellQuota }
         ];
         
         // DataTable definition
         this.widgets.dataTable = new YAHOO.widget.DataTable(this.id + "-datatable", columnDefinitions, this.widgets.dataSource,
         {
            initialLoad: false,
            renderLoopSize: 32,
            sortedBy:
            {
               key: "userName",
               dir: "asc"
            },
            MSG_EMPTY: this._msg("message.empty")
         });
      },

      /**
       * YUI WIDGET EVENT HANDLERS
       * Handlers for standard events fired from YUI widgets, e.g. "click"
       */
      
      /**
       * Panel changed history manager event handler
       *
       * @method onPanelChanged
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onPanelChanged: function ConsoleUsers_onPanelChanged(e, args)
      {
         var panel = args[1].panel;
         
         this._showPanel(panel);
      },
      
      /**
       * Search changed history manager event handler
       *
       * @method onSearchChanged
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onSearchChanged: function ConsoleUsers_onSearchChanged(e, args)
      {
         var searchTerm = args[1].search;
         
         // update the text field - as this event could come from bookmark, navigation or a search button click
         var searchTermElem = Dom.get(this.id + "-search-text");
         searchTermElem.value = searchTerm;
         
         this._performSearch(searchTerm);
      },
      
      /**
       * UserId changed history manager event handler
       *
       * @method onUserIdChanged
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onUserIdChanged: function ConsoleUsers_onUserIdChanged(e, args)
      {
         var userid = args[1].userid;
         
         // keep track of current user id
         this.currentUserId = userid;
         
         //
         // TODO: panel handlers!
         //
         if (this.currentPanel === "view")
         {
            var me = this;
            var success = function(res)
            {
               var fnSetter = function(id, val)
               {
                  Dom.get(me.id + id).innerHTML = val ? $html(val) : "";
               };
               
               var person = YAHOO.lang.JSON.parse(res.serverResponse.responseText);
               
               // apply avatar image URL
               var photos = Dom.getElementsByClassName("photoimg", "img");
               for (var i in photos)
               {
                  photos[i].src = person.avatar ?
                        Alfresco.constants.PROXY_URI + person.avatar + "?c=force" :
                        Alfresco.constants.URL_CONTEXT + "components/images/no-user-photo-64.png";
               }
               
               // About section fields
               var firstName = person.firstName;
               var lastName = person.lastName;
               var fullName = firstName + ' ' + (lastName ? lastName : "");
               fnSetter("-view-title", fullName);
               fnSetter("-view-name", fullName);
               fnSetter("-view-jobtitle", person.jobtitle);
               fnSetter("-view-organization", person.organization);
               // biography is a special html field
               var bio = person.persondescription ? person.persondescription : "";
               Dom.get(me.id + "-view-bio").innerHTML = Alfresco.util.stripUnsafeHTMLTags(bio).replace(/\n/g, "<br/>");
               
               // Contact section fields
               fnSetter("-view-location", person.location);
               fnSetter("-view-email", person.email);
               fnSetter("-view-telephone", person.telephone);
               fnSetter("-view-mobile", person.mobile);
               fnSetter("-view-skype", person.skype);
               fnSetter("-view-instantmsg", person.instantmsg);
               
               // Company section fields
               fnSetter("-view-companyname", person.organization);
               // build the company address up and set manually - encoding each value
               var addr = "";
               addr += person.companyaddress1 ? ($html(person.companyaddress1) + "<br/>") : "";
               addr += person.companyaddress2 ? ($html(person.companyaddress2) + "<br/>") : "";
               addr += person.companyaddress3 ? ($html(person.companyaddress3) + "<br/>") : "";
               addr += person.companypostcode ? ($html(person.companypostcode) + "<br/>") : "";
               Dom.get(me.id + "-view-companyaddress").innerHTML = addr;
               fnSetter("-view-companytelephone", person.companytelephone);
               fnSetter("-view-companyfax", person.companyfax);
               fnSetter("-view-companyemail", person.companyemail);
               
               // More section fields
               fnSetter("-view-username", userid);
               fnSetter("-view-enabled", person.enabled ? me._msg("label.enabled") : me._msg("label.disabled"));
               fnSetter("-view-quota", (person.quota > 0 ? Alfresco.util.formatFileSize(person.quota) : ""));
               fnSetter("-view-usage", Alfresco.util.formatFileSize(person.sizeCurrent));
               fnSetter("-view-groups", person.groups.join(", "));
            };
            
            // make an ajax call to get user details
            Alfresco.util.Ajax.request(
            {
               url: Alfresco.constants.PROXY_URI + "api/people/" + encodeURIComponent(userid) + "?groups=true",
               method: Alfresco.util.Ajax.GET,
               successCallback:
               {
                  fn: success,
                  scope: this
               },
               failureMessage: this._msg("message.getuser-failure", userid)   
            });
         }
      },
      
      /**
       * Search button click event handler
       *
       * @method onSearchClick
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onSearchClick: function ConsoleUsers_onSearchClick(e, args)
      {
         var searchTermElem = Dom.get(this.id + "-search-text");
         var searchTerm = searchTermElem.value;
         
         // inform the user if the search term entered is too small
         if (searchTerm.length < this.options.minSearchTermLength)
         {
            Alfresco.util.PopupManager.displayMessage(
            {
               text: this._msg("message.minimum-length", this.options.minSearchTermLength)
            });
            return;
         }
         
         YAHOO.util.History.multiNavigate({panel: "search", "search": searchTerm});
      },
      
      /**
       * New User button click event handler
       *
       * @method onNewUserClick
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onNewUserClick: function ConsoleUsers_onNewUserClick(e, args)
      {
         
      },
      
      /**
       * Delete User button click event handler
       *
       * @method onDeleteUserClick
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onDeleteUserClick: function ConsoleUsers_onDeleteUserClick(e, args)
      {
         
      },
      
      /**
       * Edit User button click event handler
       *
       * @method onEditUserClick
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onEditUserClick: function ConsoleUsers_onEditUserClick(e, args)
      {
         
      },
      
      /**
       * View User event handler
       *
       * @method onViewUserClick
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onViewUserClick: function ConsoleUsers_onViewUserClick(e, args)
      {
         var userid = args[1].username;
         
         YAHOO.util.History.multiNavigate({panel: "view", userid: userid});
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
       * @private
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
       * @private
       */
      _performSearch: function ConsoleUsers__performSearch(searchTerm)
      {
         // keep track of the last search performed
         this.searchTerm = searchTerm;
         
         // check search length again as we may have got here via history navigation
         if (searchTerm.length >= this.options.minSearchTermLength)
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
                     this._setResultsMessage("message.noresults");
                  }
                  catch(e)
                  {
                     this._setDefaultDataTableErrors(this.widgets.dataTable);
                  }
               }
            }
            
            this.widgets.dataSource.sendRequest(this._buildSearchParams(searchTerm),
            {
               success: successHandler,
               failure: failureHandler,
               scope: this
            });
         }
      },

      /**
       * Build URI parameters for People List JSON data webscript
       *
       * @method _buildSearchParams
       * @param searchTerm {string} User search term
       * @private
       */
      _buildSearchParams: function ConsoleUsers__buildSearchParams(searchTerm)
      {
         return "?filter=" + encodeURIComponent(searchTerm) + "&maxResults=" + this.options.maxSearchResults;
      },
      
      /**
       * Set the message in the Results Bar area
       * 
       * @method _setResultsMessage
       * @param messageId {string} The messageId to display
       * @private
       */
      _setResultsMessage: function ConsoleUsers__setResultsMessage(messageId, arg1, arg2)
      {
         var resultsDiv = Dom.get(this.id + "-search-bar");
         resultsDiv.innerHTML = this._msg(messageId, arg1, arg2);
      },
      
      /**
       * Make the specified panel visible - hiding any others
       * 
       * @method _showPanel
       * @param panel {string} The panel to make visible
       * @private
       */
      _showPanel: function ConsoleUsers__showPanel(panel)
      {
         if (this.currentPanel !== panel)
         {
            this.currentPanel = panel;
            for (var index in this.panels)
            {
               var _panel = this.panels[index];
               if (_panel !== panel)
               {
                  Dom.setStyle(this.id + "-" + _panel, "display", "none");
               }
            }
            Dom.setStyle(this.id + "-" + panel, "display", "block");
         }
         
         //
         // TODO: add panel handlers and this can be removed
         //
         if ("search" === panel)
         {
            Dom.get(this.id + "-search-text").focus();
         }
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