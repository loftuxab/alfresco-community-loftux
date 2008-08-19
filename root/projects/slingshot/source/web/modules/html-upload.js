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
 * HtmlUpload component.
 *
 * Popups a YUI panel and displays a filelist and buttons to browse for files
 * and upload them. Files can be removed and uploads can be cancelled.
 * For single file uploads version input can be submitted.
 *
 * A multi file upload scenario could look like:
 *
 * var htmlUpload = Alfresco.module.getHtmlUploadInstance();
 * var multiUploadConfig =
 * {
 *    siteId: siteId, *    containerId: doclibContainerId,
 *    path: docLibUploadPath,
 *    filter: [],
 *    mode: htmlUpload.MODE_MULTI_UPLOAD,
 * }
 * this.htmlUpload.show(multiUploadConfig);
 *
 * @namespace Alfresco.module
 * @class Alfresco.module.HtmlUpload
 */
(function()
{

   /**
    * HtmlUpload constructor.
    *
    * HtmlUpload is considered a singleton so constructor should be treated as private,
    * please use Alfresco.module.getHtmlUploadInstance() instead.
    *
    * @param {string} htmlId The HTML id of the parent element
    * @return {Alfresco.module.HtmlUpload} The new HtmlUpload instance
    * @constructor
    * @private
    */
   Alfresco.module.HtmlUpload = function(containerId)
   {
      this.name = "Alfresco.module.HtmlUpload";
      this.id = containerId;

      var instance = Alfresco.util.ComponentManager.find({id: this.id});
      if (instance !== undefined && instance.length > 0)
      {
         throw new Error("An instance of Alfresco.module.HtmlUpload already exists.");
      }


      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      // Load YUI Components
      Alfresco.util.YUILoaderHelper.require(["button", "container", "datatable", "datasource"], this.onComponentsLoaded, this);

      return this;
   }

   Alfresco.module.HtmlUpload.prototype =
   {

      /**
       * Shows uploader in single upload mode.
       *
       * @property MODE_SINGLE_UPLOAD
       * @static
       * @type int
       */
      MODE_SINGLE_UPLOAD: 1,

      /**
       * Shows uploader in single update mode.
       *
       * @property MODE_SINGLE_UPDATE
       * @static
       * @type int
       */
      MODE_SINGLE_UPDATE: 2,

      /**
       * The default config for the gui state for the uploader.
       * The user can override these properties in the show() method to use the
       * uploader for both single & multi uploads and single updates.
       *
       * @property defaultShowConfig
       * @type object
       */
      defaultShowConfig:
      {
         siteId: null,
         containerId: null,
         uploadDirectory: null,
         updateNodeRef: null,
         updateFilename: null,
         mode: this.MODE_SINGLE_UPLOAD,
         onFileUploadComplete: null,
         overwrite: true,
         thumbnails: null,
         uploadURL: null,
         username: null
      },

      /**
       * The merged result of the defaultShowConfig and the config passed in
       * to the show method.
       *
       * @property defaultShowConfig
       * @type object
       */
      showConfig: {},

      /**
       * Object container for storing YUI widget and HTMLElement instances.
       *
       * @property widgets
       * @type object
       */
      widgets: {},

      /**
       * HTMLElement of type div that displays the version input form.
       *
       * @property versionSection
       * @type HTMLElement
       */
      versionSection: null,

      /**
       * Set messages for this module.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.module.HtmlUpload} returns 'this' for method chaining
       */
      setMessages: function HU_setMessages(obj)
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
      onComponentsLoaded: function HU_onComponentsLoaded()
      {
         // Shortcut for dummy instance
         if (this.id === null)
         {
            return;
         }
      },

      /**
       * Show can be called multiple times and will display the uploader dialog
       * in different ways depending on the config parameter.
       *
       * @method show
       * @param config {object} describes how the upload dialog should be displayed
       * The config object is in the form of:
       * {
       *    siteId: {string},        // site to upload file(s) to
       *    containerId: {string},   // container to upload file(s) to (i.e. a doclib id)
       *    uploadPath: {string},    // directory path inside the component to where the uploaded file(s) should be save
       *    updateNodeRef: {string}, // nodeRef to the document that should be updated
       *    updateFilename: {string},// The name of the file that should be updated, used to display the tip
       *    mode: {int},             // MODE_SINGLE_UPLOAD or MODE_SINGLE_UPDATE
       *    filter: {array},         // limits what kind of files the user can select in the OS file selector
       *    onFileUploadComplete: null, // Callback after upload
       *    overwrite: true          // If true and in mode MODE_XXX_UPLOAD it tells
       *                             // the backend to overwrite a versionable file with the existing name
       *                             // If false and in mode MODE_XXX_UPLOAD it tells
       *                             // the backend to append a number to the versionable filename to avoid
       *                             // an overwrite and a new version
       * }
       */
      show: function HU_show(config)
      {
         // Merge the supplied config with default config and check mandatory properties
         this.showConfig = YAHOO.lang.merge(this.defaultShowConfig, config);
         if (this.showConfig.uploadDirectory === undefined && this.showConfig.updateNodeRef === undefined)
         {
             throw new Error("An updateNodeRef OR uploadDirectory must be provided");
         }
         if (this.showConfig.uploadDirectory !== null && this.showConfig.uploadDirectory.length === 0)
         {
            this.showConfig.uploadDirectory = "/";
         }
         // Check if the uploader has been shoed before
         if (this.widgets.panel)
         {
            this._showPanel();
         }
         else
         {
            // If it hasn't load the gui (template) from the server
            Alfresco.util.Ajax.request(
            {
               url: Alfresco.constants.URL_SERVICECONTEXT + "modules/html-upload?htmlid=" + this.id,
               successCallback:
               {
                  fn: this.onTemplateLoaded,
                  scope: this
               },
               failureMessage: "Could not load html upload template",
               execScripts: true
            });
         }
      },


      /**
       * Called when the uploader html template has been returned from the server.
       * Creates the YIU gui objects such as the panel,
       * saves references to HTMLElements inside the template for easy access
       * during upload progress and finally shows the panel with the gui inside.
       *
       * @method onTemplateLoaded
       * @param response {object} a Alfresco.util.Ajax.request response object
       */
      onTemplateLoaded: function HU_onTemplateLoaded(response)
      {
         var Dom = YAHOO.util.Dom;

         // Inject the template from the XHR request into a new DIV element
         var containerDiv = document.createElement("div");
         containerDiv.innerHTML = response.serverResponse.responseText;

         // Create the panel from the HTML returned in the server reponse
         var dialogDiv = YAHOO.util.Dom.getFirstChild(containerDiv);
         this.widgets.panel = new YAHOO.widget.Panel(dialogDiv,
         {
            modal: true,
            draggable: false,
            fixedcenter: true,
            visible: false,
            close: false
         });

         /**
          * Render the server reponse so the contents get inserted in the Dom.
          * Scripts in the template, such as setMessage(),  will also get run
          * at this moment.
          */
         this.widgets.panel.render(document.body);

         // Save a reference to the HTMLElement displaying texts so we can alter the texts later
         this.widgets.titleText = Dom.get(this.id + "-title-span");
         this.widgets.singleUploadTip = Dom.get(this.id + "-singleUploadTip-span");
         this.widgets.singleUpdateTip = Dom.get(this.id + "-singleUpdateTip-span");


         // Save references to hidden fields so we can set them later
         this.widgets.filedata = Dom.get(this.id + "-filedata-file");
         this.widgets.siteId = Dom.get(this.id + "-siteId-hidden");
         this.widgets.containerId = Dom.get(this.id + "-containerId-hidden");
         this.widgets.username = Dom.get(this.id + "-username-hidden");
         this.widgets.updateNodeRef = Dom.get(this.id + "-updateNodeRef-hidden");
         this.widgets.uploadDirectory = Dom.get(this.id + "-uploadDirectory-hidden");
         this.widgets.overwrite = Dom.get(this.id + "-overwrite-hidden");
         this.widgets.thumbnails = Dom.get(this.id + "-thumbnails-hidden");
         this.widgets.success = Dom.get(this.id + "-success-hidden");
         this.widgets.failure = Dom.get(this.id + "-failure-hidden");

         // Save reference to version section elements so we can set its values later
         this.widgets.description = YAHOO.util.Dom.get(this.id + "-description-textarea");
         this.widgets.minorVersion = YAHOO.util.Dom.get(this.id + "-minorVersion-radioButton");
         this.widgets.versionSection = Dom.get(this.id + "-versionSection-div");

         // Create and save a reference to the buttons so we can alter them later
         this.widgets.uploadButton = Alfresco.util.createYUIButton(this, "upload-button", null, {type: "submit" });
         this.widgets.cancelButton = Alfresco.util.createYUIButton(this, "cancel-button", this.onCancelButtonClick);
         
         // Configure the forms runtime
         var form = new Alfresco.forms.Form(this.id + "-htmlupload-form");
         this.widgets.form = form;

         // Title is mandatory
         form.addValidation(this.id + "-filedata-file", Alfresco.forms.validation.mandatory, null, "change");

         // The ok button is the submit button, and it should be enabled when the form is ready
         form.setShowSubmitStateDynamically(true, false);
         form.setSubmitElements(this.widgets.uploadButton);
         form.doBeforeFormSubmit = {
            fn: function(){
               this.widgets.uploadButton.set("disabled", true);
               this.widgets.cancelButton.set("disabled", true);
               this.widgets.panel.hide();
               this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
               {
                  text: Alfresco.util.message("message.uploading", this.name),
                  spanClass: "wait",
                  displayTime: 0
               });
            },
            obj: null,
            scope: this
         }

         // Submit as an ajax submit (not leave the page), in json format
         form.setAJAXSubmit(true, {});

         //form.setSubmitAsJSON(true);
         // We're in a popup, so need the tabbing fix
         form.applyTabFix();
         form.init();

         // Show panel
         this._showPanel();
      },

      /**
       * Called when a file has been successfully uploaded
       * Informs the user and reloads the doclib.
       *
       * @method onUploadSuccess
       */
      onUploadSuccess: function HU_onUploadSuccess()
      {
         // Hide the current message display
         this.widgets.feedbackMessage.destroy();

         // Inform user that upload was successful         
         Alfresco.util.PopupManager.displayMessage({ text: Alfresco.util.message("message.success", this.name) });

         // Tell the document list to refresh itself if present
         YAHOO.Bubbling.fire("doclistRefresh", {currentPath: this.showConfig.path});

         // Todo see if the filename can be added to the list
         var objComplete =
         {
            successful: []
         };

         var callback = this.showConfig.onFileUploadComplete;
         if (callback && typeof callback.fn == "function")
         {
            // Call the onFileUploadComplete callback in the correct scope
            callback.fn.call((typeof callback.scope == "object" ? callback.scope : this), objComplete, callback.obj);
         }
      },

      /**
       * Called when a file failed to be uploaded
       * Informs the user.
       *
       * @method onUploadFailure
       */
      onUploadFailure: function HU_onUploadFailure()
      {
         // Hide the current message display
         this.widgets.feedbackMessage.destroy();

         // Inform user that upload was successful
         Alfresco.util.PopupManager.displayMessage({ text: Alfresco.util.message("message.success", this.name) });
      },

      /**
       * Fired when the user clicks the cancel button.
       * Closes the panel.
       *
       * @method onCancelButtonClick
       * @param event {object} a Button "click" event
       */
      onCancelButtonClick: function HU_onCancelButtonClick()
      {
         // Hide the panel
         this.widgets.panel.hide();
      },

      /**
       * Adjust the gui according to the config passed into the show method.
       *
       * @method _applyConfig
       * @private
       */
      _applyConfig: function HU__applyConfig()
      {
         var Dom = YAHOO.util.Dom;

         // Set the panel title
         var title;
         if (this.showConfig.mode === this.MODE_SINGLE_UPLOAD)
         {
            title = Alfresco.util.message("header.singleUpload", this.name);
         }
         else if (this.showConfig.mode === this.MODE_SINGLE_UPDATE)
         {
            title = Alfresco.util.message("header.singleUpdate", this.name);
         }
         this.widgets.titleText["innerHTML"] = title;

         if (this.showConfig.mode === this.MODE_SINGLE_UPDATE)
         {

            var tip = Alfresco.util.message("label.singleUpdateTip", this.name, {"0": this.showConfig.updateFilename});
            this.widgets.singleUpdateTip["innerHTML"] = tip;

            // Display the version input form
            Dom.removeClass(this.widgets.versionSection, "hidden");
         }
         else
         {
            // Hide the version input form
            Dom.addClass(this.widgets.versionSection, "hidden");
         }

         // Show the help label for single updates
         if (this.showConfig.mode === this.MODE_SINGLE_UPDATE)
         {
            // Show the help label for single updates
            Dom.removeClass(this.widgets.singleUpdateTip, "hidden");
            Dom.addClass(this.widgets.singleUploadTip, "hidden");
         }
         else
         {
            // Show the help label for single uploads
            Dom.removeClass(this.widgets.singleUploadTip, "hidden");
            Dom.addClass(this.widgets.singleUpdateTip, "hidden");
         }

         this.widgets.cancelButton.set("disabled", false);
         this.widgets.filedata.value = null;
         this.widgets.uploadButton.set("disabled", true);

         // Set the hidden parameters
         this.widgets.siteId.value = this.showConfig.siteId;
         this.widgets.containerId.value = this.showConfig.containerId;
         this.widgets.username.value = this.showConfig.username;
         if (this.showConfig.mode === this.MODE_SINGLE_UPDATE)
         {
            this.widgets.updateNodeRef.value = this.showConfig.updateNodeRef;
            this.widgets.uploadDirectory.value = "";
            this.widgets.overwrite.value = "";
            this.widgets.thumbnails.value = "";
         }
         else
         {
            this.widgets.updateNodeRef.value = "";
            this.widgets.uploadDirectory.value = this.showConfig.uploadDirectory;
            this.widgets.overwrite.value = this.showConfig.overwrite;
            this.widgets.thumbnails.value = this.showConfig.thumbnails;
         }
         var success = "window.parent.Alfresco.util.ComponentManager.find({id: '" + this.id + "'})[0].onUploadSuccess()";
         this.widgets.success.value = success;

         var failure = "window.parent.Alfresco.util.ComponentManager.find({id: '" + this.id + "'})[0].onUploadFailure()";
         this.widgets.failure.value = failure;


      },

      /**
       * Prepares the gui and shows the panel.
       *
       * @method _showPanel
       * @private
       */
      _showPanel: function HU__showPanel()
      {
         // Reset references and the gui before showing it
         this.widgets.description.value = "";
         this.widgets.minorVersion.checked = true;

         // Apply the config before it is showed
         this._applyConfig();

         // Show the upload panel
         this.widgets.panel.show();
      }

   };

})();


Alfresco.module.getHtmlUploadInstance = function()
{
   var instanceId = "alfresco-htmlupload-instance";
   var instance = Alfresco.util.ComponentManager.find({id: instanceId});
   if (instance !== undefined && instance.length > 0)
   {
      instance = instance[0];
   }
   else
   {
      instance = new Alfresco.module.HtmlUpload(instanceId);
   }
   return instance;
}
