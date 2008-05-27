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

      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "container", "connection", "selector", "json", "event"], this.componentsLoaded, this);

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
         if(this.dialog)
         {
            this.dialog.show();
         }
         else
         {
            Alfresco.util.Ajax.request(
            {
               url: Alfresco.constants.URL_SERVICECONTEXT + "modules/createSite",
               dataObj: {htmlid: this.id},
               successCallback: this.templateLoaded,
               failureMessage: "Could not load create site template",
               scope: this
            });
         }
      },
      
      templateLoaded: function(response)
      {
         var Dom = YAHOO.util.Dom;

         var div = document.createElement("div");
         div.innerHTML = response.serverResponse.responseText;
         this.dialog = new YAHOO.widget.Panel(div,
         {
            fixedcenter: true,
            visible: false
         });
         this.dialog.render(document.body);
         
         var okButton = new YAHOO.widget.Button(this.id + "-ok-button", {type: "submit"});
         //okButton.subscribe("click", this.onOkButtonClick, this, true);
         
         var createSiteForm = new Alfresco.forms.Form(this.id + "-createSite-form");
         createSiteForm.addValidation(this.id + "-shortName", Alfresco.forms.validation.mandatory, null, "blur");
         createSiteForm.setShowSubmitStateDynamically(true);
         createSiteForm.setSubmitElements(okButton);
         createSiteForm.setAJAXSubmit(true, {successCallback: this.onCreateSiteSuccess});
         createSiteForm.setSubmitAsJSON(true);
         createSiteForm.init();

         var cancelButton = new YAHOO.widget.Button(this.id + "-cancel-button", {type: "button"});
         cancelButton.subscribe("click", this.onCancelButtonClick, this, true);

         this.dialog.show();
      },

      onCancelButtonClick: function(type, args)
      {
        this.dialog.hide();
      },

      onCreateSiteSuccess: function(response)
      {
         document.location.href = Alfresco.constants.URL_CONTEXT + "page/collaboration-dashboard?site=" + response.json.shortName;
      }

   };
})();

/* Dummy instance to load optional YUI components early */
new Alfresco.module.CreateSite(null);