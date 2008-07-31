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
 * DocumentList Toolbar component.
 * 
 * @namespace Alfresco
 * @class Alfresco.DocListToolbar
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
    * DocListToolbar constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.DocListToolbar} The new DocListToolbar instance
    * @constructor
    */
   Alfresco.DocListToolbar = function(htmlId)
   {
      this.name = "Alfresco.DocListToolbar";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "menu", "container"], this.onComponentsLoaded, this);
      
      // Decoupled event listeners
      YAHOO.Bubbling.on("pathChanged", this.onPathChanged, this);
      YAHOO.Bubbling.on("folderRenamed", this.onPathChanged, this);
      YAHOO.Bubbling.on("filterChanged", this.onFilterChanged, this);
      YAHOO.Bubbling.on("deactivateAllControls", this.onDeactivateAllControls, this);
      YAHOO.Bubbling.on("selectedFilesChanged", this.onSelectedFilesChanged, this);
   
      return this;
   }
   
   Alfresco.DocListToolbar.prototype =
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
         containerId: "documentLibrary",

         /**
          * Number of multi-file uploads before grouping the Activity Post
          *
          * @property groupUploadsAt
          * @type int
          * @default 5
          */
         groupUploadsAt: 5
      },
      
      /**
       * Current path being browsed.
       * 
       * @property currentPath
       * @type string
       */
      currentPath: "",

      /**
       * Current filter to choose toolbar view and populate description.
       * 
       * @property currentFilter
       * @type string
       */
      currentFilter:
      {
         filterId: "",
         filterOwner: "",
         filterData: ""
      },

      /**
       * FileUpload module instance.
       * 
       * @property fileUpload
       * @type Alfresco.module.FileUpload
       */
      fileUpload: null,

      /**
       * Object container for storing YUI widget instances.
       * 
       * @property widgets
       * @type object
       */
      widgets: {},

      /**
       * Object container for storing module instances.
       * 
       * @property modules
       * @type object
       */
      modules: {},

      /**
       * Array of selected states for visible files.
       * 
       * @property selectedFiles
       * @type array
       */
      selectedFiles: [],

      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.DocListToolbar} returns 'this' for method chaining
       */
      setOptions: function DLTB_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },
      
      /**
       * Set messages for this component.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.DocListToolbar} returns 'this' for method chaining
       */
      setMessages: function DLTB_setMessages(obj)
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
      onComponentsLoaded: function DLTB_onComponentsLoaded()
      {
         Event.onContentReady(this.id, this.onReady, this, true);
      },
   
      /**
       * Fired by YUI when parent element is available for scripting.
       * Component initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function DLTB_onReady()
      {
         // Reference to self used by inline functions
         var me = this;
         
         // New Folder button
         this.widgets.newFolder = Alfresco.util.createYUIButton(this, "newFolder-button", this.onNewFolder);
         
         // File Upload button
         this.widgets.fileUpload = Alfresco.util.createYUIButton(this, "fileUpload-button", this.onFileUpload);

         // Selected Items menu button
         this.widgets.selectedItems = Alfresco.util.createYUIButton(this, "selectedItems-button", this.onSelectedItems,
         {
            type: "menu", 
            menu: "selectedItems-menu",
            disabled: true
         });

         // Folder Up Navigation button
         this.widgets.folderUp =  Alfresco.util.createYUIButton(this, "folderUp-button", this.onFolderUp,
         {
            disabled: true
         });

         // DocLib Actions module
         this.modules.actions = new Alfresco.module.DoclibActions();
         
         // Reference to Document List component
         this.modules.docList = Alfresco.util.ComponentManager.findFirst("Alfresco.DocumentList");

         // Finally show the component body here to prevent UI artifacts on YUI button decoration
         Dom.setStyle(this.id + "-body", "visibility", "visible");
      },
      

      /**
       * YUI WIDGET EVENT HANDLERS
       * Handlers for standard events fired from YUI widgets, e.g. "click"
       */

      /**
       * New Folder button click handler
       *
       * @method onNewFolder
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onNewFolder: function DLTB_onNewFolder(e, p_obj)
      {
         var parentFolder = (this.currentPath[0] == "/") ? this.currentPath.substring(1) : this.currentPath;
         
         var actionUrl = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "slingshot/doclib/action/folder/site/{site}/{container}/{path}",
         {
            site: this.options.siteId,
            container: this.options.containerId,
            path: this.currentPath
         });
         
         var doSetupFormsValidation = function DLTB_oNF_doSetupFormsValidation(p_form)
         {
            // Validation
            // Name: mandatory value
            p_form.addValidation(this.id + "-createFolder-name", Alfresco.forms.validation.mandatory, null, "keyup");
            // Name: valid filename
            p_form.addValidation(this.id + "-createFolder-name", Alfresco.forms.validation.nodeName, null, "keyup");
            p_form.setShowSubmitStateDynamically(true, false);
         }
         
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
                        text: this._msg("message.new-folder.success", folder.name)
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
            })
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
            this.fileUpload = Alfresco.module.getFileUploadInstance(); //new Alfresco.module.FileUpload(this.id + "-fileUpload");
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
         }
         this.fileUpload.show(multiUploadConfig);
         Event.preventDefault(e);
      },
      
      /**
       * File Upload complete event handler
       *
       * @method onFileUploadComplete
       * @param complete {object} Object literal containing details of successful and failed uploads
       */
      onFileUploadComplete: function DLTB_onFileUploadComplete(complete)
      {
         var config =
         {
            method: "POST",
            url: Alfresco.constants.PROXY_URI + "slingshot/doclib/activity",
            dataObj:
            {
               site: this.options.siteId,
               container: this.options.containerId,
               browseURL: location.pathname + (location.hash || "")
            },
            successCallback: null,
            successMessage: null,
            failureCallback: null,
            failureMessage: null,
            object: null
         };

         var success = complete.successful.length;
         if (success > 0)
         {
            if (success < this.options.groupUploadsAt)
            {
               // Below cutoff for grouping Activities into one
               var file;
               for (var i = 0; i < success; i++)
               {
                  file = complete.successful[i];
                  config.dataObj.type = "file-added";
                  config.dataObj.fileName = file.fileName;
                  config.dataObj.contentURL = Alfresco.constants.PROXY_URI + "api/node/content/" + file.nodeRef.replace(":/", "") + "/" + encodeURIComponent(file.fileName);
                  config.dataObj.browseURL = location.pathname + "?file=" + config.dataObj.fileName + (location.hash || "");
                  try
                  {
                     Alfresco.util.Ajax.jsonRequest(config);
                  }
                  catch (e)
                  {
                  }
               }
            }
            else
            {
               // grouped into one message
               config.dataObj.type = "files-added";
               config.dataObj.fileCount = success;
               try
               {
                  Alfresco.util.Ajax.jsonRequest(config);
               }
               catch (e)
               {
               }
            }
         }
      },

       /**
        * Selected Items button click handler
        *
        * @method onSelectedItems
        * @param sType {string} Event type, e.g. "click"
        * @param aArgs {array} Arguments array, [0] = DomEvent, [1] = EventTarget
        * @param p_obj {object} Object passed back from subscribe method
        */
      onSelectedItems: function DLTB_onSelectedItems(sType, aArgs, p_obj)
      {
         var domEvent = aArgs[0];
         var eventTarget = aArgs[1];
         
         // Get the function related to the clicked item
         var fn = Alfresco.util.findEventClass(eventTarget);
         if (fn && (typeof this[fn] == "function"))
         {
            this[fn].call(this);
         }
         Event.preventDefault(domEvent);
      },
      
      /**
       * Copy Multiple Assets.
       *
       * @method onActionCopyTo
       * @private
       */
      onActionCopyTo: function DLTB_onActionCopyTo()
      {
         if (!this.modules.docList)
         {
            return;
         }

         var parentFolder = (this.currentPath[0] == "/") ? this.currentPath.substring(1) : this.currentPath;
         var files = this.modules.docList.getSelectedFiles();
         
         if (!this.modules.copyTo)
         {
            this.modules.copyTo = new Alfresco.module.DoclibCopyTo(this.id + "-copyTo").setOptions(
            {
               siteId: this.options.siteId,
               containerId: this.options.containerId,
               path: this.currentPath,
               files: files
            });
         }
         else
         {
            this.modules.copyTo.setOptions(
            {
               path: this.currentPath,
               files: files
            })
         }
         this.modules.copyTo.showDialog();
      },

      /**
       * Move Multiple Assets.
       *
       * @method onActionMoveTo
       * @private
       */
      onActionMoveTo: function DLTB_onActionMoveTo()
      {
         if (!this.modules.docList)
         {
            return;
         }

         var parentFolder = (this.currentPath[0] == "/") ? this.currentPath.substring(1) : this.currentPath;
         var files = this.modules.docList.getSelectedFiles();
         
         if (!this.modules.moveTo)
         {
            this.modules.moveTo = new Alfresco.module.DoclibMoveTo(this.id + "-moveTo").setOptions(
            {
               siteId: this.options.siteId,
               containerId: this.options.containerId,
               path: this.currentPath,
               files: files,
               width: "40em"
            });
         }
         else
         {
            this.modules.moveTo.setOptions(
            {
               path: this.currentPath,
               files: files
            })
         }
         this.modules.moveTo.showDialog();
      },

      /**
       * Delete Multiple Assets.
       *
       * @method onActionDelete
       * @private
       */
      onActionDelete: function DLTB_onActionDelete()
      {
         if (!this.modules.docList)
         {
            return;
         }

         var me = this;
         var files = this.modules.docList.getSelectedFiles();
         
         var fileNames = [];
         for (var i = 0, j = files.length; i < j; i++)
         {
            fileNames.push("<span class=\"" + files[i].type + "\">" + files[i].displayName + "</span>");
         }
         
         var confirmTitle = this._msg("title.multiple-delete.confirm");
         var confirmMsg = this._msg("message.multiple-delete.confirm", files.length);
         confirmMsg += "<div class=\"toolbar-file-list\">" + fileNames.join("") + "</div>";

         Alfresco.util.PopupManager.displayPrompt(
         {
            title: confirmTitle,
            text: Alfresco.util.decodeHTML(confirmMsg),
            noEscape: true,
            modal: true,
            buttons: [
            {
               text: this._msg("button.delete"),
               handler: function DLTB_onActionDelete_delete()
               {
                  this.destroy();
                  me._onActionDeleteConfirm.call(me, files);
               },
               isDefault: true
            },
            {
               text: this._msg("button.cancel"),
               handler: function DLTB_onActionDelete_cancel()
               {
                  this.destroy();
               }
            }]
         });
      },

      /**
       * Delete Multiple Assets confirmed.
       *
       * @method _onActionDeleteConfirm
       * @param files {array} Array containing files to be deleted
       * @private
       */
      _onActionDeleteConfirm: function DLTB__onActionDeleteConfirm(files)
      {
         var multipleFiles = [];
         for (var i = 0, j = files.length; i < j; i++)
         {
            multipleFiles.push(files[i].nodeRef);
         }
         
         // Success callback function
         var fnSuccess = function DLTB__oADC_success(data, files)
         {
            var result;
            var successCount = 0;

            // Did the operation succeed?
            if (!data.json.overallSuccess)
            {
               Alfresco.util.PopupManager.displayMessage(
               {
                  text: this._msg("message.multiple-delete.failure")
               });
               return;
            }

            YAHOO.Bubbling.fire("filesDeleted");

            for (var i = 0, j = data.json.totalResults; i < j; i++)
            {
               result = data.json.results[i];
               
               if (result.success)
               {
                  YAHOO.Bubbling.fire(result.type == "folder" ? "folderDeleted" : "fileDeleted",
                  {
                     multiple: true,
                     nodeRef: result.nodeRef
                  });
               }
            }

            Alfresco.util.PopupManager.displayMessage(
            {
               text: this._msg("message.multiple-delete.success", successCount)
            });
         }
         
         // Construct the data object for the genericAction call
         this.modules.actions.genericAction(
         {
            success:
            {
               callback:
               {
                  fn: fnSuccess,
                  scope: this,
                  obj: files
               }
            },
            failure:
            {
               message: this._msg("message.multiple-delete.failure")
            },
            webscript:
            {
               name: "files",
               method: Alfresco.util.Ajax.DELETE
            },
            wait:
            {
               message: this._msg("message.multiple-delete.please-wait")
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
      },

      onActionDeselectAll: function DLTB_onActionDeselectAll()
      {
         if (this.modules.docList)
         {
            this.modules.docList.selectFiles("selectNone");
         }
      },


      /**
       * Folder Up Navigate button click handler
       *
       * @method onFolderUp
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onFolderUp: function DLTB_onFolderUp(e, p_obj)
      {
         var newPath = this.currentPath.substring(0, this.currentPath.lastIndexOf("/"));

         YAHOO.Bubbling.fire("pathChanged",
         {
            path: newPath
         });
         Event.preventDefault(e);
      },
      

      /**
       * BUBBLING LIBRARY EVENT HANDLERS FOR PAGE EVENTS
       * Disconnected event handlers for inter-component event notification
       */

      /**
       * Path Changed event handler
       *
       * @method onPathChanged
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onPathChanged: function DLTB_onPathChanged(layer, args)
      {
         var obj = args[1];
         if ((obj !== null) && (obj.path !== null))
         {
            // Should be a path in the arguments
            this.currentPath = obj.path;
            this._generateBreadcrumb();
            this._generateRSSFeedUrl();
            
            // Enable/disable the Folder Up button
            var paths = this.currentPath.split("/");
            this.widgets["folderUp"].set("disabled", paths.length < 2);
         }
      },

      /**
       * Filter Changed event handler
       *
       * @method onFilterChanged
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onFilterChanged: function DLTB_onFilterChanged(layer, args)
      {
         var obj = args[1];
         if ((obj !== null) && (obj.filterId !== null))
         {
            if (this.currentFilter.filterOwner != obj.filterOwner)
            {
               var owner = obj.filterOwner.split(".")[1];
               // Obtain array of DIVs we might want to hide
               var divs = YAHOO.util.Selector.query('div.hideable', Dom.get(this.id + "-body"));
               var div;
               for (var i = 0, j = divs.length; i < j; i++)
               {
                  div = divs[i];
                  if (Dom.hasClass(div, owner))
                  {
                     Dom.removeClass(div, "toolbar-hidden");
                  }
                  else
                  {
                     Dom.addClass(div, "toolbar-hidden");
                  }
               }
            }
            
            this.currentFilter = 
            {
               filterId: obj.filterId,
               filterOwner: obj.filterOwner,
               filterData: obj.filterData
            }
            
            this._generateDescription();
            this._generateRSSFeedUrl();
         }
      },

      /**
       * Deactivate All Controls event handler
       *
       * @method onDeactivateAllControls
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onDeactivateAllControls: function DLTB_onDeactivateAllControls(layer, args)
      {
         for (widget in this.widgets)
         {
            this.widgets[widget].set("disabled", true)
         }
      },

      /**
       * Selected Files Changed event handler.
       * Determines whether to enable or disable the multi-file action drop-down
       *
       * @method onSelectedFilesChanged
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onSelectedFilesChanged: function DLTB_onSelectedFilesChanged(layer, args)
      {
         if (this.modules.docList)
         {
            var files = this.modules.docList.getSelectedFiles();
            this.widgets["selectedItems"].set("disabled", (files.length == 0))
         }
      },
   
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
         // Clone the array and re-use the root node name from the DocListTree
         var displayPaths = paths.concat();
         displayPaths[0] = Alfresco.util.message("node.root", "Alfresco.DocListTree");
         
         var eBreadcrumb = new Element(divBC);
         for (var i = 0, j = paths.length; i < j; ++i)
         {
            var eCrumb = new Element(document.createElement("span"));
            eCrumb.addClass("crumb");

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
                  innerHTML: " &gt; "
               }));
            }
            eBreadcrumb.appendChild(eCrumb);
         }
      },

      /**
       * Generates the HTML mark-up for the description from the currentFilter
       *
       * @method _generateDescription
       * @private
       */
      _generateDescription: function DLTB__generateDescription()
      {
         var divDesc = Dom.get(this.id + "-description");
         if (divDesc === null)
         {
            return;
         }
         divDesc.innerHTML = "";
         
         var eDivDesc = new Element(divDesc);

         var eDescMsg = new Element(document.createElement("span"),
         {
            innerHTML: this._msg("description." + this.currentFilter.filterId, this.currentFilter.filterData)
         });
         eDescMsg.addClass("message");

         var eDescMore = new Element(document.createElement("span"),
         {
            innerHTML: this._msg("description." + this.currentFilter.filterId + ".more", this.currentFilter.filterData)
         });
         eDescMore.addClass("more");
         
         eDescMsg.appendChild(eDescMore);
         eDivDesc.appendChild(eDescMsg);
      },
      
      /**
       * Generates the HTML mark-up for the RSS feed link
       *
       * @method _generateRSSFeedUrl
       * @private
       */
      _generateRSSFeedUrl: function DLTB__generateRSSFeedUrl()
      {
         var divFeed = Dom.get(this.id + "-rssFeed");
         if (divFeed && this.modules.docList)
         {
            var params = YAHOO.lang.substitute("{type}/site/{site}/{container}{path}",
            {
               type: this.modules.docList.options.showFolders ? "all" : "documents",
               site: encodeURIComponent(this.options.siteId),
               container: encodeURIComponent(this.options.containerId),
               path: encodeURI(this.currentPath)
            });

            params += "?filter=" + encodeURIComponent(this.currentFilter.filterId);
            if (this.currentFilter.filterData)
            {
               params += "&filterData=" + encodeURIComponent(this.currentFilter.filterData);             
            }
            params += "&format=rss";
            
            var url = Alfresco.constants.PROXY_URI + "slingshot/doclib/doclist/" + params;
            divFeed.innerHTML = '<a href="' + url + '">' + this._msg("link.rss-feed") + '</a>';
         }
         
      },
      

      /**
       * Gets a custom message
       *
       * @method _msg
       * @param messageId {string} The messageId to retrieve
       * @return {string} The custom message
       * @private
       */
      _msg: function DLTB__msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, "Alfresco.DocListToolbar", Array.prototype.slice.call(arguments).slice(1));
      }
   
   };
})();
