/* Ensure Alfresco root object exists */
if (typeof Alfresco == "undefined" || !Alfresco)
{
   var Alfresco = {};
}

/* Site-wide constants */
Alfresco.constants = Alfresco.constants ||
{
   /* AJAX Proxy URI stem */
   PROXY_URI: window.location.protocol + "//" + window.location.host + "/slingshot/proxy?endpoint=",
   
   /* THEME set by template header using ${theme} */
   THEME: "./",
   
   /* TODO: Remove ticket when AJAX/Proxy authentication in place */
   /* http://localhost:8080/alfresco/service/api/login?u={username}&pw={password} */
   TICKET: "TICKET_5afa7148ab77870ae0fb3997e5b569ef59da0319",

   /* URL_CONTEXT set by template header using ${url.context} */
   URL_CONTEXT: "./"
};


/* Ensure top-level Alfresco namespaces exist */
Alfresco.ui = Alfresco.ui || {};
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
   Alfresco.util.YUILoaderHelper

   Wrapper for helping components specify their YUI components.
   
   e.g.
      Alfresco.util.YUILoaderHelper.require(["button", "menu"], this.componentsLoaded, this)
 */
Alfresco.util.YUILoaderHelper = function()
{
   var yuiLoader = null;
   var callbacks = [];
   
   return {
      require: function(p_aComponents, p_oCallback, p_oScope)
      {
         if (yuiLoader === null)
         {
            yuiLoader = new YAHOO.util.YUILoader(
            {
               base: Alfresco.constants.URL_CONTEXT + "yui/",
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
            yuiLoader.insert();
         }
      },

      onLoaderComplete: function()
      {
         for (var i = 0; i < callbacks.length; i++)
         {
            callbacks[i].fn.call(callbacks[i].scope);
         }
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

      displayMessage: function(message, delay)
      {
         alert(message);
      },

      displayPrompt: function()
      {
         alert("Not implemented");
      },

      displayDialog: function()
      {
         alert("Not implemented");
      }

   };

}();


/*
   Alfresco.util.Request

   Helper functions for sending GET and POST using parameters and/or json.
   Will send everything through the proxy as soon possible.
 */

Alfresco.util.Request = function()
{

   return {

      /**
       *
       * @param url
       * @param getDataObj An object literal that should be sent as a get-parameter-string in the url
       * @param reqInfo
       * @param successHandler
       * @param failurehandler
       */
      doGet: function(url, getDataObj, reqInfo, successHandler, failurehandler)
      {
         // todo: transform getDataObj to a get-parameter-string and append it to the url
         var getParameterString = "";
         url += getParameterString;
         this.request("GET", url, null, null, reqInfo, successHandler, failurehandler);
      },

      /**
       *
       * @param url
       * @param postDataObj An object literal that should be sent as a post-parameter-string in the post body
       * @param reqInfo
       * @param successHandler
       * @param failurehandler
       */
      doPost: function(url, postDataObj, reqInfo, successHandler, failurehandler)
      {
         // todo: transform postDataObj to a post-parameter-string
         var postParameterString = "";
         this.request("POST", url, postParameterString, null, reqInfo, successHandler, failurehandler);
      },

      /**
       *
       * @param url
       * @param postDataObj An object literal that should be sent as a json-data in the post body
       * @param reqInfo
       * @param successHandler
       * @param failurehandler
       */
      doJsonPost: function(url, postDataObj, reqInfo, successHandler, failurehandler)
      {
         var jsonString = YAHOO.lang.JSON.stringify(postDataObj);
         this.request("POST", url, jsonString, "application/json", reqInfo, successHandler, failurehandler);
      },

      /**
       * Takes a form and submits it as an ajax post.
       *
       * @param formId The form that should be submitted
       * @param whiteList The fields in the form that should be submitted, if null all fields will be submitted.
       * @param reqInfo
       * @param successHandler
       * @param failureHandler
       */
      doForm: function(formId, whiteList, reqInfo, successHandler, failureHandler){
         var formInfo = this.getFormInfo(formId, whiteList);
         if(formInfo.method == "POST")
         {
            this.doPost(formInfo.action, formInfo.data, reqInfo, successHandler, failureHandler);
         }
         else
         {
            // assume method is "GET"
            this.doGet(formInfo.action, formInfo.data, reqInfo, successHandler, failureHandler);
         }
      },

      /**
       * Takes a form and submits it as a json post.
       *
       * @param formId The form that should be submitted
       * @param whiteList The fields in the form that should be submitted, if null all fields will be submitted.
       * @param reqInfo
       * @param successHandler
       * @param failureHandler
       */
      doJsonForm: function(formId, whiteList, reqInfo, successHandler, failureHandler){
         var formInfo = this.getFormInfo(formId, whiteList);
         if(formInfo.method != "POST")
         {
            throw new Error("Form " + formId + " must have method POST to use doJsonForm()");
         }
         this.doJsonPost(formInfo.action, formInfo.data, reqInfo, successHandler, failureHandler);
      },

      /**
       * Helper method for getting information about a form, collects the url, method and data that should be sent
       *
       * @param formId The id of the HtmlElement of type form
       * @param whiteList A list of the fields that should be submitted, if null all fields are used.
       *                  NOTE: not used at the moment!
       */
      getFormInfo: function(formId, whiteList)
      {
         var form = document.getElementById(formId);
         // todo: Make sure to use this when Proxy can handle POST:s
         // var url = Alfresco.constants.PROXY_URI + "http://localhost:8080/alfresco/service/api/sites&alf_method=POST&alf_ticket=" + Alfresco.constants.TICKET + "&";
         if(form != null && (form.tagName == "FORM" || form.tagName == "form"))
         {
            var formInfo = {};
            formInfo.action = form.action;
            formInfo.method = form.method ? form.method.toUpperCase() : "";
            formInfo.data = {};
            var length = form.elements.length;
            for (var i = 0; i < length; i++)
            {
               var element = form.elements[i];
               var name = element.name;
               var value = element.value;
               if(name)
               {
                  formInfo.data[name] = value;
               }
            }
            return formInfo;
         }
         else
         {
            throw new Error("Could not send form, since element with id " + formId + (form ? " is not of type form" : " does not exist" ));
         }
      },

      /**
       * Helper method for sending the actual request
       *
       * @param method Request method, ie POST or GET
       * @param url The url to the server resource that will handle the request
       * @param postData The data that should be put in the body if method is POST
       * @param contentType Set to application/json if json should be sent, default is ___________
       * @param reqInfo An object that will be available to the success/failure handlers, through argument.
       * @param successHandler A custom handler if Alfresco.util.Request.defaultSuccessHandler shouldn't be used.
       * @param failurehandler A custom handler if Alfresco.util.Request.defaultFailureHandler shouldn't be used.
       */
      request: function(method, url, postData, contentType, reqInfo, successHandler, failurehandler)
      {
         if(contentType){
            YAHOO.util.Connect.setDefaultPostHeader(false);
            YAHOO.util.Connect.initHeader("Content-Type", contentType);
         }
         var callback = {
            success: (successHandler ? successHandler : this.defaultSuccessHandler),
            failure: (failurehandler ? failurehandler: this.defaultFailureHandler),
            argument: reqInfo ? [reqInfo] : null
         };
         YAHOO.util.Connect.asyncRequest (method, url, callback, postData);
      },

      /**
       * Will do a PopupManager.displayMessage() of serverResponse.argument.successMessage if present.
       * The successMessage will be present if a reqInfo object with failureMessage was provided for the request.
       * @param serverResponse
       */
      defaultSuccessHandler: function(serverResponse)
      {
         if(serverResponse.argument && serverResponse.argument[0] && serverResponse.argument[0].successMessage)
         {
            Alfresco.util.PopupManager.displayMessage(serverResponse.argument[0].successMessage);
         }
      },

      /**
       * Will do a PopupManager.displayMessage() with serverResponse.argument.failureMessage if present.
       * The failureMessage will be present if a reqInfo object with failureMessage was provided for the request.
       * Otherwise the server error from serverResponse.statusText will be displayed.
       * @param serverResponse
       */
      defaultFailureHandler: function(serverResponse)
      {
         if(serverResponse.argument && serverResponse.argument[0] && serverResponse.argument[0].failureMessage)
         {
            Alfresco.util.PopupManager.displayMessage(serverResponse.argument[0].failureMessage);
         }
         else if(serverResponse.statusText)
         {
            Alfresco.util.PopupManager.displayMessage(serverResponse.statusText);
         }
         else
         {
            Alfresco.util.PopupManager.displayMessage("Error sending data to server.");
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
