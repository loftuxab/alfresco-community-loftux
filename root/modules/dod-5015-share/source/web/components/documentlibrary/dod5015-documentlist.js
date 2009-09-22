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
 * @superclass Alfresco.DocumentList
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
      $jsonDate = Alfresco.util.fromExplodedJSONDate;

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
   
   /**
    * Extend Alfresco.DocumentList
    */
   YAHOO.extend(Alfresco.RecordsDocumentList, Alfresco.DocumentList);

   /**
    * Augment prototype with RecordsActions module, ensuring overwrite is enabled
    */
   YAHOO.lang.augmentProto(Alfresco.RecordsDocumentList, Alfresco.doclib.RecordsActions, true);
   
   /**
    * Augment prototype with main class implementation, ensuring overwrite is enabled
    */
   YAHOO.lang.augmentObject(Alfresco.RecordsDocumentList.prototype,
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

         // Set-up YUI History Managers
         this._setupHistoryManagers();

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
               "index", "nodeRef", "type", "isFolder", "mimetype", "fileName", "displayName", "status", "title", "description", "author",
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

            // Fire event with parent metadata
            YAHOO.Bubbling.fire("doclistMetadata",
            {
               metadata: me.doclistMetadata
            });
            
            // Container userAccess event
            var permissions = me.doclistMetadata.parent.permissions;
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

            var record = oRecord.getData(),
               dataStatus = record.status;
            
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

               switch (type)
               {
                  case "record-series":
                  case "record-category":
                  case "record-folder":
                  case "transfer-container":
                  case "hold-container":
                     elCell.innerHTML = '<span class="folder-small">' + (isLink ? '<span class="link"></span>' : '') + '<a href="#" class="filter-change" rel="' + Alfresco.DocumentList.generatePathMarkup(locn) + '"><img src="' + Alfresco.constants.URL_CONTEXT + 'components/documentlibrary/images/' + type + '-32.png" /></a>';
                     break;

                  case "record-nonelec":
                  case "undeclared-record-nonelec":
                     elCell.innerHTML = '<span class="folder-small">' + (isLink ? '<span class="link"></span>' : '') + '<img src="' + Alfresco.constants.URL_CONTEXT + 'components/documentlibrary/images/non-electronic-32.png" />';
                     break;

                  case "metadata-stub":
                     var id = me.id + '-preview-' + oRecord.getId();
                     docDetailsUrl = Alfresco.constants.URL_PAGECONTEXT + "site/" + me.options.siteId + "/document-details?nodeRef=" + record.nodeRef;
                     elCell.innerHTML = '<span id="' + id + '" class="icon32">' + (isLink ? '<span class="link"></span>' : '') + '<a href="' + docDetailsUrl + '"><img src="' + Alfresco.constants.URL_CONTEXT + 'components/documentlibrary/images/meta-stub-32.png" /></a></span>';
                     break;

                  case "folder":
                     elCell.innerHTML = '<span class="folder-small">' + (isLink ? '<span class="link"></span>' : '') + '<a href="#" class="filter-change" rel="' + Alfresco.DocumentList.generatePathMarkup(locn) + '"><img src="' + Alfresco.constants.URL_CONTEXT + 'components/documentlibrary/images/folder-32.png" /></a>';
                     break;

                  default:
                     var id = me.id + '-preview-' + oRecord.getId();
                     docDetailsUrl = Alfresco.constants.URL_PAGECONTEXT + "site/" + me.options.siteId + "/document-details?nodeRef=" + record.nodeRef;
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
                  case "transfer-container":
                  case "hold-container":
                     elCell.innerHTML = '<span class="folder">' + (isLink ? '<span class="link"></span>' : '') + '<a href="#" class="filter-change" rel="' + Alfresco.DocumentList.generatePathMarkup(locn) + '"><img src="' + Alfresco.constants.URL_CONTEXT + 'components/documentlibrary/images/' + type + '-48.png" /></a>';
                     break;

                  case "record-nonelec":
                  case "undeclared-record-nonelec":
                     elCell.innerHTML = '<span class="folder">' + (isLink ? '<span class="link"></span>' : '') + '<img src="' + Alfresco.constants.URL_CONTEXT + 'components/documentlibrary/images/non-electronic-75x100.png" />';
                     break;

                  case "metadata-stub":
                     docDetailsUrl = Alfresco.constants.URL_PAGECONTEXT + "site/" + me.options.siteId + "/document-details?nodeRef=" + record.nodeRef;
                     elCell.innerHTML = '<span class="thumbnail">' + (isLink ? '<span class="link"></span>' : '') + '<a href="' + docDetailsUrl + '"><img src="' + Alfresco.constants.URL_CONTEXT + 'components/documentlibrary/images/meta-stub-75x100.png" /></a></span>';
                     break;

                  case "folder":
                     elCell.innerHTML = '<span class="folder">' + (isLink ? '<span class="link"></span>' : '') + '<a href="#" class="filter-change" rel="' + Alfresco.DocumentList.generatePathMarkup(locn) + '"><img src="' + Alfresco.constants.URL_CONTEXT + 'components/documentlibrary/images/folder-48.png" /></a>';
                     break;

                  default:
                     docDetailsUrl = Alfresco.constants.URL_PAGECONTEXT + "site/" + me.options.siteId + "/document-details?nodeRef=" + record.nodeRef;
                     elCell.innerHTML = '<span class="thumbnail">' + (isLink ? '<span class="link"></span>' : '') + '<a href="' + docDetailsUrl + '"><img src="' + Alfresco.DocumentList.generateThumbnailUrl(oRecord) + '" alt="' + extn + '" title="' + $html(title) + '" /></a></span>';
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
            var record = oRecord.getData(),
               type = record.type,
               nodeRef = record.nodeRef,
               isLink = record.isLink,
               locn = record.location,
               dod5015 = record.dod5015,
               fileName = record.fileName;
            
            // Use title property if it's available
            record.displayName = record.title || record.displayName;
            
            // Link handling
            if (isLink)
            {
               record.displayName = me.msg("details.link-to", record.displayName);
            }
            
            // Identifier
            var rmaIdentifier = $html(dod5015["rma:identifier"]);
            if (rmaIdentifier === "")
            {
               rmaIdentifier = me.msg("details.description.none");
            }

            switch (type)
            {
               /**
                * Record Series
                */
               case "record-series":
                  desc = '<h3 class="filename"><a href="#" class="filter-change" rel="' + Alfresco.DocumentList.generatePathMarkup(locn) + '" title="' + $html(fileName) + '">';
                  desc += $html(record.displayName) + '</a>';
                  if (record.displayName !== fileName)
                  {
                     desc += '<span class="filename">(' + $html(fileName) + ')</span>';
                  }
                  desc += '</h3>';

                  if (me.options.simpleView)
                  {
                     /**
                      * Simple View
                      */
                     desc += '<div class="detail"><span class="item-simple"><em>' + me.msg("details.series.identifier") + '</em> ' + rmaIdentifier + '</span></div>';
                     // Created by, Modified on
                     desc += '<div class="detail"><span class="item-simple"><em>' + me.msg("details.created.by") + '</em> <a href="' + Alfresco.DocumentList.generateUserProfileUrl(record.createdByUser) + '">' + $html(record.createdBy) + '</a></span>';
                     desc += '<span class="item-simple"><em>' + me.msg("details.modified.on") + '</em> ' + Alfresco.util.formatDate(record.modifiedOn) + '</span></div>';
                  }
                  else
                  {
                     /**
                      * Detailed View
                      */
                     desc += '<div class="detail detail-first"><span class="item"><em>' + me.msg("details.series.identifier") + '</em> ' + rmaIdentifier + '</span></div>';
                     description = record.description;
                     if (description === "")
                     {
                        description = me.msg("details.description.none");
                     }
                     desc += '<div class="detail"><span class="item"><em>' + me.msg("details.description") + '</em> ' + $links($html(description)) + '</span></div>';
                     // Created by, Modified on
                     desc += '<div class="detail detail-last"><span class="item"><em>' + me.msg("details.created.by") + '</em> <a href="' + Alfresco.DocumentList.generateUserProfileUrl(record.createdByUser) + '">' + $html(record.createdBy) + '</a></span>';
                     desc += '<span class="item"><em>' + me.msg("details.modified.on") + '</em> ' + Alfresco.util.formatDate(record.modifiedOn) + '</span></div>';
                  }
                  break;
               
               /**
                * Record Category
                */
               case "record-category":
                  desc = '<h3 class="filename"><a href="#" class="filter-change" rel="' + Alfresco.DocumentList.generatePathMarkup(locn) + '" title="' + $html(fileName) + '">';
                  desc += $html(record.displayName) + '</a>';
                  if (record.displayName !== fileName)
                  {
                     desc += '<span class="filename">(' + $html(fileName) + ')</span>';
                  }
                  desc += '</h3>';

                  if (me.options.simpleView)
                  {
                     /**
                      * Simple View
                      */
                     desc += '<div class="detail"><span class="item-simple"><em>' + me.msg("details.category.identifier") + '</em> ' + rmaIdentifier + '</span></div>';
                     // Created by, Modified on
                     desc += '<div class="detail"><span class="item-simple"><em>' + me.msg("details.created.by") + '</em> <a href="' + Alfresco.DocumentList.generateUserProfileUrl(record.createdByUser) + '">' + $html(record.createdBy) + '</a></span>';
                     desc += '<span class="item-simple"><em>' + me.msg("details.modified.on") + '</em> ' + Alfresco.util.formatDate(record.modifiedOn) + '</span></div>';
                  }
                  else
                  {
                     /**
                      * Detailed View
                      */
                     desc += '<div class="detail detail-first"><span class="item"><em>' + me.msg("details.category.identifier") + '</em> ' + rmaIdentifier + '</span></div>';
                     // Disposition Details
                     desc += '<div class="detail"><span class="item"><em>' + me.msg("details.category.disposition-authority") + '</em> ' + $html(dod5015["rma:dispositionAuthority"]) + '</span></div>';
                     desc += '<div class="detail"><span class="item"><em>' + me.msg("details.category.disposition-instructions") + '</em> ' + $html(dod5015["rma:dispositionInstructions"]) + '</span></div>';
                     // Vital Record Indicator
                     desc += '<div class="detail"><span class="item"><em>' + me.msg("details.category.vital-record-indicator") + '</em> ' + me.msg(dod5015["rma:vitalRecordIndicator"] ? "label.yes" : "label.no") + '</span></div>';
                     // Created by, Modified on
                     desc += '<div class="detail detail-last"><span class="item"><em>' + me.msg("details.created.by") + '</em> <a href="' + Alfresco.DocumentList.generateUserProfileUrl(record.createdByUser) + '">' + $html(record.createdBy) + '</a></span>';
                     desc += '<span class="item"><em>' + me.msg("details.modified.on") + '</em> ' + Alfresco.util.formatDate(record.modifiedOn) + '</span></div>';
                  }
                  break;
               
               /**
                * Record Folder
                */
               case "record-folder":
                  desc = '<h3 class="filename"><a href="#" class="filter-change" rel="' + Alfresco.DocumentList.generatePathMarkup(locn) + '" title="' + $html(fileName) + '">' + $html(record.displayName) + '</a>';
                  if (record.displayName !== fileName)
                  {
                     desc += '<span class="filename">(' + $html(fileName) + ')</span>';
                  }
                  desc += '</h3>';

                  if (me.options.simpleView)
                  {
                     /**
                      * Simple View
                      */
                     desc += '<div class="detail"><span class="item-simple"><em>' + me.msg("details.folder.identifier") + '</em> ' + rmaIdentifier + '</span></div>';
                     // Created by, Modified on
                     desc += '<div class="detail"><span class="item-simple"><em>' + me.msg("details.created.by") + '</em> <a href="' + Alfresco.DocumentList.generateUserProfileUrl(record.createdByUser) + '">' + $html(record.createdBy) + '</a></span>';
                     desc += '<span class="item-simple"><em>' + me.msg("details.modified.on") + '</em> ' + Alfresco.util.formatDate(record.modifiedOn) + '</span></div>';
                  }
                  else
                  {
                     /**
                      * Detailed View
                      */
                     desc += '<div class="detail detail-first"><span class="item"><em>' + me.msg("details.folder.identifier") + '</em> ' + rmaIdentifier + '</span></div>';
                     desc += '<div class="detail"><span class="item"><em>' + me.msg("details.folder.vital-record-indicator") + '</em> ' + me.msg(dod5015["rma:vitalRecordIndicator"] ? "label.yes" : "label.no") + '</span></div>';
                     // Created by, Modified on
                     desc += '<div class="detail detail-last"><span class="item"><em>' + me.msg("details.created.by") + '</em> <a href="' + Alfresco.DocumentList.generateUserProfileUrl(record.createdByUser) + '">' + $html(record.createdBy) + '</a></span>';
                     desc += '<span class="item"><em>' + me.msg("details.modified.on") + '</em> ' + Alfresco.util.formatDate(record.modifiedOn) + '</span></div>';
                  }
                  break;

               /**
                * Transfer Container
                */
               case "transfer-container":
                  var transferTitle = me.msg("details.transfer-container.title", record.displayName);
               
                  var filterObj =
                  {
                     filterId: "transfers",
                     filterData: nodeRef,
                     filterDisplay: transferTitle
                  };

                  desc = '<h3 class="filename"><a class="filter-change" href="#" rel="' + Alfresco.DocumentList.generateFilterMarkup(filterObj) + '">' + $html(transferTitle) + '</a></h3>';

                  if (me.options.simpleView)
                  {
                     /**
                      * Simple View
                      */
                     desc += '<div class="detail"><span class="item-simple"><em>' + me.msg("details.created.on") + '</em> ' + Alfresco.util.formatDate(record.createdOn) + '</span>';
                     desc += '<span class="item-simple"><em>' + me.msg("details.by") + '</em> <a href="' + Alfresco.DocumentList.generateUserProfileUrl(record.modifiedByUser) + '">' + $html(record.modifiedBy) + '</a></span></div>';
                  }
                  else
                  {
                     /**
                      * Detailed View
                      */
                     desc += '<div class="detail detail-first"><span class="item"><em>' + me.msg("details.created.on") + '</em> ' + Alfresco.util.formatDate(record.createdOn) + '</span>';
                     desc += '<span class="item"><em>' + me.msg("details.by") + '</em> <a href="' + Alfresco.DocumentList.generateUserProfileUrl(record.modifiedByUser) + '">' + $html(record.modifiedBy) + '</a></span></div>';
                     if (dod5015["rma:transferAccessionIndicator"])
                     {
                        desc += '<div class="detail">' + me.msg("details.transfer-container.is-accession") + '</div';
                     }
                     else
                     {
                        desc += '<div class="detail">&nbsp;</div>';
                     }
                     desc += '<div class="detail detail-last">&nbsp;</div>';
                  }
                  break;

               /**
                * Hold Container
                */
               case "hold-container":
                  var holdDate = record.createdOn,
                     holdReason = dod5015["rma:holdReason"] || me.msg("details.hold-container.reason.none"),
                     holdTitle = me.msg("details.hold-container.title", Alfresco.util.formatDate(holdDate, "dd mmmm yyyy"), Alfresco.util.formatDate(holdDate, "HH:MM:ss"));

                  var filterObj =
                  {
                     filterId: "holds",
                     filterData: nodeRef,
                     filterDisplay: holdTitle
                  };
                  
                  desc = '<h3 class="filename"><a class="filter-change" href="#" rel="' + Alfresco.DocumentList.generateFilterMarkup(filterObj) + '">' + $html(holdTitle) + '</a></h3>';

                  if (me.options.simpleView)
                  {
                     /**
                      * Simple View
                      */
                     desc += '<div class="detail"><span class="item-simple"><em>' + me.msg("details.hold-container.reason") + '</em> ' + holdReason + '</span></div>';
                  }
                  else
                  {
                     /**
                      * Detailed View
                      */
                     desc += '<div class="detail detail-first"><span class="item"><em>' + me.msg("details.hold-container.reason") + '</em> ' + holdReason + '</span></div>';
                     desc += '<div class="detail">&nbsp;</div>';
                     desc += '<div class="detail detail-last">&nbsp;</div>';
                  }
                  break;

               /**
                * Record
                */
               case "record":
               case "record-nonelec":
                  docDetailsUrl = Alfresco.constants.URL_PAGECONTEXT + "site/" + me.options.siteId + "/document-details?nodeRef=" + record.nodeRef;

                  desc = '<h3 class="filename"><span id="' + me.id + '-preview-' + oRecord.getId() + '"><a href="' + docDetailsUrl + '" title="' + $html(fileName) + '">' + $html(record.displayName) + '</a></span>';
                  if (record.displayName !== fileName)
                  {
                     desc += '<span class="filename">(' + $html(fileName) + ')</span>';
                  }
                  desc += '</h3>';
                  
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
                     desc += '<div class="detail detail-first"><span class="item"><em>' + me.msg("details.record.identifier") + '</em> ' + rmaIdentifier + '</span></div>';
                     desc += '<div class="detail"><span class="item"><em>' + me.msg("details.record.date-filed") + '</em> ' + Alfresco.util.formatDate($jsonDate(dod5015["rma:dateFiled"])) + '</span>';
                     desc += '<span class="item"><em>' + me.msg("details.record.publication-date") + '</em> ' + Alfresco.util.formatDate($jsonDate(dod5015["rma:publicationDate"]), "defaultDateOnly") + '</span></div>';
                     desc += '<div class="detail detail-last">';
                     desc +=    '<span class="item"><em>' + me.msg("details.record.originator") + '</em> ' + $html(dod5015["rma:originator"]) + '</span>';
                     desc +=    '<span class="item"><em>' + me.msg("details.record.originating-organization") + '</em> ' + $html(dod5015["rma:originatingOrganization"]) + '</span>';
                     desc += '</div>';
                  }
                  break;

               /**
                * Metadata Stub
                */
               case "metadata-stub":
                  docDetailsUrl = Alfresco.constants.URL_PAGECONTEXT + "site/" + me.options.siteId + "/document-details?nodeRef=" + record.nodeRef;

                  desc += '<h3 class="filename"><a href="' + docDetailsUrl + '" title="' + $html(fileName) + '">' + $html(record.displayName) + '</a>';
                  if (record.displayName !== fileName)
                  {
                     desc += '<span class="filename">(' + $html(fileName) + ')</span>';
                  }
                  desc += '</h3>';
                  
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
                     desc += '<div class="detail detail-first"><span class="item"><em>' + me.msg("details.record.identifier") + '</em> ' + rmaIdentifier + '</span></div>';
                     desc += '<div class="detail"><span class="item"><em>' + me.msg("details.record.date-filed") + '</em> ' + Alfresco.util.formatDate($jsonDate(dod5015["rma:dateFiled"])) + '</span>';
                     desc += '<span class="item"><em>' + me.msg("details.record.publication-date") + '</em> ' + Alfresco.util.formatDate($jsonDate(dod5015["rma:publicationDate"]), "defaultDateOnly") + '</span></div>';
                     desc += '<div class="detail detail-last">';
                     desc +=    '<span class="item"><em>' + me.msg("details.record.originator") + '</em> ' + $html(dod5015["rma:originator"]) + '</span>';
                     desc +=    '<span class="item"><em>' + me.msg("details.record.originating-organization") + '</em> ' + $html(dod5015["rma:originatingOrganization"]) + '</span>';
                     desc += '</div>';
                  }
                  break;

               /**
                * Undeclared Record
                */
               case "undeclared-record":
               case "undeclared-record-nonelec":
                  docDetailsUrl = Alfresco.constants.URL_PAGECONTEXT + "site/" + me.options.siteId + "/document-details?nodeRef=" + record.nodeRef;

                  desc = '<div class="undeclared-record-info">' + me.msg("details.undeclared-record.info") + '</div>';
                  desc += '<h3 class="filename"><span id="' + me.id + '-preview-' + oRecord.getId() + '"><a href="' + docDetailsUrl + '" title="' + $html(fileName) + '">' + $html(record.displayName) + '</a></span>';
                  if (record.displayName !== fileName)
                  {
                     desc += '<span class="filename">(' + $html(fileName) + ')</span>';
                  }
                  desc += '</h3>';
                  
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
                     desc += '<div class="detail detail-first"><span class="item"><em>' + me.msg("details.record.identifier") + '</em> ' + rmaIdentifier + '</span></div>';
                     desc += '<div class="detail">';
                     desc +=    '<span class="item"><em>' + me.msg("details.modified.on") + '</em> ' + Alfresco.util.formatDate(record.modifiedOn) + '</span>';
                     desc +=    '<span class="item"><em>' + me.msg("details.modified.by") + '</em> <a href="' + Alfresco.DocumentList.generateUserProfileUrl(record.modifiedByUser) + '">' + $html(record.modifiedBy) + '</a></span>';
                     desc +=    '<span class="item"><em>' + me.msg("details.size") + '</em> ' + Alfresco.util.formatFileSize(record.size) + '</span>';
                     desc += '</div>';
                     description = record.description;
                     if (description === "")
                     {
                        description = me.msg("details.description.none");
                     }
                     desc += '<div class="detail detail-last"><span class="item"><em>' + me.msg("details.description") + '</em> ' + $links($html(description)) + '</span></div>';
                  }
                  break;


               /**
                * "Normal" Folder
                * Technically not supported in the Records Management world.
                */
               case "folder":
                  desc = '<h3 class="filename"><a href="#" class="filter-change" rel="' + Alfresco.DocumentList.generatePathMarkup(locn) + '" title="' + $html(fileName) + '">';
                  desc += $html(record.displayName) + '</a></h3>';

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
                     desc += '<div class="detail detail-first"><span class="item"><em>' + me.msg("details.modified.on") + '</em> ' + Alfresco.util.formatDate(record.modifiedOn) + '</span>';
                     desc += '<span class="item"><em>' + me.msg("details.modified.by") + '</em> <a href="' + Alfresco.DocumentList.generateUserProfileUrl(record.modifiedByUser) + '">' + $html(record.modifiedBy) + '</a></span></div>';
                     description = record.description;
                     if (description === "")
                     {
                        description = me.msg("details.description.none");
                     }
                     desc += '<div class="detail"><span class="item"><em>' + me.msg("details.description") + '</em> ' + $links($html(description)) + '</span></div>';
                     desc += '</div><div class="detail detail-last">&nbsp;</div>';
                  }
                  break;
               
               /**
                * Documents and Links
                */
               default:
                  docDetailsUrl = Alfresco.constants.URL_PAGECONTEXT + "site/" + me.options.siteId + "/document-details?nodeRef=" + record.nodeRef;
                
                  desc = '<h3 class="filename"><span id="' + me.id + '-preview-' + oRecord.getId() + '"><a href="' + docDetailsUrl + '" title="' + $html(fileName) + '">' + $html(record.displayName) + '</a></span><span class="filename">(' + $html(fileName) + ')</span></h3>';
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
                     desc += '<div class="detail detail-first">';
                     desc += '<span class="item"><em>' + me.msg("details.modified.on") + '</em> ' + Alfresco.util.formatDate(record.modifiedOn) + '</span>';
                     desc += '<span class="item"><em>' + me.msg("details.modified.by") + '</em> <a href="' + Alfresco.DocumentList.generateUserProfileUrl(record.modifiedByUser) + '">' + $html(record.modifiedBy) + '</a></span>';
                     desc += '<span class="item"><em>' + me.msg("details.size") + '</em> ' + Alfresco.util.formatFileSize(record.size) + '</span>';
                     desc += '</div><div class="detail detail-last">';
                     description = record.description;
                     if (description === "")
                     {
                        description = me.msg("details.description.none");
                     }
                     desc += '<span class="item"><em>' + me.msg("details.description") + '</em> ' + $links($html(description)) + '</span>';
                     desc += '</div>';
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
         
         // Set the default view filter to be "path" and the owner to be "Alfresco.DocListTree" unless set in initialFilter
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
            selectRecords:
            {
               "record": true,
               "record-nonelec": true
            },
            selectUndeclaredRecords:
            {
               "undeclared-record": true,
               "undeclared-record-nonelec": true
            },
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
       * The urls to be used when creating links in the action cell
       *
       * @method getActionUrls
       * @param record {object} A data source element describing the item in the list
       * @return {object} Object literal containing URLs to be substituted in action placeholders
       */
      getActionUrls: function DL_getActionUrls(record)
      {
         var urlContextSite = Alfresco.constants.URL_PAGECONTEXT + "site/" + this.options.siteId,
            nodeRef = record.getData("nodeRef"),
            filePlan = this.doclistMetadata.filePlan.replace(":/", "");

         return (
         {
            downloadUrl: Alfresco.constants.PROXY_URI + record.getData("contentUrl") + "?a=true",
            documentDetailsUrl: urlContextSite + "/document-details?nodeRef=" + nodeRef,
            folderDetailsUrl: urlContextSite + "/folder-details?nodeRef=" + nodeRef,
            editMetadataUrl: urlContextSite + "/edit-metadata?nodeRef=" + nodeRef,
            recordSeriesDetailsUrl: urlContextSite + "/record-series-details?nodeRef=" + nodeRef,
            recordCategoryDetailsUrl: urlContextSite + "/record-category-details?nodeRef=" + nodeRef,
            recordFolderDetailsUrl: urlContextSite + "/record-folder-details?nodeRef=" + nodeRef,
            transfersZipUrl: Alfresco.constants.PROXY_URI + "api/node/" + filePlan + "/transfers/" + nodeRef.replace(":/", "").split("/")[2]
         });
      }
   }, true);
})();
