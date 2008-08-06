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
 * InvitationList component.
 * 
 * @namespace Alfresco
 * @class Alfresco.InvitationList
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
    * InvitationList constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.InvitationList} The new InvitationList instance
    * @constructor
    */
   Alfresco.InvitationList = function(htmlId)
   {
      this.name = "Alfresco.InvitationList";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "container", "datasource", "datatable", "json"], this.onComponentsLoaded, this);
      
      // Decoupled event listeners
      YAHOO.Bubbling.on("personSelected", this.onAddInvite, this);
   
      return this;
   }
   
   Alfresco.InvitationList.prototype =
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
          * siteId to InvitationList in. "" if InvitationList should be cross-site
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
      
      listWidgets: [],
      
      /** Auto-incremented unique id for each element added to the
       * tabel.
       */
      uniqueRecordId : 1,
      
      actionsColumnTemplate : null,
      
      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.InvitationList} returns 'this' for method chaining
       */
      setOptions: function InvitationList_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },
      
      /**
       * Set messages for this component.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.InvitationList} returns 'this' for method chaining
       */
      setMessages: function InvitationList_setMessages(obj)
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
      onComponentsLoaded: function InvitationList_onComponentsLoaded()
      {
         Event.onContentReady(this.id, this.onReady, this, true);
      },
   
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function InvitationList_onReady()
      {   
         // button to invite all people in the list 
         this.widgets.inviteButton = Alfresco.util.createYUIButton(this, "invite-button", this.inviteButtonClick);
         
         // File Select menu button
         this.widgets.allRolesSelect = Alfresco.util.createYUIButton(this, "selectallroles-button", this.onSelectAllRoles,
         {
            type: "menu", 
            menu: "selectallroles-menu"
         });
         
         // setup the datasource
         // fields of data are available in the RecordSet 
         this.widgets.dataSource = new YAHOO.util.DataSource( [ ] ); 
         this.widgets.dataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY; 
         this.widgets.dataSource.responseSchema = { 
            fields: ['id', 'firstName', 'lastName', 'email']
         };
         
         // setup of the datatable
         this._setupDataTable();

         this._enableDisableInviteButton();

         // Hook action events
         // TODO: cleanup
         Alfresco.util.myRegisterDefaultActionHandler(this.id, "remove-item-button", "span", this);
         
         // show the component now, this avoids painting issues of the dropdown button
         Dom.setStyle(this.id + "-invitationBar", "visibility", "visible");
         
      },
      
      _setupDataTable: function InvitationList_setupDataTable()
      {
         /**
          * DataTable Cell Renderers
          *
          * Each cell has a custom renderer defined as a custom function. See YUI documentation for details.
          * These MUST be inline in order to have access to the Alfresco.InvitationList class (via the "me" variable).
          */
         var me = this;

         /**
          * Description/detail custom datacell formatter
          *
          * @method renderCellDescription
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         renderCellDescription = function InvitationList_renderCellDescription(elCell, oRecord, oColumn, oData)
         {
            // we currently render all results the same way
            var name = oRecord.getData("firstName") + " " + oRecord.getData("lastName")
            var email = oRecord.getData("email");
            var desc = "";
            desc = '<h3 class="itemname">' + Alfresco.util.encodeHTML(name) + '</a></h3>';
            desc += '<div class="detail">';
            desc += Alfresco.util.encodeHTML(email);
            desc += '</div>';
            elCell.innerHTML = desc;
         };

         renderCellRole = function InvitationList_renderCellActions(elCell, oRecord, oColumn, oData)
         {  
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

            // cell where to add the element
            var cell = new YAHOO.util.Element(elCell);
            
            var id = oRecord.getData('id');
            var buttonId = me.id + '-roleselector-' + id;
            
            // create a clone of the template
            var actionsColumnTemplate = Dom.get(me.id + '-role-column-template');
            var templateInstance = actionsColumnTemplate.cloneNode(true);
            templateInstance.setAttribute("id", "actionsDiv" + id);
            Dom.setStyle(templateInstance, "display", "");

            // define the role dropdown menu and the event listeners
            var rolesMenu = [
               {
                  text: me._msg("role.siteconsumer"), value: "consumer", onclick: {
                     fn: me.onRoleSelect, obj: { record: oRecord, role: "consumer" }, scope: me
                  }
               },
               {
                  text: me._msg("role.sitecollaborator"), value: "collaborator", onclick: {
                     fn: me.onRoleSelect, obj: { record: oRecord, role: "collaborator" }, scope: me
                  }
               },
               {
                  text: me._msg("role.sitemanager"), value: "manager", onclick: {
                     fn: me.onRoleSelect, obj: { record: oRecord, role: "manager" }, scope: me
                  }
               }
            ];

            // Insert the templateInstance to the column.
            cell.appendChild (templateInstance);

            // Create a yui button for the role selector.
            var fButton = Dom.getElementsByClassName("role-selector-button", "button", templateInstance);
            var button = new YAHOO.widget.Button(fButton[0],
            {
               type: "menu",
               name: buttonId,
               label: me.getRoleLabel(oRecord),
               menu: rolesMenu
            });
            me.listWidgets[id] = { button: button };
         };

         renderCellRemoveButton = function InvitationList_renderCellRemoveButton(elCell, oRecord, oColumn, oData)
         {  
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

            var desc =
               '<span id="'+me.id+'-removeInvitee">' +
               '  <a href="#" class="remove-item-button"><span class="removeIcon">&nbsp;</span></a>' +
               '</span>';
            elCell.innerHTML = desc;
         };

         // DataTable column defintions
         var columnDefinitions = [
         {
            key: "user", label: "User", sortable: false, formatter: renderCellDescription
         },
         {
            key: "role", label: "Role", sortable: false, formatter: renderCellRole, width: 140
         },
         {
            key: "remove", label: "Remove", sortable: false, formatter: renderCellRemoveButton, width: 30
         }];

         // DataTable definition
         YAHOO.widget.DataTable.MSG_EMPTY = ""; // "First add users to this list";  //msg("message.empty", "Alfresco.InvitationList");
         this.widgets.dataTable = new YAHOO.widget.DataTable(this.id + "-inviteelist", columnDefinitions, this.widgets.dataSource,
         {
            renderLoopSize: 32
         });
      },


      getRoleLabel: function(record)
      {
         var roleName = "";
         if (record.getData("role") != undefined)
         {
            roleName = record.getData("role");
         }
         switch(roleName)
         {
            case "":
               return this._msg("invitationlist.selectrole");
            case "consumer":
               return this._msg("role.siteconsumer");
            case "collaborator":
               return this._msg("role.sitecollaborator");
            case "manager":
               return this._msg("role.sitemanager");
            default:
               return "<unknown role>";
         }
      },
      
      /**
       * Returns the role name as expected by the rest API.
       */
      getRoleName: function(record)
      {
         var roleName = "";
         if (record.getData("role") != undefined)
         {
            roleName = record.getData("role");
         }
         switch(roleName)
         {
            case "collaborator":
               return "SiteCollaborator";
            case "manager":
               return "SiteManager";
            case "consumer":
            default:
               return "SiteConsumer";
         }
      },
      
      /**
       * Triggered by the InvitationList all/site only link
       */
      toggleInvitationListScope: function InvitationList_switchInvitationListScope()
      {
         var InvitationListAll = ! this.InvitationListAll;
         // send a InvitationList bubble event to load the list
         YAHOO.Bubbling.fire("onInvitationList",
         {
            InvitationListAll : InvitationListAll
         });
      },
      
      /**
       * BubbleEvent:
       * Called by the other components to add invitees to the list of invites
       */ 
      onAddInvite: function Invitationlist_onAddInvite(layer, args)
      {   
         var data = args[1];
         var inviteData = {};
         inviteData.id = this.uniqueRecordId++;
         inviteData.userName = data.userName;
         inviteData.firstName = data.firstName;
         inviteData.lastName = data.lastName;
         inviteData.email = data.email;
         this.widgets.dataTable.addRow(inviteData);
         this._enableDisableInviteButton();
      },
      
      /**
       * DefaultActionListener:
       * Called when the user clicks on the "remove invitee" button
       */
      removeInvitee: function InvitationList_removeInvitee(owner, param)
      {
         // find the correct row
         var recordId = this.widgets.dataTable.getRecordIndex(owner);
         
         // Fire the personDeselected event
         YAHOO.Bubbling.fire("personDeselected",
         {
            userName: this.widgets.dataTable.getRecord(recordId).getData("userName")
         });

         // remove the element, but first set the empty message (which is static,
         // thus shared by this table and the one to find users)
         YAHOO.widget.DataTable.MSG_EMPTY = "";
         this.widgets.dataTable.deleteRow(recordId);
         this._enableDisableInviteButton();
      },
      
      /**
       * Select all roles dropdown
       */
      onSelectAllRoles: function DL_onFileSelect(sType, aArgs, p_obj)
      {
         var value = aArgs[1].value;
         if (value == "")
         {
            return;
         }
         
         this._setAllRolesImpl(value);
         this._enableDisableInviteButton();
         var eventTarget = aArgs[1];
         Event.preventDefault(domEvent);
      },
      
      /**
       * Called when the user select a role in the role dropdown
       * @parma p_obj: object containing record and role to set
       */
      onRoleSelect: function InvitationList_onRoleSelect(sType, aArgs, p_obj)
      {
         // set the role for the passed record
         var selectedRole = p_obj.role;
         var x = 10; // PENDING: why does the first access to p_obj fail?!?
         var role = p_obj.role;
         var record = p_obj.record;
         this._setRoleForRecord(record, role);
         
         // update the invite button
         this._enableDisableInviteButton();
         
         var eventTarget = aArgs[1];
         Event.preventDefault(domEvent);
      },
      
      _setAllRolesImpl: function(roleName)
      {
         var recordSet = this.widgets.dataTable.getRecordSet();
         for (var x=0; x < recordSet.getLength(); x++)
         {
            var record = recordSet.getRecord(x);
            this._setRoleForRecord(record, roleName);
         }
         
         // update the invite button
         this._enableDisableInviteButton();
      },
      
      /**
       * Sets the role for a given record
       */
      _setRoleForRecord: function(record, role)
      {
         // set the new role
         record.setData('role', role);
          
         // update the button
         this.listWidgets[record.getData('id')].button.set('label', this.getRoleLabel(record));   
      },
      
      /**
       * Returns whether all invitees have their role set correctly
       */
      _checkAllRolesSet: function InvitationList__checkRolesSet()
      {
         var recordSet = this.widgets.dataTable.getRecordSet();
         for (var x=0; x < recordSet.getLength(); x++)
         {
            var record = recordSet.getRecord(x);
            if (record.getData("role") == undefined)
            {
               return false;
            }
         }
         return true;
      },
      
      _enableDisableInviteButton: function InvitationList__enableDisableInviteButton()
      {
         var enable = this.widgets.dataTable.getRecordSet().getLength() > 0 &&
                      this._checkAllRolesSet();
         this.widgets.inviteButton.set("disabled", ! enable);
      },
      
      /**
       * Initiates the invite process
       */
      inviteButtonClick: function InvitationList_inviteButtonClick(e, p_obj)
      {
         // sanity check - the invite button shouldn't be clickable in this case
         var recordSet = this.widgets.dataTable.getRecordSet();
         if (recordSet.getLength() < 0 || ! this._checkAllRolesSet())
         {
            this._enableDisableInviteButton();
            return;
         }

         Alfresco.util.PopupManager.displayMessage({text: this._msg("invitationlist.pleasewait")});
         
         // copy over all records
         var recs = [];
         for (var i=0; i < recordSet.getLength(); i++)
         {
            recs.push(recordSet.getRecord(i));
         }
         var inviteData = {
             recs: recs,
             size : recs.length,
             index: 0,
             successes: [],
             failures: []
         };
         
         this._processInviteData(inviteData);
      },
      
      _processInviteData: function(inviteData)
      {   
         // check if we are already done
         if (inviteData.index >= inviteData.size)
         {  
            this._finalizeInvites(inviteData);
            return;
         }
         
         // check the current entry to see what we have to do
         var recs = inviteData.recs;
         var currentRecord = recs[inviteData.index];
         if (currentRecord.getData('userName') != undefined)
         {
            // set got a username, a simply join will be enough
            this._doJoinExistingUser(inviteData);
         }
         else
         {
            this._doInviteNewUser(inviteData);
         }
      },
      
      _finalizeInvites: function(inviteData)
      {  
         // remove the entries that were successful
         for (var x=inviteData.successes.length - 1; x >= 0; x--)
         {
            this.widgets.dataTable.deleteRow(inviteData.successes[x]);
         }
         
         // inform the user
         var message = this._msg(this._msg("invitationlist.inviteresult"), inviteData.successes.length, inviteData.failures.length);
         Alfresco.util.PopupManager.displayMessage({text: message });
      },
      
      _doInviteNewUser: function(inviteData)
      {
         // fetch the record to process
         var record = inviteData.recs[inviteData.index];
         var firstName = record.getData('firstName');
         var lastName = record.getData('lastName');
         var email = record.getData('email');
         
         // We have to do a backend call for each invited person
         Alfresco.util.Ajax.request(
         {
            method: "GET",
            url: Alfresco.constants.PROXY_URI + "api/invite/start",
            dataObj:
            {
               inviteeFirstName: firstName,
               inviteeLastName: lastName,
               inviteeEmail: email,
               siteShortName : this.options.siteId
            },
            successCallback:
            {
               fn: this._successCallback,
               obj: inviteData,
               scope: this
            },
            failureCallback:
            {
               fn: this._failureCallback,
               obj: inviteData,
               scope: this
            }
         });
      },
      
      _doJoinExistingUser: function(inviteData)
      {
         // fetch the record to process
         var record = inviteData.recs[inviteData.index];
         
         var user = record.getData('userName');
         var role = this.getRoleName(record);
         var site = this.options.siteId;
         
         // make ajax call to site service to join user
         Alfresco.util.Ajax.jsonRequest(
         {
            url: Alfresco.constants.PROXY_URI + "api/sites/" + site + "/memberships/" + encodeURIComponent(user),
            method: "PUT",
            dataObj:
            {
               role: role,
               person:
               {
                  userName: user,
                  url: "/alfresco/service/api/people/" + encodeURIComponent(user)
               },
               url: "/alfresco/service/api/sites/" + site + "/memberships/" + encodeURIComponent(user)
            },
            successCallback:
            {
               fn: this._successCallback,
               obj: inviteData,
               scope: this
            },
            failureCallback:
            {
               fn: this._failureCallback,
               obj: inviteData,
               scope: this
            }
         });
      },
      
      _successCallback: function(response, inviteData)
      {
         inviteData.successes.push(inviteData.index);
         inviteData.index++;
         this._processInviteData(inviteData);
      },
      
      _failureCallback: function(response, inviteData)
      {
         inviteData.failures.push(inviteData.index);
         inviteData.index++;
         this._processInviteData(inviteData);
      },

      /**
       * Gets a custom message
       *
       * @method _msg
       * @param messageId {string} The messageId to retrieve
       * @return {string} The custom message
       * @private
       */
      _msg: function InvitationList__msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, "Alfresco.InvitationList", Array.prototype.slice.call(arguments).slice(1));
      }

   };
})();


/**
 * Register a default action handler for a given set
 * of elements described by their class name.
 * @parma handlerObject object that is used as for the method calls
 * @param className The elements to which the action should be added to
 * @param ownerTagName the owner tag name to search for. This has to be a
 *        parent element of the default action element. The id of this element is used
 *        to call the correct method. Id's should follow the form htmlid-actionname[-param]
 *        Actions methods should have the form f(htmlid, ownerId, param)
 */
Alfresco.util.myRegisterDefaultActionHandler = function(htmlId, className, ownerTagName, handlerObject)
{         
   // Hook the tag events
   YAHOO.Bubbling.addDefaultAction(className,
      function TagLibrary_genericDefaultAction(layer, args)
      {
         var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, ownerTagName);
         if (owner !== null)
         {
            // check that the html id matches, abort otherwise
            var tmp = owner.id;
            if (tmp.indexOf(htmlId) != 0)
            {
               return true;
            }
            var tmp = tmp.substring(htmlId.length + 1);
            var parts = tmp.split('-');
            if (parts.length < 1)
            {
               // stop here
               return true;
            }
            // the first entry is the handler method to call
            var action = parts[0];
            if (typeof handlerObject[action] == "function")
            {
               // extract the param part of the id
               var param = parts.length > 1 ? tmp.substring(action.length + 1) : null;
               handlerObject[action].call(handlerObject, owner, param);
               args[1].stop = true;
            }
         }
         return true;
      }
   );
}
