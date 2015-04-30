/**
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * ManageUsers component.
 *
 * @namespace Alfresco
 * @class Alfresco.cloud.component.ManageUsers
 */
(function()
{

   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Selector = YAHOO.util.Selector;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;

   /**
    * ManageUsers constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.cloud.component.ManageUsers} The new ManageUsers instance
    * @constructor
    */
   Alfresco.cloud.component.ManageUsers = function ManageUsers_constructor(htmlId)
   {
      Alfresco.cloud.component.ManageUsers.superclass.constructor.call(this, "Alfresco.cloud.component.ManageUsers", htmlId, ["button", "menu", "container", "datasource", "datatable", "paginator", "json", "history"]);
      return this;
   };

   YAHOO.extend(Alfresco.cloud.component.ManageUsers, Alfresco.component.Base,
   {
      CSS: "cloud-manage-users",

      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       */
      options:
      {
         /**
          * Number of users to display at the same time
          *
          * @property maxItems
          * @type int
          * @default 50
          */
         maxItems: 50,

         /**
          * @property userFilters
          * @type Object
          */
         userFilters: {}
      },

      /**
       * Fired by YUI when parent element is available for scripting.
       * Initial History Manager event registration
       *
       * @method onReady
       */
      onReady: function CMU_onReady()
      {
         // Create new user button
         this.widgets.submitButton = Alfresco.util.createYUIButton(this, "newUser", this.onNewUserClick);

         // Create user list
         var url = Alfresco.constants.PROXY_URI + "internal/cloud/people?sortBy=firstName"; // + "&skipCount=0&maxItems=5&internal=true&networkAdmin=true";;
         this.widgets.pagingDataTable = new Alfresco.util.DataTable(
         {
            dataTable:
            {
               container: this.id + "-list",
               columnDefinitions:
               [
                  { key: "icon", sortable: false, formatter: this.bind(this.renderCellUserIcon), width: 80 },
                  { key: "title", sortable: false, formatter: this.bind(this.renderCellUserInfo) },
                  { key: "actions", sortable: false, formatter: this.bind(this.renderCellActions), width: 200 }
               ],
               config:
               {
                  MSG_EMPTY: this.msg("message.noUsers")
               }
            },
            dataSource:
            {
               url: url,
               defaultFilter:
               {
                  filterId: "users",
                  filterData: "all"
               },
               filterResolver: this.bind(function(filter)
               {
                  if (filter.filterId == "users")
                  {
                     return this.options.userFilters[filter.filterData];
                  }
               })
            },
            paginator:
            {
               hide: false,
               config:
               {
                  containers: [ this.id + "-paginator1", this.id + "-paginator2" ],
                  rowsPerPage: this.options.maxItems
               }
            }
         });

         // Create filter
         var userFilter = new Alfresco.component.BaseFilter("Alfresco.cloud.component.ManageUsers.UserFilter", this.id + "-filters");
         userFilter.setSelectedClass(this.CSS + "-filters-selected");
         userFilter.setFilterIds(["users"]);
      },

      onNewUserClick: function()
      {
         Alfresco.util.loadWebscript({
            url: Alfresco.constants.URL_SERVICECONTEXT + "cloud/core/components/console/create-users",
            properties:
            {
               source: "account-settings"
            }
         });
      },


      /**
       * DataTable Cell Renderers
       */

      /**
       * User icon custom data cell formatter
       *
       * @method renderCellAvatar
       * @param elCell {object}
       * @param oRecord {object}
       * @param oColumn {object}
       * @param oData {object|string}
       */
      renderCellUserIcon: function CMU_renderCellUserIcon(elCell, oRecord, oColumn, oData)
      {
         var avatarUrl = Alfresco.constants.URL_RESCONTEXT + "components/images/no-user-photo-64.png";
         if (oRecord.getData("avatar") !== undefined)
         {
            avatarUrl = Alfresco.constants.PROXY_URI + oRecord.getData("avatar") + "?c=queue&ph=true";
         }

         elCell.innerHTML = '<img class="avatar" src="' + avatarUrl + '" alt="avatar" />';
      },

      /**
       * User info custom data cell formatter
       *
       * @method CMU_renderCellTaskInfo
       * @param elCell {object}
       * @param oRecord {object}
       * @param oColumn {object}
       * @param oData {object|string}
       */
      renderCellUserInfo: function CMU_renderCellUserInfo(elCell, oRecord, oColumn, oData)
      {
         var user = oRecord.getData(),
            name = this.msg("link.userProfilePage", $html(user.firstName), $html(user.lastName)),
            jobTitle = $html(user.jobtitle),
            role = this.msg(user.isExternal ? 'role.invited' : (user.isNetworkAdmin ? 'role.admin' : 'role.member' )),
            email= $html(user.userName);

         var info = '<h3 class="' + this.CSS + '-info-title"><a href="' + Alfresco.util.uriTemplate("userprofilepage", { userid: user.userName }) + '" class="theme-color-1" title="' + this.msg("tooltip.usersProfilePage") + '">' + name + '</a></h3>';
         info += '<span class="' + this.CSS + '-info-field"><label>' + this.msg("label.email") + ':</label><span>' + (email ? email : this.msg("label.none")) + '</span></span>';
         info += '<span class="' + this.CSS + '-info-field"><label>' + this.msg("label.jobTitle") + ':</label><span>' + (jobTitle ? jobTitle : this.msg("label.none")) + '</span></span>';
         info += '<span class="' + this.CSS + '-info-field"><label>' + this.msg("label.role") + ':</label><span>' + (role ? role : this.msg("label.none")) + '</span></span>';
         elCell.innerHTML = info;
      },

      /**
       * Actions custom data cell formatter
       *
       * @method CMU_renderCellSelected
       * @param elCell {object}
       * @param oRecord {object}
       * @param oColumn {object}
       * @param oData {object|string}
       */
      renderCellActions: function CMU_renderCellActions(elCell, oRecord, oColumn, oData)
      {
         var user = oRecord.getData();
         if (user.userName !== Alfresco.constants.USERNAME)
         {
             if (!user.isExternal)
             {
                if (user.isNetworkAdmin)
                {
                   this.createAction(elCell, this.msg("action.demoteUser"), this.CSS + "-list-action-demote", this.onDemoteUserClick, user);
                }
                else
                {
                   this.createAction(elCell, this.msg("action.promoteUser"), this.CSS + "-list-action-promote", this.onPromoteUserClick, user);
                }
             }
             this.createAction(elCell, this.msg("action." + this.removeOrDeleteUser(user)), this.CSS + "-list-action-remove", this.onRemoveUserClick, user);
         }
      },

      /**
       * Demotes the clicked user to Network Member and raises a confirmation dialog if the current user is being demoted
       *
       * @method onDemoteUserClick
       * @param e {Object} Event info
       * @param user {Object} Info about the user that was clicked
       */
      onDemoteUserClick: function(e, user)
      {
         var me = this, u = user, successMsgKey = null;
         var demoteUser = function()
         {
            Alfresco.util.Ajax.jsonDelete(
            {
               url: Alfresco.constants.PROXY_URI + "internal/cloud/domains/" + encodeURIComponent(Alfresco.cloud.constants.CURRENT_TENANT) + "/account/networkadmins/" + encodeURIComponent(u.userName),
               successCallback:
               {
                  fn: function()
                  {
                     me.widgets.pagingDataTable.reloadDataTable();
                  }
               },
               successMessage: me.msg(successMsgKey),
               failureMessage: me.msg("message.demoteUser.failure")
            });
         };

         // Demote user
         successMsgKey = "message.demoteUser.success";
         demoteUser();
         
         Event.stopEvent(e);
      },

      /**
       * Promotes the clicked user to Network Admin
       *
       * @method onPromoteUserClick
       * @param e {Object} Event info
       * @param user {Object} Info about the user that was clicked
       */
      onPromoteUserClick: function(e, user)
      {
         // Promote user to Network Admin
         Alfresco.util.Ajax.jsonPost(
         {
            url: Alfresco.constants.PROXY_URI + "internal/cloud/domains/" + encodeURIComponent(Alfresco.cloud.constants.CURRENT_TENANT) + "/account/networkadmins",
            dataObj:
            {
               username: user.userName
            },
            successCallback:
            {
               fn: function()
               {
                  this.widgets.pagingDataTable.reloadDataTable();
               },
               scope: this
            },
            successMessage: this.msg("message.promoteUser.success"),
            failureMessage: this.msg("message.promoteUser.failure")
         });
         Event.stopEvent(e);
      },

      /**
       * Removes the clicked user
       *
       * @method onRemoveUserClick
       * @param e {Object} Event info
       * @param user {Object} Info about the user that was clicked
       */
      onRemoveUserClick: function(e, user)
      {
         var me = this, u = user;
         Alfresco.util.PopupManager.displayPrompt(
         {
            title: this.msg("message." + this.removeOrDeleteUser(u) + ".title"),
            text: this.msg("message." + this.removeOrDeleteUser(u) + ".description", user.firstName, user.lastName),
            buttons: [
            {
               text: this.msg("button." + this.removeOrDeleteUser(u)),
               handler: function CMU__onRemoveUserClick_delete()
               {
                  this.destroy();
                  Alfresco.util.Ajax.jsonDelete(
                  {
                     url: Alfresco.constants.PROXY_URI + "internal/cloud/domains/" + encodeURIComponent(Alfresco.cloud.constants.CURRENT_TENANT) + "/account/users/" + encodeURIComponent(u.userName),
                     successCallback:
                     {
                        fn: function()
                        {
                           me.widgets.pagingDataTable.reloadDataTable();
                        }
                     },
                     successMessage: me.msg("message." + me.removeOrDeleteUser(u) + ".success"),
                     failureCallback:
                     {
                        fn: function(response)
                        {
                           // Display error message
                           var defaultMsgKey = "message." + me.removeOrDeleteUser(u) + ".failure",
                              statusMsgKey = defaultMsgKey + "." + response.serverResponse.status,
                              msg = me.msg(defaultMsgKey);
                           if (me.msg(statusMsgKey) != statusMsgKey)
                           {
                              msg = me.msg(statusMsgKey);
                           }
                           Alfresco.util.PopupManager.displayPrompt(
                           {
                              title: me.msg("message." + me.removeOrDeleteUser(u) + ".title"),
                              text: msg
                           });
                        }
                     }
                  });
               }
            },
            {
               text: this.msg("button.cancel"),
               handler: function CMU__onRemoveUserClick_cancel()
               {
                  this.destroy();
               },
               isDefault: true
            }]
         });
         Event.stopEvent(e);
      },

      /**
       * @method createAction
       * @param label
       * @param css
       * @param action
       * @param oRecord
       */
      createAction: function WA_createAction(elCell, label, css, action, oRecord)
      {
         var div = document.createElement("div"),
            actionCss = this.CSS + "-list-action";
         Dom.addClass(div, actionCss);
         Dom.addClass(div, css);
         div.onmouseover = function()
         {
            Dom.addClass(this, actionCss + "-over");
         };
         div.onmouseout = function()
         {
            Dom.removeClass(this, actionCss + "-over");
         };
         var a = document.createElement("a");
         if (YAHOO.lang.isFunction(action))
         {
            Event.addListener(a, "click", action, oRecord, this);
            a.setAttribute("href", "#");
         }
         else
         {
            a.setAttribute("href", action);
         }
         var span = document.createElement("span");
         Dom.addClass(span, "theme-color-1");
         span.appendChild(document.createTextNode(label));
         a.appendChild(span);
         div.appendChild(a);
         elCell.appendChild(div);
      },

      /**
       * Helper for constructing remove user message keys based on whether user is
       * external or not. 
       */
      removeOrDeleteUser : function(user)
      {
         return user.isExternal ? "removeUser" : "deleteUser";
      }
   });

})();
                                         