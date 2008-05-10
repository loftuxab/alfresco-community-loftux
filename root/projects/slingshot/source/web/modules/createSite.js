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
         Alfresco.util.Ajax.serviceRequest(
         {
            url: "modules/createSite",
            dataObj: {htmlid: this.id},
            success: this.templateLoaded,
            failureMessage: "Could not load create site template",
            scope: this
         });
         //YAHOO.util.Connect.asyncRequest("GET", Alfresco.constants.URL_SERVICECONTEXT + "modules/createSite?htmlid=" + this.id, callback);
      },
      
      templateLoaded: function(response)
      {
         var Dom = YAHOO.util.Dom;

         Dom.get(this.id).innerHTML = response.serverResponse.responseText;
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

         // The getFormInfo call will be replaced by Gav's forms runtime
         // ...and perhaps its that runtime that will make the proxyRequest call instead?
         // To do that it will need to take in success, failure, successMessage and failureMessage
         var formInfo = Alfresco.util.Ajax.getFormInfo(this.id + "-createSite-form");
         Alfresco.util.Ajax.jsonProxyRequest(
         {
            url: formInfo.action,
            dataObj: formInfo.data,
            success: this.onCreateSiteSucces,
            failureMessage: "Could not create site"
         });
      },

      onCreateSiteSucces: function(response)
      {
         document.location.href = Alfresco.constants.URL_CONTEXT + "page/collaboration/dashboard?site=" + response.json.shortName;
      }

   };
})();

/* Dummy instance to load optional YUI components early */
new Alfresco.module.CreateSite(null);