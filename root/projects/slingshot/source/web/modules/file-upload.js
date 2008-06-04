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
 *    title: "Upload file(s)",
 *    filter: [],
 *    multiSelect: true,
 *    noOfVisibleRows: 5,
 *    versionInput: false
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
       * The default config for the gui state for the uploader.
       * The user can override these properties in the show() method to use the
       * uploader for both single and multi uploads.
       *
       * @property defaultShowConfig
       * @type object
       */
      defaultShowConfig: {
         siteId: null,
         componentId: null,
         path: null,
         title: "",
         multiSelect: false,
         filter: [],
         noOfVisibleRows: 1,
         versionInput: true
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
       * HTMLElement of type div that is used to as a template to display a
       * row in the file table list. It is loaded dynamically from the server
       * and then cloned for each row in the file list.
       *
       * @property fileItemTemplate
       * @type HTMLElement
       */
      fileItemTemplate: null,

      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function()
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
       *    title: {string},        // dialog title
       *    multiSelect: {boolean}, // true if the user shall be able to select multiple files, false is default
       *    filter: {array},        // limits what kind of files the user can select in the OS file selector
       *    versionInput: {boolean} // true if version input should be displayed, default is true
       * }                                
       */
      show: function(config)
      {
         // Remember the the label used by other components so we can reset it at close
         this.previousFileListEmptyMessage = YAHOO.widget.DataTable.MSG_EMPTY;
         YAHOO.widget.DataTable.MSG_EMPTY = "No files to display. Click 'Browse' select files to upload.";

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
       * Fired by YUI:s DataTable when a row has been added to the data table list.
       * Keeps track of added files.
       *
       * @method onReady
       * @param event {object} a DataTable "rowAdd" event
       */
      onRowAddEvent: function(event)
      {
         // Since the flash movie allows the user to select one file several
         // times we need to keep track of the selected files by our selves
         var uniqueFileToken = this._getUniqeFileToken(event.record.getData());
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
      onFileSelect: function(event)
      {
         // For each time the user select new files, all the previous selected
         // files also are included in the event.fileList. Make sure we only
         // add files to the table that haven's been added before.
         for (var i in event.fileList)
         {
            var data = YAHOO.widget.DataTable._cloneObject(event.fileList[i]);
            if (!this.addedFiles[this._getUniqeFileToken(data)])
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
      onUploadStart: function(event)
      {
         // Hide the contentType drop down if it wasn't selected already
         var Dom = YAHOO.util.Dom;
         var fileInfo = this.fileStore[event["id"]];
         if (!Dom.hasClass(fileInfo.contentType, "hiddenComponents"))
         {
            Dom.addClass(fileInfo.contentType, "hiddenComponents");
         }

         // Make sure we know have gone into upload state
         fileInfo.state = this.STATE_UPLOADING;
      },

      /**
       * Fired by YIU:s Uploader during the transfer for one of the files.
       * Adjusts the gui and its progress bars.
       *
       * @method onUploadComplete
       * @param event {object} an Uploader "uploadProgress" event
       */
      onUploadProgress: function(event)
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
       * Adjusts the gui and calls for another file to upload.
       *
       * @method onUploadComplete
       * @param event {object} an Uploader "uploadComplete" event
       */
      onUploadComplete: function(event)
      {
         // The individual file has been transfered completely
         // Now adjust the gui for the individual file row
         var fileInfo = this.fileStore[event["id"]];
         fileInfo.state = this.STATE_SUCCESS;
         fileInfo.fileButton.set("disabled", true);

         // Add the label "Successful" after the filename
         fileInfo.progressInfo["innerHTML"] = fileInfo.progressInfo["innerHTML"] + " Success";

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
       * Fired by YIU:s Uploader when transfer is completed for ALL files.
       * Doesn't do anything.
       *
       * @method onUploadCompleteData
       * @param event {object} an Uploader "uploadCompleteData" event
       */
      onUploadCompleteData: function(event)
      {
          // We don't do anything since we handle the uploading queue ourselves.
      },

      /**
       * Fired by YIU:s Uploader when transfer has been cancelled for one of the files.
       * Doesn't do anything.
       *
       * @method onUploadCancel
       * @param event {object} an Uploader "uploadCancel" event
       */
      onUploadCancel: function(event)
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
      onUploadError: function(event)
      {
         var fileInfo = this.fileStore[event["id"]];

         // This sometimes gets called twice, make sure we only adjust the gui once
         if (fileInfo.state !== this.STATE_FAILURE)
         {
            fileInfo.state = this.STATE_FAILURE;

            // Add the label "Failure" to the filename
            fileInfo.progressInfo["innerHTML"] = fileInfo.progressInfo["innerHTML"] + " Failure";

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
      onBrowseButtonClick: function(event)
      {
         // Make sure we know have gone into browsing state
         this.state = this.STATE_BROWSING;

         // Tell the flash movie to display the OS's file selector dialog
         this.uploader.browse(this.showConfig.multiSelect, this.showConfig.filter);
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
      _onFileButtonClickHandler: function(flashId, recordId)
      {
         /**
          * The file button has been clicked to remove a file.
          * Remove the file from the datatable and all references to it.
          */
         var r = this.dataTable.getRecordSet().getRecord(recordId);
         this.addedFiles[this._getUniqeFileToken(r.getData())] = null;
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
      onCancelOkButtonClick: function()
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
               message = "The remaining upload(s) has been cancel, at least " + noOfUploadedFiles + " file(s) were uploaded.";
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
         if (YAHOO.env.ua.gecko == 1.8)
         {
            this.panel.destroy();
            this.panel = null;
         }

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
      onUploadButtonClick: function()
      {
         if (this.state === this.STATE_BROWSING)
         {
            // Change the stat to uploading state and adjust the gui
            var length = this.dataTable.getRecordSet().getLength();
            if (length > 0)
            {
               this.state = this.STATE_UPLOADING;
               this.widgets.uploadButton.set("label", "Uploading...");
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
      _applyConfig: function()
      {
         var Dom = YAHOO.util.Dom;

         // Set the panel title
         this.titleText["innerHTML"] = this.showConfig.title;

         // Display the version input form
         if (this.showConfig.versionInput)
         {
            if (this.showConfig.multiSelect)
            {
               // Doesn't make sense since version input only applies to a single file
               throw new Error("Cannot show version input fields for multiple files");
            }
            else
            {
               Dom.setStyle(this.versionSection, "display", "true");
            }
         }
         else
         {
            Dom.setStyle(this.versionSection, "display", "none");
         }

         // Display the help label for how to select multiple files
         Dom.setStyle(this.multiSelectText, "display", (this.showConfig.multiSelect ? "true" : "none"));
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
      onTemplateLoaded: function(response)
      {
         var Dom = YAHOO.util.Dom;

         // Remember the label for empty data tables so we can reset it on close
         this.previousFileListEmptyMessage = YAHOO.widget.DataTable.MSG_EMPTY;
         YAHOO.widget.DataTable.MSG_EMPTY = "No files to display. Click 'Browse' select files to upload.";

         // Inject the template from the XHR request into a new DIV element
         var containerDiv = document.createElement("div");
         containerDiv.innerHTML = response.serverResponse.responseText;

         // The panel is created from the HTML returned in the XHR request, not the container
         var dialogDiv = YAHOO.util.Dom.getFirstChild(containerDiv);

         this.panel = new YAHOO.widget.Panel(dialogDiv,
         {
            modal: true,
            draggable: false,
            fixedcenter: true,
            visible: false,
            close: false,
            width: "622px"
         });
         this.panel.render(document.body);

         // Save a reference to the file row template that is hidden inside the markup
         this.fileItemTemplate = Dom.get(this.id + "-fileItemTemplate-div");

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

         // Create a buttongroup for versions
         var vGroup = Dom.get(this.id + "-version-buttongroup");
         var oButtonGroup1 = new YAHOO.widget.ButtonGroup(vGroup);

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
       * Helper function to create the data table and its cell formatter.
       *
       * @method _createEmptyDataTable
       * @private
       */
      _createEmptyDataTable: function()
      {

         var Dom = YAHOO.util.Dom;

         /**
          * Save a reference of 'this' so that the formatter below can use it
          * later. This is hard other wise since the formatter method gets
          * called with another scope than 'this'.
          */
         var myThis = this;

         /**
          * Responsible for rendering a row in the data table
          *
          * @param el HTMLElement the td element
          * @param oRecord Holds the file data object
          */
         this.formatCell = function(el, oRecord) {

            // Get the id that is used to reference each file in the uploader
            var flashId = oRecord.getData()["id"];

            // Wrap the HTMLELement el in YIU Element
            var cell = new YAHOO.util.Element(el);

            // Display the file size in human readable format after the filename
            var readableSize = new Number(oRecord.getData()["size"]);
            readableSize = Alfresco.util.formatFileSize(readableSize);
            var fileInfoStr = oRecord.getData()["name"] + " (" + readableSize + ")";

            /**
             * Use the hidden template found in the gui provided by the server
             * to display each row in the table. Give it a unique id.
             */
            var templateInstance = myThis.fileItemTemplate.cloneNode(true);
            templateInstance.setAttribute("id", myThis.id + "-fileItemTemplate-div-" + flashId);

            /**
             * Find parts in the gui that will be changed during the upload
             * progress and therefore should be referenced for easy access.
             */
            var progress = Dom.getElementsByClassName("fileupload-progressSuccess-span", "span", templateInstance)[0];
            var progressInfo = Dom.getElementsByClassName("fileupload-progressInfo-span", "span", templateInstance)[0];
            progressInfo["innerHTML"] = fileInfoStr;
            var contentType = Dom.getElementsByClassName("fileupload-contentType-menu", "select", templateInstance)[0];
            var progressPercentage = Dom.getElementsByClassName("fileupload-percentage-span", "span", templateInstance)[0];

            // Create a yui button for the fileButton
            var fButton = Dom.getElementsByClassName("fileupload-file-button", "button", templateInstance)[0];
            var fileButton = new YAHOO.widget.Button(fButton, {type: "button"});
            fileButton.subscribe("click", function(){ myThis._onFileButtonClickHandler(flashId, oRecord.getId()); }, myThis, true);

            /**
             * Save all the references in the fileStore object for easy access
             * during the upload progress during which they should be altered.
             */
            myThis.fileStore[flashId] =
            {
               fileButton: fileButton,
               state: myThis.STATE_BROWSING,
               progress: progress,
               progressInfo: progressInfo,
               progressPercentage: progressPercentage,
               contentType: contentType
            };

            // Insert the new row inside the browsers DOM.
            cell.appendChild (templateInstance);
         };

         // Definition of the data table column
         var myColumnDefs = [
            {key:"id", label: "Files", width:580, resizable: false, formatter: this.formatCell}
         ];

         // The data tables underlying data source.
         var myDataSource = new YAHOO.util.DataSource([]);
         myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
         myDataSource.responseSchema =
         {
            fields: ["id","name","created","modified","type", "size", "progress"]
         };

         // Create the data table.
         var dataTableDiv = Dom.get(this.id + "-filelist-table");
         this.dataTable = new YAHOO.widget.DataTable(dataTableDiv, myColumnDefs, myDataSource,
         {
            scrollable: true,
            height: "200px",
            width: "600px",
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
      _showPanel: function()
      {
         // Reset references and the gui before showing it
         this.state = this.STATE_BROWSING;
         this.noOfFailedUploads = 0;
         this.noOfSuccessfulUploads = 0;
         this.statusText["innerHTML"] = "&nbsp;";
         this.widgets.uploadButton.set("label", "Upload Files");
         this.widgets.uploadButton.set("disabled", true);
         this.widgets.cancelOkButton.set("label", "Cancel");
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
       * @method _getUniqeFileToken
       * @param data {object} a file data object describing a file
       * @private
       */
      _getUniqeFileToken: function(data)
      {
         return data.name + ":" + data.size + ":" + data.cDate + ":" + data.mDate
      },

      /**
       * Update the status label with the latest information about the upload progress
       *
       * @method _updateStatus
       * @private
       */
      _updateStatus: function(){
         // Update the status label with the latest information about the upload progress
         var status = "Status: " + this.noOfSuccessfulUploads + "/" +
                      this.dataTable.getRecordSet().getLength() + " uploaded";
         if (this.noOfFailedUploads > 0)
         {
            status +=" (" + this.noOfFailedUploads + " failed)";
         }
         this.statusText["innerHTML"] = status; 
      },

      /**
       * Checks if all files are finished (successfully uploaded or failed)
       * and if so adjusts the gui.
       *
       * @method _adjustGuiIfFinished
       * @private
       */
      _adjustGuiIfFinished: function()
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
         this.widgets.cancelOkButton.set("label", "Ok");
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
      _uploadFromQueue: function(noOfUploadsToStart){

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
               var url = Alfresco.constants.PROXY_URI + "api/upload?alf_ticket=" + Alfresco.constants.ALF_TICKET;
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
      _cancelAllUploads: function()
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
      _clear: function()
      {
         /**
          * Remove all references to files inside the data table, flash movie
          * and this class's references.
          */
         var length = this.dataTable.getRecordSet().getLength();
         this.uploader.clearFileList();
         this.addedFiles = {};
         this.fileStore = {};
         this.dataTable.deleteRows(0, length);
      }

   };

})();

/* Dummy instance to load optional YUI components early */
new Alfresco.module.FileUpload(null);
