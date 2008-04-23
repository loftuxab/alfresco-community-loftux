/*
 *** Alfresco.DocumentList
*/
(function()
{
   Alfresco.DocumentList = function(htmlId)
   {
      this.name = "Alfresco.DocumentList";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);
      
      /* Load YUI Components */
      new Alfresco.util.YUILoaderHelper().load(["button", "menu", "containercore"], this.componentsLoaded, this);
      
      return this;
   }
   
   Alfresco.DocumentList.prototype =
   {
      fileUpload: null,
      
      componentsLoaded: function()
      {
         YAHOO.util.Event.onDOMReady(this.init, this, true);
      },
   
      init: function()
      {
         var Dom = YAHOO.util.Dom;
      
         /* File Select Button */
         var fsButton = Dom.getElementsByClassName("doclib-fileSelect-button", "input", this.id)[0];
         var fsMenu = Dom.getElementsByClassName("doclib-fileSelect-menu", "select", this.id)[0];
         var fileSelectButton = new YAHOO.widget.Button(fsButton,
         {
            type: "menu", 
            menu: fsMenu
         });
         fileSelectButton.getMenu().subscribe("click", this.onFileSelectButtonClick, this, true);
         
         /* Show the Upload button if a FileUpload component has registered on the page */
         var fileUploads = Alfresco.util.ComponentManager.find({name:"Alfresco.FileUpload"});
         if (fileUploads.length > 0)
         {
            this.fileUpload = fileUploads[0];
            var fuButton = Dom.getElementsByClassName("doclib-fileUpload-button", "span", this.id)[0];
            var fileUploadButton = new YAHOO.widget.Button(fuButton,
            {
               type: "button"
            });
            fileUploadButton.subscribe("click", this.onFileUploadButtonClick, this, true);
            Dom.removeClass(fuButton, "hiddenComponents");
         }
         
      },
   
      onFileSelectButtonClick: function(type, args)
      {
         if (type == "click")
         {
            var domEvent = args[0]
            var eventTarget = args[1];
            alert(eventTarget.value);
         }
      },

      onFileUploadButtonClick: function(type, args)
      {
         var fuComponent = Alfresco.util.ComponentManager.find({name:"Alfresco.FileUpload"})[0];
         fuComponent.show();
      }

   };
})();
