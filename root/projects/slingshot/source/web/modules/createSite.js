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

         YAHOO.util.Connect.asyncRequest("GET", Alfresco.constants.URL_SERVICECONTEXT + "modules/createSite", callback);
      },
      
      templateLoaded: function(response)
      {
         YAHOO.util.Dom.get(this.id).innerHTML = response.responseText;

         var handleSubmit = function()
         {
            this.submit();
         }

         var handleCancel = function()
         {
            this.cancel();
         }

         this.dialog = new YAHOO.widget.Dialog(this.id,
         {
            width: "30em",
            fixedcenter: true,
            visible: false,
            constraintoviewport: true,
            buttons:
            [
               {
                  text: "Create", handler: handleSubmit, isDefault: true
               },
               {
                  text: "Cancel", handler: handleCancel
               }
            ]
         });
         this.dialog.render();
         this.dialog.show();
      },
      
      templateFailed: function()
      {
         YAHOO.util.Dom.get(this.id).innerHTML = "<b>Couldn't get template</b>";
      }
   };
})();

/* Dummy instance to load optional YUI components early */
new Alfresco.module.CreateSite(null);