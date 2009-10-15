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
 * User Profile component.
 * 
 * @namespace Alfresco
 * @class Alfresco.UserProfile
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
    * UserProfile constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.UserProfile} The new UserProfile instance
    * @constructor
    */
   Alfresco.UserProfile = function(htmlId)
   {
      this.name = "Alfresco.UserProfile";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button"], this.onComponentsLoaded, this);
      
      return this;
   };
   
   Alfresco.UserProfile.prototype =
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
          * Current userId.
          * 
          * @property userId
          * @type string
          */
         userId: "",
         
         /**
          * Id of the profile to display
          * 
          * @property profileId
          * @type string
          */
         profileId: ""
      },

      /**
       * FileUpload module instance.
       * 
       * @property fileUpload
       * @type Alfresco.module.FileUpload
       */
      fileUpload: null,

      /**
       * Object container for storing YUI widget instances.
       * 
       * @property widgets
       * @type object
       */
      widgets: {},

      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.UserProfile} returns 'this' for method chaining
       */
      setOptions: function UP_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },
      
      /**
       * Set messages for this component.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.UserProfile} returns 'this' for method chaining
       */
      setMessages: function UP_setMessages(obj)
      {
         Alfresco.util.addMessages(obj, this.name);
         return this;
      },
      
      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function UP_onComponentsLoaded()
      {
         Event.onContentReady(this.id, this.onReady, this, true);
      },
   
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function UP_onReady()
      {
         // Reference to self used by inline functions
         var me = this;
         
         // Allow edit if we are displaying this users profile
         if (this.options.profileId == this.options.userId)
         {
            // Buttons
            this.widgets.upload = Alfresco.util.createYUIButton(this, "button-upload", this.onUpload);
            this.widgets.edit = Alfresco.util.createYUIButton(this, "button-edit", this.onEditProfile);
            this.widgets.save = Alfresco.util.createYUIButton(this, "button-save", null,
               {
                  type: "submit"
               });
            this.widgets.cancel = Alfresco.util.createYUIButton(this, "button-cancel", this.onCancel);
            
            // Form definition
            var form = new Alfresco.forms.Form(this.id + "-form");
            form.setSubmitElements(this.widgets.save);
            form.setShowSubmitStateDynamically(true);
            form.setSubmitAsJSON(true);
            form.setAJAXSubmit(true,
            {
               successCallback:
               {
                  fn: this.onSuccess,
                  scope: this
               }
            });
            
            // Form field validation
            form.addValidation(this.id + "-input-firstName", Alfresco.forms.validation.mandatory, null, "keyup");
            form.addValidation(this.id + "-input-lastName", Alfresco.forms.validation.mandatory, null, "keyup");
            
            // Initialise the form
            form.init();
         }
         
         // Finally show the main component body here to prevent UI artifacts on YUI button decoration
         Dom.removeClass(this.id + "-readview", "hidden");
      },
      
      /**
       * Edit Profile button click handler
       * 
       * @method onEditProfile
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onEditProfile: function UP_onEditProfile(e, p_obj)
      {
         Dom.addClass(this.id + "-readview", "hidden");
         Dom.removeClass(this.id + "-editview", "hidden");
      },


      /**
       * YUI WIDGET EVENT HANDLERS
       * Handlers for standard events fired from YUI widgets, e.g. "click"
       */
      
      /**
       * Upload button click handler
       *
       * @method onUpload
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onUpload: function UP_onUpload(e, p_obj)
      {
         if (this.fileUpload === null)
         {
            this.fileUpload = Alfresco.getFileUploadInstance();
         }
         
         // Show uploader for single file select - override the upload URL to use avatar upload service
         var uploadConfig =
         {
            flashUploadURL: "slingshot/profile/uploadavatar",
            htmlUploadURL: "slingshot/profile/uploadavatar.html",
            username: this.options.userId,
            mode: this.fileUpload.MODE_SINGLE_UPLOAD,
            onFileUploadComplete:
            {
               fn: this.onFileUploadComplete,
               scope: this
            }
         };
         this.fileUpload.show(uploadConfig);
         Event.preventDefault(e);
      },
      
      /**
       * File Upload complete event handler
       *
       * @method onFileUploadComplete
       * @param complete {object} Object literal containing details of successful and failed uploads
       */
      onFileUploadComplete: function UP_onFileUploadComplete(complete)
      {
         var success = complete.successful.length;
         if (success > 0)
         {
            var noderef = complete.successful[0].nodeRef;
            
            // replace avatar image URL with the updated one
            var photos = Dom.getElementsByClassName("photoimg", "img");
            for (i in photos)
            {
               photos[i].src = Alfresco.constants.PROXY_URI + "api/node/" + noderef.replace("://", "/") +
                               "/content/thumbnails/avatar?c=force";
            }
            
            // call to update the user object - photo changes take effect immediately!
            var json = {};
            json[this.id + "-photoref"] = noderef;
            var config =
            {
               method: "POST",
               url: Dom.get(this.id + "-form").attributes.action.nodeValue,
               dataObj: json
            };
            Alfresco.util.Ajax.jsonRequest(config);
         }
      },
      
      /**
       * Save Changes form submit success handler
       *
       * @method onSuccess
       * @param response {object} Server response object
       */
      onSuccess: function UP_onSuccess(response)
      {
         if (response)
         {
            // succesfully updated details - refresh the page with the new user details
            location.reload(true);
         }
         else
         {
            Alfresco.util.PopupManager.displayPrompt(
            {
               text: Alfresco.util.message("message.failure", this.name)
            });
         }
      },
      
      /**
       * Cancel Changes button click handler
       *
       * @method onCancel
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onCancel: function UP_onCancel(e, p_obj)
      {
         Dom.addClass(this.id + "-editview", "hidden");
         Dom.removeClass(this.id + "-readview", "hidden");
      }
   };
})();