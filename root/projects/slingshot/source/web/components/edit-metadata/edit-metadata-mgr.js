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
 * EditMetadataMgr template.
 * 
 * @namespace Alfresco
 * @class Alfresco.EditMetadataMgr
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom;
      
   /**
    * EditMetadataMgr constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.EditMetadataMgr} The new EditMetadataMgr instance
    * @constructor
    */
   Alfresco.EditMetadataMgr = function EditMetadataMgr_constructor(htmlId)
   {
      Alfresco.EditMetadataMgr.superclass.constructor.call(this, "Alfresco.EditMetadataMgr", htmlId);
      
      /* Decoupled event listeners */
      YAHOO.Bubbling.on("formContentReady", this.onFormContentReady, this);
      YAHOO.Bubbling.on("beforeFormRuntimeInit", this.onBeforeFormRuntimeInit, this);
      
      return this;
   };

   YAHOO.extend(Alfresco.EditMetadataMgr, Alfresco.component.Base,
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
      onFormContentReady: function EditMetadataMgr_onFormContentReady(layer, args)
      {
         // change the default 'Submit' label to be 'Save'
         var submitButton = args[1].buttons.submit;
         submitButton.set("label", this.msg("button.save"));
         
         // add a handler to the cancel button
         var cancelButton = args[1].buttons.cancel;
         cancelButton.addListener("click", this.onCancelButtonClick, null, this);
      },
      
      /**
       * Event handler called when the "beforeFormRuntimeInit" event is received
       */
      onBeforeFormRuntimeInit: function EditMetadataMgr_onBeforeFormRuntimeInit(layer, args)
      {
         args[1].runtime.setAJAXSubmit(true,
         {
            successCallback:
            {
               fn: this.onMetadataUpdateSuccess,
               scope: this
            },
            failureCallback:
            {
               fn: this.onMetadataUpdateFailure,
               scope: this
            }
         });
      },
      
      /**
       * Handler called when the metadata was updated successfully
       *
       * @method onMetadataUpdateSuccess
       * @param response The response from the submission
       */
      onMetadataUpdateSuccess: function EditMetadataMgr_onMetadataUpdateSuccess(response)
      {
         this._navigateForward();
      },
      
      /**
       * Handler called when the metadata update operation failed
       *
       * @method onMetadataUpdateFailure
       * @param response The response from the submission
       */
      onMetadataUpdateFailure: function EditMetadataMgr_onMetadataUpdateFailure(response)
      {
         var errorMsg = this.msg("edit-metadata-mgr.update.failed");
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
      onCancelButtonClick: function EditMetadataMgr_onCancel(type, args)
      {
         this._navigateForward();
      },
      
      /**
       * Displays the corresponding details page for the current node
       *
       * @method _navigateForward
       * @private
       */
      _navigateForward: function EditMetadataMgr__navigateForward()
      {
         /* Did we come from the document library? If so, then direct the user back there */
         if (document.referrer.match(/documentlibrary([?]|$)/) || document.referrer.match(/repository([?]|$)/))
         {
            // go back to the referrer page
            history.go(-1);
         }
         else
         {
            // go forward to the appropriate details page for the node
            window.location.href = this.options.nodeType + "-details?nodeRef=" + this.options.nodeRef;
         }
      }
   });
})();
