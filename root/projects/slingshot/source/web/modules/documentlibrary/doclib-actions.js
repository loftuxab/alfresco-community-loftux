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
         urlStem: Alfresco.constants.PROXY_URI + "slingshot/doclib/action/",
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
            if (config.dataObj !== null)
            {
               // Change this request into a POST with the alf_method override
               config.method = Alfresco.util.Ajax.POST;
               if (config.url.indexOf("alf_method") < 1)
               {
                  config.url += (config.url.indexOf("?") < 0 ? "?" : "&") + "alf_method=delete";
               }
               Alfresco.util.Ajax.jsonRequest(config);
            }
            else
            {
               Alfresco.util.Ajax.request(config);
            }
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
       * @param action.success.callback.fn {object} Callback function to call on success.
       * <pre>function(data, obj) where data is an object literal containing config, json, serverResponse</pre>
       * @param action.success.callback.scope {object} Success callback function scope
       * @param action.success.callback.obj {object} Success callback function object passed to callback
       * @param action.failure.event.name {string} Bubbling event to fire on failure
       * @param action.failure.event.obj {object} Bubbling event failure parameter object
       * @param action.failure.message {string} Timed message to display on failure
       * @param action.failure.callback.fn {object} Callback function to call on failure.
       * <pre>function(data, obj) where data is an object literal containing config, json, serverResponse</pre>
       * @param action.failure.callback.scope {object} Failure callback function scope
       * @param action.failure.callback.obj {object} Failure callback function object passed to callback
       * @param action.webscript.stem {string} optional webscript URL stem
       * <pre>default: Alfresco.constants.PROXY_URI + "slingshot/doclib/action/"</pre>
       * @param action.webscript.name {string} data webscript URL name
       * @param action.webscript.method {string} HTTP method to call the data webscript on
       * @param action.webscript.queryString {string} Optional queryString to append to the webscript URL
       * @param action.params.siteId {string} current site
       * @param action.params.containerId {string} component container
       * @param action.params.path {string} path where file is located
       * @param action.params.file {string} file to be deleted
       * @param action.params.nodeRef {string} noderef instead of site, container, path, file
       * @param action.wait.message {string} if set, show a Please wait-style message during the operation
       * @param action.config {object} optional additional request configuration overrides
       * @return {boolean} false: module not ready
       */
      genericAction: function DLA_genericAction(action)
      {
         var path = "";
         var success = action.success;
         var failure = action.failure;
         var webscript = action.webscript;
         var params = action.params;
         var overrideConfig = action.config;
         var wait = action.wait;
         var configObj = null;

         var fnCallback = function DLA_genericAction_callback(data, obj)
         {
            // Check for notification event
            if (obj)
            {
               // Event specified?
               if (obj.event && obj.event.name)
               {
                  YAHOO.Bubbling.fire(obj.event.name, obj.event.obj);
               }
               // Please wait pop-up active?
               if (obj.popup)
               {
                  obj.popup.destroy();
               }
               // Message?
               if (obj.message)
               {
                  Alfresco.util.PopupManager.displayMessage(
                  {
                     text: obj.message
                  });
               }
               // Callback function specified?
               if (obj.callback && obj.callback.fn)
               {
                  obj.callback.fn.call((typeof obj.callback.scope == "object" ? obj.callback.scope : this),
                  {
                     config: data.config,
                     json: data.json,
                     serverResponse: data.serverResponse
                  }, obj.callback.obj);
               }
            }
         }
         
         // Please Wait... message pop-up?
         if (wait && wait.message)
         {
            if (typeof success != "object")
            {
               success = {};
            }
            if (typeof failure != "object")
            {
               failure = {};
            }
            
            success.popup = Alfresco.util.PopupManager.displayMessage(
            {
               modal: true,
               displayTime: 0,
               text: wait.message
            });
            failure.popup = success.popup;
         }

         var url;
         if (webscript.stem)
         {
            url = webscript.stem + webscript.name;
         }
         else
         {
            url = this.defaultConfig.urlStem + webscript.name;
         }
         
         if (params)
         {
            url = YAHOO.lang.substitute(url, params);
            configObj = params;
         }
         if (webscript.queryString)
         {
            url += "?" + webscript.queryString;
         }
                  
         var config = YAHOO.lang.merge(this.defaultConfig,
         {
            successCallback:
            {
               fn: fnCallback,
               scope: this,
               obj: success
            },
            successMessage: null,
            failureCallback:
            {
               fn: fnCallback,
               scope: this,
               obj: failure
            },
            failureMessage: null,
            url: url,
            method: webscript.method,
            responseContentType: Alfresco.util.Ajax.JSON,
            object: configObj
         });

         return this._runAction(config, overrideConfig);
      }
   };
})();

/* Dummy instance to load optional YUI components early */
new Alfresco.module.DoclibActions();