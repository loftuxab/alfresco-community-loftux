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
      $combine = Alfresco.util.combinePaths,
      $jsonDate = Alfresco.util.fromExplodedISO8601;

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
      return Alfresco.RecordsDocumentList.superclass.constructor.call(this, htmlId);
   };
   
   YAHOO.extend(Alfresco.RecordsDocumentList, Alfresco.DocumentList,
   {
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
               template: this.msg("pagination.template"),
               pageReportTemplate: this.msg("pagination.template.page-report"),
               previousPageLinkLabel: this.msg("pagination.previousPageLinkLabel"),
               nextPageLinkLabel: this.msg("pagination.nextPageLinkLabel")
            });
            
            this.widgets.paginator.subscribe("changeRequest", handlePagination, this);
            
            // Display the bottom paginator bar
            Dom.setStyle(this.id + "-doclistBarBottom", "display", "block");
         }

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
         var uriDocList = Alfresco.constants.PROXY_URI + "slingshot/doclib/dod5015/doclist/";
         this.widgets.dataSource = new YAHOO.util.DataSource(uriDocList);
         this.widgets.dataSource.responseType = YAHOO.util.DataSource.TYPE_JSON;
         this.widgets.dataSource.responseSchema =
         {
            resultsList: "items",
            fields:
            [
               "index", "nodeRef", "type", "mimetype", "fileName", "displayName", "status", "title", "description", "author",
               "createdOn", "createdBy", "createdByUser", "modifiedOn", "modifiedBy", "modifiedByUser", "size", "version", "contentUrl", "actionSet", "tags",
               "location", "permissions", "dod5015"
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

            var dataStatus = oRecord.getData("status");
            if (dataStatus.length > 0)
            {
               var statuses = dataStatus.split(","),
                  status, s, SPACE = " ", meta,
                  tip = "",
                  desc = "";

               for (var i = 0, j = statuses.length; i < j; i++)
               {
                  status = statuses[i];
                  meta = "";
                  s = status.indexOf(SPACE);
                  if (s > -1)
                  {
                     meta = status.substring(s + 1);
                     status = status.substring(0, s);
                  }
                  
                  tip = me.msg("tip." + status, meta);
                  desc += '<div class="status"><img src="' + Alfresco.constants.URL_CONTEXT + 'components/documentlibrary/images/' + status + '-indicator-16.png" title="' + tip + '" alt="' + status + '" /></div>';
               }

               elCell.innerHTML = desc;
            }
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
                  case "record-series":
                  case "record-category":
                  case "record-folder":
                     elCell.innerHTML = '<span class="folder-small">' + (isLink ? '<span class="link"></span>' : '') + '<a href="" onclick="' + generatePathOnClick(locn) + '"><img src="' + Alfresco.constants.URL_CONTEXT + 'components/documentlibrary/images/' + type + '-32.png" /></a>';
                     break;

                  case "non-electronic-record":
                     elCell.innerHTML = '<span class="folder-small">' + (isLink ? '<span class="link"></span>' : '') + '<img src="' + Alfresco.constants.URL_CONTEXT + 'components/documentlibrary/images/generic-file-32.png" />';
                     break;
                  
                  case "folder":
                     elCell.innerHTML = '<span class="folder-small">' + (isLink ? '<span class="link"></span>' : '') + '<a href="" onclick="' + generatePathOnClick(locn) + '"><img src="' + Alfresco.constants.URL_CONTEXT + 'components/documentlibrary/images/folder-32.png" /></a>';
                     break;

                  default:
                     var id = me.id + '-preview-' + oRecord.getId();
                     docDetailsUrl = Alfresco.constants.URL_PAGECONTEXT + "site/" + me.options.siteId + "/document-details?nodeRef=" + oRecord.getData("nodeRef");
                     elCell.innerHTML = '<span id="' + id + '" class="icon32">' + (isLink ? '<span class="link"></span>' : '') + '<a href="' + docDetailsUrl + '"><img src="' + Alfresco.constants.URL_CONTEXT + 'components/images/filetypes/' + Alfresco.util.getFileIcon(name) + '" alt="' + extn + '" title="' + $html(title) + '" /></a></span>';
                  
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
                  case "record-series":
                  case "record-category":
                  case "record-folder":
                     elCell.innerHTML = '<span class="folder">' + (isLink ? '<span class="link"></span>' : '') + '<a href="" onclick="' + generatePathOnClick(locn) + '"><img src="' + Alfresco.constants.URL_CONTEXT + 'components/documentlibrary/images/' + type + '-48.png" /></a>';
                     break;

                  case "non-electronic-record":
                     elCell.innerHTML = '<span class="folder">' + (isLink ? '<span class="link"></span>' : '') + '<img src="' + Alfresco.constants.URL_CONTEXT + 'components/documentlibrary/images/nonElectronicRecord.png" />';
                     break;

                  case "folder":
                     elCell.innerHTML = '<span class="folder">' + (isLink ? '<span class="link"></span>' : '') + '<a href="" onclick="' + generatePathOnClick(locn) + '"><img src="' + Alfresco.constants.URL_CONTEXT + 'components/documentlibrary/images/folder-48.png" /></a>';
                     break;

                  default:
                     docDetailsUrl = Alfresco.constants.URL_PAGECONTEXT + "site/" + me.options.siteId + "/document-details?nodeRef=" + oRecord.getData("nodeRef");
                     elCell.innerHTML = '<span class="thumbnail">' + (isLink ? '<span class="link"></span>' : '') + '<a href="' + docDetailsUrl + '"><img src="' + generateThumbnailUrl(oRecord) + '" alt="' + extn + '" title="' + $html(title) + '" /></a></span>';
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
               locn = oRecord.getData("location"),
               dod5015 = oRecord.getData("dod5015");
            
            // Link handling
            if (isLink)
            {
               oRecord.setData("displayName", me.msg("details.link-to", oRecord.getData("displayName")));
            }
            
            // Identifier
            var rmaIdentifier = $html(dod5015["rma:identifier"]);
            if (rmaIdentifier == "")
            {
               rmaIdentifier = me.msg("details.description.none");
            }

            switch (type)
            {
               /**
                * Record Series
                */
               case "record-series":
                  desc = '<h3 class="filename"><a href="" onclick="' + generatePathOnClick(locn) + '">';
                  desc += $html(oRecord.getData("displayName")) + '</a></h3>';

                  if (me.options.simpleView)
                  {
                     /**
                      * Simple View
                      */
                     desc += '<div class="detail"><span class="item-simple"><em>' + me.msg("details.series.identifier") + '</em> ' + rmaIdentifier + '</span></div>';
                  }
                  else
                  {
                     /**
                      * Detailed View
                      */
                     desc += '<div class="detail"><span class="item"><em>' + me.msg("details.series.identifier") + '</em> ' + rmaIdentifier + '</span></div>';
                     description = oRecord.getData("description");
                     if (description === "")
                     {
                        description = me.msg("details.description.none");
                     }
                     desc += '<div class="detail"><span class="item"><em>' + me.msg("details.description") + '</em> ' + $links($html(description)) + '</span></div>';
                  }
                  break;
               
               /**
                * Record Category
                */
               case "record-category":
                  desc = '<h3 class="filename"><a href="" onclick="' + generatePathOnClick(locn) + '">';
                  desc += $html(oRecord.getData("displayName")) + '</a></h3>';

                  if (me.options.simpleView)
                  {
                     /**
                      * Simple View
                      */
                     desc += '<div class="detail"><span class="item-simple"><em>' + me.msg("details.category.identifier") + '</em> ' + rmaIdentifier + '</span></div>';
                  }
                  else
                  {
                     /**
                      * Detailed View
                      */
                     desc += '<div class="detail"><span class="item"><em>' + me.msg("details.category.identifier") + '</em> ' + rmaIdentifier + '</span></div>';
                     // Disposition Details
                     desc += '<div class="detail"><span class="item"><em>' + me.msg("details.category.disposition-authority") + '</em> ' + $html(dod5015["rma:dispositionAuthority"]) + '</span></div>';
                     desc += '<div class="detail"><span class="item"><em>' + me.msg("details.category.disposition-instructions") + '</em> ' + $html(dod5015["rma:dispositionInstructions"]) + '</span></div>';
                     // Vital Record Indicator
                     desc += '<div class="detail"><span class="item"><em>' + me.msg("details.category.vital-record-indicator") + '</em> ' + me.msg(dod5015["rma:vitalRecordIndicator"] ? "label.yes" : "label.no") + '</span></div>';
                  }
                  break;
               
               /**
                * Record Folder
                */
               case "record-folder":
                  desc = '<h3 class="filename"><a href="" onclick="' + generatePathOnClick(locn) + '">';
                  desc += $html(oRecord.getData("displayName")) + '</a></h3>';

                  if (me.options.simpleView)
                  {
                     /**
                      * Simple View
                      */
                     desc += '<div class="detail"><span class="item-simple"><em>' + me.msg("details.folder.identifier") + '</em> ' + rmaIdentifier + '</span></div>';
                  }
                  else
                  {
                     /**
                      * Detailed View
                      */
                     desc += '<div class="detail"><span class="item"><em>' + me.msg("details.folder.identifier") + '</em> ' + rmaIdentifier + '</span></div>';
                     desc += '<div class="detail"><span class="item"><em>' + me.msg("details.folder.vital-record-indicator") + '</em> ' + me.msg(dod5015["rma:vitalRecordIndicator"] ? "label.yes" : "label.no") + '</span></div>';
                     desc += '<div class="detail"><span class="item"><em>' + me.msg("details.created.by") + '</em> <a href="' + generateUserProfileUrl(oRecord.getData("createdByUser")) + '">' + $html(oRecord.getData("createdBy")) + '</a></span></div>';
                     desc += '<div class="detail"><span class="item"><em>' + me.msg("details.modified.on") + '</em> ' + Alfresco.util.formatDate(oRecord.getData("modifiedOn")) + '</span></div>';
                     desc += '<div class="detail">&nbsp;</div>';
                  }
                  break;

               /**
                * Record
                */
               case "record":
                  docDetailsUrl = Alfresco.constants.URL_PAGECONTEXT + "site/" + me.options.siteId + "/document-details?nodeRef=" + oRecord.getData("nodeRef");

                  desc = '<h3 class="filename"><span id="' + me.id + '-preview-' + oRecord.getId() + '"><a href="' + docDetailsUrl + '">' + $html(oRecord.getData("displayName")) + '</a></span></h3>';
                  if (me.options.simpleView)
                  {
                     /**
                      * Simple View
                      */
                     desc += '<div class="detail"><span class="item-simple"><em>' + me.msg("details.record.identifier") + '</em> ' + rmaIdentifier + '</span></div>';
                  }
                  else
                  {
                     /**
                      * Detailed View
                      */
                     desc += '<div class="detail"><span class="item"><em>' + me.msg("details.record.identifier") + '</em> ' + rmaIdentifier + '</span></div>';
                     desc += '<div class="detail"><span class="item"><em>' + me.msg("details.record.date-filed") + '</em> ' + Alfresco.util.formatDate($jsonDate(dod5015["rma:dateFiled"])) + '</span>';
                     desc += '<span class="item"><em>' + me.msg("details.record.publication-date") + '</em> ' + Alfresco.util.formatDate($jsonDate(dod5015["rma:publicationDate"])) + '</span></div>';
                     desc += '<div class="detail">';
                     desc +=    '<span class="item"><em>' + me.msg("details.record.originator") + '</em> ' + $html(dod5015["rma:originator"]) + '</span>';
                     desc +=    '<span class="item"><em>' + me.msg("details.record.originating-organisation") + '</em> ' + $html(dod5015["rma:originatingOrganization"]) + '</span>';
                     desc += '</div>';
                  }
                  break;

               /**
                * Non-Electronic Record
                */
               case "non-electronic-record":
                  break;

               /**
                * Undeclared Record
                */
               case "undeclared-record":
                  docDetailsUrl = Alfresco.constants.URL_PAGECONTEXT + "site/" + me.options.siteId + "/document-details?nodeRef=" + oRecord.getData("nodeRef");

                  desc = '<div class="undeclared-record-info">' + me.msg("details.undeclared-record.info") + '</div>';
                  desc += '<h3 class="filename"><span id="' + me.id + '-preview-' + oRecord.getId() + '"><a href="' + docDetailsUrl + '">' + $html(oRecord.getData("displayName")) + '</a></span></h3>';
                  if (me.options.simpleView)
                  {
                     /**
                      * Simple View
                      */
                     desc += '<div class="detail"><span class="item-simple"><em>' + me.msg("details.modified.on") + '</em> ' + Alfresco.util.formatDate(oRecord.getData("modifiedOn"), "dd mmmm yyyy") + '</span>';
                     desc += '<span class="item-simple"><em>' + me.msg("details.by") + '</em> <a href="' + generateUserProfileUrl(oRecord.getData("modifiedByUser")) + '">' + $html(oRecord.getData("modifiedBy")) + '</a></span></div>';
                  }
                  else
                  {
                     /**
                      * Detailed View
                      */
                     desc += '<div class="detail"><span class="item"><em>' + me.msg("details.record.identifier") + '</em> ' + rmaIdentifier + '</span></div>';
                     desc += '<div class="detail">';
                     desc +=    '<span class="item"><em>' + me.msg("details.modified.on") + '</em> ' + Alfresco.util.formatDate(oRecord.getData("modifiedOn")) + '</span>';
                     desc +=    '<span class="item"><em>' + me.msg("details.modified.by") + '</em> <a href="' + generateUserProfileUrl(oRecord.getData("modifiedByUser")) + '">' + $html(oRecord.getData("modifiedBy")) + '</a></span>';
                     desc +=    '<span class="item"><em>' + me.msg("details.version") + '</em> ' + oRecord.getData("version") + '</span>';
                     desc +=    '<span class="item"><em>' + me.msg("details.size") + '</em> ' + Alfresco.util.formatFileSize(oRecord.getData("size")) + '</span>';
                     desc += '</div>';
                     description = oRecord.getData("description");
                     if (description === "")
                     {
                        description = me.msg("details.description.none");
                     }
                     desc += '<div class="detail"><span class="item"><em>' + me.msg("details.description") + '</em> ' + $links($html(description)) + '</span></div>';
                  }
                  break;


               /**
                * "Normal" Folder
                */
               case "folder":
                  desc = '<h3 class="filename"><a href="" onclick="' + generatePathOnClick(locn) + '">';
                  desc += $html(oRecord.getData("displayName")) + '</a></h3>';

                  if (me.options.simpleView)
                  {
                     /**
                      * Simple View
                      */
                     desc += '<div class="detail"><span class="item-simple"><em>' + me.msg("details.modified.on") + '</em> ' + Alfresco.util.formatDate(oRecord.getData("modifiedOn"), "dd mmmm yyyy") + '</span>';
                     desc += '<span class="item-simple"><em>' + me.msg("details.by") + '</em> <a href="' + generateUserProfileUrl(oRecord.getData("modifiedByUser")) + '">' + $html(oRecord.getData("modifiedBy")) + '</a></span></div>';
                  }
                  else
                  {
                     /**
                      * Detailed View
                      */
                     desc += '<div class="detail"><span class="item"><em>' + me.msg("details.modified.on") + '</em> ' + Alfresco.util.formatDate(oRecord.getData("modifiedOn")) + '</span>';
                     desc += '<span class="item"><em>' + me.msg("details.modified.by") + '</em> <a href="' + generateUserProfileUrl(oRecord.getData("modifiedByUser")) + '">' + $html(oRecord.getData("modifiedBy")) + '</a></span></div>';
                     description = oRecord.getData("description");
                     if (description === "")
                     {
                        description = me.msg("details.description.none");
                     }
                     desc += '<div class="detail"><span class="item"><em>' + me.msg("details.description") + '</em> ' + $links($html(description)) + '</span></div>';
                     desc += '</div><div class="detail">&nbsp;</div>';
                  }
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
                     desc += '<div class="detail"><span class="item-simple"><em>' + me.msg("details.modified.on") + '</em> ' + Alfresco.util.formatDate(oRecord.getData("modifiedOn"), "dd mmmm yyyy") + '</span>';
                     desc += '<span class="item-simple"><em>' + me.msg("details.by") + '</em> <a href="' + generateUserProfileUrl(oRecord.getData("modifiedByUser")) + '">' + $html(oRecord.getData("modifiedBy")) + '</a></span></div>';
                  }
                  else
                  {
                     /**
                      * Detailed View
                      */
                     desc += '<div class="detail">';
                     desc += '<span class="item"><em>' + me.msg("details.modified.on") + '</em> ' + Alfresco.util.formatDate(oRecord.getData("modifiedOn")) + '</span>';
                     desc += '<span class="item"><em>' + me.msg("details.modified.by") + '</em> <a href="' + generateUserProfileUrl(oRecord.getData("modifiedByUser")) + '">' + $html(oRecord.getData("modifiedBy")) + '</a></span>';
                     desc += '<span class="item"><em>' + me.msg("details.version") + '</em> ' + oRecord.getData("version") + '</span>';
                     desc += '<span class="item"><em>' + me.msg("details.size") + '</em> ' + Alfresco.util.formatFileSize(oRecord.getData("size")) + '</span>';
                     desc += '</div><div class="detail">';
                     description = oRecord.getData("description");
                     if (description === "")
                     {
                        description = me.msg("details.description.none");
                     }
                     desc += '<span class="item"><em>' + me.msg("details.description") + '</em> ' + $links($html(description)) + '</span>';
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
            Dom.addClass(elCell.parentNode, oRecord.getData("type"));

            elCell.innerHTML = '<div id="' + me.id + '-actions-' + oRecord.getId() + '" class="hidden"></div>';
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
            Alfresco.logger.error("Alfresco.RecordsDocumentList: Couldn't initialize HistoryManager.", e);
            this.onHistoryManagerReady();
         }
         
      },


      /**
       * Public functions
       *
       * Functions designed to be called form external sources
       */

      /**
       * Public function to select files by specified groups
       *
       * @method selectFiles
       * @param p_selectType {string} Can be one of the following:
       * <pre>
       * selectAll - all documents and folders
       * selectNone - deselect all
       * selectInvert - invert selection
       * selectRecords - select all records
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
            selectRecords: "record",
            selectUndeclaredRecords: "undeclared-record",
            selectFolders:
            {
               "record-folder": true,
               "record-category": true,
               "record-series": true
            }
         };

         switch (p_selectType)
         {
            case "selectAll":
               fnCheck = function(assetType, isChecked)
               {
                  return true;
               }
               break;
            
            case "selectNone":
               fnCheck = function(assetType, isChecked)
               {
                  return false;
               }
               break;

            case "selectInvert":
               fnCheck = function(assetType, isChecked)
               {
                  return !isChecked;
               }
               break;

            case "selectRecords":
            case "selectUndeclaredRecords":
            case "selectFolders":
               typeMap = typeMapping[p_selectType];
               fnCheck = function(assetType, isChecked)
               {
                  if (typeof typeMap === "object")
                  {
                     return typeMap[assetType];
                  }
                  return assetType == typeMap;
               }
               break;
            
            default:
               fnCheck = function(assetType, isChecked)
               {
                  return isChecked;
               }
         }

         for (i = 0; i < len; i++)
         {
            record = recordSet.getRecord(i);
            this.selectedFiles[record.getData("nodeRef")] = checks[i].checked = fnCheck(record.getData("type"), checks[i].checked);
         }
         
         YAHOO.Bubbling.fire("selectedFilesChanged");
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
         return (
         {
            downloadUrl: Alfresco.constants.PROXY_URI + record.getData("contentUrl") + "?a=true",
            documentDetailsUrl: Alfresco.constants.URL_PAGECONTEXT + "site/" + this.options.siteId + "/document-details?nodeRef=" + record.getData("nodeRef"),
            folderDetailsUrl: Alfresco.constants.URL_PAGECONTEXT + "site/" + this.options.siteId + "/folder-details?nodeRef=" + record.getData("nodeRef"),
            recordFolderDetailsUrl: Alfresco.constants.URL_PAGECONTEXT + "site/" + this.options.siteId + "/record-folder-details?nodeRef=" + record.getData("nodeRef"),
            recordCategoryDetailsUrl: Alfresco.constants.URL_PAGECONTEXT + "site/" + this.options.siteId + "/record-category-details?nodeRef=" + record.getData("nodeRef"),
            recordSeriesDetailsUrl: Alfresco.constants.URL_PAGECONTEXT + "site/" + this.options.siteId + "/record-series-details?nodeRef=" + record.getData("nodeRef"),
            editMetadataUrl: Alfresco.constants.URL_PAGECONTEXT + "site/" + this.options.siteId + "/edit-metadata?nodeRef=" + record.getData("nodeRef")
         });

      },

      /**
       * BUBBLING LIBRARY EVENT HANDLERS FOR ACTIONS
       * Disconnected event handlers for action event notification
       */

      /**
       * Copy single document or folder.
       *
       * @method onActionCopyTo
       * @param row {object} DataTable row representing file to be actioned
       */
      onActionCopyTo: function DL_onActionCopyTo(row)
      {
         this._copyMoveFileTo("copy", row)
      },

      /**
       * File single document or folder.
       *
       * @method onActionFileTo
       * @param row {object} DataTable row representing file to be actioned
       */
      onActionFileTo: function DL_onActionFileTo(row)
      {
         this._copyMoveFileTo("file", row)
      },

      /**
       * Move single document or folder.
       *
       * @method onActionMoveTo
       * @param row {object} DataTable row representing file to be actioned
       */
      onActionMoveTo: function DL_onActionMoveTo(row)
      {
         this._copyMoveFileTo("move", row)
      },
      
      /**
       * Copy/Move/File To implementation.
       *
       * @method _copyMoveFileTo
       * @param mode {String} Operation mode: copy|file|move
       * @param row {object} DataTable row representing file to be actioned
       * @private
       */
      _copyMoveFileTo: function DL__copyMoveFileTo(mode, row)
      {
         var file = this.widgets.dataTable.getRecord(row).getData();
         
         if (!this.modules.copyMoveFileTo)
         {
            this.modules.copyMoveFileTo = new Alfresco.module.RecordsCopyMoveFileTo(this.id + "-copyMoveFileTo");
         }

         this.modules.copyMoveFileTo.setOptions(
         {
            mode: mode,
            siteId: this.options.siteId,
            containerId: this.options.containerId,
            path: this.currentPath,
            files: file
         });

         this.modules.copyMoveFileTo.showDialog();
      },


      /**
       * Freeze action.
       * NOTE: Placeholder action which needs to pop-up a dialog to collect metadata from the user
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
       * Close Record Folder action.
       *
       * @method onActionClose
       * @param row {object} DataTable row representing file to be actioned
       */
      onActionClose: function DL_onActionClose(row)
      {
         this._dod5015Action(row, "closeRecordFolder", "message.close");
      },

      /**
       * Cut Off action.
       *
       * @method onActionCutoff
       * @param row {object} DataTable row representing file to be actioned
       */
      onActionCutoff: function DL_onActionCutoff(row)
      {
         this._dod5015Action(row, "cutoff", "message.cutoff");
      },

      /**
       * Declare Record action.
       * Special case handling due to the ability to jump to the Edit Metadata page if the action failed.
       *
       * @method onActionDeclare
       * @param row {object} DataTable row representing file to be actioned
       */
      onActionDeclare: function DL_onActionDeclare(row)
      {
         var record = this.widgets.dataTable.getRecord(row),
            displayName = record.getData("displayName"),
            editMetadataUrl = Alfresco.constants.URL_PAGECONTEXT + "site/" + this.options.siteId + "/edit-metadata?nodeRef=" + record.getData("nodeRef");

         this._dod5015Action(row, "declareRecord", "message.declare",
         {
            failure:
            {
               message: null,
               callback:
               {
                  fn: function DL_oAD_failure(data)
                  {
                     Alfresco.util.PopupManager.displayPrompt(
                     {
                        title: this.msg("message.declare.failure", displayName),
                        text: this.msg("message.declare.failure.more"),
                        buttons: [
                        {
                           text: this.msg("actions.edit-details"),
                           handler: function DL_oAD_f_editDetails()
                           {
                              window.location = editMetadataUrl;
                              this.destroy();
                           },
                           isDefault: true
                        },
                        {
                           text: this.msg("button.cancel"),
                           handler: function DL_oAD_f_cancel()
                           {
                              this.destroy();
                           }
                        }]
                     });
                  },
                  scope: this
               }
            }
         });
      },

      /**
       * Destroy action.
       *
       * @method onActionDestroy
       * @param row {object} DataTable row representing file to be actioned
       */
      onActionDestroy: function DL_onActionDestroy(row)
      {
         this._dod5015Action(row, "destroy", "message.destroy");
      },

      /**
       * Re-open Record Folder action.
       *
       * @method onActionReopen
       * @param row {object} DataTable row representing file to be actioned
       */
      onActionReopen: function DL_onActionReopen(row)
      {
         this._dod5015Action(row, "openRecordFolder", "message.open");
      },

      /**
       * Reviewed action.
       *
       * @method onActionReviewed
       * @param row {object} DataTable row representing file to be actioned
       */
      onActionReviewed: function DL_onActionReviewed(row)
      {
         this._dod5015Action(row, "reviewed", "message.review");
      },

      /**
       * Undeclare record.
       *
       * @method onActionUndeclare
       * @param row {object} DataTable row representing file to be actioned
       */
      onActionUndeclare: function DL_onActionUndeclare(row)
      {
         this._dod5015Action(row, "undeclareRecord", "message.undeclare");
      },


      /**
       * DOD5015 action.
       *
       * @method _dod5015Action
       * @param row {object} DataTable row representing file to be actioned
       * @param actionName {string} Name of repository action to run
       * @param i18n {string} Will be appended with ".success" or ".failure" depending on action outcome
       * @param customParams {object} Optional object literal to override default action parameters
       * @private
       */
      _dod5015Action: function DL__dod5015Action(row, actionName, i18n, customParams)
      {
         var record = this.widgets.dataTable.getRecord(row),
            displayName = record.getData("displayName"),
            nodeRef = record.getData("nodeRef");
         
         var actionParams =
         {
            success:
            {
               event:
               {
                  name: "metadataRefresh"
               },
               message: this.msg(i18n + ".success", displayName)
            },
            failure:
            {
               message: this.msg(i18n + ".failure", displayName)
            },
            webscript:
            {
               method: Alfresco.util.Ajax.POST,
               stem: Alfresco.constants.PROXY_URI + "api/rma/actions/",
               name: "ExecutionQueue"
            },
            config:
            {
               requestContentType: Alfresco.util.Ajax.JSON,
               dataObj:
               {
                  name: actionName,
                  nodeRef: nodeRef
               }
            }
         };
         
         if (YAHOO.lang.isObject(customParams))
         {
            actionParams = YAHOO.lang.merge(actionParams, customParams);
         }

         this.modules.actions.genericAction(actionParams);
      }
   });
})();
