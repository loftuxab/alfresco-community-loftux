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
      /* Mandatory properties */
      this.name = "Alfresco.InvitationList";
      this.id = htmlId;
      
      /* Initialise prototype properties */
      this.widgets = {};
      this.listWidgets = [];
      
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
         siteId: "",
         
         /**
          * Available roles for the site.
          */
         roles: [],
      },

      /**
       * Object container for storing YUI widget instances.
       * 
       * @property widgets
       * @type object
       */
      widgets: null,
      
      /**
       * Object container for storing YUI widget instances used in the list cells
       */
      listWidgets: null,
      
      /** Auto-incremented unique id for each element added to the
       * tabel.
       */
      uniqueRecordId : 1,
      
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
         this.widgets.dataSource = new YAHOO.util.DataSource( [ ] ); 
         this.widgets.dataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY; 
         this.widgets.dataSource.responseSchema = { 
            fields: ['id', 'firstName', 'lastName', 'email']
         };
         
         // setup of the datatable
         this._setupDataTable();

         // make sure the invite button is initially disabled
         this._enableDisableInviteButton();

         // Hook remove invitee action handler
         var me = this;
         var fnRemoveInviteeHandler = function InvitationList_fnRemoveInviteeHandler(layer, args)
         {
            // call the remove method
            me.removeInvitee.call(me, args[1].anchor);
            args[1].stop = true;
            return true;
         }
         YAHOO.Bubbling.addDefaultAction("remove-item-button", fnRemoveInviteeHandler);
         
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
            var name = oRecord.getData("firstName") + " " + oRecord.getData("lastName");
            var email = oRecord.getData("email");
            var desc = "";
            desc = '<h3 class="itemname">' + Alfresco.util.encodeHTML(name) + '</h3>';
            desc += '<div class="detail">';
            desc += Alfresco.util.encodeHTML(email);
            desc += '</div>';
            elCell.innerHTML = desc;
         };

         /**
          * Role selector datacell formatter
          *
          * @method renderCellRole
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
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
                        record: oRecord,
                        role: role
                     },
                     scope: me
                  }
               }
               );
            }

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

         /**
          * Remove user datacell formatter
          *
          * @method renderCellRemoveButton
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
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

      /**
       * Returns the role label for a given record.
       */
      getRoleLabel: function(record)
      {
         if (record.getData("role") != undefined)
         {
            return this._msg('role.' + record.getData("role"));
         }
         else
         {
            return this._msg("invitationlist.selectrole");
         }
      },
      
      /**
       * Adds an invite to the list.
       * This function is called through a bubble event
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
       * Remove invitee action handler
       */
      removeInvitee: function InvitationList_removeInvitee(owner)
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
         var x = 10; // strange: first access to p_obj fails
         var role = p_obj.role;
         var record = p_obj.record;
         this._setRoleForRecord(record, role);
         
         // update the invite button
         this._enableDisableInviteButton();
         
         var eventTarget = aArgs[1];
         Event.preventDefault(domEvent);
      },
      
      /**
       * Implementation of set all roles functionality
       */
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
      
      /**
       * Enables or disables the invite button.
       * The invite button is only enabled if a role has been selected for all invitees
       */
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

         // disable button
         this.widgets.inviteButton.set("disabled", true);

         // show a wait message
         this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
         {
            text: this._msg("message.wait"),
            spanClass: "wait",
            displayTime: 0
         });
         
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
         
         // kick off the processing
         this._processInviteData(inviteData);
      },
      
      /**
       * Processes the invite data.
       */
      _processInviteData: function(inviteData)
      {   
         // check if we are already done
         if (inviteData.index >= inviteData.size)
         {  
            this._finalizeInvites(inviteData);
            return;
         }
         else
         {
            this._doInviteUser(inviteData);
         }
      },
      
      /**
       * Invites one user and returns to _processInviteData on completion.
       * 
       * @param inviteData data about all invites including the info which invite should be processed
       */
      _doInviteUser: function InvitationList__doInviteUser(inviteData)
      {
         // success handler
         var success = function InvitationList__doInviteUser_success(response)
         {
            inviteData.successes.push(inviteData.index);
            inviteData.index++;
            this._processInviteData(inviteData);
         };

         var failure = function InvitationList__doInviteUser_failure(response)
         {
            inviteData.failures.push(inviteData.index);
            inviteData.index++;
            this._processInviteData(inviteData);
         };
          
         // fetch the record to process
         var record = inviteData.recs[inviteData.index];
         var firstName = record.getData('firstName');
         var lastName = record.getData('lastName');
         var email = record.getData('email');
         var role = record.getData('role');
         
         // We have to do a backend call for each invited person
         var serverPath = window.location.protocol + "//" + window.location.host + Alfresco.constants.URL_CONTEXT;
         Alfresco.util.Ajax.request(
         {
            method: "GET",
            url: Alfresco.constants.PROXY_URI + "api/invite/start",
            dataObj:
            {
               inviteeFirstName: firstName,
               inviteeLastName: lastName,
               inviteeEmail: email,
               siteShortName : this.options.siteId,
               inviteeSiteRole : role,
               serverPath : serverPath,
               acceptUrl : 'page/accept-invite',
               rejectUrl : 'page/reject-invite'
            },
            successCallback:
            {
               fn: success,
               scope: this
            },
            failureCallback:
            {
               fn: failure,
               scope: this
            }
         });
      },

      /**
       * Called when all invites have been processed
       */
      _finalizeInvites: function(inviteData)
      {  
         // remove the entries that were successful
         for (var x=inviteData.successes.length - 1; x >= 0; x--)
         {
            this.widgets.dataTable.deleteRow(inviteData.successes[x]);
         }
         
         // remove wait message
         this.widgets.feedbackMessage.destroy();
         
         // inform the user
         var message = this._msg("message.inviteresult", inviteData.successes.length, inviteData.failures.length);
         Alfresco.util.PopupManager.displayMessage({text: message });
         
         // re-enable invite button
         this.widgets.inviteButton.set("disabled", false);
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
