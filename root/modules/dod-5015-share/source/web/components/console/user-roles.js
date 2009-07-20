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
 * Alfresco top-level RM namespace.
 * 
 * @namespace Alfresco
 * @class Alfresco.RM
 */
Alfresco.Admin = Alfresco.Admin || {};
Alfresco.Admin.RM = Alfresco.Admin.RM || {};

/**
 * RM Roles component
 * 
 * @namespace Alfresco
 * @class Alfresco.RM.References
 */
(function Admin_RM_Roles()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
       Event = YAHOO.util.Event,
       Sel = YAHOO.util.Selector;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML,
       $links = Alfresco.util.activateLinks;


   /**
    * RM Roles componentconstructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.dashlet.MyDocuments} The new component instance
    * @constructor
    */
   Alfresco.Admin.RM.Roles = function Admin_RM_ManageRoles_constructor(htmlId)
   {
      Alfresco.Admin.RM.Roles.superclass.constructor.call(this, "Alfresco.Admin.RM.ManageRoles", htmlId, []);     
      return this;
   };
    
    YAHOO.extend(Alfresco.Admin.RM.Roles, Alfresco.component.Base,
   {
      
      /**
       * Initialises event listening and custom events
       *  
       */
      initEvents : function Admin_RM_ManageRoles_initEvents()
      {
         //requires EventProvider
         //this.createEvent('newReference')
         
         Event.on(this.id,'submit',this.onInteractionEvent,null,this);
         Event.on(this.id,'click',this.onInteractionEvent,null,this);         
         
         this.registerEventHandler('submit','form#newRoleForm',{
            handler:this.onSubmit,
            scope:this
         });
         
         this.registerEventHandler('click','.cancel',{
            handler:this.onCancel,
            scope:this
         });
         
         this.registerEventHandler('click','.selectAll',{
            handler:this.onSelectAll,
            scope:this
         });               
         
         return this;
      },
      onSelectAll : function Admin_RM_ManageRoles_onSelectAll(e, args)
      {
         var elTarget = Event.getTarget(e), 
             checkedStatus = false, 
             id = elTarget.id.replace('SelectAll-button','');

         if (!Dom.hasClass(elTarget,'selected'))
         {
            checkedStatus = true;
            Dom.addClass(elTarget, 'selected');
            elTarget.innerHTML = this.msg('label.deselect-all');
         }
         else
         {
            checkedStatus = false;
            Dom.removeClass(elTarget, 'selected');
            elTarget.innerHTML = this.msg('label.select-all');
         }

         
         var cbs = Sel.query('input[type="checkbox"]',id+'Capabilities');
         for (var i=0,len=cbs.length;i<len;i++)
         {
            cbs[i].checked = checkedStatus;
         }
         
         YAHOO.util.Event.preventDefault(e);

      },
      /**
       * Validates forms and submits form
       *  
       */
      onSubmit : function Admin_RM_ManageRoles_onSubmitCreate(e, args)
      {         
         var isACheckboxSelected = false,
             cbs = Sel.query('input[type="checkbox"]',this.id);
 
         for (var i=0,len=cbs.length;i<len;i++)
         {
            if (cbs[i].checked)
            {
               isACheckboxSelected = true;
               break;
            }
         }
         if ( (Dom.get('roleName').value!='') && (isACheckboxSelected))
         {
            alert('valid');
         }
         else
         {
            alert('invalid');
            YAHOO.util.Event.preventDefault(e);
         }

      },
      onCancel : function Admin_RM_ManageRoles_onCancel(e, args)
      {
         YAHOO.util.Event.preventDefault(e);
      },
      /**
       * Fired by YUI when parent element is available for scripting
       * @method onReady
       * 
       */
      onReady: function Admin_RM_ManageRoles_onReady()
      {
         this.initEvents();
         var buttons = Sel.query('button',this.id).concat(Sel.query('input[type=submit]',this.id));

         // Create widget button while reassigning classname to src element (since YUI removes classes). 
         // We need the classname so we can identify what action to take when it is interacted with (event delegation).
         for (var i=0, len = buttons.length; i<len; i++)
         {
            var button= buttons[i];
            var id = button.id.replace(this.id+'-','');
            this.widgets[id] = new YAHOO.widget.Button(button.id);
            this.widgets[id]._button.className=button.className;
         }
      }

   });
})();


/**
 * RM View Roles component
 * 
 * @namespace Alfresco
 * @class Alfresco.RM.References
 */
(function Admin_RM_View_Roles()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
       Event = YAHOO.util.Event,
       Sel = YAHOO.util.Selector;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML,
       $links = Alfresco.util.activateLinks;


   /**
    * RM View Roles componentconstructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.dashlet.MyDocuments} The new component instance
    * @constructor
    */
   Alfresco.Admin.RM.ViewRoles = function Admin_RM_View_Roles_constructor(htmlId)
   {
      Alfresco.Admin.RM.ViewRoles.superclass.constructor.call(this, "Alfresco.Admin.RM.ManageRoles", htmlId, []);     
      return this;
   };
    
    YAHOO.extend(Alfresco.Admin.RM.ViewRoles, Alfresco.component.Base,
   {
      
      /**
       * Initialises event listening and custom events
       *  
       */
      initEvents : function Admin_RM_View_Roles_initEvents()
      {
         //requires EventProvider
         //this.createEvent('newReference')
         
         Event.on(this.id,'click',this.onInteractionEvent,null,this);         
         
         this.registerEventHandler('submit','form#newRoleForm',{
            handler:this.onSubmit,
            scope:this
         });

         this.registerEventHandler('click','button#newRole-button',{
            handler:this.onNewRole,
            scope:this
         });
         
         this.registerEventHandler('click','button#editRole-button',{
            handler:this.onEditRole,
            scope:this
         });

         this.registerEventHandler('click','button#deleteRole-button',{
            handler:this.onDeleteRole,
            scope:this
         });

         this.registerEventHandler('click','.role',{
            handler:this.onRoleSelect,
            scope:this
         });               
         return this;
      },

      /**
       * Fired by YUI when parent element is available for scripting
       * @method onReady
       * 
       */
      onReady: function Admin_RM_View_Roles_onReady()
      {
         this.initEvents();
         var buttons = Sel.query('button',this.id);

         // Create widget button while reassigning classname to src element (since YUI removes classes). 
         // We need the classname so we can identify what action to take when it is interacted with (event delegation).
         for (var i=0, len = buttons.length; i<len; i++)
         {
            var button= buttons[i];
            var id = button.id.replace(this.id+'-','');
            this.widgets[id] = new YAHOO.widget.Button(button.id);
            this.widgets[id]._button.className=button.className;
         }
      },
      onRoleSelect : function Admin_RM_View_Roles_onRoleSelect(e)
      {
        alert('switch tabs');
      },
      onNewRole: function Admin_RM_View_Roles_onNewRole(e)
      {
         window.location.href=Alfresco.constants.URL_CONTEXT+'page/console/admin-console/define-roles?action=new';
      },

      onEditRole: function Admin_RM_View_Roles_onEditRole(e)
      {
         var el = Event.getTarget(e);
         //get roleid from button value
         var roleId = this.widgets[el.id.replace('-button','')].get('value');
window.location.href=Alfresco.constants.URL_CONTEXT+'page/console/admin-console/define-roles?action=edit&roleId='+roleId;
      }, 

      onDeleteRole: function Admin_RM_View_Roles_onDeleteRole(e)
      {
         var el = Event.getTarget(e);
         var roleId = this.widgets[el.id.replace('-button','')];

         var performDelete = this.performDelete;
         var me = this;

         Alfresco.util.PopupManager.displayPrompt({
           title: this.msg('label.confirm-delete-title'),
           text: this.msg('label.confirm-delete-message'),
           modal: true, 
           close: true, 
           buttons: [
              {
               text: this.msg("button.ok"),
               handler: function Admin_RM_View_Roles_performDelete()
               {
                  this.destroy();
                  performDelete.call(me, roleId);
               }
              },
              {
               text: this.msg("button.cancel"),
               handler: function Admin_RM_View_Roles_cancelDelete()
               {
                  this.destroy();
               }
              }
         ]
        });
      }, 
      performDelete: function(roleId)
      {
         alert('role deleted');
         // execute ajax request
         // Alfresco.util.Ajax.request(
         // {
         //    url: url,
         //    method: "DELETE",
         //    responseContentType : "application/json",
         //    successMessage: this._msg("message.delete.success"),
         //    successCallback:
         //    {
         //       fn: success,
         //       scope: this
         //    },
         //    failureMessage: this._msg("message.delete.failure"),
         //    failureCallback:
         //    {
         //       fn: failure,
         //       scope: this
         //    }
         // });
      },   
      onRoleSelect: function Admin_RM_View_Roles_onRoleSelect(e)
      {
         var el = Event.getTarget(e);
         var currentRole = window.location.hash;
         if (currentRole!='')
         {
            var roleEl = Dom.get(currentRole);
            if (roleEl)
            {
               roleEl.addClass('hiddenRole');
            }
               
         }
      }
   });
})();