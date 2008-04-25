/*
 *** Alfresco.Header
*/
(function()
{
   Alfresco.Header = function(htmlId)
   {
      this.name = "Alfresco.Header";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);
      
      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "menu", "containercore"], this.componentsLoaded, this);

      return this;
   };

   Alfresco.Header.prototype =
   {
      componentsLoaded: function()
      {
         YAHOO.util.Event.onContentReady(this.id, this.init, this, true);
      },

      init: function()
      {
         var Dom = YAHOO.util.Dom;

         /* Splitbuttons */
         var ssButton = Dom.getElementsByClassName("header-sitesSelect-button", "input", this.id)[0];
         var ssMenu = Dom.getElementsByClassName("header-sitesSelect-menu", "select", this.id)[0];
         var sitesSelectButton = new YAHOO.widget.Button(ssButton,
         {
            type: "split",
            menu: ssMenu
         });

         var csButton = Dom.getElementsByClassName("header-colleaguesSelect-button", "input", this.id)[0];
         var csMenu = Dom.getElementsByClassName("header-colleaguesSelect-menu", "select", this.id)[0];
         var colleaguesSelectButton = new YAHOO.widget.Button(csButton,
         {
            type: "split",
            menu: csMenu
         });
      }
   };
})();
