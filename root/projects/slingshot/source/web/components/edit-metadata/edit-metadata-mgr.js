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
      this.name = "Alfresco.EditMetadataMgr";
      this.id = htmlId;
      
      /* Decoupled event listeners */
      YAHOO.Bubbling.on("formContentReady", this.onFormContentReady, this);
      YAHOO.Bubbling.on("beforeFormRuntimeInit", this.onBeforeFormRuntimeInit, this);
      
      return this;
   };
   
   Alfresco.EditMetadataMgr.prototype =
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
          * Current siteId.
          * 
          * @property siteId
          * @type string
          */
         siteId: null
      },

      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.Search} returns 'this' for method chaining
       */
      setOptions: function EditMetadataMgr_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },
      
      /**
       * Set messages for this component.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.Search} returns 'this' for method chaining
       */
      setMessages: function EditMetadataMgr_setMessages(obj)
      {
         Alfresco.util.addMessages(obj, this.name);
         return this;
      },
      
      /**
       * Event handler called when the "formContentReady" event is received
       */
      onFormContentReady: function EditMetadataMgr_onFormContentReady(layer, args)
      {
         // change the default 'Submit' label to be 'Save'
         var submitButton = args[1].buttons.submit;
         submitButton.set("label", this._msg("edit-metadata-mgr.button.save"));
         
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
         this._showDocDetails();
      },
      
      /**
       * Handler called when the metadata update operation failed
       *
       * @method onMetadataUpdateFailure
       * @param response The response from the submission
       */
      onMetadataUpdateFailure: function EditMetadataMgr_onMetadataUpdateFailure(response)
      {
         Alfresco.util.PopupManager.displayPrompt(
         {
            title: this._msg("message.failure"),
            text: this._msg("edit-metadata-mgr.update.failed")
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
         this._showDocDetails();
      },
      
      /**
       * Displays the document details page for the current node
       *
       * @method _showDocDetails
       * @private
       */
      _showDocDetails: function EditMetadataMgr__showDocDetails()
      {
         // go back to the document details page for the node
         var docDetailsUrl = Alfresco.constants.URL_PAGECONTEXT + "site/" + this.options.siteId + 
            "/document-details?nodeRef=" + this.options.nodeRef;
         window.location.href = docDetailsUrl;
      },
      
      /**
       * Gets a custom message
       *
       * @method _msg
       * @param messageId {string} The messageId to retrieve
       * @return {string} The custom message
       * @private
       */
      _msg: function EditMetadataMgr__msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, "Alfresco.EditMetadataMgr", Array.prototype.slice.call(arguments).slice(1));
      }
   };
})();
