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
 * Document actions component - DOD5015 extensions.
 * 
 * @namespace Alfresco
 * @class Alfresco.RecordsDocumentReferences
 */
(function()
{
/**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
       Event = YAHOO.util.Event,
       Sel = YAHOO.util.Selector;

   /**
    * RecordsDocumentReferences constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.RecordsDocumentReferences} The new RecordsDocumentReferences instance
    * @constructor
    */
   Alfresco.RecordsDocumentReferences = function(htmlId)
   {
      Alfresco.util.ComponentManager.register(this);
      return Alfresco.RecordsDocumentReferences.superclass.constructor.call(this, "Alfresco.RecordsDocumentReferences",htmlId,[]);
   }
   
   /**
    * Extend from Alfresco.DocumentActions
    */
   YAHOO.extend(Alfresco.RecordsDocumentReferences, Alfresco.component.Base,
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
          * String Id used for document picker control
          *
          * @property controlId
          * @type String
          */
         controlId: "",
         /**
          * String Id used for document picker picker
          *
          * @property pickerId
          * @type String
          */
         pickerId: "",
         /**
          * Comma separated value of selected documents (nodeRefs). 
          *
          * @property pickerId
          * @type String
          */
         currentValue: ""
         
      },
      
      /**
       * Initialises event listening and custom events
       *  
       */
      initEvents : function RecordsDocumentReferences_initEvents()
      {
         Event.on(this.id,'click',this.onInteractionEvent, null, this);
         
         this.registerEventHandler('click',[
            {
               rule : 'button.manageRef',
               o : {
                     handler:this.goToManageReferences,
                     scope : this
               }
            },                       

         ]);
         
         return this;
      },

      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function RecordsDocumentReferences_onComponentsLoaded()
      {
         Event.onContentReady(this.id, this.onReady, this, true);
      },
      
      /**
       * Fired by YUI when parent element is available for scripting
       * @method onReady
       * 
       */
      onReady: function RecordsDocumentReferences_onReady()
      {
         this.initEvents();
         var buttons = Sel.query('#manageRef',this.id);
         // Create widget button while reassigning classname to src element (since YUI removes classes). 
         // We need the classname so we can identify what action to take when it is interacted with (event delegation).
         for (var i=0, len = buttons.length; i<len; i++)
         {
            var button= buttons[i];
            var id = button.id;
            this.widgets[id] = new YAHOO.widget.Button(id);
            this.widgets[id]._button.className=button.className;
         }
         YAHOO.Bubbling.on('documentDetailsAvailable',this.onDocumentDetailsAvailable, this);
      },

      onDocumentDetailsAvailable: function RecordsDocumentReferences_onDocumentDetailsAvailable(e, args)
      {
         this.options.parentNodeRef=args[1].metadata.filePlan.replace(':/','');
         this.options.docName = args[1].documentDetails.displayName;
      },
      
      goToManageReferences: function RecordsDocumentReferences_goToManageReferences()
      {
         var uriTemplate = Alfresco.constants.URL_PAGECONTEXT + 'site/{site}/rmreferences?nodeRef={nodeRef}&parentNodeRef={parentNodeRef}&docName={docName}';         

         var url = YAHOO.lang.substitute(uriTemplate,
         {
            site: encodeURIComponent(this.options.siteId),
            nodeRef: this.options.nodeRef,
            parentNodeRef: this.options.parentNodeRef,
            docName:encodeURIComponent(this.options.docName)
         });

         window.location.href = url;
      }
      
   });
   
})();
