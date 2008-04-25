/* Ensure Alfresco root object exists */
var Alfresco = {};

/* Site-wide constants */
Alfresco.constants = Alfresco.constants || {};

Alfresco.constants =
{
   /* URL_CONTEXT set by header component using ${url.context} */
   URL_CONTEXT: "./",
   
   /* AJAX Proxy URI stem */
   PROXY_URI: window.location.protocol + "//" + window.location.host + "/slingshot/proxy?endpoint=",
   
   /* TODO: Remove ticket when AJAX/Proxy authentication in place */
   /* http://localhost:8080/alfresco/service/api/login?u={username}&pw={password} */
   TICKET: "TICKET_6b23531441346ccde360c0666d879557103d0404"
};


/* Ensure Alfresco.util object exists */
Alfresco.util = Alfresco.util || {};

/*
   Alfresco.util.appendArray
   
   Appends an array onto an object
 */
Alfresco.util.appendArray = function(obj, arr)
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
   Alfresco.util.YUILoaderHelper

   Wrapper for helping components specify their YUI components.
   
   e.g.
      Alfresco.util.YUILoaderHelper.require(["button", "menu"], this.componentsLoaded, this)
 */
Alfresco.util.YUILoaderHelper = function()
{
   var preloads = {};
   var yuiLoader = null;
   var callbacks = [];
   
   return {
      preloaded: function(p_aComponents)
      {
         Alfresco.util.appendArray(preloads, p_aComponents);
      },
      
      require: function(p_aComponents, p_oCallback, p_oScope)
      {
         if (yuiLoader === null)
         {
            yuiLoader = new YAHOO.util.YUILoader(
            {
               loadOptional: false,
               filter: "",
               onSuccess: Alfresco.util.YUILoaderHelper.onLoaderComplete,
               scope: this
            });
         }
         
         if (p_aComponents.length > 0)
         {
            /* Have we preloaded all the YUI components the caller requires? */
            var isPreloaded = true;
            for (var i = 0; i < p_aComponents.length; i++)
            {
               if ((p_aComponents[i] != "") && !(p_aComponents[i] in preloads))
               {
                  isPreloaded = false;
                  break;
               }
            }
            if (isPreloaded && (p_oCallback !== null))
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
         yuiLoader.insert(
         {
            base: Alfresco.constants.URL_CONTEXT + "yui/"
         });
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
