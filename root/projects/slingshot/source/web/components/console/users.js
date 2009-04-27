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
      YAHOO.Bubbling.on("panelChanged", this.onPanelChanged, this);
      YAHOO.Bubbling.on("searchChanged", this.onSearchChanged, this);
      YAHOO.Bubbling.on("userIdChanged", this.onUserIdChanged, this);
      
      /* Define panel handlers */
      var parent = this;
      Alfresco.ConsolePanelHandler = function(id)
      {
         this.id = id;
         
         // register the panel with the parent object
         parent.panels.push(this);
      };
      
      /** Alfresco.ConsolePanelHandler prototype */
      Alfresco.ConsolePanelHandler.prototype =
      {
         id : null,
         
         /**
          * Event handler - called once only when panel first initialised
          * @method onLoad
          */
         onLoad: function onLoad()
         {
         },
         
         /**
          * Event handler - called once if bookmarked history state is found
          * @method onHistoryInit
          */
         onHistoryInit: function onHistoryInit()
         {
         },
         
         /**
          * Event handler - called just before panel is going to be made visible
          * @method onBeforeShow
          */
         onBeforeShow: function onBeforeShow()
         {
         },
         
         /**
          * Event handler - called after the panel has been made visible
          * @method onShow
          */
         onShow: function onShow()
         {
         },
         
         /**
          * Event handler - called to request the panel update it's current state
          * @method onUpdate
          */
         onUpdate: function onUpdate()
         {
         },
         
         /**
          * Event handler - called after the panel has been made invisible
          * @method onHide
          */
         onHide: function onHide()
         {
         }
      };
      
      // NOTE: the panel registered first is considered the "default" view and is displayed first
      
      /* Search Panel Handler */
      SearchPanelHandler = function SearchPanelHandler_constructor()
      {
         SearchPanelHandler.superclass.constructor.call(this, "search");
      };
      
      YAHOO.extend(SearchPanelHandler, Alfresco.ConsolePanelHandler,
      {
         onLoad: function onLoad()
         {
            // Buttons
            parent.widgets.searchButton = Alfresco.util.createYUIButton(parent, "search-button", parent.onSearchClick);
            parent.widgets.newuserButton = Alfresco.util.createYUIButton(parent, "newuser-button", parent.onNewUserClick);
            
            // DataTable and DataSource setup
            parent.widgets.dataSource = new YAHOO.util.DataSource(Alfresco.constants.PROXY_URI + "api/people");
            parent.widgets.dataSource.responseType = YAHOO.util.DataSource.TYPE_JSON;
            parent.widgets.dataSource.responseSchema =
            {
               resultsList: "people",
               fields:
               [
                  "avatar", "userName", "enabled", "firstName", "lastName", "jobtitle", "email", "quota", "sizeCurrent"
               ],
               metaFields:
               {
                  recordOffset: "startIndex",
                  totalRecords: "totalRecords"
               }
            };
            
            var me = this;
            
            // Work to be performed after data has been queried but before display by the DataTable
            parent.widgets.dataSource.doBeforeParseData = function PeopleFinder_doBeforeParseData(oRequest, oFullResponse)
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
               if (items.length < parent.options.maxSearchResults)
               {
                  me._setResultsMessage("message.results", $html(parent.searchTerm), items.length);
               }
               else
               {
                  me._setResultsMessage("message.maxresults", parent.options.maxSearchResults);
               }
               
               return updatedResponse;
            }
            
            // Setup the main datatable
            this._setupDataTable();
            
            // register the "enter" event on the search text field
            var searchText = Dom.get(parent.id + "-search-text");
            
            new YAHOO.util.KeyListener(searchText,
            {
               keys: YAHOO.util.KeyListener.KEY.ENTER
            },
            {
               fn: function() 
               {
                  parent.onSearchClick();
               },
               scope: parent,
               correctScope: true
            }, "keydown").enable();
         },
         
         onHistoryInit: function onHistoryInit()
         {
            var bookmarkedSearch = YAHOO.util.History.getBookmarkedState("search") || null;
            if (bookmarkedSearch !== null)
            {
               YAHOO.Bubbling.fire("searchChanged",
               {
                  search: bookmarkedSearch
               });
            }
         },
         
         onShow: function onShow()
         {
            Dom.get(parent.id + "-search-text").focus();
         },
         
         onUpdate: function onUpdate()
         {
            // update the text field - as this event could come from bookmark, navigation or a search button click
            var searchTermElem = Dom.get(parent.id + "-search-text");
            searchTermElem.value = parent.searchTerm;
            
            // check search length again as we may have got here via history navigation
            if (parent.searchTerm.length >= parent.options.minSearchTermLength)
            {
               var me = this;
               
               // Reset the custom error messages
               me._setDefaultDataTableErrors(parent.widgets.dataTable);
               
               // Don't display any message
               parent.widgets.dataTable.set("MSG_EMPTY", parent._msg("message.searching"));
               
               // Empty results table
               parent.widgets.dataTable.deleteRows(0, parent.widgets.dataTable.getRecordSet().getLength());
               
               var successHandler = function ConsoleUsers__ps_successHandler(sRequest, oResponse, oPayload)
               {
                  me._setDefaultDataTableErrors(parent.widgets.dataTable);
                  parent.widgets.dataTable.onDataReturnInitializeTable.call(parent.widgets.dataTable, sRequest, oResponse, oPayload);
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
                        parent.widgets.dataTable.set("MSG_ERROR", response.message);
                        parent.widgets.dataTable.showTableMessage(response.message, YAHOO.widget.DataTable.CLASS_ERROR);
                        me._setResultsMessage("message.noresults");
                     }
                     catch(e)
                     {
                        me._setDefaultDataTableErrors(parent.widgets.dataTable);
                     }
                  }
               }
               
               parent.widgets.dataSource.sendRequest(me._buildSearchParams(parent.searchTerm),
               {
                  success: successHandler,
                  failure: failureHandler,
                  scope: parent
               });
            }
         },
         
         /**
          * Setup the YUI DataTable with custom renderers.
          *
          * @method _setupDataTable
          * @private
          */
         _setupDataTable: function _setupDataTable()
         {
            /**
             * DataTable Cell Renderers
             *
             * Each cell has a custom renderer defined as a custom function. See YUI documentation for details.
             * These MUST be inline in order to have access to the parent instance (via the "parent" variable).
             */
            
            /**
             * User avatar custom datacell formatter
             *
             * @method renderCellAvatar
             */
            var renderCellAvatar = function renderCellAvatar(elCell, oRecord, oColumn, oData)
            {
               Dom.setStyle(elCell, "min-height", "64px");
               Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");
               Dom.setStyle(elCell.parentNode, "border-right", "1px solid #D7D7D7");
               
               // apply the avatar image as a background
               var avatarUrl = Alfresco.constants.URL_CONTEXT + "components/images/no-user-photo-64.png";
               if (oRecord.getData("avatar") !== undefined)
               {
                  avatarUrl = Alfresco.constants.PROXY_URI + oRecord.getData("avatar") + "?c=queue&ph=true";
               }
               Dom.setStyle(elCell, "background-image", "url('" + avatarUrl + "')");
               Dom.setStyle(elCell, "background-repeat", "no-repeat");
               Dom.setStyle(elCell, "background-position", "22px 50%");
               
               // overlay the account enabled/disabled indicator image
               var enabled = (oRecord.getData("enabled") ? 'enabled' : 'disabled');
               elCell.innerHTML = '<img class="indicator" alt="' + parent._msg("label." + enabled) + '" src="' + Alfresco.constants.URL_CONTEXT +
                     'components/console/images/account_' + enabled + '.gif" alt="" />';
            };
            
            /**
             * User full name custom datacell formatter
             *
             * @method renderCellFullName
             */
            var renderCellFullName = function renderCellFullName(elCell, oRecord, oColumn, oData)
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
            var renderCellQuota = function renderCellQuota(elCell, oRecord, oColumn, oData)
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
            var renderCellUsage = function renderCellQuota(elCell, oRecord, oColumn, oData)
            {
               elCell.innerHTML = Alfresco.util.formatFileSize(oRecord.getData("sizeCurrent"));
            };
            
            /**
             * Generic HTML-safe custom datacell formatter
             */
            var renderCellSafeHTML = function renderCellSafeHTML(elCell, oRecord, oColumn, oData)
            {
               elCell.innerHTML = $html(oData);
            };
            
            /**
             * Usage custom datacell sorter
             */
            var sortCellUsage = function sortCellUsage(a, b, desc)
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
            var sortCellQuota = function sortCellQuota(a, b, desc)
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
               { key: "fullName", label: parent._msg("label.name"), sortable: true, formatter: renderCellFullName },
               { key: "userName", label: parent._msg("label.username"), sortable: true, formatter: renderCellSafeHTML },
               { key: "jobtitle", label: parent._msg("label.jobtitle"), sortable: true, formatter: renderCellSafeHTML },
               { key: "email", label: parent._msg("label.email"), sortable: true, formatter: renderCellSafeHTML },
               { key: "usage", label: parent._msg("label.usage"), sortable: true, sortOptions: {sortFunction: sortCellUsage}, formatter: renderCellUsage },
               { key: "quota", label: parent._msg("label.quota"), sortable: true, sortOptions: {sortFunction: sortCellQuota}, formatter: renderCellQuota }
            ];
            
            // DataTable definition
            parent.widgets.dataTable = new YAHOO.widget.DataTable(parent.id + "-datatable", columnDefinitions, parent.widgets.dataSource,
            {
               initialLoad: false,
               renderLoopSize: 32,
               sortedBy:
               {
                  key: "userName",
                  dir: "asc"
               },
               MSG_EMPTY: parent._msg("message.empty")
            });
         },
         
         /**
          * Resets the YUI DataTable errors to our custom messages
          * NOTE: Scope could be YAHOO.widget.DataTable, so can't use "this"
          *
          * @method _setDefaultDataTableErrors
          * @param dataTable {object} Instance of the DataTable
          * @private
          */
         _setDefaultDataTableErrors: function _setDefaultDataTableErrors(dataTable)
         {
            var msg = Alfresco.util.message;
            dataTable.set("MSG_EMPTY", parent._msg("message.empty", "Alfresco.ConsoleUsers"));
            dataTable.set("MSG_ERROR", parent._msg("message.error", "Alfresco.ConsoleUsers"));
         },
         
         /**
          * Build URI parameters for People List JSON data webscript
          *
          * @method _buildSearchParams
          * @param searchTerm {string} User search term
          * @private
          */
         _buildSearchParams: function _buildSearchParams(searchTerm)
         {
            return "?filter=" + encodeURIComponent(searchTerm) + "&maxResults=" + parent.options.maxSearchResults;
         },
         
         /**
          * Set the message in the Results Bar area
          * 
          * @method _setResultsMessage
          * @param messageId {string} The messageId to display
          * @private
          */
         _setResultsMessage: function _setResultsMessage(messageId, arg1, arg2)
         {
            var resultsDiv = Dom.get(parent.id + "-search-bar");
            resultsDiv.innerHTML = parent._msg(messageId, arg1, arg2);
         }
      });
      new SearchPanelHandler();
      
      /* View Panel Handler */
      ViewPanelHandler = function ViewPanelHandler_constructor()
      {
         ViewPanelHandler.superclass.constructor.call(this, "view");
      };
      
      YAHOO.extend(ViewPanelHandler, Alfresco.ConsolePanelHandler,
      {
         onLoad: function onLoad()
         {
            // Buttons
            parent.widgets.deleteuserButton = Alfresco.util.createYUIButton(parent, "deleteuser-button", parent.onDeleteUserClick);
            parent.widgets.edituserButton = Alfresco.util.createYUIButton(parent, "edituser-button", parent.onEditUserClick);
         },
         
         onHistoryInit: function onHistoryInit()
         {
            var bookmarkedUserId = YAHOO.util.History.getBookmarkedState("userid") || null;
            if (bookmarkedUserId !== null)
            {
               YAHOO.Bubbling.fire("userIdChanged",
               {
                  userid: bookmarkedUserId
               });
            }
         },
         
         onBeforeShow: function onBeforeShow()
         {
            // Hide the main panel area before it is displayed - so we don't show
            // old data to the user before the Update() method paints the results
            Dom.get(parent.id + "-view-title").innerHTML = "";
            Dom.setStyle(parent.id + "-view-main", "visibility", "hidden");
         },
         
         onShow: function onShow()
         {
            window.scrollTo(0, 0);
         },
         
         onUpdate: function onUpdate()
         {
            var success = function(res)
            {
               var fnSetter = function(id, val)
               {
                  Dom.get(parent.id + id).innerHTML = val ? $html(val) : "";
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
               Dom.get(parent.id + "-view-bio").innerHTML = Alfresco.util.stripUnsafeHTMLTags(bio).replace(/\n/g, "<br/>");
               
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
               Dom.get(parent.id + "-view-companyaddress").innerHTML = addr;
               fnSetter("-view-companytelephone", person.companytelephone);
               fnSetter("-view-companyfax", person.companyfax);
               fnSetter("-view-companyemail", person.companyemail);
               
               // More section fields
               fnSetter("-view-username", parent.currentUserId);
               fnSetter("-view-enabled", person.enabled ? parent._msg("label.enabled") : parent._msg("label.disabled"));
               fnSetter("-view-quota", (person.quota > 0 ? Alfresco.util.formatFileSize(person.quota) : ""));
               fnSetter("-view-usage", Alfresco.util.formatFileSize(person.sizeCurrent));
               fnSetter("-view-groups", person.groups.join(", "));
               
               // Make main panel area visible
               Dom.setStyle(parent.id + "-view-main", "visibility", "visible");
            };
            
            // make an ajax call to get user details
            Alfresco.util.Ajax.request(
            {
               url: Alfresco.constants.PROXY_URI + "api/people/" + encodeURIComponent(parent.currentUserId) + "?groups=true",
               method: Alfresco.util.Ajax.GET,
               successCallback:
               {
                  fn: success,
                  scope: parent
               },
               failureMessage: parent._msg("message.getuser-failure", parent.currentUserId)   
            });
         }
      });
      new ViewPanelHandler();
      
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
       * Object container for storing YUI pop dialog instances.
       * 
       * @property popups
       * @type object
       */
      popups: {},
      
      /**
       * List of the available UI panel handler objects; such as Search, View, Edit etc.
       * 
       * @property panels
       * @type array
       */
      panels: [],
      
      /**
       * The current UI panel ID on display
       * 
       * @property currentPanelId
       * @type string
       */
      currentPanelId: "",
      
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
         // Generate the popup dialog for confirmation of deleting a user
         this.popups.deleteDialog = new YAHOO.widget.SimpleDialog("deleteDialog", 
         {
            width: "36em",
            fixedcenter: true,
            visible: false,
            draggable: false,
            modal: true,
            close: true,
            text: '<div class="yui-u" style="text-align:center"><br/>' + this._msg("panel.delete.msg") + '<br/><br/></div>',
            constraintoviewport: true,
            buttons: [
            {
               text: this._msg("button.delete"),
               handler:
               {
                  fn: this.onDeleteUserOK,
                  scope: this
               }
            },
            {
               text: this._msg("button.cancel"),
               handler:
               {
                  fn: this.onDeleteUserCancel,
                  scope: this
               },
               isDefault: true
            }]
         });
         
         this.popups.deleteDialog.setHeader(this._msg("panel.delete.header"));
         this.popups.deleteDialog.render(document.body);
         
         // YUI History
         var bookmarkedSearch = YAHOO.util.History.getBookmarkedState("search") || "";
         var bookmarkedPanel = YAHOO.util.History.getBookmarkedState("panel") || this.panels[0].id;
         var bookmarkedUserId = YAHOO.util.History.getBookmarkedState("userid") || "";
         
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
         // Fire the onLoad() panel lifecycle event for each registered panel
         // To perform one-off setup of contained widgets and internal event handlers
         for (var i in this.panels)
         {
            this.panels[i].onLoad();
         }
         
         // display the initial panel based on history or default
         var bookmarkedPanel = YAHOO.util.History.getBookmarkedState("panel") || this.panels[0].id;
         YAHOO.Bubbling.fire("panelChanged",
         {
            panel: bookmarkedPanel
         });
         
         // Fire the onHistoryInit() panel lifecycle event for the panel found in the history state
         // - handles initial display based on any stored bookmark state
         for (var i in this.panels)
         {
            if (this.panels[i].id === bookmarkedPanel)
            {
               this.panels[i].onHistoryInit();
               break;
            }
         }
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
         // keep track of the last search performed
         var searchTerm = args[1].search;
         this.searchTerm = searchTerm;
         
         // annoyingly we need to check the panel ID so we don't update multiple times
         if (this.currentPanelId === "search")
         {
            this._updateCurrentPanel();
         }
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
         this.currentUserId = userid;
         
         // annoyingly we need to check the panel ID so we don't update multiple times
         if (this.currentPanelId === "view")
         {
            this._updateCurrentPanel();
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
       * Delete User button click event handler
       *
       * @method onDeleteUserClick
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onDeleteUserClick: function ConsoleUsers_onDeleteUserClick(e, args)
      {
         this.popups.deleteDialog.show();
      },
      
      /**
       * Fired when the admin confirms that they want to delete a User.
       *
       * @method onDeleteUserOK
       * @param e {object} DomEvent
       */
      onDeleteUserOK: function ConsoleUsers_onDeleteUserOK(e)
      {
         Alfresco.util.Ajax.request(
         {
            method: Alfresco.util.Ajax.DELETE,
            url: Alfresco.constants.PROXY_URI + "api/people/" + encodeURIComponent(this.currentUserId),
            successCallback:
            {
               fn: this.onDeletedUser,
               scope: this
            },
            failureMessage: this._msg("panel.delete.fail")
         });
      },
      
      /**
       * Fired on successful deletion of a user.
       *
       * @method onDeletedUser
       * @param e {object} DomEvent
       */
      onDeletedUser: function ConsoleUsers_onDeletedUser(e)
      {
         // return to the search screen - we can no longer view the user details
         this.popups.deleteDialog.hide();
         YAHOO.util.History.multiNavigate({panel: "search", "search": this.searchTerm});
      },
      
      /**
       * Fired when the admin cancels the operation to delete a User.
       *
       * @method onDeleteUserCancel
       * @param e {object} DomEvent
       */
      onDeleteUserCancel: function ConsoleUsers_onDeleteUserCancel(e)
      {
         this.popups.deleteDialog.hide();
      },
      
      /**
       * PRIVATE FUNCTIONS
       */
      
      /**
       * Make the specified panel visible - hiding any others and firing
       * the various Panel events as we go.
       * 
       * @method _showPanel
       * @param panelId {string} ID of the panel to make visible
       * @private
       */
      _showPanel: function ConsoleUsers__showPanel(panelId)
      {
         if (this.currentPanelId !== panelId)
         {
            this.currentPanelId = panelId;
            for (var index in this.panels)
            {
               var panel = this.panels[index];
               if (panel.id !== panelId)
               {
                  Dom.setStyle(this.id + "-" + panel.id, "display", "none");
                  
                  // Fire the onHide() panel lifecycle event
                  panel.onHide();
               }
            }
            
            // Fire the onBeforeShow() panel lifecycle event
            panel.onBeforeShow();
            
            // Display the specified panel to the user
            Dom.setStyle(this.id + "-" + panelId, "display", "block");
            
            // Fire the onShow() panel lifecycle event
            panel.onShow();
         }
      },
      
      /**
       * Fire an onUpdate() event to the currently visible panel.
       * 
       * @method _updateCurrentPanel
       * @private
       */
      _updateCurrentPanel: function ConsoleUsers__updateCurrentPanel()
      {
         for (var i in this.panels)
         {
            if (this.panels[i].id === this.currentPanelId)
            {
               this.panels[i].onUpdate();
               break;
            }
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