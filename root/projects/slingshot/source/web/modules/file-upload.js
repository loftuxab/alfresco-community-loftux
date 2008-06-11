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
 * FileUpload component.
 *
 * Popups a YUI panel and displays a filelist and buttons to browse for files
 * and upload them. Files can be removed and uploads can be cancelled.
 * For single file uploads version input can be submitted.
 *
 * A multi file upload scenario could look like:
 *
 * var fileUpload = new Alfresco.module.FileUpload(this.id + "-fileUpload");
 * var multiUploadConfig =
 * {
 *    siteId: siteId,
 *    componentId: doclibComponentId,
 *    path: docLibUploadPath,
 *    filter: [],
 *    mode: fileUpload.MODE_MULTI_UPLOAD,
 * }
 * this.fileUpload.show(multiUploadConfig); 
 *
 * @namespace Alfresco.module
 * @class Alfresco.module.FileUpload
 */
(function()
{


   /**
    * FileUpload constructor.
    *
    * @param {string} htmlId The HTML id of the parent element
    * @return {Alfresco.module.FileUpload} The new FileUpload instance
    * @constructor
    */
   Alfresco.module.FileUpload = function(containerId)
   {
      this.name = "Alfresco.module.FileUpload";
      this.id = containerId;      
      this.swf = Alfresco.constants.URL_CONTEXT + "yui/uploader/assets/uploader.swf";

      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      // Load YUI Components
      Alfresco.util.YUILoaderHelper.require(["button", "container", "datatable", "datasource", "uploader"], this.onComponentsLoaded, this);

      return this;
   }

   Alfresco.module.FileUpload.prototype =
   {

      /**
       * The user is browsing and adding files to the file list
       *
       * @property STATE_BROWSING
       * @type int
       */
      STATE_BROWSING: 1,

      /**
       * File(s) is being uploaded to the server
       *
       * @property STATE_UPLOADING
       * @type int
       */
      STATE_UPLOADING: 2,

      /**
       * All files are processed and have either failed or been successfully
       * uploaded to the server.
       *
       * @property STATE_FINISHED
       * @type int
       */
      STATE_FINISHED: 3,
      
      /**
       * File failed to upload.
       *
       * @property STATE_FAILURE
       * @type int
       */
      STATE_FAILURE: 4,

      /**
       * File was successfully STATE_SUCCESS.
       *
       * @property STATE_SUCCESS
       * @type int
       */
      STATE_SUCCESS: 5,

       /**
       * The state of which the uploader currently is, where the flow is.
       * STATE_BROWSING > STATE_UPLOADING > STATE_FINISHED
       *
       * @property state
       * @type int
       */
      state: 1,

      /**
       * Stores references and state for each file that is in the file list.
       * The fileId parameter from the YAHOO.widget.Uploader is used as the key
       * and the value is an object that stores the state and references.
       *
       * @property fileStore
       * @type object Used as a hash table with fileId as key and an object
       *       literal as the value.
       *       The object literal is of the form:
       *       {
       *          contentType: {HTMLElement},        // select that holds the chosen contentType for the file.
       *          fileButton: {YAHOO.widget.Button}, // Will be disabled on success or STATE_FAILURE
       *          state: {int},                      // Keeps track if the individual file has been successfully uploaded or failed
       *                                             // (state flow: STATE_BROWSING > STATE_UPLOADING > STATE_SUCCESS or STATE_FAILURE)
       *          progress: {HTMLElement},           // span that is the "progress bar" which is moved during progress
       *          progressInfo: {HTMLElement},       // span that displays the filename and the state
       *          progressPercentage: {HTMLElement}  // span that displays the upload percentage for the individual file
       *       }
       */
      fileStore: {},

      /**
       * The number of successful uploads since upload was clicked.
       *
       * @property noOfSuccessfulUploads
       * @type int
       */
      noOfSuccessfulUploads: 0,

      /**
       * The number of failed uploads since upload was clicked.
       *
       * @property noOfFailedUploads
       * @type int
       */
      noOfFailedUploads: 0,


      /**
       * Remembers what files that how been added to the file list since
       * the show method was called.
       *
       * @property addedFiles
       * @type object
       */
      addedFiles: {},

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
        * Shows uploader in multi upload mode.
        *
        * @property MODE_MULTI_UPLOAD
        * @static
        * @type int
        */
      MODE_MULTI_UPLOAD: 3,
      
      /**
       * The default config for the gui state for the uploader.
       * The user can override these properties in the show() method to use the
       * uploader for both single & multi uploads and single updates.
       *
       * @property defaultShowConfig
       * @type object
       */
      defaultShowConfig: {
         siteId: null,
         componentId: null,
         path: null,
         mode: this.MODE_SINGLE_UPLOAD,
         filter: []
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
       * Since is YAHOO.widget.DataTable.MSG_EMPTY is global and can't be set
       * on an individual datatable it is stored here. When the uploader closes
       * YAHOO.widget.DataTable.MSG_EMPTY will be set with it's previous value.
       *
       * @property previousFileListEmptyMessage
       * @type string
       */
      previousFileListEmptyMessage: null,


      /**
       * Contains the upload gui
       *
       * @property panel
       * @type YAHOO.widget.Panel
       */
      panel: null,

      /**
       * Object container for storing YUI widget instances.
       * 
       * @property widgets
       * @type object
       */
       widgets: {},

      /**
       * YUI class that controls the .swf to open the browser dialog window
       * and transfers the files.
       *
       * @property uploader
       * @type YAHOO.widget.Uploader
       */
      uploader: null,

      /**
       * Used to display the user selceted files and keep track of what files
       * that are selected and should be STATE_FINISHED.
       *
       * @property uploader
       * @type YAHOO.widget.DataTable
       */
      dataTable: null,

      /**
       * HTMLElement of type span that displays the dialog title.
       *
       * @property titleText
       * @type HTMLElement
       */
      titleText: null,

      /**
       * HTMLElement of type span that displays help text for multiple selection.
       *
       * @property multiSelectText
       * @type HTMLElement
       */
      multiSelectText:null,

      /**
       * HTMLElement of type span that displays the total upload status
       *
       * @property statusText
       * @type HTMLElement
       */
      statusText: null,

      /**
       * HTMLElement of type div that displays the version input form.
       *
       * @property versionSection
       * @type HTMLElement
       */
      versionSection: null,

      /**
       * HTMLElements of type div that is used to to display a column in a
       * row in the file table list. It is loaded dynamically from the server
       * and then cloned for each row and column in the file list.
       * The fileItemTemplates has the following form:
       * {
       *    left:   HTMLElement to display the left column
       *    center: HTMLElement to display the center column
       *    right:  HTMLElement to display the right column
       * }
       *
       * @property fileItemTemplates
       * @type HTMLElement
       */
      fileItemTemplates: {},

      /**
       * Set messages for this module.
       *       
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.DocListTree} returns 'this' for method chaining
       */
      setMessages: function FU_setMessages(obj)
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
      onComponentsLoaded: function FU_onComponentsLoaded()
      {
         // Tell the YUI class where the swf is
         YAHOO.widget.Uploader.SWFURL = this.swf;

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
       *    siteId: {string},       // site to upload file(s) to
       *    componentId: {string},  // component to upload file(s) to (i.e. a doclib id)
       *    path: {string},         // path inside the component to upload file(s) to
       *    mode: {int},            // MODE_SINGLE_UPLOAD, MODE_MULTI_UPLOAD or MODE_SINGLE_UPDATE
       *    filter: {array},        // limits what kind of files the user can select in the OS file selector
       * }
       */
      show: function FU_show(config)
      {
         // Remember the the label used by other components so we can reset it at close
         this.previousFileListEmptyMessage = YAHOO.widget.DataTable.MSG_EMPTY;
         YAHOO.widget.DataTable.MSG_EMPTY = Alfresco.util.message("label.noFiles", this.name);

         // Merge the supplied config with default config and check mandatory properties
         this.showConfig = YAHOO.lang.merge(this.defaultShowConfig, config);
         if (this.showConfig.path === undefined)
         {
             throw new Error("A path must be provided");
         }
         else if (this.showConfig.path.length === 0)
         {
            this.showConfig.path = "/";
         }
         // Check if the uploader has been shoed before
         if (this.panel)
         {
            this._showPanel();
         }
         else
         {
            /* TODO: Probably place the loading of i18n messages here (or in the constructor) */            

            // If it hasn't load the gui (template) from the server
            Alfresco.util.Ajax.request(
            {
               url: Alfresco.constants.URL_SERVICECONTEXT + "modules/file-upload?htmlid=" + this.id,
               successCallback:
               {
                  fn: this.onTemplateLoaded,
                  scope: this
               },
               failureMessage: "Could not load file upload template"
            });
            
         }
      },


      /**
       * Called when the uploader html template has been returned from the server.
       * Creates the YIU gui objects such as the data table and panel,
       * saves references to HTMLElements inside the template for easy access
       * during upload progress and finally shows the panel with the gui inside.
       *
       * @method onTemplateLoaded
       * @param response {object} a Alfresco.util.Ajax.request response object
       */
      onTemplateLoaded: function FU_onTemplateLoaded(response)
      {
         var Dom = YAHOO.util.Dom;

         // Inject the template from the XHR request into a new DIV element
         var containerDiv = document.createElement("div");
         containerDiv.innerHTML = response.serverResponse.responseText;         

         // Create the panel from the HTML returned in the server reponse
         var dialogDiv = YAHOO.util.Dom.getFirstChild(containerDiv);
         this.panel = new YAHOO.widget.Panel(dialogDiv,
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
         this.panel.render(document.body);

         // Remember the label for empty data tables so we can reset it on close
         this.previousFileListEmptyMessage = YAHOO.widget.DataTable.MSG_EMPTY;
         YAHOO.widget.DataTable.MSG_EMPTY = Alfresco.util.message("label.noFiles", this.name);

         // Save a reference to the file row template that is hidden inside the markup
         this.fileItemTemplates.left = Dom.get(this.id + "-left-div");
         this.fileItemTemplates.center = Dom.get(this.id + "-center-div");
         this.fileItemTemplates.right = Dom.get(this.id + "-right-div");

         // Create the YIU datatable object
         this._createEmptyDataTable();

         // Save a reference to the HTMLElement displaying texts so we can alter the texts later
         this.titleText = Dom.get(this.id + "-title-span");
         this.multiSelectText = Dom.get(this.id + "-multiSelect-span");
         this.statusText = Dom.get(this.id + "-status-span");

         // Save a reference to browseButton so wa can change it later
         this.widgets.browseButton = Alfresco.util.createYUIButton(this, "browse-button", this.onBrowseButtonClick);

         // Save a reference to the HTMLElement displaying version input so we can hide or show it
         this.versionSection = Dom.get(this.id + "-versionSection-div");

         // Create and save a reference to the uploadButton so we can alter it later
         this.widgets.uploadButton = Alfresco.util.createYUIButton(this, "upload-button", this.onUploadButtonClick);

         // Create and save a reference to the cancelOkButton so we can alter it later
         this.widgets.cancelOkButton = Alfresco.util.createYUIButton(this, "cancelOk-button", this.onCancelOkButtonClick);

         // Create and save a reference to the uploader so we can call it later
         this.uploader = new YAHOO.widget.Uploader(this.id + "-flashuploader-div");
         this.uploader.subscribe("fileSelect", this.onFileSelect, this, true);
         this.uploader.subscribe("uploadComplete",this.onUploadComplete, this, true);
         this.uploader.subscribe("uploadProgress",this.onUploadProgress, this, true);
         this.uploader.subscribe("uploadStart",this.onUploadStart, this, true);
         this.uploader.subscribe("uploadCancel",this.onUploadCancel, this, true);
         this.uploader.subscribe("uploadCompleteData",this.onUploadCompleteData, this, true);
         this.uploader.subscribe("uploadError",this.onUploadError, this, true);

         // Show the uploader panel
         this._showPanel();
      },

      /**
       * Fired by YUI:s DataTable when a row has been added to the data table list.
       * Keeps track of added files.
       *
       * @method onReady
       * @param event {object} a DataTable "rowAdd" event
       */
      onRowAddEvent: function FU_onRowAddEvent(event)
      {
         // Since the flash movie allows the user to select one file several
         // times we need to keep track of the selected files by our selves
         var uniqueFileToken = this._getUniqueFileToken(event.record.getData());
         this.addedFiles[uniqueFileToken] = event.record.getId();
      },

      /**
       * Fired by YIUs Uploader when the user has selected one or more files
       * from the OS:s file dialog window.
       * Adds file that hasn't been selected before to the gui and adjusts the gui.
       *
       * @method onFileSelect
       * @param event {object} an Uploader "fileSelect" event
       */
      onFileSelect: function FU_onFileSelect(event)
      {
         // For each time the user select new files, all the previous selected
         // files also are included in the event.fileList. Make sure we only
         // add files to the table that haven's been added before.
         for (var i in event.fileList)
         {
            var data = YAHOO.widget.DataTable._cloneObject(event.fileList[i]);
            if (!this.addedFiles[this._getUniqueFileToken(data)])
            {
               this.dataTable.addRow(data, 0);
            }
         }
         // Enable the upload button if there are files in the list
         // and it wasn't enabled already
         if (this.dataTable.getRecordSet().getLength() > 0 &&
             this.widgets.uploadButton.get("disabled"))
         {
            this.widgets.uploadButton.set("disabled", false);
         }
      },

      /**
       * Fired by YIU:s Uploader when transfer has been start for one of the files.
       * Adjusts the gui.
       *
       * @method onUploadStart
       * @param event {object} an Uploader "uploadStart" event
       */
      onUploadStart: function FU_onUploadStart(event)
      {
         // Get the reference to the files gui components
         var Dom = YAHOO.util.Dom;
         var fileInfo = this.fileStore[event["id"]];

         // Hide the contentType drop down if it wasn't hidden already
         if (!Dom.hasClass(fileInfo.contentType, "hiddenComponents"))
         {
            Dom.addClass(fileInfo.contentType, "hiddenComponents");
         }

         // Show the progress percentage if it wasn't visible already
         fileInfo.progressPercentage["innerHTML"] = "0%";
         if (Dom.hasClass(fileInfo.progressPercentage, "hiddenComponents"))
         {
            Dom.removeClass(fileInfo.progressPercentage, "hiddenComponents");
         }

         // Make sure we know we are in upload state
         fileInfo.state = this.STATE_UPLOADING;
      },

      /**
       * Fired by YIU:s Uploader during the transfer for one of the files.
       * Adjusts the gui and its progress bars.
       *
       * @method onUploadComplete
       * @param event {object} an Uploader "uploadProgress" event
       */
      onUploadProgress: function FU_onUploadProgress(event)
      {
         var flashId = event["id"];
         var fileInfo = this.fileStore[flashId];

         // Set percentage
         var STATE_FINISHED = event["bytesLoaded"] / event["bytesTotal"];
         fileInfo.progressPercentage["innerHTML"] = Math.round(STATE_FINISHED * 100) + "%";

         // Set progress position
         var left = (-400 + (STATE_FINISHED * 400));
         YAHOO.util.Dom.setStyle(fileInfo.progress, "left", left + "px");
      },

      /**
       * Fired by YIU:s Uploader when transfer is complete for one of the files.
       *
       * @method onUploadComplete
       * @param event {object} an Uploader "uploadComplete" event
       */
      onUploadComplete: function FU_onUploadComplete(event)
      {
         /**
          * Actions taken on a completed upload is handled by the
          * onUploadCompleteData() method instead.
          */
      },

      /**
       * Fired by YIU:s Uploader when transfer is completed for a file.
       * A difference compared to the onUploadComplete() method is that
       * the response body is available in the event.
       * Adjusts the gui and calls for another file to upload if the upload
       * was succesful.
       *
       * @method onUploadCompleteData
       * @param event {object} an Uploader "uploadCompleteData" event
       */
      onUploadCompleteData: function FU_onUploadCompleteData(event)
      {
         // todo: Check that the upload was succesful when a standardized
         // json response is returned form the server

         // The individual file has been transfered completely
         // Now adjust the gui for the individual file row
         var fileInfo = this.fileStore[event["id"]];
         fileInfo.state = this.STATE_SUCCESS;
         fileInfo.fileButton.set("disabled", true);

         // Add the label "Successful" after the filename
         fileInfo.progressInfo["innerHTML"] = fileInfo.progressInfo["innerHTML"] +
                                              " " + Alfresco.util.message("label.success", this.name);

         // Change the style of the progress bar
         fileInfo.progress.setAttribute("class", "fileupload-progressFinished-span");

         // Move the progress bar to "full" progress
         YAHOO.util.Dom.setStyle(fileInfo.progress, "left", 0 + "px");
         fileInfo.progressPercentage["innerHTML"] = "100%";
         this.noOfSuccessfulUploads++;

         // Adjust the rest of the gui
         this._updateStatus();
         this._uploadFromQueue(1);
         this._adjustGuiIfFinished();
      },

      /**
       * Fired by YIU:s Uploader when transfer has been cancelled for one of the files.
       * Doesn't do anything.
       *
       * @method onUploadCancel
       * @param event {object} an Uploader "uploadCancel" event
       */
      onUploadCancel: function FU_onUploadCancel(event)
      {
         // The gui has already been adjusted in the function that caused the cancel
      },

      /**
       * Fired by YIU:s Uploader when transfer failed for one of the files.
       * Adjusts the gui and calls for another file to upload.
       *
       * @method onUploadError
       * @param event {object} an Uploader "uploadError" event
       */
      onUploadError: function FU_onUploadError(event)
      {
         var fileInfo = this.fileStore[event["id"]];

         // This sometimes gets called twice, make sure we only adjust the gui once
         if (fileInfo.state !== this.STATE_FAILURE)
         {
            fileInfo.state = this.STATE_FAILURE;

            // Add the label "Failure" to the filename
            fileInfo.progressInfo["innerHTML"] = fileInfo.progressInfo["innerHTML"] +
                                                 " " + Alfresco.util.message("label.failure", this.name);

            // Change the style of the progress bar
            fileInfo.progress.setAttribute("class", "fileupload-progressFailure-span");

            // Set the progress bar to "full" progress
            YAHOO.util.Dom.setStyle(fileInfo.progress, "left", 0 + "px");

            // Disable the remove button
            fileInfo.fileButton.set("disabled", true);

            // Adjust the rest of the gui
            this.noOfFailedUploads++;
            this._updateStatus();
            this._uploadFromQueue(1);
            this._adjustGuiIfFinished();
         }
      },

      /**
       * Fired when the user clicks the browse button.
       * Opens the file picker in.
       *
       * @method onBrowseButtonClick
       * @param event {object} an Uploader "browseButtonClick" event
       */
      onBrowseButtonClick: function FU_onBrowseButtonClick(event)
      {
         // Make sure we know have gone into browsing state
         this.state = this.STATE_BROWSING;

         // Tell the flash movie to display the OS's file selector dialog
         var multiSelect = this.showConfig.mode === this.MODE_MULTI_UPLOAD;
         this.uploader.browse(multiSelect, this.showConfig.filter);
      },

      /**
       * Called by an anonymous function which that redirects the call to here
       * when the user clicks the file remove button.
       * Removes the file and cancels it if it was being uploaded
       *
       * @method _onFileButtonClickHandler
       * @param flashId {string} an id matching the flash movies fileId
       * @param recordId {int} an id matching a record in the data tables data source
       */
      _onFileButtonClickHandler: function FU__onFileButtonClickHandler(flashId, recordId)
      {
         /**
          * The file button has been clicked to remove a file.
          * Remove the file from the datatable and all references to it.
          */
         var r = this.dataTable.getRecordSet().getRecord(recordId);
         this.addedFiles[this._getUniqueFileToken(r.getData())] = null;
         this.fileStore[flashId] = null;
         this.dataTable.deleteRow(r);
         if (this.state === this.STATE_BROWSING)
         {
            // Remove the file from the flash movies memory
            this.uploader.removeFile(flashId);
            if (this.dataTable.getRecordSet().getLength() === 0)
            {
               // If it was the last file, disable the gui since no files exist.
               this.widgets.uploadButton.set("disabled", true);
               this.widgets.browseButton.set("disabled", false);
            }
         }
         else if (this.state === this.STATE_UPLOADING)
         {
            // Cancel the ongoing upload for the file in the flash movie
            this.uploader.cancel(flashId);

            // Continue to upload documents from the queue
            this._uploadFromQueue(1);

            // Update the rest of the gui
            this._updateStatus();
            this._adjustGuiIfFinished();
         }
      },

      /**
       * Fired when the user clicks the cancel/ok button.
       * The action taken depends on what state the uploader is in.
       * In STATE_BROWSING  - Closes the panel.
       * In STATE_UPLOADING - Cancels current uploads,
       *                      informs the user about how many that were uploaded,
       *                      tells the documentlist to update itself
       *                      and closes the panel.
       * In STATE_FINISHED  - Tells the documentlist to update itself
       *                      and closes the panel.
       *
       * @method onBrowseButtonClick
       * @param event {object} a Button "click" event
       */
      onCancelOkButtonClick: function FU_onCancelOkButtonClick()
      {
         var message;
         if (this.state === this.STATE_BROWSING)
         {     
            // Do nothing (but close the panel, which happens below)
         }
         else if (this.state === this.STATE_UPLOADING)
         {
            this._cancelAllUploads();

            // Inform the user if any files were uploaded before the rest was cancelled
            var noOfUploadedFiles = 0;
            for (var i in this.fileStore)
            {
               if (this.fileStore[i] && this.fileStore[i].state === this.STATE_SUCCESS)
               {
                  noOfUploadedFiles++;
               }
            }
            if (noOfUploadedFiles > 0)
            {
               message = Alfresco.util.message("label.cancelStatus", this.name);
               message = YAHOO.lang.substitute(message, {"0": noOfUploadedFiles});
            }

            // Tell the document list to refresh itself if present
            YAHOO.Bubbling.fire("onDoclistRefresh", {currentPath: this.showConfig.path});
         }
         else if (this.state === this.STATE_FINISHED)
         {
            // Tell the document list to refresh itself if present
            YAHOO.Bubbling.fire("onDoclistRefresh", {currentPath: this.showConfig.path});
         }

         // Reset the message for empty datatables for other components
         YAHOO.widget.DataTable.MSG_EMPTY = this.previousFileListEmptyMessage;

         // Hide the panel
         this.panel.hide();
         
         // Firefox 2 isn't always great at hiding the panel
         /*

         Since panel is set to null it will make the panel load its template
         from the server everytime show() is called. Find another solution...

         if (YAHOO.env.ua.gecko == 1.8)
         {
            this.panel.destroy();
            this.panel = null;
         }
         */

         // Remove all files and references for this upload "session"
         this._clear();

         // Inform the user if any files were uploaded before the rest was cancelled
         if (message)
         {
            Alfresco.util.PopupManager.displayPrompt({text: message});
         }
      },

      /**
       * Fired when the user clicks the upload button.
       * Starts the uploading and adjusts the gui.
       *
       * @method onBrowseButtonClick
       * @param event {object} a Button "click" event
       */
      onUploadButtonClick: function FU_onUploadButtonClick()
      {
         if (this.state === this.STATE_BROWSING)
         {
            // Change the stat to uploading state and adjust the gui
            var length = this.dataTable.getRecordSet().getLength();
            if (length > 0)
            {
               this.state = this.STATE_UPLOADING;
               this.widgets.uploadButton.set("label", Alfresco.util.message("button.uploading", this.name));
               this.widgets.uploadButton.set("disabled", true);
               this.widgets.browseButton.set("disabled", true);
            }
            // And start uploading from the queue
            this._uploadFromQueue(2);
         }
      },

      /**
       * Adjust the gui according to the config passed into the show method.
       *
       * @method _applyConfig
       * @private
       */
      _applyConfig: function FU__applyConfig()
      {
         var Dom = YAHOO.util.Dom;

         // Set the panel title
         var title;
         if(this.showConfig.mode === this.MODE_SINGLE_UPLOAD)
         {
            title = Alfresco.util.message("header.singleUpload", this.name);
         }
         else if(this.showConfig.mode === this.MODE_MULTI_UPLOAD)
         {
            title = Alfresco.util.message("header.multiUpload", this.name);
         }
         else if(this.showConfig.mode === this.MODE_SINGLE_UPDATE)
         {
            title = Alfresco.util.message("header.singleUpdate", this.name);
         }
         this.titleText["innerHTML"] = title;

         if (this.showConfig.mode === this.MODE_SINGLE_UPDATE)
         {
            // Display the version input form
            if(Dom.hasClass(this.versionSection, "hiddenComponents"))
            {
               Dom.removeClass(this.versionSection, "hiddenComponents");
            }
         }
         else
         {
            // Hide the version input form
            if(!Dom.hasClass(this.versionSection, "hiddenComponents"))
            {
               Dom.addClass(this.versionSection, "hiddenComponents");
            }
         }

         if(this.showConfig.mode === this.MODE_MULTI_UPLOAD)
         {
            // Show the help label for how to select multiple files
            if(Dom.hasClass(this.multiSelectText, "hiddenComponents"))
            {
               Dom.removeClass(this.multiSelectText, "hiddenComponents");
            }

            // Make the file list long
            this.dataTable.set("height", "204px");
         }
         else
         {
            // Hide the help label for how to select multiple files
            if(!Dom.hasClass(this.multiSelectText, "hiddenComponents"))
            {
               Dom.addClass(this.multiSelectText, "hiddenComponents");
            }

            // Make the file list short
            this.dataTable.set("height", "40px");
         }

      },

      /**
       * Helper function to create the data table and its cell formatter.
       *
       * @method _createEmptyDataTable
       * @private
       */
      _createEmptyDataTable: function FU__createEmptyDataTable()
      {

         var Dom = YAHOO.util.Dom;

         /**
          * Save a reference of 'this' so that the formatter below can use it
          * later (since the formatter method gets called with another scope
          * than 'this').
          */
         var myThis = this;

         /**
          * Responsible for rendering the left row in the data table
          *
          * @param el HTMLElement the td element
          * @param oRecord Holds the file data object
          */
         this.formatLeftCell = function(el, oRecord) {
            myThis._formatCellElements(el, oRecord, myThis.fileItemTemplates.left);
         };

         /**
          * Responsible for rendering the center row in the data table
          *
          * @param el HTMLElement the td element
          * @param oRecord Holds the file data object
          */
         this.formatCenterCell = function(el, oRecord) {
            myThis._formatCellElements(el, oRecord, myThis.fileItemTemplates.center);
         };

         /**
          * Responsible for rendering the right row in the data table
          *
          * @param el HTMLElement the td element
          * @param oRecord Holds the file data object
          */
         this.formatRightCell = function(el, oRecord) {
            myThis._formatCellElements(el, oRecord, myThis.fileItemTemplates.right);
         };

         /**
          * Takes a left, center or right column template and looks for expected
          * html components and vcreates yui objects or saves references to
          * them so they can be updated during the upload progress.
          *
          * @param el HTMLElement the td element
          * @param oRecord Holds the file data object
          * @param template the template to display in the column
          */
         this._formatCellElements = function(el, oRecord, template) {

            // Set the state for this file(/row) if it hasn't been set
            var flashId = oRecord.getData()["id"];
            if(!this.fileStore[flashId])
            {
               this.fileStore[flashId] = { state: this.STATE_BROWSING };
            }

            // create an instance from the template and give it a uniqueue id.
            var cell = new YAHOO.util.Element(el);
            var templateInstance = template.cloneNode(true);
            templateInstance.setAttribute("id", templateInstance.getAttribute("id") + flashId);

            // Save references to elements that will be updated during upload.
            var progress = Dom.getElementsByClassName("fileupload-progressSuccess-span", "span", templateInstance);
            if(progress.length == 1)
            {
               this.fileStore[flashId].progress = progress[0];
            }
            var progressInfo = Dom.getElementsByClassName("fileupload-progressInfo-span", "span", templateInstance);
            if(progressInfo.length == 1)
            {
               // Display the file size in human readable format after the filename.
               var readableSize = new Number(oRecord.getData()["size"]);
               readableSize = Alfresco.util.formatFileSize(readableSize);
               var fileInfoStr = oRecord.getData()["name"] + " (" + readableSize + ")";

               // Display the file name and size.
               progressInfo = progressInfo[0];
               this.fileStore[flashId].progressInfo = progressInfo;
               this.fileStore[flashId].progressInfo["innerHTML"] = fileInfoStr;
            }

            /**
             * Save a reference to the contentType dropdown so we can find each
             * files contentType before upload.
             */
            var contentType = Dom.getElementsByClassName("fileupload-contentType-menu", "select", templateInstance);
            if(contentType.length == 1)
            {
               this.fileStore[flashId].contentType = contentType[0];
            }

            // Save references to elements that will be updated during upload.
            var progressPercentage = Dom.getElementsByClassName("fileupload-percentage-span", "span", templateInstance);
            if(progressPercentage.length == 1)
            {
               this.fileStore[flashId].progressPercentage = progressPercentage[0];
            }

            // Create a yui button for the fileButton.
            var fButton = Dom.getElementsByClassName("fileupload-file-button", "button", templateInstance);
            if(fButton.length == 1)
            {
               var fileButton = new YAHOO.widget.Button(fButton[0], {type: "button"});
               fileButton.subscribe("click", function(){ this._onFileButtonClickHandler(flashId, oRecord.getId()); }, this, true);
               this.fileStore[flashId].fileButton = fileButton;
            }

            // Insert the templateInstance to the column.
            cell.appendChild (templateInstance);
         };


         // Definition of the data table column
         var myColumnDefs = [
            {className:"col-left", resizable: false, formatter: this.formatLeftCell},
            {className:"col-center", resizable: false, formatter: this.formatCenterCell},
            {className:"col-right", resizable: false, formatter: this.formatRightCell}
         ];

         // The data tables underlying data source.
         var myDataSource = new YAHOO.util.DataSource([]);
         myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
         myDataSource.responseSchema =
         {
            fields: ["id","name","created","modified","type", "size", "progress"]
         };

         /**
          * Create the data table.
          * Set the properties even if they will get changed in applyConfig
          * afterwards, if not set here they will not be changed later.
          */
         YAHOO.widget.DataTable._bStylesheetFallback = !!YAHOO.env.ua.ie;
         var dataTableDiv = Dom.get(this.id + "-filelist-table");
         this.dataTable = new YAHOO.widget.DataTable(dataTableDiv, myColumnDefs, myDataSource,
         {
            scrollable: true,
            height: "1px",
            renderLoopSize: 5
         });
         this.dataTable.subscribe("rowAddEvent", this.onRowAddEvent, this, true);
      },

      /**
       * Prepares the gui and shows the panel.
       *
       * @method _showPanel
       * @private
       */
      _showPanel: function FU__showPanel()
      {
         // Reset references and the gui before showing it
         this.state = this.STATE_BROWSING;
         this.noOfFailedUploads = 0;
         this.noOfSuccessfulUploads = 0;
         this.statusText["innerHTML"] = "&nbsp;";
         this.widgets.uploadButton.set("label", Alfresco.util.message("button.upload", this.name));
         this.widgets.uploadButton.set("disabled", true);
         this.widgets.cancelOkButton.set("label", Alfresco.util.message("button.cancel", this.name));
         this.widgets.cancelOkButton.set("disabled", false);
         this.widgets.browseButton.set("disabled", false);

         // Apply the config before it is showed
         this._applyConfig();

         // Show the upload panel
         this.panel.show();
      },

      /**
       * Helper function to create a unique file token from the file data object
       *
       * @method _getUniqueFileToken
       * @param data {object} a file data object describing a file
       * @private
       */
      _getUniqueFileToken: function FU__getUniqueFileToken(data)
      {
         return data.name + ":" + data.size + ":" + data.cDate + ":" + data.mDate
      },

      /**
       * Update the status label with the latest information about the upload progress
       *
       * @method _updateStatus
       * @private
       */
      _updateStatus: function FU__updateStatus(){
         // Update the status label with the latest information about the upload progress
         var status = Alfresco.util.message("label.uploadStatus", this.name);
         status = YAHOO.lang.substitute(status,
         {
            "0" : this.noOfSuccessfulUploads,
            "1" : this.dataTable.getRecordSet().getLength(),
            "2" : this.noOfFailedUploads
         });
         this.statusText["innerHTML"] = status; 
      },

      /**
       * Checks if all files are finished (successfully uploaded or failed)
       * and if so adjusts the gui.
       *
       * @method _adjustGuiIfFinished
       * @private
       */
      _adjustGuiIfFinished: function FU__adjustGuiIfFinished()
      {
         // Go into finished state if all files are finished: successful or failures
         for (var i in this.fileStore)
         {
            if (this.fileStore[i] &&
               this.fileStore[i].state !== this.STATE_SUCCESS &&
               this.fileStore[i].state !== this.STATE_FAILURE)
            {
               return;
            }
         }
         this.state = this.STATE_FINISHED;
         this.widgets.cancelOkButton.set("label", Alfresco.util.message("button.ok", this.name));
         this.widgets.uploadButton.set("disabled", true);
      },

      /**
       * Starts to upload as many files as specified by noOfUploadsToStart
       * as long as there are files left to upload.
       *
       * @method _uploadFromQueue
       * @param noOfUploadsToStart
       * @private
       */
      _uploadFromQueue: function FU__uploadFromQueue(noOfUploadsToStart){

         // Find files to upload
         var startedUploads = 0;
         var length = this.dataTable.getRecordSet().getLength();
         for (var i = 0; i < length && startedUploads < noOfUploadsToStart; i++)
         {
            var record = this.dataTable.getRecordSet().getRecord(i);
            var flashId = record.getData("id");
            var fileInfo = this.fileStore[flashId];
            if (fileInfo.state === this.STATE_BROWSING)
            {
               // Upload has NOT been started for this file, start it now
               fileInfo.state = this.STATE_UPLOADING;

               // The contentType that the file should be uploaded as.
               var contentType = fileInfo.contentType.options[fileInfo.contentType.selectedIndex].value;
               //var url = Alfresco.constants.PROXY_URI + "api/upload?alf_ticket=" + Alfresco.constants.ALF_TICKET;
               var url = "http://localhost:8080/alfresco/service/api/upload?alf_ticket=" + Alfresco.constants.ALF_TICKET;
               this.uploader.upload(flashId, url, "POST",
               {
                  path: this.showConfig.path,
                  siteId: this.showConfig.siteId,
                  componentId: this.showConfig.componentId,
                  contentType: contentType
               }, "filedata");
               startedUploads++;
            }
         }
      },

      /**
       * Cancels all uploads inside the flash movie.
       *
       * @method _cancelAllUploads
       * @private
       */
      _cancelAllUploads: function FU__cancelAllUploads()
      {
         // Cancel all uploads inside the flash movie
         var length = this.dataTable.getRecordSet().getLength();
         for (var i = 0; i < length; i++){
            var record = this.dataTable.getRecordSet().getRecord(i);
            var flashId = record.getData("id");
            this.uploader.cancel(flashId);
         }
      },

      /**
       * Remove all references to files inside the data table, flash movie
       * and the this class references.
        *
       * @method _clear
       * @private
       */
      _clear: function FU__clear()
      {
         /**
          * Remove all references to files inside the data table, flash movie
          * and this class's references.
          */
         var length = this.dataTable.getRecordSet().getLength();
         this.addedFiles = {};
         this.fileStore = {};
         this.dataTable.deleteRows(0, length);
         this.uploader.clearFileList();
      }

   };

})();

/* Dummy instance to load optional YUI components early */
new Alfresco.module.FileUpload(null);
