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
 * Records Search component.
 * 
 * @namespace Alfresco
 * @class Alfresco.RecordsPermissions
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
       Event = YAHOO.util.Event;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;
   
   /**
    * Search constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.RecordsPermissions} The new RecordsPermissions instance
    * @constructor
    */
   Alfresco.RecordsPermissions = function(htmlId)
   {
      /* Super class constructor call */
      Alfresco.RecordsPermissions.superclass.constructor.call(
         this, "Alfresco.RecordsPermissions", htmlId,
         ["button", "container", "datasource", "datatable", "json", "menu"]);
      
      return this;
   };
   
   YAHOO.extend(Alfresco.RecordsPermissions, Alfresco.component.Base,
   {
      /**
       * Object container for storing YUI menu instances, indexed by property name.
       * 
       * @property modifyMenus
       * @type object
       */
      modifyMenus: null,
      
      /**
       * Object container for storing YUI button instances, indexed by property name.
       * 
       * @property removeButtons
       * @type object
       */
      removeButtons: null,
      
      /**
       * Array of objects representing the permissions list as displayed.
       * Of the form:
       * {
       *    "authority": "GROUP|USERNAME",
       *    "id": "PERMISSIONID",
       *    "remove": BOOLEAN,
       *    "modified": BOOLEAN,
       *    "el": DOMELEMENT
       * }
       * 
       * @property permissions
       * @type Array
       */
      permissions: null,
      
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function RecordsPermissions_onReady()
      {
         var me = this;
         
         // Buttons
         this.widgets.addButton = Alfresco.util.createYUIButton(this, "addusergroup-button", this.onAddClick);
         this.widgets.finishButton = Alfresco.util.createYUIButton(this, "finish-button", this.onFinishClick);
         
         // Events
         Event.on(this.id + "-inherit", "change", this.onInheritCheckChanged, this, true);
         
         // Load in the Authority Finder component from the server
         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.URL_SERVICECONTEXT + "components/people-finder/authority-finder",
            dataObj:
            {
               htmlid: this.id + "-authoritypicker"
            },
            successCallback:
            {
               fn: this.onAuthorityFinderLoaded,
               scope: this
            },
            failureMessage: this.msg("message.authoritypickerfail"),
            execScripts: true
         });
         
         // initial update of the UI
         this.refreshPermissionsList();
      },
      
      /**
       * Called when the authority finder template has been loaded.
       * Creates a dialog and inserts the authority finder for choosing groups and users to add.
       *
       * @method onAuthorityFinderLoaded
       * @param response The server response
       */
      onAuthorityFinderLoaded: function RecordsPermissions_onAuthorityFinderLoaded(response)
      {
         // Inject the component from the XHR request into it's placeholder DIV element
         var finderDiv = Dom.get(this.id + "-authoritypicker");
         finderDiv.innerHTML = response.serverResponse.responseText;
         
         this.widgets.authorityFinder = finderDiv;
         
         // Find the Authority Finder by container ID
         this.modules.authorityFinder = Alfresco.util.ComponentManager.get(this.id + "-authoritypicker");
         
         // Set the correct options for our use
         this.modules.authorityFinder.setOptions(
         {
            viewMode: Alfresco.AuthorityFinder.VIEW_MODE_COMPACT,
            singleSelectMode: true,
            showSelf: true,
            minSearchTermLength: 3
         });
         
         // Make sure we listen for events when the user selects an authority
         YAHOO.Bubbling.on("itemSelected", this.onAuthoritySelected, this);
      },
      
      /**
       * Authority selected event handler.
       * This event is fired from Group picker - so we much ensure
       * the event is for the current panel by checking panel visibility.
       *
       * @method onGroupSelected
       * @param e DomEvent
       * @param args Event parameters (depends on event type)
       */
      onAuthoritySelected: function ViewPanelHandler_onAuthoritySelected(e, args)
      {
         //alert(args[1].itemName);
         Dom.removeClass(this.widgets.authorityFinder, "active");
         this.showingFilter = false;
      },
      
      /**
       * Refresh the permissions list.
       * 
       * @method refreshPermissionsList
       */
      refreshPermissionsList: function RecordsPermissions_refreshPermissionsList()
      {
         // clear the list of meta-data items
         var elPermList = Dom.get(this.id + "-list");
         elPermList.innerHTML = "";
         
         // reset widget references
         this.modifyMenus = {};
         this.removeButtons = {};
         
         // perform ajax call to get the current permissions for the node
         Alfresco.util.Ajax.request(
         {
            method: Alfresco.util.Ajax.GET,
            url: Alfresco.constants.PROXY_URI + "api/node/" + this.options.nodeRef.replace(":/", "") + "/rmpermissions",
            successCallback:
            {
               fn: this.onPermissionsLoaded,
               scope: this
            },
            failureCallback:
            {
               fn: function()
               {
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: this.msg("message.getpermissionsfail")
                  });
               },
               scope: this
            }
         });
      },
      
      /**
       * Permissions list - ajax handler callback
       *
       * @method onPermissionsLoaded
       * @param res {object} Response
       */
      onPermissionsLoaded: function RecordsPermissions_onPermissionsLoaded(res)
      {
         // clear the list of local permissions
         this.permissions = [];
         
         var json = Alfresco.util.parseJSON(res.serverResponse.responseText);
         var perms = json.data.permissions;
         for (var i in perms)
         {
            this.addPermissionRow(perms[i]);
         }
      },
      
      /**
       * Add a row to the list of permissions. Also updates the internal local
       * permission object list.
       * Expects a permission object descriptor:
       * {
       *    "id": "Filing",
       *    "authority":
       *    {
       *       "id": "GROUP_ALFRESCO_ADMINISTRATORS",
       *       "label": "ALFRESCO_ADMINISTRATORS"
       *    },
       *    "inherited": false
       * }
       * Generally provided via JSON call or created for a new permission.
       * 
       * @method addPermissionRow
       * @param permission {object} See above
       */
      addPermissionRow: function RecordsPermissions_addPermissionRow(permission)
      {
         var me = this;
         
         var elPermList = Dom.get(this.id + "-list");
         
         // build row item for the permission and controls
         var div = document.createElement("div");
         
         // construct local permission reference from current data
         var i = this.permissions.length;
         var p =
         {
            "authority": permission.authority.id,
            "id": permission.id,
            "remove": false,
            "modified": false,
            "el": div
         };
         this.permissions.push(p);
         
         // dynamically generated button ids
         var modifyMenuContainerId = this.id + '-edit-' + i;
         var removeBtnContainerId  = this.id + '-remove-' + i;
         
         // messages
         var msgReadOnly = this.msg("label.readonly");
         var msgReadFile = this.msg("label.readandfile");
         var msgInherited = this.msg("label.inherited");
         var msgLocal = this.msg("label.local");
         
         // construct row data
         var allowModify = false;
         var allowRemove = false;
         var html = '<div class="list-item"><div class="controls">';
         if (permission.inherited)
         {
            // inherited - additional permissions can be granted if not already at maximum
            if (permission.id !== "Filing")
            {
               html += '<div class="actions"></div><span id="' + modifyMenuContainerId + '"></span>';
               allowModify = true;
            }
            else
            {
               html += '<div class="actions"></div><div class="readonly-label">' + (permission.id === "Filing" ? msgReadFile : msgReadOnly) + '</div>';
            }
         }
         else
         {
            // directly applied permission - can modify or remove
            html += '<div class="actions"><span id="' + removeBtnContainerId + '"></span></div><span id="' + modifyMenuContainerId + '"></span>';
            allowModify = true;
            allowRemove = true;
         }
         html += '</div><span class="label">' + $html(permission.authority.label) + '</span>';
         html += '<span class="hint-label">(' + (permission.inherited ? msgInherited : msgLocal) + ')</span>';
         html += '</div>';
         
         div.innerHTML = html;
         
         // insert into the DOM for display
         elPermList.appendChild(div);
         
         // generate menu and buttons (NOTE: must occur after DOM insertion)
         this.modifyMenus[i] = new YAHOO.widget.Button(
         {
            type: "menu",
            container: modifyMenuContainerId,
            menu: [
               { text: msgReadOnly, value: "ReadRecords" },
               { text: msgReadFile, value: "Filing" }
            ]
         });
         // set menu button text on current permission
         this.modifyMenus[i].set("label", (permission.id === "Filing" ? msgReadFile : msgReadOnly));
         // subscribe to the menu click event
         this.modifyMenus[i].getMenu().subscribe("click", function(p_sType, p_aArgs, index)
         {
            var menuItem = p_aArgs[1];
            if (menuItem)
            {
               me.modifyMenus[index].set("label", menuItem.cfg.getProperty("text"));
               // TODO: update modified permissions value
               alert(menuItem.value);
            }
         }, i);
         
         this.removeButtons[i] = new YAHOO.widget.Button(
         {
            type: "button",
            label: this.msg("button.remove"),
            name: this.id + '-removeButton-' + i,
            container: removeBtnContainerId,
            onclick:
            {
               fn: this.onClickRemovePermission,
               obj: i,
               scope: this
            }
         });
      },
      
      /**
       * Remove Permission button click handler
       *
       * @method onClickRemovePermission
       * @param e {object} DomEvent
       * @param obj {object} Object passed back from addListener method
       */
      onClickRemovePermission: function RecordsPermissions_onClickRemovePermission(e, obj)
      {
         // mark as removed and clear related DOM element
         var permission = this.permissions[obj];
         permission.remove = true;
         permission.el.parentNode.removeChild(permission.el);
         permission.el = null;
      },
      
      /**
       * Fired when the Add User/Group button is clicked.
       * 
       * @method onAddClick
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onAddClick: function RecordsPermissions_onAddClick(e, args)
      {
         if (!this.showingFilter)
         {
            Dom.addClass(this.widgets.authorityFinder, "active");
            this.modules.authorityFinder.clearResults();
            this.showingFilter = true;            
         }
         else
         {
            Dom.removeClass(this.widgets.authorityFinder, "active");
            this.showingFilter = false;
         }
      },
      
      /**
       * Fired when the Inherit Permissions checkbox state is changed.
       * 
       * @method onInheritCheckChanged
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onInheritCheckChanged: function RecordsPermissions_onInheritCheckChanged(e, args)
      {
      },
      
      /**
       * Fired when the Finish button is clicked.
       * 
       * @method onFinishClick
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onFinishClick: function RecordsPermissions_onFinishClick(e, args)
      {
      }
   });
})();