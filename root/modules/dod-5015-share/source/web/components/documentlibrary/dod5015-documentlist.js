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
 * DOD5015 DocumentList component.
 * 
 * @namespace Alfresco
 * @class Alfresco.RecordsDocumentList
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
   var PREFERENCES_ROOT = "org.alfresco.share.documentList",
      PREF_SIMPLE_VIEW = PREFERENCES_ROOT + ".simpleView";

   
   /**
    * RecordsDocumentList constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.RecordsDocumentList} The new Records DocumentList instance
    * @constructor
    */
   Alfresco.RecordsDocumentList = function(htmlId)
   {
      Alfresco.RecordsDocumentList.superclass.constructor.call(this, htmlId);

      // Unregister the superclass component and register this one
      Alfresco.util.ComponentManager.unregister(Alfresco.DocumentLibrary.superclass);
      Alfresco.util.ComponentManager.register(this);

      return this;
   };
   
   YAHOO.extend(Alfresco.RecordsDocumentList, Alfresco.DocumentList,
   {
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
               // Unexpected navigation - source event needs to be pathChanged event handler
               YAHOO.Bubbling.fire("pathChanged",
               {
                  path: newPath
               });
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
               template: this._msg("pagination.template"),
               pageReportTemplate: this._msg("pagination.template.page-report"),
               previousPageLinkLabel: this._msg("pagination.previousPageLinkLabel"),
               nextPageLinkLabel: this._msg("pagination.nextPageLinkLabel")
            });
            
            this.widgets.paginator.subscribe("changeRequest", handlePagination, this);
            
            // Display the bottom paginator bar
            Dom.setStyle(this.id + "-doclistBarBottom", "display", "block");
         }

         // Detailed/Simple List button
         this.widgets.simpleView =  Alfresco.util.createYUIButton(this, "simpleView-button", this.onSimpleView);
         this.widgets.simpleView.set("label", this._msg(this.options.simpleView ? "button.view.detailed" : "button.view.simple"));

         // File Select menu button
         this.widgets.fileSelect = Alfresco.util.createYUIButton(this, "fileSelect-button", this.onFileSelect,
         {
            type: "menu", 
            menu: "fileSelect-menu"
         });

         // Preferences service
         this.services.preferences = new Alfresco.service.Preferences();
         
         // DataSource definition
         var uriDocList = Alfresco.constants.PROXY_URI + "slingshot/doclib/dod5015/doclist/";
         this.widgets.dataSource = new YAHOO.util.DataSource(uriDocList);
         this.widgets.dataSource.responseType = YAHOO.util.DataSource.TYPE_JSON;
         this.widgets.dataSource.responseSchema =
         {
            resultsList: "items",
            fields:
            [
               "index", "nodeRef", "type", "isLink", "mimetype", "fileName", "displayName", "status", "lockedBy", "lockedByUser", "title", "description",
               "createdOn", "createdBy", "createdByUser", "modifiedOn", "modifiedBy", "modifiedByUser", "version", "size", "contentUrl", "actionSet", "tags",
               "activeWorkflows", "location", "permissions", "onlineEditUrl"
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
         };
         
         
         /**
          * Custom field generator functions
          */

         /**
          * Generate "pathChanged" event onClick mark-up
          *
          * @method generatePathOnClick
          * @param locn {object} Location object containing path and folder name to navigate to
          * @return {string} Mark-up for use in onClick attribute
          */
         var generatePathOnClick = function DL_generatePathOnClick(locn)
         {
            var path = $combine(locn.path, locn.file);
            return "YAHOO.Bubbling.fire('pathChanged', {path: '" + path.replace(/[']/g, "\\'") + "'}); return false;";
         };
         
         /**
          * Generate URL to thumbnail image
          *
          * @method generateThumbnailUrl
          * @param path {YAHOO.widget.Record} File record
          * @return {string} URL to thumbnail
          */
         var generateThumbnailUrl = function DL_generateThumbnailUrl(record)
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
         var generateUserProfileUrl = function DL_generateUserProfileUrl(userName)
         {
            return Alfresco.util.uriTemplate("userpage",
            {
               userid: userName,
               pageid: "profile"
            });
         };

         /**
          * DataTable Cell Renderers
          *
          * Each cell has a custom renderer defined as a custom function. See YUI documentation for details.
          * These MUST be inline in order to have access to the Alfresco.RecordsDocumentList class (via the "me" variable).
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

            var status = oRecord.getData("actionSet"),
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
               tip = me._msg("tip." + lockType, oRecord.getData("lockedBy"), oRecord.getData("lockedByUser"));
               desc += '<div class="status"><img src="' + Alfresco.constants.URL_CONTEXT + 'components/documentlibrary/images/' + lockType + '-status-16.png" title="' + tip + '" alt="' + lockType + '" /></div>';
            }
            
            // In workflow status
            status = oRecord.getData("activeWorkflows");
            if (status !== "")
            {
               tip = me._msg("tip.active-workflow", status.split(",").length);
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
            var name = oRecord.getData("fileName"),
               title = oRecord.getData("title"),
               type = oRecord.getData("type"),
               isLink = oRecord.getData("isLink"),
               locn = oRecord.getData("location"),
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

               switch (type)
               {
                  case "recordSeries":
                  case "recordCategory":
                  case "recordFolder":
                     elCell.innerHTML = '<span class="folder-small">' + (isLink ? '<span></span>' : '') + '<a href="" onclick="' + generatePathOnClick(locn) + '"><img src="' + Alfresco.constants.URL_CONTEXT + 'components/documentlibrary/images/' + type + '-32.png" /></a>';
                     break;

                  case "nonElectronicRecord":
                     elCell.innerHTML = '<span class="folder-small">' + (isLink ? '<span></span>' : '') + '<img src="' + Alfresco.constants.URL_CONTEXT + 'components/documentlibrary/images/generic-file-32.png" />';
                     break;

                  default:
                     var id = me.id + '-preview-' + oRecord.getId();
                     docDetailsUrl = Alfresco.constants.URL_PAGECONTEXT + "site/" + me.options.siteId + "/document-details?nodeRef=" + oRecord.getData("nodeRef");
                     elCell.innerHTML = '<span id="' + id + '" class="icon32">' + (isLink ? '<span></span>' : '') + '<a href="' + docDetailsUrl + '"><img src="' + Alfresco.constants.URL_CONTEXT + 'components/images/filetypes/' + Alfresco.util.getFileIcon(name) + '" alt="' + extn + '" title="' + $html(title) + '" /></a></span>';
                  
                     // Preview tooltip
                     me.previewTooltips.push(id);
                     break;
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

               switch (type)
               {
                  case "recordSeries":
                  case "recordCategory":
                  case "recordFolder":
                     elCell.innerHTML = '<span class="folder">' + (isLink ? '<span></span>' : '') + '<a href="" onclick="' + generatePathOnClick(locn) + '"><img src="' + Alfresco.constants.URL_CONTEXT + 'components/documentlibrary/images/' + type + '-48.png" /></a>';
                     break;

                  case "nonElectronicRecord":
                     elCell.innerHTML = '<span class="folder">' + (isLink ? '<span></span>' : '') + '<img src="' + Alfresco.constants.URL_CONTEXT + 'components/documentlibrary/images/nonElectronicRecord.png" />';
                     break;

                  default:
                     docDetailsUrl = Alfresco.constants.URL_PAGECONTEXT + "site/" + me.options.siteId + "/document-details?nodeRef=" + oRecord.getData("nodeRef");
                     elCell.innerHTML = '<span class="thumbnail">' + (isLink ? '<span></span>' : '') + '<a href="' + docDetailsUrl + '"><img src="' + generateThumbnailUrl(oRecord) + '" alt="' + extn + '" title="' + $html(title) + '" /></a></span>';
                     break;
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
            var desc = "", description, docDetailsUrl, i, j;
            var type = oRecord.getData("type"),
               isLink = oRecord.getData("isLink"),
               locn = oRecord.getData("location");
            
            // Link handling
            if (isLink)
            {
               oRecord.setData("displayName", me._msg("details.link-to", oRecord.getData("displayName")));
            }

            switch (type)
            {
               /**
                * Folders
                */
               case "recordFolder":
                  desc = '<h3 class="filename"><a href="" onclick="' + generatePathOnClick(locn) + '">';
                  desc += $html(oRecord.getData("displayName")) + '</a></h3>';

                  if (me.options.simpleView)
                  {
                     /**
                      * Simple View
                      */
                     desc += '<div class="detail"><span class="item-simple"><em>' + me._msg("details.modified.on") + '</em> ' + Alfresco.util.formatDate(oRecord.getData("modifiedOn"), "dd mmmm yyyy") + '</span>';
                     desc += '<span class="item-simple"><em>' + me._msg("details.by") + '</em> <a href="' + generateUserProfileUrl(oRecord.getData("modifiedByUser")) + '">' + $html(oRecord.getData("modifiedBy")) + '</a></span></div>';
                  }
                  else
                  {
                     /**
                      * Detailed View
                      */
                     desc += '<div class="detail"><span class="item"><em>' + me._msg("details.modified.on") + '</em> ' + Alfresco.util.formatDate(oRecord.getData("modifiedOn")) + '</span>';
                     desc += '<span class="item"><em>' + me._msg("details.modified.by") + '</em> <a href="' + generateUserProfileUrl(oRecord.getData("modifiedByUser")) + '">' + $html(oRecord.getData("modifiedBy")) + '</a></span></div>';
                     description = oRecord.getData("description");
                     if (description === "")
                     {
                        description = me._msg("details.description.none");
                     }
                     desc += '<div class="detail"><span class="item"><em>' + me._msg("details.description") + '</em> ' + $links($html(description)) + '</span></div>';
                     desc += '</span></div><div class="detail">&nbsp;</div>';
                  }
                  break;

               case "recordSeries":
                  desc = '<h3 class="filename"><a href="" onclick="' + generatePathOnClick(locn) + '">';
                  desc += $html(oRecord.getData("displayName")) + '</a></h3>';

                  if (me.options.simpleView)
                  {
                     /**
                      * Simple View
                      */
                     desc += '<div class="detail"><span class="item-simple"><em>' + me._msg("details.modified.on") + '</em> ' + Alfresco.util.formatDate(oRecord.getData("modifiedOn"), "dd mmmm yyyy") + '</span>';
                     desc += '<span class="item-simple"><em>' + me._msg("details.by") + '</em> <a href="' + generateUserProfileUrl(oRecord.getData("modifiedByUser")) + '">' + $html(oRecord.getData("modifiedBy")) + '</a></span></div>';
                  }
                  else
                  {
                     /**
                      * Detailed View
                      */
                     desc += '<div class="detail"><span class="item"><em>' + me._msg("details.modified.on") + '</em> ' + Alfresco.util.formatDate(oRecord.getData("modifiedOn")) + '</span>';
                     desc += '<span class="item"><em>' + me._msg("details.modified.by") + '</em> <a href="' + generateUserProfileUrl(oRecord.getData("modifiedByUser")) + '">' + $html(oRecord.getData("modifiedBy")) + '</a></span></div>';
                     description = oRecord.getData("description");
                     if (description === "")
                     {
                        description = me._msg("details.description.none");
                     }
                     desc += '<div class="detail"><span class="item"><em>' + me._msg("details.description") + '</em> ' + $links($html(description)) + '</span></div>';
                     desc += '</span></div><div class="detail">&nbsp;</div>';
                  }
                  break;
               
               case "recordCategory":
                  desc = '<h3 class="filename"><a href="" onclick="' + generatePathOnClick(locn) + '">';
                  desc += $html(oRecord.getData("displayName")) + '</a></h3>';

                  if (me.options.simpleView)
                  {
                     /**
                      * Simple View
                      */
                     desc += '<div class="detail"><span class="item-simple"><em>' + me._msg("details.modified.on") + '</em> ' + Alfresco.util.formatDate(oRecord.getData("modifiedOn"), "dd mmmm yyyy") + '</span>';
                     desc += '<span class="item-simple"><em>' + me._msg("details.by") + '</em> <a href="' + generateUserProfileUrl(oRecord.getData("modifiedByUser")) + '">' + $html(oRecord.getData("modifiedBy")) + '</a></span></div>';
                  }
                  else
                  {
                     /**
                      * Detailed View
                      */
                     desc += '<div class="detail"><span class="item"><em>' + me._msg("details.modified.on") + '</em> ' + Alfresco.util.formatDate(oRecord.getData("modifiedOn")) + '</span>';
                     desc += '<span class="item"><em>' + me._msg("details.modified.by") + '</em> <a href="' + generateUserProfileUrl(oRecord.getData("modifiedByUser")) + '">' + $html(oRecord.getData("modifiedBy")) + '</a></span></div>';
                     description = oRecord.getData("description");
                     if (description === "")
                     {
                        description = me._msg("details.description.none");
                     }
                     desc += '<div class="detail"><span class="item"><em>' + me._msg("details.description") + '</em> ' + $links($html(description)) + '</span></div>';
                     desc += '</span></div><div class="detail">&nbsp;</div>';
                  }
                  break;
               
               case "nonElectronicRecord":
                  break;

               /**
                * Documents and Links
                */
               default:
                  docDetailsUrl = Alfresco.constants.URL_PAGECONTEXT + "site/" + me.options.siteId + "/document-details?nodeRef=" + oRecord.getData("nodeRef");
                
                  desc = '<h3 class="filename"><span id="' + me.id + '-preview-' + oRecord.getId() + '"><a href="' + docDetailsUrl + '">' + $html(oRecord.getData("displayName")) + '</a></span></h3>';
                  if (me.options.simpleView)
                  {
                     /**
                      * Simple View
                      */
                     desc += '<div class="detail"><span class="item-simple"><em>' + me._msg("details.modified.on") + '</em> ' + Alfresco.util.formatDate(oRecord.getData("modifiedOn"), "dd mmmm yyyy") + '</span>';
                     desc += '<span class="item-simple"><em>' + me._msg("details.by") + '</em> <a href="' + generateUserProfileUrl(oRecord.getData("modifiedByUser")) + '">' + $html(oRecord.getData("modifiedBy")) + '</a></span></div>';
                  }
                  else
                  {
                     /**
                      * Detailed View
                      */
                     description = oRecord.getData("description");
                     if (description === "")
                     {
                        description = me._msg("details.description.none");
                     }

                     desc += '<div class="detail">';
                     desc += '<span class="item"><em>' + me._msg("details.modified.on") + '</em> ' + Alfresco.util.formatDate(oRecord.getData("modifiedOn")) + '</span>';
                     desc += '<span class="item"><em>' + me._msg("details.modified.by") + '</em> <a href="' + generateUserProfileUrl(oRecord.getData("modifiedByUser")) + '">' + $html(oRecord.getData("modifiedBy")) + '</a></span>';
                     desc += '<span class="item"><em>' + me._msg("details.version") + '</em> ' + oRecord.getData("version") + '</span>';
                     desc += '<span class="item"><em>' + me._msg("details.size") + '</em> ' + Alfresco.util.formatFileSize(oRecord.getData("size")) + '</span>';
                     desc += '</div><div class="detail">';
                     desc += '<span class="item"><em>' + me._msg("details.description") + '</em> ' + $links($html(description)) + '</span>';
                     desc += '</div>';
                     desc += '</span></div>';
                  }
                  break;
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
            
            /**
             * Configure the Online Edit URL if conditions met
             */
            if (me.doclistMetadata.onlineEditing && (oRecord.getData("mimetype") in me.options.onlineEditMimetypes))
            {
               var loc = oRecord.getData("location"), path;
               
               oRecord.setData("onlineEditUrl", window.location.protocol + "//" + window.location.hostname + ":7070/" + $combine("alfresco", loc.site, loc.container, loc.path, loc.file));
            }
         };


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
            key: "actions", label: "Actions", sortable: false, formatter: renderCellActions, width: 180
         }];

         // DataTable definition
         this.widgets.dataTable = new YAHOO.widget.DataTable(this.id + "-documents", columnDefinitions, this.widgets.dataSource,
         {
            renderLoopSize: this.options.usePagination ? 16 : 32,
            initialLoad: false,
            dynamicData: true,
            MSG_EMPTY: this._msg("message.loading")
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
                  this.widgets.dataTable.set("MSG_ERROR", response.message);
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


         // Tooltip for thumbnail in Simple View
         this.widgets.previewTooltip = new YAHOO.widget.Tooltip(this.id + "-previewTooltip",
         {
            width: "108px"
         });
         this.widgets.previewTooltip.contextTriggerEvent.subscribe(function(type, args)
         {
            var context = args[0],
               record = me.widgets.dataTable.getRecord(context.id);
            this.cfg.setProperty("text", '<img src="' + generateThumbnailUrl(record) + '" />');
         });
         
         
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
               if (typeof me[owner.className] == "function")
               {
                  args[1].stop = true;
                  me[owner.className].call(me, args[1].target.offsetParent, owner);
               }
            }
      		 
            return true;
         };
         YAHOO.Bubbling.addDefaultAction("action-link", fnActionHandler);
         YAHOO.Bubbling.addDefaultAction("show-more", fnActionHandler);

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
            Alfresco.logger.debug("Alfresco.RecordsDocumentList: Couldn't initialize HistoryManager.", e.toString());
            this.onHistoryManagerReady();
         }
         
      },
   
      /**
       * BUBBLING LIBRARY EVENT HANDLERS FOR ACTIONS
       * Disconnected event handlers for action event notification
       */

      /**
       * Freeze action.
       *
       * @method onActionFreeze
       * @param row {object} DataTable row representing file to be actioned
       */
      onActionFreeze: function DL_onActionFreeze(row)
      {
         if (!this.modules.freeze)
         {
            this.modules.freeze = new Alfresco.module.RMFreeze(this.id + "-details");
         }

         this.modules.freeze.setOptions(
         {
            file: this.widgets.dataTable.getRecord(row).getData()
         }).showDialog();
      },

      /**
       * Cut Off action.
       *
       * @method onActionCutoff
       * @param row {object} DataTable row representing file to be actioned
       */
      onActionCutoff: function DL_onActionCutoff(row)
      {
         var record = this.widgets.dataTable.getRecord(row),
            displayName = record.getData("displayName"),
            nodeRef = record.getData("nodeRef");

         this.modules.actions.genericAction(
         {
            success:
            {
               event:
               {
                  name: "doclistRefresh"
               },
               message: this._msg("message.cutoff.success", displayName)
            },
            failure:
            {
               message: this._msg("message.cutoff.failure", displayName)
            },
            webscript:
            {
               stem: Alfresco.constants.PROXY_URI + "/api/rma/actions/",
               name: "ExecutionQueue",
               method: Alfresco.util.Ajax.POST
            },
            params:
            {
               name: "cutoff",
               nodeRef: nodeRef.replace(":/", "")
            }
         });
      }
   });
})();
