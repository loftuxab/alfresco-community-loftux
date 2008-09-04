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
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Element = YAHOO.util.Element;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;
   
   /**
    * DocumentList constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.DocumentList} The new DocumentList instance
    * @constructor
    */
   Alfresco.DocumentList = function(htmlId)
   {
      // Mandatory properties
      this.name = "Alfresco.DocumentList";
      this.id = htmlId;

      // Initialise prototype properties
      this.widgets = {};
      this.currentFilter =
      {
         filterId: "path",
         filterOwner: "",
         filterData: ""
      };
      this.modules = {};
      this.actions = {};
      this.selectedFiles = {};
      this.tagId =
      {
         id: 0,
         tags: {}
      };
      this.afterDocListUpdate = [];
      this.doclistMetadata = {};
      
      // Register this component
      Alfresco.util.ComponentManager.register(this);

      // Load YUI Components
      Alfresco.util.YUILoaderHelper.require(["button", "menu", "container", "datasource", "datatable", "json", "history"], this.onComponentsLoaded, this);
      
      /**
       * Decoupled event listeners
       */
      // Specific event handlers
      YAHOO.Bubbling.on("deactivateAllControls", this.onDeactivateAllControls, this);
      YAHOO.Bubbling.on("doclistRefresh", this.onDocListRefresh, this);
      YAHOO.Bubbling.on("documentPreviewFailure", this.onDocumentPreviewFailure, this);
      YAHOO.Bubbling.on("fileRenamed", this.onFileRenamed, this);
      YAHOO.Bubbling.on("filterChanged", this.onFilterChanged, this);
      YAHOO.Bubbling.on("folderCreated", this.onDocListRefresh, this);
      YAHOO.Bubbling.on("folderRenamed", this.onFileRenamed, this);
      YAHOO.Bubbling.on("highlightFile", this.onHighlightFile, this);
      YAHOO.Bubbling.on("pathChanged", this.onPathChanged, this);
      YAHOO.Bubbling.on("tagSelected", this.onTagSelected, this);
      // File actions which may be part of a multi-file action set
      YAHOO.Bubbling.on("fileCopied", this.onFileAction, this);
      YAHOO.Bubbling.on("fileDeleted", this.onFileAction, this);
      YAHOO.Bubbling.on("fileMoved", this.onFileAction, this);
      YAHOO.Bubbling.on("fileWorkflowed", this.onFileAction, this);
      YAHOO.Bubbling.on("filePermissionsUpdated", this.onFileAction, this);
      YAHOO.Bubbling.on("folderCopied", this.onFileAction, this);
      YAHOO.Bubbling.on("folderDeleted", this.onFileAction, this);
      YAHOO.Bubbling.on("folderMoved", this.onFileAction, this);
      YAHOO.Bubbling.on("folderWorkflowed", this.onFileAction, this);
      YAHOO.Bubbling.on("folderPermissionsUpdated", this.onFileAction, this);
      // Multi-file actions
      YAHOO.Bubbling.on("filesCopied", this.onDocListRefresh, this);
      YAHOO.Bubbling.on("filesDeleted", this.onDocListRefresh, this);
      YAHOO.Bubbling.on("filesMoved", this.onDocListRefresh, this);
      YAHOO.Bubbling.on("filesWorkflowed", this.onDocListRefresh, this)
      YAHOO.Bubbling.on("filesPermissionsUpdated", this.onDocListRefresh, this);

      return this;
   }
   
   Alfresco.DocumentList.prototype =
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
          * Flag indicating whether folders are visible or not.
          * 
          * @property showFolders
          * @type boolean
          */
         showFolders: false,

         /**
          * Flag indicating whether the list shows a detailed view or a simple one.
          * 
          * @property simpleView
          * @type boolean
          */
         simpleView: false,

         /**
          * Flag indicating whether pagination is available or not.
          * 
          * @property usePagination
          * @type boolean
          */
         usePagination: false,

         /**
          * Current siteId.
          * 
          * @property siteId
          * @type string
          */
         siteId: "",

         /**
          * ContainerId representing root container
          *
          * @property containerId
          * @type string
          * @default "documentLibrary"
          */
         containerId: "documentLibrary",

         /**
          * Initial path to show on load (otherwise taken from URL hash).
          * 
          * @property initialPath
          * @type string
          */
         initialPath: "",

         /**
          * Initial page to show on load (otherwise taken from URL hash).
          * 
          * @property initialPage
          * @type int
          */
         initialPage: 1,

         /**
          * Number of items per page
          * 
          * @property pageSize
          * @type int
          */
         pageSize: 50,

         /**
          * Initial filter to show on load.
          * 
          * @property initialFilter
          * @type object
          */
         initialFilter: {},
         
         /**
          * Delay time value for "More Actions" popup, in milliseconds
          *
          * @property actionsPopupTimeout
          * @type int
          * @default 500
          */
         actionsPopupTimeout: 500,
         
         /**
          * Delay before showing "loading" message for slow data requests
          *
          * @property loadingMessageDelay
          * @type int
          * @default 1000
          */
         loadingMessageDelay: 1000,

         /**
          * FileName to highlight on initial DataTable render.
          * 
          * @property highlightFile
          * @type string
          */
         highlightFile: null,
         
         /**
          * Valid .swf preview mimetypes
          * Stored as an object literal for quick "string in object" look-up
          * @property previewMimetypes
          * @type object
          */
         previewMimetypes:
         {
            // Native PDF
            "application/pdf": true,
            // Microsoft Office 2003
            "application/vnd.excel": true,
            "application/vnd.powerpoint": true,
            "application/msword": true,
            // OpenOffice.org 2.0
            "application/vnd.oasis.opendocument.text": true,
            "application/vnd.oasis.opendocument.text-template": true,
            "application/vnd.oasis.opendocument.text-web": true,
            "application/vnd.oasis.opendocument.text-master": true,
            "application/vnd.oasis.opendocument.graphics": true,
            "application/vnd.oasis.opendocument.graphics-template": true,
            "application/vnd.oasis.opendocument.presentation": true,
            "application/vnd.oasis.opendocument.presentation-template": true,
            "application/vnd.oasis.opendocument.spreadsheet": true,
            "application/vnd.oasis.opendocument.spreadsheet-template": true,
            "application/vnd.oasis.opendocument.chart": true,
            "application/vnd.oasis.opendocument.formula": true,
            "application/vnd.oasis.opendocument.image": true,
            // OpenOffice.org 1.0 / StarOffice 6.0
            "application/vnd.sun.xml.calc": true,
            "application/vnd.sun.xml.draw": true,
            "application/vnd.sun.xml.impress": true,
            "application/vnd.sun.xml.writer": true
         }
      },
      
      /**
       * Current path being browsed.
       * 
       * @property currentPath
       * @type string
       */
      currentPath: "",

      /**
       * Current page being browsed.
       * 
       * @property currentPage
       * @type int
       * @default 1
       */
      currentPage: 1,

      /**
       * Current filter to filter document list.
       * 
       * @property currentFilter
       * @type object
       */
      currentFilter: null,

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
      widgets: null,

      /**
       * Object container for storing module instances.
       * 
       * @property modules
       * @type object
       */
      modules: null,

      /**
       * Object container for storing action markup elements.
       * 
       * @property actions
       * @type object
       */
      actions: null,

      /**
       * Object literal of selected states for visible files (indexed by nodeRef).
       * 
       * @property selectedFiles
       * @type object
       */
      selectedFiles: null,

      /**
       * Flag to indicate this HistoryManager event was expected.
       * An unexpected event means the user has updated the URL hash manually.
       * 
       * @property expectedHistoryEvent
       * @type boolean
       * @default false
       */
      expectedHistoryEvent: false,

      /**
       * Current actions menu being shown
       * 
       * @property currentActionsMenu
       * @type object
       * @default null
       */
      currentActionsMenu: null,

      /**
       * Whether "More Actions" pop-up is currently visible.
       * 
       * @property showingMoreActions
       * @type boolean
       * @default false
       */
      showingMoreActions: false,

      /**
       * Deferred actions menu element when showing "More Actions" pop-up.
       * 
       * @property deferredActionsMenu
       * @type object
       * @default null
       */
      deferredActionsMenu: null,

      /**
       * Object literal used to generate unique tag ids
       * 
       * @property tagId
       * @type object
       */
      tagId: null,
      
      /**
       * Deferred function calls for after a document list update
       *
       * @property afterDocListUpdate
       * @type array
       */
      afterDocListUpdate: null,

      /**
       * Metadata returned by doclist data webscript
       *
       * @property doclistMetadata
       * @type object
       * @default null
       */
      doclistMetadata: null,

      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.DocumentList} returns 'this' for method chaining
       */
      setOptions: function DL_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },
      
      /**
       * Set messages for this component.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.DocumentList} returns 'this' for method chaining
       */
      setMessages: function DL_setMessages(obj)
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
      onComponentsLoaded: function DL_onComponentsLoaded()
      {
         Event.onContentReady(this.id, this.onReady, this, true);
      },

      /**
       * Fired by YUI when parent element is available for scripting.
       * Initial History Manager event registration
       *
       * @method onReady
       */
      onReady: function DL_onReady()
      {
         // Reference to self used by inline functions
         var me = this;

         /**
          * YUI History - path
          */
         var bookmarkedPath = YAHOO.util.History.getBookmarkedState("path") || "";
         while (bookmarkedPath != (bookmarkedPath = decodeURIComponent(bookmarkedPath)));
         
         this.currentPath = bookmarkedPath || this.options.initialPath || "";
         if ((this.currentPath.length > 0) && (this.currentPath[0] != "/"))
         {
            this.currentPath = "/" + this.currentPath;
         }
         else if (this.currentPath == "/")
         {
            this.currentPath = "";
         }

         // Register History Manager path update callback
         YAHOO.util.History.register("path", "", function(newPath)
         {
            Alfresco.logger.debug("HistoryManager: path changed:" + newPath);
            if (this.expectedHistoryEvent)
            {
               // Clear the flag and update the DocList
               this.expectedHistoryEvent = false;
               this._updateDocList.call(this,
               {
                  path: (YAHOO.env.ua.gecko) ? decodeURIComponent(newPath) : newPath
               });
            }
            else
            {
               // Unexpected navigation - source event needs to be pathChanged event handler
               YAHOO.Bubbling.fire("pathChanged",
               {
                  path: newPath
               })
            }
         }, null, this);


         /**
          * YUI History - page
          */
         if (this.options.usePagination)
         {
            var bookmarkedPage = YAHOO.util.History.getBookmarkedState("page") || "1";
            while (bookmarkedPage != (bookmarkedPage = decodeURIComponent(bookmarkedPage)));
            this.currentPage = parseInt(bookmarkedPage || this.options.initialPage, 10);

            // Register History Manager page update callback
            YAHOO.util.History.register("page", bookmarkedPage, function(newPage)
            {
               Alfresco.logger.debug("HistoryManager: page changed:" + newPage);
               // Update the DocList
               if (this.currentPage != newPage)
               {
                  this._updateDocList.call(this,
                  {
                     page: newPage
                  });
               }
               else
               {
                  Alfresco.logger.debug("...page changed event ignored.");
               }
            }, null, this);

            // YUI Paginator definition
            this.widgets.paginator = new YAHOO.widget.Paginator(
            {
               containers: [this.id + "-paginator"],
               rowsPerPage: this.options.pageSize,
               initialPage: this.currentPage,
               template: this._msg("pagination.template"),
               pageReportTemplate: this._msg("pagination.template.page-report")
            });
         }


         // Hide/Show Folders button
         this.widgets.showFolders = Alfresco.util.createYUIButton(this, "showFolders-button", this.onShowFolders,
         {
            type: "checkbox",
            checked: this.options.showFolders
         });

         // Detailed/Simple List button
         this.widgets.simpleView =  Alfresco.util.createYUIButton(this, "simpleView-button", this.onSimpleView,
         {
            type: "checkbox",
            checked: this.options.simpleView
         });

         // File Select menu button
         this.widgets.fileSelect = Alfresco.util.createYUIButton(this, "fileSelect-button", this.onFileSelect,
         {
            type: "menu", 
            menu: "fileSelect-menu"
         });

         // DataSource definition
         var uriDocList = Alfresco.constants.PROXY_URI + "slingshot/doclib/doclist/";
         this.widgets.dataSource = new YAHOO.util.DataSource(uriDocList);
         this.widgets.dataSource.responseType = YAHOO.util.DataSource.TYPE_JSON;
         this.widgets.dataSource.connXhrMode = "queueRequests";
         this.widgets.dataSource.responseSchema =
         {
            resultsList: "items",
            fields:
            [
               "index", "nodeRef", "type", "mimetype", "icon32", "fileName", "displayName", "status", "lockedBy", "lockedByUser", "title", "description",
               "createdOn", "createdBy", "createdByUser", "modifiedOn", "modifiedBy", "modifiedByUser", "version", "size", "contentUrl", "actionSet", "tags",
               "activeWorkflows", "location", "permissions"
            ],
            metaFields:
            {
               paginationRecordOffset: "startIndex",
               totalRecords: "totalRecords"
            }
         };

         // Intercept data returned from data webscript to extract custom metadata
         this.widgets.dataSource.doBeforeCallback = function DL_doBeforeCallback(oRequest, oFullResponse, oParsedResponse)
         {
            me.doclistMetadata = oFullResponse.metadata;
            
            // Container userAccess event
            var permissions = me.doclistMetadata.permissions;
            if (permissions && permissions.userAccess)
            {
               YAHOO.Bubbling.fire("userAccess",
               {
                  userAccess: permissions.userAccess
               });
            }
            
            return oParsedResponse;
         }
         
         
         /**
          * Custom field generator functions
          */

         /**
          * Generate "pathChanged" event onClick mark-up
          *
          * @method generatePathOnClick
          * @param path {string} New path to navigate to
          * @return {string} Mark-up for use in onClick attribute
          */
         var generatePathOnClick = function DL_generatePathOnClick(path)
         {
            return "YAHOO.Bubbling.fire('pathChanged', {path: '" + path.replace(/[']/g, "\\'") + "'}); return false;";
         }
         
         /**
          * Generate URL to thumbnail image
          *
          * @method generateThumbnailUrl
          * @param path {YAHOO.widget.Record} File record
          * @return {string} URL to thumbnail
          */
         var generateThumbnailUrl = function DL_generateThumbnailUrl(record)
         {
            var url = Alfresco.constants.PROXY_URI + "api/node/" + record.getData("nodeRef").replace(":/", "");
            url += "/content/thumbnails/doclib?c=queue&ph=true";
            return url;
         }

         /**
          * Generate URL to user profile page
          *
          * @method generateUserProfileUrl
          * @param userName {string} Username
          * @return {string} URL to profile page
          */
         var generateUserProfileUrl = function DL_generateUserProfileUrl(userName)
         {
            return Alfresco.util.uriTemplate("userpage",
            {
               userid: userName,
               pageid: "profile"
            });
         }
         
         /**
          * Generate ID alias for tag, suitable for DOM ID attribute
          *
          * @method generateTagId
          * @param scope {object} DocumentLibrary instance
          * @param tagName {string} Tag name
          * @return {string} A unique DOM-safe ID for the tag
          */
         var generateTagId = function DL_generateTagId(scope, tagName)
         {
            var id = 0;
            var tagId = scope.tagId;
            if (tagName in tagId.tags)
            {
                id = tagId.tags[tagName];
            }
            else
            {
               tagId.id++;
               id = tagId.tags[tagName] = tagId.id;
            }
            return scope.id + "-tagId-" + id;
         }


         /**
          * DataTable Cell Renderers
          *
          * Each cell has a custom renderer defined as a custom function. See YUI documentation for details.
          * These MUST be inline in order to have access to the Alfresco.DocumentList class (via the "me" variable).
          */

         /**
          * Selector custom datacell formatter
          *
          * @method renderCellSelected
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         var renderCellSelected = function DL_renderCellSelected(elCell, oRecord, oColumn, oData)
         {
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

            elCell.innerHTML = '<input id="checkbox-' + oRecord.getId() + '" type="checkbox" name="fileChecked" value="'+ oData + '"' + (me.selectedFiles[oData] ? ' checked="checked">' : '>');
         }
          
         /**
          * Status custom datacell formatter
          *
          * @method renderCellStatus
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         var renderCellStatus = function DL_renderCellStatus(elCell, oRecord, oColumn, oData)
         {
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

            var status = oRecord.getData("actionSet");
            var tip = "";
            var desc = "";
            
            // Locked by/Editing status
            var lockType = "";
            switch (status)
            {
               case "locked":
                  lockType = "locked";
                  break;
               
               case "workingCopyOwner":
                  lockType = "editing";
                  break;
                  
               case "lockOwner":
                  lockType = "lock-owner";
                  break;
            }
            if (lockType != "")
            {
               tip = me._msg("tip." + lockType, oRecord.getData("lockedBy"), oRecord.getData("lockedByUser"));
               desc += '<div class="status"><img src="' + Alfresco.constants.URL_CONTEXT + 'components/documentlibrary/images/' + lockType + '-status-16.png" title="' + tip + '" alt="' + lockType + '" /></div>'
            }
            
            // In workflow status
            status = oRecord.getData("activeWorkflows");
            if (status != "")
            {
               tip = me._msg("tip.active-workflow", status.split(",").length);
               desc += '<div class="status"><img src="' + Alfresco.constants.URL_CONTEXT + 'components/documentlibrary/images/workflow-16.png" title="' + tip + '" alt="' + tip + '" /></div>'
            }
            
            elCell.innerHTML = desc;
         }
          
         /**
          * Thumbnail custom datacell formatter
          *
          * @method renderCellThumbnail
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         var renderCellThumbnail = function DL_renderCellThumbnail(elCell, oRecord, oColumn, oData)
         {
            var name = oRecord.getData("fileName");
            var extn = name.substring(name.lastIndexOf("."));

            if (me.options.simpleView)
            {
               /**
                * Simple View
                */
               oColumn.width = 40;
               Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

               if (oRecord.getData("type") == "folder")
               {
                  elCell.innerHTML = '<a href="" onclick="' + generatePathOnClick(me.currentPath + "/" + name) + '"><span class="folder-small"></span></a>';
               }
               else
               {
                  var docDetailsUrl = Alfresco.constants.URL_PAGECONTEXT + "site/" + me.options.siteId + "/document-details?nodeRef=" + oRecord.getData("nodeRef");
                  elCell.innerHTML = '<span id="' + me.id + '-preview-' + oRecord.getId() + '" class="icon32"><a href="' + docDetailsUrl + '"><img src="' + Alfresco.constants.URL_CONTEXT + 'components/documentlibrary/images/generic-file-32.png" alt="' + extn + '" /></a></span>';
               }
            }
            else
            {
               /**
                * Detailed View
                */
               oColumn.width = 100;
               Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

               if (oRecord.getData("type") == "folder")
               {
                  elCell.innerHTML = '<a href="" onclick="' + generatePathOnClick(me.currentPath + "/" + name) + '"><span class="folder"></span></a>';
               }
               else
               {
                  var docDetailsUrl = Alfresco.constants.URL_PAGECONTEXT + "site/" + me.options.siteId + "/document-details?nodeRef=" + oRecord.getData("nodeRef");
                  elCell.innerHTML = '<span id="' + me.id + '-preview-' + oRecord.getId() + '" class="thumbnail"><a href="' + docDetailsUrl + '"><img src="' + generateThumbnailUrl(oRecord) + '" alt="' + extn + '" /></a></span>';
               }
            }
         }

         /**
          * Description/detail custom datacell formatter
          *
          * @method renderCellDescription
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         var renderCellDescription = function DL_renderCellDescription(elCell, oRecord, oColumn, oData)
         {
            var desc = "";
            if (oRecord.getData("type") == "folder")
            {
               /**
                * Folders
                */
               desc = '<h3 class="filename"><a href="" onclick="' + generatePathOnClick(me.currentPath + "/" + oRecord.getData("fileName")) + '">';
               desc += '<b>' + $html(oRecord.getData("displayName")) + '</b></a></h3>';

               if (me.options.simpleView)
               {
                  /**
                   * Simple View
                   */
                  desc += '<div class="detail"><span class="item-simple"><b>' + me._msg("details.modified.on") + '</b> ' + Alfresco.util.formatDate(oRecord.getData("modifiedOn"), "dd mmmm yyyy") + '</span>';
                  desc += '<span class="item-simple"><b>' + me._msg("details.by") + '</b> <a href="' + generateUserProfileUrl(oRecord.getData("modifiedByUser")) + '">' + oRecord.getData("modifiedBy") + '</a></span></div>';
               }
               else
               {
                  /**
                   * Detailed View
                   */
                  desc += '<div class="detail"><span class="item"><b>' + me._msg("details.modified.on") + '</b> ' + Alfresco.util.formatDate(oRecord.getData("modifiedOn")) + '</span>';
                  desc += '<span class="item"><b>' + me._msg("details.modified.by") + '</b> <a href="' + generateUserProfileUrl(oRecord.getData("modifiedByUser")) + '">' + oRecord.getData("modifiedBy") + '</a></span></div>';
                  var description = oRecord.getData("description");
                  if (description == "")
                  {
                     description = me._msg("details.description.none");
                  }
                  desc += '<div class="detail"><span class="item"><b>' + me._msg("details.description") + '</b> ' + $html(description) + '</span></div>';
                  /* Tags */
                  var tags = oRecord.getData("tags");
                  desc += '<div class="detail"><span class="item tag-item"><b>' + me._msg("details.tags") + '</b> ';
                  if (tags.length > 0)
                  {
                     for (var i = 0, j = tags.length; i < j; i++)
                     {
                        desc += '<span id="' + generateTagId(me, tags[i]) + '" class="tag"><a href="#" class="tag-link" title="' + tags[i] + '"><span>' + $html(tags[i]) + '</span></a></span>';
                     }
                  }
                  else
                  {
                     desc += me._msg("details.tags.none");
                  }
                  desc += '</span></div>';
               }
            }
            else
            {
               /**
                * Documents
                */
               var docDetailsUrl = Alfresco.constants.URL_PAGECONTEXT + "site/" + me.options.siteId + "/document-details?nodeRef=" + oRecord.getData("nodeRef");
                
               desc = '<h3 class="filename"><span id="' + me.id + '-preview-' + oRecord.getId() + '"><a href="' + docDetailsUrl + '">' + $html(oRecord.getData("displayName")) + '</a></span></h3>';
               if (me.options.simpleView)
               {
                  /**
                   * Simple View
                   */
                  desc += '<div class="detail"><span class="item-simple"><b>' + me._msg("details.modified.on") + '</b> ' + Alfresco.util.formatDate(oRecord.getData("modifiedOn"), "dd mmmm yyyy") + '</span>';
                  desc += '<span class="item-simple"><b>' + me._msg("details.by") + '</b> <a href="' + generateUserProfileUrl(oRecord.getData("modifiedByUser")) + '">' + oRecord.getData("modifiedBy") + '</a></span></div>';
               }
               else
               {
                  /**
                   * Detailed View
                   */
                  var description = oRecord.getData("description");
                  if (description == "")
                  {
                     description = me._msg("details.description.none");
                  }

                  if (oRecord.getData("status").indexOf("workingCopy") != -1)
                  {
                     /**
                      * Working Copy
                      */
                     desc += '<div class="detail">';
                     desc += '<span class="item"><b>' + me._msg("details.checked-out.on") + '</b> ' + Alfresco.util.formatDate(oRecord.getData("modifiedOn")) + '</span>';
                     desc += '<span class="item"><b>' + me._msg("details.checked-out.by") + '</b> <a href="' + generateUserProfileUrl(oRecord.getData("modifiedByUser")) + '">' + oRecord.getData("modifiedBy") + '</a></span>';
                     desc += '<span class="item"><b>' + me._msg("details.size") + '</b> ' + Alfresco.util.formatFileSize(oRecord.getData("size")) + '</span>';
                     desc += '</div><div class="detail">';
                     desc += '<span class="item"><b>' + me._msg("details.description") + '</b> ' + $html(description) + '</span>';
                     desc += '</div>';
                  }
                  else
                  {
                     /**
                      * Non-Working Copy
                      */
                     desc += '<div class="detail">';
                     desc += '<span class="item"><b>' + me._msg("details.modified.on") + '</b> ' + Alfresco.util.formatDate(oRecord.getData("modifiedOn")) + '</span>';
                     desc += '<span class="item"><b>' + me._msg("details.modified.by") + '</b> <a href="' + generateUserProfileUrl(oRecord.getData("modifiedByUser")) + '">' + oRecord.getData("modifiedBy") + '</a></span>';
                     desc += '<span class="item"><b>' + me._msg("details.version") + '</b> ' + oRecord.getData("version") + '</span>';
                     desc += '<span class="item"><b>' + me._msg("details.size") + '</b> ' + Alfresco.util.formatFileSize(oRecord.getData("size")) + '</span>';
                     desc += '</div><div class="detail">';
                     desc += '<span class="item"><b>' + me._msg("details.description") + '</b> ' + $html(description) + '</span>';
                     desc += '</div>';

                     /* Tags */
                     var tags = oRecord.getData("tags");
                     desc += '<div class="detail"><span class="item tag-item"><b>' + me._msg("details.tags") + '</b> ';
                     if (tags.length > 0)
                     {
                        for (var i = 0, j = tags.length; i < j; i++)
                        {
                           desc += '<span id="' + generateTagId(me, tags[i]) + '" class="tag"><a href="#" class="tag-link" title="' + tags[i] + '"><span>' + $html(tags[i]) + '</span></a></span>';
                        }
                     }
                     else
                     {
                        desc += me._msg("details.tags.none");
                     }
                     desc += '</span></div>';
                  }
               }
            }
            elCell.innerHTML = desc;
         }

         /**
          * Actions custom datacell formatter
          *
          * @method renderCellActions
          * @param elCell {object}
          * @param oRecord {object}
          * @param oColumn {object}
          * @param oData {object|string}
          */
         var renderCellActions = function DL_renderCellActions(elCell, oRecord, oColumn, oData)
         {
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");
            Dom.setStyle(elCell.parentNode, "border-left", "3px solid #fff");

            elCell.innerHTML = '<div id="' + me.id + '-actions-' + oRecord.getId() + '" class="hidden"></div>';
         }


         // DataTable column defintions
         var columnDefinitions = [
         {
            key: "nodeRef", label: "Select", sortable: false, formatter: renderCellSelected, width: 16
         },
         {
            key: "status", label: "Status", sortable: false, formatter: renderCellStatus, width: 16
         },
         {
            key: "thumbnail", label: "Preview", sortable: false, formatter: renderCellThumbnail, width: 100
         },
         {
            key: "fileName", label: "Description", sortable: false, formatter: renderCellDescription
         },
         {
            key: "actions", label: "Actions", sortable: false, formatter: renderCellActions, width: 140
         }];

         // Temporary "empty datatable" message
         YAHOO.widget.DataTable.MSG_EMPTY = this._msg("message.loading");

         var handlePagination = function DL_handlePagination(state, dt)
         {
            YAHOO.util.History.navigate("page", String(state.page));
         }

         // DataTable definition
         this.widgets.dataTable = new YAHOO.widget.DataTable(this.id + "-documents", columnDefinitions, this.widgets.dataSource,
         {
            renderLoopSize: this.options.usePagination ? 16 : 32,
            initialLoad: false,
            /*
            initialRequest: this._buildDocListParams(
            {
               page: this.currentPage
            }),
            */
            paginationEventHandler: handlePagination,
            paginator: this.widgets.paginator
         });
         
         // Custom error messages
         this._setDefaultDataTableErrors();

         // Hook tableMsgShowEvent to clear out fixed-pixel width on <table> element (breaks resizer)
         this.widgets.dataTable.subscribe("tableMsgShowEvent", function(oArgs)
         {
            // NOTE: Scope needs to be DataTable
            this._elMsgTbody.parentNode.style.width = "";
         });
         
         // Override abstract function within DataTable to set custom error message
         this.widgets.dataTable.doBeforeLoadData = function DL_doBeforeLoadData(sRequest, oResponse, oPayload)
         {
            if (oResponse.error)
            {
               try
               {
                  var response = YAHOO.lang.JSON.parse(oResponse.responseText);
                  YAHOO.widget.DataTable.MSG_ERROR = response.message;
               }
               catch(e)
               {
                  me._setDefaultDataTableErrors();
               }
            }
            else if (oResponse.results && !me.options.usePagination)
            {
               this.renderLoopSize = oResponse.results.length >> (YAHOO.env.ua.gecko) ? 3 : 5;
            }
            
            // We don't get an initEvent for an empty recordSet, but we'd like one anyway
            if (oResponse.results.length == 0)
            {
               this.fireEvent("initEvent");
            }
            
            // Must return true to have the "Loading..." message replaced by the error message
            return true;
         }

         // File checked handler
         this.widgets.dataTable.subscribe("checkboxClickEvent", function(e)
         { 
            var id = e.target.value; 
            this.selectedFiles[id] = e.target.checked;
            YAHOO.Bubbling.fire("selectedFilesChanged");
         }, this, true);
         
         // Rendering complete event handler
         this.widgets.dataTable.subscribe("initEvent", function()
         {
            if (this.options.highlightFile)
            {
               YAHOO.Bubbling.fire("highlightFile",
               {
                  fileName: this.options.highlightFile
               });
            }

            // Deferred functions specified?
            for (var i = 0, j = this.afterDocListUpdate.length; i < j; i++)
            {
               this.afterDocListUpdate[i].call(this);
            }
            this.afterDocListUpdate = [];
            
         }, this, true);
         
         // Enable row highlighting
         this.widgets.dataTable.subscribe("rowMouseoverEvent", this.onEventHighlightRow, this, true);
         this.widgets.dataTable.subscribe("rowMouseoutEvent", this.onEventUnhighlightRow, this, true);
         
         // Set the default view filter to be "path" and the owner to be "Alfresco.DocListTree" unless set in initialFilter
         var filterObj = YAHOO.lang.merge(
         {
            filterId: "path",
            filterOwner: "Alfresco.DocListTree",
            filterData: this.currentPath
         }, this.options.initialFilter);

         YAHOO.Bubbling.fire("filterChanged", filterObj);

         // Hook action events
         var fnActionHandler = function DL_fnActionHandler(layer, args)
         {
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "div");
            if (owner !== null)
            {
               var action = owner.className;
               var target = args[1].target;
               if (typeof me[action] == "function")
               {
                  args[1].stop = true;
                  me[action].call(me, target.offsetParent, owner);
               }
            }
      		 
            return true;
         }
         YAHOO.Bubbling.addDefaultAction("action-link", fnActionHandler);
         YAHOO.Bubbling.addDefaultAction("show-more", fnActionHandler);

         // Hook tag clicks
         var fnTagHandler = function DL_fnTagHandler(layer, args)
         {
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "span");
            if (owner !== null)
            {
               var tagId = owner.id;
               tagId = tagId.substring(tagId.lastIndexOf("-") + 1);
               for (tag in me.tagId.tags)
               {
                  if (me.tagId.tags[tag] == tagId)
                  {
                     YAHOO.Bubbling.fire("tagSelected",
                     {
                        tagname: tag
                     });
                     break;
                  }
               }
               args[1].stop = true;
            }
      		 
            return true;
         }
         YAHOO.Bubbling.addDefaultAction("tag-link", fnTagHandler);

         // Hook preview clicks
         var fnPreviewHandler = function DL_fnPreviewHandler(layer, args)
         {
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "span");
            if (owner !== null)
            {
               var fileId = owner.id;
               fileId = fileId.substring(fileId.lastIndexOf("yui-rec"));
               var record = me.widgets.dataTable.getRecord(fileId);
               // Show the preview, or just the content?
               if (record.getData("mimetype") in (me.options.previewMimetypes))
               {
                  Alfresco.module.getDocumentPreviewInstance().show(
                  {
                      nodeRef: record.getData("nodeRef"),
                      fileName: record.getData("fileName"),
                      icon32: "/components/documentlibrary/images/generic-file-32.png"
                   });
               }
               else
               {
                  YAHOO.Bubbling.fire("documentPreviewFailure",
                  {
                     error: 0,
                     nodeRef: record.getData("nodeRef"),
                     failureUrl: Alfresco.constants.PROXY_URI + record.getData("contentUrl")
                  });
               }
               args[1].stop = true;
            }
      		 
            return true;
         }
         YAHOO.Bubbling.addDefaultAction("preview-link", fnPreviewHandler);
         
         // DocLib Actions module
         this.modules.actions = new Alfresco.module.DoclibActions();

         // Continue only when History Manager fires its onReady event
         YAHOO.util.History.onReady(this.onHistoryManagerReady, this, true);

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
            Alfresco.logger.debug("Alfresco.DocumentList: Couldn't initialize HistoryManager.", e.toString());
            this.onHistoryManagerReady();
         }
         
      },
   
      /**
       * Fired by YUI when History Manager is initialised and available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onHistoryManagerReady
       */
      onHistoryManagerReady: function DL_onHistoryManagerReady()
      {
         if (this.options.initialFilter.filterId == "path")
         {
            // Fire pathChanged event for first-time population
            YAHOO.Bubbling.fire("pathChanged",
            {
               path: this.currentPath,
               doclibFirstTimeNav: true
            });
         }
         
         // Finally show the component body here to prevent UI artifacts on YUI button decoration
         Dom.setStyle(this.id + "-body", "visibility", "visible");
      },
      
      /**
       * Public functions
       *
       * Functions designed to be called form external sources
       */

      /**
       * Public function to get array of selected files
       *
       * @method getSelectedFiles
       * @return {Array} Currently selected files
       */
      getSelectedFiles: function DL_getSelectedFiles()
      {
         var files = [];
         var recordSet = this.widgets.dataTable.getRecordSet();
         var record;
         
         for (var i = 0, j = recordSet.getLength(); i < j; i++)
         {
            record = recordSet.getRecord(i);
            if (this.selectedFiles[record.getData("nodeRef")])
            {
               files.push(record.getData());
            }
         }
         
         return files;
      },
      
      /**
       * Public function to select files by specified groups
       *
       * @method selectFiles
       * @param p_selectType {string} Can be one of the following:
       * <pre>
       * selectAll - all documents and folders
       * selectNone - deselect all
       * selectInvert - invert selection
       * selectDocuments - select all documents
       * selectFolders - select all folders
       * </pre>
       */
      selectFiles: function DL_selectFiles(p_selectType)
      {
         var recordSet = this.widgets.dataTable.getRecordSet();
         var record;
         var checks = YAHOO.util.Selector.query('input[type="checkbox"]', this.widgets.dataTable.getTbodyEl());
         var len = checks.length; 

         switch (p_selectType)
         {
            case "selectAll":
               for (var i = 0; i < len; i++)
               {
                  record = recordSet.getRecord(i);
                  this.selectedFiles[record.getData("nodeRef")] = checks[i].checked = true;
               }
               break;
            
            case "selectNone":
               for (var i = 0; i < len; i++)
               {
                  record = recordSet.getRecord(i);
                  this.selectedFiles[record.getData("nodeRef")] = checks[i].checked = false;
               }
               break;

            case "selectInvert":
               for (var i = 0; i < len; i++)
               {
                  record = recordSet.getRecord(i);
                  this.selectedFiles[record.getData("nodeRef")] = checks[i].checked = !checks[i].checked;
               }
               break;

            case "selectDocuments":
               for (var i = 0; i < len; i++)
               {
                  record = recordSet.getRecord(i);
                  this.selectedFiles[record.getData("nodeRef")] = checks[i].checked = record.getData("type") == "document";
               }
               break;

            case "selectFolders":
               for (var i = 0; i < len; i++)
               {
                  record = recordSet.getRecord(i);
                  this.selectedFiles[record.getData("nodeRef")] = checks[i].checked = record.getData("type") == "folder";
               }
               break;
         }
         
         YAHOO.Bubbling.fire("selectedFilesChanged");
      },


      /**
       * YUI WIDGET EVENT HANDLERS
       * Handlers for standard events fired from YUI widgets, e.g. "click"
       */

      /**
       * Show/Hide folders button click handler
       *
       * @method onShowFolders
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onShowFolders: function DL_onShowFolders(e, p_obj)
      {
         this.options.showFolders = !this.options.showFolders;
         p_obj.set("checked", this.options.showFolders);

         YAHOO.Bubbling.fire("doclistRefresh");
         Event.preventDefault(e);
      },
      
      /**
       * Show/Hide detailed list button click handler
       *
       * @method onSimpleView
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onSimpleView: function DL_onSimpleView(e, p_obj)
      {
         this.options.simpleView = !this.options.simpleView;
         p_obj.set("checked", this.options.simpleView);

         YAHOO.Bubbling.fire("doclistRefresh");
         Event.preventDefault(e);
      },
      
      /**
       * Multi-file select button click handler
       *
       * @method onFileSelect
       * @param sType {string} Event type, e.g. "click"
       * @param aArgs {array} Arguments array, [0] = DomEvent, [1] = EventTarget
       * @param p_obj {object} Object passed back from subscribe method
       */
      onFileSelect: function DL_onFileSelect(sType, aArgs, p_obj)
      {
         var domEvent = aArgs[0]
         var eventTarget = aArgs[1];

         // Get the className of the clicked item
         var action = Alfresco.util.findEventClass(eventTarget);

         this.selectFiles(action);

         Event.preventDefault(domEvent);
      },

      /**
       * Custom event handler to highlight row.
       *
       * @method onEventHighlightRow
       * @param oArgs.event {HTMLEvent} Event object.
       * @param oArgs.target {HTMLElement} Target element.
       */
      onEventHighlightRow: function DL_onEventHighlightRow(oArgs)
      {
         // Call through to get the row highlighted by YUI
         this.widgets.dataTable.onEventHighlightRow.call(this.widgets.dataTable, oArgs);

         /**
          * NOTE: YUI 2.6.0 drops the yuiRecordId property. The value can be found in target.id
          */
         var target = oArgs.target;

         // elRename is the element id of the rename file link
         // var elRename = Dom.get(this.id + "-rename-" + target.yuiRecordId);

         // elActions is the element id of the active table cell where we'll inject the actual links
         var elActions = Dom.get(this.id + "-actions-" + (target.yuiRecordId || target.id));

         // Inject the correct action elements into the actionsId element
         if (elActions.firstChild === null)
         {
            // Retrieve the actionSet for this asset
            var record = this.widgets.dataTable.getRecord(target);
            var actionSet = record.getData("actionSet");
            
            // Clone the actionSet template node from the DOM
            var clone = Dom.get(this.id + "-actionSet-" + actionSet).cloneNode(true);
            
            // Token replacement
            clone.innerHTML = YAHOO.lang.substitute(unescape(clone.innerHTML),
            {
               downloadUrl: Alfresco.constants.PROXY_URI + record.getData("contentUrl") + "?a=true"
            });
            
            // Generate an id
            clone.id = elActions.id + "_a";
            
            // Simple or detailed view
            Dom.addClass(clone, this.options.simpleView ? "simple" : "detailed");
            
            /**
             * NOTE: If linefeeds exist between the <div> and <a> tags, the firstChild property
             *       in the outer loop will return a text node "\n" instead of the <a> tag.
             */
            // Trim the items in the clone depending on the user's access
            var userAccess = record.getData("permissions").userAccess;
            var actions = YAHOO.util.Selector.query("div", clone);
            var actionPermissions, i, ii, j, jj;
            for (i = 0, ii = actions.length; i < ii; i++)
            {
               if (actions[i].firstChild.rel != "")
               {
                  actionPermissions = actions[i].firstChild.rel.split(",");
                  for (j = 0, jj = actionPermissions.length; j < jj; j++)
                  {
                     if (!userAccess[actionPermissions[j]])
                     {
                        clone.removeChild(actions[i]);
                        break;
                     }
                  }
               }
            }
            
            // Need the "More >" container?
            var splitAt = record.getData("type") == "folder" ? 1 : 3;
            actions = YAHOO.util.Selector.query("div", clone);
            if (actions.length > splitAt)
            {
               var moreContainer = Dom.get(this.id + "-moreActions").cloneNode(true);
               var containerDivs = YAHOO.util.Selector.query("div", moreContainer);
               // Insert the two necessary DIVs before the third action item
               Dom.insertBefore(containerDivs[0], actions[splitAt]);
               Dom.insertBefore(containerDivs[1], actions[splitAt]);
               // Now make action items three onwards children of the 2nd DIV
               var moreActions = actions.slice(splitAt);
               for (index in moreActions)
               {
                  containerDivs[1].appendChild(moreActions[index]);
               }
            }
            
            elActions.appendChild(clone);
         }
         
         if (this.showingMoreActions)
         {
            this.deferredActionsMenu = elActions;
         }
         else
         {
            this.currentActionsMenu = elActions;
            // Show the actions
            // Dom.removeClass(elRename, "hidden");
            Dom.removeClass(elActions, "hidden");
         }
      },

      /**
       * Custom event handler to unhighlight row.
       *
       * @method onEventUnhighlightRow
       * @param oArgs.event {HTMLEvent} Event object.
       * @param oArgs.target {HTMLElement} Target element.
       */
      onEventUnhighlightRow: function DL_onEventUnhighlightRow(oArgs)
      {
         // Call through to get the row unhighlighted by YUI
         this.widgets.dataTable.onEventUnhighlightRow.call(this.widgets.dataTable, oArgs);

         /**
          * NOTE: YUI 2.6.0 drops the yuiRecordId property. The value can be found in target.id
          */
         var target = oArgs.target;
         // var renameId = this.id + "-rename-" + target.yuiRecordId;
         var elActions = Dom.get(this.id + "-actions-" + (target.yuiRecordId || target.id));

         if (!this.showingMoreActions)
         {
            // Just hide the action links, rather than removing them from the DOM
            // Dom.addClass(renameId, "hidden");
            Dom.addClass(elActions, "hidden");
         }
      },


      /**
       * BUBBLING LIBRARY EVENT HANDLERS FOR ACTIONS
       * Disconnected event handlers for action event notification
       */

      /**
       * Show more actions pop-up.
       *
       * @method onActionShowMore
       * @param row {object} DataTable row representing file to be actioned
       * @param elMore {element} DOM Element of "More Actions" link
       */
      onActionShowMore: function DL_onActionShowMore(row, elMore)
      {
         var me = this;
         
         // Get the pop-up div, sibling of the "More Actions" link
         var elMoreActions = Dom.getNextSibling(elMore);
         Dom.removeClass(elMoreActions, "hidden");
         me.showingMoreActions = elMoreActions;
         
         // Hide pop-up timer function
         var fnHidePopup = function DL_oASM_fnHidePopup()
         {
            // Need to rely on the "elMoreActions" enclosed variable, as MSIE doesn't support
            // parameter passing for timer functions.
            Event.removeListener(elMoreActions, "mouseover");
            Event.removeListener(elMoreActions, "mouseout");
            Dom.addClass(elMoreActions, "hidden");
            me.showingMoreActions = false;
            if (me.deferredActionsMenu !== null)
            {
               Dom.addClass(me.currentActionsMenu, "hidden");
               me.currentActionsMenu = me.deferredActionsMenu;
               me.deferredActionsMenu = null;
               Dom.removeClass(me.currentActionsMenu, "hidden");
            }
         }

         // Initial after-click hide timer - 5x the mouseOut timer delay
         if (elMoreActions.hideTimerId)
         {
            clearTimeout(elMoreActions.hideTimerId);
         }
         elMoreActions.hideTimerId = setTimeout(fnHidePopup, me.options.actionsPopupTimeout * 5);
         
         // Mouse over handler
         var onMouseOver = function DLSM_onMouseOver(e, obj)
         {
            // Clear any existing hide timer
            if (obj.hideTimerId)
            {
               clearTimeout(obj.hideTimerId);
               obj.hideTimerId = null;
            }
         }
         
         // Mouse out handler
         var onMouseOut = function DLSM_onMouseOut(e, obj)
         {
            var elTarget = Event.getTarget(e);
            var elTag = elTarget.nodeName.toLowerCase();
            var related = e.relatedTarget;

            // In some cases we should ignore this mouseout event
            if ((related != obj) && (!Dom.isAncestor(obj, related)))
            {
               if (obj.hideTimerId)
               {
                  clearTimeout(obj.hideTimerId);
               }
               obj.hideTimerId = setTimeout(fnHidePopup, me.options.actionsPopupTimeout);
            }
         }
         
         Event.on(elMoreActions, "mouseover", onMouseOver, elMoreActions);
         Event.on(elMoreActions, "mouseout", onMouseOut, elMoreActions);
      },
      
      /**
       * Tag selected handler (document details)
       *
       * @method onTagSelected
       * @param tagId {string} Tag name.
       * @param target {HTMLElement} Target element clicked.
       */
      onTagSelected: function DL_onTagSelected(layer, args)
      {
         var obj = args[1];
         if (obj && (obj.tagName !== null))
         {
            alert(obj.tagName);
         }
      },

      /**
       * Asset details.
       *
       * @method onActionDetails
       * @param row {object} DataTable row representing file to be actioned
       */
      onActionDetails: function DL_onActionDetails(row)
      {
         if (!this.modules.details)
         {
            this.modules.details = new Alfresco.module.DoclibDetails(this.id + "-details");
         }

         this.modules.details.setOptions(
         {
            file: this.widgets.dataTable.getRecord(row).getData()
         }).showDialog();
      },

      /**
       * Delete Asset.
       *
       * @method onActionDelete
       * @param row {object} DataTable row representing file to be actioned
       */
      onActionDelete: function DL_onActionDelete(row)
      {
         var me = this;
         var record = this.widgets.dataTable.getRecord(row);
         Alfresco.util.PopupManager.displayPrompt(
         {
            text: this._msg("message.confirm.delete", record.getData("fileName")),
            buttons: [
            {
               text: this._msg("button.delete"),
               handler: function DL_onActionDelete_delete()
               {
                  this.destroy();
                  me._onActionDeleteConfirm.call(me, record);
               },
               isDefault: true
            },
            {
               text: this._msg("button.cancel"),
               handler: function DL_onActionDelete_cancel()
               {
                  this.destroy();
               }
            }]
         });
      },

      /**
       * Delete Asset confirmed.
       *
       * @method _onActionDeleteConfirm
       * @param record {object} DataTable record representing file to be actioned
       * @private
       */
      _onActionDeleteConfirm: function DL__onActionDeleteConfirm(record)
      {
         var fileType = record.getData("type");
         var fileName = record.getData("fileName");
         var filePath = this.currentPath + "/" + fileName;
         var displayName = record.getData("displayName");
         
         this.modules.actions.genericAction(
         {
            success:
            {
               event:
               {
                  name: fileType == "folder" ? "folderDeleted" : "fileDeleted",
                  obj:
                  {
                     path: filePath
                  }
               },
               message: this._msg("message.delete.success", displayName)
            },
            failure:
            {
               message: this._msg("message.delete.failure", displayName)
            },
            webscript:
            {
               name: "file/site/{site}/{container}{path}/{file}",
               method: Alfresco.util.Ajax.DELETE
            },
            params:
            {
               site: this.options.siteId,
               container: this.options.containerId,
               path: this.currentPath,
               file: fileName
            }
         });
      },

      /**
       * Edit Offline.
       *
       * @method onActionEditOffline
       * @param row {object} DataTable row representing file to be actioned
       */
      onActionEditOffline: function DL_onActionEditOffline(row)
      {
         var record = this.widgets.dataTable.getRecord(row);
         var fileName = record.getData("fileName");
         var displayName = record.getData("displayName");

         this.modules.actions.genericAction(
         {
            success:
            {
               event:
               {
                  name: "filterChanged",
                  obj:
                  {
                     filterId: "editingMe",
                     filterOwner: "Alfresco.DocListFilter"
                  }
               },
               callback:
               {
                  fn: function DL_oAEO_success(data)
                  {
                     // The filterChanged event causes the DocList to update, so we need to run these functions afterwards
                     var fnAfterUpdate = function DL_oAEO_success_afterUpdate()
                     {
                        Alfresco.util.PopupManager.displayMessage(
                        {
                           text: this._msg("message.edit-offline.success", displayName)
                        });
                        // Kick off the download 3 seconds after the confirmation message
                        YAHOO.lang.later(3000, this, function()
                        {
                           window.location = Alfresco.constants.PROXY_URI + data.json.results[0].downloadUrl;
                        });
                     }
                     this.afterDocListUpdate.push(fnAfterUpdate);
                  },
                  scope: this
               }
            },
            failure:
            {
               message: this._msg("message.edit-offline.failure", displayName)
            },
            webscript:
            {
               name: "checkout/site/{site}/{container}{path}/{file}",
               method: Alfresco.util.Ajax.POST
            },
            params:
            {
               site: this.options.siteId,
               container: this.options.containerId,
               path: this.currentPath,
               file: fileName
            }
         });
      },

      /**
       * Upload new version.
       *
       * @method onActionUploadNewVersion
       * @param row {object} DataTable row representing file to be actioned
       */
      onActionUploadNewVersion: function DL_onActionUploadNewVersion(row)
      {
         var record = this.widgets.dataTable.getRecord(row);
         var fileName = record.getData("fileName");
         var nodeRef = record.getData("nodeRef");

         if (this.fileUpload === null)
         {
            this.fileUpload = Alfresco.module.getFileUploadInstance();
            // this.fileUpload = new Alfresco.module.FileUpload(this.id + "-fileUpload");
         }

         // Show uploader for multiple files
         var description = this._msg("label.filter-description", record.getData("displayName"));
         var extensions = "*" + fileName.substring(fileName.lastIndexOf("."));
         var singleUpdateConfig =
         {
            siteId: this.options.siteId,
            containerId: this.options.containerId,
            updateNodeRef: nodeRef,
            updateFilename: fileName,
            filter: [
            {
               description: description,
               extensions: extensions
            }],
            mode: this.fileUpload.MODE_SINGLE_UPDATE,
            onFileUploadComplete:
            {
               fn: this.onNewVersionUploadComplete,
               scope: this
            }
         }
         this.fileUpload.show(singleUpdateConfig);
         Event.preventDefault(e);
      },

      /**
       * Called from the uploader component after a the new version has been uploaded.
       *
       * @method onNewVersionUploadComplete
       * @param complete {object} Object literal containing details of successful and failed uploads
       */
      onNewVersionUploadComplete: function DL_onNewVersionUploadComplete(complete)
      {
         // Do something after the new version is uploaded
      },

      /**
       * Cancel editing.
       *
       * @method onActionCancelEditing
       * @param row {object} DataTable row representing file to be actioned
       */
      onActionCancelEditing: function DL_onActionCancelEditing(row)
      {
         var record = this.widgets.dataTable.getRecord(row);
         var fileName = record.getData("fileName");
         var displayName = record.getData("displayName");
         var nodeRef = record.getData("nodeRef");

         this.modules.actions.genericAction(
         {
            success:
            {
               event:
               {
                  name: "doclistRefresh"
               },
               message: this._msg("message.edit-cancel.success", displayName)
            },
            failure:
            {
               message: this._msg("message.edit-cancel.failure", displayName)
            },
            webscript:
            {
               name: "cancel-checkout/node/{nodeRef}",
               method: Alfresco.util.Ajax.POST
            },
            params:
            {
               nodeRef: nodeRef.replace(":/", "")
            }
         });
      },
      
      /**
       * Copy single document or folder.
       *
       * @method onActionCopyTo
       * @param row {object} DataTable row representing file to be actioned
       */
      onActionCopyTo: function DL_onActionCopyTo(row)
      {
         var file = this.widgets.dataTable.getRecord(row).getData();
         
         if (!this.modules.copyTo)
         {
            this.modules.copyTo = new Alfresco.module.DoclibCopyTo(this.id + "-copyTo").setOptions(
            {
               siteId: this.options.siteId,
               containerId: this.options.containerId,
               path: this.currentPath,
               files: file
            });
         }
         else
         {
            this.modules.copyTo.setOptions(
            {
               path: this.currentPath,
               files: file
            })
         }
         this.modules.copyTo.showDialog();
      },

      /**
       * Move single document or folder.
       *
       * @method onActionMoveTo
       * @param row {object} DataTable row representing file to be actioned
       */
      onActionMoveTo: function DL_onActionMoveTo(row)
      {
         var file = this.widgets.dataTable.getRecord(row).getData();
         
         if (!this.modules.moveTo)
         {
            this.modules.moveTo = new Alfresco.module.DoclibMoveTo(this.id + "-moveTo").setOptions(
            {
               siteId: this.options.siteId,
               containerId: this.options.containerId,
               path: this.currentPath,
               files: file
            });
         }
         else
         {
            this.modules.moveTo.setOptions(
            {
               path: this.currentPath,
               files: file
            })
         }
         this.modules.moveTo.showDialog();
      },

      /**
       * Assign workflow.
       *
       * @method onActionAssignWorkflow
       * @param row {object} DataTable row representing file to be actioned
       */
      onActionAssignWorkflow: function DL_onActionAssignWorkflow(row)
      {
         var file = this.widgets.dataTable.getRecord(row).getData();
         
         if (!this.modules.assignWorkflow)
         {
            this.modules.assignWorkflow = new Alfresco.module.DoclibWorkflow(this.id + "-workflow").setOptions(
            {
               siteId: this.options.siteId,
               containerId: this.options.containerId,
               path: this.currentPath,
               files: file
            });
         }
         else
         {
            this.modules.assignWorkflow.setOptions(
            {
               path: this.currentPath,
               files: file
            })
         }
         this.modules.assignWorkflow.showDialog();
      },

      /**
       * Set permissions on a single document or folder.
       *
       * @method onActionManagePermissions
       * @param row {object} DataTable row representing file to be actioned
       */
      onActionManagePermissions: function DL_onActionManagePermissions(row)
      {
         var file = this.widgets.dataTable.getRecord(row).getData();
         
         if (!this.modules.permissions)
         {
            this.modules.permissions = new Alfresco.module.DoclibPermissions(this.id + "-permissions").setOptions(
            {
               siteId: this.options.siteId,
               containerId: this.options.containerId,
               path: this.currentPath,
               files: file
            });
         }
         else
         {
            this.modules.permissions.setOptions(
            {
               path: this.currentPath,
               files: file
            })
         }
         this.modules.permissions.showDialog();
      },


      /**
       * BUBBLING LIBRARY EVENT HANDLERS FOR PAGE EVENTS
       * Disconnected event handlers for inter-component event notification
       */

      /**
       * Path Changed event handler
       *
       * @method onPathChanged
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onPathChanged: function DL_onPathChanged(layer, args)
      {
         var obj = args[1];
         // Should be a path in the arguments
         if (obj && (obj.path !== null))
         {
            Alfresco.logger.debug("DL_onPathChanged: ", obj);
            if (!obj.doclistSourcedEvent)
            {
               /**
                * This event was received as a result of a UI event. We need to tell the History Manager about
                * it and perform the actual navigation in that callback.
                */
               try
               {
                  // Flag to indicate we're expecting the HistoryManager's event
                  this.expectedHistoryEvent = true;
                  
                  var bookmarkedState = YAHOO.util.History.getBookmarkedState("path");
                  while (bookmarkedState != (bookmarkedState = decodeURIComponent(bookmarkedState)));
                  if (obj.path != bookmarkedState)
                  {
                     var objNav =
                     {
                        path: (YAHOO.env.ua.gecko) ? encodeURIComponent(obj.path) : obj.path
                     }
                     
                     if (this.options.usePagination)
                     {
                        this.currentPage = 1;
                        objNav.page = "1";
                     }
                     
                     YAHOO.util.History.multiNavigate(objNav);
                  }
                  else
                  {
                     // The HistoryManager won't fire in this case although we do need to update the DocList
                     this._updateDocList.call(this,
                     {
                        path: obj.path
                     });
                  }
               }
               catch (e)
               {
                  // Fallback for non-supported browsers, or hidden iframe loading delay
                  this._updateDocList.call(this,
                  {
                     path: obj.path
                  });
               }
            }
         }
      },
      
      /**
       * Generic file action event handler
       *
       * @method onFileAction
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onFileAction: function DL_onFileAction(layer, args)
      {
         var obj = args[1];
         if (obj)
         {
            if (!obj.multiple)
            {
               this._updateDocList.call(this);
            }
         }
      },

      /**
       * File or folder renamed event handler
       *
       * @method onFileRenamed
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onFileRenamed: function DL_onFileRenamed(layer, args)
      {
         var obj = args[1];
         if (obj && (obj.file !== null))
         {
            var recordFound = this._findRecordByParameter(obj.file.nodeRef, "nodeRef");
            if (recordFound != null)
            {
               this.widgets.dataTable.updateRow(recordFound, obj.file);
               var el = this.widgets.dataTable.getTrEl(recordFound);
               Alfresco.util.Anim.pulse(el);
            }
         }
      },

      /**
       * DocList Refresh Required event handler
       *
       * @method onDocListRefresh
       * @param layer {object} Event fired (unused)
       * @param args {array} Event parameters (unused)
       */
      onDocListRefresh: function DL_onDocListRefresh(layer, args)
      {
         var obj = args[1];
         if (obj && (obj.highlightFile !== null))
         {
            this.options.highlightFile = obj.highlightFile;
         }
         this._updateDocList.call(this);
      },

      /**
       * DocList View Filter changed event handler
       *
       * @method onFilterChanged
       * @param layer {object} Event fired (unused)
       * @param args {array} Event parameters (new filterId)
       */
      onFilterChanged: function DL_onFilterChanged(layer, args)
      {
         var obj = args[1];
         if ((obj !== null) && (obj.filterId !== null))
         {
            Alfresco.logger.debug("DL_onFilterChanged: ", obj);
            // Should be a filterId in the arguments
            this.currentFilter =
            {
               filterId: obj.filterId,
               filterOwner: obj.filterOwner,
               filterData: obj.filterData
            };
            // Ignore if it's the path, as we'll update on the pathChanged event
            if (obj.filterId != "path")
            {
               this._updateDocList.call(this);
            }
         }
      },

      /**
       * Highlight file event handler
       * Used when a component (including the DocList itself on loading) wants to scroll to and highlight a file
       *
       * @method onHighlightFile
       * @param layer {object} Event fired (unused)
       * @param args {array} Event parameters (filename to be highlighted)
       */
      onHighlightFile: function DL_onHighlightFile(layer, args)
      {
         var obj = args[1];
         if ((obj !== null) && (obj.fileName !== null))
         {
            var recordFound = this._findRecordByParameter(obj.fileName, "fileName");
            if (recordFound !== null)
            {
               // Scroll the record into view and highlight it
               var el = this.widgets.dataTable.getTrEl(recordFound);
               var yPos = Dom.getY(el);
               if (YAHOO.env.ua.ie > 0)
               {
                  yPos = yPos - (document.body.clientHeight / 3)
               }
               else
               {
                  yPos = yPos - (window.innerHeight / 3);
               }
               window.scrollTo(0, yPos);
               Alfresco.util.Anim.pulse(el);
               this.options.highlightFile = null;

               // Select the file
               Dom.get("checkbox-" + recordFound.getId()).checked = true;
               this.selectedFiles[recordFound.getData("nodeRef")] = true;
               YAHOO.Bubbling.fire("selectedFilesChanged");
            }
         }
      },

      /**
       * Deactivate All Controls event handler
       *
       * @method onDeactivateAllControls
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onDeactivateAllControls: function DL_onDeactivateAllControls(layer, args)
      {
         for (widget in this.widgets)
         {
            this.widgets[widget].set("disabled", true);
         }
      },


      /**
       * Document preview failed event handler
       *
       * @method onDocumentPreviewFailure
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onDocumentPreviewFailure: function DL_onDocumentPreviewFailure(layer, args)
      {
         var obj = args[1];
         if ((obj !== null) && (obj.failureUrl !== null))
         {
            window.open(obj.failureUrl, "_blank");
         }
      },

      /**
       * PRIVATE FUNCTIONS
       */

      /**
       * Gets a custom message
       *
       * @method _msg
       * @param messageId {string} The messageId to retrieve
       * @return {string} The custom message
       * @private
       */
      _msg: function DL__msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, "Alfresco.DocumentList", Array.prototype.slice.call(arguments).slice(1));
      },
      
      /**
       * Resets the YUI DataTable errors to our custom messages
       * NOTE: Scope could be YAHOO.widget.DataTable, so can't use "this"
       *
       * @method _setDefaultDataTableErrors
       */
      _setDefaultDataTableErrors: function DL__setDefaultDataTableErrors()
      {
         var msg = Alfresco.util.message;
         YAHOO.widget.DataTable.MSG_EMPTY = msg("message.empty", "Alfresco.DocumentList");
         YAHOO.widget.DataTable.MSG_ERROR = msg("message.error", "Alfresco.DocumentList");
      },
      
      /**
       * Updates document list by calling data webscript with current site and path
       *
       * @method _updateDocList
       * @param p_obj.path {string} Optional path to navigate to (defaults to this.currentPath)
       * @param p_obj.page {string} Optional page to navigate to (defaults to this.widgets.paginator.get("page"))
       */
      _updateDocList: function DL__updateDocList(p_obj)
      {
         Alfresco.logger.debug("DL__updateDocList: ", p_obj);
         var successPath = (p_obj && (p_obj.path !== undefined)) ? p_obj.path : this.currentPath;
         var successPage = (p_obj && (p_obj.page !== undefined)) ? p_obj.page : this.currentPage;
         var loadingMessage = null;

         // Clear the current document list if the data webscript is taking too long
         var fnShowLoadingMessage = function DL_fnShowLoadingMessage()
         {
            Alfresco.logger.debug("DL_fnShowLoadingMessage: slow data webscript detected.");
            loadingMessage = Alfresco.util.PopupManager.displayMessage(
            {
               displayTime: 0,
               text: '<span class="wait">' + $html(this._msg("message.loading")) + '</span>',
               noEscape: true
            });
         }
         
         // Reset the custom error messages
         this._setDefaultDataTableErrors();
         
         var successHandler = function DL__uDL_successHandler(sRequest, oResponse, oPayload)
         {
            if (timerShowLoadingMessage)
            {
               // Stop the timed function from clearing the document list
               timerShowLoadingMessage.cancel();
            }
            if (loadingMessage)
            {
               loadingMessage.destroy();
            }

            // Updating the Doclist may change the file selection
            var fnAfterUpdate = function DL__uDL_sH_fnAfterUpdate()
            {
               YAHOO.Bubbling.fire("selectedFilesChanged");
            }
            this.afterDocListUpdate.push(fnAfterUpdate);
            
            Alfresco.logger.debug("currentPath was [" + this.currentPath + "] now [" + successPath + "]");
            Alfresco.logger.debug("currentPage was [" + this.currentPage + "] now [" + successPage + "]");
            this.currentPath = successPath;
            this.currentPage = successPage;
            this.widgets.dataTable.onDataReturnInitializeTable.call(this.widgets.dataTable, sRequest, oResponse, oPayload);
         }
         
         var failureHandler = function DL__uDL_failureHandler(sRequest, oResponse)
         {
            if (timerShowLoadingMessage)
            {
               // Stop the timed function from clearing the document list
               timerShowLoadingMessage.cancel();
            }
            if (loadingMessage !== null)
            {
               loadingMessage.destroy();
            }
            
            // Clear out deferred functions
            this.afterDocListUpdate = [];

            if (oResponse.status == 401)
            {
               // Our session has likely timed-out, so refresh to offer the login page
               window.location.reload(true);
            }
            else
            {
               try
               {
                  var response = YAHOO.lang.JSON.parse(oResponse.responseText);
                  YAHOO.widget.DataTable.MSG_ERROR = response.message;
                  this.widgets.dataTable.showTableMessage(response.message, YAHOO.widget.DataTable.CLASS_ERROR);
                  if (oResponse.status == 404)
                  {
                     // Site or container not found - deactivate controls
                     YAHOO.Bubbling.fire("deactivateAllControls");
                  }
               }
               catch(e)
               {
                  this._setDefaultDataTableErrors();
               }
            }
         }

         // Slow data webscript message
         var timerShowLoadingMessage = YAHOO.lang.later(this.options.loadingMessageDelay, this, fnShowLoadingMessage);
         
         // Update the DataSource
         var requestParams = this._buildDocListParams(p_obj || {});
         Alfresco.logger.debug("DataSource requestParams: ", requestParams);
         this.widgets.dataSource.sendRequest(requestParams,
         {
            success: successHandler,
            failure: failureHandler,
            scope: this
         });
      },

      /**
       * Build URI parameter string for doclist JSON data webscript
       *
       * @method _buildDocListParams
       * @param p_obj.page {string} Page number
       * @param p_obj.pageSize {string} Number of items per page
       * @param p_obj.path {string} Path to query
       * @param p_obj.type {string} Filetype to filter: "all", "documents", "folders"
       * @param p_obj.site {string} Current site
       * @param p_obj.container {string} Current container
       * @param p_obj.filter {string} Current filter
       */
      _buildDocListParams: function DL__buildDocListParams(p_obj)
      {
         // Essential defaults
         var obj = 
         {
            path: this.currentPath,
            type: this.options.showFolders ? "all" : "documents",
            site: this.options.siteId,
            container: this.options.containerId,
            filter: this.currentFilter
         };
         
         // Pagination in use?
         if (this.options.usePagination)
         {
            obj.page = this.widgets.paginator.get("page") || "1";
            obj.pageSize = this.widgets.paginator.get("rowsPerPage");
         }

         // Passed-in overrides
         if (typeof p_obj == "object")
         {
            obj = YAHOO.lang.merge(obj, p_obj);
         }

         // Build the URI stem
         var params = YAHOO.lang.substitute("{type}/site/{site}/{container}{path}",
         {
            type: encodeURIComponent(obj.type),
            site: encodeURIComponent(obj.site),
            container: encodeURIComponent(obj.container),
            path: encodeURI(obj.path)
         });

         // Filter parameters
         params += "?filter=" + encodeURIComponent(obj.filter.filterId);
         if (obj.filter.filterData)
         {
            params += "&filterData=" + encodeURIComponent(obj.filter.filterData);             
         }
         
         // Paging parameters
         if (this.options.usePagination)
         {
            params += "&size=" + obj.pageSize  + "&pos=" + obj.page;
         }
         return params;
      },
       
      /**
       * Searches the current recordSet for a record with the given parameter value
       *
       * @method _findRecordByParameter
       * @param p_value {string} Value to find
       * @param p_parameter {string} Parameter to look for the value in
       */
      _findRecordByParameter: function DL__findRecordByParameter(p_value, p_parameter)
      {
        var recordSet = this.widgets.dataTable.getRecordSet();
        for (var i = 0, j = recordSet.getLength(); i < j; i++)
        {
           if (recordSet.getRecord(i).getData(p_parameter) == p_value)
           {
              return recordSet.getRecord(i);
           }
        }
        return null;
      }
   };
})();
