/*
 *** Alfresco.Header
*/

Alfresco.MySites = function()
{
   /* Shortcuts */
   var Dom = YAHOO.util.Dom,
         Event = YAHOO.util.Event;

   return {
      ID: null,

      createPanel: null,
      modalPanelConfig: {
         fixedcenter: true,
         close:false,
         draggable:false,
         zindex:4,
         modal:true,
         visible:true
      },

      init: function()
      {
         Event.onDOMReady(this.start, this, true);
      },

      start: function()
      {

         /* Html Dialog */
         var createDiv = Dom.getElementsByClassName("mysites-createdialog-panel", "div", this.ID)[0];
         this.createPanel = new YAHOO.widget.Panel(createDiv, this.modalPanelConfig);
      },

      showCreateDialog: function()
      {
          this.createPanel.render(document.body);
          this.createPanel.show();
      }

   }
}();

Alfresco.MySites.init();

