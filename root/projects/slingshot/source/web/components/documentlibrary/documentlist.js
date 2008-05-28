/*

YAHOO.util.Dom.get("template.documentlist.documentlibrary-body").clientWidth

*/


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
 * DocumentList component.
 * 
 * @namespace Alfresco
 * @class Alfresco.DocumentList
 */
(function()
{
   /**
    * DocumentList constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.DocumentList} The new DocumentList instance
    * @constructor
    */
   Alfresco.DocumentList = function(htmlId)
   {
      this.name = "Alfresco.DocumentList";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "menu", "container", "datasource", "datatable", "history"], this.onComponentsLoaded, this);
      
      return this;
   }
   
   Alfresco.DocumentList.prototype =
   {
      /**
       * Object container for initialization options
       */
      options:
      {
         /**
          * Flag indicating whether folders are visible or not.
          * 
          * @property showFolders
          * @type boolean
          */
         showFolders: true,

         /**
          * Flag indicating whether the list shows a detailed view or a simple one.
          * 
          * @property showDetail
          * @type boolean
          */
         showDetail: true,

         /**
          * Current siteId.
          * 
          * @property siteId
          * @type string
          */
         siteId: "",

         /**
          * Initial path to show on load.
          * 
          * @property initialPath
          * @type string
          */
         initialPath: ""
      },
      
      /**
       * Current path being browsed.
       * 
       * @property currentPath
       * @type string
       */
      currentPath: "",

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
       */
      setOptions: function DL_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
      },
      
      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function DL_onComponentsLoaded()
      {
         YAHOO.util.Event.onContentReady(this.id, this.onReady, this, true);
         
         // Patch for width and/or minWidth Column values bug in non-scrolling DataTables
         (function(){var B=YAHOO.widget.DataTable,A=YAHOO.util.Dom;B.prototype._setColumnWidth=function(I,D,J){I=this.getColumn(I);if(I){J=J||"hidden";if(!B._bStylesheetFallback){var N;if(!B._elStylesheet){N=document.createElement("style");N.type="text/css";B._elStylesheet=document.getElementsByTagName("head").item(0).appendChild(N)}if(B._elStylesheet){N=B._elStylesheet;var M=".yui-dt-col-"+I.getId();var K=B._oStylesheetRules[M];if(!K){if(N.styleSheet&&N.styleSheet.addRule){N.styleSheet.addRule(M,"overflow:"+J);N.styleSheet.addRule(M,"width:"+D);K=N.styleSheet.rules[N.styleSheet.rules.length-1]}else{if(N.sheet&&N.sheet.insertRule){N.sheet.insertRule(M+" {overflow:"+J+";width:"+D+";}",N.sheet.cssRules.length);K=N.sheet.cssRules[N.sheet.cssRules.length-1]}else{B._bStylesheetFallback=true}}B._oStylesheetRules[M]=K}else{K.style.overflow=J;K.style.width=D}return }B._bStylesheetFallback=true}if(B._bStylesheetFallback){if(D=="auto"){D=""}var C=this._elTbody?this._elTbody.rows.length:0;if(!this._aFallbackColResizer[C]){var H,G,F;var L=["var colIdx=oColumn.getKeyIndex();","oColumn.getThEl().firstChild.style.width="];for(H=C-1,G=2;H>=0;--H){L[G++]="this._elTbody.rows[";L[G++]=H;L[G++]="].cells[colIdx].firstChild.style.width=";L[G++]="this._elTbody.rows[";L[G++]=H;L[G++]="].cells[colIdx].style.width="}L[G]="sWidth;";L[G+1]="oColumn.getThEl().firstChild.style.overflow=";for(H=C-1,F=G+2;H>=0;--H){L[F++]="this._elTbody.rows[";L[F++]=H;L[F++]="].cells[colIdx].firstChild.style.overflow=";L[F++]="this._elTbody.rows[";L[F++]=H;L[F++]="].cells[colIdx].style.overflow="}L[F]="sOverflow;";this._aFallbackColResizer[C]=new Function("oColumn","sWidth","sOverflow",L.join(""))}var E=this._aFallbackColResizer[C];if(E){E.call(this,I,D,J);return }}}else{}};B.prototype._syncColWidths=function(){var J=this.get("scrollable");if(this._elTbody.rows.length>0){var M=this._oColumnSet.keys,C=this.getFirstTrEl();if(M&&C&&(C.cells.length===M.length)){var O=false;if(J&&(YAHOO.env.ua.gecko||YAHOO.env.ua.opera)){O=true;if(this.get("width")){this._elTheadContainer.style.width="";this._elTbodyContainer.style.width=""}else{this._elContainer.style.width=""}}var I,L,F=C.cells.length;for(I=0;I<F;I++){L=M[I];if(!L.width){this._setColumnWidth(L,"auto","visible")}}for(I=0;I<F;I++){L=M[I];var H=0;var E="hidden";if(!L.width){var G=L.getThEl();var K=C.cells[I];if(J){var N=(G.offsetWidth>K.offsetWidth)?G.firstChild:K.firstChild;if(G.offsetWidth!==K.offsetWidth||N.offsetWidth<L.minWidth){H=Math.max(0,L.minWidth,N.offsetWidth-(parseInt(A.getStyle(N,"paddingLeft"),10)|0)-(parseInt(A.getStyle(N,"paddingRight"),10)|0))}}else{if(K.offsetWidth<L.minWidth){E=K.offsetWidth?"visible":"hidden";H=Math.max(0,L.minWidth,K.offsetWidth-(parseInt(A.getStyle(K,"paddingLeft"),10)|0)-(parseInt(A.getStyle(K,"paddingRight"),10)|0))}}}else{H=L.width}if(L.hidden){L._nLastWidth=H;this._setColumnWidth(L,"1px","hidden")}else{if(H){this._setColumnWidth(L,H+"px",E)}}}if(O){var D=this.get("width");this._elTheadContainer.style.width=D;this._elTbodyContainer.style.width=D}}}this._syncScrollPadding()}})();
         // Patch for initial hidden Columns bug
         (function(){var A=YAHOO.util,B=YAHOO.env.ua,E=A.Event,C=A.Dom,D=YAHOO.widget.DataTable;D.prototype._initTheadEls=function(){var X,V,T,Z,I,M;if(!this._elThead){Z=this._elThead=document.createElement("thead");I=this._elA11yThead=document.createElement("thead");M=[Z,I];E.addListener(Z,"focus",this._onTheadFocus,this);E.addListener(Z,"keydown",this._onTheadKeydown,this);E.addListener(Z,"mouseover",this._onTableMouseover,this);E.addListener(Z,"mouseout",this._onTableMouseout,this);E.addListener(Z,"mousedown",this._onTableMousedown,this);E.addListener(Z,"mouseup",this._onTableMouseup,this);E.addListener(Z,"click",this._onTheadClick,this);E.addListener(Z.parentNode,"dblclick",this._onTableDblclick,this);this._elTheadContainer.firstChild.appendChild(I);this._elTbodyContainer.firstChild.appendChild(Z)}else{Z=this._elThead;I=this._elA11yThead;M=[Z,I];for(X=0;X<M.length;X++){for(V=M[X].rows.length-1;V>-1;V--){E.purgeElement(M[X].rows[V],true);M[X].removeChild(M[X].rows[V])}}}var N,d=this._oColumnSet;var H=d.tree;var L,P;for(T=0;T<M.length;T++){for(X=0;X<H.length;X++){var U=M[T].appendChild(document.createElement("tr"));P=(T===1)?this._sId+"-hdrow"+X+"-a11y":this._sId+"-hdrow"+X;U.id=P;for(V=0;V<H[X].length;V++){N=H[X][V];L=U.appendChild(document.createElement("th"));if(T===0){N._elTh=L}P=(T===1)?this._sId+"-th"+N.getId()+"-a11y":this._sId+"-th"+N.getId();L.id=P;L.yuiCellIndex=V;this._initThEl(L,N,X,V,(T===1))}if(T===0){if(X===0){C.addClass(U,D.CLASS_FIRST)}if(X===(H.length-1)){C.addClass(U,D.CLASS_LAST)}}}if(T===0){var R=d.headers[0];var J=d.headers[d.headers.length-1];for(X=0;X<R.length;X++){C.addClass(C.get(this._sId+"-th"+R[X]),D.CLASS_FIRST)}for(X=0;X<J.length;X++){C.addClass(C.get(this._sId+"-th"+J[X]),D.CLASS_LAST)}var Q=(A.DD)?true:false;var c=false;if(this._oConfigs.draggableColumns){for(X=0;X<this._oColumnSet.tree[0].length;X++){N=this._oColumnSet.tree[0][X];if(Q){L=N.getThEl();C.addClass(L,D.CLASS_DRAGGABLE);var O=D._initColumnDragTargetEl();N._dd=new YAHOO.widget.ColumnDD(this,N,L,O)}else{c=true}}}for(X=0;X<this._oColumnSet.keys.length;X++){N=this._oColumnSet.keys[X];if(N.resizeable){if(Q){L=N.getThEl();C.addClass(L,D.CLASS_RESIZEABLE);var G=L.firstChild;var F=G.appendChild(document.createElement("div"));F.id=this._sId+"-colresizer"+N.getId();N._elResizer=F;C.addClass(F,D.CLASS_RESIZER);var e=D._initColumnResizerProxyEl();N._ddResizer=new YAHOO.util.ColumnResizer(this,N,L,F.id,e);var W=function(f){E.stopPropagation(f)};E.addListener(F,"click",W)}else{c=true}}}if(c){}}else{}}for(var a=0,Y=this._oColumnSet.keys.length;a<Y;a++){if(this._oColumnSet.keys[a].hidden){var b=this._oColumnSet.keys[a];var S=b.getThEl();b._nLastWidth=S.offsetWidth-(parseInt(C.getStyle(S,"paddingLeft"),10)|0)-(parseInt(C.getStyle(S,"paddingRight"),10)|0);this._setColumnWidth(b.getKeyIndex(),"1px")}}if(B.webkit&&B.webkit<420){var K=this;setTimeout(function(){K._elThead.style.display=""},0);this._elThead.style.display="none"}}})();
      },
   
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function DL_onReady()
      {
         var Dom = YAHOO.util.Dom,
            Event = YAHOO.util.Event;

         // Reference to self used by inline functions
         var me = this;
         
         // Decoupled event listeners
         YAHOO.Bubbling.on("onDoclistPathChanged", this.onDoclistPathChanged, this);
         YAHOO.Bubbling.on("onDoclistRefresh", this.onDoclistRefresh, this);
      
         // YUI History
         var bookmarkedPath = YAHOO.util.History.getBookmarkedState("path");
         this.currentPath = bookmarkedPath || this.options.initialPath || "";
         if ((this.currentPath.length > 0) && (this.currentPath[0] != "/"))
         {
            this.currentPath = "/" + this.currentPath;
         }

         // Register History Manager path update callback
         YAHOO.util.History.register("path", "", function(newPath)
         {
            this._updateDocList.call(this, newPath);
         }, null, this);

         // Initialize the browser history management library
         try
         {
             YAHOO.util.History.initialize("yui-history-field", "yui-history-iframe");
         }
         catch(e)
         {
             /*
              * The only exception that gets thrown here is when the browser is
              * not supported (Opera, or not A-grade)
              */
            Alfresco.logger.debug("DocList_onReady: Couldn't initialize HistoryManager.", e.toString());
         }
         
         // File Select button
         this.widgets.fileSelectButton = new YAHOO.widget.Button(this.id + "-fileSelect-button",
         {
            type: "menu", 
            menu: this.id + "-fileSelect-menu"
         });
         this.widgets.fileSelectButton.getMenu().subscribe("click", this.onFileSelectButtonClick, this, true);
         
         // File Upload button
         this.widgets.fileUploadButton = new YAHOO.widget.Button(this.id + "-fileUpload-button",
         {
            type: "button"
         });
         this.widgets.fileUploadButton.on("click", this.onFileUploadButtonClick, this.widgets.fileUploadButton, this);

         // Hide/Show Folders button
         this.widgets.showFoldersButton = new YAHOO.widget.Button(this.id + "-showFolders-button",
         {
            type: "button"
         });
         this.widgets.showFoldersButton.on("click", this.onShowFoldersButtonClick, this.widgets.showFoldersButton, this);

         // Detailed/Simple List button
         this.widgets.showDetailButton = new YAHOO.widget.Button(this.id + "-showDetail-button",
         {
            type: "button"
         });
         this.widgets.showDetailButton.on("click", this.onShowDetailButtonClick, this.widgets.showDetailButton, this);

         // Folder Up Navigation button
         this.widgets.folderUpButton = new YAHOO.widget.Button(this.id + "-folderUp-button",
         {
            type: "button"
         });
         this.widgets.folderUpButton.on("click", this.onFolderUpButtonClick, this.widgets.folderUpButton, this);
         
         // DataSource definition
         var uriDoclist = Alfresco.constants.PROXY_URI + "slingshot/doclib/doclist?";
         this.widgets.dataSource = new YAHOO.util.DataSource(uriDoclist);
         this.widgets.dataSource.responseType = YAHOO.util.DataSource.TYPE_JSON;
         this.widgets.dataSource.connXhrMode = "queueRequests";
         this.widgets.dataSource.responseSchema =
         {
             resultsList: "doclist.items",
             fields: ["nodeRef", "type", "icon32", "name", "description"]
         };
         
         // Custom error messages
         YAHOO.widget.DataTable.MSG_EMPTY = "No documents or folders found in Document Library.";
         
         this.widgets.dataSource.doBeforeParseData = function(oRequest, oFullResponse)
         {
            if (oFullResponse.doclist.error)
            {
               YAHOO.widget.DataTable.MSG_ERROR = oFullResponse.doclist.error;
            }
            return oFullResponse;
         }


         /**
          * DataTable Formatters
          *
          * Each cell has a custom formatter defined as a custom function. See YUI documentation for details.
          * These MUST be inline in order to have access to the Alfresco.DocumentList class.
          */

         /**
          * Thumbnail custom datacell formatter
          *
          * @method formatThumbnail
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         formatThumbnail = function DL_formatThumbnail(elCell, oRecord, oColumn, oData)
         {
            elCell.innerHTML = "<img src=\"" + Alfresco.constants.URL_CONTEXT + oRecord.getData("icon32").substring(1) + "\" />";
         };

         /**
          * Description/detail custom datacell formatter
          *
          * @method formatDescription
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         formatDescription = function DL_formatDescription(elCell, oRecord, oColumn, oData)
         {
            var desc = "";
            if (oRecord.getData("type") == "folder")
            {
               var newPath = me.currentPath + "/" + oRecord.getData("name");

               // TODO: *** Update the onclick to be logically-bound, not via HTML
               desc = "<p><a href=\"\" onclick=\"YAHOO.Bubbling.fire('onDoclistPathChanged', {path: '" + newPath.replace(/'/g, "\'") + "'}); return false;\"><b>" + oRecord.getData("name") + "</b></a></p>"
            }
            else
            {
               desc = "<p><b>" + oRecord.getData("name") + "</b></p><p>Description: La la la</p>";
            }
            elCell.innerHTML = desc;
         };

         /**
          * Actions custom datacell formatter
          *
          * @method formatActions
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         formatActions = function DL_formatActions(elCell, oRecord, oColumn, oData)
         {
            elCell.innerHTML = "Actions here";
         };


         // DataTable column defintions
         var columnDefinitions = [
         {
            key: "icon32", label: "Preview", sortable: false, formatter: formatThumbnail, width: 128
         },
         {
            key: "name", label: "Description", sortable: false, formatter: formatDescription
         },
         {
            key: "actions", label: "Actions", sortable: false, formatter: formatActions, width: 256
         }];

         // DataTable definition
         // initialRequest made here, otherwise YUI will make an automatic one with null arguments
         this.widgets.dataTable = new YAHOO.widget.DataTable(this.id + "-documents", columnDefinitions, this.widgets.dataSource,
         {
            renderLoopSize: 5,
            initialRequest: "site=" + encodeURIComponent(this.options.siteId) + "&path=" + encodeURIComponent(this.currentPath)
         });

         // Fire disconnected event, but add "ignore" flag this time, due to initialRequest above
         YAHOO.Bubbling.fire('onDoclistPathChanged',
         {
            doclistInitialNav: true,
            path: this.currentPath
         });
         
         Dom.setStyle(this.id + "-body", "visibility", "visible");
      },
      

      /**
       * YUI WIDGET EVENT HANDLERS
       * Handlers for standard events fired from YUI widgets, e.g. "click"
       */

       /**
        * Multi-file select button click handler
        *
        * @method onFileSelectButtonClick
        * @param sType {string} Event type, e.g. "click"
        * @param aArgs {array} Arguments array, [0] = DomEvent, [1] = EventTarget
        * @param oValue {object} Object passed back from subscribe method
        */
      onFileSelectButtonClick: function DL_onFileSelectButtonClick(sType, aArgs, oValue)
      {
         var domEvent = aArgs[0]
         var eventTarget = aArgs[1];
      },

      /**
       * File Upload button click handler
       *
       * @method onFileUploadButtonClick
       * @param e {object} DomEvent
       * @param oValue {object} Object passed back from addListener method
       */
      onFileUploadButtonClick: function DL_onFileUploadButtonClick(e, oValue)
      {
         if (this.fileUpload === null)
         {
            this.fileUpload = new Alfresco.module.FileUpload(this.id + "-fileUpload");
         }
         // Use  like this for multi uploads
         var multiUploadConfig =
         {
            siteId: this.options.siteId,
            componentId: "documentLibrary",
            path: this.currentPath,
            title: "Upload file(s)",
            filter: [],
            multiSelect: true,
            noOfVisibleRows: 5,
            versionInput: false
         }
         this.fileUpload.show(multiUploadConfig);
         YAHOO.util.Event.preventDefault(e);
      },
      
      /**
       * Show/Hide folders button click handler
       *
       * @method onShowFoldersButtonClick
       * @param e {object} DomEvent
       * @param oValue {object} Object passed back from addListener method
       */
      onShowFoldersButtonClick: function DL_onShowFoldersButtonClick(e, oValue)
      {
         this.options.showFolders = !this.options.showFolders;
         oValue.set("label", (this.options.showFolders ? "Hide Folders" : "Show Folders"));

         YAHOO.Bubbling.fire('onDoclistRefresh');
         YAHOO.util.Event.preventDefault(e);
      },
      
      /**
       * Show/Hide detailed list button click handler
       *
       * @method onShowDetailButtonClick
       * @param e {object} DomEvent
       * @param oValue {object} Object passed back from addListener method
       */
      onShowDetailButtonClick: function DL_onShowDetailButtonClick(e, oValue)
      {
         this.options.showDetail = !this.options.showDetail;
         oValue.set("label", (this.options.showDetail ? "Simple List" : "Detailed List"));

         YAHOO.Bubbling.fire('onDoclistRefresh');
         YAHOO.util.Event.preventDefault(e);
      },
      
      /**
       * Folder Up Navigate button click handler
       *
       * @method onFolderUpButtonClick
       * @param e {object} DomEvent
       * @param oValue {object} Object passed back from addListener method
       */
      onFolderUpButtonClick: function DL_onFolderUpButtonClick(e, oValue)
      {
         var newPath = this.currentPath.substring(0, this.currentPath.lastIndexOf("/"));
         YAHOO.Bubbling.fire('onDoclistPathChanged',
         {
            path: newPath
         });
         YAHOO.util.Event.preventDefault(e);
      },
      

      /**
       * BUBBLING LIBRARY EVENT HANDLERS
       * Disconnected event handlers for inter-component event notification
       */

      /**
       * Path Changed event handler
       *
       * @method onDoclistPathChanged
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onDoclistPathChanged: function DL_onDoclistPathChanged(layer, args)
      {
         var obj = args[1];
         if (obj !== null)
         {
            // Was this our "first navigation" event?
            if (!!obj.doclistInitialNav)
            {
               return;
            }
            
            // Should be a path in the arguments
            if (obj.path !== null)
            {
               try
               {
                  // Update History Manager with new path. It will callback to update the doclist
                  YAHOO.util.History.navigate("path", obj.path);
               }
               catch (e)
               {
                  // Fallback for non-supported browsers, or hidden iframe loading delay
                  this._updateDocList.call(this, this.currentPath);
               }
            }
         }
      },
      
      /**
       * DocList Refresh Required event handler
       *
       * @method onDoclistRefresh
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onDoclistRefresh: function DL_onDoclistRefresh(layer, args)
      {
         this._updateDocList.call(this, this.currentPath);
      },

   
      /**
       * PRIVATE FUNCTIONS
       */
      
       /**
        * Updates document list by calling data webscript with current site and path
        *
        * @method _updateDocList
        * @param path {string} Path to navigate to
        */
      _updateDocList: function DL__updateDocList(path)
      {
         Alfresco.logger.debug("DocList_updateDocList:", path);
         
         function successHandler(sRequest, oResponse, oPayload)
         {
            this.currentPath = path;
            this.widgets.dataTable.onDataReturnInitializeTable.call(this.widgets.dataTable, sRequest, oResponse, oPayload);
         }
         
         this.widgets.dataSource.sendRequest("site=" + encodeURIComponent(this.options.siteId) + "&path=" + encodeURIComponent(path) + (this.options.showFolders ? "" : "&type=documents"),
         {
               success: successHandler,
               failure: null,
               scope: this
         });
      }

   };
})();
