/*
 *** Alfresco.FileUpload
*/

(function()
{
   Alfresco.FileUpload = function(htmlId)
   {
      this.name = "Alfresco.FileUpload";
      this.id = htmlId;

      this.swf = Alfresco.constants.URL_CONTEXT + "yui/uploader/assets/uploader.swf";

      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["datatable", "datasource", "uploader"], this.componentsLoaded, this);

      return this;
   }

   Alfresco.FileUpload.prototype =
   {

      htmlPanel: null,

      flashPanel: null,

      multiSelect: false,
      filter: [],
      noOfVisibleRows: 1,
      versionInput: true,

      uploader: null,
      hasRequestedVersion: false,
      dataTable: null,
      titleText: null,
      versionSection: null,
      multiSelectText:null,
      fileItemTemplate: null,      
      addedFiles: {},

      modalPanelConfig: {
         width: "600px",
         fixedcenter: true,
         close:false,
         draggable:false,
         zindex:4,
         modal:true,
         visible:true
      },

      componentsLoaded: function()
      {
         YAHOO.widget.Uploader.SWFURL = this.swf;
         YAHOO.util.Event.onContentReady(this.id, this.init, this, true);
      },

      init: function()
      {

         var Dom = YAHOO.util.Dom;

         var htmlDiv = Dom.get(this.id + "-htmldialog-panel");
         this.htmlPanel = new YAHOO.widget.Panel(htmlDiv, this.modalPanelConfig);

         this.fileItemTemplate = Dom.get(this.id + "-fileItemTemplate-div");

         this.createEmptyDataTable();

         this.titleText = Dom.get(this.id + "-title-span");

         var flashDiv = Dom.get(this.id + "-flashdialog-panel");
         this.flashPanel = new YAHOO.widget.Panel(flashDiv, this.modalPanelConfig);


         var clButton = Dom.get(this.id + "-clear-button");
         var clearButton = new YAHOO.widget.Button(clButton, {type: "button"});
         clearButton.subscribe("click", this.clear, this, true);

         var bButton = Dom.get(this.id + "-browse-button");
         var browseButton = new YAHOO.widget.Button(bButton, {type: "button"});
         browseButton.subscribe("click", this.browse, this, true);

         this.versionSection = Dom.get(this.id + "-versionSection-div");
         this.multiSelectText = Dom.get(this.id + "-multiSelect-span");

         var vGroup = Dom.get(this.id + "-version-buttongroup");
         var oButtonGroup1 = new YAHOO.widget.ButtonGroup(vGroup); 

         var uButton = Dom.get(this.id + "-upload-button");
         var uploadButton = new YAHOO.widget.Button(uButton, {type: "button"});
         uploadButton.subscribe("click", this.upload, this, true);

         var caButton = Dom.get(this.id + "-cancel-button");
         var cancelButton = new YAHOO.widget.Button(caButton, {type: "button"});
         cancelButton.subscribe("click", this.hide, this, true);

         this.hasRequestedVersion = DetectFlashVer(9, 0, 45); // majorVersion, minorVersions, revisionVersion
         if(this.hasRequestedVersion)
         {
            this.uploader = new YAHOO.widget.Uploader(this.id + "-flashuploader-div");
            this.uploader.subscribe("fileSelect", this.onFileSelect, this, true);
            this.uploader.subscribe("uploadComplete",this.onUploadComplete, this, true);
            this.uploader.subscribe("uploadProgress",this.onUploadProgress, this, true);
            this.uploader.subscribe("uploadStart",this.onUploadStart, this, true);
            this.uploader.subscribe("uploadCancel",this.onUploadCancel, this, true);
            this.uploader.subscribe("uploadCompleteData",this.onUploadCompleteData, this, true);
            this.uploader.subscribe("uploadError",this.onUploadError, this, true);
         }
      },

      /**
       *
       * @param multiSelect Boolean true if the user shall be allowed to select multiple files
       * @param filter Array Describes what files that shall be selectable i.e. [{description:"Images", extensions:"*.jpg"}]
       * @param versionInput Boolean true if the input fields shall be displayed (only available if multiSelect is true)
       */
      show: function(title, filter, multiSelect, noOfVisibleRows, versionInput)
      {
         var Dom = YAHOO.util.Dom;
         
         this.multiSelect = multiSelect;
         this.noOfVisibleRows = noOfVisibleRows;
         this.filter = filter;
         this.versionInput = versionInput;

         this.titleText["innerHTML"] = title;
         if(this.versionInput)
         {
            if(this.multiSelect)
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
         Dom.setStyle(this.multiSelectText, "display", (this.multiSelect ? "true" : "none"));
         var p = this.hasRequestedVersion ? this.flashPanel : this.htmlPanel;

         p.render(document.body);
         p.show();
      },

      hide: function()
      {
         if(this.hasRequestedVersion)
         {
            this.flashPanel.hide();
            this.clear();
         }
         else
         {
            this.htmlPanel.hide();
         }
      },

      browse: function()
      {
         this.uploader.browse(this.multiSelect, this.filter);
      },

      createEmptyDataTable: function()
      {

         var Dom = YAHOO.util.Dom;

         var myColumnDefs = [
            {key:"id", label: "File", width:400, resizable: true, formatter: this.formatFileInfoCell}
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

      formatFileInfoCell:function(el, oRecord, oColumn, oData) {
         // The scope for 'this' in this function is NOT Alfresco.FileUpload
         var thisComponent = Alfresco.util.ComponentManager.find({name:"Alfresco.FileUpload"})[0];
         thisComponent.formatCell(el, oRecord);
      },

      formatCell:function(el, oRecord) {
         var Dom = YAHOO.util.Dom;

         var flashId = oRecord.getData()["id"];
         var cell = new YAHOO.util.Element(el);
         var fileInfoStr = oRecord.getData()["name"] + "(" + oRecord.getData()["size"] + "b)";

         var templateInstance = this.fileItemTemplate.cloneNode(true);
         templateInstance.setAttribute("id", this.id + "-fileItemTemplate-div-" + flashId);
         Dom.getElementsByClassName("progressInfo", "span", templateInstance)[0]["innerHTML"] = fileInfoStr;
         Dom.getElementsByClassName("progressPercentage", "span", templateInstance)[0]["innerHTML"] = "0%";

         // Set id so it can be referenced later (the class name can't be used since it will change if a failure occurs)
         var progress = Dom.getElementsByClassName("progressSuccess", "span", templateInstance)[0];
         progress.setAttribute("id", "progress-" + flashId);

         var rButton = Dom.getElementsByClassName("fileupload-remove-button", "input", templateInstance)[0];
         var removeButton = new YAHOO.widget.Button(rButton, {type: "button"});
         removeButton.subscribe("click", function(){ this.removeFile(flashId, oRecord.getId()); }, this, true);

         cell.appendChild (templateInstance);
      },

      removeFile: function(flashId, recordId) {
         this.uploader.removeFile(flashId);
         var r = this.dataTable.getRecordSet().getRecord(recordId);
         this.dataTable.deleteRow(r);
      }

   };

})();

