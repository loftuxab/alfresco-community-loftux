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
       * ACTION: Generic action.
       * Generic DocLib action based on passed-in parameters
       *
       * @method genericAction
       * @param action.success.event.name {string} Bubbling event to fire on success
       * @param action.success.event.obj {object} Bubbling event success parameter object
       * @param action.success.message {string} Timed message to display on success
       * @param action.failure.event.name {string} Bubbling event to fire on failure
       * @param action.failure.event.obj {object} Bubbling event failure parameter object
       * @param action.failure.message {string} Timed message to display on failure
       * @param action.webscript.name {string} data webscript URL name
       * @param action.webscript.method {string} HTTP method to call the data webscript on
       * @param action.params.siteId {string} current site
       * @param action.params.containerId {string} component container
       * @param action.params.path {string} path where file is located
       * @param action.params.file {string} file to be deleted
       * @param action.params.nodeRef {string} noderef instead of site, container, path, file
       * @param obj {object} optional additional request configuration
       * @return {boolean} false: module not ready
       */
      genericAction: function DLA_genericAction(action, obj)
      {
         var filePath = null;
         var success = action.success;
         var failure = action.failure;
         var webscript = action.webscript;
         var params = action.params;

         var fnCallback = function DLA_genericAction_callback(data, obj)
         {
            // Check for notification event
            if (obj && obj.event && obj.event.name)
            {
               YAHOO.Bubbling.fire(obj.event.name, obj.event.obj);
            }
         }
         
         var url = this.defaultConfig.url + webscript.name + "/";
         if (params.nodeRef)
         {
            url += "node/" + params.nodeRef.replace(":/", "");
         }
         else
         {
            filePath = params.path + "/" + params.file;
            url += "site/" + params.siteId + "/" + params.containerId + filePath;
         }
         
         var config = YAHOO.lang.merge(this.defaultConfig,
         {
            successCallback:
            {
               fn: fnCallback,
               scope: this,
               obj: success
            },
            successMessage: (success && success.message) ? success.message : null,
            failureCallback:
            {
               fn: fnCallback,
               scope: this,
               obj: failure
            },
            failureMessage: (failure && failure.message) ? failure.message : null,
            url: url,
            method: webscript.method,
            responseContentType: Alfresco.util.Ajax.JSON,
            object:
            {
               nodeRef: params.nodeRef,
               siteId: params.siteId,
               containerId: params.containerId,
               path: params.path,
               file: params.file,
               filePath: filePath
            }
         });

         return this._runAction(config, obj);
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
            url: this.defaultConfig.url + "file/site/" + site + "/" + containerId + filePath,
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
       * @method editFileOffline
       * @param site {string} current site
       * @param containerId {string} component container
       * @param path {string} path where file is located
       * @param file {string} file to be deleted
       * @param obj {object} optional additional request configuration
       * @return {boolean} false: module not ready
       */
      editFileOffline: function DLA_editFileOffline(site, containerId, path, file, obj)
      {
         var filePath =  path + "/" + file;
         
         var config = YAHOO.lang.merge(this.defaultConfig,
         {
            url: this.defaultConfig.url + "checkout/site/" + site + "/" + containerId + filePath,
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