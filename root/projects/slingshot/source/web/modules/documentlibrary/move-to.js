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
 * Document Library "Move To" module for Document Library.
 * 
 * @namespace Alfresco.module
 * @class Alfresco.module.DoclibMoveTo
 */
(function()
{
   /**
   * YUI Library aliases
   */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Element = YAHOO.util.Element;

   Alfresco.module.DoclibMoveTo = function(htmlId)
   {
      this.name = "Alfresco.module.DoclibMoveTo";
      this.id = htmlId;

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "container", "connection", "json", "treeview"], this.onComponentsLoaded, this);

      return this;
   };
   
   Alfresco.module.DoclibMoveTo.prototype =
   {
      /**
       * Object container for initialization options
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
          * @default: 30em
          */
         width: "30em",
         
         /**
          * Files to move
          *
          * @property: files
          * @type: object
          * @default: null
          */
         files: null
      },
      
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
      pathsToExpand: [],

      /**
       * Selected tree node.
       * 
       * @property selectedNode
       * @type {YAHOO.widget.Node}
       */
      selectedNode: null,

      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.module.DoclibMoveTo} returns 'this' for method chaining
       */
      setOptions: function DLMT_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },

      /**
       * Set messages for this component.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.module.DoclibMoveTo} returns 'this' for method chaining
       */
      setMessages: function DLMT_setMessages(obj)
      {
         Alfresco.util.addMessages(obj, this.name);
         return this;
      },

      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function DLMT_onComponentsLoaded()
      {
         // DocLib Actions module
         this.modules.actions = new Alfresco.module.DoclibActions();
      },
      
      /**
       * Main entry point
       * @method showDialog
       */
      showDialog: function DLMT_showDialog()
      {
         if (!this.containerDiv)
         {
            // Load the UI template from the server
            Alfresco.util.Ajax.request(
            {
               url: Alfresco.constants.URL_SERVICECONTEXT + "modules/documentlibrary/move-to",
               dataObj:
               {
                  htmlid: this.id
               },
               successCallback:
               {
                  fn: this.onTemplateLoaded,
                  scope: this
               },
               failureMessage: "Could not load Document Library Move-To template",
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
      onTemplateLoaded: function DLMT_onTemplateLoaded(response)
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
         this.widgets.dialog = new YAHOO.widget.Panel(dialogDiv,
         {
            modal: true,
            draggable: false,
            fixedcenter: true,
            close: true,
            visible: false,
            width: this.options.width
         });
         this.widgets.dialog.render(document.body);
         
         // OK button
         this.widgets.okButton = Alfresco.util.createYUIButton(this, "ok", this.onOK);

         // Cancel button
         this.widgets.cancelButton = Alfresco.util.createYUIButton(this, "cancel", this.onCancel);

         /**
          * Dynamically loads TreeView nodes.
          * This MUST be inline in order to have access to the Alfresco.DocListMoveTo class.
          * @method fnLoadNodeData
          * @param node {object} Parent node
          * @param fnLoadComplete {function} Expanding node's callback function
          */
         this.fnLoadNodeData = function DLMT_oR_fnLoadNodeData(node, fnLoadComplete)
         {
            // Get the path this node refers to
            var nodePath = node.data.path;

            // Prepare URI for XHR data request
            var uri = me._buildTreeNodeUrl.call(me, nodePath);

            // Prepare the XHR callback object
            var callback =
            {
               success: function DLMT_lND_success(oResponse)
               {
                  var results = eval("(" + oResponse.responseText + ")");

                  if (results.treenode.items)
                  {
                     for (var i = 0, j = results.treenode.items.length; i < j; i++)
                     {
                        var item = results.treenode.items[i];
                        var tempNode = new YAHOO.widget.TextNode(
                        {
                           label: item.name,
                           path: nodePath + "/" + item.name,
                           nodeRef: item.nodeRef,
                           description: item.description
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
               failure: function DLMT_lND_failure(oResponse)
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
      _showDialog: function DLMT__showDialog()
      {
         // Enable buttons
         this.widgets.okButton.set("disabled", false);
         this.widgets.cancelButton.set("disabled", false);

         // Dialog title
         var titleDiv = Dom.get(this.id + "-title");
         if (YAHOO.lang.isArray(this.options.files))
         {
            titleDiv.innerHTML = this._msg("title.multi", this.options.files.length)
         }
         else
         {
            var fileSpan = '<span class="light">' + this.options.files.displayName + '</span>';
            titleDiv.innerHTML = this._msg("title.single", fileSpan)
         }

         // Build the TreeView widget
         this._buildTree();
         
         // Show the dialog
         this.widgets.dialog.show();

         // Kick-off navigation to initial path
         if (this.options.path !== null)
         {
            this.pathChanged(this.options.path);
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
      onOK: function DLMT_onOK(e, p_obj)
      {
         var files, multipleFiles = [];

         // Single/multi files into array of nodeRefs
         if (YAHOO.lang.isArray(this.options.files))
         {
            files = this.options.files;
         }
         else
         {
            files = [this.options.files];
         }
         for (var i = 0, j = files.length; i < j; i++)
         {
            multipleFiles.push(files[i].nodeRef);
         }
         
         // Success callback function
         var fnSuccess = function DLMT__onOK_success(p_data)
         {
            var result;
            var successCount = p_data.json.successCount;
            var failureCount = p_data.json.failureCount;
            
            this.widgets.dialog.hide();

            // Did the operation succeed?
            if (!p_data.json.overallSuccess)
            {
               Alfresco.util.PopupManager.displayMessage(
               {
                  text: this._msg("message.move-to.failure")
               });
               return;
            }
            
            YAHOO.Bubbling.fire("filesMoved",
            {
               destination: this.currentPath,
               successCount: successCount,
               failureCount: failureCount
            });
            
            for (var i = 0, j = data.json.totalResults; i < j; i++)
            {
               result = p_data.json.results[i];
               
               if (result.success)
               {
                  YAHOO.Bubbling.fire(result.type == "folder" ? "folderMoved" : "fileMoved",
                  {
                     multiple: true,
                     nodeRef: result.nodeRef,
                     destination: this.currentPath
                  });
               }
            }
            
            Alfresco.util.PopupManager.displayMessage(
            {
               text: this._msg("message.move-to.success", successCount)
            });
         }
         
         // Failure callback function
         var fnFailure = function DLMT__onOK_failure(p_data)
         {
            this.widgets.dialog.hide();

            Alfresco.util.PopupManager.displayMessage(
            {
               text: this._msg("message.move-to.failure")
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
               name: "move-to",
               method: Alfresco.util.Ajax.POST
            },
            wait:
            {
               message: this._msg("message.please-wait")
            },
            params:
            {
               siteId: this.options.siteId,
               containerId: this.options.containerId,
               path: this.selectedNode.data.path
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
      onCancel: function DLMT_onCancel(e, p_obj)
      {
         this.widgets.dialog.hide();
      },

      /**
       * Fired by YUI TreeView when a node has finished expanding
       * @method onExpandComplete
       * @param oNode {YAHOO.widget.Node} the node recently expanded
       */
      onExpandComplete: function DLMT_onExpandComplete(oNode)
      {
         Alfresco.logger.debug("DLMT_onExpandComplete");

         // Make sure the tree's DOM has been updated
         this.widgets.treeview.draw();
         // Redrawing the tree will clear the highlight
         this._showHighlight(true);
         
         if (this.pathsToExpand.length > 0)
         {
            var node = this.widgets.treeview.getNodeByProperty("path", this.pathsToExpand.shift());
            if (node !== null)
            {
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
       * @param node {YAHOO.widget.Node} the node clicked
       * @return allowExpand {boolean} allow or disallow node expansion
       */
      onNodeClicked: function DLMT_onNodeClicked(node)
      {
         Alfresco.logger.debug("DLMT_onNodeClicked");
         this.pathChanged(node.data.path);
         this._updateSelectedNode(node);
         return false;
      },

      
      /**
       * Update tree when the path has changed
       * @method pathChanged
       * @param path {string} new path
       */
      pathChanged: function DLMT_onPathChanged(path)
      {
         Alfresco.logger.debug("DLMT_onPathChanged");

         // ensure path starts with leading slash if not the root node
         if ((path != "") && (path.substring(0, 1) != "/"))
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
         var paths = path.split("/");
         var expandPath = "";
         for (var i = 0; i < paths.length; i++)
         {
            if (paths[i] != "")
            {
               // Push the path onto the list of paths to be expanded
               expandPath += "/" + paths[i];
               this.pathsToExpand.push(expandPath);
            }
         }
         
         // Kick off the expansion process by expanding the root node
         node = this.widgets.treeview.getNodeByProperty("path", "");
         if (node !== null)
         {
            node.expand();
         }
      },
      

      /**
       * PRIVATE FUNCTIONS
       */

      /**
       * Creates the TreeView control and renders it to the parent element.
       * @method _buildTree
       * @private
       */
      _buildTree: function DLMT__buildTree()
      {
         Alfresco.logger.debug("DLMT__buildTree");

         // Create a new tree
         var tree = new YAHOO.widget.TreeView(this.id + "-treeview");
         this.widgets.treeview = tree;

         // Turn dynamic loading on for entire tree
         tree.setDynamicLoad(this.fnLoadNodeData);

         // Get root node for tree
         var root = tree.getRoot();

         // Add default top-level node
         var tempNode = new YAHOO.widget.TextNode(
         {
            label: Alfresco.util.message("node.root", this.name),
            path: "",
            nodeRef: ""
         }, root, false);

         // Register tree-level listeners
         tree.subscribe("labelClick", this.onNodeClicked, this, true);
         tree.subscribe("expandComplete", this.onExpandComplete, this, true);

         // Render tree with this one top-level node
         tree.draw();
      },

      _showHighlight: function DLMT__showHighlight(isVisible)
      {
         Alfresco.logger.debug("DLMT__showHighlight");

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
      
      _updateSelectedNode: function DLMT__updateSelectedNode(node)
      {
         Alfresco.logger.debug("DLMT__updateSelectedNode");

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
       _buildTreeNodeUrl: function DLMT__buildTreeNodeUrl(path)
       {
          var uriTemplate = Alfresco.constants.PROXY_URI + "slingshot/doclib/treenode/site/{site}/{container}{path}";

          var url = YAHOO.lang.substitute(uriTemplate,
          {
             site: encodeURIComponent(this.options.siteId),
             container: encodeURIComponent(this.options.containerId),
             path: encodeURI(path)
          });

          return url;
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
          return Alfresco.util.message.call(this, messageId, this.name, Array.prototype.slice.call(arguments).slice(1));
       }
   };
})();

/* Dummy instance to load optional YUI components early */
new Alfresco.module.DoclibMoveTo(null);