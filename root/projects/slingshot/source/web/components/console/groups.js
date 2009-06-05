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
 * ConsoleGroups tool component.
 *
 * @namespace Alfresco
 * @class Alfresco.ConsoleGroups
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
    * ConsoleGroups constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.ConsoleGroups} The new ConsoleGroups instance
    * @constructor
    */
   Alfresco.ConsoleGroups = function(htmlId)
   {
      this.name = "Alfresco.ConsoleGroups";
      Alfresco.ConsoleGroups.superclass.constructor.call(this, htmlId);

      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "container", "datasource", "datatable", "json", "history", "columnbrowser"], this.onComponentsLoaded, this);

      /* Decoupled event listeners */
      YAHOO.Bubbling.on("newGroup", this.onNewGroup, this);
      YAHOO.Bubbling.on("viewGroup", this.onViewGroup, this);
      YAHOO.Bubbling.on("updateGroup", this.onUpdateGroup, this);
      YAHOO.Bubbling.on("deleteGroup", this.onDeleteGroup, this);

      /* Define panel handlers */
      var parent = this;

      // NOTE: the panel registered first is considered the "default" view and is displayed first

      /* Search Panel Handler */
      SearchPanelHandler = function SearchPanelHandler_constructor()
      {
         SearchPanelHandler.superclass.constructor.call(this, "search");
      };

      YAHOO.extend(SearchPanelHandler, Alfresco.ConsolePanelHandler,
      {
         BROWSE_PANEL: "browse-panel",
         SEARCH_PANEL: "search-panel",

         state: 1,

         onLoad: function onLoad()
         {
            var me = this;

            // Search Button
            var searchButton = new YAHOO.widget.Button(parent.id + "-search-button",
            {
               type: "button",
               disabled: false
            });
            searchButton.on("click", this.onSearchClick, searchButton, this);

            // ColumnBrowser
            parent.widgets.columnbrowser = new YAHOO.extension.ColumnBrowser(parent.id + "-columnbrowser",
            {
               url: Alfresco.constants.PROXY_URI + "/api/rootgroups",
               numVisible: 3,
               columnInfoBuilder: {
                  fn: this.onBuildColumnInfo,
                  scope: this
               },
               emptyColumnInfoBuilder: {
                  fn: this.onBuildEmptyColumnInfo,
                  scope: this
               }
            });

            // ColumnBrowser Breadcrumb
            parent.widgets.breadcrumb = new YAHOO.extension.ColumnBrowserBreadCrumb(parent.id + "-breadcrumb",
            {
               columnBrowser: parent.widgets.columnbrowser,
               root: parent._msg("label.breadcrumb.root")
            });

            // Close search button            
            var closeSearchButton = new YAHOO.widget.Button(parent.id + "-closesearch-button",
            {
               type: "button",
               disabled: false
            });
            closeSearchButton.on("click", this.onCloseSearchClick, closeSearchButton, this);

                        
            // DataTable and DataSource setup
            parent.widgets.dataSource = new YAHOO.util.DataSource(Alfresco.constants.PROXY_URI + "api/groups");
            parent.widgets.dataSource.responseType = YAHOO.util.DataSource.TYPE_JSON;
            parent.widgets.dataSource.responseSchema =
            {
               resultsList: "data",
               fields:
               [
                  "shortName", "displayName"
               ],                                  
               metaFields:
               {
                  recordOffset: "startIndex",
                  totalRecords: "totalRecords"
               }
            };

            // Work to be performed after data has been queried but before display by the DataTable
            parent.widgets.dataSource.doBeforeParseData = function ConsoleGroups_SearchPanel_doBeforeParseData(oRequest, oFullResponse)
            {
               var updatedResponse = oFullResponse;

               if (oFullResponse)
               {
                  var items = oFullResponse.data;

                  // initial sort by username field
                  items.sort(function(a, b)
                  {
                     return (a.shortName ? a.shortName.toLowerCase() : "" > b.shortName ? b.shortName.toLowerCase() : "");
                  });

                  // we need to wrap the array inside a JSON object so the DataTable gets the object it expects
                  updatedResponse =
                  {
                     "data": items
                  };
               }

               // update Results Bar message with number of results found
               if (items.length == 0)
               {
                  me._setResultsMessage("message.noresults");
               }
               else if(items.length < parent.options.maxSearchResults)
               {
                  me._setResultsMessage("message.results", $html(parent.query), items.length);
               }
               else
               {
                  me._setResultsMessage("message.maxresults", parent.options.maxSearchResults);
               }

               return updatedResponse;
            };

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
                  this.onSearchClick();
               },
               scope: this,
               correctScope: true
            }, "keydown").enable();
         },

         onShow: function onShow()
         {
            Dom.get(parent.id + "-search-text").focus();
         },

         onUpdate: function onUpdate()
         {
            if(parent.query)
            {
               Dom.addClass(parent.id + "-browse-panel", "hidden");
               Dom.removeClass(parent.id + "-search-panel", "hidden");

               // update the text field - as this event could come from bookmark, navigation or a search button click
               var queryElem = Dom.get(parent.id + "-search-text");
               queryElem.value = parent.query;

               // check search length again as we may have got here via history navigation
               if (parent.query.length >= parent.options.minqueryLength)
               {
                  var me = this;

                  // Reset the custom error messages
                  me._setDefaultDataTableErrors(parent.widgets.dataTable);

                  // Don't display any message
                  parent.widgets.dataTable.set("MSG_EMPTY", parent._msg("message.searching"));

                  // Empty results table
                  parent.widgets.dataTable.deleteRows(0, parent.widgets.dataTable.getRecordSet().getLength());

                  var successHandler = function ConsoleGroups__ps_successHandler(sRequest, oResponse, oPayload)
                  {
                     me._setDefaultDataTableErrors(parent.widgets.dataTable);
                     parent.widgets.dataTable.onDataReturnInitializeTable.call(parent.widgets.dataTable, sRequest, oResponse, oPayload);
                  };

                  var failureHandler = function ConsoleGroups__ps_failureHandler(sRequest, oResponse)
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
                  };

                  parent.widgets.dataSource.sendRequest(me._buildSearchParams(parent.query),
                  {
                     success: successHandler,
                     failure: failureHandler,
                     scope: parent
                  });
               }
            }
            else
            {
               Dom.addClass(parent.id + "-search-panel", "hidden");
               Dom.removeClass(parent.id + "-browse-panel", "hidden");
            }

         },

         onSearchClick: function SearchPanel_onSearchClick()
         {
            var queryElem = Dom.get(parent.id + "-search-text");
            var query = queryElem.value;

            // inform the user if the search term entered is too small
            if (query.length < parent.options.minqueryLength)
            {
               Alfresco.util.PopupManager.displayMessage(
               {
                  text: parent._msg("message.minimum-length", parent.options.minqueryLength)
               });
               return;
            }

            parent.refreshUIState({"query": query});
         },

         onCloseSearchClick: function SearchPanel_onCloseSearchClick()
         {
            parent.refreshUIState({"query": undefined});
         },

         onBuildEmptyColumnInfo: function ConsoleGroups_onBuildEmptyColumnInfo(itemInfo)
         {
            return {
               parent: itemInfo,
               header: {
                  label: parent._msg("button.newgroup"),
                  click: {
                     fn: this.onHeaderClick,
                     scope: this
                  }
               },
               body: {},
               footer: {
                  label: parent._msg("label.nogroups")
               }
            };
         },

         onBuildColumnInfo: function ConsoleGroups_onBuildColumnInfo(serverResponse, itemInfo)
         {
            var obj = YAHOO.lang.JSON.parse(serverResponse.responseText);
            var column = {
               parent: itemInfo,
               header: {
                  label: parent._msg("button.newgroup"),
                  click: {
                     fn: this.onHeaderClick,
                     scope: this
                  }
               },
               body: {
                  items: []
               }
            };

            for(var i = 0; i < obj.data.length; i++)
            {
               var o = obj.data[i];
               if(o.authorityType == 'GROUP')
               {
                  var item = {
                     shortName: o.shortName,
                     href: Alfresco.constants.PROXY_URI + o.url + "/children",
                     label: o.displayName,
                     rel : o.groupCount > 0 ? "ajax" : null,
                     next : null,
                     buttons: [
                        {
                           title: parent._msg("button.updategroup"),
                           type: "groups-update-button",
                           click: {
                              fn: this.onUpdateClick,
                              scope: this
                           }
                        },
                        {
                           title: parent._msg("button.deletegroup"),
                           type: "groups-delete-button",
                           click: {
                              fn: this.onDeleteClick,
                              scope: this
                           }
                        }

                     ]
                  };
                  column.body.items.push(item);
               }
            }
            // Sort the groups
            column.body.items = column.body.items.sort(function(a, b)
            {
               return (a.label.toLowerCase() > b.label.toLowerCase());
            });

            column.footer =
            {
               label: (column.body.items.length ? parent._msg("label.noofgroups", column.body.items.length) : parent._msg("label.nogroups"))
            };

            return column;
         },

         onHeaderClick: function ConsoleGroups_onHeaderClick(columnInfo)
         {
            // Send avenet so the create panel will be displayed
            YAHOO.Bubbling.fire('newGroup',
            {
               group: columnInfo.parent ? columnInfo.parent.shortName : undefined,
               groupDisplayName: columnInfo.parent ? columnInfo.parent.displayName : parent._msg("label.theroot")
            });
         },

         onDeleteClick: function ConsoleGroups_onDeleteClick(itemInfo)
         {
            YAHOO.Bubbling.fire('deleteGroup', {group: itemInfo.shortName, groupDisplayName: itemInfo.label});
         },

         onUpdateClick: function ConsoleGroups_onUpdateClick(itemInfo)
         {
            YAHOO.Bubbling.fire('updateGroup', {group: itemInfo.shortName, groupDisplayName: itemInfo.label});
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
             * Generic HTML-safe custom datacell formatter
             */
            var renderCellSafeHTML = function renderCellSafeHTML(elCell, oRecord, oColumn, oData)
            {
               elCell.innerHTML = $html(oData);
            };

            /**
             * Group actions custom datacell formatter
             *
             * @method renderActions
             */
            var renderActions = function renderActions(elCell, oRecord, oColumn, oData)
            {
               // fire the 'updateGroupClick' event when the group has been clicked
               var actions = "<a href='#' class=\"update\" onclick=\"YAHOO.Bubbling.fire('updateGroup', {group: '" + oRecord.getData("shortName") + "', groupDisplayName: '" + oRecord.getData("displayName") + "'}); return false;\">&nbsp;</a>";
               // fire the 'deleteGroupClick' event when the group has been clicked
               actions += "<a href='#' class=\"delete\" onclick=\"YAHOO.Bubbling.fire('deleteGroup', {group: '" + oRecord.getData("shortName") + "', groupDisplayName: '" + oRecord.getData("displayName") + "'}); return false;\">&nbsp;</a>";
               elCell.innerHTML = actions;
            };

            // DataTable column defintions
            var columnDefinitions =
            [
               { key: "shortName", label: parent._msg("label.shortname"), sortable: true, formatter: renderCellSafeHTML },
               { key: "displayName", label: parent._msg("label.displayname"), sortable: true, formatter: renderCellSafeHTML },
               { key: "actions",     label: parent._msg("label.actions"), sortable: false, formatter: renderActions }
            ];

            // DataTable definition
            parent.widgets.dataTable = new YAHOO.widget.DataTable(parent.id + "-datatable", columnDefinitions, parent.widgets.dataSource,
            {
               initialLoad: false,
               renderLoopSize: 32,
               sortedBy:
               {
                  key: "displayName",
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
            dataTable.set("MSG_EMPTY", parent._msg("message.empty", "Alfresco.ConsoleGroups"));
            dataTable.set("MSG_ERROR", parent._msg("message.error", "Alfresco.ConsoleGroups"));
         },

         /**
          * Build URI parameters for People List JSON data webscript
          *
          * @method _buildSearchParams
          * @param query {string} User search term
          * @private
          */
         _buildSearchParams: function _buildSearchParams(query)
         {
            return "?shortNameFilter=" + encodeURIComponent(query);
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
            var resultsDiv = Dom.get(parent.id + "-search-bar-text");
            resultsDiv.innerHTML = parent._msg(messageId, arg1, arg2);
         }
      });
      new SearchPanelHandler();

      /* Create Group Panel Handler */
      CreatePanelHandler = function CreatePanelHandler_constructor()
      {
         CreatePanelHandler.superclass.constructor.call(this, "create");
      };

      YAHOO.extend(CreatePanelHandler, Alfresco.ConsolePanelHandler,
      {
         _visible: false,

         onLoad: function onLoad()
         {

            var validFields = [];
            var onFieldKeyUp = function onFieldKeyUp(e)
            {
               validFields[this.id] = (this.value.length !== 0);
               var valid = true;
               for (var i in validFields)
               {
                  if (validFields[i] == false)
                  {
                     valid = false;
                     break;
                  }
               }
               parent.widgets.creategroupOkButton.set("disabled", !valid);
               parent.widgets.creategroupAnotherButton.set("disabled", !valid);
            };

            // Buttons
            parent.widgets.creategroupOkButton = Alfresco.util.createYUIButton(parent, "creategroup-ok-button", parent.onCreateGroupOKClick);
            parent.widgets.creategroupAnotherButton = Alfresco.util.createYUIButton(parent, "creategroup-another-button", parent.onCreateGroupAnotherClick);
            parent.widgets.creategroupCancelButton = Alfresco.util.createYUIButton(parent, "creategroup-cancel-button", parent.onCreateGroupCancelClick);
            parent.widgets.creategroupOkButton.set("disabled", true);
            parent.widgets.creategroupAnotherButton.set("disabled", true);

            // Event handlers for mandatory fields
            validFields[parent.id + "-create-shortname"] = false;
            Event.on(parent.id + "-create-shortname", "keyup", onFieldKeyUp);
            validFields[parent.id + "-create-displayname"] = false;
            Event.on(parent.id + "-create-displayname", "keyup", onFieldKeyUp);

         },

         onBeforeShow: function onBeforeShow()
         {
            // Hide the main panel area before it is displayed - so we don't show
            // old data to the user before the onShow() method paints the results
            Dom.setStyle(parent.id + "-create-main", "visibility", "hidden");

            var fnClearEl = function(id)
            {
               Dom.get(parent.id + id).value = "";
            };

            // clear data fields
            fnClearEl("-create-shortname");
            fnClearEl("-create-displayname");

         },

         onShow: function onShow()
         {
            this._visible = true;
            window.scrollTo(0, 0);

            // Make main panel area visible
            Dom.setStyle(parent.id + "-create-main", "visibility", "visible");

            Dom.get(parent.id + "-create-shortname").focus();
         },

         onHide: function onHide()
         {
            this._visible = false;
         }
      });
      new CreatePanelHandler();


      /* Update Group Panel Handler */
      UpdatePanelHandler = function UpdatePanelHandler_constructor()
      {
         UpdatePanelHandler.superclass.constructor.call(this, "update");
      };

      YAHOO.extend(UpdatePanelHandler, Alfresco.ConsolePanelHandler,
      {
         _visible: false,

         onLoad: function onLoad()                 
         {
            var validFields = [];
            var onFieldKeyUp = function onFieldKeyUp(e)
            {
               validFields[this.id] = (this.value.length !== 0);
               var valid = true;
               for (var i in validFields)
               {
                  if (validFields[i] == false)
                  {
                     valid = false;
                     break;
                  }
               }
               parent.widgets.updategroupSaveButton.set("disabled", !valid);
            };

            // Buttons
            parent.widgets.updategroupSaveButton = Alfresco.util.createYUIButton(parent, "updategroup-save-button", parent.onUpdateGroupOKClick);
            parent.widgets.updategroupCancelButton = Alfresco.util.createYUIButton(parent, "updategroup-cancel-button", parent.onUpdateGroupCancelClick);

            // Event handlers for mandatory fields
            validFields[parent.id + "-update-shortname"] = true;
            Event.on(parent.id + "-update-shortname", "keyup", onFieldKeyUp);
            validFields[parent.id + "-update-displayname"] = true;
            Event.on(parent.id + "-update-displayname", "keyup", onFieldKeyUp);
         },

         onBeforeShow: function onBeforeShow()
         {
            // Hide the main panel area before it is displayed - so we don't show
            // old data to the user before the Update() method paints the results
            Dom.setStyle(parent.id + "-update-main", "visibility", "hidden");
         },

         onShow: function onShow()
         {
            this._visible = true;
            window.scrollTo(0, 0);
         },

         onHide: function onHide()
         {
            this._visible = false;
         },

         onUpdate: function onUpdate()
         {
            var me = this;
            var success = function(o)
            {
               var fnSetter = function(id, val)
               {
                  Dom.get(parent.id + id).value = val;
               };

               var group = o.json.data;


               // About section fields
               Dom.get(parent.id + "-update-title").innerHTML = $html(group.displayName);
               fnSetter("-update-shortname", group.shortName);
               fnSetter("-update-displayname", group.displayName);

               // Make main panel area visible
               Dom.setStyle(parent.id + "-update-main", "visibility", "visible");
            };

            // make an ajax call to get user details
            Alfresco.util.Ajax.request(
            {
               url: Alfresco.constants.PROXY_URI + "api/groups/" + parent.group+ "",
               method: Alfresco.util.Ajax.GET,
               successCallback:
               {
                  fn: success,
                  scope: parent
               },
               failureMessage: parent._msg("message.getgroup-failure", parent.group)
            });
         }
      });
      new UpdatePanelHandler();

      return this;
   };

   YAHOO.extend(Alfresco.ConsoleGroups, Alfresco.ConsoleTool,
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
          * @property minqueryLength
          * @type int
          * @default 3
          */
         minqueryLength: 3,

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
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function ConsoleGroups_onComponentsLoaded()
      {
         Event.onContentReady(this.id, this.onReady, this, true);
      },

      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function ConsoleGroups_onReady()
      {
         Alfresco.ConsoleGroups.superclass.onReady.call(this);
      },


      /**
       * YUI WIDGET EVENT HANDLERS
       * Handlers for standard events fired from YUI widgets, e.g. "click"
       */

      /**
       * Fired when the Create Group OK button is clicked.
       *
       * @method onCreateGroupOKClick
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onCreateGroupOKClick: function ConsoleUsers_onCreateGroupOKClick(e, args)
      {
         var successHandler = function(res)
         {
            window.scrollTo(0, 0);
            Alfresco.util.PopupManager.displayMessage(
            {
               text: this._msg("message.create-success")
            });
            this.refreshUIState({"panel": "search"});
         };
         this._createGroup(successHandler);
      },

      /**
       * Fired when the Create Group and Create Another button is clicked.
       *
       * @method onCreateGroupAnotherClick
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onCreateGroupAnotherClick: function ConsoleGroups_onCreateGroupAnotherClick(e, args)
      {
         var successHandler = function(res)
         {
            window.scrollTo(0, 0);
            Alfresco.util.PopupManager.displayMessage(
            {
               text: this._msg("message.create-success")
            });

            // TODO: clear fields?

            Dom.get(this.id + "-create-shortname").focus();
         };
         this._createGroup(successHandler);
      },

      /**
       * Fired when the Create Group Cancel button is clicked.
       *
       * @method onCreateGroupCancelClick
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onCreateGroupCancelClick: function ConsoleGroups_onCreateGroupCancelClick(e, args)
      {
         this.refreshUIState({"panel": "search"});
      },

      /**
       * History manager state change event handler (override base class)
       *
       * @method onStateChanged
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onStateChanged: function ConsoleUsers_onStateChanged(e, args)
      {
         // Clear old states
         this.query = undefined;
         this.group = undefined;
         this.groupDisplayName = undefined;

         var state = this.decodeHistoryState(args[1].state);
         if(state.query)
         {
            this.query = state.query;
         }
         if(state.group)
         {
            this.group = state.group;
         }
         if(state.groupDisplayName)
         {
            this.groupDisplayName = state.groupDisplayName;
         }

         // test if panel has actually changed?
         if (state.panel)
         {
            this.showPanel(state.panel);
         }

         if (this.currentPanelId === "search")
         {
            this.updateCurrentPanel();
         }
         else if (this.currentPanelId === "create" ||
                  (state.group && (this.currentPanelId === "view" || this.currentPanelId === "update")))
         {
            this.updateCurrentPanel();
         }
      },

      /**
       * New Group event handler
       *
       * @method onNewGroup
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onNewGroup: function ConsoleUsers_onNewGroup(e, args)
      {
         var parentGroup = args[1].group;
         this.refreshUIState({"panel": "create", "group": parentGroup});
      },

      /**
       * View User event handler
       *
       * @method onViewGroup
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onViewGroup: function ConsoleGroups_onViewGroup(e, args)
      {
         var group = args[1].group;
         this.refreshUIState({"panel": "view", "group": group});
      },

      /**
       * Update User event handler
       *
       * @method onUpdateGroup
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onUpdateGroup: function ConsoleGroups_onUpdateGroup(e, args)
      {
         var group = args[1].group;
         this.refreshUIState({"panel": "update", "group": group});
      },

      /**
       * Delete User event handler
       *
       * @method deleteGroupClick
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onDeleteGroup: function ConsoleGroups_onDeleteGroup(e, args)
      {
         this._confirmDeleteGroup(args[1].group, args[1].groupDisplayName);
      },

      /**
       * Asks the users if he is sure he wants to delete the group
       *
       * @param shortName The id of the group
       * @param displayName The displayName  of the group
       */
      _confirmDeleteGroup: function ConsoleGroups_onDeleteGroup(shortName, displayName)
      {
         var me = this;
         Alfresco.util.PopupManager.displayPrompt(
         {
            text: this._msg("message.confirm.delete", displayName),
            buttons: [
               {
                  text: this._msg("button.yes"),
                  handler: function ConsoleGroups__deleteGroup_confirmYes()
                  {
                     this.destroy();
                     me._deleteGroup.call(me, shortName, displayName);
                  }
               },
               {
                  text: this._msg("button.no"),
                  handler: function ConsoleGroups__deleteGroup_confirmNo()
                  {
                     this.destroy();
                  },
                  isDefault: true
               }]
         });
      },

      /**
       * Remove the user from the repository
       *
       * @param shortName The id of the group
       * @param displayName The displayName  of the group
       */
      _deleteGroup: function ConsoleGroups_onDeleteGroup(shortName, displayName)
      {
         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.PROXY_URI + "api/groups/" + shortName,
            method: Alfresco.util.Ajax.DELETE,
            requestContentType: Alfresco.util.Ajax.JSON,
            successMessage: this._msg("message.delete-success", displayName),
            failureMessage: this._msg("message.delete-failure", displayName)
         });
      },

      /**
       * Fired when the Update User OK button is clicked.
       *
       * @method onUpdateGroupOKClick
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onUpdateGroupOKClick: function ConsoleGroups_onUpdateGroupOKClick(e, args)
      {
         var me = this;
         var handler = function(res)
         {
            window.scrollTo(0, 0);
            Alfresco.util.PopupManager.displayMessage(
            {
               text: me._msg("message.update-success")
            });
            me.refreshUIState({"panel": "search"});
         };
         this._updateGroup(handler);
      },

      /**
       * Fired when the Update Group Cancel button is clicked.
       *
       * @method onUpdateGroupCancelClick
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onUpdateGroupCancelClick: function ConsoleGroups_onUpdateGroupCancelClick(e, args)
      {
         this.refreshUIState({"panel": "search"});
      },

      /**
       * Encode state object into a packed string for use as url history value.
       * Override base class.
       *
       * @method encodeHistoryState
       * @param obj {object} state object
       * @private
       */
      encodeHistoryState: function ConsoleGroups_encodeHistoryState(obj)
      {
         // wrap up current state values
         var stateObj = {};
         if (this.currentPanelId !== "")
         {
            stateObj.panel = this.currentPanelId;
         }

         // convert to encoded url history state - overwriting with any supplied values
         var state = "";
         if (obj.panel || stateObj.panel)
         {
            state += "panel=" + encodeURIComponent(obj.panel ? obj.panel : stateObj.panel);
         }
         if (obj.group)
         {
            state += state.length > 0 ? "&" : "";            
            state += "group=" + encodeURIComponent(obj.group);
         }
         if (obj.query)
         {
            state += state.length > 0 ? "&" : "";
            state += "query=" + encodeURIComponent(obj.query);
         }
         return state;   
       },


      /**
       * Create a group - but first check if it already exists
       *
       * @method _createGroup
       * @param handler {function} Handler function to be called on successful creation
       * @private
       */
      _createGroup: function ConsoleGroups__createGroup(successHandler)
      {
         var shortName = Dom.get(this.id + "-create-shortname").value;
         Alfresco.util.Ajax.request(
         {
            url:  Alfresco.constants.PROXY_URI + "api/groups/" + shortName + "/parents?level=ALL",
            method: Alfresco.util.Ajax.GET,
            requestContentType: Alfresco.util.Ajax.JSON,
            successCallback:
            {
               fn: function(o)
               {
                  // The identifier is already used
                  var groups = o.json.data ? o.json.data : [];

                  // Lets see if the identifer already is placed under this group
                  var alreadyThere = false;

                  var parentStr = "";
                  for(var i = 0; i < groups.length; i++)
                  {
                     parentStr += groups[i].displayName + (i < groups.length - 1 ? ", " : "");
                  }
                  parentStr = parentStr.length > 0 ? parentStr : this._msg("label.theroot");
                  var me = this;
                  Alfresco.util.PopupManager.displayPrompt(
                  {
                     text: this._msg("message.confirm.add", shortName, parentStr, this.group ? this.group : this._msg("label.theroot")),
                     buttons: [
                        {
                           text: this._msg("button.ok"),
                           handler: function ConsoleGroups__createGroup_confirmOk()
                           {
                              this.destroy();
                              me._createGroupAfterExistCheck.call(me, successHandler);
                           }
                        },
                        {
                           text: this._msg("button.cancel"),
                           handler: function ConsoleGroups__createGroup_confirmCancel()
                           {
                              this.destroy();
                           },
                           isDefault: true
                        }]
                  });
               },
               scope: this
            },
            failureCallback:
            {
               fn: function(o)
               {
                  if(o.serverResponse.status == 404)
                  {
                     // group didn't exist just continue
                     this._createGroupAfterExistCheck(successHandler);
                  }
                  else
                  {
                     // Notify the user that an error occured when checking if the identifier already is used
                     Alfresco.util.PopupManager.displayPrompt(
                     {
                        title: this._msg("message.failure"),
                        text: this._msg("message.create-failure", o.json.message)
                     });
                  }
               },
               scope: this
            }
         });
      },

      /**
       * Create a group - returning true on success, false on any error.
       *
       * @method _createGroupAfterExistCheck
       * @param handler {function} Handler function to be called on successful creation
       * @private
       */
      _createGroupAfterExistCheck: function ConsoleGroups__createGroupAfterExistCheck(successHandler)
      {
         // gather up the data for our JSON PUT request
         var shortName = Dom.get(this.id + "-create-shortname").value;
         var displayName = Dom.get(this.id + "-create-displayname").value;
         displayName = displayName == "" ? undefined : displayName;
         var groupObj = {};

         var url = Alfresco.constants.PROXY_URI + "api/";
         var sh = successHandler;
         if(this.group && this.group.length > 0)
         {
            url += "groups/" + this.group + "/children/GROUP_" + shortName;
            sh = function(o)
            {
               if(o.serverResponse && o.serverResponse.status == 200)
               {
                  // Group already existed but was added as a child
                  // shall we display a message about it??
                  // Shall the displayName be updated??
               }
               if(displayName && shortName != displayName)
               {
                  /**
                   * When a group is created by adding it to a parent group its not possible to
                   * set the displayName in the same call, then another call must be made to
                   * update the display name.
                   */
                  groupObj.displayName = displayName;
                  this._updateGroupRequest(shortName, groupObj, successHandler);
               }
            };
         }
         else
         {
            url += "rootgroups/" + shortName;
            if(displayName)
            {
               groupObj.displayName = displayName;
            }
         }

         Alfresco.util.Ajax.request(
         {
            url: url,
            method: Alfresco.util.Ajax.POST,
            dataObj: groupObj,
            requestContentType: Alfresco.util.Ajax.JSON,
            successCallback:
            {
               fn: sh,
               scope: this
            },
            failureCallback:
            {
               fn: function(o)
               {
                  var obj = YAHOO.lang.JSON.parse(o.serverResponse.responseText);
                  Alfresco.util.PopupManager.displayPrompt(
                  {
                     title: this._msg("message.failure"),
                     text: this._msg("message.create-failure", obj.message)
                  });
               },
               scope: this
            }
         });
      },


      /**
       * Update a group - returning true on success, false on any error.
       *
       * @method _updateGroup
       * @param handler {function} Handler function to be called on successful update
       * @private
       */
      _updateGroup: function ConsoleGroups__updateGroup(successHandler)
      {
         var me = this;
         var fnGetter = function(id)
         {
            return Dom.get(me.id + id).value;
         };

         var shortName =  fnGetter("-update-shortname");
         var displayName = fnGetter("-update-displayname");
                  
         var groupObj =
         {
            displayName: displayName
         };
         this._updateGroupRequest(shortName, groupObj, successHandler);
      },

      /**
       * Update a group - returning true on success, false on any error.
       *
       * @method _updateGroupRequest
       * @param handler {function} Handler function to be called on successful update
       * @private
       */
      _updateGroupRequest: function ConsoleGroups__updateGroupRequest(shortName, groupObj, successHandler)
      {
         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.PROXY_URI + "api/groups/" + shortName,
            method: Alfresco.util.Ajax.PUT,
            dataObj: groupObj,
            requestContentType: Alfresco.util.Ajax.JSON,
            successCallback:
            {
               fn: successHandler,
               scope: this
            },
            failureCallback:
            {
               fn: function(o)
               {
                  Alfresco.util.PopupManager.displayPrompt(
                  {
                     title: this._msg("message.failure"),
                     text: this._msg("message.update-failure", o.json.message)
                  });
               },
               scope: this
            }
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
      _msg: function ConsoleGroups__msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, "Alfresco.ConsoleGroups", Array.prototype.slice.call(arguments).slice(1));
      }

   });

})();