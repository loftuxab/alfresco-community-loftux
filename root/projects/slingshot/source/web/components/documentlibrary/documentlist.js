/**
 * Copyright (C) 2005-2009 Alfresco Software Limited.
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
      Event = YAHOO.util.Event;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML,
      $links = Alfresco.util.activateLinks,
      $combine = Alfresco.util.combinePaths;

   /**
    * Preferences
    */
   var PREFERENCES_DOCLIST = "org.alfresco.share.documentList",
      PREF_SHOW_FOLDERS = PREFERENCES_DOCLIST + ".showFolders",
      PREF_SIMPLE_VIEW = PREFERENCES_DOCLIST + ".simpleView";

   
   /**
    * DocumentList constructor.
    * 
    * @param htmlId {String} The HTML id of the parent element
    * @return {Alfresco.DocumentList} The new DocumentList instance
    * @constructor
    */
   Alfresco.DocumentList = function(htmlId)
   {
      Alfresco.DocumentList.superclass.constructor.call(this, "Alfresco.DocumentList", htmlId, ["button", "menu", "container", "datasource", "datatable", "paginator", "json", "history"]);

      // Initialise prototype properties
      this.state =
      {
         actionEditOfflineActive: false
      };
      this.currentFilter =
      {
         filterId: "path",
         filterData: ""
      };
      this.actions = {};
      this.selectedFiles = {};
      this.afterDocListUpdate = [];
      this.doclistMetadata = {};
      this.previewTooltips = [];
      
      /**
       * Decoupled event listeners
       */
      // Specific event handlers
      YAHOO.Bubbling.on("deactivateAllControls", this.onDeactivateAllControls, this);
      YAHOO.Bubbling.on("metadataRefresh", this.onDocListRefresh, this);
      YAHOO.Bubbling.on("fileRenamed", this.onFileRenamed, this);
      YAHOO.Bubbling.on("filterChanged", this.onFilterChanged, this);
      YAHOO.Bubbling.on("folderCreated", this.onDocListRefresh, this);
      YAHOO.Bubbling.on("folderRenamed", this.onFileRenamed, this);
      YAHOO.Bubbling.on("highlightFile", this.onHighlightFile, this);
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
      YAHOO.Bubbling.on("filesWorkflowed", this.onDocListRefresh, this);
      YAHOO.Bubbling.on("filesPermissionsUpdated", this.onDocListRefresh, this);

      return this;
   };

   /**
    * Extend from Alfresco.component.Base
    */
   YAHOO.extend(Alfresco.DocumentList, Alfresco.component.Base);

   /**
    * Augment prototype with Actions module
    */
   YAHOO.lang.augmentProto(Alfresco.DocumentList, Alfresco.doclib.Actions);

   /**
    * Custom field generator functions
    */

   /**
    * Generate "filterChanged" event mark-up suitable for element attribute.
    *
    * @method generateFilterMarkup
    * @param filter {object} Object literal containing new filter parameters
    * @return {string} Mark-up for use in node attribute
    */
   Alfresco.DocumentList.generateFilterMarkup = function DL_generateFilterMarkup(filter)
   {
      var filterObj = Alfresco.util.cleanBubblingObject(filter),
         markup = YAHOO.lang.substitute("{filterOwner}|{filterId}|{filterData}|{filterDisplay}", filterObj, function(p_key, p_value, p_meta)
         {
            return typeof p_value == "undefined" ? "" : $html(p_value);
         });
      
      return markup;
   };
   
   /**
    * Generate "filterChanged" event mark-up specifically for path changes
    *
    * @method generatePathMarkup
    * @param locn {object} Location object containing path and folder name to navigate to
    * @return {string} Mark-up for use in node attribute
    */
   Alfresco.DocumentList.generatePathMarkup = function DL_generatePathMarkup(locn)
   {
      return Alfresco.DocumentList.generateFilterMarkup(
      {
         filterId: "path",
         filterData: $combine(locn.path, locn.file)
      });
   };
   
   /**
    * Generate URL to thumbnail image
    *
    * @method generateThumbnailUrl
    * @param path {YAHOO.widget.Record} File record
    * @return {string} URL to thumbnail
    */
   Alfresco.DocumentList.generateThumbnailUrl = function DL_generateThumbnailUrl(record)
   {
      return Alfresco.constants.PROXY_URI + "api/node/" + record.getData("nodeRef").replace(":/", "") + "/content/thumbnails/doclib?c=queue&ph=true";
   };

   /**
    * Generate URL to user profile page
    *
    * @method generateUserProfileUrl
    * @param userName {string} Username
    * @return {string} URL to profile page
    */
   Alfresco.DocumentList.generateUserProfileUrl = function DL_generateUserProfileUrl(userName)
   {
      return Alfresco.util.uriTemplate("userpage",
      {
         userid: userName,
         pageid: "profile"
      });
   };
   
   /**
    * Generate favourite indicator
    *
    * @method generateFavourite
    * @param scope {object} DocumentLibrary instance
    * @param record {object} DataTable record
    * @return {string} HTML mark-up for favourite indicator
    */
   Alfresco.DocumentList.generateFavourite = function DL_generateFavourite(scope, record)
   {
      var id = scope.id + "-fav-" + record.getId(),
         isFavourite = record.getData("isFavourite");

      return '<a id="' + id + '" class="favourite-document' + (isFavourite ? ' enabled' : '') + '" title="' + scope.msg("tip.favourite-document." + (isFavourite ? 'remove' : 'add')) + '">&nbsp;</a>';
   };

   
   /**
    * Augment prototype with main class implementation, ensuring overwrite is enabled
    */
   YAHOO.lang.augmentObject(Alfresco.DocumentList.prototype,
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
          * @default false
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
          * Holds IDs to register preview tooltips with.
          * 
          * @property previewTooltips
          * @type array
          */
         previewTooltips: null,
         
         /**
          * Number of multi-file uploads before grouping the Activity Post
          *
          * @property groupActivitiesAt
          * @type int
          * @default 5
          */
         groupActivitiesAt: 5,
         
         /**
          * Valid online edit mimetypes
          * Currently allowed are Microsoft Office 2003 and 2007 mimetypes for Excel, PowerPoint and Word only
          *
          * @property onlineEditMimetypes
          * @type object
          */
         onlineEditMimetypes:
         {
            "application/vnd.excel": true,
            "application/vnd.powerpoint": true,
            "application/msword": true,
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet": true,
            "application/vnd.openxmlformats-officedocument.presentationml.presentation": true,
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document": true 
         },
         
         /**
          * SharePoint (Vti) Server Details
          *
          * @property vtiServer
          * @type object
          */
         vtiServer: null
      },

      /**
       * Keeps track of different states
       */
      state:
      {
         /**
          * True if an an edit offline ajax call is in process
          *
          * @property: actionEditOfflineActive
          * @type: boolean
          * @default: false
          */
         actionEditOfflineActive: false
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
       * Total number of records (documents + folders) in the currentPath.
       * 
       * @property totalRecords
       * @type int
       * @default 0
       */
      totalRecords: 0,

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
       * @type Alfresco.FileUpload
       */
      fileUpload: null,

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
       * Fired by YUI when parent element is available for scripting.
       * Initial History Manager event registration
       *
       * @method onReady
       */
      onReady: function DL_onReady()
      {
         // Reference to self used by inline functions
         var me = this;

         // Set-up YUI History Managers
         this._setupHistoryManagers();

         // Hide/Show Folders button
         this.widgets.showFolders = Alfresco.util.createYUIButton(this, "showFolders-button", this.onShowFolders);
         this.widgets.showFolders.set("label", this.msg(this.options.showFolders ? "button.folders.hide" : "button.folders.show"));

         // Detailed/Simple List button
         this.widgets.simpleView =  Alfresco.util.createYUIButton(this, "simpleView-button", this.onSimpleView);
         this.widgets.simpleView.set("label", this.msg(this.options.simpleView ? "button.view.detailed" : "button.view.simple"));

         // File Select menu button
         this.widgets.fileSelect = Alfresco.util.createYUIButton(this, "fileSelect-button", this.onFileSelect,
         {
            type: "menu", 
            menu: "fileSelect-menu"
         });

         // Preferences service
         this.services.preferences = new Alfresco.service.Preferences();
         
         // DataSource definition
         var uriDocList = Alfresco.constants.PROXY_URI + "slingshot/doclib/doclist/";
         this.widgets.dataSource = new YAHOO.util.DataSource(uriDocList);
         this.widgets.dataSource.responseType = YAHOO.util.DataSource.TYPE_JSON;
         this.widgets.dataSource.responseSchema =
         {
            resultsList: "items",
            fields:
            [
               "index", "nodeRef", "type", "isFolder", "isLink", "mimetype", "fileName", "displayName", "status", "lockedBy", "lockedByUser", "title", "description",
               "createdOn", "createdBy", "createdByUser", "modifiedOn", "modifiedBy", "modifiedByUser", "version", "size", "contentUrl", "actionSet", "tags",
               "activeWorkflows", "isFavourite", "location", "permissions", "onlineEditUrl"
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
            
            // Fire event with parent metadata
            YAHOO.Bubbling.fire("doclistMetadata",
            {
               metadata: me.doclistMetadata
            });
            
            // Reset onlineEdit flag if correct conditions not met
            if ((YAHOO.env.ua.ie === 0) || (typeof me.options.vtiServer.port != "number"))
            {
               me.doclistMetadata.onlineEditing = false;
            }
            
            // Container userAccess event
            var permissions = me.doclistMetadata.parent.permissions;
            if (permissions && permissions.userAccess)
            {
               YAHOO.Bubbling.fire("userAccess",
               {
                  userAccess: permissions.userAccess
               });
            }
            
            // Update "Empty" message to reflect subfolders are available but hidden?
            var itemCounts = me.doclistMetadata.itemCounts;
            if (itemCounts.documents === 0 && itemCounts.folders > 0 && !me.options.showFolders)
            {
               var showFoldersLink = '<span class="show-folders-link theme-color-1" onclick="Alfresco.util.ComponentManager.get(\'' + me.id + '\').onShowFolders();">' + me.msg("message.empty.subfolders.link") + '</span>';
               me.widgets.dataTable.set("MSG_EMPTY", me.msg("message.empty.subfolders", showFoldersLink, itemCounts.folders));
            }
            
            return oParsedResponse;
         };


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
            Dom.setStyle(elCell, "width", oColumn.width + "px");
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

            elCell.innerHTML = '<input id="checkbox-' + oRecord.getId() + '" type="checkbox" name="fileChecked" value="'+ oData + '"' + (me.selectedFiles[oData] ? ' checked="checked">' : '>');
         };
          
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
            Dom.setStyle(elCell, "width", oColumn.width + "px");
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

            var record = oRecord.getData(),
               status = record.actionSet,
               tip = "",
               desc = "";
            
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
            if (lockType !== "")
            {
               tip = me.msg("tip." + lockType, record.lockedBy, record.lockedByUser);
               desc += '<div class="status"><img src="' + Alfresco.constants.URL_CONTEXT + 'components/documentlibrary/images/' + lockType + '-status-16.png" title="' + tip + '" alt="' + lockType + '" /></div>';
            }
            
            // In workflow status
            status = record.activeWorkflows;
            if (status !== "")
            {
               tip = me.msg("tip.active-workflow", status.split(",").length);
               desc += '<div class="status"><img src="' + Alfresco.constants.URL_CONTEXT + 'components/documentlibrary/images/workflow-16.png" title="' + tip + '" alt="' + tip + '" /></div>';
            }
            
            elCell.innerHTML = desc;
         };
          
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
            var record = oRecord.getData(),
               name = record.fileName,
               title = record.title,
               type = record.type,
               isLink = record.isLink,
               locn = record.location,
               extn = name.substring(name.lastIndexOf(".")),
               docDetailsUrl;

            if (me.options.simpleView)
            {
               /**
                * Simple View
                */
               oColumn.width = 40;
               Dom.setStyle(elCell, "width", oColumn.width + "px");
               Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

               if (type == "folder")
               {
                  elCell.innerHTML = '<span class="folder-small">' + (isLink ? '<span class="link"></span>' : '') + '<a href="#" class="filter-change" rel="' + Alfresco.DocumentList.generatePathMarkup(locn) + '"><img src="' + Alfresco.constants.URL_CONTEXT + 'components/documentlibrary/images/folder-32.png" /></a>';
               }
               else
               {
                  var id = me.id + '-preview-' + oRecord.getId();
                  docDetailsUrl = Alfresco.constants.URL_PAGECONTEXT + "site/" + me.options.siteId + "/document-details?nodeRef=" + record.nodeRef;
                  elCell.innerHTML = '<span id="' + id + '" class="icon32">' + (isLink ? '<span class="link"></span>' : '') + '<a href="' + docDetailsUrl + '"><img src="' + Alfresco.constants.URL_CONTEXT + 'components/images/filetypes/' + Alfresco.util.getFileIcon(name) + '" alt="' + extn + '" title="' + $html(title) + '" /></a></span>';
                  
                  // Preview tooltip
                  me.previewTooltips.push(id);
               }
            }
            else
            {
               /**
                * Detailed View
                */
               oColumn.width = 100;
               Dom.setStyle(elCell, "width", oColumn.width + "px");
               Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

               if (type == "folder")
               {
                  elCell.innerHTML = '<span class="folder">' + (isLink ? '<span class="link"></span>' : '') + '<a href="#" class="filter-change" rel="' + Alfresco.DocumentList.generatePathMarkup(locn) + '"><img src="' + Alfresco.constants.URL_CONTEXT + 'components/documentlibrary/images/folder-48.png" /></a>';
               }
               else
               {
                  docDetailsUrl = Alfresco.constants.URL_PAGECONTEXT + "site/" + me.options.siteId + "/document-details?nodeRef=" + record.nodeRef;
                  elCell.innerHTML = '<span class="thumbnail">' + (isLink ? '<span class="link"></span>' : '') + '<a href="' + docDetailsUrl + '"><img src="' + Alfresco.DocumentList.generateThumbnailUrl(oRecord) + '" alt="' + extn + '" title="' + $html(title) + '" /></a></span>';
               }
            }
         };

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
            var desc = "", docDetailsUrl, tags, tag, i, j;
            var record = oRecord.getData(),
               type = record.type,
               isLink = record.isLink,
               locn = record.location,
               title = "",
               description = record.description || me.msg("details.description.none");
            
            // Use title property if it's available
            if (record.title && record.title !== record.fileName)
            {
               title = '<span class="title">(' + $html(record.title) + ')</span>';
            }

            // Link handling
            if (isLink)
            {
               oRecord.setData("displayName", me.msg("details.link-to", record.displayName));
            }
            
            if (type == "folder")
            {
               /**
                * Folders
                */
               desc = '<h3 class="filename"><a href="#" class="filter-change" rel="' + Alfresco.DocumentList.generatePathMarkup(locn) + '">';
               desc += $html(record.displayName) + '</a>' + title + '</h3>';

               if (me.options.simpleView)
               {
                  /**
                   * Simple View
                   */
                  desc += '<div class="detail"><span class="item-simple"><em>' + me.msg("details.modified.on") + '</em> ' + Alfresco.util.formatDate(record.modifiedOn, "dd mmmm yyyy") + '</span>';
                  desc += '<span class="item-simple"><em>' + me.msg("details.by") + '</em> <a href="' + Alfresco.DocumentList.generateUserProfileUrl(record.modifiedByUser) + '">' + $html(record.modifiedBy) + '</a></span></div>';
               }
               else
               {
                  /**
                   * Detailed View
                   */
                  desc += '<div class="detail"><span class="item"><em>' + me.msg("details.modified.on") + '</em> ' + Alfresco.util.formatDate(record.modifiedOn) + '</span>';
                  desc += '<span class="item"><em>' + me.msg("details.modified.by") + '</em> <a href="' + Alfresco.DocumentList.generateUserProfileUrl(record.modifiedByUser) + '">' + $html(record.modifiedBy) + '</a></span></div>';
                  desc += '<div class="detail"><span class="item"><em>' + me.msg("details.description") + '</em> ' + $links($html(description)) + '</span></div>';
                  /* Tags */
                  tags = record.tags;
                  desc += '<div class="detail"><span class="item tag-item"><em>' + me.msg("details.tags") + '</em> ';
                  if (tags.length > 0)
                  {
                     for (i = 0, j = tags.length; i < j; i++)
                     {
                        tag = $html(tags[i]);
                        desc += '<span class="tag"><a href="#" class="tag-link" rel="' + tag + '" title="' + tags[i] + '">' + tag + '</a></span>';
                     }
                  }
                  else
                  {
                     desc += me.msg("details.tags.none");
                  }
                  desc += '</span></div><div class="detail">&nbsp;</div>';
               }
            }
            else
            {
               /**
                * Documents and Links
                */
               docDetailsUrl = Alfresco.constants.URL_PAGECONTEXT + "site/" + me.options.siteId + "/document-details?nodeRef=" + record.nodeRef;
                
               desc = '<h3 class="filename">' + Alfresco.DocumentList.generateFavourite(me, oRecord) + '<span id="' + me.id + '-preview-' + oRecord.getId() + '"><a href="' + docDetailsUrl + '">';
               desc += $html(record.displayName) + '</a></span>' + title + '</h3>';
               
               if (me.options.simpleView)
               {
                  /**
                   * Simple View
                   */
                  desc += '<div class="detail"><span class="item-simple"><em>' + me.msg("details.modified.on") + '</em> ' + Alfresco.util.formatDate(record.modifiedOn, "dd mmmm yyyy") + '</span>';
                  desc += '<span class="item-simple"><em>' + me.msg("details.by") + '</em> <a href="' + Alfresco.DocumentList.generateUserProfileUrl(record.modifiedByUser) + '">' + $html(record.modifiedBy) + '</a></span></div>';
               }
               else
               {
                  /**
                   * Detailed View
                   */
                  if (record.status.indexOf("workingCopy") != -1)
                  {
                     /**
                      * Working Copy
                      */
                     desc += '<div class="detail">';
                     desc += '<span class="item"><em>' + me.msg("details.checked-out.on") + '</em> ' + Alfresco.util.formatDate(record.modifiedOn) + '</span>';
                     desc += '<span class="item"><em>' + me.msg("details.checked-out.by") + '</em> <a href="' + Alfresco.DocumentList.generateUserProfileUrl(record.modifiedByUser) + '">' + $html(record.modifiedBy) + '</a></span>';
                     desc += '<span class="item"><em>' + me.msg("details.size") + '</em> ' + Alfresco.util.formatFileSize(record.size) + '</span>';
                     desc += '</div><div class="detail">';
                     desc += '<span class="item"><em>' + me.msg("details.description") + '</em> ' + $links($html(description)) + '</span>';
                     desc += '</div>';
                  }
                  else
                  {
                     /**
                      * Non-Working Copy
                      */
                     desc += '<div class="detail">';
                     desc += '<span class="item"><em>' + me.msg("details.modified.on") + '</em> ' + Alfresco.util.formatDate(record.modifiedOn) + '</span>';
                     desc += '<span class="item"><em>' + me.msg("details.modified.by") + '</em> <a href="' + Alfresco.DocumentList.generateUserProfileUrl(record.modifiedByUser) + '">' + $html(record.modifiedBy) + '</a></span>';
                     desc += '<span class="item"><em>' + me.msg("details.version") + '</em> ' + record.version + '</span>';
                     desc += '<span class="item"><em>' + me.msg("details.size") + '</em> ' + Alfresco.util.formatFileSize(record.size) + '</span>';
                     desc += '</div><div class="detail">';
                     desc += '<span class="item"><em>' + me.msg("details.description") + '</em> ' + $links($html(description)) + '</span>';
                     desc += '</div>';

                     /* Tags */
                     tags = record.tags;
                     desc += '<div class="detail"><span class="item tag-item"><em>' + me.msg("details.tags") + '</em> ';
                     if (tags.length > 0)
                     {
                        for (i = 0, j = tags.length; i < j; i++)
                        {
                           tag = $html(tags[i]);
                           desc += '<span class="tag"><a href="#" class="tag-link" rel="' + tag + '" title="' + tags[i] + '">' + tag + '</a></span>';
                        }
                     }
                     else
                     {
                        desc += me.msg("details.tags.none");
                     }
                     desc += '</span></div>';
                  }
               }
            }
            elCell.innerHTML = desc;
         };

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
            if (me.options.simpleView)
            {
               /**
                * Simple View
                */
                oColumn.width = 80;
            }
            else
            {
               /**
                * Detailed View
                */
                oColumn.width = 180;
            }
            Dom.setStyle(elCell, "width", oColumn.width + "px");
            Dom.setStyle(elCell.parentNode, "width", oColumn.width + "px");

            elCell.innerHTML = '<div id="' + me.id + '-actions-' + oRecord.getId() + '" class="hidden"></div>';
            
            var record = oRecord.getData();
            
            /**
             * Configure the Online Edit URL if enabled for this mimetype
             */
            if (me.doclistMetadata.onlineEditing && (record.mimetype in me.options.onlineEditMimetypes))
            {
               var loc = record.location;
               oRecord.setData("onlineEditUrl", window.location.protocol + "//" + window.location.hostname + ":" + me.options.vtiServer.port + "/" + $combine("alfresco", loc.site, loc.container, loc.path, loc.file));
            }
         };

         // DataTable column defintions
         var columnDefinitions =
         [
            { key: "nodeRef", label: "Select", sortable: false, formatter: renderCellSelected, width: 16 },
            { key: "status", label: "Status", sortable: false, formatter: renderCellStatus, width: 16 },
            { key: "thumbnail", label: "Preview", sortable: false, formatter: renderCellThumbnail, width: 100 },
            { key: "fileName", label: "Description", sortable: false, formatter: renderCellDescription },
            { key: "actions", label: "Actions", sortable: false, formatter: renderCellActions, width: 180 }
         ];

         // DataTable set-up and event registration
         this._setupDataTable(columnDefinitions);

         // Tooltip for thumbnail in Simple View
         this.widgets.previewTooltip = new YAHOO.widget.Tooltip(this.id + "-previewTooltip",
         {
            width: "108px"
         });
         this.widgets.previewTooltip.contextTriggerEvent.subscribe(function(type, args)
         {
            var context = args[0],
               record = me.widgets.dataTable.getRecord(context.id);
            this.cfg.setProperty("text", '<img src="' + Alfresco.DocumentList.generateThumbnailUrl(record) + '" />');
         });
         
         // Set the default view filter to be "path" unless set in initialFilter
         var filterObj = YAHOO.lang.merge(
         {
            filterId: "path",
            filterData: this.currentPath
         }, this.options.initialFilter);

         // Only fire this if it's not the path filter - otherwise we need to wait for the History Manager to be ready
         if (filterObj.filterId !== "path")
         {
            Alfresco.logger.debug("DL_onReady (initial filter)", "filterChanged =>", filterObj);
            YAHOO.Bubbling.fire("filterChanged", filterObj);
         }

         // Hook action events
         var fnActionHandler = function DL_fnActionHandler(layer, args)
         {
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "div");
            if (owner !== null)
            {
               if (typeof me[owner.className] == "function")
               {
                  args[1].stop = true;
                  var asset = me.widgets.dataTable.getRecord(args[1].target.offsetParent).getData();
                  me[owner.className].call(me, asset, owner);
               }
            }
      		 
            return true;
         };
         YAHOO.Bubbling.addDefaultAction("action-link", fnActionHandler);
         YAHOO.Bubbling.addDefaultAction("show-more", fnActionHandler);
         
         // Hook favourite document events
         var fnFavouriteHandler = function DL_fnFavouriteHandler(layer, args)
         {
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "div");
            if (owner !== null)
            {
               me.onFavouriteDocument.call(me, args[1].target.offsetParent, owner);
            }
      		 
            return true;
         };
         YAHOO.Bubbling.addDefaultAction("favourite-document", fnFavouriteHandler);
         
         // Hook filter change events
         var fnFilterChangeHandler = function DL_fnFilterChangeHandler(layer, args)
         {
            var owner = args[1].anchor;
            if (owner !== null)
            {
               var filter = owner.rel,
                  filterObj = {},
                  filters;
               if (owner.rel && owner.rel !== "")
               {
                  filters = filter.split("|");
                  args[1].stop = true;
                  filterObj =
                  {
                     filterOwner: decodeURI(filters[0]),
                     filterId: decodeURI(filters[1]),
                     filterData: decodeURI(filters[2]),
                     filterDisplay: decodeURI(filters[3])
                  };
                  Alfresco.logger.debug("DL_fnFilterChangeHandler", "filterChanged =>", filterObj);
                  YAHOO.Bubbling.fire("filterChanged", filterObj);
               }
            }
            return true;
         };
         YAHOO.Bubbling.addDefaultAction("filter-change", fnFilterChangeHandler);

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
            Alfresco.logger.error("Alfresco.DocumentList: Couldn't initialize HistoryManager.", e);
            this.onHistoryManagerReady();
         }
         
      },

      /**
       * History Manager set-up and event registration
       *
       * @method _setupHistoryManagers
       */
      _setupHistoryManagers: function DL__setupHistoryManagers()
      {
         /**
          * YUI History - path
          */
         var bookmarkedPath = YAHOO.util.History.getBookmarkedState("path") || "";
         while (bookmarkedPath != (bookmarkedPath = decodeURIComponent(bookmarkedPath))){}
         
         this.currentPath = bookmarkedPath || this.options.initialPath || "";
         this.currentPath = $combine("/", this.currentPath);

         // Register History Manager path update callback
         YAHOO.util.History.register("path", bookmarkedPath, function DL_onHistoryManagerPathChanged(newPath)
         {
            Alfresco.logger.debug("HistoryManager: path changed:" + newPath);
            
            if (this.expectedHistoryEvent)
            {
               // Clear the flag and update the DocList
               this.expectedHistoryEvent = false;
               this._updateDocList.call(this,
               {
                  path: (YAHOO.env.ua.gecko > 0) ? decodeURIComponent(newPath) : newPath,
                  page: this.currentPage
               });
            }
            else
            {
               // Unexpected navigation - source event needs to be filterChanged event handler
               var filter =
               {
                  doclistSourcedEvent: true,
                  filterId: "path",
                  filterData: newPath
               };
               Alfresco.logger.debug("DL_onHistoryManagerPathChanged", "filterChanged =>", filter);
               YAHOO.Bubbling.fire("filterChanged", filter);
            }
         }, null, this);


         /**
          * YUI History - page
          */
         var handlePagination = function DL_handlePagination(state, me)
         {
            me.widgets.paginator.setState(state);
            YAHOO.util.History.navigate("page", String(state.page));
         };

         if (this.options.usePagination)
         {
            var bookmarkedPage = YAHOO.util.History.getBookmarkedState("page") || "1";
            while (bookmarkedPage != (bookmarkedPage = decodeURIComponent(bookmarkedPage))){}
            this.currentPage = parseInt(bookmarkedPage || this.options.initialPage, 10);

            // Register History Manager page update callback
            YAHOO.util.History.register("page", bookmarkedPage, function DL_onHistoryManagerPageChanged(newPage)
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
               containers: [this.id + "-paginator", this.id + "-paginatorBottom"],
               rowsPerPage: this.options.pageSize,
               initialPage: this.currentPage,
               template: this.msg("pagination.template"),
               pageReportTemplate: this.msg("pagination.template.page-report"),
               previousPageLinkLabel: this.msg("pagination.previousPageLinkLabel"),
               nextPageLinkLabel: this.msg("pagination.nextPageLinkLabel")
            });
            
            this.widgets.paginator.subscribe("changeRequest", handlePagination, this);
            
            // Display the bottom paginator bar
            Dom.setStyle(this.id + "-doclistBarBottom", "display", "block");
         }
      },
      
      /**
       * DataTable set-up and event registration
       *
       * @method _setupDataTable
       * @private
       */
      _setupDataTable: function DL__setupDataTable(columnDefinitions)
      {
         var me = this;
         
         // DataTable definition
         this.widgets.dataTable = new YAHOO.widget.DataTable(this.id + "-documents", columnDefinitions, this.widgets.dataSource,
         {
            renderLoopSize: this.options.usePagination ? 16 : 32,
            initialLoad: false,
            dynamicData: true,
            MSG_EMPTY: this.msg("message.loading")
         });

         // Update totalRecords on the fly with value from server
         this.widgets.dataTable.handleDataReturnPayload = function DL_handleDataReturnPayload(oRequest, oResponse, oPayload)
         {
            me.totalRecords = oResponse.meta.totalRecords;
            return oResponse.meta;
         };

         // Custom error messages
         this._setDefaultDataTableErrors(this.widgets.dataTable);

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
                  me.widgets.dataTable.set("MSG_ERROR", response.message);
               }
               catch(e)
               {
                  me._setDefaultDataTableErrors(me.widgets.dataTable);
               }
            }
            else if (oResponse.results && !me.options.usePagination)
            {
               this.renderLoopSize = oResponse.results.length >> (YAHOO.env.ua.gecko === 1.8) ? 3 : 5;
            }
            
            // We don't get an renderEvent for an empty recordSet, but we'd like one anyway
            if (oResponse.results.length === 0)
            {
               this.fireEvent("renderEvent",
               {
                  type: "renderEvent"
               });
            }
            
            // Must return true to have the "Loading..." message replaced by the error message
            return true;
         };

         // File checked handler
         this.widgets.dataTable.subscribe("checkboxClickEvent", function(e)
         { 
            var id = e.target.value; 
            this.selectedFiles[id] = e.target.checked;
            YAHOO.Bubbling.fire("selectedFilesChanged");
         }, this, true);
         
         // Rendering complete event handler
         this.widgets.dataTable.subscribe("renderEvent", function()
         {
            Alfresco.logger.debug("DataTable renderEvent");
            
            if (this.widgets.dataTable.getRecordSet().getLength() === 0)
            {
               this.widgets.dataTable.set("renderLoopSize", 0);
            }
            else
            {
               this.widgets.dataTable.set("renderLoopSize", this.options.usePagination ? 16 : 32);
            }
            
            // Update the paginator if it's been created
            if (this.widgets.paginator)
            {
               Alfresco.logger.debug("Setting paginator state: page=" + this.currentPage + ", totalRecords=" + this.totalRecords);

               this.widgets.paginator.setState(
               {
                  page: this.currentPage,
                  totalRecords: this.totalRecords
               });
               this.widgets.paginator.render();
            }
            
            // Need to highlight a file now the data is available?
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
            
            // Register preview tooltips
            this.widgets.previewTooltip.cfg.setProperty("context", this.previewTooltips);
            
         }, this, true);
         
         // Enable row highlighting
         this.widgets.dataTable.subscribe("rowMouseoverEvent", this.onEventHighlightRow, this, true);
         this.widgets.dataTable.subscribe("rowMouseoutEvent", this.onEventUnhighlightRow, this, true);
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
            // Fire filterChanged event for first-time population
            var filterObj =
            {
               doclistFirstTimeNav: true,
               filterId: "path",
               filterData: this.currentPath
            };
            Alfresco.logger.debug("DL_onHistoryManagerReady", "filterChanged =>", filterObj);
            YAHOO.Bubbling.fire("filterChanged", filterObj);
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
         var files = [],
            recordSet = this.widgets.dataTable.getRecordSet(),
            record;
         
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
         var recordSet = this.widgets.dataTable.getRecordSet(),
            checks = YAHOO.util.Selector.query('input[type="checkbox"]', this.widgets.dataTable.getTbodyEl()),
            len = checks.length,
            record, i, fnCheck, typeMap;

         var typeMapping =
         {
            selectDocuments: "document",
            selectFolders: "folder"
         };

         switch (p_selectType)
         {
            case "selectAll":
               fnCheck = function(assetType, isChecked)
               {
                  return true;
               };
               break;
            
            case "selectNone":
               fnCheck = function(assetType, isChecked)
               {
                  return false;
               };
               break;

            case "selectInvert":
               fnCheck = function(assetType, isChecked)
               {
                  return !isChecked;
               };
               break;

            case "selectDocuments":
            case "selectFolders":
               typeMap = typeMapping[p_selectType];
               fnCheck = function(assetType, isChecked)
               {
                  if (typeof typeMap === "object")
                  {
                     return typeMap[assetType];
                  }
                  return assetType == typeMap;
               };
               break;

            default:
               fnCheck = function(assetType, isChecked)
               {
                  return isChecked;
               };
         }

         for (i = 0; i < len; i++)
         {
            record = recordSet.getRecord(i);
            this.selectedFiles[record.getData("nodeRef")] = checks[i].checked = fnCheck(record.getData("type"), checks[i].checked);
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
         this.widgets.showFolders.set("label", this.msg(this.options.showFolders ? "button.folders.hide" : "button.folders.show"));
         this.services.preferences.set(PREF_SHOW_FOLDERS, this.options.showFolders);
         YAHOO.Bubbling.fire("metadataRefresh");
         if (e)
         {
            Event.preventDefault(e);
         }
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
         p_obj.set("label", this.msg(this.options.simpleView ? "button.view.detailed" : "button.view.simple"));

         this.services.preferences.set(PREF_SIMPLE_VIEW, this.options.simpleView);

         YAHOO.Bubbling.fire("metadataRefresh");
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
         var domEvent = aArgs[0];
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

         // elRename is the element id of the rename file link
         // var elRename = Dom.get(this.id + "-rename-" + oArgs.target.id);

         // elActions is the element id of the active table cell where we'll inject the actions
         var elActions = Dom.get(this.id + "-actions-" + oArgs.target.id);

         // Inject the correct action elements into the actionsId element
         if (elActions.firstChild === null)
         {
            // Retrieve the actionSet for this asset
            var record = this.widgets.dataTable.getRecord(oArgs.target.id),
               actionSet = record.getData("actionSet");
            
            // Clone the actionSet template node from the DOM
            var clone = Dom.get(this.id + "-actionSet-" + actionSet).cloneNode(true);
            
            // Token replacement
            clone.innerHTML = YAHOO.lang.substitute(window.unescape(clone.innerHTML), this.getActionUrls(record));

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
            
            // Inject a special-case permission if online editing is configured
            if (record.getData("onlineEditUrl"))
            {
               userAccess["online-edit"] = true;
            }
            
            // Remove any actions the user doesn't have permission for
            var actions = YAHOO.util.Selector.query("div", clone),
               actionPermissions, i, ii, j, jj;
            for (i = 0, ii = actions.length; i < ii; i++)
            {
               if (actions[i].firstChild.rel !== "")
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
            var splitAt = record.getData("type") == "folder" ? 2 : 3;
            actions = YAHOO.util.Selector.query("div", clone);
            if (actions.length > splitAt + (this.options.simpleView ? 0 : 1))
            {
               var moreContainer = Dom.get(this.id + "-moreActions").cloneNode(true);
               var containerDivs = YAHOO.util.Selector.query("div", moreContainer);
               // Insert the two necessary DIVs before the third action item
               Dom.insertBefore(containerDivs[0], actions[splitAt]);
               Dom.insertBefore(containerDivs[1], actions[splitAt]);
               // Now make action items three onwards children of the 2nd DIV
               var index, moreActions = actions.slice(splitAt);
               for (index in moreActions)
               {
                  if (moreActions.hasOwnProperty(index))
                  {
                     containerDivs[1].appendChild(moreActions[index]);
                  }
               }
            }
            
            elActions.appendChild(clone);
         }
         
         if (this.showingMoreActions)
         {
            this.deferredActionsMenu = elActions;
         }
         else if (!Dom.hasClass(document.body, "masked"))
         {
            this.currentActionsMenu = elActions;
            // Show the actions
            // Dom.removeClass(elRename, "hidden");
            Dom.removeClass(elActions, "hidden");
            this.deferredActionsMenu = null;
         }
      },

      /**
       * The urls to be used when creating links in the action cell
       *
       * @method getActionUrls
       * @param record {object} A data source element describing the item in the list
       * @return {object} Object literal containing URLs to be substituted in action placeholders
       */
      getActionUrls: function DL_getActionUrls(record)
      {
         var urlContextSite = Alfresco.constants.URL_PAGECONTEXT + "site/" + this.options.siteId,
            nodeRef = record.getData("nodeRef");

         return (
         {
            downloadUrl: Alfresco.constants.PROXY_URI + record.getData("contentUrl") + "?a=true",
            documentDetailsUrl: urlContextSite + "/document-details?nodeRef=" + nodeRef,
            folderDetailsUrl: urlContextSite + "/folder-details?nodeRef=" + nodeRef,
            editMetadataUrl: urlContextSite + "/edit-metadata?nodeRef=" + nodeRef
         });
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

         // var renameId = this.id + "-rename-" + oArgs.target.id;
         var elActions = Dom.get(this.id + "-actions-" + (oArgs.target.id));

         // Don't hide unless the More Actions drop-down is showing, or a dialog mask is present
         if (!this.showingMoreActions || Dom.hasClass(document.body, "masked"))
         {
            // Just hide the action links, rather than removing them from the DOM
            // Dom.addClass(renameId, "hidden");
            Dom.addClass(elActions, "hidden");
            this.deferredActionsMenu = null;
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
       * @param asset {object} Unused
       * @param elMore {element} DOM Element of "More Actions" link
       */
      onActionShowMore: function DL_onActionShowMore(asset, elMore)
      {
         var me = this;
         
         // Fix "More Actions" hover style
         Dom.addClass(elMore.firstChild, "highlighted");
         
         // Get the pop-up div, sibling of the "More Actions" link
         var elMoreActions = Dom.getNextSibling(elMore);
         Dom.removeClass(elMoreActions, "hidden");
         me.showingMoreActions = true;
         
         // Hide pop-up timer function
         var fnHidePopup = function DL_oASM_fnHidePopup()
         {
            // Need to rely on the "elMoreActions" enclosed variable, as MSIE doesn't support
            // parameter passing for timer functions.
            Event.removeListener(elMoreActions, "mouseover");
            Event.removeListener(elMoreActions, "mouseout");
            Dom.removeClass(elMore.firstChild, "highlighted");
            Dom.addClass(elMoreActions, "hidden");
            me.showingMoreActions = false;
            if (me.deferredActionsMenu !== null)
            {
               Dom.addClass(me.currentActionsMenu, "hidden");
               me.currentActionsMenu = me.deferredActionsMenu;
               me.deferredActionsMenu = null;
               Dom.removeClass(me.currentActionsMenu, "hidden");
            }
         };

         // Initial after-click hide timer - 5x the mouseOut timer delay
         if (elMoreActions.hideTimerId)
         {
            window.clearTimeout(elMoreActions.hideTimerId);
         }
         elMoreActions.hideTimerId = window.setTimeout(fnHidePopup, me.options.actionsPopupTimeout * 5);
         
         // Mouse over handler
         var onMouseOver = function DLSM_onMouseOver(e, obj)
         {
            // Clear any existing hide timer
            if (obj.hideTimerId)
            {
               window.clearTimeout(obj.hideTimerId);
               obj.hideTimerId = null;
            }
         };
         
         // Mouse out handler
         var onMouseOut = function DLSM_onMouseOut(e, obj)
         {
            var elTarget = Event.getTarget(e);
            var related = elTarget.relatedTarget;

            // In some cases we should ignore this mouseout event
            if ((related != obj) && (!Dom.isAncestor(obj, related)))
            {
               if (obj.hideTimerId)
               {
                  window.clearTimeout(obj.hideTimerId);
               }
               obj.hideTimerId = window.setTimeout(fnHidePopup, me.options.actionsPopupTimeout);
            }
         };
         
         Event.on(elMoreActions, "mouseover", onMouseOver, elMoreActions);
         Event.on(elMoreActions, "mouseout", onMouseOut, elMoreActions);
      },
      
      /**
       * Edit Offline.
       *
       * @override
       * @method onActionEditOffline
       * @param asset {object} Object literal representing file or folder to be actioned
       */
      onActionEditOffline: function DL_onActionEditOffline(asset)
      {
         if (!this.state.actionEditOfflineActive)
         {
            // Make sure we don't call edit offline twice
            this.state.actionEditOfflineActive = true;

            var path = asset.location.path,
               fileName = asset.fileName,
               displayName = asset.displayName;

            var me = this;

            this.modules.actions.genericAction(
            {
               success:
               {
                  event:
                  {
                     name: "filterChanged",
                     obj:
                     {
                        filterId: "editingMe"
                     }
                  },
                  callback:
                  {
                     fn: function DL_oAEO_success(data)
                     {
                        me.state.actionEditOfflineActive = false;

                        // The filterChanged event causes the DocList to update, so we need to run these functions afterwards
                        var fnAfterUpdate = function DL_oAEO_success_afterUpdate()
                        {
                           var downloadUrl = Alfresco.constants.PROXY_URI + data.json.results[0].downloadUrl;
                           if (YAHOO.env.ua.ie > 6)
                           {
                              // MSIE7 blocks the download and gets the wrong URL in the "manual download bar"
                              Alfresco.util.PopupManager.displayPrompt(
                              {
                                 title: this.msg("message.edit-offline.success", displayName),
                                 text: this.msg("message.edit-offline.success.ie7"),
                                 buttons: [
                                 {
                                    text: this.msg("button.download"),
                                    handler: function DL_oAEO_success_download()
                                    {
                                       window.location = downloadUrl;
                                       this.destroy();
                                    },
                                    isDefault: true
                                 },
                                 {
                                    text: this.msg("button.close"),
                                    handler: function DL_oAEO_success_close()
                                    {
                                       this.destroy();
                                    }
                                 }]
                              });
                           }
                           else
                           {
                              Alfresco.util.PopupManager.displayMessage(
                              {
                                 text: this.msg("message.edit-offline.success", displayName)
                              });
                              // Kick off the download 3 seconds after the confirmation message
                              YAHOO.lang.later(3000, this, function()
                              {
                                 window.location = downloadUrl;
                              });
                           }
                        };
                        this.afterDocListUpdate.push(fnAfterUpdate);
                     },
                     scope: this
                  }
               },
               failure:
               {
                  callback:
                  {
                     fn: function DL_oAEO_failure()
                     {
                        me.state.actionEditOfflineActive = false;
                     }
                  },
                  message: this.msg("message.edit-offline.failure", displayName)
               },
               webscript:
               {
                  method: Alfresco.util.Ajax.POST,
                  name: "checkout/site/{site}/{container}{path}/{file}",
                  params:
                  {
                     site: this.options.siteId,
                     container: this.options.containerId,
                     path: Alfresco.util.encodeURIPath(path),
                     file: encodeURIComponent(fileName)
                  }
               }
            });
         }
      },
      
      /**
       * Edit Online.
       *
       * @method onActionEditOnline
       * @param asset {object} Object literal representing file or folder to be actioned
       */
      onActionEditOnline: function DL_onActionEditOnline(asset)
      {
         window.open(asset.onlineEditUrl, "_blank");
         // Really, we'd need to refresh after the document has been opened, but we don't know when/if this occurs
         YAHOO.Bubbling.fire("metadataRefresh");
      },


      /**
       * BUBBLING LIBRARY EVENT HANDLERS FOR PAGE EVENTS
       * Disconnected event handlers for inter-component event notification
       */
      
      /**
       * Path Changed handler
       *
       * @method pathChanged
       * @param path {string} New path
       * @param flags {object} Logic control flags
       */
      pathChanged: function DL_pathChanged(path, flags)
      {
         Alfresco.logger.debug("DL_pathChanged: ", path, flags);
         if (flags.doclistFirstTimeNav)
         {
            this._updateDocList.call(this,
            {
               path: path
            });
            return;
         }
         if (!flags.doclistSourcedEvent)
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
               while (bookmarkedState != (bookmarkedState = decodeURIComponent(bookmarkedState))){}
               if (path != bookmarkedState)
               {
                  var objNav =
                  {
                     path: (YAHOO.env.ua.gecko > 0) ? encodeURIComponent(path || "/") : path || "/"
                  };
                  
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
                     path: path
                  });
               }
            }
            catch (e)
            {
               // Fallback for non-supported browsers, or hidden iframe loading delay
               this._updateDocList.call(this,
               {
                  path: path
               });
            }
         }
         else
         {
            // The HistoryManager won't fire in this case although we do need to update the DocList
            this._updateDocList.call(this,
            {
               path: path
            });
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
            if (recordFound !== null)
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
            obj.filterOwner = obj.filterOwner || Alfresco.util.FilterManager.getOwner(obj.filterId);

            // Should be a filterId in the arguments
            this.currentFilter = Alfresco.util.cleanBubblingObject(obj);
            Alfresco.logger.debug("DL_onFilterChanged: ", this.currentFilter);

            // Special handling for path filter changes
            if (obj.filterId == "path")
            {
               this.pathChanged(obj.filterData,
               {
                  doclistFirstTimeNav: obj.doclistFirstTimeNav,
                  doclistSourcedEvent: obj.doclistSourcedEvent
               });
            }
            else
            {
               this._updateDocList.call(this,
               {
                  page: 1
               });
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
                  yPos = yPos - (document.body.clientHeight / 3);
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
         var index, fnDisable = Alfresco.util.disableYUIButton;
         for (index in this.widgets)
         {
            if (this.widgets.hasOwnProperty(index))
            {
               fnDisable(this.widgets[index]);
            }
         }
      },
      
      /**
       * Favourite document event handler
       *
       * @method onFavouriteDocument
       * @param asset {object} Object literal representing file or folder to be actioned
       */
      onFavouriteDocument: function DL_onFavouriteDocument(row)
      {
         var record = this.widgets.dataTable.getRecord(row),
            file = record.getData(),
            nodeRef = file.nodeRef;
         
         file.isFavourite = !file.isFavourite;
         this.widgets.dataTable.updateRow(record, file);
               
         var responseConfig =
         {
            failureCallback:
            {
               fn: function DL_oFD_failure(event, p_oRecord)
               {
                  // Reset the flag to it's previous state
                  var file = p_oRecord.getData();
                  file.isFavourite = !file.isFavourite;
                  this.widgets.dataTable.updateRow(p_oRecord, file);
                  Alfresco.util.PopupManager.displayPrompt(
                  {
                     text: this.msg("message.favourite.failure", file.displayName)
                  });
               },
               scope: this,
               obj: record
            }
         };

         var fnPref = file.isFavourite ? "add" : "remove";
         this.services.preferences[fnPref].call(this.services.preferences, Alfresco.service.Preferences.FAVOURITE_DOCUMENTS, nodeRef, responseConfig);
      },


      /**
       * PRIVATE FUNCTIONS
       */

      /**
       * Resets the YUI DataTable errors to our custom messages
       * NOTE: Scope could be YAHOO.widget.DataTable, so can't use "this"
       *
       * @method _setDefaultDataTableErrors
       * @param dataTable {object} Instance of the DataTable
       */
      _setDefaultDataTableErrors: function DL__setDefaultDataTableErrors(dataTable)
      {
         var msg = Alfresco.util.message;
         dataTable.set("MSG_EMPTY", msg("message.empty", "Alfresco.DocumentList"));
         dataTable.set("MSG_ERROR", msg("message.error", "Alfresco.DocumentList"));
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
         var successPath = (p_obj && (p_obj.path !== undefined)) ? p_obj.path : this.currentPath,
            successPage = (p_obj && (p_obj.page !== undefined)) ? p_obj.page : this.currentPage,
            loadingMessage = null,
            timerShowLoadingMessage = null;

         // Clear the current document list if the data webscript is taking too long
         var fnShowLoadingMessage = function DL_fnShowLoadingMessage()
         {
            Alfresco.logger.debug("DL__uDL_fnShowLoadingMessage: slow data webscript detected.");
            // Check the timer still exists. This is to prevent IE firing the event after we cancelled it. Which is "useful".
            if (timerShowLoadingMessage)
            {
               loadingMessage = Alfresco.util.PopupManager.displayMessage(
               {
                  displayTime: 0,
                  text: '<span class="wait">' + $html(this.msg("message.loading")) + '</span>',
                  noEscape: true,
                  effectDuration: 0.1
               });
            }
         };
         
         // Reset the custom error messages
         this._setDefaultDataTableErrors(this.widgets.dataTable);
         
         // Reset preview tooltips array
         this.previewTooltips = [];
         
         // More Actions menu no longer relevant
         this.showingMoreActions = false;
         
         // Slow data webscript message
         timerShowLoadingMessage = YAHOO.lang.later(this.options.loadingMessageDelay, this, fnShowLoadingMessage);
         
         var successHandler = function DL__uDL_successHandler(sRequest, oResponse, oPayload)
         {
            if (timerShowLoadingMessage)
            {
               // Stop the "slow loading" timed function
               Alfresco.logger.debug("DL__uDL_successHandler: Cancelling loading message timer.");
               timerShowLoadingMessage.cancel();
               timerShowLoadingMessage = null;
            }
            if (loadingMessage !== null)
            {
               Alfresco.logger.debug("DL__uDL_successHandler: Destroying loading message pop-up.");
               loadingMessage.destroy();
            }

            // Updating the Doclist may change the file selection
            var fnAfterUpdate = function DL__uDL_sH_fnAfterUpdate()
            {
               YAHOO.Bubbling.fire("selectedFilesChanged");
            };
            this.afterDocListUpdate.push(fnAfterUpdate);
            
            Alfresco.logger.debug("currentPath was [" + this.currentPath + "] now [" + successPath + "]");
            Alfresco.logger.debug("currentPage was [" + this.currentPage + "] now [" + successPage + "]");
            this.currentPath = successPath;
            this.currentPage = successPage;
            this.widgets.dataTable.onDataReturnInitializeTable.call(this.widgets.dataTable, sRequest, oResponse, oPayload);
         };
         
         var failureHandler = function DL__uDL_failureHandler(sRequest, oResponse)
         {
            if (timerShowLoadingMessage)
            {
               // Stop the "slow loading" timed function
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
                  this.widgets.dataTable.set("MSG_ERROR", response.message);
                  this.widgets.dataTable.showTableMessage(response.message, YAHOO.widget.DataTable.CLASS_ERROR);
                  if (oResponse.status == 404)
                  {
                     // Site or container not found - deactivate controls
                     YAHOO.Bubbling.fire("deactivateAllControls");
                  }
               }
               catch(e)
               {
                  this._setDefaultDataTableErrors(this.widgets.dataTable);
               }
            }
         };
         
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
            obj.page = this.widgets.paginator.getCurrentPage() || this.currentPage;
            obj.pageSize = this.widgets.paginator.getRowsPerPage();
         }

         // Passed-in overrides
         if (typeof p_obj == "object")
         {
            obj = YAHOO.lang.merge(obj, p_obj);
         }

         // Build the URI stem
         var params = YAHOO.lang.substitute("{type}/site/{site}/{container}" + (obj.filter.filterId == "path" ? "{path}" : ""),
         {
            type: encodeURIComponent(obj.type),
            site: encodeURIComponent(obj.site),
            container: encodeURIComponent(obj.container),
            path: $combine("/", Alfresco.util.encodeURIPath(obj.path))
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
   }, true);
})();
