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
 * DOD5015 Document Library Actions module
 * 
 * @namespace Alfresco.doclib
 * @class Alfresco.doclib.RecordsActions
 */
(function()
{
   /**
    * Alfresco.doclib namespace
    */
   Alfresco.doclib = Alfresco.doclib || {};
   Alfresco.doclib.RecordsActions = {};
   
   Alfresco.doclib.RecordsActions.prototype =
   {
      /**
       * Copy single document or folder.
       *
       * @method onActionCopyTo
       * @param asset {object} Object literal representing file or folder to be actioned
       */
      onActionCopyTo: function RDLA_onActionCopyTo(asset)
      {
         this._copyMoveFileTo("copy", asset);
      },

      /**
       * File single document or folder.
       *
       * @method onActionFileTo
       * @param asset {object} Object literal representing file or folder to be actioned
       */
      onActionFileTo: function RDLA_onActionFileTo(asset)
      {
         this._copyMoveFileTo("file", asset);
      },

      /**
       * Move single document or folder.
       *
       * @method onActionMoveTo
       * @param asset {object} Object literal representing file or folder to be actioned
       */
      onActionMoveTo: function RDLA_onActionMoveTo(asset)
      {
         this._copyMoveFileTo("move", asset);
      },
      
      /**
       * Copy/Move/File To implementation.
       *
       * @method _copyMoveFileTo
       * @param mode {String} Operation mode: copy|file|move
       * @param asset {object} Object literal representing file or folder to be actioned
       * @private
       */
      _copyMoveFileTo: function RDLA__copyMoveFileTo(mode, asset)
      {
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
            files: asset
         }).showDialog();
      },

      /**
       * Close Record Folder action.
       *
       * @method onActionCloseFolder
       * @param asset {object} Object literal representing file or folder to be actioned
       */
      onActionCloseFolder: function RDLA_onActionCloseFolder(asset)
      {
         this._dod5015Action("message.close-folder", asset, "closeRecordFolder");
      },

      /**
       * Cut Off action.
       *
       * @method onActionCutoff
       * @param asset {object} Object literal representing file or folder to be actioned
       */
      onActionCutoff: function RDLA_onActionCutoff(asset)
      {
         this._dod5015Action("message.cutoff", asset, "cutoff");
      },

      /**
       * Declare Record action.
       * Special case handling due to the ability to jump to the Edit Metadata page if the action failed.
       *
       * @method onActionDeclare
       * @param asset {object} Object literal representing file or folder to be actioned
       */
      onActionDeclare: function RDLA_onActionDeclare(asset)
      {
         var displayName = asset.displayName,
            editMetadataUrl = Alfresco.constants.URL_PAGECONTEXT + "site/" + this.options.siteId + "/edit-metadata?nodeRef=" + asset.nodeRef;

         this._dod5015Action("message.declare", asset, "declareRecord", null,
         {
            failure:
            {
               message: null,
               callback:
               {
                  fn: function RDLA_oAD_failure(data)
                  {
                     Alfresco.util.PopupManager.displayPrompt(
                     {
                        title: this.msg("message.declare.failure", displayName),
                        text: this.msg("message.declare.failure.more"),
                        buttons: [
                        {
                           text: this.msg("actions.edit-details"),
                           handler: function RDLA_oAD_failure_editDetails()
                           {
                              window.location = editMetadataUrl;
                              this.destroy();
                           },
                           isDefault: true
                        },
                        {
                           text: this.msg("button.cancel"),
                           handler: function RDLA_oAD_failure_cancel()
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
       * @param asset {object} Object literal representing file or folder to be actioned
       */
      onActionDestroy: function RDLA_onActionDestroy(asset)
      {
         this._dod5015Action("message.destroy", asset, "destroy");
      },

      /**
       * Freeze action.
       *
       * @method onActionFreeze
       * @param asset {object} Object literal representing file or folder to be actioned
       */
      onActionFreeze: function RDLA_onActionFreeze(asset)
      {
         Alfresco.util.PopupManager.getUserInput(
         {
            title: this.msg("message.freeze.reason.title"),
            text: this.msg("message.freeze.reason.label"),
            okButtonText: this.msg("button.freeze.record"),
            callback:
            {
               fn: function RDLA_onActionFreeze_callback(value)
               {
                  this._dod5015Action("message.freeze", asset, "freeze",
                  {
                     "reason": value
                  });
               },
               scope: this
            }
         });
      },

      /**
       * Open Record Folder action.
       *
       * @method onActionOpenFolder
       * @param asset {object} Object literal representing file or folder to be actioned
       */
      onActionOpenFolder: function RDLA_onActionOpenFolder(asset)
      {
         this._dod5015Action("message.open-folder", asset, "openRecordFolder");
      },

      /**
       * Relinquish Hold action.
       *
       * @method onActionRelinquish
       * @param asset {object} Object literal representing file or folder to be actioned
       */
      onActionRelinquish: function RDLA_onActionRelinquish(asset)
      {
         this._dod5015Action("message.relinquish", asset, "relinquishHold");
      },

      /**
       * Retain action.
       *
       * @method onActionRetain
       * @param asset {object} Object literal representing file or folder to be actioned
       */
      onActionRetain: function RDLA_onActionRetain(asset)
      {
         this._dod5015Action("message.retain", asset, "retain");
      },

      /**
       * Reviewed action.
       *
       * @method onActionReviewed
       * @param asset {object} Object literal representing file or folder to be actioned
       */
      onActionReviewed: function RDLA_onActionReviewed(asset)
      {
         this._dod5015Action("message.review", asset, "reviewed");
      },

      /**
       * Transfer action.
       *
       * @method onActionTransfer
       * @param asset {object} Object literal representing file or folder to be actioned
       */
      onActionTransfer: function RDLA_onActionTransfer(asset)
      {
         this._dod5015Action("message.transfer", asset, "transfer");
      },

      /**
       * Transfer Confirmation action.
       *
       * @method onActionTransferConfirm
       * @param asset {object} Object literal representing file or folder to be actioned
       */
      onActionTransferConfirm: function RDLA_onActionTransferConfirm(asset)
      {
         this._dod5015Action("message.transfer-confirm", asset, "transferConfirm");
      },

      /**
       * Undeclare record.
       *
       * @method onActionUndeclare
       * @param asset {object} Object literal representing file or folder to be actioned
       */
      onActionUndeclare: function RDLA_onActionUndeclare(asset)
      {
         this._dod5015Action("message.undeclare", asset, "undeclareRecord");
      },

      /**
       * Unfreeze record.
       *
       * @method onActionUnfreeze
       * @param asset {object} Object literal representing file or folder to be actioned
       */
      onActionUnfreeze: function RDLA_onActionUnfreeze(asset)
      {
         this._dod5015Action("message.unfreeze", asset, "unfreeze");
      },
      
      /**
       * Split email record action.
       *
       * @method onActionSplitEmail
       * @param asset {object} Object literal representing file or folder to be actioned
       */
      onActionSplitEmail: function RDLA_onActionSplitEmail(asset)
      {
         this._dod5015Action("message.split-email", asset, "splitEmail");
      },

      /**
       * DOD5015 action.
       *
       * @method _dod5015Action
       * @param i18n {string} Will be appended with ".success" or ".failure" depending on action outcome
       * @param asset {object} Object literal representing file(s) or folder(s) to be actioned
       * @param actionName {string} Name of repository action to run
       * @param actionParams {object} Optional object literal to pass parameters to the action
       * @param configOverride {object} Optional object literal to override default configuration parameters
       * @private
       */
      _dod5015Action: function RDLA__dod5015Action(i18n, asset, actionName, actionParams, configOverride)
      {
         var displayName = asset.displayName,
            dataObj =
            {
               name: actionName
            };

         if (YAHOO.lang.isArray(asset))
         {
            dataObj.nodeRefs = [];
            for (var i = 0, ii = asset.length; i < ii; i++)
            {
               dataObj.nodeRefs.push(asset[i].nodeRef);
            }
         }
         else
         {
            dataObj.nodeRef = asset.nodeRef;
         }

         if (YAHOO.lang.isObject(actionParams))
         {
            dataObj.params = actionParams;
         }
         
         var config =
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
               dataObj: dataObj
            }
         };
         
         if (YAHOO.lang.isObject(configOverride))
         {
            config = YAHOO.lang.merge(config, configOverride);
         }

         this.modules.actions.genericAction(config);
      }
   };
})();
