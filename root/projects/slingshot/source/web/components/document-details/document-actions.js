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
 * Document actions component.
 * 
 * @namespace Alfresco
 * @class Alfresco.DocumentActions
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
    * DocumentActions constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.DocumentActions} The new DocumentActions instance
    * @constructor
    */
   Alfresco.DocumentActions = function(htmlId)
   {
      this.name = "Alfresco.DocumentActions";
      this.id = htmlId;
      
      // initialise prototype properties
      this.widgets = {};
      this.modules = {};
      this.docData = null;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button"], this.onComponentsLoaded, this);
   
      /* Decoupled event listeners */
      YAHOO.Bubbling.on("documentDetailsAvailable", this.onDocumentDetailsAvailable, this);
      
      return this;
   }
   
   Alfresco.DocumentActions.prototype =
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
         containerId: "documentLibrary"
      },
      
      /**
       * Object container for storing YUI widget instances.
       * 
       * @property widgets
       * @type object
       */
       widgets: {},
       
      /**
       * Object container for storing YUI widget instances.
       * 
       * @property widgets
       * @type object
       */
       modules: {},
       
       /**
        * The data for the document
        * 
        * @property docData
        * @type object
        */
       docData: null,
       
      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.Search} returns 'this' for method chaining
       */
      setOptions: function SiteMembers_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },
      
      /**
       * Set messages for this component.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.Search} returns 'this' for method chaining
       */
      setMessages: function DocumentActions_setMessages(obj)
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
      onComponentsLoaded: function DocumentActions_onComponentsLoaded()
      {
         // do nothing
      },
      
      /**
       * Event handler called when the "documentDetailsAvailable" event is received
       */
      onDocumentDetailsAvailable: function DocumentActions_onDocumentDetailsAvailable(layer, args)
      {
         // reference to self
         var me = this;
         
         // remember the data for the document
         this.docData = args[1];
         
         // update the href for the download link
         var url = Alfresco.constants.PROXY_URI + this.docData.contentUrl;
         Dom.get(this.id + "-download-action").href = url + "?a=true";
         
         /**
          * NOTE: If linefeeds exist between the <div> and <a> tags, the firstChild property
          *       in the outer loop will return a text node "\n" instead of the <a> tag.
          */
          // Disable actions not accessible to current user
          var actionsContainer = Dom.get(this.id + "-actionSet-document");
         if (this.docData.permissions && this.docData.permissions.userAccess)
         {
            var userAccess = this.docData.permissions.userAccess;
            var actions = YAHOO.util.Selector.query("div", actionsContainer);
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
                        actionsContainer.removeChild(actions[i]);
                        break;
                     }
                  }
               }
            }
         }
         Dom.setStyle(actionsContainer, "visibility", "visible");
         
         // Hook action events
         var fnActionHandler = function DocumentActions_fnActionHandler(layer, args)
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
         
         // DocLib Actions module
         this.modules.actions = new Alfresco.module.DoclibActions();
      },
      
      /**
       * Copy single document or folder.
       *
       * @method onActionCopyTo
       * @param obj {object} Not used
       */
      onActionCopyTo: function DocumentActions_onActionCopyTo(obj)
      {
         if (!this.modules.copyTo)
         {
            this.modules.copyTo = new Alfresco.module.DoclibCopyTo(this.id + "-copyTo").setOptions(
            {
               siteId: this.options.siteId,
               containerId: this.options.containerId,
               path: this.docData.location.path,
               files: this.docData
            });
         }
         else
         {
            this.modules.copyTo.setOptions(
            {
               path: this.docData.location.path,
               files: this.docData
            })
         }

         // show the dialog         
         this.modules.copyTo.showDialog();
      },
      
      /**
       * Move single document or folder.
       *
       * @method onActionMoveTo
       * @param obj {object} Not used
       */
      onActionMoveTo: function DocumentActions_onActionMoveTo(obj)
      {
         if (!this.modules.moveTo)
         {
            this.modules.moveTo = new Alfresco.module.DoclibMoveTo(this.id + "-moveTo").setOptions(
            {
               siteId: this.options.siteId,
               containerId: this.options.containerId,
               path: this.docData.location.path,
               files: this.docData
            });
         }
         else
         {
            this.modules.moveTo.setOptions(
            {
               path: this.docData.location.path,
               files: this.docData
            })
         }
         
         // show the dialog
         this.modules.moveTo.showDialog();
      },
      
      /**
       * Assign workflow.
       *
       * @method onActionAssignWorkflow
       * @param obj {object} Not used
       */
      onActionAssignWorkflow: function DocumentActions_onActionAssignWorkflow(obj)
      {
         if (!this.modules.assignWorkflow)
         {
            this.modules.assignWorkflow = new Alfresco.module.DoclibWorkflow(this.id + "-workflow").setOptions(
            {
               siteId: this.options.siteId,
               containerId: this.options.containerId,
               path: this.docData.location.path,
               files: this.docData
            });
         }
         else
         {
            this.modules.assignWorkflow.setOptions(
            {
               path: this.docData.location.path,
               files: this.docData
            })
         }
         
         // show the dialog
         this.modules.assignWorkflow.showDialog();
      },
      
      /**
       * Delete Asset.
       *
       * @method onActionDelete
       * @param obj {object} Not used
       */
      onActionDelete: function DocumentActions_onActionDelete(obj)
      {
         var me = this;

         Alfresco.util.PopupManager.displayPrompt(
         {
            text: this._msg("document-actions.delete.confirm", this.docData.fileName),
            buttons: [
            {
               text: this._msg("document-actions.button.delete"),
               handler: function DocumentActions_onActionDelete_delete()
               {
                  this.destroy();
                  me._onActionDeleteConfirm.call(me, this.docData);
               },
               isDefault: true
            },
            {
               text: this._msg("button.cancel"),
               handler: function DocumentActions_onActionDelete_cancel()
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
       * @param obj {object} Not used
       * @private
       */
      _onActionDeleteConfirm: function DocumentActions__onActionDeleteConfirm(record)
      {
         var fileType = this.docData.type;
         var fileName = this.docData.fileName;
         var filePath = this.docData.location.path + "/" + fileName;
         var displayName = this.docData.displayName;
         
         var callbackUrl = Alfresco.constants.URL_PAGECONTEXT + "site/" + this.options.siteId + "/documentlibrary#path=";
         var encodedPath = (YAHOO.env.ua.gecko) ? encodeURIComponent(this.docData.location.path) : this.docData.location.path;
         
         this.modules.actions.genericAction(
         {
            success:
            {
               callback:
               {
                  fn: function DocumentActions_oADC_success(data)
                  {
                     window.location = callbackUrl + encodedPath;
                  }
               }
            },
            failure:
            {
               message: this._msg("document-actions.delete.failure", displayName)
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
               path: this.docData.location.path,
               file: fileName
            }
         });
      },
      
      /**
       * Gets a custom message
       *
       * @method _msg
       * @param messageId {string} The messageId to retrieve
       * @return {string} The custom message
       * @private
       */
      _msg: function DocumentActions__msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, "Alfresco.DocumentActions", Array.prototype.slice.call(arguments).slice(1));
      }
   };
})();
