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

         // Patch for width and/or minWidth Column values bug in non-scrolling DataTables
         (function(){var B=YAHOO.widget.DataTable,A=YAHOO.util.Dom;B.prototype._setColumnWidth=function(I,D,J){I=this.getColumn(I);if(I){J=J||"hidden";if(!B._bStylesheetFallback){var N;if(!B._elStylesheet){N=document.createElement("style");N.type="text/css";B._elStylesheet=document.getElementsByTagName("head").item(0).appendChild(N)}if(B._elStylesheet){N=B._elStylesheet;var M=".yui-dt-col-"+I.getId();var K=B._oStylesheetRules[M];if(!K){if(N.styleSheet&&N.styleSheet.addRule){N.styleSheet.addRule(M,"overflow:"+J);N.styleSheet.addRule(M,"width:"+D);K=N.styleSheet.rules[N.styleSheet.rules.length-1]}else{if(N.sheet&&N.sheet.insertRule){N.sheet.insertRule(M+" {overflow:"+J+";width:"+D+";}",N.sheet.cssRules.length);K=N.sheet.cssRules[N.sheet.cssRules.length-1]}else{B._bStylesheetFallback=true}}B._oStylesheetRules[M]=K}else{K.style.overflow=J;K.style.width=D}return }B._bStylesheetFallback=true}if(B._bStylesheetFallback){if(D=="auto"){D=""}var C=this._elTbody?this._elTbody.rows.length:0;if(!this._aFallbackColResizer[C]){var H,G,F;var L=["var colIdx=oColumn.getKeyIndex();","oColumn.getThEl().firstChild.style.width="];for(H=C-1,G=2;H>=0;--H){L[G++]="this._elTbody.rows[";L[G++]=H;L[G++]="].cells[colIdx].firstChild.style.width=";L[G++]="this._elTbody.rows[";L[G++]=H;L[G++]="].cells[colIdx].style.width="}L[G]="sWidth;";L[G+1]="oColumn.getThEl().firstChild.style.overflow=";for(H=C-1,F=G+2;H>=0;--H){L[F++]="this._elTbody.rows[";L[F++]=H;L[F++]="].cells[colIdx].firstChild.style.overflow=";L[F++]="this._elTbody.rows[";L[F++]=H;L[F++]="].cells[colIdx].style.overflow="}L[F]="sOverflow;";this._aFallbackColResizer[C]=new Function("oColumn","sWidth","sOverflow",L.join(""))}var E=this._aFallbackColResizer[C];if(E){E.call(this,I,D,J);return }}}else{}};B.prototype._syncColWidths=function(){var J=this.get("scrollable");if(this._elTbody.rows.length>0){var M=this._oColumnSet.keys,C=this.getFirstTrEl();if(M&&C&&(C.cells.length===M.length)){var O=false;if(J&&(YAHOO.env.ua.gecko||YAHOO.env.ua.opera)){O=true;if(this.get("width")){this._elTheadContainer.style.width="";this._elTbodyContainer.style.width=""}else{this._elContainer.style.width=""}}var I,L,F=C.cells.length;for(I=0;I<F;I++){L=M[I];if(!L.width){this._setColumnWidth(L,"auto","visible")}}for(I=0;I<F;I++){L=M[I];var H=0;var E="hidden";if(!L.width){var G=L.getThEl();var K=C.cells[I];if(J){var N=(G.offsetWidth>K.offsetWidth)?G.firstChild:K.firstChild;if(G.offsetWidth!==K.offsetWidth||N.offsetWidth<L.minWidth){H=Math.max(0,L.minWidth,N.offsetWidth-(parseInt(A.getStyle(N,"paddingLeft"),10)|0)-(parseInt(A.getStyle(N,"paddingRight"),10)|0))}}else{if(K.offsetWidth<L.minWidth){E=K.offsetWidth?"visible":"hidden";H=Math.max(0,L.minWidth,K.offsetWidth-(parseInt(A.getStyle(K,"paddingLeft"),10)|0)-(parseInt(A.getStyle(K,"paddingRight"),10)|0))}}}else{H=L.width}if(L.hidden){L._nLastWidth=H;this._setColumnWidth(L,"1px","hidden")}else{if(H){this._setColumnWidth(L,H+"px",E)}}}if(O){var D=this.get("width");this._elTheadContainer.style.width=D;this._elTbodyContainer.style.width=D}}}this._syncScrollPadding()}})();
         // Patch for initial hidden Columns bug
         (function(){var A=YAHOO.util,B=YAHOO.env.ua,E=A.Event,C=A.Dom,D=YAHOO.widget.DataTable;D.prototype._initTheadEls=function(){var X,V,T,Z,I,M;if(!this._elThead){Z=this._elThead=document.createElement("thead");I=this._elA11yThead=document.createElement("thead");M=[Z,I];E.addListener(Z,"focus",this._onTheadFocus,this);E.addListener(Z,"keydown",this._onTheadKeydown,this);E.addListener(Z,"mouseover",this._onTableMouseover,this);E.addListener(Z,"mouseout",this._onTableMouseout,this);E.addListener(Z,"mousedown",this._onTableMousedown,this);E.addListener(Z,"mouseup",this._onTableMouseup,this);E.addListener(Z,"click",this._onTheadClick,this);E.addListener(Z.parentNode,"dblclick",this._onTableDblclick,this);this._elTheadContainer.firstChild.appendChild(I);this._elTbodyContainer.firstChild.appendChild(Z)}else{Z=this._elThead;I=this._elA11yThead;M=[Z,I];for(X=0;X<M.length;X++){for(V=M[X].rows.length-1;V>-1;V--){E.purgeElement(M[X].rows[V],true);M[X].removeChild(M[X].rows[V])}}}var N,d=this._oColumnSet;var H=d.tree;var L,P;for(T=0;T<M.length;T++){for(X=0;X<H.length;X++){var U=M[T].appendChild(document.createElement("tr"));P=(T===1)?this._sId+"-hdrow"+X+"-a11y":this._sId+"-hdrow"+X;U.id=P;for(V=0;V<H[X].length;V++){N=H[X][V];L=U.appendChild(document.createElement("th"));if(T===0){N._elTh=L}P=(T===1)?this._sId+"-th"+N.getId()+"-a11y":this._sId+"-th"+N.getId();L.id=P;L.yuiCellIndex=V;this._initThEl(L,N,X,V,(T===1))}if(T===0){if(X===0){C.addClass(U,D.CLASS_FIRST)}if(X===(H.length-1)){C.addClass(U,D.CLASS_LAST)}}}if(T===0){var R=d.headers[0];var J=d.headers[d.headers.length-1];for(X=0;X<R.length;X++){C.addClass(C.get(this._sId+"-th"+R[X]),D.CLASS_FIRST)}for(X=0;X<J.length;X++){C.addClass(C.get(this._sId+"-th"+J[X]),D.CLASS_LAST)}var Q=(A.DD)?true:false;var c=false;if(this._oConfigs.draggableColumns){for(X=0;X<this._oColumnSet.tree[0].length;X++){N=this._oColumnSet.tree[0][X];if(Q){L=N.getThEl();C.addClass(L,D.CLASS_DRAGGABLE);var O=D._initColumnDragTargetEl();N._dd=new YAHOO.widget.ColumnDD(this,N,L,O)}else{c=true}}}for(X=0;X<this._oColumnSet.keys.length;X++){N=this._oColumnSet.keys[X];if(N.resizeable){if(Q){L=N.getThEl();C.addClass(L,D.CLASS_RESIZEABLE);var G=L.firstChild;var F=G.appendChild(document.createElement("div"));F.id=this._sId+"-colresizer"+N.getId();N._elResizer=F;C.addClass(F,D.CLASS_RESIZER);var e=D._initColumnResizerProxyEl();N._ddResizer=new YAHOO.util.ColumnResizer(this,N,L,F.id,e);var W=function(f){E.stopPropagation(f)};E.addListener(F,"click",W)}else{c=true}}}if(c){}}else{}}for(var a=0,Y=this._oColumnSet.keys.length;a<Y;a++){if(this._oColumnSet.keys[a].hidden){var b=this._oColumnSet.keys[a];var S=b.getThEl();b._nLastWidth=S.offsetWidth-(parseInt(C.getStyle(S,"paddingLeft"),10)|0)-(parseInt(C.getStyle(S,"paddingRight"),10)|0);this._setColumnWidth(b.getKeyIndex(),"1px")}}if(B.webkit&&B.webkit<420){var K=this;setTimeout(function(){K._elThead.style.display=""},0);this._elThead.style.display="none"}}})();

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
            {key:"id", label: "Files", width:600, resizable: true, formatter: this.formatCell} //this.formatFileInfoCell}
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
