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
 * Document Library "Copy To" module for Document Library.
 * 
 * @namespace Alfresco.module
 * @class Alfresco.module.DoclibCopyTo
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

   Alfresco.module.DoclibCopyTo = function(htmlId)
   {
      Alfresco.module.DoclibCopyTo.superclass.constructor.call(this, "Alfresco.module.DoclibCopyTo", htmlId, ["button", "container", "connection", "json", "treeview"]);

      // Initialise prototype properties
      this.pathsToExpand = [];
      this.containers = {};

      // Decoupled event listeners
      if (htmlId != "null")
      {
         YAHOO.Bubbling.on("copyTo-siteChanged", this.onSiteChanged, this);
         YAHOO.Bubbling.on("copyTo-containerChanged", this.onContainerChanged, this);
      }

      return this;
   };

   /**
   * Alias to self
   */
   var DLCT = Alfresco.module.DoclibCopyTo;
      
   YAHOO.lang.augmentObject(DLCT,
   {
      /**
       * Copy To dialog "Site mode" constant.
       *
       * @property MODE_SITE
       * @type integer
       * @final
       * @default 0
       */
      MODE_SITE: 0,

      /**
       * Copy To dialog "Repository mode" constant.
       *
       * @property MODE_REPOSITORY
       * @type integer
       * @final
       * @default 1
       */
      MODE_REPOSITORY: 1
   });

   YAHOO.extend(Alfresco.module.DoclibCopyTo, Alfresco.component.Base,
   {
      /**
       * Object container for initialization options
       */
      options:
      {
         /**
          * Current siteId for site mode.
          * 
          * @property siteId
          * @type string
          */
         siteId: "",

         /**
          * ContainerId representing root container in site mode
          *
          * @property containerId
          * @type string
          * @default "documentLibrary"
          */
         containerId: "documentLibrary",

         /**
          * ContainerType representing root container in site mode
          *
          * @property containerType
          * @type string
          * @default "cm:folder"
          */
         containerType: "cm:folder",

         /**
          * NodeRef representing root container in repository mode
          *
          * @property nodeRef
          * @type string
          * @default "alfresco://company/home"
          */
         nodeRef: "alfresco://company/home",

         /**
          * Initial path to expand on module load
          *
          * @property path
          * @type string
          * @default ""
          */
         path: "",

         /**
          * Width for the dialog
          *
          * @property: width
          * @type: integer
          * @default: 40em
          */
         width: "60em",
         
         /**
          * Files to copy
          *
          * @property: files
          * @type: object
          * @default: null
          */
         files: null,

         /**
          * Dialog mode: site or repository
          *
          * @property: mode
          * @type: integer
          * @default: Alfresco.modules.DoclibCopyTo.MODE_SITES
          */
         mode: DLCT.MODE_SITE
      },
      
      /**
       * Container element for template in DOM.
       * 
       * @property containerDiv
       * @type DOMElement
       */
      containerDiv: null,

      /**
       * Paths we have to expand as a result of a deep navigation event.
       * 
       * @property pathsToExpand
       * @type array
       */
      pathsToExpand: null,

      /**
       * Selected tree node.
       * 
       * @property selectedNode
       * @type {YAHOO.widget.Node}
       */
      selectedNode: null,

      /**
       * Current list of containers.
       * 
       * @property containers
       * @type {object}
       */
      containers: null,

      /**
       * Main entry point
       * @method showDialog
       */
      showDialog: function DLCT_showDialog()
      {
         // DocLib Actions module
         if (!this.modules.actions)
         {
            this.modules.actions = new Alfresco.module.DoclibActions();
         }
         
         if (!this.containerDiv)
         {
            // Load the UI template from the server
            Alfresco.util.Ajax.request(
            {
               url: Alfresco.constants.URL_SERVICECONTEXT + "modules/documentlibrary/copy-to",
               dataObj:
               {
                  htmlid: this.id
               },
               successCallback:
               {
                  fn: this.onTemplateLoaded,
                  scope: this
               },
               failureMessage: "Could not load Document Library Copy-To template",
               execScripts: true
            });
         }
         else
         {
            // Show the dialog
            this._showDialog();
         }
      },
      
      /**
       * Event callback when dialog template has been loaded
       *
       * @method onTemplateLoaded
       * @param response {object} Server response from load template XHR request
       */
      onTemplateLoaded: function DLCT_onTemplateLoaded(response)
      {
         // Reference to self - used in inline functions
         var me = this;
         
         // Inject the template from the XHR request into a new DIV element
         this.containerDiv = document.createElement("div");
         this.containerDiv.setAttribute("style", "display:none");
         this.containerDiv.innerHTML = response.serverResponse.responseText;

         // The panel is created from the HTML returned in the XHR request, not the container
         var dialogDiv = Dom.getFirstChild(this.containerDiv);
         
         // Create and render the YUI dialog
         this.widgets.dialog = Alfresco.util.createYUIPanel(dialogDiv,
         {
            width: this.options.width
         });
         
         // OK button
         this.widgets.okButton = Alfresco.util.createYUIButton(this, "ok", this.onOK);

         // Cancel button
         this.widgets.cancelButton = Alfresco.util.createYUIButton(this, "cancel", this.onCancel);

         // Mode buttons
         var modeButtons = new YAHOO.widget.ButtonGroup(this.id + "-modeGroup");
         modeButtons.on("checkedButtonChange", this.onModeChange, this.widgets.modeButtons, this);
         this.widgets.modeButtons = modeButtons;

         // Make user enter-key-strokes also trigger a change
         var buttons = this.widgets.modeButtons.getButtons();
         for (var i = 0; i < buttons.length; i++)
         {
            var bi = i;
            buttons[i].addListener("keydown", function(e)
            {
               if (YAHOO.util.KeyListener.KEY.ENTER == e.keyCode)
               {
                  modeButtons.check(bi);
               }
            });
         }
         
         /**
          * Dynamically loads TreeView nodes.
          * This MUST be inline in order to have access to the Alfresco.DocListCopyTo class.
          * @method fnLoadNodeData
          * @param node {object} Parent node
          * @param fnLoadComplete {function} Expanding node's callback function
          */
         this.fnLoadNodeData = function DLCT_oR_fnLoadNodeData(node, fnLoadComplete)
         {
            // Get the path this node refers to
            var nodePath = node.data.path;

            // Prepare URI for XHR data request
            var uri = me._buildTreeNodeUrl.call(me, nodePath);

            // Prepare the XHR callback object
            var callback =
            {
               success: function DLCT_lND_success(oResponse)
               {
                  var results = eval("(" + oResponse.responseText + ")");

                  if (results.items)
                  {
                     for (var i = 0, j = results.items.length; i < j; i++)
                     {
                        var item = results.items[i];
                        var tempNode = new YAHOO.widget.TextNode(
                        {
                           label: $html(item.name),
                           path: $combine(nodePath, item.name),
                           nodeRef: item.nodeRef,
                           description: item.description,
                           userAccess: item.userAccess,
                           style: item.userAccess.create ? "" : "no-permission"
                        }, node, false);

                        if (!item.hasChildren)
                        {
                           tempNode.isLeaf = true;
                        }
                     }
                  }
                  
                  /**
                  * Execute the node's loadComplete callback method which comes in via the argument
                  * in the response object
                  */
                  oResponse.argument.fnLoadComplete();
               },

               // If the XHR call is not successful, fire the TreeView callback anyway
               failure: function DLCT_lND_failure(oResponse)
               {
                  try
                  {
                     var response = YAHOO.lang.JSON.parse(oResponse.responseText);
                     
                     // Show the error in place of the root node
                     var rootNode = this.widgets.treeview.getRoot();
                     var docNode = rootNode.children[0];
                     docNode.isLoading = false;
                     docNode.isLeaf = true;
                     docNode.label = response.message;
                     docNode.labelStyle = "ygtverror";
                     rootNode.refresh();
                  }
                  catch(e)
                  {
                  }
               },
               
               // Callback function scope
               scope: me,

               // XHR response argument information
               argument:
               {
                  "node": node,
                  "fnLoadComplete": fnLoadComplete
               },

               // Timeout -- abort the transaction after 7 seconds
               timeout: 7000
            };

            // Make the XHR call using Connection Manager's asyncRequest method
            YAHOO.util.Connect.asyncRequest("GET", uri, callback);
         };

         // Show the dialog
         this._showDialog();
      },
      
      /**
       * Internal show dialog function
       * @method _showDialog
       */
      _showDialog: function DLCT__showDialog()
      {
         // Must have list of files
         if (this.options.files == null)
         {
            Alfresco.util.PopupManager.displayMessage(
            {
               text: this.msg("message.no-files")
            });
            return;
         }

         // Enable buttons
         this.widgets.okButton.set("disabled", false);
         this.widgets.cancelButton.set("disabled", false);

         // Dialog title
         var titleDiv = Dom.get(this.id + "-title");
         if (YAHOO.lang.isArray(this.options.files))
         {
            titleDiv.innerHTML = this.msg("title.multi", this.options.files.length)
         }
         else
         {
            var fileSpan = '<span class="light">' + $html(this.options.files.displayName) + '</span>';
            titleDiv.innerHTML = this.msg("title.single", fileSpan)
         }

         // Dialog mode
         this.setMode(this.options.mode, true);
         
         // Register the ESC key to close the dialog
         var escapeListener = new YAHOO.util.KeyListener(document,
         {
            keys: YAHOO.util.KeyListener.KEY.ESCAPE
         },
         {
            fn: function(id, keyEvent)
            {
               this.onCancel();
            },
            scope: this,
            correctScope: true
         });
         escapeListener.enable();

         // Show the dialog
         this.widgets.dialog.show();
      },
      
      /**
       * Public function to set current dialog mode
       *  
       * @method setMode
       * @param mode {integer} New dialog mode constant
       */
      setMode: function DLCT_setMode(mode)
      {
         this.options.mode = mode;
         
         if (mode == DLCT.MODE_SITE)
         {
            Dom.removeClass(this.id + "-wrapper", "repository-mode");
            this._populateSitePicker();
         }
         else
         {
            Dom.addClass(this.id + "-wrapper", "repository-mode");
            // Build the TreeView widget
            this._buildTree(this.options.nodeRef);
         }
      },


      /**
       * BUBBLING LIBRARY EVENT HANDLERS
       * Disconnected event handlers for event notification
       */

      /**
       * Site Changed event handler
       *
       * @method onSiteChanged
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onSiteChanged: function DLCT_onSiteChanged(layer, args)
      {
         var obj = args[1];
         if (obj !== null)
         {
            // Should be a site in the arguments
            if (obj.site !== null)
            {
               this.options.siteId = obj.site;
               this._populateContainerPicker();
               var sites = YAHOO.util.Selector.query("a", this.id + "-sitePicker"), site, i, j,
                  picker = Dom.get(this.id + "-sitePicker");

               for (i = 0, j = sites.length; i < j; i++)
               {
                  site = sites[i];
                  if (site.getAttribute("rel") == obj.site)
                  {
                     Dom.addClass(site, "selected");
                     if (obj.scrollTo)
                     {
                        picker.scrollTop = Dom.getY(site) - Dom.getY(picker);
                     }
                  }
                  else
                  {
                     Dom.removeClass(site, "selected");
                  }
               }
            }
         }
      },

      /**
       * Container Changed event handler
       *
       * @method onContainerChanged
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onContainerChanged: function DLCT_onContainerChanged(layer, args)
      {
         var obj = args[1];
         if (obj !== null)
         {
            // Should be a container in the arguments
            if (obj.container !== null)
            {
               this.options.containerId = obj.container;
               var container = this.containers[obj.container];
               this.options.containerType = container.type;
               this._buildTree("");
               // Kick-off navigation to current path
               this.onPathChanged(this.options.path);
               var containers = YAHOO.util.Selector.query("a", this.id + "-containerPicker"), container, i, j,
                  picker = Dom.get(this.id + "-containerPicker");
               
               for (i = 0, j = containers.length; i < j; i++)
               {
                  container = containers[i];
                  if (container.getAttribute("rel") == obj.container)
                  {
                     Dom.addClass(container, "selected");
                     if (obj.scrollTo)
                     {
                        picker.scrollTop = Dom.getY(container) - Dom.getY(picker);
                     }
                  }
                  else
                  {
                     Dom.removeClass(container, "selected");
                  }
               }
            }
         }
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
      onOK: function DLCT_onOK(e, p_obj)
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
         var fnSuccess = function DLCT__onOK_success(p_data)
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
         }

         // Failure callback function
         var fnFailure = function DLCT__onOK_failure(p_data)
         {
            this.widgets.dialog.hide();

            Alfresco.util.PopupManager.displayMessage(
            {
               text: this.msg("message.failure")
            });
         }

         var webscriptName;
         if (this.options.mode == DLCT.MODE_SITE)
         {
            webscriptName = "copy-to/site/{site}/{container}{path}"
            // Parameters are site, container-based
            params =
            {
               site: this.options.siteId,
               container: this.options.containerId,
               path: this.selectedNode.data.path
            }
         }
         else
         {
            webscriptName = "copy-to/node/{nodeRef}/{path}"
            // Parameters are nodeRef-based
            params =
            {
               nodeRef: this.options.nodeRef.replace(":/", ""),
               path: this.selectedNode.data.path
            }
         }
         
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
               params: params
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
       * Dialog Cancel button event handler
       *
       * @method onCancel
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onCancel: function DLCT_onCancel(e, p_obj)
      {
         // Lose the dialog
         this.widgets.dialog.hide();
      },

      /**
       * Mode change buttongroup event handler
       *
       * @method onModeChange
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onModeChange: function DLCT_onModeChange(e, p_obj)
      {
         var mode = e.newValue.get("name");
         this.setMode(mode == "site" ? DLCT.MODE_SITE : DLCT.MODE_REPOSITORY);
      },
      
      /**
       * Fired by YUI TreeView when a node has finished expanding
       * @method onExpandComplete
       * @param oNode {YAHOO.widget.Node} the node recently expanded
       */
      onExpandComplete: function DLCT_onExpandComplete(oNode)
      {
         Alfresco.logger.debug("DLCT_onExpandComplete");

         // Make sure the tree's DOM has been updated
         this.widgets.treeview.render();
         // Redrawing the tree will clear the highlight
         this._showHighlight(true);
         
         if (this.pathsToExpand.length > 0)
         {
            var node = this.widgets.treeview.getNodeByProperty("path", this.pathsToExpand.shift());
            if (node !== null)
            {
               var el = node.getContentEl(),
                  container = Dom.get(this.id + "-treeview");
               
               container.scrollTop = Dom.getY(el) - (container.scrollHeight / 3);

               if (node.data.path == this.currentPath)
               {
                  this._updateSelectedNode(node);
               }
               node.expand();
            }
         }
      },

      /**
       * Fired by YUI TreeView when a node label is clicked
       * @method onNodeClicked
       * @param args.event {HTML Event} the event object
       * @param args.node {YAHOO.widget.Node} the node clicked
       * @return allowExpand {boolean} allow or disallow node expansion
       */
      onNodeClicked: function DLCT_onNodeClicked(args)
      {
         Alfresco.logger.debug("DLCT_onNodeClicked");

         var node = args.node,
            userAccess = node.data.userAccess;
         
         if ((userAccess && userAccess.create) || (node.data.nodeRef == "") || (node.data.nodeRef == "alfresco://company/home"))
         {
            this.onPathChanged(node.data.path);
            this._updateSelectedNode(node);
         }
         return false;
      },

      
      /**
       * Update tree when the path has changed
       * @method onPathChanged
       * @param path {string} new path
       */
      onPathChanged: function DLCT_onPathChanged(path)
      {
         Alfresco.logger.debug("DLCT_onPathChanged");

         // ensure path starts with leading slash if not the root node
         if (path.charAt(0) != "/")
         {
            path = "/" + path;
         }
         this.currentPath = path;
         
         // Search the tree to see if this path's node is expanded
         var node = this.widgets.treeview.getNodeByProperty("path", path);
         if (node !== null)
         {
            // Node found
            this._updateSelectedNode(node);
            node.expand();
            while (node.parent !== null)
            {
               node = node.parent;
               node.expand();
            }
            return;
         }
         
         /**
          * The path's node hasn't been loaded into the tree. Create a stack
          * of parent paths that we need to expand one-by-one in order to
          * eventually display the current path's node
          */
         var paths = path.split("/"),
            expandPath = "/";
         // Check for root path special case
         if (path === "/")
         {
            paths = [""];
         }
         this.pathsToExpand = [];
         
         for (var i = 0, j = paths.length; i < j; i++)
         {
            // Push the path onto the list of paths to be expanded
            expandPath = $combine(expandPath, paths[i]);
            this.pathsToExpand.push(expandPath);
         }

         // Kick off the expansion process by expanding the first unexpanded path
         do
         {
            node = this.widgets.treeview.getNodeByProperty("path", this.pathsToExpand.shift());
         } while (this.pathsToExpand.length > 0 && node.expanded);
         
         if (node !== null)
         {
            node.expand();
         }
      },
      

      /**
       * PRIVATE FUNCTIONS
       */

      /**
       * Creates the Site Picker control.
       * @method _populateSitePicker
       * @private
       */
      _populateSitePicker: function DLCT__populateSitePicker()
      {
         var sitePicker = Dom.get(this.id + "-sitePicker");
         sitePicker.innerHTML = "";
         
         var fnSuccess = function DLCT__pSP_fnSuccess(response, sitePicker)
         {
            var sites = response.json, element, site, onclick, i, j;
            
            for (i = 0, j = sites.length; i < j; i++)
            {
               site = sites[i];
               element = document.createElement("div");
               if (i == j - 1)
               {
                  Dom.addClass(element, "last");
               }
               onclick = "YAHOO.Bubbling.fire('copyTo-siteChanged', {site: '" + site.shortName.replace(/[']/g, "\\'") + "'}); return false;";
               element.innerHTML = '<a rel="' + site.shortName + '" href="#" onclick="' + onclick + '"><h4>' + $html(site.title) + '</h4>' + '<span>' + $html(site.description) + '</span></a>';
               sitePicker.appendChild(element);
            }
            
            // Select current site
            YAHOO.Bubbling.fire("copyTo-siteChanged",
            {
               site: this.options.siteId,
               scrollTo: true
            });
         }
         
         var config =
         {
            url: Alfresco.constants.PROXY_URI + "api/sites",
            responseContentType: Alfresco.util.Ajax.JSON,
            successCallback:
            {
               fn: fnSuccess,
               scope: this,
               obj: sitePicker
            },
            failureCallback: null
         }
         
         Alfresco.util.Ajax.request(config);
      },

      /**
       * Creates the Container Picker control.
       * @method _populateContainerPicker
       * @private
       */
      _populateContainerPicker: function DLCT__populateContainerPicker()
      {
         var containerPicker = Dom.get(this.id + "-containerPicker");
         containerPicker.innerHTML = "";
         
         var fnSuccess = function DLCT__pSP_fnSuccess(response, containerPicker)
         {
            var containers = response.json.containers, element, container, onclick, i, j;
            this.containers = {};
            
            for (i = 0, j = containers.length; i < j; i++)
            {
               container = containers[i];
               this.containers[container.name] = container;
               element = document.createElement("div");
               if (i == j - 1)
               {
                  Dom.addClass(element, "last");
               }
               onclick = "YAHOO.Bubbling.fire('copyTo-containerChanged', {container: '" + container.name.replace(/[']/g, "\\'") + "'}); return false;";
               if (container.description == "")
               {
                  container.description = this.msg("default.container.description");
               }
               element.innerHTML = '<a rel="' + container.name + '" href="#" onclick="' + onclick + '"><h4>' + container.name + '</h4>' + '<span>' + container.description + '</span></a>';
               containerPicker.appendChild(element);
            }

            // Select current container
            YAHOO.Bubbling.fire("copyTo-containerChanged",
            {
               container: this.options.containerId,
               scrollTo: true
            });
         }
         
         var config =
         {
            url: Alfresco.constants.PROXY_URI + "slingshot/doclib/containers/" + this.options.siteId,
            responseContentType: Alfresco.util.Ajax.JSON,
            successCallback:
            {
               fn: fnSuccess,
               scope: this,
               obj: containerPicker
            },
            failureCallback: null
         }
         
         Alfresco.util.Ajax.request(config);
      },

      /**
       * Creates the TreeView control and renders it to the parent element.
       * @method _buildTree
       * @param p_rootNodeRef {string} NodeRef of root node for this tree
       * @private
       */
      _buildTree: function DLCT__buildTree(p_rootNodeRef)
      {
         Alfresco.logger.debug("DLCT__buildTree");

         // Create a new tree
         var tree = new YAHOO.widget.TreeView(this.id + "-treeview");
         this.widgets.treeview = tree;

         // Having both focus and highlight are just confusing (YUI 2.7.0 addition)
         YAHOO.widget.TreeView.FOCUS_CLASS_NAME = "";

         // Turn dynamic loading on for entire tree
         tree.setDynamicLoad(this.fnLoadNodeData);

         var rootLabel = (this.options.mode == DLCT.MODE_SITE ? (this.options.containerType == "dod:filePlan" ? "node.root-filePlan" : "node.root-sites") : "node.root-repository" )

         // Add default top-level node
         var tempNode = new YAHOO.widget.TextNode(
         {
            label: this.msg(rootLabel),
            path: "/",
            nodeRef: p_rootNodeRef
         }, tree.getRoot(), false);

         // Register tree-level listeners
         tree.subscribe("clickEvent", this.onNodeClicked, this, true);
         tree.subscribe("expandComplete", this.onExpandComplete, this, true);

         // Render tree with this one top-level node
         tree.render();
      },

      /**
       * Highlights the currently selected node.
       * @method _showHighlight
       * @param isVisible {boolean} Whether the highlight is visible or not
       * @private
       */
      _showHighlight: function DLCT__showHighlight(isVisible)
      {
         Alfresco.logger.debug("DLCT__showHighlight");

         if (this.selectedNode !== null)
         {
            if (isVisible)
            {
               Dom.addClass(this.selectedNode.getEl(), "selected");
            }
            else
            {
               Dom.removeClass(this.selectedNode.getEl(), "selected");
            }
         }
      },
      
      /**
       * Updates the currently selected node.
       * @method _updateSelectedNode
       * @param node {object} New node to set as currently selected one
       * @private
       */
      _updateSelectedNode: function DLCT__updateSelectedNode(node)
      {
         Alfresco.logger.debug("DLCT__updateSelectedNode");

         this._showHighlight(false);
         this.selectedNode = node;
         this._showHighlight(true);
      },

      /**
       * Build URI parameter string for treenode JSON data webscript
       *
       * @method _buildTreeNodeUrl
       * @param path {string} Path to query
       */
       _buildTreeNodeUrl: function DLCT__buildTreeNodeUrl(path)
       {
          var uriTemplate = Alfresco.constants.PROXY_URI;
          if (this.options.mode == DLCT.MODE_SITE)
          {
             if (this.options.containerType == "dod:filePlan")
             {
                uriTemplate += "slingshot/doclib/dod5015/treenode/site/{site}/{container}{path}";
             }
             else
             {
                uriTemplate += "slingshot/doclib/treenode/site/{site}/{container}{path}";
             }
          }
          else
          {
             uriTemplate += "slingshot/doclib/treenode/node/{nodeRef}{path}";
          }

          var url = YAHOO.lang.substitute(uriTemplate,
          {
             site: encodeURIComponent(this.options.siteId),
             container: encodeURIComponent(this.options.containerId),
             nodeRef: this.options.nodeRef.replace(":/", ""),
             path: Alfresco.util.encodeURIPath(path)
          });

          return url;
       }
   });
})();

/* Dummy instance to load optional YUI components early */
new Alfresco.module.DoclibCopyTo("null");