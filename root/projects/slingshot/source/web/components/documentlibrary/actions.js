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
    * Alfresco Slingshot aliases
    */
   var $combine = Alfresco.util.combinePaths;
   
   /**
    * Alfresco.doclib.Actions implementation
    */
   Alfresco.doclib.Actions = {};
   Alfresco.doclib.Actions.prototype =
   {
      /**
       * assets details.
       *
       * @method onActionDetails
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionDetails: function dlA_onActionDetails(assets)
      {
         if (!this.modules.details)
         {
            this.modules.details = new Alfresco.module.DoclibDetails(this.id + "-details");
         }

         this.modules.details.setOptions(
         {
            siteId: this.options.siteId,
            file: assets
         }).showDialog();
      },

      /**
       * Delete assets.
       *
       * @method onActionDelete
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionDelete: function dlA_onActionDelete(assets)
      {
         var me = this;
         
         Alfresco.util.PopupManager.displayPrompt(
         {
            title: this.msg("message.confirm.delete.title"),
            text: this.msg("message.confirm.delete", assets.displayName),
            buttons: [
            {
               text: this.msg("button.delete"),
               handler: function dlA_onActionDelete_delete()
               {
                  this.destroy();
                  me._onActionDeleteConfirm.call(me, assets);
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
       * Delete assets confirmed.
       *
       * @method _onActionDeleteConfirm
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       * @private
       */
      _onActionDeleteConfirm: function dlA__onActionDeleteConfirm(assets)
      {
         var path = assets.location.path,
            fileName = assets.fileName,
            filePath = $combine(path, fileName),
            displayName = assets.displayName;
         
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
                  name: assets.isFolder ? "folderDeleted" : "fileDeleted",
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
               name: "file/site/{site}/{container}{path}/{file}",
               params:
               {
                  site: this.options.siteId,
                  container: this.options.containerId,
                  path: Alfresco.util.encodeURIPath(path),
                  file: encodeURIComponent(fileName)
               }
            }
         });
      },

      /**
       * Edit Offline.
       * NOTE: Placeholder only, clients MUST implement their own editOffline action
       *
       * @method onActionEditOffline
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionEditOffline: function dlA_onActionEditOffline(assets)
      {
         Alfresco.logger.error("onActionEditOffline", "Abstract implementation not overridden");
      },

      /**
       * Edit Online.
       * NOTE: Placeholder only, clients MUST implement their own editOffline action
       *
       * @method onActionEditOnline
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionEditOnline: function dlA_onActionEditOnline(assets)
      {
         Alfresco.logger.error("onActionEditOnline", "Abstract implementation not overridden");
      },

      /**
       * Upload new version.
       *
       * @method onActionUploadNewVersion
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionUploadNewVersion: function dlA_onActionUploadNewVersion(assets)
      {
         var displayName = assets.displayName,
            nodeRef = assets.nodeRef;

         if (!this.fileUpload)
         {
            this.fileUpload = Alfresco.getFileUploadInstance();
         }

         // Show uploader for multiple files
         var description = this.msg("label.filter-description", displayName),
            extensions = "*" + displayName.substring(displayName.lastIndexOf("."));
         
         var singleUpdateConfig =
         {
            siteId: this.options.siteId,
            containerId: this.options.containerId,
            updateNodeRef: nodeRef,
            updateFilename: displayName,
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
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionCancelEditing: function dlA_onActionCancelEditing(assets)
      {
         var displayName = assets.displayName,
            nodeRef = assets.nodeRef;

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
                  nodeRef: nodeRef.replace(":/", "")
               }
            }
         });
      },
      
      /**
       * Copy single document or folder.
       *
       * @method onActionCopyTo
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionCopyTo: function dlA_onActionCopyTo(assets)
      {
         this._copyMoveTo("copy", assets);
      },

      /**
       * Move single document or folder.
       *
       * @method onActionMoveTo
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionMoveTo: function dlA_onActionMoveTo(assets)
      {
         this._copyMoveTo("move", assets);
      },

      /**
       * Copy/Move To implementation.
       *
       * @method _copyMoveTo
       * @param mode {String} Operation mode: copy|move
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       * @private
       */
      _copyMoveTo: function dlA__copyMoveTo(mode, assets)
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
            files: assets
         }).showDialog();
      },

      /**
       * Assign workflow.
       *
       * @method onActionAssignWorkflow
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionAssignWorkflow: function dlA_onActionAssignWorkflow(assets)
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
            files: assets
         }).showDialog();
      },

      /**
       * Set permissions on a single document or folder.
       *
       * @method onActionManagePermissions
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionManagePermissions: function dlA_onActionManagePermissions(assets)
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
            files: assets
         }).showDialog();
      },

      /**
       * Manage aspects.
       *
       * @method onActionManageAspects
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionManageAspects: function dlA_onActionManageAspects(assets)
      {
         if (!this.modules.aspects)
         {
            this.modules.aspects = new Alfresco.module.DoclibAspects(this.id + "-aspects");
         }

         this.modules.aspects.setOptions(
         {
            file: assets
         }).show();
      },

      /**
       * Change Type
       *
       * @method onActionChangeType
       * @param assets {object} Object literal representing one or more file(s) or folder(s) to be actioned
       */
      onActionChangeType: function dlA_onActionChangeType(assets)
      {
         var nodeRef = assets.nodeRef,
            displayName = assets.displayName,
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
            templateUrl: Alfresco.constants.URL_SERVICECONTEXT + "modules/documentlibrary/change-type?nodeRef=" + nodeRef,
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