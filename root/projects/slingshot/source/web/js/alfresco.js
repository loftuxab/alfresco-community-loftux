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
 * Alfresco root namespace.
 * 
 * @namespace Alfresco
 */
// Ensure Alfresco root object exists
if (typeof Alfresco == "undefined" || !Alfresco)
{
   var Alfresco = {};
}

/**
 * Alfresco top-level constants namespace.
 * 
 * @namespace Alfresco
 * @class Alfresco.constants
 */
Alfresco.constants = Alfresco.constants || {};

/**
 * Alfresco top-level module namespace.
 * 
 * @namespace Alfresco
 * @class Alfresco.module
 */
Alfresco.module = Alfresco.module || {};

/**
 * Alfresco top-level util namespace.
 * 
 * @namespace Alfresco
 * @class Alfresco.util
 */
Alfresco.util = Alfresco.util || {};

/**
 * Alfresco top-level logger namespace.
 * 
 * @namespace Alfresco
 * @class Alfresco.logger
 */
Alfresco.logger = Alfresco.logger || {};

/**
 * Appends an array onto an object
 * @method Alfresco.util.appendArrayToObject
 * @param obj {object} Object to be appended to
 * @param arr {array} Array to append/merge onto object
 * @return {object} The appended object
 * @static
 */
Alfresco.util.appendArrayToObject = function(obj, arr)
{
   if (arr)
   {
      for (var i = 0; i < arr.length; i++)
      {
          obj[arr[i]] = true;
      }
   }
   return obj;
};

/**
 * Convert an array into an object
 * @method Alfresco.util.arrayToObject
 * @param arr {array} Array to convert to object
 * @return {object} Object conversion of array
 * @static
 */
Alfresco.util.arrayToObject = function(arr)
{
   var obj = {};
   if (arr)
   {
      for (var i = 0; i < arr.length; i++)
      {
          obj[arr[i]] = true;
      }
   }
   return obj;
};

/**
 * Asserts param contains a proper value
 * @method Alfresco.util.assertNotEmpty
 * @param param {object} Parameter to assert valid
 * @param message {string} Error message to throw on assertion failure
 * @static
 * @throws {Error}
 */
Alfresco.util.assertNotEmpty = function(param, message)
{
   if (typeof param == "undefined" || !param || param === "")
   {
      throw new Error(message);
   }
};

/**
 * Wrapper to create a YUI Button with common attributes.
 * All supplied object parameters are passed to the button constructor
 * e.g. Alfresco.util.createYUIButton(this, "OK", this.onOK, {type: "submit"});
 *
 * @method Alfresco.util.createYUIButton
 * @param p_scope {object} Component containing button; must have "id" parameter
 * @param p_name {string} Dom element ID of markup that button is created from {p_scope.id}-{name}
 * @param p_onclick {function} If supplied, registered with the button's click event
 * @param p_obj {object} Optional extra object parameters to pass to button constructor
 * @return {YUI.widget.Button} New Button instance
 * @static
 */
Alfresco.util.createYUIButton = function(p_scope, p_name, p_onclick, p_obj)
{
   // Default button parameters
   var obj =
   {
      type: "button" 
   };
   
   // Any extra parameters?
   if (typeof p_obj == "object")
   {
      obj = YAHOO.lang.merge(obj, p_obj);
   }
   
   // Fix-up the menu element ID
   if ((obj.type == "menu") && (typeof obj.menu == "string"))
   {
      obj.menu = p_scope.id + "-" + obj.menu;
   }
   
   // Create the button
   var htmlId = p_scope.id + "-" + p_name;
   var button = new YAHOO.widget.Button(htmlId, obj);
   if (typeof button == "object")
   {
      // Register the click listener if one was supplied
      if (typeof p_onclick == "function")
      {
         // Special case for a menu
         if (obj.type == "menu")
         {
            button.getMenu().subscribe("click", p_onclick, p_scope, true);
         }
         else
         {
            button.on("click", p_onclick, button, p_scope);
         }
      }
   }
   return button;
}

/**
 * Wrapper for helping components specify their YUI components.
 * @class Alfresco.util.YUILoaderHelper
 */
Alfresco.util.YUILoaderHelper = function()
{
   /**
    * The YUILoader single instance which will load all the dependencies
    * @property yuiLoader
    * @type YAHOO.util.YUILoader
    */
   var yuiLoader = null;

   /**
    * Array to store callbacks from all component registrants
    * @property callbacks
    * @type Array
    */
   var callbacks = [];

   /**
    * Flag to indicate whether the initial YUILoader has completed
    * @property initialLoaderComplete
    * @type boolean
    */
   var initialLoaderComplete = false;
   
   return {
      /**
       * Main entrypoint for components wishing to load a YUI component
       * @method require
       * @param p_aComponents {Array} List of required YUI components. See YUILoader documentation for valid names
       * @param p_oCallback {function} Callback function invoked when all required YUI components have been loaded
       * @param p_oScope {object} Scope for callback function
       */
      require: function YLH_require(p_aComponents, p_oCallback, p_oScope)
      {
         if (yuiLoader === null)
         {
            yuiLoader = new YAHOO.util.YUILoader(
            {
               base: Alfresco.constants.URL_CONTEXT + "yui/",
               filter: Alfresco.constants.DEBUG ? "DEBUG" : "",
               loadOptional: false,
               skin:
               {
                  defaultSkin: Alfresco.constants.THEME,
                  /* TODO: Remove these once bug fixed from YUI 2.5.1 */
                  /* See: http://sourceforge.net/tracker/index.php?func=detail&aid=1954935&group_id=165715&atid=836478 */
                  base: 'assets/skins/',
                  path: 'skin.css',
                  after: ['reset', 'fonts', 'grids', 'base'],
                  rollup: 3
               },
               onSuccess: Alfresco.util.YUILoaderHelper.onLoaderComplete,
               scope: this
            });
         }
         
         if (p_aComponents.length > 0)
         {
            /* Have all the YUI components the caller requires been registered? */
            var isRegistered = true;
            for (var i = 0; i < p_aComponents.length; i++)
            {
               if (YAHOO.env.getVersion(p_aComponents[i]) === null)
               {
                  isRegistered = false;
                  break;
               }
            }
            if (isRegistered && (p_oCallback !== null))
            {
               p_oCallback.call(typeof p_oScope != "undefined" ? p_oScope : window);
            }
            else
            {
               /* Add to the list of components to be loaded */
               yuiLoader.require(p_aComponents);

               /* Store the callback function and scope for later */
               callbacks.push(
               {
                  required: Alfresco.util.arrayToObject(p_aComponents),
                  fn: p_oCallback,
                  scope: (typeof p_oScope != "undefined" ? p_oScope : window)
               });
            }
         }
         else if (p_oCallback !== null)
         {
            p_oCallback.call(typeof p_oScope != "undefined" ? p_oScope : window);
         }
      },
      
      /**
       * Called by template once all component dependencies have been registered. Should be just before the </body> closing tag.
       * @method loadComponents
       */
      loadComponents: function YLH_loadComponents()
      {
         if (yuiLoader !== null)
         {
            yuiLoader.insert(null, "js");
         }
      },

      /**
       * Callback from YUILoader once all required YUI componentshave been loaded by the browser.
       * @method onLoaderComplete
       */
      onLoaderComplete: function YLH_onLoaderComplete()
      {
         for (var i = 0; i < callbacks.length; i++)
         {
            if (callbacks[i].fn)
            {
               callbacks[i].fn.call(callbacks[i].scope);
            }
         }
         callbacks = [];
         initialLoaderComplete = true;
      }
   };
}();


/**
 * Keeps track of Alfresco components on a page. Components should register() upon creation to be compliant.
 * @class Alfresco.util.ComponentManager
 */
Alfresco.util.ComponentManager = function()
{
   /**
    * Array of registered components.
    * 
    * @property components
    * @type Array
    */
   var components = [];
   
   return {
      /**
       * Main entrypoint for components wishing to register themselves with the ComponentManager
       * @method register
       * @param p_aComponent {object} Component instance to be registered
       */
      register: function CM_register(p_oComponent)
      {
         components.push(p_oComponent);
      },

      /**
       * Allows components to find other regsitered components by name, id or both
       * e.g. find({name: "Alfresco.DocumentLibrary"})
       * @method find
       * @param p_oParams {object} List of paramters to search by
       * @return {Array} Array of components found in the search
       */
      find: function(p_oParams)
      {
         var found = [];
         var bMatch, component;
         
         for (var i = 0; i< components.length; i++)
         {
            component = components[i];
            bMatch = true;
            for (var key in p_oParams)
            {
               if (p_oParams[key] != component[key])
               {
                  bMatch = false;
               }
            }
            if (bMatch)
            {
               found.push(component);
            }
         }
         return found;
      }
   };
}();

/**
 * Provides a common interface for displaying popups in various forms
 * @class Alfresco.util.PopupManager
 */
Alfresco.util.PopupManager = function()
{
   return {
      zIndex: 15,

      displayMessageConfig:
      {
         text: null,
         autoHide: true,
         effect: YAHOO.widget.ContainerEffect.FADE,
         effectDuration: 0.5,
         displayTime: 2.5,
         modal: false
      },

      displayMessage: function(userConfig)
      {
         var c = YAHOO.lang.merge(this.displayMessageConfig, userConfig);
         if (c.text === undefined)
         {
            alert("Property text in userConfig must be set");
         }
         var message = new YAHOO.widget.Dialog("message",
         {
            visible: false,
            close: false,
            draggable:false,
            effect:{effect: c.effect, duration: c.effectDuration},
            modal: c.modal,
            zIndex: this.zIndex++
         });
         message.setBody(c.text);
         message.render(document.body);
         message.center();
         if (c.autoHide)
         {
            message.subscribe("show", this._delayPopupHide,
            {
               popup: message,
               displayTime: (c.displayTime * 1000)
            }, true);
         }
         message.show();
      },

      _delayPopupHide: function()
      {         
         YAHOO.lang.later(this.displayTime, this, function()
         {
            this.popup.hide();
         });
      },

      displayPromptConfig:
      {
         title: null,
         text: null,
         icon: null,
         autoHide: true,
         effect: null,
         effectDuration: 0.5,
         modal: false,
         close: false,
         buttons: [
         {
            text:"OK",
            handler: function()
            {
               this.hide();
            },
            isDefault: true
         }]
      },

      displayPrompt: function(userConfig)
      {
         var c = YAHOO.lang.merge(this.displayPromptConfig, userConfig);
         if (c.text === undefined)
         {
            alert("Property text in userConfig must be set");
         }

         var prompt = new YAHOO.widget.SimpleDialog("prompt",
         {
            visible: false,
            draggable: false,
            effect: c.effect,
            modal: c.modal,
            close: c.close,
            zIndex: this.zIndex++
         });
         if (c.title)
         {
            prompt.setHeader(c.title);
         }
         prompt.setBody(c.text);
         if (c.icon)
         {
            prompt.cfg.setProperty("icon", c.icon);
         }
         // TODO: Localize the OK label
         if (c.buttons)
         {
            prompt.cfg.queueProperty("buttons", c.buttons);
         }
         prompt.render(document.body);
         prompt.center();
         prompt.show();
      },

      displayDialog: function()
      {
         alert("Not implemented");
      }
   };
}();


/**
 * @class Alfresco.util.Ajax
 */
Alfresco.util.Ajax = function()
{
   return {
      JSON: "application/json",

      GET: "GET",
      POST: "POST",
      PUT: "PUT",
      DELETE: "DELETE",

      requestConfig:
      {
         method: "GET",        // GET, POST and hopefully PUT or DELETE if ot works...
         url: null,            // Must be set by user
         dataObj: null,        // Will be encoded to parameters (key1=value1&key2=value2)
                               // or a json string if contentType is set to JSON
         dataStr: null,        // Will be used in the request body, could be a already created parameter or json string
                               // Will be overriden by the encoding result from dataObj if dataObj is provided
         dataForm: null,       // A form object or id that contains the data to be sent with request
         requestContentType: null,    // Set to JSON if json should be used
         responseContentType: null,    // Set to JSON if json should be used
         successCallback: null,// Object literal representing callback upon successful operation
         successMessage: null, // Will be displayed by Alfresco.util.displayMessage if no success handler is provided
         failureCallback: null,// Object literal representing callback upon failed operation
         failureMessage: null,  // Will be displayed by Alfresco.util.displayPrompt if no failure handler is provided
         object: null           // An object that can be passed to be used by the success or failure handlers
      },

      jsonRequest: function(config)
      {
         config.requestContentType = this.JSON;
         config.responseContentType = this.JSON;
         this.request(config);
      },
      
      /**
       * Wraps a YAHOO.util.Connect.asyncRequest call and provides some default behaviour.
       *
       * If json is used it encodes config.dataObj to json (if provided)
       * and decodes the server response to the response.json provided to the callback.
       * If a json string already has been created it should be sent in as the config.dataStr.
       *
       * If normal parameters are used it can create them from config.dataObj handles enconding and decoding.
       * If a parameter string already has been created it should have been added to the
       * config.url for GET:s or config.dataStr for other methods.
       *
       * If request succeeds it calls the success handler (if provided),
       * if not displays the successMessage (if provided),
       * otherwise it does nothing.
       *
       * If request fails it calls the failure handler (if provided),
       * if not displays the failureMessage (if provided),
       * otherwise displays the best error message it can from the server response.
       *
       * The success or failure handler can expect a response object that looks like this and to be run int the scope
       * defined by config.scope:
       * {
       *   config: config,                  // The config object passed in to the request,
       *                                    // use config to add application specific attributes that the application
       *                                    // needs when handling the reponse or the give a certain id to the request
       *   serverResponse: serverResponse,  // The response provided by YIU
       *   json: json                       // If json was used this is the parsed result of serverResponse.responseText
       * }
       *
       * Should be called from the helper functions above to simplify the request calls and "hide" the paths to the
       * proxies, services etc.
       *
       * @method request
       * @param config {Object literal} Overridings of requestConfig, url is mandatory.
       *                                Use config to add application specific attributes that the application
       *                                needs when handling the reponse or the give a certain id to the request.
       */
      request: function(config)
      {
         var c = YAHOO.lang.merge(this.requestConfig, config);
         Alfresco.util.assertNotEmpty(c.url, "Parameter 'url' can NOT be null");
         Alfresco.util.assertNotEmpty(c.method, "Parameter 'method' can NOT be null");
         if (c.requestContentType)
         {
            YAHOO.util.Connect.setDefaultPostHeader(false);
            YAHOO.util.Connect.initHeader("Content-Type", c.requestContentType);
         }
         
         if (c.requestContentType === this.JSON)
         {
            if (c.method.toUpperCase() === this.GET)
            {
               throw new Error("Parameter 'method' can not be 'GET' when using contentType '" + c.requestContentType + "'");
            }
            else
            {
               if (c.dataObj)
               {
                  c.dataStr = YAHOO.lang.JSON.stringify(c.dataObj);
               }
            }
         }
         else
         {
            if (c.method.toUpperCase() === this.GET)
            {
               c.url += (c.url.indexOf("?") == -1 ? "?" : "&") + this._toParamString(c.dataObj);
            }
            else
            {
               if (c.dataObj)
               {
                  c.dataStr = this._toParamString(c.dataObj);
               }
            }
         }
         
         if (c.dataForm !== null)
         {
            // set the form on the connection manager
            YAHOO.util.Connect.setForm(c.dataForm);
         }
         
         var callback = 
         {
            success: this._successHandler,
            failure: this._failureHandler,
            argument:
            {
               config: config
            }
         };
         
         // make the request
         YAHOO.util.Connect.asyncRequest (c.method, c.url, callback, c.dataStr);
      },

      _toParamString: function(obj)
      {
         var params = "";
         var first = true;
         for (attr in obj)
         {
            if(first)
            {
               first = false;
            }
            else
            {
               params += "&";
            }
            /* TODO: Decode any url reserved characters */
            params += attr + "=" + obj[attr];
         }
         return params;
      },

      _successHandler: function(serverResponse)
      {
         var config = serverResponse.argument.config;
         var callback = config.successCallback;
         if (typeof callback.fn == "function")
         {
            /* User provided a custom successHandler */
            var json = null;
            if (config.responseContentType === "application/json")
            {
               if (serverResponse.responseText && serverResponse.responseText.length > 0)
               {
                  json = YAHOO.lang.JSON.parse(serverResponse.responseText);
               }
            }
            callback.fn.call((typeof callback.scope == "object" ? callback.scope : this),
            {
               config: config,
               json: json,
               serverResponse: serverResponse
            }, callback.object);
         }
         else if (config.successMessage)
         {
            /* User did not provide a custom successHandler but a custom successMessage */
            Alfresco.util.PopupManager.displayMessage(
            {
               text: config.successMessage
            });
         }
      },

      _failureHandler: function(serverResponse)
      {
         var config = serverResponse.argument.config;
         var callback = config.failureCallback;
         if (typeof callback.fn == "function")
         {
            /* User provided a custom failureHandler */
            var json = null;
            if (config.responseContentType === "application/json")
            {
               /* TODO: When error response is in valid json format */
               //json = YAHOO.lang.JSON.parse(serverResponse.responseText);
            }
            callback.fn.call((typeof callback.scope == "object" ? callback.scope : this),
            {
               config: config,
               json: json,
               serverResponse: serverResponse
            }, callback.object);
         }
         else if (config.failureMessage)
         {
            /* User did not provide a custom failureHandler but a custom failureMessage */
            Alfresco.util.PopupManager.displayPrompt(
            {
               text: config.failureMessage
            });
         }
         else
         {
            // User did not provide any failure info, display as good info as possible from the server response instead
            if (config.responseContentType == "application/json")
            {
               var json = null;
               /* TODO: When error response is in valid json format */
               // json = YAHOO.lang.JSON.parse(serverResponse.responseText);
               // Alfresco.util.PopupManager.displayPrompt({title: json.status.name, text: json.message});
               Alfresco.util.PopupManager.displayPrompt(
               {
                  title: serverResponse.statusText,
                  text: "Failure"
               });
            }
            else if (serverResponse.statusText)
            {
               Alfresco.util.PopupManager.displayPrompt(
               {
                  title: serverResponse.statusText
               });
            }
            else
            {
               Alfresco.util.PopupManager.displayPrompt(
               {
                  text: "Error sending data to server."
               });
            }
         }
      }
   };
}();


/**
 * @method Alfresco.logger.isDebugEnabled
 * @return {boolean}
 * @static
 */
Alfresco.logger.isDebugEnabled = function()
{
   return Alfresco.constants.DEBUG;
}

/**
 * @method Alfresco.logger.debug
 * @param p1 {object|string} Object or string for debug output
 * @param p2 {object} Optional: object to be dumped if p1 is a string
 * @return {boolean}
 * @static
 */
Alfresco.logger.debug = function(p1, p2)
{
   if (!this.isDebugEnabled())
   {
      return;
   }
   
   var msg;
   
   if (typeof p1 == "string" && p2 != null)
   {
      msg = p1 + " " + YAHOO.lang.dump(p2);
   }
   else
   {
      msg = YAHOO.lang.dump(p1);
   }
   
   /**
    * TODO: use an inline div first, then support the YUI logger and
    *       log to that if possible.
    */
   
   // if console.log is available use it otherwise use alert for now
   if (window.console)
   {
      window.console.log(msg);
   }
   else
   {
      alert(msg);
   }
}
