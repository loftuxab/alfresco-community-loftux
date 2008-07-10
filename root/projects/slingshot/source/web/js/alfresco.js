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
 * Alfresco top-level thirdparty namespace.
 * Used for importing third party javascript functions
 * 
 * @namespace Alfresco
 * @class Alfresco.thirdparty
 */
Alfresco.thirdparty = Alfresco.thirdparty || {};

/**
 * Alfresco top-level messages namespace.
 * 
 * @namespace Alfresco
 * @class Alfresco.messages
 */
Alfresco.messages = Alfresco.messages ||
{
   global: null,
   scope: {}
};


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
}

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
}

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
}

/**
 * Converts a file size in bytes to human readable form
 *
 * @method Alfresco.util.formatFileSize
 * @param fileSize {number} File size in bytes
 * @return {string} The file size in a readable form, i.e 1.2mb
 * @static
 * @throws {Error}
 */
Alfresco.util.formatFileSize = function(fileSize)
{   
   if (fileSize < 999)
   {
      return fileSize + " B"
   }
   else if (fileSize < 999999)
   {
      fileSize = Math.round(fileSize / 1024);
      return fileSize + " KB"
   }
   else if (fileSize < 999999999)
   {
      fileSize = Math.round(fileSize / 1048576);
      return fileSize + " MB";
   }

   fileSize = Math.round(fileSize / 1073741824);
   return fileSize + " GB";
}

/**
 * Formats a Freemarker datetime into more UI-friendly format
 *
 * @method Alfresco.util.formatDate
 * @param date {string} Optional: Date as returned from data webscript. Today used if missing.
 * @param mask {string} Optional: Mask to use to override default.
 * @return {string} Date formatted for UI
 * @static
 */
Alfresco.util.formatDate = function(date)
{
   try
   {
      return Alfresco.thirdparty.dateFormat.apply(this, arguments);
   }
   catch(e)
   {
      return date;
   }
}

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
      type: "button",
      disabled: false
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
 * Add a component's messages to the central message store.
 *
 * @method Alfresco.util.addMessages
 * @param p_obj {object} Object literal containing messages in the correct locale
 * @param p_messageScope {string} Message scope to add these to, e.g. componentId
 * @return {boolean} true if messages added
 * @throws {Error}
 * @static
 */
Alfresco.util.addMessages = function(p_obj, p_messageScope)
{
   if (p_messageScope === undefined)
   {
      throw new Error("messageScope must be defined");
   }
   else if (p_messageScope == "global")
   {
      throw new Error("messageScope cannot be 'global'");
   }
   else
   {
      Alfresco.messages.scope[p_messageScope] = p_obj;
      return true;
   }
   // for completeness...
   return false;
}

/**
 * Resolve a messageId into a message.
 * If a messageScope is supplied, that container will be searched first
 * followed by the "global" message scope.
 *
 * @method Alfresco.util.message
 * @param p_messageId {string} Message id to resolve
 * @param p_messageScope {string} Message scope, e.g. componentId
 * @param multiple-values {string} Values to replace tokens with
 * @return {string} The localized message string or the messageId if not found
 * @throws {Error}
 * @static
 */
Alfresco.util.message = function(p_messageId, p_messageScope)
{
   var msg = p_messageId;
   
   if (typeof p_messageId != "string")
   {
      throw new Error("Missing or invalid argument: messageId");
   }
   
   if ((typeof p_messageScope == "string") && (typeof Alfresco.messages.scope[p_messageScope] == "object"))
   {
      var scopeMsg = Alfresco.messages.scope[p_messageScope][p_messageId];
      if (typeof scopeMsg == "string")
      {
         msg = scopeMsg;
      }
   }
   
   var globalMsg = Alfresco.messages.global[p_messageId];
   if (typeof globalMsg == "string")
   {
      msg = globalMsg;
   }
   
   // Search/replace tokens
   var tokens;
   if ((arguments.length == 3) && (typeof arguments[2] == "object"))
   {
      tokens = arguments[2];
   }
   else
   {
      tokens = Array.prototype.slice.call(arguments).slice(2);
   }
   msg = YAHOO.lang.substitute(msg, tokens);
   
   return msg;
}

/**
 * Fixes the hidden caret problem in Firefox 2.x.
 * Assumes <input> or <textarea> elements are wrapped in a <div class="yui-u"></div>
 *
 * @method Alfresco.util.caretFix
 * @param p_formElement {element|string} Form element to fix input boxes within
 * @static
 */
Alfresco.util.caretFix = function(p_formElement)
{
   if (YAHOO.env.ua.gecko == 1.8)
   {
      if (typeof p_formElement == "string")
      {
         p_formElement = YAHOO.util.Dom.get(p_formElement);
      }
      var nodes = YAHOO.util.Selector.query(".yui-u", p_formElement);
      for (var x = 0; x < nodes.length; x++)
      {
         var elem = nodes[x];
         YAHOO.util.Dom.addClass(elem, "caret-fix");
      }
   }
}

/**
 * Parses a string to a json object and returns it.
 * If str contains invalid json code that is displayed using displayPrompt().
 *
 * @method Alfresco.util.parseJSON
 * @param jsonStr {string} Message id to resolve
 * @return {object} The object representing the json str
 * @throws {Error} if str contains invalid json
 * @static
 */
Alfresco.util.parseJSON = function(jsonStr)
{
   try
   {
      return YAHOO.lang.JSON.parse(jsonStr)
   }
   catch(error)
   {
      Alfresco.util.PopupManager.displayPrompt(
      {
         title: "Failure",
         text: "Can't parse response as json: '" + jsonStr + "'"
      });
      throw error;
   }
}

/**
 * Returns a populated URI template, given a TemplateId and an object literal
 * containing the tokens to be substituted
 *
 * @method Alfresco.util.uriTemplate
 * @param templateId {string} URI TemplateId from web-framework configuration
 * @param obj {object} The object literal containing the token values to substitute
 * @param absolute {boolean} Whether the URL should include the protocol and host
 * @return {string|null} The populated URI or null if templateId not found
 * @static
 */
Alfresco.util.uriTemplate = function(templateId, obj, absolute)
{
   // Check we know about the templateId
   if (!templateId in Alfresco.constants.URI_TEMPLATES)
   {
      return null;
   }
   
   var uri = "";
   var template = Alfresco.constants.URI_TEMPLATES[templateId];
   
   // Page context end with trailing "/", so remove any leading one from the URI template
   if (template[0] == "/")
   {
      template = template.substring(1);
   }
   // Absolute URI needs current protocol and host
   if (absolute)
   {
      uri = location.protocol + "//" + location.host;
   }
   uri += Alfresco.constants.URL_PAGECONTEXT + YAHOO.lang.substitute(template, obj);
   
   return uri;
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
 *
 * @class Alfresco.util.PopupManager
 */
Alfresco.util.PopupManager = function()
{
   return {

      /**
       * The html zIndex startvalue that will be incremented for each popup
       * that is displayed to make sure the popup is visible to the user.
       *
       * @property zIndex
       * @type int
       */
      zIndex: 15,

      /**
       * The default config for the displaying messages, can be overriden
       * when calling displayMessage()
       *
       * @property defaultDisplayMessageConfig
       * @type object
       */
      defaultDisplayMessageConfig:
      {
         title: null,
         text: null,
         effect: YAHOO.widget.ContainerEffect.FADE,
         effectDuration: 0.5,
         displayTime: 2.5,
         modal: false
      },

      /**
       * Intended usage: To quickly assure the user that the expected happened.
       *
       * Displays a message as a popup on the screen.
       * In default mode it fades, is visible for half a second and then fades out.
       *
       * @method displayMessage
       * @param config {object}
       * The config object is in the form of:
       * {
       *    text: {string},         // The message text to display, mandatory 
       *    effect: {YAHOO.widget.ContainerEffect}, // the effect to use when shpwing and hiding the message,
       *                                            // default is YAHOO.widget.ContainerEffect.FADE
       *    effectDuration: {int},  // time in seconds that the effect should be played, default is 0.5
       *    displayTime: {int},     // time in seconds that the message will be displayed, default 1.5
       *    modal: {true}           // if the message should modal (the background overlayed with a gray transparent layer), default is false
       * }
       */
      displayMessage: function(config)
      {
         // Merge the users config with the default config and check mandatory properties
         var c = YAHOO.lang.merge(this.defaultDisplayMessageConfig, config);
         if (c.text === undefined)
         {
            throw new Error("Property text in userConfig must be set");
         }
         // Construct the YUI Dialog that will display the message
         var message = new YAHOO.widget.Dialog("message",
         {
            visible: false,
            close: false,
            draggable:false,
            effect:{effect: c.effect, duration: c.effectDuration},
            modal: c.modal,
            zIndex: this.zIndex++
         });

         // Set the message that should be displayed
         message.setBody(c.text);

         /**
          * Add it to the dom, center it, schedule the fade out of the message
          * and show it.
          */
         message.render(document.body);
         message.center();
         message.subscribe("show", this._delayPopupHide,
         {
            popup: message,
            displayTime: (c.displayTime * 1000)
        }, true);
         message.show();
      },

      /**
       * Gets called after the message has been displayed as long as it was
       * configured.
       * Hides the message from the user.
       *
       * @method _delayPopupHide
       */
      _delayPopupHide: function()
      {         
         YAHOO.lang.later(this.displayTime, this, function()
         {
            this.popup.hide();
         });
      },

      /**
       * The default config for the displaying messages, can be overriden
       * when calling displayPromp()
       *
       * @property defaultDisplayPromptConfig
       * @type object
       */
      defaultDisplayPromptConfig:
      {
         title: null,
         text: null,
         icon: null,
         effect: null,
         effectDuration: 0.5,
         modal: true,
         close: false,
         buttons: [
         {
            text: null, // To early to localize at this time, do it when called instead
            handler: function()
            {
               this.hide();
            },
            isDefault: true
         }]
      },

      /**
       * Intended usage: To inform the user that something unexpected happened
       * OR that ask the user if if an action should be performed.
       *
       * Displays a message as a popup on the screen with a button to make sure
       * the user responds to the prompt.
       *
       * In default mode it shows with an OK button that needs clicking to get closed.
       *
       * @method displayPrompt
       * @param config {object}
       * The config object is in the form of:
       * {
       *    title: {string},       // the title of the dialog, default is null
       *    text: {string},        // the text to display for the user, mandatory
       *    icon: null,            // the icon to display next to the text, default is null
       *    effect: {YAHOO.widget.ContainerEffect}, // the effect to use when showing and hiding the prompt, default is null
       *    effectDuration: {int}, // the time in seconds that the effect should run, default is 0.5
       *    modal: {boolean},      // if a grey transparent overlay should be displayed in the background
       *    close: {boolean},      // if a close icon should be displayed in the right upper corner, default is false
       *    buttons: []            // an array of button configs as described by YUI:s SimpleDialog, default is a single OK button
       * }
       */
      displayPrompt: function(config)
      {
         if (this.defaultDisplayPromptConfig.buttons[0].text === null)
         {
            /**
             * This default value could not be set at instantion time since the
             * localized messages weren't present at that time
             */
            this.defaultDisplayPromptConfig.buttons[0].text = Alfresco.util.message("button.ok", this.name);
         }
         // Merge users config and the default config and check manadatory properties
         var c = YAHOO.lang.merge(this.defaultDisplayPromptConfig, config);
         if (c.text === undefined)
         {
            throw new Error("Property text in userConfig must be set");
         }

         // Create the SImpleDialog that will display the text
         var prompt = new YAHOO.widget.SimpleDialog("prompt",
         {
            visible: false,
            draggable: false,
            effect: c.effect,
            modal: c.modal,
            close: c.close,
            zIndex: this.zIndex++
         });

         // Show the title if it exists
         if (c.title)
         {
            prompt.setHeader(c.title);
         }

         // Show the actual text taht should be prompted for the user
         prompt.setBody(c.text);

         // Show the title if it exists
         if (c.icon)
         {
            prompt.cfg.setProperty("icon", c.icon);
         }

         // Add the buttons to the dialog
         if (c.buttons)
         {
            prompt.cfg.queueProperty("buttons", c.buttons);
         }

         // Add the dialog to the dom, center it and show it.
         prompt.render(document.body);
         prompt.center();
         prompt.show();
      }

   };
}();


/**
 * Helper class for submitting data to serverthat wraps a
 * YAHOO.util.Connect.asyncRequest call.
 *
 * The request methid provides default behaviour for displaying messages on
 * success and error events and simplifies json handling with encoding and decoding.
 *
 * @class Alfresco.util.Ajax
 */
Alfresco.util.Ajax = function()
{
   return {

      /**
       * Constant for contentType of type json
       *
       * @property JSON
       * @type string
       */
      JSON: "application/json",

      /**
       * Constant for method of type GET
       *
       * @property GET
       * @type string
       */
      GET: "GET",

      /**
       * Constant for method of type POST
       *
       * @property POST
       * @type string
       */
      POST: "POST",

      /**
       * Constant for method of type PUT
       *
       * @property PUT
       * @type string
       */
      PUT: "PUT",

      /**
       * Constant for method of type DELETE
       *
       * @property DELETE
       * @type string
       */
      DELETE: "DELETE",

      /**
       * The default request config used by method request()
       *
       * @property defaultRequestConfig
       * @type object
       */
      defaultRequestConfig:
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
         execScripts: false,    // Whether embedded <script> tags will be executed within the successful response
         object: null           // An object that can be passed to be used by the success or failure handlers
      },

      /**
       * Wraps a YAHOO.util.Connect.asyncRequest call and provides some default
       * behaviour for displaying error or success messages, uri encoding and
       * json encoding and decoding.
       *
       * JSON
       *
       * If requestContentType is JSON, config.dataObj (if available) is encoded
       * to a json string and set in the request body.
       *
       * If a json string already has been created by the application it should
       * be passed in as the config.dataStr which will be put in the rewuest body.
       *
       * If responseContentType is JSON the server response is decoded to a
       * json object and set in the "json" attribute in the response object
       * which is passed to the succes or failure callback.
       *
       * PARAMETERS
       *
       * If requestContentType is null, config.dataObj (if available) is encoded
       * to a normal parameter string which is added to the url if method is
       * GET or DELETE and to the request body if method is POST or PUT.
       *
       * FORMS
       * A form can also be passed it and submitted just as desccribed in the
       * YUI documentation.
       *
       * SUCCESS
       *
       * If the request is successful successCallback.fn is called.
       * If successCallback.fn isn't provided successMessage is displayed.
       * If successMessage isn't provided nothing happens.
       *
       * FAILURE
       *
       * If the request fails failureCallback.fn is called.
       * If failureCallback.fn isn't displayed failureMessage is displayed.
       * If failureMessage isn't provided the "best error message as possible"
       * from the server response is displayed.
       *
       * CALLBACKS
       *
       * The success or failure handlers can expect a response object of the
       * following form (they will be called in the scope defined by config.scope)
       *
       * {
       *   config: {object},         // The config object passed in to the request,
       *   serverResponse: {object}, // The response provided by YUI
       *   json: {object}            // The serverResponse parsed and ready as an object
       * }
       *
       * @method request
       * @param config {object} Description of the request that should be made
       * The config object has the following form:
       * {
       *    method: {string}               // GET, POST, PUT or DELETE, default is GET
       *    url: {string},                 // the url to send the request to, mandatory
       *    dataObj: {object},             // Will be encoded to parameters (key1=value1&key2=value2) or a json string if requestContentType is set to JSON
       *    dataStr: {string},             // the request body, will be overriden by the encoding result from dataObj if dataObj is provided
       *    dataForm: {HTMLElement},       // A form object or id that contains the data to be sent with request
       *    requestContentType: {string},  // Set to JSON if json should be used
       *    responseContentType: {string}, // Set to JSON if json should be used
       *    successCallback: {object},     // Callback for successful request, should have the following form: {fn: successHandler, scope: scopeForSuccessHandler}
       *    successMessage: {string},      // Will be displayed using Alfresco.util.displayMessage if successCallback isn't provided
       *    failureCallback: {object},     // Callback for failed request, should have the following form: {fn: failureHandler, scope: scopeForFailureHandler}
       *    failureMessage: {string},      // Will be displayed by Alfresco.util.displayPrompt if no failureCallback isn't provided
       *    execScripts: {boolean},        // Whether embedded <script> tags will be executed within the successful response
       *    object: {object}               // An object that can be passed to be used by the success or failure handlers
       * }
       */
      request: function(config)
      {
         // Merge the user config with the default config and check for mandatory parameters
         var c = YAHOO.lang.merge(this.defaultRequestConfig, config);
         Alfresco.util.assertNotEmpty(c.url, "Parameter 'url' can NOT be null");
         Alfresco.util.assertNotEmpty(c.method, "Parameter 'method' can NOT be null");

         // If a contentType is provided set it in the header
         if (c.requestContentType)
         {
            YAHOO.util.Connect.setDefaultPostHeader(false);
            YAHOO.util.Connect.initHeader("Content-Type", c.requestContentType);
         }

         if (c.requestContentType === this.JSON)
         {
            // If json is used encode the dataObj parameter and put it in the body
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
               else
               {
                  c.dataStr = "{}";
               }
            }
         }
         else
         {
            // Normal URL parameters
            if (c.method.toUpperCase() === this.GET)
            {
               // Encode the dataObj and put it in the url
               c.url += (c.url.indexOf("?") == -1 ? "?" : "&") + this._toParamString(c.dataObj);
            }
            else
            {
               if (c.dataObj)
               {
                  // Enccode the dataObj and put it in the body
                  c.dataStr = this._toParamString(c.dataObj);
               }
            }
         }
         
         if (c.dataForm !== null)
         {
            // Set the form on the connection manager
            YAHOO.util.Connect.setForm(c.dataForm);
         }

         /**
          * The private "inner" callback that will handle json and displaying
          * of messages and prompts
          */
         var callback = 
         {
            success: this._successHandler,
            failure: this._failureHandler,
            scope: this,
            argument:
            {
               config: config
            }
         };

         // Encode url to make sure it is transfered correctly
         c.url = encodeURI(c.url);

         // Make the request
         YAHOO.util.Connect.asyncRequest (c.method, c.url, callback, c.dataStr);
      },

      /**
       * Helper function for pure json requests, where both the request and
       * response are using json. Will result in a call to request() with
       * requestContentType and responseContentType set to JSON.
       *
       * @method request
       * @param config {object} Description of the request that should be made
       */
      jsonRequest: function(config)
      {
         config.requestContentType = this.JSON;
         config.responseContentType = this.JSON;
         this.request(config);
      },

      /**
       * Takes an object and creates a decoded URL parameter string of it.
       * Note! Does not contain a '?' character in the beginning.
       *
       * @method request
       * @param obj
       * @private
       */
      _toParamString: function(obj)
      {
         var params = "";
         var first = true;
         for (attr in obj)
         {
            if (first)
            {
               first = false;
            }
            else
            {
               params += "&";
            }
            // Make sure no user input destroys the url 
            params += encodeURIComponent(attr) + "=" + encodeURIComponent(obj[attr]);
         }
         return params;
      },

      /**
       * Handles successful request triggered by the request() method.
       * If execScripts was requested, retrieve and execute the script(s).
       * Otherwise, fall through to the _successHandlerPostExec function immediately.
       *
       * @method request
       * @param serverResponse
       * @private
       */
      _successHandler: function(serverResponse)
      {
         // Get the config that was used in the request() method
         var config = serverResponse.argument.config;
         
         // Need to execute embedded "<script>" tags?
         if (config.execScripts)
         {
            var scripts = [];
            var script = null;
            var regexp = /<script[^>]*>([\s\S]*?)<\/script>/gi;
            while ((script = regexp.exec(serverResponse.responseText)))
            {
               scripts.push(script[1]);
            }
            scripts = scripts.join("\n");
            
            // Remove the script from the responseText so it doesn't get executed twice
            serverResponse.responseText = serverResponse.responseText.replace(regexp, "");

            // Use setTimeout to execute the script. Note scope will always be "window"
            window.setTimeout(scripts, 0);

            // Delay-call the PostExec function to continue response processing after the setTimeout above
            YAHOO.lang.later(0, this, this._successHandlerPostExec, serverResponse);
         }
         else
         {
            this._successHandlerPostExec(serverResponse);
         }
      },
      
      /**
       * Follow-up handler after successful request triggered by the request() method.
       * If execScripts was requested, this function continues after the scripts have been run.
       * If the responseContentType was set to json the response is decoded
       * for easy access to the success callback.
       * If no success callback is provided the successMessage is displayed
       * using Alfresco.util.PopupManager.displayMessage().
       * If no successMessage is provided nothing happens.
       *
       * @method request
       * @param serverResponse
       * @private
       */
      _successHandlerPostExec: function(serverResponse)
      {
         // Get the config that was used in the request() method
         var config = serverResponse.argument.config;
         var callback = config.successCallback;
         if (callback && typeof callback.fn == "function")
         {
            // User provided a custom successHandler
            var json = null;
            if (config.responseContentType === "application/json")
            {
               // Decode the response since it should be json
               json = Alfresco.util.parseJSON(serverResponse.responseText);
            }

            // Call the success callback in the correct scope
            callback.fn.call((typeof callback.scope == "object" ? callback.scope : this), {
               config: config,
               json: json,
               serverResponse: serverResponse
            }, callback.obj);
         }
         if (config.successMessage)
         {
            /**
             * User provided successMessage.
             */
            Alfresco.util.PopupManager.displayMessage(
            {
               text: config.successMessage
            });
         }
      },

      /**
       * Handles failed request triggered by the request() method.
       * If the responseContentType was set to json the response is decoded
       * for easy access to the failure callback.
       * If no failure callback is provided the failureMessage is displayed
       * using Alfresco.util.PopupManager.displayPrompt().
       * If no failureMessage is provided "the best available server response"
       * is displayed using Alfresco.util.PopupManager.displayPrompt().
       *
       * @method request
       * @param serverResponse
       * @private
       */
      _failureHandler: function(serverResponse)
      {
         // Get the config that was used in the request() method
         var config = serverResponse.argument.config;
         var callback = config.failureCallback;
         if ((callback && typeof callback.fn == "function") || (config.failureMessage))
         {
            if (callback && typeof callback.fn == "function")
            {
               // User provided a custom failureHandler
               var json = null;
               if (config.responseContentType === "application/json")
               {
                  json = Alfresco.util.parseJSON(serverResponse.responseText);
               }
               callback.fn.call((typeof callback.scope == "object" ? callback.scope : this),
               {
                  config: config,
                  json: json,
                  serverResponse: serverResponse
               }, callback.obj);
            }
            if (config.failureMessage)
            {
               /**
               * User did not provide a custom failureHandler, instead display
               * the failureMessage if it exists
               */
               Alfresco.util.PopupManager.displayPrompt(
               {
                  text: config.failureMessage
               });
            }
         }
         else
         {
            /**
             * User did not provide any failure info at all, display as good
             * info as possible from the server response.
             */
            if (config.responseContentType == "application/json")
            {
               var json = Alfresco.util.parseJSON(serverResponse.responseText);
               Alfresco.util.PopupManager.displayPrompt({title: json.status.name, text: json.message});
            }
            else if (serverResponse.statusText)
            {
               Alfresco.util.PopupManager.displayPrompt(
               {
                  title: "Failure",
                  text: serverResponse.statusText
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
 * Helper class for setting the user mouse cursor and making sure its used the
 * same way in the whole application.
 *
 * Use setCursor with the predefined state constants to set the cursor.
 * Each constant has a css selector in base.css where it can be styled
 * differently if needed.
 *
 * @class Alfresco.util.Cursor
 */
Alfresco.util.Cursor = function()
{
   return {

      /**
       * Show cursor in state to indicate that the current element is draggable.
       * Styled through css selector ".draggable" in base.css
       *
       * @property DRAGGABLE
       * @type string
       */
      DRAGGABLE: "draggable",

      /**
       * Show cursor in state to indicate that the current element is dragged.
       * Styled through css selector ".drag" in base.css
       *
       * @property DRAG
       * @type string
       */
      DRAG: "drag",

      /**
       * Show cursor in state to indicate that the element dragged over IS a valid drop point.
       * Styled through css selector ".dropValid" in base.css
       *
       * @property DROP_VALID
       * @type string
       */
      DROP_VALID: "dropValid",

      /**
       * Show cursor in state to indicate that the element dragged over is NOT a valid drop point.
       * Styled through css selector ".dropInvalid" in base.css
       *
       * @property DROP_INVALID
       * @type string
       */
      DROP_INVALID: "dropInvalid",


      /**
       * @method setCursorState
       * @param el {HTMLElement} Object that is dragged and who's style affects the cursor
       * @param cursor {string} Predifined constant from Alfresco.util.CURSOR_XXX
       */
      setCursorState: function(el, cursorState)
      {
         var allStates = [this.DRAGGABLE, this.DRAG, this.DROP_VALID, this.DROP_INVALID];
         for (var i = 0; i < allStates.length; i++)
         {
            var cs = allStates[i];
            if(cs === cursorState)
            {
               YAHOO.util.Dom.addClass(el, cursorState);
            }
            else
            {
               YAHOO.util.Dom.removeClass(el, cs);
            }
         }
      }

   };
}();

/**
 * Transition methods that handles browser limitations.
 *
 * @class Alfresco.util.Anim
 */
Alfresco.util.Anim = function()
{
   return {

      /**
       * The default attributes for a fadeIn or fadeOut call.
       *
       * @property fadeAttributes
       * @type {object} An object literal of the following form:
       * {
       *    adjustDisplay: true, // Will handle style attribute "display" in
       *                         // the appropriate way depending on if its
       *                         // fadeIn or fadeOut, default is true.
       *    callback: null,      // A function that will get called after the fade
       *    scope: this,         // The scope the callback function will get called in
       */
      fadeAttributes: {
         adjustDisplay: true,
         callback: null,
         scope: this
      },

      /**
       * Displays an object with opacity 0, increases the opacity during
       * 0.5 seconds for browsers supporting opcaity.
       *
       * (IE does not support opacity)
       *
       * @method fadeIn
       * @param el {HTMLElement} element to fade in
       * @param attributes
       */
      fadeIn: function A_fadeIn(el, attributes)
      {
         return this._fade(el, true, attributes);
      },

      /**
       * Displays an object with opacity 1, decreases the opacity during
       * 0.5 seconds for browsers supporting opacity and finally hides it.
       *
       * (IE does not support opacity)
       *
       * @method fadeOut
       * @param el {HTMLElement} element to fade out
       * @param attributes
       */
      fadeOut: function A_fadeOut(el, attributes)
      {
         return this._fade(el, false, attributes);
      },

      /**
       * @method _fade
       * @param el {HTMLElement} element to fade in
       * @param fadeIn {boolean} true if fadeIn false if fadeOut
       * @param attributes
       */
      _fade: function A__fade(el, fadeIn, attributes)
      {
         var Dom = YAHOO.util.Dom;
         el = Dom.get(el);
         // No manadatory elements in attributes, avoid null checks below though
         attributes = YAHOO.lang.merge(this.fadeAttributes, attributes ? attributes : {});
         var adjustDisplay = attributes.adjustDisplay;

         // todo test against functionality instead of browser
         var supportsOpacity = YAHOO.env.ua.ie === 0;

         // Prepare el before fade
         if(supportsOpacity)
         {
            Dom.setStyle(el, "opacity", fadeIn ? 0 : 1);
         }

         // Show the element, transparent if opacity supported,
         // otherwise its visible and the "fade in" is finished
         if(supportsOpacity)
         {
            Dom.setStyle(el, "visibility", "visible");
         }
         else
         {
            Dom.setStyle(el, "visibility", fadeIn ? "visible" : "hidden");
         }

         // Make sure element is displayed
         if(adjustDisplay && Dom.getStyle(el, "display") === "none")
         {
            Dom.setStyle(el, "display", "");
         }

         // Put variables in scope so they can be used in the callback below
         var fn = attributes.callback;
         var scope = attributes.scope;
         var myEl = el;
         if(supportsOpacity)
         {
            // Do the fade (from value/opacity has already been set above)
            var fade = new YAHOO.util.Anim(el, { opacity: { to: fadeIn ? 1 : 0 } }, 0.5);
            fade.onComplete.subscribe(function(e) {
               if(!fadeIn && adjustDisplay)
               {
                  // Hide element from Dom if its a fadeOut
                  YAHOO.util.Dom.setStyle(myEl, "display", "none");
               }
               if(fn)
               {
                  // Call custom callback
                  fn.call(scope ? scope : this);
               }
            });
            fade.animate();
         }
         else
         {
            if(!fadeIn && adjustDisplay)
            {
               // Hide element from Dom if its a fadeOut
               YAHOO.util.Dom.setStyle(myEl, "display", "none");
            }
            if(fn)
            {
               // Call custom callback
               fn.call(scope ? scope : this);
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
   if (!Alfresco.constants.DEBUG)
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
      /* Too annoying!
      alert(msg);
      */
   }
}


/**
 * Format a date object to a user-specified mask
 * Modified to retrieve i18n strings from Alfresco.messages
 *
 * Original code:
 *    Date Format 1.1
 *    (c) 2007 Steven Levithan <stevenlevithan.com>
 *    MIT license
 *    With code by Scott Trenda (Z and o flags, and enhanced brevity)
 *
 * http://blog.stevenlevithan.com/archives/date-time-format
 *
 * @method Alfresco.thirdparty.dateFormat
 * @return {string}
 * @static
 */
Alfresco.thirdparty.dateFormat = function()
{
   /*** dateFormat
   	Accepts a date, a mask, or a date and a mask.
   	Returns a formatted version of the given date.
   	The date defaults to the current date/time.
   	The mask defaults ``"ddd mmm d yyyy HH:MM:ss"``.
   */
   var DAY_NAMES = Alfresco.util.message("days").split(",");
	var MONTH_NAMES = Alfresco.util.message("months").split(",");
   
   var dateFormat = function () {
   	var   token        = /d{1,4}|m{1,4}|yy(?:yy)?|([HhMsTt])\1?|[LloZ]|"[^"]*"|'[^']*'/g,
      		timezone     = /\b(?:[PMCEA][SDP]T|(?:Pacific|Mountain|Central|Eastern|Atlantic) (?:Standard|Daylight|Prevailing) Time|(?:GMT|UTC)(?:[-+]\d{4})?)\b/g,
      		timezoneClip = /[^-+\dA-Z]/g,
      		pad = function (value, length) {
      			value = String(value);
      			length = parseInt(length) || 2;
      			while (value.length < length)
      				value = "0" + value;
      			return value;
      		};

   	// Regexes and supporting functions are cached through closure
   	return function (date, mask) {
   		// Treat the first argument as a mask if it doesn't contain any numbers
   		if (
   			arguments.length == 1 &&
   			(typeof date == "string" || date instanceof String) &&
   			!/\d/.test(date)
   		) {
   			mask = date;
   			date = undefined;
   		}

   		date = date ? new Date(date) : new Date();
   		if (isNaN(date))
   			throw "invalid date";

   		var dF = dateFormat;
   		mask   = String(dF.masks[mask] || mask || dF.masks["default"]);

   		var	d = date.getDate(),
   			D = date.getDay(),
   			m = date.getMonth(),
   			y = date.getFullYear(),
   			H = date.getHours(),
   			M = date.getMinutes(),
   			s = date.getSeconds(),
   			L = date.getMilliseconds(),
   			o = date.getTimezoneOffset(),
   			flags = {
   				d:    d,
   				dd:   pad(d),
   				ddd:  dF.i18n.dayNames[D],
   				dddd: dF.i18n.dayNames[D + 7],
   				m:    m + 1,
   				mm:   pad(m + 1),
   				mmm:  dF.i18n.monthNames[m],
   				mmmm: dF.i18n.monthNames[m + 12],
   				yy:   String(y).slice(2),
   				yyyy: y,
   				h:    H % 12 || 12,
   				hh:   pad(H % 12 || 12),
   				H:    H,
   				HH:   pad(H),
   				M:    M,
   				MM:   pad(M),
   				s:    s,
   				ss:   pad(s),
   				l:    pad(L, 3),
   				L:    pad(L > 99 ? Math.round(L / 10) : L),
   				t:    H < 12 ? "a"  : "p",
   				tt:   H < 12 ? "am" : "pm",
   				T:    H < 12 ? "A"  : "P",
   				TT:   H < 12 ? "AM" : "PM",
   				Z:    (String(date).match(timezone) || [""]).pop().replace(timezoneClip, ""),
   				o:    (o > 0 ? "-" : "+") + pad(Math.floor(Math.abs(o) / 60) * 100 + Math.abs(o) % 60, 4)
   			};

   		return mask.replace(token, function ($0) {
   			return ($0 in flags) ? flags[$0] : $0.slice(1, $0.length - 1);
   		});
   	};
   }();

   // Some common format strings
   dateFormat.masks = {
   	"default":       "ddd mmm d yyyy HH:MM:ss",
   	shortDate:       "m/d/yy",
   	mediumDate:      "mmm d, yyyy",
   	longDate:        "mmmm d, yyyy",
   	fullDate:        "dddd, mmmm d, yyyy",
   	shortTime:       "h:MM TT",
   	mediumTime:      "h:MM:ss TT",
   	longTime:        "h:MM:ss TT Z",
   	isoDate:         "yyyy-mm-dd",
   	isoTime:         "HH:MM:ss",
   	isoDateTime:     "yyyy-mm-dd'T'HH:MM:ss",
   	isoFullDateTime: "yyyy-mm-dd'T'HH:MM:ss.lo"
   };

   // Internationalization strings
   dateFormat.i18n = {
   	dayNames: DAY_NAMES,
   	monthNames: MONTH_NAMES
   };
 
   /**
    * Alfresco wrapper: delegate to wrapped code
    */
   return dateFormat.apply(this, arguments);
}