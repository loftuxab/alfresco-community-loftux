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
 * Document Library "Copy- and Move-To" module for Document Library.
 * 
 * @namespace Alfresco.module
 * @class Alfresco.module.DoclibCopyMoveTo
 */
(function()
{
   Alfresco.module.DoclibCopyMoveTo = function(htmlId)
   {
      Alfresco.module.DoclibCopyMoveTo.superclass.constructor.call(this, htmlId);

      // Re-register with our own name
      this.name = "Alfresco.module.DoclibCopyMoveTo";
      Alfresco.util.ComponentManager.reregister(this);

      return this;
   };

   YAHOO.extend(Alfresco.module.DoclibCopyMoveTo, Alfresco.module.DoclibGlobalFolder,
   {
      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @override
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.module.DoclibMoveTo} returns 'this' for method chaining
       */
      setOptions: function DLCMT_setOptions(obj)
      {
         var dataWebScripts =
         {
            copy: "copy-to",
            move: "move-to"
         };
         
         if (typeof dataWebScripts[obj.mode] == "undefined")
         {
            throw new Error("Alfresco.module.CopyMoveTo: Invalid mode '" + obj.mode + "'");
         }
         
         var allowedViewModes =
         [
            Alfresco.module.DoclibGlobalFolder.VIEW_MODE_SITE,
            Alfresco.module.DoclibGlobalFolder.VIEW_MODE_REPOSITORY
         ];
         
         // Actions module
         this.modules.actions = new Alfresco.module.DoclibActions((obj.siteId && obj.siteId !== "") ? Alfresco.doclib.MODE_SITE : Alfresco.doclib.MODE_REPOSITORY);

         return Alfresco.module.DoclibCopyMoveTo.superclass.setOptions.call(this, YAHOO.lang.merge(
         {
            viewMode: (obj.siteId && obj.siteId !== "") ? Alfresco.module.DoclibGlobalFolder.VIEW_MODE_SITE : Alfresco.module.DoclibGlobalFolder.VIEW_MODE_REPOSITORY,
            allowedViewModes: allowedViewModes,
            templateUrl: Alfresco.constants.URL_SERVICECONTEXT + "modules/documentlibrary/copy-move-to",
            dataWebScript: dataWebScripts[obj.mode]
         }, obj));
      },


      /**
       * YUI WIDGET EVENT HANDLERS
       * Handlers for standard events fired from YUI widgets, e.g. "click"
       */

      /**
       * Dialog OK button event handler
       *
       * @method onOK
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onOK: function DLCMT_onOK(e, p_obj)
      {
         var files, multipleFiles = [], params, i, j;

         // Single/multi files into array of nodeRefs
         if (YAHOO.lang.isArray(this.options.files))
         {
            files = this.options.files;
         }
         else
         {
            files = [this.options.files];
         }
         for (i = 0, j = files.length; i < j; i++)
         {
            multipleFiles.push(files[i].nodeRef);
         }
         
         // Success callback function
         var fnSuccess = function DLCMT__onOK_success(p_data)
         {
            var result,
               successCount = p_data.json.successCount,
               failureCount = p_data.json.failureCount;

            this.widgets.dialog.hide();

            // Did the operation succeed?
            if (!p_data.json.overallSuccess)
            {
               Alfresco.util.PopupManager.displayMessage(
               {
                  text: this.msg("message.failure")
               });
               return;
            }

            YAHOO.Bubbling.fire("filesCopied",
            {
               destination: this.currentPath,
               successCount: successCount,
               failureCount: failureCount
            });
            
            for (var i = 0, j = p_data.json.totalResults; i < j; i++)
            {
               result = p_data.json.results[i];
               
               if (result.success)
               {
                  YAHOO.Bubbling.fire(result.type == "folder" ? "folderCopied" : "fileCopied",
                  {
                     multiple: true,
                     nodeRef: result.nodeRef,
                     destination: this.currentPath
                  });
               }
            }

            Alfresco.util.PopupManager.displayMessage(
            {
               text: this.msg("message.success", successCount)
            });
         };

         // Failure callback function
         var fnFailure = function DLCMT__onOK_failure(p_data)
         {
            this.widgets.dialog.hide();

            Alfresco.util.PopupManager.displayMessage(
            {
               text: this.msg("message.failure")
            });
         };

         // Construct webscript URI based on current viewMode
         var webscriptName = this.options.dataWebScript + "/node/{nodeRef}",
            nodeRef = new Alfresco.util.NodeRef(this.selectedNode.data.nodeRef);
         
         // Construct the data object for the genericAction call
         this.modules.actions.genericAction(
         {
            success:
            {
               callback:
               {
                  fn: fnSuccess,
                  scope: this
               }
            },
            failure:
            {
               callback:
               {
                  fn: fnFailure,
                  scope: this
               }
            },
            webscript:
            {
               method: Alfresco.util.Ajax.POST,
               name: webscriptName,
               params:
               {
                  nodeRef: nodeRef.uri
               }
            },
            wait:
            {
               message: this.msg("message.please-wait")
            },
            config:
            {
               requestContentType: Alfresco.util.Ajax.JSON,
               dataObj:
               {
                  nodeRefs: multipleFiles
               }
            }
         });
         
         this.widgets.okButton.set("disabled", true);
         this.widgets.cancelButton.set("disabled", true);
      },

      /**
       * Gets a custom message depending on current view mode
       *
       * @method msg
       * @param messageId {string} The messageId to retrieve
       * @return {string} The custom message
       * @override
       */
      msg: function DLCMT_msg(messageId)
      {
         return Alfresco.util.message.call(this, this.options.mode + "." + messageId, this.name, Array.prototype.slice.call(arguments).slice(1));
      },

      
      /**
       * PRIVATE FUNCTIONS
       */

      /**
       * Internal show dialog function
       * @method _showDialog
       * @override
       */
      _showDialog: function DLCMT__showDialog()
      {
         this.widgets.okButton.set("label", this.msg("button"));
         return Alfresco.module.DoclibCopyMoveTo.superclass._showDialog.apply(this, arguments);
      }
   });

   /* Dummy instance to load optional YUI components early */
   var dummyInstance = new Alfresco.module.DoclibCopyMoveTo("null");
})();