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
 * ManagePermissions template.
 * 
 * @namespace Alfresco.template
 * @class Alfresco.template.ManagePermissions
 */
(function()
{
   /**
    * ManagePermissions constructor.
    * 
    * @return {Alfresco.template.ManagePermissions} The new ManagePermissions instance
    * @constructor
    */
   Alfresco.template.ManagePermissions = function ManagePermissions_constructor()
   {
      return Alfresco.template.ManagePermissions.superclass.constructor.call(this, null, "Alfresco.template.ManagePermissions");
   };
   
   YAHOO.extend(Alfresco.template.ManagePermissions, Alfresco.component.Base,
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
          * nodeRef of folder being viewed
          * 
          * @property nodeRef
          * @type string
          */
         nodeRef: null,
         
         /**
          * Current siteId.
          * 
          * @property siteId
          * @type string
          */
         siteId: "",

         /**
          * Root node if Repository-based library
          * 
          * @property rootNode
          * @type Alfresco.util.NodeRef
          */
         rootNode: null
      },

      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @override
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function ManagePermissions_onComponentsLoaded()
      {
         YAHOO.util.Event.onDOMReady(this.onReady, this, true);
      },

      /**
       * Fired by YUI when parent element is available for scripting.
       * Template initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function ManagePermissions_onReady()
      {
         var url = Alfresco.constants.PROXY_URI + 'slingshot/doclib/node/' + this.options.nodeRef.uri;
         if (this.options.siteId == "")
         {
            // Repository mode
            url += "?libraryRoot=" + encodeURIComponent(this.options.rootNode.nodeRef);
         }
         Alfresco.util.Ajax.jsonGet(
         {
            url: url,
            successCallback: 
            { 
               fn: this._getDataSuccess, 
               scope: this 
            },
            failureMessage: "Failed to load data for permission details"
         });
      },
      
      /**
       * Success handler called when the AJAX call to the doclist web script returns successfully
       *
       * @response The response object
       */
      _getDataSuccess: function ManagePermissions__getDataSuccess(response)
      {
         if (response.json !== undefined)
         {
            var nodeDetails = response.json.item;
            
            // Fire event to inform any listening components that the data is ready
            YAHOO.Bubbling.fire("nodeDetailsAvailable",
            {
               nodeDetails: nodeDetails,
               metadata: response.json.metadata
            });
         }
      }
   });
})();
