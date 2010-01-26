/**
 * Copyright (C) 2005-2010 Alfresco Software Limited.
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
 * Repository DocumentList Toolbar component.
 * 
 * @namespace Alfresco
 * @class Alfresco.RepositoryDocListToolbar
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
   var $html = Alfresco.util.encodeHTML,
      $combine = Alfresco.util.combinePaths;

   /**
    * RepositoryDocListToolbar constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.RepositoryDocListToolbar} The new DocListToolbar instance
    * @constructor
    */
   Alfresco.RepositoryDocListToolbar = function(htmlId)
   {
      return Alfresco.RepositoryDocListToolbar.superclass.constructor.call(this, htmlId);
   };

   /**
    * Extend Alfresco.DocListToolbar
    */
   YAHOO.extend(Alfresco.RepositoryDocListToolbar, Alfresco.DocListToolbar);

   /**
    * Augment prototype with RecordsActions module, ensuring overwrite is enabled
    */
   YAHOO.lang.augmentProto(Alfresco.RepositoryDocListToolbar, Alfresco.doclib.RepositoryActions, true);
   
   /**
    * Augment prototype with main class implementation, ensuring overwrite is enabled
    */
   YAHOO.lang.augmentObject(Alfresco.RepositoryDocListToolbar.prototype,
   {
      /**
       * YUI WIDGET EVENT HANDLERS
       * Handlers for standard events fired from YUI widgets, e.g. "click"
       */

      /**
       * Create Content menu click handler
       *
       * @method onCreateContent
       * @param sType {string} Event type, e.g. "click"
       * @param aArgs {array} Arguments array, [0] = DomEvent, [1] = EventTarget
       * @param p_obj {object} Object passed back from subscribe method
       */
      onCreateContent: function DLTB_onCreateContent(sType, aArgs, p_obj)
      {
         var domEvent = aArgs[0],
            eventTarget = aArgs[1];

         // Get the mimetype related to the clicked item
         var mimetype = eventTarget.element.firstChild.rel,
            destination = this.modules.docList.doclistMetadata.parent.nodeRef;
         if (mimetype)
         {
            // TODO: Think about replacing this with code that rewrites the href attributes on a "filterChanged" (path) event.
            // This might be necessary to allow the referrer HTTP header to be set by MSIE.
            var url = Alfresco.constants.URL_PAGECONTEXT + "create-content";
            url += "?mimeType=" + encodeURIComponent(mimetype) + "&destination=" + encodeURIComponent(destination);
            window.location.href = url;
         }

         Event.preventDefault(domEvent);
      },

      /**
       * New Folder button click handler
       *
       * @method onNewFolder
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onNewFolder: function DLTB_onNewFolder(e, p_obj)
      {
         var actionUrl = Alfresco.constants.PROXY_URI + $combine("slingshot/doclib/action/folder/site", this.options.siteId, this.options.containerId, Alfresco.util.encodeURIPath(this.currentPath));

         var doSetupFormsValidation = function DLTB_oNF_doSetupFormsValidation(p_form)
         {
            // Validation
            p_form.addValidation(this.id + "-createFolder-name", Alfresco.forms.validation.mandatory, null, "blur");
            p_form.addValidation(this.id + "-createFolder-name", Alfresco.forms.validation.nodeName, null, "keyup");
            p_form.addValidation(this.id + "-createFolder-name", Alfresco.forms.validation.length,
            {
               max: 256,
               crop: true
            }, "keyup");
            p_form.addValidation(this.id + "-createFolder-title", Alfresco.forms.validation.length,
            {
               max: 256,
               crop: true
            }, "keyup");
            p_form.addValidation(this.id + "-createFolder-description", Alfresco.forms.validation.length,
            {
               max: 512,
               crop: true
            }, "keyup");
            p_form.setShowSubmitStateDynamically(true, false);
         };

         if (!this.modules.createFolder)
         {
            this.modules.createFolder = new Alfresco.module.SimpleDialog(this.id + "-createFolder").setOptions(
            {
               width: "30em",
               templateUrl: Alfresco.constants.URL_SERVICECONTEXT + "modules/documentlibrary/create-folder",
               actionUrl: actionUrl,
               doSetupFormsValidation:
               {
                  fn: doSetupFormsValidation,
                  scope: this
               },
               firstFocus: this.id + "-createFolder-name",
               onSuccess:
               {
                  fn: function DLTB_onNewFolder_callback(response)
                  {
                     var folder = response.json.results[0];
                     YAHOO.Bubbling.fire("folderCreated",
                     {
                        name: folder.name,
                        parentPath: folder.parentPath,
                        nodeRef: folder.nodeRef
                     });
                     Alfresco.util.PopupManager.displayMessage(
                     {
                        text: this.msg("message.new-folder.success", folder.name)
                     });
                  },
                  scope: this
               }
            });
         }
         else
         {
            this.modules.createFolder.setOptions(
            {
               actionUrl: actionUrl,
               clearForm: true
            });
         }
         this.modules.createFolder.show();
      },

      /**
       * File Upload button click handler
       *
       * @method onFileUpload
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onFileUpload: function DLTB_onFileUpload(e, p_obj)
      {
         if (this.fileUpload === null)
         {
            this.fileUpload = Alfresco.getFileUploadInstance(); 
         }
         
         // Show uploader for multiple files
         var multiUploadConfig =
         {
            siteId: this.options.siteId,
            containerId: this.options.containerId,
            uploadDirectory: this.currentPath,
            filter: [],
            mode: this.fileUpload.MODE_MULTI_UPLOAD,
            thumbnails: "doclib",
            onFileUploadComplete:
            {
               fn: this.onFileUploadComplete,
               scope: this
            }
         };
         this.fileUpload.show(multiUploadConfig);
      },
      
      /**
       * File Upload complete event handler
       *
       * @method onFileUploadComplete
       * @param complete {object} Object literal containing details of successful and failed uploads
       */
      onFileUploadComplete: function DLTB_onFileUploadComplete(complete)
      {
         // Overridden so activity doesn't get posted
      },

      /**
       * Delete Multiple Assets confirmed.
       *
       * @method _onActionDeleteConfirm
       * @param assets {array} Array containing assets to be deleted
       * @private
       */
      _onActionDeleteConfirm: function DLTB__onActionDeleteConfirm(assets)
      {
         var multipleAssets = [], i, ii;
         for (i = 0, ii = assets.length; i < ii; i++)
         {
            multipleAssets.push(assets[i].nodeRef);
         }
         
         // Success callback function
         var fnSuccess = function DLTB__oADC_success(data, assets)
         {
            var result;
            var successCount = 0;

            // Did the operation succeed?
            if (!data.json.overallSuccess)
            {
               Alfresco.util.PopupManager.displayMessage(
               {
                  text: this.msg("message.multiple-delete.failure")
               });
               return;
            }

            YAHOO.Bubbling.fire("filesDeleted");

            for (i = 0, ii = data.json.totalResults; i < ii; i++)
            {
               result = data.json.results[i];
               
               if (result.success)
               {
                  successCount++;
                  
                  YAHOO.Bubbling.fire(result.type == "folder" ? "folderDeleted" : "fileDeleted",
                  {
                     multiple: true,
                     nodeRef: result.nodeRef
                  });
               }
            }

            Alfresco.util.PopupManager.displayMessage(
            {
               text: this.msg("message.multiple-delete.success", successCount)
            });
         };
         
         // Construct the data object for the genericAction call
         this.modules.actions.genericAction(
         {
            success:
            {
               callback:
               {
                  fn: fnSuccess,
                  scope: this,
                  obj: assets
               }
            },
            failure:
            {
               message: this.msg("message.multiple-delete.failure")
            },
            webscript:
            {
               method: Alfresco.util.Ajax.DELETE,
               name: "files"
            },
            wait:
            {
               message: this.msg("message.multiple-delete.please-wait")
            },
            config:
            {
               requestContentType: Alfresco.util.Ajax.JSON,
               dataObj:
               {
                  nodeRefs: multipleAssets
               }
            }
         });
      },


      /**
       * BUBBLING LIBRARY EVENT HANDLERS FOR PAGE EVENTS
       * Disconnected event handlers for inter-component event notification
       */

      /**
       * Document List Metadata event handler
       * NOTE: This is a temporary fix to enable access to the View Details action from the breadcrumb.
       *       A more complete solution is to present the full list of parent folder actions.
       *
       * @method onDoclistMetadata
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onDoclistMetadata: function DLTB_onDoclistMetadata(layer, args)
      {
         var obj = args[1];
         this.folderDetailsUrl = null;
         if (obj && obj.metadata)
         {
            this.folderDetailsUrl = Alfresco.constants.URL_PAGECONTEXT + "folder-details?nodeRef=" + obj.metadata.parent.nodeRef;
         }
      },
      
   
      /**
       * PRIVATE FUNCTIONS
       */
      
      /**
       * Generates the HTML mark-up for the RSS feed link
       *
       * @method _generateRSSFeedUrl
       * @private
       */
      _generateRSSFeedUrl: function DLTB__generateRSSFeedUrl()
      {
         if (this.widgets.rssFeed && this.modules.docList)
         {
            var params = YAHOO.lang.substitute("{type}/node/{nodeRef}/{path}",
            {
               type: this.modules.docList.options.showFolders ? "all" : "documents",
               nodeRef: "TODO",
               path: Alfresco.util.encodeURIPath(this.currentPath)
            });

            params += "?filter=" + encodeURIComponent(this.currentFilter.filterId);
            if (this.currentFilter.filterData)
            {
               params += "&filterData=" + encodeURIComponent(this.currentFilter.filterData);             
            }
            params += "&format=rss";
            
            this.widgets.rssFeed.set("href", Alfresco.constants.URL_FEEDSERVICECONTEXT + "components/documentlibrary/feed/" + params);
         }
      }
   }, true);
})();