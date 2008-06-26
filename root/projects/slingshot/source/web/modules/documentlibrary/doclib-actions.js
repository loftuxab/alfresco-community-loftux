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
 * Document Library Actions module for Document Library.
 * 
 * @namespace Alfresco.module
 * @class Alfresco.module.DoclibActions
 */
(function()
{
   Alfresco.module.DoclibActions = function()
   {
      this.name = "Alfresco.module.DoclibActions";
      
      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["connection", "json", "selector"], this.onComponentsLoaded, this);

      return this;
   };

   Alfresco.module.DoclibActions.prototype =
   {
      /**
       * Flag indicating whether module is ready to be used.
       * Flag is set when all YUI component dependencies have loaded.
       * 
       * @property isReady
       * @type boolean
       */
      isReady: false,

      /**
       * Object literal for default AJAX request configuration
       *
       * @property defaultConfig
       * @type object
       */
      defaultConfig:
      {
         method: "POST",
         url: Alfresco.constants.PROXY_URI + "slingshot/doclib/action/",
         dataObj: null,
         successCallback: null,
         successMessage: null,
         failureCallback: null,
         failureMessage: null,
         object: null
      },

      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function DLA_onComponentsLoaded()
      {
         this.isReady = true;
      },
      
      /**
       * Make AJAX request to data webscript
       *
       * @method _runAction
       * @private
       * @return {boolean} false: module not ready for use
       */
      _runAction: function DLA__runAction(config, obj)
      {
         // Check components loaded
         if (!this.isReady)
         {
            return false;
         }

         // Merge-in any supplied object
         if (typeof obj == "object")
         {
            config = YAHOO.lang.merge(config, obj);
         }
         
         if (config.method == Alfresco.util.Ajax.DELETE)
         {
            Alfresco.util.Ajax.request(config);
         }
         else
         {
            Alfresco.util.Ajax.jsonRequest(config);
         }
      },
      
      
      /**
       * ACTION: Delete file.
       * Deletes a file from the component container, given filepath
       *
       * @method deleteFile
       * @param site {string} current site
       * @param containerId {string} component container
       * @param path {string} path where file is located
       * @param file {string} file to be deleted
       * @param obj {object} optional additional request configuration
       * @return {boolean} false: module not ready
       */
      deleteFile: function DLA_deleteFile(site, containerId, path, file, obj)
      {
         var filePath =  path + "/" + file;
         
         var config = YAHOO.lang.merge(this.defaultConfig,
         {
            url: this.defaultConfig.url + "file/" + site + "/" + containerId + filePath,
            method: Alfresco.util.Ajax.DELETE,
            responseContentType: Alfresco.util.Ajax.JSON,
            object:
            {
               filePath: filePath,
               fileName: file
            }
         });

         return this._runAction(config, obj);
      },
      
      /**
       * ACTION: Checkout file.
       * Checks out a working copy file from the component container, given filepath
       *
       * @method checkoutFile
       * @param site {string} current site
       * @param containerId {string} component container
       * @param path {string} path where file is located
       * @param file {string} file to be deleted
       * @param obj {object} optional additional request configuration
       * @return {boolean} false: module not ready
       */
      checkoutFile: function DLA_checkoutFile(site, containerId, path, file, obj)
      {
         var filePath =  path + "/" + file;
         
         var config = YAHOO.lang.merge(this.defaultConfig,
         {
            url: this.defaultConfig.url + "checkout/" + site + "/" + containerId + filePath,
            method: Alfresco.util.Ajax.POST,
            responseContentType: Alfresco.util.Ajax.JSON,
            dataObj: {},
            object:
            {
               filePath: filePath,
               fileName: file
            }
         });

         return this._runAction(config, obj);
      }
      
   };
})();

/* Dummy instance to load optional YUI components early */
new Alfresco.module.DoclibActions();