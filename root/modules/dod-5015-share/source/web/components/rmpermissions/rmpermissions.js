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
         
         // Events
         Event.on(this.id + "-inherit", "change", this.onInheritCheckChanged, this, true);
      },
      
      /**
       * BUBBLING LIBRARY EVENT HANDLERS FOR PAGE EVENTS
       * Disconnected event handlers for inter-component event notification
       */
      
      /**
       * Fired when the Add User/Group button is clicked.
       * 
       * @method onAddClick
       * @param e {object} DomEvent
       * @param args {array} Event parameters (depends on event type)
       */
      onAddClick: function RecordsPermissions_onAddClick(e, args)
      {
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
      }
   });
})();