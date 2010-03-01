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
      return Alfresco.RecordsDocumentReferences.superclass.constructor.call(this, "Alfresco.RecordsDocumentReferences", htmlId);
   };
   
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
          * Fileplan nodeRef
          *
          * @property filePlanNodeRef
          * @type String
          */
         filePlanNodeRef: null,

         /**
          * Document name
          *
          * @property docName
          * @type String
          */
         docName: null
      },
      
      /**
       * Fired by YUI when parent element is available for scripting
       * 
       * @method onReady
       */
      onReady: function RecordsDocumentReferences_onReady()
      {
         this.widgets.manageRefs = Alfresco.util.createYUIButton(this, "manageRefs-button", this.onManageReferences,
         {
            disabled: true
         });
         YAHOO.Bubbling.on("documentDetailsAvailable", this.onDocumentDetailsAvailable, this);
      },

      /**
       * Event handler for documentDetailsAvailable bubbling event
       * 
       * @method onDocumentDetailsAvailable
       * @param e {object} Event
       * @param args {object} Event arguments
       */
      onDocumentDetailsAvailable: function RecordsDocumentReferences_onDocumentDetailsAvailable(e, args)
      {
         var docDetails = args[1].documentDetails;

         this.options.parentNodeRef = args[1].metadata.filePlan.replace(':/','');
         this.options.docName = docDetails.displayName;

         if (docDetails.permissions.userAccess.Create && docDetails.type !== "metadata-stub")
         {
            this.widgets.manageRefs.set("disabled", false);
         }
      },
      
      /**
       * Mange References button click handler. Redirects browser to Manage References page
       * 
       * @method onManageReferences
       */
      onManageReferences: function RecordsDocumentReferences_onManageReferences()
      {
         var uriTemplate = Alfresco.constants.URL_PAGECONTEXT + 'site/{site}/rmreferences?nodeRef={nodeRef}&parentNodeRef={parentNodeRef}&docName={docName}',     
            url = YAHOO.lang.substitute(uriTemplate,
            {
               site: encodeURIComponent(this.options.siteId),
               nodeRef: this.options.nodeRef,
               parentNodeRef: this.options.parentNodeRef,
               docName: encodeURIComponent(this.options.docName)
            });

         window.location.href = url;
      }
   });
})();
