/*
 *** Alfresco.module.CreateSite
*/
(function()
{
   Alfresco.module.CreateSite = function(containerId)
   {
      this.name = "Alfresco.module.CreateSite";
      this.id = containerId;
      
      this.dialog = null;
      
      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "container", "connection"], this.componentsLoaded, this);

      return this;
   };

   Alfresco.module.CreateSite.prototype =
   {
      componentsLoaded: function()
      {
         /* Shortcut for dummy instance */
         if (this.id === null)
         {
            return;
         }
      },
      
      show: function()
      {
         var callback =
         {
            success: this.templateLoaded,
            failure: this.templateFailed,
            scope: this
         }

         YAHOO.util.Connect.asyncRequest("GET", Alfresco.constants.URL_SERVICECONTEXT + "modules/createSite?htmlid=" + this.id, callback);
      },
      
      templateLoaded: function(response)
      {
         var Dom = YAHOO.util.Dom;

         Dom.get(this.id).innerHTML = response.responseText;
         this.dialog = new YAHOO.widget.Dialog(this.id,
         {
            fixedcenter: true,
            visible: false
         });

         var clButton = Dom.get(this.id + "-ok-button");
         var clearButton = new YAHOO.widget.Button(clButton, {type: "button"});
         clearButton.subscribe("click", this.onOkButtonClick, this, true);

         this.dialog.render();
         this.dialog.show();
      },


      onOkButtonClick: function(type, args)
      {
         Alfresco.util.Request.doJsonForm(this.id + "-createSite-form", null,
            {failureMessage: "Could not create site"}
               , this.onCreateSiteSucces
         );

      },

      onCreateSiteSucces: function(response)
      {
         document.location.href = Alfresco.constants.URL_CONTEXT + "page/collaboration/dashboard?site=" + response.json.shortName;
      },

      templateFailed: function(o)
      {
         Alfresco.util.PopupManager.displayPrompt({ text: "Could not load create site template"});
      }
      
   };
})();

/* Dummy instance to load optional YUI components early */
new Alfresco.module.CreateSite(null);