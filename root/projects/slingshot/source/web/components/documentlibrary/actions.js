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
 * Document Library Actions module
 * 
 * @namespace Alfresco.doclib
 * @class Alfresco.doclib.Actions
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML,
      $combine = Alfresco.util.combinePaths;
   
   /**
    * Alfresco.doclib.Actions implementation
    */
   Alfresco.doclib.Actions = {};
   Alfresco.doclib.Actions.prototype =
   {
      /**
       * Asset metadata.
       *
       * @override
       * @method onActionDetails
       * @param asset {object} Object literal representing one file or folder to be actioned
       */
      onActionDetails: function dlA_onActionDetails(asset)
      {
         var scope = this;
         
         // Intercept before dialog show
         var doBeforeDialogShow = function dlA_onActionDetails_doBeforeDialogShow(p_form, p_dialog)
         {
            // Dialog title
            var titleDiv = Dom.get(p_dialog.id + "-dialogTitle"),
               fileSpan = '<span class="light">' + $html(asset.displayName) + '</span>';
            titleDiv.innerHTML = scope.msg("edit-details.title", fileSpan);

            // Edit metadata link button
            this.widgets.editMetadata = Alfresco.util.createYUIButton(p_dialog, "editMetadata", null, 
            {
               type: "link",
               label: scope.msg("edit-details.label.edit-metadata"),
               href: "edit-metadata?nodeRef=" + asset.nodeRef
            });
         };

         var templateUrl = YAHOO.lang.substitute(Alfresco.constants.URL_SERVICECONTEXT + "components/form?itemKind={itemKind}&itemId={itemId}&destination={destination}&mode={mode}&submitType={submitType}&formId={formId}&showCancelButton=true",
         {
            itemKind: "node",
            itemId: asset.nodeRef,
            mode: "edit",
            submitType: "json",
            formId: "doclib-simple-metadata"
         });

         // Using Forms Service, so always create new instance
         var editDetails = new Alfresco.module.SimpleDialog(this.id + "-editDetails");

         editDetails.setOptions(
         {
            width: "40em",
            templateUrl: templateUrl,
            actionUrl: null,
            doBeforeDialogShow:
            {
               fn: doBeforeDialogShow,
               scope: this
            },
            onSuccess:
            {
               fn: function dlA_onActionDetails_success(response)
               {
                  // Reload the node's metadata
                  Alfresco.util.Ajax.request(
                  {
                     url: Alfresco.constants.PROXY_URI + "slingshot/doclib/doclist/node/" + asset.nodeRef.replace(":/", ""),
                     successCallback:
                     {
                        fn: function dlA_onActionDetails_refreshSuccess(response)
                        {
                           var file = response.json.items[0];

                           // Fire "renamed" event
                           YAHOO.Bubbling.fire(asset.type == "folder" ? "folderRenamed" : "fileRenamed",
                           {
                              file: file
                           });

                           // Fire "tagRefresh" event
                           YAHOO.Bubbling.fire("tagRefresh");

                           // Display success message
                           Alfresco.util.PopupManager.displayMessage(
                           {
                              text: this.msg("message.details.success")
                           });
                        },
                        scope: this
                     },
                     failureCallback:
                     {
                        fn: function dlA_onActionDetails_refreshFailure(response)
                        {
                           Alfresco.util.PopupManager.displayMessage(
                           {
                              text: this.msg("message.details.failure")
                           });
                        },
                        scope: this
                     }
                  });
               },
               scope: this
            },
            onFailure:
            {
               fn: function dLA_onActionDetails_failure(response)
               {
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: this.msg("message.details.failure")
                  });
               },
               scope: this
            }
         }).show();
      },

      /**
       * Locate folder.
       *
       * @method onActionLocate
       * @param asset {object} Object literal representing one file or folder to be actioned
       */
      onActionLocate: function dlA_onActionLocate(asset)
      {
         this.options.highlightFile = asset.displayName;

         // Change active filter to path
         YAHOO.Bubbling.fire("changeFilter",
         {
            filterId: "path",
            filterData: asset.location.path
         });
      },

      /**
       * Delete asset.
       *
       * @method onActionDelete
       * @param asset {object} Object literal representing the file or folder to be actioned
       */
      onActionDelete: function dlA_onActionDelete(asset)
      {
         var me = this;
         
         Alfresco.util.PopupManager.displayPrompt(
         {
            title: this.msg("message.confirm.delete.title"),
            text: this.msg("message.confirm.delete", asset.displayName),
            buttons: [
            {
               text: this.msg("button.delete"),
               handler: function dlA_onActionDelete_delete()
               {
                  this.destroy();
                  me._onActionDeleteConfirm.call(me, asset);
               }
            },
            {
               text: this.msg("button.cancel"),
               handler: function dlA_onActionDelete_cancel()
               {
                  this.destroy();
               },
               isDefault: true
            }]
         });
      },

      /**
       * Delete asset confirmed.
       *
       * @method _onActionDeleteConfirm
       * @param asset {object} Object literal representing the file or folder to be actioned
       * @private
       */
      _onActionDeleteConfirm: function dlA__onActionDeleteConfirm(asset)
      {
         var path = asset.location.path,
            fileName = asset.fileName,
            filePath = $combine(path, fileName),
            displayName = asset.displayName,
            nodeRef = new Alfresco.util.NodeRef(asset.nodeRef);
         
         this.modules.actions.genericAction(
         {
            success:
            {
               activity:
               {
                  siteId: this.options.siteId,
                  activityType: "file-deleted",
                  page: "documentlibrary",
                  activityData:
                  {
                     fileName: fileName,
                     path: path
                  }
               },
               event:
               {
                  name: asset.isFolder ? "folderDeleted" : "fileDeleted",
                  obj:
                  {
                     path: filePath
                  }
               },
               message: this.msg("message.delete.success", displayName)
            },
            failure:
            {
               message: this.msg("message.delete.failure", displayName)
            },
            webscript:
            {
               method: Alfresco.util.Ajax.DELETE,
               name: "file/node/{nodeRef}",
               params:
               {
                  nodeRef: nodeRef.uri
               }
            }
         });
      },

      /**
       * Edit Offline.
       * NOTE: Placeholder only, clients MUST implement their own editOffline action
       *
       * @method onActionEditOffline
       * @param asset {object} Object literal representing the file or folder to be actioned
       */
      onActionEditOffline: function dlA_onActionEditOffline(asset)
      {
         Alfresco.logger.error("onActionEditOffline", "Abstract implementation not overridden");
      },

      /**
       * Edit Online.
       * NOTE: Placeholder only, clients MUST implement their own editOffline action
       *
       * @method onActionEditOnline
       * @param asset {object} Object literal representing the file or folder to be actioned
       */
      onActionEditOnline: function dlA_onActionEditOnline(asset)
      {
         Alfresco.logger.error("onActionEditOnline", "Abstract implementation not overridden");
      },

      /**
       * Upload new version.
       *
       * @method onActionUploadNewVersion
       * @param asset {object} Object literal representing the file or folder to be actioned
       */
      onActionUploadNewVersion: function dlA_onActionUploadNewVersion(asset)
      {
         var displayName = asset.displayName,
            nodeRef = asset.nodeRef,
            version = asset.version;

         if (!this.fileUpload)
         {
            this.fileUpload = Alfresco.getFileUploadInstance();
         }

         // Show uploader for multiple files
         var description = this.msg("label.filter-description", displayName),
            extensions = "*";
         if (displayName && new RegExp(/[^\.]+\.[^\.]+/).exec(displayName))
         {
            // Only add an filtering extension if filename contains a name and a suffix
            extensions = "*" + displayName.substring(displayName.lastIndexOf("."));
         }
         
         if (asset.custom && asset.custom.workingCopyVersion)
         {
            version = asset.custom.workingCopyVersion;
         }
         
         var singleUpdateConfig =
         {
            siteId: this.options.siteId,
            containerId: this.options.containerId,
            updateNodeRef: nodeRef,
            updateFilename: displayName,
            updateVersion: version,
            overwrite: true,
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
         };
         this.fileUpload.show(singleUpdateConfig);
      },

      /**
       * Called from the uploader component after a the new version has been uploaded.
       *
       * @method onNewVersionUploadComplete
       * @param complete {object} Object literal containing details of successful and failed uploads
       */
      onNewVersionUploadComplete: function dlA_onNewVersionUploadComplete(complete)
      {
         var success = complete.successful.length, activityData, file;
         if (success > 0)
         {
            if (success < this.options.groupActivitiesAt || 5)
            {
               // Below cutoff for grouping Activities into one
               for (var i = 0; i < success; i++)
               {
                  file = complete.successful[i];
                  activityData =
                  {
                     fileName: file.fileName,
                     nodeRef: file.nodeRef
                  };
                  this.modules.actions.postActivity(this.options.siteId, "file-updated", "document-details", activityData);
               }
            }
            else
            {
               // grouped into one message
               activityData =
               {
                  fileCount: success,
                  path: this.currentPath
               };
               this.modules.actions.postActivity(this.options.siteId, "files-updated", "documentlibrary", activityData);
            }
         }
      },

      /**
       * Cancel editing.
       *
       * @method onActionCancelEditing
       * @param asset {object} Object literal representing the file or folder to be actioned
       */
      onActionCancelEditing: function dlA_onActionCancelEditing(asset)
      {
         var displayName = asset.displayName,
            nodeRef = new Alfresco.util.NodeRef(asset.nodeRef);

         this.modules.actions.genericAction(
         {
            success:
            {
               event:
               {
                  name: "metadataRefresh"
               },
               message: this.msg("message.edit-cancel.success", displayName)
            },
            failure:
            {
               message: this.msg("message.edit-cancel.failure", displayName)
            },
            webscript:
            {
               method: Alfresco.util.Ajax.POST,
               name: "cancel-checkout/node/{nodeRef}",
               params:
               {
                  nodeRef: nodeRef.uri
               }
            }
         });
      },
      
      /**
       * Copy single document or folder.
       *
       * @method onActionCopyTo
       * @param asset {object} Object literal representing the file or folder to be actioned
       */
      onActionCopyTo: function dlA_onActionCopyTo(asset)
      {
         this._copyMoveTo("copy", asset);
      },

      /**
       * Move single document or folder.
       *
       * @method onActionMoveTo
       * @param asset {object} Object literal representing the file or folder to be actioned
       */
      onActionMoveTo: function dlA_onActionMoveTo(asset)
      {
         this._copyMoveTo("move", asset);
      },

      /**
       * Copy/Move To implementation.
       *
       * @method _copyMoveTo
       * @param mode {String} Operation mode: copy|move
       * @param asset {object} Object literal representing the file or folder to be actioned
       * @private
       */
      _copyMoveTo: function dlA__copyMoveTo(mode, asset)
      {
         // Check mode is an allowed one
         if (!mode in
            {
               copy: true,
               move: true
            })
         {
            throw new Error("'" + mode + "' is not a valid Copy/Move to mode.");
         }
         
         if (!this.modules.copyMoveTo)
         {
            this.modules.copyMoveTo = new Alfresco.module.DoclibCopyMoveTo(this.id + "-copyMoveTo");
         }

         this.modules.copyMoveTo.setOptions(
         {
            mode: mode,
            siteId: this.options.siteId,
            containerId: this.options.containerId,
            path: this.currentPath,
            files: asset
         }).showDialog();
      },

      /**
       * Assign workflow.
       *
       * @method onActionAssignWorkflow
       * @param asset {object} Object literal representing the file or folder to be actioned
       */
      onActionAssignWorkflow: function dlA_onActionAssignWorkflow(asset)
      {
         if (!this.modules.assignWorkflow)
         {
            this.modules.assignWorkflow = new Alfresco.module.DoclibWorkflow(this.id + "-workflow");
         }

         this.modules.assignWorkflow.setOptions(
         {
            siteId: this.options.siteId,
            containerId: this.options.containerId,
            path: this.currentPath,
            files: asset
         }).showDialog();
      },

      /**
       * Set permissions on a single document or folder.
       *
       * @method onActionManagePermissions
       * @param asset {object} Object literal representing the file or folder to be actioned
       */
      onActionManagePermissions: function dlA_onActionManagePermissions(asset)
      {
         if (!this.modules.permissions)
         {
            this.modules.permissions = new Alfresco.module.DoclibPermissions(this.id + "-permissions");
         }

         this.modules.permissions.setOptions(
         {
            siteId: this.options.siteId,
            containerId: this.options.containerId,
            path: this.currentPath,
            files: asset
         }).showDialog();
      },

      /**
       * Manage aspects.
       *
       * @method onActionManageAspects
       * @param asset {object} Object literal representing the file or folder to be actioned
       */
      onActionManageAspects: function dlA_onActionManageAspects(asset)
      {
         if (!this.modules.aspects)
         {
            this.modules.aspects = new Alfresco.module.DoclibAspects(this.id + "-aspects");
         }

         this.modules.aspects.setOptions(
         {
            file: asset
         }).show();
      },

      /**
       * Change Type
       *
       * @method onActionChangeType
       * @param asset {object} Object literal representing the file or folder to be actioned
       */
      onActionChangeType: function dlA_onActionChangeType(asset)
      {
         var nodeRef = asset.nodeRef,
            currentType = asset.nodeType,
            displayName = asset.displayName,
            actionUrl = Alfresco.constants.PROXY_URI + $combine("slingshot/doclib/type/node", nodeRef.replace(":/", ""));

         var doSetupFormsValidation = function dlA_oACT_doSetupFormsValidation(p_form)
         {
            // Validation
            p_form.addValidation(this.id + "-changeType-type", function fnValidateType(field, args, event, form, silent, message)
            {
               return field.options[field.selectedIndex].value !== "-";
            }, null, "change");
            p_form.setShowSubmitStateDynamically(true, false);
         };

         // Always create a new instance
         this.modules.changeType = new Alfresco.module.SimpleDialog(this.id + "-changeType").setOptions(
         {
            width: "30em",
            templateUrl: Alfresco.constants.URL_SERVICECONTEXT + "modules/documentlibrary/change-type?currentType=" + encodeURIComponent(currentType),
            actionUrl: actionUrl,
            doSetupFormsValidation:
            {
               fn: doSetupFormsValidation,
               scope: this
            },
            firstFocus: this.id + "-changeType-type",
            onSuccess:
            {
               fn: function dlA_onActionChangeType_success(response)
               {
                  YAHOO.Bubbling.fire("metadataRefresh",
                  {
                     highlightFile: displayName
                  });
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: this.msg("message.change-type.success", displayName)
                  });
               },
               scope: this
            },
            onFailure:
            {
               fn: function dlA_onActionChangeType_failure(response)
               {
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: this.msg("message.change-type.failure", displayName)
                  });
               },
               scope: this
            }
         });
         this.modules.changeType.show();
      }
   };
})();