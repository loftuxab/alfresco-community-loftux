/* Ensure Alfresco root object exists */
if (typeof Alfresco == "undefined" || !Alfresco)
{
   var Alfresco = {};
}

/* Ensure top-level Alfresco namespaces exist */
Alfresco.constants = Alfresco.constants || {};
Alfresco.module = Alfresco.module || {};
Alfresco.util = Alfresco.util || {};
Alfresco.logger = Alfresco.logger || {};

/*
   Alfresco.util.appendArrayToObject
   Appends an array onto an object
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
};

/*
   Alfresco.util.arrayToObject
   Converts an array into an object
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

/*
   Alfresco.util.assert
   Asserts param contains a proper value
   Didn't want to use the YAHOO.util.Assert methods since it would mean yet another of a new yui package ("yuitest")
 */
Alfresco.util.assertNotEmpty = function(param, message)
{
   if(typeof param == "undefined" || !param || param === "")
   {
      throw new Error(message);
   }
};

/*
   Alfresco.util.YUILoaderHelper

   Wrapper for helping components specify their YUI components.
   
   e.g.
      Alfresco.util.YUILoaderHelper.require(["button", "menu"], this.componentsLoaded, this)
 */
Alfresco.util.YUILoaderHelper = function()
{
   var yuiLoader = null;
   var callbacks = [];
   var initialLoaderComplete = false;
   
   return {
      require: function(p_aComponents, p_oCallback, p_oScope)
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
      
      loadComponents: function()
      {
         if (yuiLoader !== null)
         {
            yuiLoader.insert(null, "js");
         }
      },

      onLoaderComplete: function()
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


/*
   Alfresco.util.ComponentManager

   Keeps track of Alfresco components on a page.
   Components should register() upon creation to be compliant.
 */
Alfresco.util.ComponentManager = function()
{
   var components = [];
   
   return {
      /* Components must register here to be discoverable */
      register: function(p_oComponent)
      {
         components.push(p_oComponent);
      },

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

/*
   Alfresco.util.PopupManager

   Provides a common interface for displaying popups in various forms
 */
Alfresco.util.PopupManager = function()
{

   return {

      zIndex: 15,

      displayMessageConfig: {
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
         var message = new YAHOO.widget.Dialog("message",
            {
               visible: false,
               close: false,
               draggable:false,
               effect:{effect: c.effect, duration: c.effectDuration},
               modal: c.modal,
               zIndex: this.zIndex++
            }
         );
         message.setBody(c.text);
         message.render(document.body);
         message.center();
         if(c.autoHide)
         {
            message.subscribe("show", this._delayPopupHide, {popup: message, displayTime: (c.displayTime * 1000)}, true);
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

      displayPromptConfig: {
         title: null,
         text: null,
         icon: null,
         autoHide: true,
         effect: null,
         effectDuration: 0.5,
         modal: false,
         close: false,
         buttons: [{ text:"OK", handler: function(){ this.hide(); }, isDefault: true }]
      },

      displayPrompt: function(userConfig)
      {
         var c = YAHOO.lang.merge(this.displayPromptConfig, userConfig);
         var prompt = new YAHOO.widget.SimpleDialog("prompt", {
            visible:false,
            draggable:false,
            effect: c.effect,
            modal: c.modal,
            close: c.close,
            zIndex: this.zIndex++
         });
         if(c.title)
         {
            prompt.setHeader(c.title);
         }
         prompt.setBody(c.text);
         if(c.icon)
         {
            prompt.cfg.setProperty("icon", c.icon);
         }
         // todo: Hmm how shall the OK label be localized?
         if(c.buttons){
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


/*
   Alfresco.util.Ajax
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
         scope: this,          // The scope in which the success and failure handlers will be called.
         dataObj: null,        // Will be encoded to parameters (key1=value1&key2=value2)
                               // or a json string if contentType is set to JSON
         dataStr: null,        // Will be used in the request body, could be a already created parameter or json string
                               // Will be overriden by the encoding result from dataObj if dataObj is provided
         dataForm: null,       // A form object or id that contains the data to be sent with request
         requestContentType: null,    // Set to JSON if json should be used
         responseContentType: null,    // Set to JSON if json should be used
         successCallback: null,// Will be called in the scop of scope with a response object literal described below
         successMessage: null, // Will be displayed by Alfresco.util.displayMessage if no success handler is provided
         failureCallback: null,// Will be called in the scop of scope with a response object literal described below
         failureMessage: null,  // Will be displayed by Alfresco.util.displayPrompt if no failure handler is provided
         object: null           // An object that can be passed to be used by the success or failure handlers
      },

      jsonRequest: function(config)
      {
         config.requestContentType = this.JSON;
         config.responseContentType = this.JSON;
         this.request(config);
      },
      
      /*
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
            /* todo: decode any url reserved characters */
            params += attr + "=" + obj[attr];
         }
         return params;
      },

      _successHandler: function(serverResponse)
      {
         var config = serverResponse.argument.config;
         if(config.successCallback)
         {
            /* User provided a custom successHandler */
            var json = null;
            if(config.responseContentType === "application/json"){
               if(serverResponse.responseText && serverResponse.responseText.length > 0)
               {
                  json = YAHOO.lang.JSON.parse(serverResponse.responseText);
               }
            }
            YAHOO.lang.later(0, (config.scope ? config.scope : this), config.successCallback, {config: config, json: json, serverResponse: serverResponse});
         }
         else if(config.successMessage)
         {
            /* User did not provide a custom successHandler but a custom successMessage */
            Alfresco.util.PopupManager.displayMessage({text: config.successMessage});
         }
      },

      _failureHandler: function(serverResponse)
      {
         var config = serverResponse.argument.config;
         if(config.failureCallback)
         {
            /* User provided a custom failureHandler */
            var json = null;
            if(config.responseContentType === "application/json"){
               /* todo: When error response is in valid json format */
               //json = YAHOO.lang.JSON.parse(serverResponse.responseText);
            }
            YAHOO.lang.later(0, (config.scope ? config.scope : this), config.failureCallback, {config: config, json: json, serverResponse: serverResponse});
         }
         else if(config.failureMessage)
         {
            /* User did not provide a custom failureHandler but a custom failureMessage */
            Alfresco.util.PopupManager.displayPrompt({text: config.failureMessage});
         }
         else
         {
            // User did not provide any failure info, display as good info as possible from the server response instead
            if(config.responseContentType === "application/json"){
               var json = null;
               /* todo: When error response is in valid json format */
               // json = YAHOO.lang.JSON.parse(serverResponse.responseText);
               // Alfresco.util.PopupManager.displayPrompt({title: json.status.name, text: json.message});
               Alfresco.util.PopupManager.displayPrompt({title: serverResponse.statusText, text: "Failure"});
            }
            else if(serverResponse.statusText)
            {
               Alfresco.util.PopupManager.displayPrompt({title: serverResponse.statusText});
            }
            else
            {
               Alfresco.util.PopupManager.displayPrompt({text: "Error sending data to server."});
            }
         }
      }

   };

}();

Alfresco.logger.isDebugEnabled = function()
{
   // TODO: make this switchable
   
   return false;
}

Alfresco.logger.debug = function(p1, p2)
{
   var msg;
   
   if (typeof p1 == "string" && p2 != null)
   {
      msg = p1 + " " + YAHOO.lang.dump(p2);
   }
   else
   {
      msg = YAHOO.lang.dump(p1);
   }
   
   // TODO: use an inline div first, then support the YUI logger and
   //       log to that if possible.
   
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
