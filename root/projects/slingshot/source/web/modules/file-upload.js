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
         /* Shortcut for dummy instance */
         YAHOO.widget.Uploader.SWFURL = this.swf;
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


      show: function(userConfig)
      {
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
               successCallback: this.templateLoaded,
               failureMessage: "Could not load file upload template",
               scope: this
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
            width: "530px"
         });
         this.panel.render(document.body);

         this.fileItemTemplate = Dom.get(this.id + "-fileItemTemplate-div");

         this.createEmptyDataTable();

         this.titleText = Dom.get(this.id + "-title-span");

         var clearButton = new YAHOO.widget.Button(this.id + "-clear-button", {type: "button"});
         clearButton.subscribe("click", this.clear, this, true);

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

            var progress = Dom.getElementsByClassName("progressSuccess", "span", templateInstance)[0];

            var progressInfo = Dom.getElementsByClassName("progressInfo", "span", templateInstance)[0];
            progressInfo["innerHTML"] = fileInfoStr;

            var contentType = Dom.getElementsByClassName("fileupload-contentType-menu", "select", templateInstance)[0];

            var progressPercentage = Dom.getElementsByClassName("progressPercentage", "span", templateInstance)[0];
            progressPercentage["innerHTML"] = "";

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
            {key:"id", label: "File", width: 500, resizable: false, formatter: this.formatCell} //this.formatFileInfoCell}
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
         //hide instead this.startStopButton.set("disabled", true);
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

      addFilesToDataTable: function(entries)
      {
         for(var i in entries) {
            var data = YAHOO.widget.DataTable._cloneObject(entries[i]);
            if(!this.addedFiles[this.getUniqeFileToken(data)]){
               this.dataTable.addRow(data, 0);
            }
         }
      },

      onFileSelect: function(event)
      {
         this.addFilesToDataTable(event.fileList);
      },

      onUploadStart: function(event)
      {
         var fileInfo = this.fileStore[event["id"]];
         fileInfo.fileButton.set("label", "Cancel");
         fileInfo.state = this.UPLOADING;
      },

      onUploadComplete: function(event)
      {
         var fileInfo = this.fileStore[event["id"]];
         fileInfo.fileButton.set("disabled", true);
         fileInfo.state = this.UPLOADED;

         // Change gui if all files are uploaded
         for(var i in this.fileStore)
         {
            if(this.fileStore[i] && this.fileStore[i].state !== this.UPLOADED)
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
         var left = (-320 + (uploaded * 320));
         YAHOO.util.Dom.setStyle(fileInfo.progress, "left", left + "px");
      },

      onUploadCancel: function(event)
      {
         alert("cancel");
      },

      onUploadCompleteData: function(event)
      {
         //alert("completeData");
      },

      onUploadError: function(event)
      {
         var fileInfo = this.fileStore[event["id"]];
         fileInfo.progress.setAttribute("class", "progressFailure");
         //YAHOO.util.Dom.setStyle(fileInfo.progress, "left", 0 + "px");
      },

      browse: function()
      {
         this.state = this.BROWSING;
         this.cancelOkButton.set("label", "Cancel");
         this.startStopButton.set("label", "Start uploading");
         this.uploader.browse(this.config.multiSelect, this.config.filter);
      },
      
      handleCancelOkClick: function()
      {
         if(this.state === this.UPLOADING)
         {
            this.cancelAllUploads();
         }
         else if(this.state === this.UPLOADED)
         {
            YAHOO.Bubbling.fire("onDoclistRefresh", {currentPath: this.config.path});
         }
         this.panel.hide();
         this.clear();
         this.state = this.UPLOADED;
         this.browseButton.set("disabled", false);
         this.startStopButton.set("disabled", false);

      },


      handleStartStopClick: function()
      {
         if(this.state === this.BROWSING)
         {
            var length = this.dataTable.getRecordSet().getLength();
            for(var i = 0; i < length; i++)
            {
               var record = this.dataTable.getRecordSet().getRecord(i);
               var flashId = record.getData("id");
               var fileInfo = this.fileStore[flashId];
               var contentType = fileInfo.contentType.options[fileInfo.contentType.selectedIndex].value;
               var url = Alfresco.constants.PROXY_URI + "api/upload?alf_ticket=" + Alfresco.constants.ALF_TICKET;
               this.uploader.upload(flashId, url, "POST",
               {
                  path: this.config.path,
                  siteId: this.config.siteId,
                  componentId: this.config.componentId,
                  contentType: contentType
               }, "filedata");
            }
            if(length > 0)
            {
               this.state = this.UPLOADING;
               this.startStopButton.set("label", "Stop upload");
               this.browseButton.set("disabled", true);
               // hide contentType
            }
         }
         else if(this.state === this.UPLOADING)
         {
            this.cancelAllUploads();
         }
      },

      handleFileButtonClick: function(flashId, recordId)
      {
         if(this.state === this.BROWSING)
         {
            this.uploader.removeFile(flashId);
            var r = this.dataTable.getRecordSet().getRecord(recordId);
            this.addedFiles[this.getUniqeFileToken(r.getData())] = null;
            this.fileStore[flashId] = null;
            this.dataTable.deleteRow(r);
         }
         else if(this.state === this.UPLOADING)
         {
            this.uploader.cancel(flashId);
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