(function()
{
   Alfresco.MySites = function(htmlId)
   {
      this.name = "Alfresco.MySites";
      this.id = htmlId;

      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "container"], this.componentsLoaded, this);

      return this;
   }

   Alfresco.MySites.prototype =
   {
      createSitePanel: null,
      modalPanelConfig: {
         fixedcenter: true,
         close:false,
         draggable:false,
         zindex:4,
         modal:true,
         visible:true
      },

      componentsLoaded: function()
      {
         YAHOO.util.Event.onContentReady(this.id, this.init, this, true);
      },

      init: function()
      {
         var Dom = YAHOO.util.Dom;

         /* Create Site Button */
         var csButton = Dom.getElementsByClassName("mysites-createSite-button", "span", this.id)[0];
         var createSiteButton = new YAHOO.widget.Button(csButton,
         {
            type: "button"
         });
         createSiteButton.subscribe("click", this.onCreateSiteButtonClick, this, true);

         var createSiteDiv = Dom.getElementsByClassName("mysites-createSite-panel", "div", this.id)[0];
         this.createSitePanel = new YAHOO.widget.Panel(createSiteDiv, this.modalPanelConfig);
      },

      onCreateSiteButtonClick: function(event)
      {
         this.createSitePanel.render(document.body);
         this.createSitePanel.show();
      }

   };
})();
