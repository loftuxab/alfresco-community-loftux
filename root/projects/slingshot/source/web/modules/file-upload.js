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

      defaultConfig: {
         title: "",
         multiSelect: false,
         filter: [],
         noOfVisibleRows: 1,
         versionInput: true
      },

      config: {},

      uploader: null,
      hasRequestedVersion: false,
      dataTable: null,
      titleText: null,
      versionSection: null,
      multiSelectText:null,
      fileItemTemplate: null,      
      addedFiles: {},

      show: function(userConfig)
      {
         this.config = YAHOO.lang.merge(this.defaultConfig, userConfig);
         if(this.panel)
         {
            this.applyConfig();
            this.panel.show();
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
            fixedcenter: true,
            visible: false,
            width: "450px"
         });
         this.panel.render(document.body);

         this.fileItemTemplate = Dom.get(this.id + "-fileItemTemplate-div");

         this.createEmptyDataTable();

         this.titleText = Dom.get(this.id + "-title-span");

         var clearButton = new YAHOO.widget.Button(this.id + "-clear-button", {type: "button"});
         clearButton.subscribe("click", this.clear, this, true);

         var browseButton = new YAHOO.widget.Button(this.id + "-browse-button", {type: "button"});
         browseButton.subscribe("click", this.browse, this, true);

         this.versionSection = Dom.get(this.id + "-versionSection-div");
         this.multiSelectText = Dom.get(this.id + "-multiSelect-span");

         var vGroup = Dom.get(this.id + "-version-buttongroup");
         var oButtonGroup1 = new YAHOO.widget.ButtonGroup(vGroup);

         var uploadButton = new YAHOO.widget.Button(this.id + "-upload-button", {type: "button"});
         uploadButton.subscribe("click", this.upload, this, true);

         var cancelButton = new YAHOO.widget.Button(this.id + "-cancel-button", {type: "button"});
         cancelButton.subscribe("click", this.hide, this, true);

         this.uploader = new YAHOO.widget.Uploader(this.id + "-flashuploader-div");
         this.uploader.subscribe("fileSelect", this.onFileSelect, this, true);
         this.uploader.subscribe("uploadComplete",this.onUploadComplete, this, true);
         this.uploader.subscribe("uploadProgress",this.onUploadProgress, this, true);
         this.uploader.subscribe("uploadStart",this.onUploadStart, this, true);
         this.uploader.subscribe("uploadCancel",this.onUploadCancel, this, true);
         this.uploader.subscribe("uploadCompleteData",this.onUploadCompleteData, this, true);
         this.uploader.subscribe("uploadError",this.onUploadError, this, true);

         this.applyConfig();
         this.panel.show();
      },

      hide: function()
      {
         this.panel.hide();
         this.clear();
      },

      browse: function()
      {
         this.uploader.browse(this.config.multiSelect, this.config.filter);
      },

      createEmptyDataTable: function()
      {

         var Dom = YAHOO.util.Dom;

         var myThis = this;

         this.formatCell = function(el, oRecord) {
            var flashId = oRecord.getData()["id"];
            var cell = new YAHOO.util.Element(el);
            var fileInfoStr = oRecord.getData()["name"] + "(" + oRecord.getData()["size"] + "b)";

            var templateInstance = myThis.fileItemTemplate.cloneNode(true);
            templateInstance.setAttribute("id", myThis.id + "-fileItemTemplate-div-" + flashId);
            Dom.getElementsByClassName("progressInfo", "span", templateInstance)[0]["innerHTML"] = fileInfoStr;
            Dom.getElementsByClassName("progressPercentage", "span", templateInstance)[0]["innerHTML"] = "0%";

            // Set id so it can be referenced later (the class name can't be used since it will change if a failure occurs)
            var progress = Dom.getElementsByClassName("progressSuccess", "span", templateInstance)[0];
            progress.setAttribute("id", "progress-" + flashId);

            var rButton = Dom.getElementsByClassName("fileupload-remove-button", "input", templateInstance)[0];
            var removeButton = new YAHOO.widget.Button(rButton, {type: "button"});
            removeButton.subscribe("click", function(){ myThis.removeFile(flashId, oRecord.getId()); }, myThis, true);

            cell.appendChild (templateInstance);
         };

         var myColumnDefs = [
            {key:"id", label: "File", width:400, resizable: true, formatter: this.formatCell} //this.formatFileInfoCell}
         ];

         var myDataSource = new YAHOO.util.DataSource([]);
         myDataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
         myDataSource.responseSchema = {
            fields: ["id","name","created","modified","type", "size", "progress"]
         };

         var dataTableDiv = Dom.get(this.id + "-filelist-table");
         this.dataTable = new YAHOO.widget.DataTable
               (dataTableDiv, myColumnDefs, myDataSource, {
                  scrollable: true,
                  height: "200px"
               }
         );
         this.dataTable.subscribe("rowAddEvent", this.rememberAddedFiles, this, true);
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

      upload: function() {
         var length = this.dataTable.getRecordSet().getLength();
         for(var i = 0; i < length; i++){
            var record = this.dataTable.getRecordSet().getRecord(i);
            var idToUpload = record.getData("id");
            this.uploader.upload(idToUpload, "http://localhost:8080/alfresco/uploadFileServlet?ticket=heppTicket123");
         }
      },

      onUploadStart: function(event) {
      },

      onUploadComplete: function(event) {
      },

      onUploadProgress: function(event) {
        this.updateUploadStatus(event["id"], event["bytesLoaded"], event["bytesTotal"]);
      },

      updateUploadStatus: function(flashId, bytesLoaded, bytesTotal) {
         var Dom = YAHOO.util.Dom;

         // Set percentage
         var templateInstance = Dom.get(this.id + "-fileItemTemplate-div-" + flashId);
         var uploaded = bytesLoaded / bytesTotal;
         Dom.getElementsByClassName("progressPercentage", "span", templateInstance)[0]["innerHTML"] = (uploaded * 100) + "%";

         // Set progress position
         var progress = Dom.get("progress-" + flashId);
         var left = (-300 + (uploaded * 300));
         Dom.setStyle(progress, "left", left + "px");
      },

      onUploadCancel: function(event) {
         alert("cancel");
      },

      onUploadCompleteData: function(event) {
         alert("completeData");
      },

      onUploadError: function(event) {
         var Dom = YAHOO.util.Dom;         
         var flashId = event["id"];
         var progress = Dom.get("progress-" + flashId);
         progress.setAttribute("class", "progressFailure");
      },

      clear: function() {
         var length = this.dataTable.getRecordSet().getLength();
         this.uploader.clearFileList();
         this.addedFiles = {};
         this.dataTable.deleteRows(0, length);
      },

      removeFile: function(flashId, recordId) {
         this.uploader.removeFile(flashId);
         var r = this.dataTable.getRecordSet().getRecord(recordId);
         this.dataTable.deleteRow(r);
      }

   };

})();

/* Dummy instance to load optional YUI components early */
new Alfresco.module.FileUpload(null);