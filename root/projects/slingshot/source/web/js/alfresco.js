/* Ensure Alfresco root object exists */
var Alfresco = (typeof Alfresco == "undefined" || !Alfresco ? {} : Alfresco);

/* Site-wide constants */
Alfresco.constants = Alfresco.constants ||
{
   /* URL_CONTEXT set by header component using ${url.context} */
   URL_CONTEXT: "./"
};

/* Ensure Alfresco.util object exists */
Alfresco.util = Alfresco.util || {};


/*
   Alfresco.util.YUILoaderHelper

   Wrapper for helping components specify their YUI components.
   Constructor returns self to enable chaining (see example)
   
   function load(Array components, Object callback, Object scope)
   e.g.
      new Alfresco.util.YUILoaderHelper().load(["button", "menu"], this.componentsLoaded, this)
 */

(function()
{
   Alfresco.util.YUILoaderHelper = function()
   {
      this._yuiLoader = new YAHOO.util.YUILoader(
      {
         base: Alfresco.constants.URL_CONTEXT + "yui/",
         loadOptional: true,
         filter: "DEBUG"
      });
      
      return this;
   };
   
   Alfresco.util.YUILoaderHelper.prototype =
   {
      load: function(p_aComponents, p_oCallback, p_oScope)
      {
         this._yuiLoader.insert(
         {
            require: p_aComponents,
            scope: p_oScope,
            onSuccess: p_oCallback
         });
      }
   };
})();


/*
   Alfresco.util.ComponentManager

   Keeps track of Alfresco components on a page.
   Components should register() upon creation to be compliant.
 */
Alfresco.util.ComponentManager = function()
{
   var components = [];
   
   return (
   {
      /* Components must register here to be discoverable */
      register: function(p_oComponent)
      {
         components.push(p_oComponent);
      },

      /* Find a previously-registered component
         Matches using AND on passed-in object properties
         e.g.
            find({name:"DocumentList", id:"template.documentlist.documentlibrary"})
       */
      find: function(p_oParams)
      {
         var found = [];
         var bMatch;
         
         for each (component in components)
         {
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
   });
}();
