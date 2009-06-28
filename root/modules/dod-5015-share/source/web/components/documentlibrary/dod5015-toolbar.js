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
 * DocumentList Toolbar component.
 * 
 * @namespace Alfresco
 * @class Alfresco.RecordsDocListToolbar
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
    * Preferences
    */
   var PREFERENCES_ROOT = "org.alfresco.share.documentList",
      PREF_HIDE_NAVBAR = PREFERENCES_ROOT + ".hideNavBar";
   
   /**
    * RecordsDocListToolbar constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.RecordsDocListToolbar} The new RecordsDocListToolbar instance
    * @constructor
    */
   Alfresco.RecordsDocListToolbar = function(htmlId)
   {
      return Alfresco.RecordsDocListToolbar.superclass.constructor.call(this, htmlId);
   };
   
   YAHOO.extend(Alfresco.RecordsDocListToolbar, Alfresco.DocListToolbar,
   {
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function DLTB_onReady()
      {
         // New Series button: user needs "create,series" access
         this.widgets.newSeries = Alfresco.util.createYUIButton(this, "newSeries-button", this.onNewContainer,
         {
            disabled: true,
            value: "create,new-series"
         });

         // New Category button: user needs "create,category" access
         this.widgets.newCategory = Alfresco.util.createYUIButton(this, "newCategory-button", this.onNewContainer,
         {
            disabled: true,
            value: "create,new-category"
         });

         // New Folder button: user needs "create,folder" access
         this.widgets.newFolder = Alfresco.util.createYUIButton(this, "newFolder-button", this.onNewContainer,
         {
            disabled: true,
            value: "create,new-folder"
         });
         
         // File Upload button: user needs "file" access
         this.widgets.fileUpload = Alfresco.util.createYUIButton(this, "fileUpload-button", this.onFileUpload,
         {
            disabled: true,
            value: "file"
         });

         // Selected Items menu button
         this.widgets.selectedItems = Alfresco.util.createYUIButton(this, "selectedItems-button", this.onSelectedItems,
         {
            type: "menu", 
            menu: "selectedItems-menu",
            disabled: true
         });
         // Clear the lazyLoad flag and fire init event to get menu rendered into the DOM
         this.widgets.selectedItems.getMenu().lazyLoad = false;
         this.widgets.selectedItems.getMenu().initEvent.fire();
         this.widgets.selectedItems.getMenu().render();

         // Customize button
         this.widgets.customize = Alfresco.util.createYUIButton(this, "customize-button", this.onCustomize);

         // Hide/Show NavBar button
         this.widgets.hideNavBar = Alfresco.util.createYUIButton(this, "hideNavBar-button", this.onHideNavBar);
         this.widgets.hideNavBar.set("label", this._msg(this.options.hideNavBar ? "button.navbar.show" : "button.navbar.hide"));
         Dom.setStyle(this.id + "-navBar", "display", this.options.hideNavBar ? "none" : "block");
         
         // Folder Up Navigation button
         this.widgets.folderUp =  Alfresco.util.createYUIButton(this, "folderUp-button", this.onFolderUp,
         {
            disabled: true
         });

         // DocLib Actions module
         this.modules.actions = new Alfresco.module.DoclibActions();
         
         // Reference to Document List component
         this.modules.docList = Alfresco.util.ComponentManager.findFirst("Alfresco.DocumentList");

         // Preferences service
         this.services.preferences = new Alfresco.service.Preferences();

         // Finally show the component body here to prevent UI artifacts on YUI button decoration
         Dom.setStyle(this.id + "-body", "visibility", "visible");
      },
      

      /**
       * YUI WIDGET EVENT HANDLERS
       * Handlers for standard events fired from YUI widgets, e.g. "click"
       */

      /**
       * New Container button click handler
       *
       * Look at the event source to work out what type of container to create
       *
       * @method onNewContainer
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onNewContainer: function DLTB_onNewContainer(e, p_obj)
      {
         var actionUrl = Alfresco.constants.PROXY_URI + $combine("slingshot/doclib/dod5015/action/folder/site", this.options.siteId, this.options.containerId, this.currentPath);

         var folderType = p_obj.get("name"),
            label = "label.new-" + p_obj.get("name"),
            msgTitle = this._msg(label + ".title"),
            msgHeader = this._msg(label + ".header");

         // Inject forms validation
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

         // Intercept before dialog show
         var doBeforeDialogShow = function DLTB_oNF_doBeforeDialogShow(p_form, p_dialog)
         {
            Dom.get(p_dialog.id + "-dialogTitle").innerHTML = msgTitle;
            Dom.get(p_dialog.id + "-dialogHeader").innerHTML = msgHeader;
         };
         
         // Intercept before ajax request
         var doBeforeAjaxRequest = function DLTB_oNF_doBeforeAjaxRequest(p_config, p_obj)
         {
            p_config.dataObj.type = p_obj.folderType;
            return true;
         };

         if (!this.modules.createFolder)
         {
            this.modules.createFolder = new Alfresco.module.SimpleDialog(this.id + "-createFolder");
         }
         this.modules.createFolder.setOptions(
         {
            width: "30em",
            templateUrl: Alfresco.constants.URL_SERVICECONTEXT + "modules/documentlibrary/create-folder",
            actionUrl: actionUrl,
            doSetupFormsValidation:
            {
               fn: doSetupFormsValidation,
               scope: this
            },
            doBeforeDialogShow:
            {
               fn: doBeforeDialogShow,
               scope: this
            },
            doBeforeAjaxRequest:
            {
               fn: doBeforeAjaxRequest,
               obj:
               {
                  folderType: folderType
               },
               scope: this
            },
            clearForm: true,
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
                     text: this._msg("message.new-folder.success", folder.name)
                  });
               },
               scope: this
            }
         });
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
         var success = complete.successful.length, activityData, file;
         if (success > 0)
         {
            if (success < this.options.groupActivitiesAt)
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
                  this.modules.actions.postActivity(this.options.siteId, "file-added", "document-details", activityData);
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
               this.modules.actions.postActivity(this.options.siteId, "files-added", "documentlibrary", activityData);
            }
         }
      },


      /**
       * BUBBLING LIBRARY EVENT HANDLERS FOR PAGE EVENTS
       * Disconnected event handlers for inter-component event notification
       */

      /**
       * PRIVATE FUNCTIONS
       */

      /**
       * Generates the HTML mark-up for the breadcrumb from the currentPath
       *
       * @method _generateBreadcrumb
       * @private
       */
      _generateBreadcrumb: function DLTB__generateBreadcrumb()
      {
         var divBC = Dom.get(this.id + "-breadcrumb");
         if (divBC === null)
         {
            return;
         }
         divBC.innerHTML = "";
         
         var paths = this.currentPath.split("/");
         // Check for root path special case
         if (this.currentPath === "/")
         {
            paths = ["/"];
         }
         // Clone the array and re-use the root node name from the DocListTree
         var displayPaths = paths.concat();
         displayPaths[0] = Alfresco.util.message("node.root", "Alfresco.DocListTree");
         
         var eBreadcrumb = new Element(divBC);
         for (var i = 0, j = paths.length; i < j; ++i)
         {
            var eCrumb = new Element(document.createElement("span"));
            eCrumb.addClass("crumb");
            if (i === 0)
            {
               eCrumb.addClass("first");
            }
            else
            {
               eCrumb.addClass("folder");
            }

            // Last crumb shouldn't be rendered as a link
            if (j - i < 2)
            {
               eCrumb.set("innerHTML", displayPaths[i]);
            }
            else
            {
               var eLink = new Element(document.createElement("a"),
               {
                  href: "",
                  innerHTML: displayPaths[i]
               });
               var newPath = paths.slice(0, i+1).join("/");
               eLink.on("click", function DLTB__gB_click(e, path)
               {
                  YAHOO.Bubbling.fire("pathChanged",
                  {
                     path: path
                  });
                  Event.stopEvent(e);
               }, newPath);
               eCrumb.appendChild(eLink);
               eCrumb.appendChild(new Element(document.createElement("span"),
               {
                  innerHTML: "&gt;",
                  className: "separator"
               }));
            }
            eBreadcrumb.appendChild(eCrumb);
         }
      }
   });
})();