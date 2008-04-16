/*
 *** Alfresco.DocumentList
*/
Alfresco.DocumentList = function()
{
   /* Shortcuts */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;

   return {
      ID: null,
      
      init: function()
      {
         Event.onDOMReady(this.start, this, true);
      },
      
      start: function()
      {
         /* File Select Button */
         var fsButton = Dom.getElementsByClassName("doclib-fileSelect-button", "input", this.ID)[0];
         var fsMenu = Dom.getElementsByClassName("doclib-fileSelect-menu", "select", this.ID)[0];
         var fileSelectButton = new YAHOO.widget.Button(fsButton,
         {
            type: "menu", 
            menu: fsMenu
         });
         fileSelectButton.getMenu().subscribe("click", this.onFileSelectButtonClick, this, true);
      },
      
      onFileSelectButtonClick: function(type, args)
      {
         if (type == "click")
         {
            var domEvent = args[0]
            var eventTarget = args[1];
            alert(eventTarget.value);
         }
      }
   }
}();

Alfresco.DocumentList.init();
