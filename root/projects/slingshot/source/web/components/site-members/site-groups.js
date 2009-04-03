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
 * Site Members component.
 * 
 * @namespace Alfresco
 * @class Alfresco.SiteGroups
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
    * SiteGroups constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.SiteGroups} The new SiteGroups instance
    * @constructor
    */
   Alfresco.SiteGroups = function(htmlId)
   {
      this.name = "Alfresco.SiteGroups";
      this.id = htmlId;
      
      // initialise prototype properties
      this.widgets = {};
      this.listWidgets = {};
      this.buttons = [];
      this.modules = {};
      this.isCurrentUserSiteAdmin = false;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "container", "datasource", "datatable", "json"], this.onComponentsLoaded, this);
   
      /* Decoupled event listeners */
      YAHOO.Bubbling.on("deactivateAllControls", this.onDeactivateAllControls, this);

      return this;
   };
   
   Alfresco.SiteGroups.prototype =
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
          * Number of characters required for a search.
          * 
          * @property minSearchTermLength
          * @type int
          * @default 3
          */
         minSearchTermLength: 3,
         
         /**
          * Maximum number of search results displayed.
          * 
          * @property maxSearchResults
          * @type int
          * @default 100
          */
         maxSearchResults: 100,
         
         /**
          * The userid of the current user
          * 
          * @property currentUser
          * @type string
          */
         currentUser: "",
         
         /**
          * The role of the current user in the current site
          * 
          * @property currentUserRole
          * @type string
          */
         currentUserRole: "",
         
         /**
          * Holds the list of roles available in the site
          */
         roles: [],

         /**
          * Set to an error string is an error occurred
          */
         error: null
      },

      /**
       * Object container for storing YUI widget instances.
       * 
       * @property widgets
       * @type object
       */
      widgets: null,
      
      /**
       * Object container for storing YUI widget instances.
       * 
       * @property listWidgets
       * @type object
       */
      listWidgets: null,
 
      /**
       * List of uninvite buttons
       * 
       * @property buttons
       * @type array
       */
      buttons: null,

      /**
       * Object container for storing module instances.
       * 
       * @property modules
       * @type object
       */
      modules: null,

      /**
       * Flag to determine whether the current user is a site administrator
       * 
       * @property isCurrentUserSiteAdmin
       * @type boolean
       */
      isCurrentUserSiteAdmin: null,
      
      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.Search} returns 'this' for method chaining
       */
      setOptions: function SiteGroups_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },
      
      /**
       * Set messages for this component.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.Search} returns 'this' for method chaining
       */
      setMessages: function SiteGroups_setMessages(obj)
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
      onComponentsLoaded: function SiteGroups_onComponentsLoaded()
      {
         Event.onContentReady(this.id, this.onReady, this, true);
      },
      
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function SiteGroups_onReady()
      {  
         var me = this;
         
         // DataSource definition
         var uriSearchResults = Alfresco.constants.PROXY_URI + "api/sites/" + me.options.siteId + "/memberships?";
         this.widgets.dataSource = new YAHOO.util.DataSource(uriSearchResults);
         this.widgets.dataSource.responseType = YAHOO.util.DataSource.TYPE_JSON;
         this.widgets.dataSource.connXhrMode = "queueRequests";
         this.widgets.dataSource.responseSchema =
         {
             resultsList: "items",
             fields: ["userName", "firstName", "lastName", "role", "avatar", "jobtitle", "organization"]
         };
         this.widgets.dataSource.doBeforeParseData = function SiteGroups_doBeforeParseData(oRequest , oFullResponse)
         {
            var updatedResponse = oFullResponse;
               
            if (oFullResponse)
            {
               var items = [];
               
               // create a data format that the DataTable can use
               for (var x = 0; x < oFullResponse.length; x++)
               {
                  var memberData = oFullResponse[x];
                  
                  // create object to represent member
                  var member =
                  {
                     "userName": memberData.person.userName,
                     "firstName": memberData.person.firstName,
                     "lastName": memberData.person.lastName,
                     "role": memberData.role
                  };
                  
                  // add optional metadata
                  if (memberData.person.avatar !== undefined)
                  {
                     member.avatar = memberData.person.avatar;
                  }
                  
                  if (memberData.person.jobtitle !== undefined)
                  {
                     member.jobtitle = memberData.person.jobtitle;
                  }
                  
                  if (memberData.person.organization !== undefined)
                  {
                     member.organization = memberData.person.organization;
                  }
                  
                  // add member to list
                  items.push(member);
               }
               
               // Sort the memeber list by name
               items.sort(function (membership1, membership2)
               {
                  var name1 = membership1.firstName + membership1.lastName;
                  var name2 = membership2.firstName + membership2.lastName;
                  return (name1 > name2) ? 1 : (name1 < name2) ? -1 : 0;
               });
               
               // we need to wrap the array inside a JSON object so the DataTable is happy
               updatedResponse =
               {
                  "items": items
               };
            }
            
            return updatedResponse;
         };
         
         // determine if current user is a site administrator
         if (me.options.currentUserRole !== undefined &&
             me.options.currentUserRole === "SiteManager")
         {
            this.isCurrentUserSiteAdmin = true;
         }
         
         // setup of the datatable.
         this._setupDataTable();
         
         // setup the buttons
         this.widgets.searchButton = Alfresco.util.createYUIButton(this, "button", this.doSearch);
         this.widgets.addGroups = Alfresco.util.createYUIButton(this, "addGroups", null, 
         {
            type: "link"
         });
         
         // register the "enter" event on the search text field
         var searchInput = Dom.get(this.id + "-term");
         var enterListener = new YAHOO.util.KeyListener(searchInput,
         {
            keys:13
         },
         {
            fn: function() 
            {
               me.doSearch();
            },
            scope: this,
            correctScope: true
         }, "keydown");
         enterListener.enable();
         
         if (this.options.error)
         {
            enterListener.disable();
            this.widgets.dataTable.set("MSG_ERROR", this.options.error);
            this.widgets.dataTable.showTableMessage(this.options.error, YAHOO.widget.DataTable.CLASS_ERROR);
            // Deactivate controls
            YAHOO.Bubbling.fire("deactivateAllControls");
         }
         
         // Finally show the component body here to prevent UI artifacts on YUI button decoration
         Dom.setStyle(this.id + "-body", "visibility", "visible");
      },

      _setupDataTable: function SiteGroups_setupDataTable()
      {
         /**
          * DataTable Cell Renderers
          *
          * Each cell has a custom renderer defined as a custom function. See YUI documentation for details.
          * These MUST be inline in order to have access to the Alfresco.SiteGroups class (via the "me" variable).
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
         var renderCellAvatar = function SiteGroups_renderCellAvatar(elCell, oRecord, oColumn, oData)
         {
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

            var userName = oRecord.getData("userName");
            var userUrl = Alfresco.constants.URL_PAGECONTEXT + "user/" + userName + "/profile";
            var avatarUrl = Alfresco.constants.URL_CONTEXT + "components/images/no-user-photo-64.png";
            if (oRecord.getData("avatar") !== undefined)
            {
               avatarUrl = Alfresco.constants.PROXY_URI + oRecord.getData("avatar") + "?c=queue&ph=true";
            }

            elCell.innerHTML = '<a href="' + userUrl + '"><img src="' + avatarUrl + '" alt="avatar" /></a>';
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
         var renderCellDescription = function SiteGroups_renderCellDescription(elCell, oRecord, oColumn, oData)
         {
            // Currently rendering all results the same way
            var userName = oRecord.getData("userName");
            var name = userName;
            var firstName = oRecord.getData("firstName");
            var lastName = oRecord.getData("lastName");
            if ((firstName !== undefined) || (lastName !== undefined))
            {
               name = firstName ? firstName + " " : "";
               name += lastName ? lastName : "";
            }

            var url = Alfresco.constants.URL_PAGECONTEXT + "user/" + userName + "/profile";
            var title = oRecord.getData("jobtitle") ? oRecord.getData("jobtitle") : "";
            var organization = oRecord.getData("organization") ? oRecord.getData("organization") : "";
            var desc = '<h3><a href="' + url + '">' + $html(name) + '</a></h3>';
            if (title.length > 0)
            {
               desc += '<div><span class="attr-name">' + me._msg('title') + ': </span>&nbsp;<span class="attr-value">' + $html(title) + '</span></div>';
            }
            if (organization.length > 0)
            {
               desc += '<div><span class="attr-name">' + me._msg('company') + ':</span>&nbsp;<span class="attr-value">' + $html(organization) + '</span></div>';
            }
            
            elCell.innerHTML = desc;
         };
         
         /**
          * Role select custom datacell formatter
          *
          * @method renderCellRoleSelect
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         var renderCellRoleSelect = function SiteGroups_renderCellRoleSelect(elCell, oRecord, oColumn, oData)
         {
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");
            Dom.setStyle(elCell.parentNode, "text-align", "right");
            Dom.addClass(elCell, "overflow");
            
            var currentRole = oRecord.getData("role");
            
            if (me.isCurrentUserSiteAdmin)
            {
               // create HTML for representing buttons
               var userName = oRecord.getData("userName");
               elCell.innerHTML = '<span id="' + me.id + '-roleselector-' + userName + '"></span>';
               
               // create the roles menu
               var rolesMenu = [];
               for (var x=0; x < me.options.roles.length; x++)
               {
                  var role = me.options.roles[x];
                  var recordIndex = x;
                  rolesMenu.push(
                  {
                     text: me._msg("role." + role),
                     value: role,
                     onclick:
                     {
                        fn: me.onRoleSelect,
                        obj:
                        {
                           user: userName,
                           currentRole: currentRole,
                           newRole: role,
                           recordId: oRecord.getId()
                        },
                        scope: me
                     }
                  }
                  );
               }
               
               // create the role selector button
               var roleselector = new YAHOO.widget.Button(
               {
                  container: me.id + '-roleselector-' + userName,
                  type: "menu",
                  label: me._msg("role." + currentRole),
                  menu: rolesMenu
               });
               
               // store a reference to the role selector button
               me.listWidgets[userName] =
               {
                  roleSelector: roleselector
               };
               
               // store the buttons
               me.buttons[userName + "-roleselector"] =
               {
                  roleselector: roleselector
               };
            }
            else
            {
               // output padding div so layout is not messed up due to missing buttons
               elCell.innerHTML = '<div>' + me._msg("role." + currentRole) + '</div>';
            }
         };
         
         /**
          * Uninvite button custom datacell formatter
          *
          * @method renderCellUninvite
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         var renderCellUninvite = function SiteGroups_renderCellUninvite(elCell, oRecord, oColumn, oData)
         {
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

            if (me.isCurrentUserSiteAdmin)
            {
               // create HTML for representing buttons
               var userName = oRecord.getData("userName");
               elCell.innerHTML = '<span id="' + me.id + '-button-' + userName + '"></span>';
               
               // create the uninvite button
               var button = new YAHOO.widget.Button(
               {
                   container: me.id + '-button-' + userName,
                   label: me._msg("site-groups.remove"),
                   onclick:
                   {
                      fn: me.doRemove,
                      obj: userName,
                      scope: me
                     }
               });
               
               // store the buttons
               me.buttons[userName + "-button"] =
               {
                  button: button
               };
            }
            else
            {
               // output padding div so layout is not messed up due to missing buttons
               elCell.innerHTML = '<div></div>';
            }
         };

         // DataTable column defintions
         var columnDefinitions = [
         {
            key: "userName", label: "User Name", sortable: false, formatter: renderCellAvatar, width: 64
         },
         {
            key: "bio", label: "Bio", sortable: false, formatter: renderCellDescription
         },
         {
            key: "role", label: "Select Role", formatter: renderCellRoleSelect, width: 140
         },
         {
            key: "uninvite", label: "Uninvite", formatter: renderCellUninvite, width: 80
         }
         ];

         // DataTable definition
         this.widgets.dataTable = new YAHOO.widget.DataTable(this.id + "-groups", columnDefinitions, this.widgets.dataSource,
         {
            renderLoopSize: 32,
            initialLoad: false,
            MSG_EMPTY: '<span style="white-space: nowrap;">' + this._msg("site-groups.enter-search-term") + '</span>'
         });

         // Override abstract function within DataTable to set custom error message
         this.widgets.dataTable.doBeforeLoadData = function SiteGroups_doBeforeLoadData(sRequest, oResponse, oPayload)
         {
            if (oResponse.error)
            {
               try
               {
                  var response = YAHOO.lang.JSON.parse(oResponse.responseText);
                  me.widgets.dataTable.set("MSG_ERROR", response.message);
               }
               catch(e)
               {
                  me._setDefaultDataTableErrors(me.widgets.dataTable);
               }
            }
            else if (oResponse.results)
            {
               if (oResponse.results.length === 0)
               {
                  me.widgets.dataTable.set("MSG_EMPTY", '<span style="white-space: nowrap;">' + me._msg("message.empty") + '</span>');
               }
               me.renderLoopSize = oResponse.results.length >> (YAHOO.env.ua.gecko === 1.8) ? 3 : 5;
            }
            
            // Must return true to have the "Searching..." message replaced by the error message
            return true;
         };
      },
      
      /**
       * Search event handler
       *
       * @method doSearch
       */
      doSearch: function SiteGroups_doSearch()
      {
         var searchTerm = $html(Dom.get(this.id + "-term").value);
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
       * Remove user event handler
       * 
       * @method doRemove
       * @param event {object} The event object
       * @param user {string} The userName to remove
       */
      doRemove: function SiteGroups_doRemove(event, user)
      {
         // show a wait message
         this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
         {
            text: this._msg("message.removing"),
            spanClass: "wait",
            displayTime: 0
         });
         
         // request success handler
         var success = function SiteGroups_doRemove_success(response, user)
         {
            // hide the wait message
            this.widgets.feedbackMessage.destroy();
             
            // show popup message to confirm
            Alfresco.util.PopupManager.displayMessage(
            {
               text: this._msg("site-groups.remove-success", user)
            });
         
            // remove the entry
            var recordIndex = this.widgets.dataTable.getRecordIndex(event.target.id);
            this.widgets.dataTable.deleteRow(recordIndex);
         };
         
         // request failure handler
         var failure = function SiteGroups_doRemove_failure(response)
         {
            // remove the message
            this.widgets.feedbackMessage.destroy();
         };
          
         // make ajax call to site service to join user
         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.PROXY_URI + "api/sites/" + this.options.siteId + "/memberships/" + user,
            method: "DELETE",
            successCallback:
            {
               fn: success,
               obj: user,
               scope: this
            },
            failureMessage: this._msg("site-groups.remove-failure", user),
            failureCallback:
            {
               fn: failure,
               scope: this
            }
         });
      },
      
      /**
       * Called when the user selects a role in the role dropdown
       * 
       * @parma p_obj: object containing record and role to set
       */
      onRoleSelect: function SiteGroups_onRoleSelect(type, event, args)
      {
         // fetch the current and new roles to see whether we have to change the role
         var record = this.widgets.dataTable.getRecord(args.recordId)
         var data = record.getData();
         var recordIndex = this.widgets.dataTable.getRecordIndex(record);
         var currentRole = data.role;
         var selectedRole = args.newRole;
         var user = args.user;
         if (selectedRole !== currentRole)
         {
            // show a wait message
            this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
            {
               text: this._msg("message.changingrole"),
               spanClass: "wait",
               displayTime: 0
            });

            // request success handler
            var success = function SiteGroups_onRoleSelect_success(response, userRole)
            {
               // hide the wait message
               this.widgets.feedbackMessage.destroy();

               // show popup message to confirm
               Alfresco.util.PopupManager.displayMessage(
               {
                  text: this._msg("site-groups.change-role-success", userRole.user, userRole.role)
               });

               // update the data and table
               var data = this.widgets.dataTable.getRecord(userRole.recordIndex).getData();
               data.role = args.newRole;
               this.widgets.dataTable.updateRow(userRole.recordIndex, data);
            };

            // request failure handler
            var failure = function SiteGroups_onRoleSelect_failure(response)
            {
               // remove the message
               this.widgets.feedbackMessage.destroy();
            };

            // make ajax call to site service to change role
            Alfresco.util.Ajax.jsonRequest(
            {
               url: Alfresco.constants.PROXY_URI + "api/sites/" + this.options.siteId + "/memberships/" + user,
               method: "PUT",
               dataObj:
               {
                  role: selectedRole,
                  person:
                  {
                     userName: user
                  }
               },
               successCallback:
               {
                  fn: success,
                  obj:
                  {
                     user: user,
                     role: selectedRole,
                     recordIndex: recordIndex
                  },
                  scope: this
               },
               failureMessage: this._msg("site-groups.change-role-failure", user),
               failureCallback:
               {
                  fn: failure,
                  scope: this
               }
            });
         }
      },

      /**
       * Deactivate All Controls event handler
       *
       * @method onDeactivateAllControls
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onDeactivateAllControls: function SiteGroups_onDeactivateAllControls(layer, args)
      {
         var index, widget, fnDisable = Alfresco.util.disableYUIButton;
         for (index in this.widgets)
         {
            if (this.widgets.hasOwnProperty(index))
            {
               fnDisable(this.widgets[index]);
            }
         }
      },

      
      /**
       * Resets the YUI DataTable errors to our custom messages
       * NOTE: Scope could be YAHOO.widget.DataTable, so can't use "this"
       *
       * @method _setDefaultDataTableErrors
       * @param dataTable {object} Instance of the DataTable
       */
      _setDefaultDataTableErrors: function SiteGroups__setDefaultDataTableErrors(dataTable)
      {
         var msg = Alfresco.util.message;
         dataTable.set("MSG_EMPTY", msg("message.empty", "Alfresco.SiteGroups"));
         dataTable.set("MSG_ERROR", msg("message.error", "Alfresco.SiteGroups"));
      },
      
      /**
       * Updates members list by calling data webscript with current search term
       *
       * @method _performSearch
       * @param searchTerm {string} The term to search for
       */
      _performSearch: function SiteGroups__performSearch(searchTerm)
      {
         // Reset the custom error messages
         this._setDefaultDataTableErrors(this.widgets.dataTable);
         
         // Display loading message
         this.widgets.dataTable.set("MSG_EMPTY", this._msg("site-groups.searching"));
         
         // empty results table
         this.widgets.dataTable.deleteRows(0, this.widgets.dataTable.getRecordSet().getLength());
         
         function successHandler(sRequest, oResponse, oPayload)
         {
            this.widgets.dataTable.onDataReturnInitializeTable.call(this.widgets.dataTable, sRequest, oResponse, oPayload);
         }
         
         function failureHandler(sRequest, oResponse)
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
         
         this.widgets.dataSource.sendRequest(this._buildSearchParams(searchTerm),
         {
            success: successHandler,
            failure: failureHandler,
            scope: this
         });
      },

      /**
       * Build URI parameter string for finding site members
       *
       * @method _buildSearchParams
       * @param searchTerm {string} Path to query
       */
      _buildSearchParams: function SiteGroups__buildSearchParams(searchTerm)
      {
         var params = YAHOO.lang.substitute("size={maxResults}&nf={term}",
         {
            maxResults: this.options.maxSearchResults,
            term: encodeURIComponent(searchTerm)
         });
         
         return params;
      },
      
      /**
       * Gets a custom message
       *
       * @method _msg
       * @param messageId {string} The messageId to retrieve
       * @return {string} The custom message
       * @private
       */
      _msg: function SiteGroups__msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, "Alfresco.SiteGroups", Array.prototype.slice.call(arguments).slice(1));
      }
   };
})();
