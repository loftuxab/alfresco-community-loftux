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
    * @return {Alfresco.SiteFinder} The new SiteMembers instance
    * @constructor
    */
   Alfresco.SiteMembers = function(htmlId)
   {
      this.name = "Alfresco.SiteMembers";
      this.id = htmlId;
      
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
         currentUserRole: ""
      },

      /**
       * Object container for storing YUI widget instances.
       * 
       * @property widgets
       * @type object
       */
      widgets: {},
      
      /**
       * List of Join/Leave buttons
       * 
       * @property buttons
       * @type array
       */
      buttons: [],

      /**
       * Object container for storing module instances.
       * 
       * @property modules
       * @type object
       */
      modules: {},

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
      isCurrentUserSiteAdmin: false,
      
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
         this.widgets.dataSource.doBeforeParseData = function SiteFinder_doBeforeParseData(oRequest , oFullResponse)
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
          * These MUST be inline in order to have access to the Alfresco.SiteFinder class (via the "me" variable).
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
         renderCellDescription = function SiteFinder_renderCellDescription(elCell, oRecord, oColumn, oData)
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
               desc += '<div><span class="attr-name">Title:</span>&nbsp;<span class="attr-value">' + $html(title) + '</span></div>';
            }
            if (organization.length > 0)
            {
               desc += '<div><span class="attr-name">Company:</span>&nbsp;<span class="attr-value">' + $html(organization) + '</span></div>';
            }
            
            elCell.innerHTML = desc;
         };
         
         /**
          * Actions custom datacell formatter
          *
          * @method renderCellActions
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         renderCellActions = function InvitationList_renderCellActions(elCell, oRecord, oColumn, oData)
         {
            if (me.isCurrentUserSiteAdmin)
            {
               var userName = oRecord.getData("userName");
               var action = '<span id="' + me.id + '-button-' + userName + '"></span>';
               elCell.innerHTML = action;
               
               // create button
               var button = new YAHOO.widget.Button(
               {
                   container: me.id + '-button-' + userName
               });
               
               button.set("label", Alfresco.util.message("site-members.uninvite", "Alfresco.SiteMembers"));
               button.set("onclick", { fn: me.doRemove, obj: userName, scope: me});
               
               me.buttons[userName] = { button: button };
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
            key: "userName", label: "User Name", sortable: false, formatter: renderCellAvatar
         },
         {
            key: "bio", label: "Bio", sortable: false, formatter: renderCellDescription
         },
         {
            key: "actions", label: "Actions", formatter: renderCellActions
         }
         ];

         YAHOO.widget.DataTable.MSG_EMPTY = '<span style="white-space: nowrap;">' +
            Alfresco.util.message("site-members.enter-search-term", "Alfresco.SiteMembers") + '</span>';

         // DataTable definition
         this.widgets.dataTable = new YAHOO.widget.DataTable(this.id + "-members", columnDefinitions, this.widgets.dataSource,
         {
            renderLoopSize: 32,
            initialLoad: false
         });
         
         // Override abstract function within DataTable to set custom error message
         this.widgets.dataTable.doBeforeLoadData = function SiteFinder_doBeforeLoadData(sRequest, oResponse, oPayload)
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
                     Alfresco.util.message("message.empty", "Alfresco.SiteMembers") + '</span>';
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
         // make ajax call to site service to join user
         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.PROXY_URI + "api/sites/" + this.options.siteId + "/memberships/" + user,
            method: "DELETE",
            successCallback:
            {
               fn: this._removeSuccess,
               obj: user,
               scope: this
            },
            failureMessage: Alfresco.util.message("site-members.remove-failure", "Alfresco.SiteMembers", user)
         });
      },
      
      /**
       * Callback handler used when a user is successfully removed from the site
       * 
       * @method _removeSuccess
       * @param response {object}
       * @param user {object}
       */
      _removeSuccess: function SiteMembers__removeSuccess(response, user)
      {
         // show popup message to confirm
         Alfresco.util.PopupManager.displayMessage(
         {
            text: Alfresco.util.message("site-members.remove-success", "Alfresco.SiteMembers", user)
         });
         
         // redo the search again to get updated info
         this.doSearch();
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
      _performSearch: function SiteFinder__performSearch(searchTerm)
      {
         // Reset the custom error messages
         this._setDefaultDataTableErrors();
         
         // Display loading message
         YAHOO.widget.DataTable.MSG_EMPTY = Alfresco.util.message("site-members.searching", "Alfresco.SiteMembers");
         
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
      _buildSearchParams: function SiteFinder__buildSearchParams(searchTerm)
      {
         var params = YAHOO.lang.substitute("size={maxResults}",
         {
            maxResults : this.options.maxResults
         });

         return params;
      }
   };
})();
