/*
 *** Alfresco.module.FileUpload
*/

(function()
{
   Alfresco.module.FileUpload = function(containerId)
   {
      this.name = "Alfresco.module.FileUpload";
      this.id = containerId;

      this.swf = Alfresco.constants.URL_CONTEXT + "yui/uploader/assets/uploader.swf";

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "container", "datatable", "datasource", "uploader"], this.componentsLoaded, this);

      return this;
   }

   Alfresco.module.FileUpload.prototype =
   {

      componentsLoaded: function()
      {
         YAHOO.widget.Uploader.SWFURL = this.swf;
         YAHOO.widget.Column.prototype.minWidth = 0;

         /* Shortcut for dummy instance */
         if (this.id === null)
         {
            return;
         }
      },

      panel: null,
      browseButton: null,
      startStopButton: null,
      cancelOkButton: null,

      defaultConfig: {
         siteId: null,
         componentId: null,
         path: null,
         title: "",
         multiSelect: false,
         filter: [],
         noOfVisibleRows: 1,
         versionInput: true
      },

      // State
      state: 1,
      BROWSING: 1,
      UPLOADING: 2,
      UPLOADED: 3,
      FAILURE: 4,
      SUCCESS: 5,

      config: {},

      uploader: null,
      hasRequestedVersion: false,
      dataTable: null,
      titleText: null,
      versionSection: null,
      multiSelectText:null,
      fileItemTemplate: null,
      addedFiles: {}, // unique id of files data to keep track of which ones that have been added
      fileStore: {}, // flashId : {fileButton, state}

      previousFileListEmptyMessage: null,

      show: function(userConfig)
      {
         this.previousFileListEmptyMessage = YAHOO.widget.DataTable.MSG_EMPTY;
         YAHOO.widget.DataTable.MSG_EMPTY = "No files to display. Click 'Browse' select files to upload.";

         this.config = YAHOO.lang.merge(this.defaultConfig, userConfig);
         if(this.config.path === undefined)
         {
             throw new Error("A path must be provided");
         }
         else if(this.config.path.length == 0)
         {
            this.config.path = "/";
         }
         if(this.panel)
         {
            this.showPanel();
         }
         else
         {
            Alfresco.util.Ajax.request(
            {
               url: Alfresco.constants.URL_SERVICECONTEXT + "modules/file-upload?htmlid=" + this.id,
               successCallback:
               {
                  fn: this.templateLoaded,
                  scope: this
               },
               failureMessage: "Could not load file upload template"
            });
         }
      },

      applyConfig: function()
      {
         var Dom = YAHOO.util.Dom;
         this.titleText["innerHTML"] = this.config.title;
         if(this.config.versionInput)
         {
            if(this.config.multiSelect)
            {
               alert("Cannot show version input fields for multiple files");
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
         Dom.setStyle(this.multiSelectText, "display", (this.config.multiSelect ? "true" : "none"));
      },

      templateLoaded: function(response)
      {
         this.previousFileListEmptyMessage = YAHOO.widget.DataTable.MSG_EMPTY;
         YAHOO.widget.DataTable.MSG_EMPTY = "No files to display. Click 'Browse' select files to upload.";

         var Dom = YAHOO.util.Dom;

         var div = document.createElement("div");
         div.innerHTML = response.serverResponse.responseText;
         this.panel = new YAHOO.widget.Panel(div,
         {
            modal: true,
            draggable: false,
            fixedcenter: true,
            visible: false,
            close: false,
            width: "650px"
         });
         this.panel.render(document.body);

         this.fileItemTemplate = Dom.get(this.id + "-fileItemTemplate-div");

         this.createEmptyDataTable();

         this.titleText = Dom.get(this.id + "-title-span");

         this.browseButton = new YAHOO.widget.Button(this.id + "-browse-button", {type: "button"});
         this.browseButton.subscribe("click", this.browse, this, true);

         this.versionSection = Dom.get(this.id + "-versionSection-div");
         this.multiSelectText = Dom.get(this.id + "-multiSelect-span");

         var vGroup = Dom.get(this.id + "-version-buttongroup");
         var oButtonGroup1 = new YAHOO.widget.ButtonGroup(vGroup);

         this.startStopButton = new YAHOO.widget.Button(this.id + "-startStop-button", {type: "button"});
         this.startStopButton.subscribe("click", this.handleStartStopClick, this, true);

         this.cancelOkButton = new YAHOO.widget.Button(this.id + "-cancelOk-button", {type: "button"});
         this.cancelOkButton.subscribe("click", this.handleCancelOkClick, this, true);

         this.uploader = new YAHOO.widget.Uploader(this.id + "-flashuploader-div");
         this.uploader.subscribe("fileSelect", this.onFileSelect, this, true);
         this.uploader.subscribe("uploadComplete",this.onUploadComplete, this, true);
         this.uploader.subscribe("uploadProgress",this.onUploadProgress, this, true);
         this.uploader.subscribe("uploadStart",this.onUploadStart, this, true);
         this.uploader.subscribe("uploadCancel",this.onUploadCancel, this, true);
         this.uploader.subscribe("uploadCompleteData",this.onUploadCompleteData, this, true);
         this.uploader.subscribe("uploadError",this.onUploadError, this, true);

         this.showPanel();
      },

      createEmptyDataTable: function()
      {

         var Dom = YAHOO.util.Dom;

         var myThis = this;

         this.formatCell = function(el, oRecord) {
            var flashId = oRecord.getData()["id"];
            var cell = new YAHOO.util.Element(el);
            var readableSize = oRecord.getData()["size"] + "b";
            var fileInfoStr = oRecord.getData()["name"] + " (" + readableSize + ")";

            var templateInstance = myThis.fileItemTemplate.cloneNode(true);
            templateInstance.setAttribute("id", myThis.id + "-fileItemTemplate-div-" + flashId);

            var progress = Dom.getElementsByClassName("fileupload-progressSuccess-span", "span", templateInstance)[0];

            var progressInfo = Dom.getElementsByClassName("fileupload-progressInfo-span", "span", templateInstance)[0];
            progressInfo["innerHTML"] = fileInfoStr;

            var contentType = Dom.getElementsByClassName("fileupload-contentType-menu", "select", templateInstance)[0];

            var progressPercentage = Dom.getElementsByClassName("fileupload-percentage-span", "span", templateInstance)[0];
            //progressPercentage["innerHTML"] = "";

            var fButton = Dom.getElementsByClassName("fileupload-file-button", "input", templateInstance)[0];
            var fileButton = new YAHOO.widget.Button(fButton, {type: "button"});
            fileButton.subscribe("click", function(){ myThis.handleFileButtonClick(flashId, oRecord.getId()); }, myThis, true);
            myThis.fileStore[flashId] =
            {
               fileButton: fileButton,
               state: myThis.BROWSING,
               progress: progress,
               progressInfo: progressInfo,
               progressPercentage: progressPercentage,
               contentType: contentType
            };

            cell.appendChild (templateInstance);
         };

         var myColumnDefs = [
            {key:"id", label: "Files", width:600, resizable: false, formatter: this.formatCell} //this.formatFileInfoCell}
         ];

         var myDataSource = new YAHOO.util.DataSource([]);
         myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
         myDataSource.responseSchema =
         {
            fields: ["id","name","created","modified","type", "size", "progress"]
         };

         var dataTableDiv = Dom.get(this.id + "-filelist-table");
         this.dataTable = new YAHOO.widget.DataTable(dataTableDiv, myColumnDefs, myDataSource,
         {
            scrollable: true,
            height: "200px"
         });
         this.dataTable.subscribe("rowAddEvent", this.rememberAddedFiles, this, true);
      },

      showPanel: function()
      {
         this.state = this.BROWSING;
         this.startStopButton.set("label", "Upload Files");
         this.startStopButton.set("disabled", true);
         this.cancelOkButton.set("label", "Cancel");
         this.cancelOkButton.set("disabled", false);
         this.browseButton.set("disabled", false);
         this.applyConfig();
         this.panel.show();
      },

      rememberAddedFiles: function(oArgs)
      {
         var uniqueFileToken = this.getUniqeFileToken(oArgs.record.getData());
         this.addedFiles[uniqueFileToken] = oArgs.record.getId();
      },

      getUniqeFileToken: function(data)
      {
         return data.name + ":" + data.size + ":" + data.cDate + ":" + data.mDate
      },

      onFileSelect: function(event)
      {
         var filesBeforeBrowse = this.dataTable.getRecordSet().getLength();
         for(var i in event.fileList)
         {
            var data = YAHOO.widget.DataTable._cloneObject(event.fileList[i]);
            if(!this.addedFiles[this.getUniqeFileToken(data)])
            {
               this.dataTable.addRow(data, 0);
            }
         }
         if(filesBeforeBrowse == 0){
            this.startStopButton.set("disabled", false);
         }
      },

      onUploadStart: function(event)
      {
         var Dom = YAHOO.util.Dom;
         var fileInfo = this.fileStore[event["id"]];
         //fileInfo.fileButton.set("label", "Cancel");
         if(!Dom.hasClass(fileInfo.contentType, "hiddenComponents"))
         {
            Dom.addClass(fileInfo.contentType, "hiddenComponents");
         }
         fileInfo.state = this.UPLOADING;
      },

      onUploadComplete: function(event)
      {
         var fileInfo = this.fileStore[event["id"]];
         fileInfo.state = this.SUCCESS;
         fileInfo.fileButton.set("disabled", true);
         fileInfo.progressInfo["innerHTML"] = fileInfo.progressInfo["innerHTML"] + " Success";
         fileInfo.progress.setAttribute("class", "fileupload-progressFinished-span");                     
         YAHOO.util.Dom.setStyle(fileInfo.progress, "left", 0 + "px");
         this.uploadFromQueue(1);
         this.adjustIfUploaded();
      },

      adjustIfUploaded: function()
      {
         // Change gui if all files are uploaded or failed
         for(var i in this.fileStore)
         {
            if(this.fileStore[i] &&
               this.fileStore[i].state !== this.SUCCESS &&
               this.fileStore[i].state !== this.FAILURE)
            {
               return;
            }
         }
         this.state = this.UPLOADED;
         this.cancelOkButton.set("label", "Ok");
         this.startStopButton.set("disabled", true);

      },

      onUploadProgress: function(event)
      {
         var flashId = event["id"];
         var fileInfo = this.fileStore[flashId];

         // Set percentage
         var uploaded = event["bytesLoaded"] / event["bytesTotal"];
         fileInfo.progressPercentage["innerHTML"] = Math.round(uploaded * 100) + "%";

         // Set progress position
         var left = (-400 + (uploaded * 400));
         YAHOO.util.Dom.setStyle(fileInfo.progress, "left", left + "px");
      },

      onUploadCancel: function(event)
      {
      },

      onUploadCompleteData: function(event)
      {
      },

      onUploadError: function(event)
      {
         var fileInfo = this.fileStore[event["id"]];
         if(fileInfo.state !== this.FAILURE)
         {
            fileInfo.state = this.FAILURE;
            fileInfo.progressInfo["innerHTML"] = fileInfo.progressInfo["innerHTML"] + " Failure";
            fileInfo.progress.setAttribute("class", "fileupload-progressFailure-span");
            fileInfo.fileButton.set("disabled", true);
            YAHOO.util.Dom.setStyle(fileInfo.progress, "left", 0 + "px");
            this.uploadFromQueue(1);            
            this.adjustIfUploaded();
         }
      },

      browse: function()
      {
         this.state = this.BROWSING;
         this.uploader.browse(this.config.multiSelect, this.config.filter);
      },

      handleCancelOkClick: function()
      {
         var message;
         if(this.state === this.BROWSING)
         {
            // Do nothing but close
         }
         else if(this.state === this.UPLOADING)
         {
            this.cancelAllUploads();
            // Inform the ise if any files were uploaded before the rest was cancelled
            var noOfUploadedFiles = 0;
            for(var i in this.fileStore)
            {
               if(this.fileStore[i] && this.fileStore[i].state === this.SUCCESS)
               {
                  noOfUploadedFiles++;
               }
            }
            if(noOfUploadedFiles > 0)
            {
               message = "The remaining upload(s) has been cancel, at least " + noOfUploadedFiles + " file(s) were uploaded.";
            }
            YAHOO.Bubbling.fire("onDoclistRefresh", {currentPath: this.config.path});
         }
         else if(this.state === this.UPLOADED)
         {
            YAHOO.Bubbling.fire("onDoclistRefresh", {currentPath: this.config.path});
         }
         YAHOO.widget.DataTable.MSG_EMPTY = this.previousFileListEmptyMessage;
         this.panel.hide();
         this.clear();
         if(message)
         {
            Alfresco.util.PopupManager.displayPrompt({text: message});
         }
      },


      handleStartStopClick: function()
      {
         if(this.state === this.BROWSING)
         {
            var length = this.dataTable.getRecordSet().getLength();
            if(length > 0)
            {
               this.state = this.UPLOADING;
               this.startStopButton.set("label", "Uploading...");
               this.startStopButton.set("disabled", true);
               this.browseButton.set("disabled", true);
            }
            this.uploadFromQueue(2);
         }
         else if(this.state === this.UPLOADING)
         {
            this.cancelAllUploads();
         }
      },

      uploadFromQueue: function(noOfUploadsToStart){
         var startedUploads = 0;
         var length = this.dataTable.getRecordSet().getLength();
         for(var i = 0; i < length && startedUploads < noOfUploadsToStart; i++)
         {
            var record = this.dataTable.getRecordSet().getRecord(i);
            var flashId = record.getData("id");
            var fileInfo = this.fileStore[flashId];
            if(fileInfo.state === this.BROWSING)
            {
               fileInfo.state = this.UPLOADING;               
               var contentType = fileInfo.contentType.options[fileInfo.contentType.selectedIndex].value;
               var url = Alfresco.constants.PROXY_URI + "api/upload?alf_ticket=" + Alfresco.constants.ALF_TICKET;
               this.uploader.upload(flashId, url, "POST",
               {
                  path: this.config.path,
                  siteId: this.config.siteId,
                  componentId: this.config.componentId,
                  contentType: contentType
               }, "filedata");
               startedUploads++;
            }
         }
      },

      handleFileButtonClick: function(flashId, recordId)
      {
         var r = this.dataTable.getRecordSet().getRecord(recordId);
         this.addedFiles[this.getUniqeFileToken(r.getData())] = null;
         this.fileStore[flashId] = null;
         this.dataTable.deleteRow(r);
         if(this.state === this.BROWSING)
         {
            this.uploader.removeFile(flashId);
            if(this.dataTable.getRecordSet().getLength() == 0)
            {
               this.startStopButton.set("disabled", true);
               this.browseButton.set("disabled", false);
            }
         }
         else if(this.state === this.UPLOADING)
         {
            this.uploader.cancel(flashId);
            this.adjustIfUploaded();
         }
      },

      cancelAllUploads: function()
      {
         var length = this.dataTable.getRecordSet().getLength();
         for(var i = 0; i < length; i++){
            var record = this.dataTable.getRecordSet().getRecord(i);
            var flashId = record.getData("id");
            this.uploader.cancel(flashId);
         }
      },

      clear: function()
      {
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
