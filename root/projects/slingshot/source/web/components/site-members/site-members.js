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
 * @class Alfresco.SiteMembers
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
    * SiteMembers constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.SiteMembers} The new SiteMembers instance
    * @constructor
    */
   Alfresco.SiteMembers = function(htmlId)
   {
      this.name = "Alfresco.SiteMembers";
      this.id = htmlId;
      
      // initialise prototype properties
      this.widgets = {};
      this.listWidgets = {};
      this.buttons = [];
      this.modules = {};
      this.searchTerm = "";
      this.isCurrentUserSiteAdmin = false;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "container", "datasource", "datatable", "json"], this.onComponentsLoaded, this);
   
      return this;
   }
   
   Alfresco.SiteMembers.prototype =
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
          * Maximum number of results displayed.
          * 
          * @property maxResults
          * @type int
          * @default 100
          */
         maxResults: 100,
         
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
         roles: []
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
       * Search term used for the site search.
       * 
       * @property searchTerm
       * @type string
       */
      searchTerm: "",
      
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
      setOptions: function SiteMembers_setOptions(obj)
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
      setMessages: function SiteMembers_setMessages(obj)
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
      onComponentsLoaded: function SiteMembers_onComponentsLoaded()
      {
         Event.onContentReady(this.id, this.onReady, this, true);
      },
      
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function SiteMembers_onReady()
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
         this.widgets.dataSource.doBeforeParseData = function SiteMembers_doBeforeParseData(oRequest , oFullResponse)
         {
            var updatedResponse = oFullResponse;
               
            if (oFullResponse)
            {
               var items = [];
               
               // create a data format that the DataTable can use
               for (var x = 0; x < oFullResponse.length; x++)
               {
                  var lowerSearchTerm = me.searchTerm.toLowerCase();
                  var memberData = oFullResponse[x];
                  var firstName = memberData.person.firstName;
                  var lastName = memberData.person.lastName;
                  
                  // Determine if member matches search term
                  if (firstName.toLowerCase().indexOf(lowerSearchTerm) != -1 ||
                      lastName.toLowerCase().indexOf(lowerSearchTerm) != -1)
                  {
                     // create object to represent member
                     var member = {
                        "userName": memberData.person.userName,
                        "firstName": firstName,
                        "lastName": lastName,
                        "role": memberData.role
                     };
                     
                     // add optional metadata
                     if (memberData.person.avatar !== undefined)
                     {
                        member["avatar"] = memberData.person.avatar;
                     }
                     
                     if (memberData.person.jobtitle !== undefined)
                     {
                        member["jobtitle"] = memberData.person.jobtitle;
                     }
                     
                     if (memberData.person.organization !== undefined)
                     {
                        member["organization"] = memberData.person.organization;
                     }
                     
                     // add member to list
                     items.push(member);
                  }
               }
               
               // we need to wrap the array inside a JSON object so the DataTable is happy
               updatedResponse = {
                  "items": items
               };
            }
            
            return updatedResponse;
         }
         
         // determine if current user is a site administrator
         if (me.options.currentUserRole !== undefined &&
             me.options.currentUserRole === "SiteManager")
         {
            this.isCurrentUserSiteAdmin = true;
         }
         
         // setup of the datatable.
         this._setupDataTable();
         
         // setup the button
         this.widgets.searchButton = Alfresco.util.createYUIButton(this, "button", this.doSearch);
         
         // register the "enter" event on the search text field
         var searchInput = Dom.get(this.id + "-term");
         new YAHOO.util.KeyListener(searchInput, { keys:13 }, 
         {
            fn: function() 
            {
               me.doSearch()
            },
            scope:this,
            correctScope:true
         }, 
         "keydown" 
         ).enable();
         
         // Finally show the component body here to prevent UI artifacts on YUI button decoration
         Dom.setStyle(this.id + "-body", "visibility", "visible");
      },
      
      _setupDataTable: function SiteMembers_setupDataTable()
      {
         /**
          * DataTable Cell Renderers
          *
          * Each cell has a custom renderer defined as a custom function. See YUI documentation for details.
          * These MUST be inline in order to have access to the Alfresco.SiteMembers class (via the "me" variable).
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
         var renderCellAvatar = function SiteMembers_renderCellAvatar(elCell, oRecord, oColumn, oData)
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
         renderCellDescription = function SiteMembers_renderCellDescription(elCell, oRecord, oColumn, oData)
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
            desc = '<h3><a href="' + url + '">' + $html(name) + '</a></h3>';
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
         renderCellRoleSelect = function SiteMembers_renderCellRoleSelect(elCell, oRecord, oColumn, oData)
         {
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");
            Dom.setStyle(elCell.parentNode, "text-align", "right");
            
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
                  rolesMenu.push(
                  {
                     text: me._msg("role." + role),
                     value: role,
                     onclick:
                     {
                        fn: me.onRoleSelect,
                        obj: {
                           user: userName,
                           currentRole: currentRole,
                           newRole: role
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
               me.listWidgets[userName] = {
                  roleSelector: roleselector
               };
               
               // store the buttons
               me.buttons[userName + "-roleselector"] = { roleselector: roleselector };
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
         renderCellUninvite = function InvitationList_renderCellUninvite(elCell, oRecord, oColumn, oData)
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
                   label: me._msg("site-members.uninvite"),
                   onclick: { fn: me.doRemove, obj: userName, scope: me}
               });
               
               // store the buttons
               me.buttons[userName + "-button"] = { button: button };
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

         YAHOO.widget.DataTable.MSG_EMPTY = '<span style="white-space: nowrap;">' +
            this._msg("site-members.enter-search-term") + '</span>';

         // DataTable definition
         this.widgets.dataTable = new YAHOO.widget.DataTable(this.id + "-members", columnDefinitions, this.widgets.dataSource,
         {
            renderLoopSize: 32,
            initialLoad: false
         });
         
         // Override abstract function within DataTable to set custom error message
         this.widgets.dataTable.doBeforeLoadData = function SiteMembers_doBeforeLoadData(sRequest, oResponse, oPayload)
         {
            if (oResponse.error)
            {
               try
               {
                  var response = YAHOO.lang.JSON.parse(oResponse.responseText);
                  YAHOO.widget.DataTable.MSG_ERROR = response.message;
               }
               catch(e)
               {
                  me._setDefaultDataTableErrors();
               }
            }
            else if (oResponse.results)
            {
               if (oResponse.results.length == 0)
               {
                  YAHOO.widget.DataTable.MSG_EMPTY = '<span style="white-space: nowrap;">' + 
                     me._msg("message.empty") + '</span>';
               }
               me.renderLoopSize = oResponse.results.length >> (YAHOO.env.ua.gecko) ? 3 : 5;
            }
            
            // Must return true to have the "Searching..." message replaced by the error message
            return true;
         }
      },
      
      /**
       * Search event handler
       *
       * @method doSearch
       */
      doSearch: function SiteMembers_doSearch()
      {
         this.searchTerm = Dom.get(this.id + "-term").value;
         this._performSearch(this.searchTerm);
      },
      
      /**
       * Remove user event handler
       * 
       * @method doRemove
       * @param event {object} The event object
       * @param user {string} The userName to remove
       */
      doRemove: function SiteMembers_doRemove(event, user)
      {
         // show a wait message
         this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
         {
            text: this._msg("message.removing"),
            spanClass: "wait",
            displayTime: 0
         });
         
         // request success handler
         var success = function SiteMembers_doRemove_success(response, user)
         {
            // hide the wait message
            this.widgets.feedbackMessage.destroy();
             
            // show popup message to confirm
            Alfresco.util.PopupManager.displayMessage(
            {
               text: this._msg("site-members.remove-success", user)
            });
         
            // remove the entry
            var recordIndex = this.widgets.dataTable.getRecordIndex(event.target);
            this.widgets.dataTable.deleteRow(recordIndex);
         };
         
         // request failure handler
         var failure = function SiteMembers_doRemove_failure(response)
         {
            // remove the message
            this.widgets.feedbackMessage.destroy();
         }
          
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
            failureMessage: this._msg("site-members.remove-failure", user),
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
      onRoleSelect: function SiteMembers_onRoleSelect(type, event, args)
      {
         // show a wait message
         this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
         {
            text: this._msg("message.changingrole"),
            spanClass: "wait",
            displayTime: 0
         });
         
         // request success handler
         var success = function SiteMembers_onRoleSelect_success(response, userRole)
         {
            // hide the wait message
            this.widgets.feedbackMessage.destroy();
            
            // show popup message to confirm
            Alfresco.util.PopupManager.displayMessage(
            {
               text: this._msg("site-members.change-role-success", userRole.user, userRole.role)
            });

            // update the data and table
            var recordIndex = this.widgets.dataTable.getRecordIndex(event[0].target);
            var data = this.widgets.dataTable.getRecord(recordIndex).getData();
            data.role = args.newRole;
            this.widgets.dataTable.updateRow(recordIndex, data);
         };
         
         // request failure handler
         var failure = function SiteMembers_onRoleSelect_failure(response)
         {
            // remove the message
            this.widgets.feedbackMessage.destroy();
         };
         
         // fetch the current and new roles to see whether we have to change the role
         var recordIndex = this.widgets.dataTable.getRecordIndex(event[0].target);
         var data = this.widgets.dataTable.getRecord(recordIndex).getData();
         var currentRole = data.role;
         var selectedRole = args.newRole;
         var user = args.user;
         
         if (selectedRole !== currentRole)
         {
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
                  obj: {
                     user: user,
                     role: selectedRole
                  },
                  scope: this
               },
               failureMessage: this._msg("site-members.change-role-failure", user),
               failureCallback:
               {
                  fn: failure,
                  scope: this
               },
            });
         }
      },

      
      /**
       * Resets the YUI DataTable errors to our custom messages
       * NOTE: Scope could be YAHOO.widget.DataTable, so can't use "this"
       *
       * @method _setDefaultDataTableErrors
       */
      _setDefaultDataTableErrors: function SiteMembers__setDefaultDataTableErrors()
      {
         var msg = Alfresco.util.message;
         YAHOO.widget.DataTable.MSG_EMPTY = msg("message.empty", "Alfresco.SiteMembers");
         YAHOO.widget.DataTable.MSG_ERROR = msg("message.error", "Alfresco.SiteMembers");
      },
      
      /**
       * Updates members list by calling data webscript with current search term
       *
       * @method _performSearch
       * @param searchTerm {string} The term to search for
       */
      _performSearch: function SiteMembers__performSearch(searchTerm)
      {
         // Reset the custom error messages
         this._setDefaultDataTableErrors();
         
         // Display loading message
         YAHOO.widget.DataTable.MSG_EMPTY = this._msg("site-members.searching");
         
         // empty results table
         this.widgets.dataTable.deleteRows(0, this.widgets.dataTable.getRecordSet().getLength());
         
         function successHandler(sRequest, oResponse, oPayload)
         {
            this.searchTerm = searchTerm;
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
                  YAHOO.widget.DataTable.MSG_ERROR = response.message;
                  this.widgets.dataTable.showTableMessage(response.message, YAHOO.widget.DataTable.CLASS_ERROR);
               }
               catch(e)
               {
                  this._setDefaultDataTableErrors();
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
       * Build URI parameter string for finding sites
       *
       * @method _buildSearchParams
       * @param searchTerm {string} Path to query
       */
      _buildSearchParams: function SiteMembers__buildSearchParams(searchTerm)
      {
         var params = YAHOO.lang.substitute("size={maxResults}",
         {
            maxResults : this.options.maxResults
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
      _msg: function SiteMembers__msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, "Alfresco.SiteMembers", Array.prototype.slice.call(arguments).slice(1));
      }
   };
})();
