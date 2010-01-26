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
 * CreateContentMgr template.
 * 
 * @namespace Alfresco
 * @class Alfresco.CreateContentMgr
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom;
      
   /**
    * CreateContentMgr constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.CreateContentMgr} The new CreateContentMgr instance
    * @constructor
    */
   Alfresco.CreateContentMgr = function CreateContentMgr_constructor(htmlId)
   {
      Alfresco.CreateContentMgr.superclass.constructor.call(this, "Alfresco.CreateContentMgr", htmlId);
      
      /* Decoupled event listeners */
      YAHOO.Bubbling.on("formContentReady", this.onFormContentReady, this);
      YAHOO.Bubbling.on("beforeFormRuntimeInit", this.onBeforeFormRuntimeInit, this);
      
      return this;
   };

   YAHOO.extend(Alfresco.CreateContentMgr, Alfresco.component.Base,
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
          * Current nodeRef.
          * 
          * @property nodeRef
          * @type string
          */
         nodeRef: null,

         /**
          * Current node type.
          * The manager needs to know whether the following page is document-details or folder-details
          * 
          * @property nodeType
          * @type string
          * @default "document"
          */
         nodeType: "document",
         
         /**
          * Current siteId.
          * 
          * @property siteId
          * @type string
          */
         siteId: null
      },

      /**
       * Event handler called when the "formContentReady" event is received
       */
      onFormContentReady: function CreateContentMgr_onFormContentReady(layer, args)
      {
         // change the default 'Submit' label to be 'Save'
         var submitButton = args[1].buttons.submit;
         submitButton.set("label", this.msg("button.create"));
         
         // add a handler to the cancel button
         var cancelButton = args[1].buttons.cancel;
         cancelButton.addListener("click", this.onCancelButtonClick, null, this);
      },
      
      /**
       * Event handler called when the "beforeFormRuntimeInit" event is received
       */
      onBeforeFormRuntimeInit: function CreateContentMgr_onBeforeFormRuntimeInit(layer, args)
      {
         args[1].runtime.setAJAXSubmit(true,
         {
            successCallback:
            {
               fn: this.onCreateContentSuccess,
               scope: this
            },
            failureCallback:
            {
               fn: this.onCreateContentFailure,
               scope: this
            }
         });
      },
      
      /**
       * Handler called when the metadata was updated successfully
       *
       * @method onCreateContentSuccess
       * @param response The response from the submission
       */
      onCreateContentSuccess: function CreateContentMgr_onCreateContentSuccess(response)
      {
         // TODO: Grab the new nodeRef and pass it on to _navigateForward() to optionally use
         this._navigateForward();
      },
      
      /**
       * Handler called when the metadata update operation failed
       *
       * @method onCreateContentFailure
       * @param response The response from the submission
       */
      onCreateContentFailure: function CreateContentMgr_onCreateContentFailure(response)
      {
         var errorMsg = this.msg("create-content-mgr.create.failed");
         if (response.json.message)
         {
            errorMsg = errorMsg + ": " + response.json.message;
         }  
            
         Alfresco.util.PopupManager.displayPrompt(
         {
            title: this.msg("message.failure"),
            text: errorMsg
         });
      },
      
      /**
       * Called when user clicks on the cancel button.
       *
       * @method onCancelButtonClick
       * @param type
       * @param args
       */
      onCancelButtonClick: function CreateContentMgr_onCancel(type, args)
      {
         this._navigateForward();
      },
      
      /**
       * Displays the corresponding details page for the current node
       *
       * @method _navigateForward
       * @private
       */
      _navigateForward: function CreateContentMgr__navigateForward()
      {
         /* Did we come from the document library? If so, then direct the user back there */
         if (document.referrer.match(/documentlibrary([?]|$)/) || document.referrer.match(/repository([?]|$)/))
         {
            // go back to the referrer page
            history.go(-1);
         }
         else if (this.options.siteId && this.options.siteId !== "")
         {
            // In a Site, so go back to the document library root
            window.location.href = Alfresco.util.uriTemplate("sitepage",
            {
               site: this.options.siteId,
               pageid: "documentlibrary"
            });
         }
         else
         {
            // Nowhere sensible to go other than the default page
            window.location.href = Alfresco.constants.URL_CONTEXT;
         }
      }
   });
})();
