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
 * Document links component.
 * 
 * @namespace Alfresco
 * @class Alfresco.DocumentLinks
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
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;
   
   /**
    * DocumentLinks constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.DocumentLinks} The new DocumentLinks instance
    * @constructor
    */
   Alfresco.DocumentLinks = function(htmlId)
   {
      Alfresco.DocumentLinks.superclass.constructor.call(this, "Alfresco.DocumentLinks", htmlId, ["button"]);
      
      /* Decoupled event listeners */
      YAHOO.Bubbling.on("documentDetailsAvailable", this.onDocumentDetailsAvailable, this);
      
      return this;
   }
   
   YAHOO.extend(Alfresco.DocumentLinks, Alfresco.component.Base,
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
          * External authentication being used for login
          * 
          * @property externalAuth
          * @type boolean
          */
         externalAuth: false
      },
      
      /**
       * Event handler called when the "documentDetailsAvailable" event is received
       *
       * @method: onDocumentDetailsAvailable
       */
      onDocumentDetailsAvailable: function DocumentLinks_onDocumentDetailsAvailable(layer, args)
      {
         var docData = args[1].documentDetails,
            workingCopyMode = args[1].workingCopyMode || false,
            hasClipboard = window.clipboardData && clipboardData.setData;

         if (!workingCopyMode)
         {
            Dom.removeClass(this.id + "-body", "hidden");
         }
         
         // Construct the content-based URLs if the document actually has content (size > 0)
         if (parseInt(docData.size, 10) > 0)
         {
            // Show UI controls
            Dom.setStyle(this.id + "-download", "display", "block");
            Dom.setStyle(this.id + "-view", "display", "block");
            
            var contentUrl = (this.options.externalAuth ? Alfresco.constants.PROXY_URI : Alfresco.constants.PROXY_FEED_URI) + docData.contentUrl;

            // Populate the text fields with the appropriate URLs
            Dom.get(this.id + "-download-url").value = contentUrl + "?a=true";
            Dom.get(this.id + "-view-url").value = contentUrl;
            
            // Create YUI buttons for copy if clipboard functions available
            if (hasClipboard)
            {
               this.widgets.downloadCopyButton = Alfresco.util.createYUIButton(this, "download-button", null,
               {
                  onclick:
                  {
                     fn: this._handleCopyClick,
                     obj: "-download-url",
                     scope: this
                  }
               });

               this.widgets.viewCopyButton = Alfresco.util.createYUIButton(this, "view-button", null,
               {
                  onclick:
                  {
                     fn: this._handleCopyClick,
                     obj: "-view-url",
                     scope: this
                  }
               });
            }

            // Add focus event handlers to fields        
            Event.addListener(Dom.get(this.id + "-download-url"), "focus", this._handleFocus, "-download-url", this);
            Event.addListener(Dom.get(this.id + "-view-url"), "focus", this._handleFocus, "-view-url", this);
         }
         else
         {
            // Hide UI controls
            Dom.setStyle(this.id + "-download", "display", "none");
            Dom.setStyle(this.id + "-view", "display", "none");
         }

         // Page link URL and copy button if possible
         Dom.get(this.id + "-page-url").value = window.location.href;
         if (hasClipboard)
         {
            this.widgets.pageCopyButton = Alfresco.util.createYUIButton(this, "page-button", null,
            {
               onclick:
               {
                  fn: this._handleCopyClick,
                  obj: "-page-url",
                  scope: this
               }
            });
         }
         
         Event.addListener(Dom.get(this.id + "-page-url"), "focus", this._handleFocus, "-page-url", this);
      },
      
      /**
       * Event handler to copy URLs to the system clipboard
       * 
       * @method _handleCopyClick
       * @param event The event
       * @param urlId The id of the element holding the URL to copy
       */
      _handleCopyClick: function DocumentLinks__handleCopyClick(event, urlId)
      {
         clipboardData.setData("Text", Dom.get(this.id + urlId).value);
      },
      
      /**
       * Event handler used to select text in the field when focus is received
       *
       * @method _handleFocus
       * @param event The event
       * @field The suffix of the id of the field to select
       */
      _handleFocus: function DocumentLinks__handleFocus(event, field)
      {
         YAHOO.util.Event.stopEvent(event);
         
         var fieldObj = Dom.get(this.id + field);
         if (fieldObj && fieldObj.select)
         {
            fieldObj.select();
         }
      }
   });
})();
