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

         Event.on(this.id,'click',this.onInteractionEvent, null, this);

         this.registerEventHandler('click',[
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
                     handler:this.onDoneReference,
                     scope : this
               }
            },                       
            {
               rule : 'button.newRef',
               o : {
                      handler:this.onNewReference,
                      scope:this
                   }
            }
         ]);
         return this;
      },
      /**
       * Handler for Done button
       *  
       */
      onDoneReference : function RM_References_onDoneReference(e, args)
      {
         var uriTemplate = Alfresco.constants.URL_PAGECONTEXT + 'site/{site}/document-details?nodeRef={nodeRef}';

         var pageUrl = YAHOO.lang.substitute(uriTemplate,
         {
            site: encodeURIComponent(this.options.siteId),
            nodeRef: this.options.nodeRef
         });

         window.location.href = pageUrl;
      },
      /**
       * Handler for delete button
       *  
       */
      onDeleteReference : function RM_References_onDeleteReference(e, args)
      {
         var refId = this.widgets[Event.getTarget(e).id.replace('-button','')].get('value');         

         Alfresco.util.Ajax.jsonRequest(
         {
            method: Alfresco.util.Ajax.DELETE,
            url: Alfresco.constants.PROXY_URI + "api/node/" + this.options.nodeRef.replace(':/','')+'/customreferences'+'/'+refId,
            successCallback:
            {
               fn: this.onDeleteSuccess,
               scope: this
            },
            successMessage: Alfresco.util.message("message.delete.success", 'Alfresco.RM.References'),
            failureMessage: Alfresco.util.message("message.delete.fail", 'Alfresco.RM.References')
         });
      },
      
      /**
       * Handler for new reference  button 
       */
      onNewReference : function RM_References_onNewReference(e, args)
      {
        var uriTemplate = Alfresco.constants.URL_PAGECONTEXT + 'site/{site}/new-rmreference?nodeRef={nodeRef}&parentNodeRef={parentNodeRef}&docName={docName}';
         var url = YAHOO.lang.substitute(uriTemplate,
         {
            site: encodeURIComponent(this.options.siteId),
            nodeRef: this.options.nodeRef,
            parentNodeRef: this.options.parentNodeRef,
            docName: encodeURIComponent(this.options.docName)
         });

         window.location.href = url; 
      },
      
      /**
       * Handler for deletion success 
       *  
       */
       onDeleteSuccess : function RM_References_onDeleteSuccess(e)
       {
          //remove list item
          var parent = Dom.getAncestorByTagName(Event.getTarget(e),'li');
          parent.parentNode.removeChild(parent);
          //if no more references, remove list and display msg
          if (Sel.query('li', this.id).length==0)
          {
             var ol  = (Sel.query('ol', this.id)[0]);
             ol.parentNode.removeChild(ol);
             Dom.addClass("no-refs",'active');
          }
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


