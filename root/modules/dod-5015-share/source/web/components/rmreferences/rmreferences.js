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
Alfresco.RM = Alfresco.RM || {};
/**
 * RM References component
 * 
 * @namespace Alfresco
 * @class Alfresco.RM.References
 */
(function RM_References()
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
    * RM References componentconstructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.dashlet.MyDocuments} The new component instance
    * @constructor
    */
   Alfresco.RM.References = function RM_References_constructor(htmlId)
   {
      Alfresco.RM.References.superclass.constructor.call(this, "Alfresco.RM.References", htmlId, []);
     
      // this.eventHandlers = {};
      return this;
   };
    
    YAHOO.extend(Alfresco.RM.References, Alfresco.component.Base,
   {
      
      /**
       * Initialises event listening and custom events
       *  
       */
      initEvents : function RM_References_initEvents()
      {
         //requires EventProvider
         //this.createEvent('newReference')
         Event.on(this.id,'click',this.onInteractionEvent, null, this);

         this.registerEventHandler('click',[
            {
               rule : 'button.editRef',
               o : {
                     handler:function editReference(e,args){
                        alert('editReference');
                     },
                     scope : this
               }
            },
            {
               rule : 'button.deleteRef',
               o : {
                     handler:this.onDeleteReference,
                     scope : this
               }
            },
            {
               rule : 'button.doneRef',
               o : {
                     handler:function doneReference(e,args){
                        alert('doneReference');
                     },
                     scope : this
               }
            },                       
            {
               rule : 'button.newRef',
               o : {
                      handler:function newReference(e,args){
                         alert('newReference');
                      },
                      scope:this
                   }
            }
         ]);
         return this;
      },
      onDeleteReference : function RM_References_onDeleteReference(e, args)
      {
         alert('deleteReference');
      },
      /**
       * Fired by YUI when parent element is available for scripting
       * @method onReady
       * 
       */
      onReady: function RM_References_onReady()
      {
         this.initEvents();
         var buttons = Sel.query('button',this.id);
         // Create widget button while reassigning classname to src element (since YUI removes classes). 
         // We need the classname so we can identify what action to take when it is interacted with (event delegation).
         for (var i=0, len = buttons.length; i<len; i++)
         {
            var button= buttons[i];
            var id = button.id;
            this.widgets[id] = new YAHOO.widget.Button(id);
            this.widgets[id]._button.className=button.className;
         }
      }

   });
})();


