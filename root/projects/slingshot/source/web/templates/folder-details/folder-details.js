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
 * FolderDetails template.
 * 
 * @namespace Alfresco
 * @class Alfresco.FolderDetails
 */
(function()
{
   /**
    * FolderDetails constructor.
    * 
    * @return {Alfresco.FolderDetails} The new FolderDetails instance
    * @constructor
    */
   Alfresco.FolderDetails = function FolderDetails_constructor()
   {
      // Load YUI Components
      Alfresco.util.YUILoaderHelper.require(["editor"], this.onComponentsLoaded, this);
      
      /* Decoupled event listeners */
      YAHOO.Bubbling.on("metadataRefresh", this.onReady, this);
      YAHOO.Bubbling.on("filesPermissionsUpdated", this.onReady, this);
      YAHOO.Bubbling.on("filesMoved", this.onReady, this);
            
      return this;
   };
   
   Alfresco.FolderDetails.prototype =
   {
      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       */
      options:
      {
         nodeRef: null,
         
         /**
          * Current siteId.
          * 
          * @property siteId
          * @type string
          */
         siteId: ""
      },

      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.Search} returns 'this' for method chaining
       */
      setOptions: function FolderDetails_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },
      
      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function FolderDetails_onComponentsLoaded()
      {
         YAHOO.util.Event.onDOMReady(this.onReady, this, true);
      },
   
      /**
       * Fired by YUI when parent element is available for scripting.
       * Template initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function FolderDetails_onReady()
      {
         var config =
         {
            method: "GET",
            url: Alfresco.constants.PROXY_URI + 'slingshot/doclib/doclist/folders/node/' + 
                 this.options.nodeRef.replace(":/", "") + '?filter=node',
            successCallback: 
            { 
               fn: this._getDataSuccess, 
               scope: this 
            },
            failureMessage: "Failed to load data for folder details"
         };
         Alfresco.util.Ajax.request(config);
         
      },
      
      /**
       * Success handler called when the AJAX call to the doclist web script returns successfully
       *
       * @response The response object
       */
      _getDataSuccess: function FolderDetails__getDataSuccess(response)
      {
         if (response.json !== undefined)
         {
            var folderData = response.json.items[0];
            
            // fire event to inform any listening components that the data is ready
            YAHOO.Bubbling.fire("folderDetailsAvailable", folderData);
            
            // fire event to show comments for folder
            var itemUrl = YAHOO.lang.substitute("site/{site}/folder-details?nodeRef={nodeRef}",
            {
               site: this.options.siteId,
               nodeRef: this.options.nodeRef
            });
            var eventData =
            { 
               nodeRef: this.options.nodeRef,
               title: folderData.displayName,
               page: "folder-details",
               pageParams:
               {
                  nodeRef: this.options.nodeRef
               }
            }
            
            YAHOO.Bubbling.fire("setCommentedNode", eventData);
         }
      }
   };
})();
