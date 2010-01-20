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
 * DocumentList TreeView component.
 * 
 * @namespace Alfresco
 * @class Alfresco.RecordsDocListTree
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
    * Records DocumentList TreeView constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.RecordsDocListTree} The new RecordsDocListTree instance
    * @constructor
    */
   Alfresco.RecordsDocListTree = function DLT_constructor(htmlId)
   {
      return Alfresco.RecordsDocListTree.superclass.constructor.call(this, htmlId);
   };
   
   YAHOO.extend(Alfresco.RecordsDocListTree, Alfresco.DocListTree,
   {
      /**
       * PRIVATE FUNCTIONS
       */

      /**
       * Build a tree node using passed-in data
       *
       * @method _buildTreeNode
       * @param p_oData {object} Object literal containing required data for new node
       * @param p_oParent {object} Optional parent node
       * @param p_expanded {object} Optional expanded/collaped state flag
       * @return {YAHOO.widget.TextNode} The new tree node
       */
      _buildTreeNode: function DLT__buildTreeNode(p_oData, p_oParent, p_expanded)
      {
         var treeNode = new YAHOO.widget.TextNode(
         {
            label: $html(p_oData.name),
            path: p_oData.path,
            nodeRef: p_oData.nodeRef,
            description: p_oData.description
         }, p_oParent, p_expanded);
         
         // Override YUI getStyle() function to allow our own folder nodes to be rendered.
         // NOTE: Not currently in active use - this code is simply a copy of the YUI code
         treeNode.getStyle = function()
         {
            if (this.isLoading)
            {
                return "ygtvloading";
            }
            else
            {
                // location top or bottom, middle nodes also get the top style
                var loc = (this.nextSibling) ? "t" : "l";

                // type p=plus(expand), m=minus(collapse), n=none(no children)
                var type = "n";
                if (this.hasChildren(true) || (this.isDynamic() && !this.getIconMode()))
                {
                    type = (this.expanded) ? "m" : "p";
                }

                return "ygtv" + loc + type;
            }
         };
         
         return treeNode;
      },

      /**
       * Build URI parameter string for treenode JSON data webscript
       *
       * @method _buildTreeNodeUrl
       * @param path {string} Path to query
       */
       _buildTreeNodeUrl: function DLT__buildTreeNodeUrl(path)
       {
          var uriTemplate ="slingshot/doclib/dod5015/treenode/site/" + $combine(encodeURIComponent(this.options.siteId), encodeURIComponent(this.options.containerId), Alfresco.util.encodeURIPath(path));
          uriTemplate += "?children=" + this.options.evaluateChildFolders;
          return  Alfresco.constants.PROXY_URI + uriTemplate + "?perms=false";
       }
   });
})();