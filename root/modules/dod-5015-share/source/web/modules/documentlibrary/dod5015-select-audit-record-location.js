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
 * Document Library Selector. Allows selection of document library (and folder) of a specified site
 * 
 * @namespace Alfresco.module
 * @class Alfresco.module.SelectAuditRecordLocation
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

   Alfresco.module.SelectAuditRecordLocation = function(htmlId)
   {
      Alfresco.module.DoclibMoveTo.superclass.constructor.call(this, htmlId);
      
      // Re-register with our own name
      this.name = "Alfresco.module.SelectAuditRecordLocation";
      Alfresco.util.ComponentManager.reregister(this);

      // Initialise prototype properties
      this.pathsToExpand = [];

      return this;
   };
   
   YAHOO.extend(Alfresco.module.SelectAuditRecordLocation, Alfresco.module.DoclibMoveTo,
   {
      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @override
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.module.SelectAuditRecordLocation} returns 'this' for method chaining
       */
      setOptions: function SARL_setOptions(obj)
      {
         var dataWebScripts =
         {
            copy: "copy-to/site",
            move: "move-to/site",
            file: "add-child/site"
         };
         
         if (typeof dataWebScripts[obj.mode] == "undefined")
         {
            throw new Error("Alfresco.module.SelectAuditRecordLocation: Invalid mode '" + obj.mode + "'");
         }
         
         return Alfresco.module.SelectAuditRecordLocation.superclass.setOptions.call(this, YAHOO.lang.merge(
         {
            templateUrl: Alfresco.constants.URL_SERVICECONTEXT + "modules/documentlibrary/dod5015/copy-move-file-to",
            dataWebScript: dataWebScripts[obj.mode]
         }, obj));
      },

      /**
       * Gets a custom message
       *
       * @method msg
       * @param messageId {string} The messageId to retrieve
       * @return {string} The custom message
       * @override
       */
      msg: function SARL_msg(messageId)
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
      _showDialog: function SARL__showDialog()
      {
         this.widgets.okButton.set("label", this.msg("button"));
         return Alfresco.module.SelectAuditRecordLocation.superclass._showDialog.apply(this, arguments);
      },

      /**
       * Dialog OK button event handler
       *
       * @method onOK
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onOK: function SARL_onOK(e, p_obj)
      {
         var node = this.widgets.treeview.getNodeByProperty("path", this.currentPath);
         YAHOO.Bubbling.fire('AuditRecordLocationSelected', {nodeRef:node.data.nodeRef});
         this.widgets.dialog.hide();
      },

      /**
       * Build URI parameter string for treenode JSON data webscript
       *
       * @method _buildTreeNodeUrl
       * @param path {string} Path to query
       */
       _buildTreeNodeUrl: function SARL__buildTreeNodeUrl(path)
       {
          var uriTemplate = Alfresco.constants.PROXY_URI + "slingshot/doclib/dod5015/treenode/site/{site}/{container}{path}";

          var url = YAHOO.lang.substitute(uriTemplate,
          {
             site: encodeURIComponent(this.options.siteId),
             container: encodeURIComponent(this.options.containerId),
             path: Alfresco.util.encodeURIPath(path)
          });

          return url;
       }
   });
})();

/* Dummy instance to load optional YUI components early */
new Alfresco.module.SelectAuditRecordLocation("null");