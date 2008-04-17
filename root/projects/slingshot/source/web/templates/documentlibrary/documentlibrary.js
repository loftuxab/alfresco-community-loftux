/*
 *** Alfresco.DocumentLibrary
*/
Alfresco.DocumentLibrary = function()
{
   /* Shortcuts */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;

   return {
      /* Constants */
      DEFAULT_MinFilterPanelWidth: 150,
      DEFAULT_MaxFilterPanelWidth: 600,
      
      init: function()
      {
         Event.onDOMReady(this.start, this, true);
      },
      
      start: function()
      {
         /* Horizontal Resizer */
         var horizResize = new YAHOO.util.Resize('divDoclibFilters',
         {
            handles: ['r'],
            minWidth: this.DEFAULT_MinFilterPanelWidth,
            maxWidth: this.DEFAULT_MaxFilterPanelWidth
         });

         /* Resizer listener event */
         horizResize.addListener('resize', function(eventTarget)
         {
            this.doclibResize(eventTarget.width);
         }, this, true);

         /* Initial size */
         horizResize.resize(null, null, this.DEFAULT_MinFilterPanelWidth, 0, 0, true);
         
         /*
         if (typeof Alfresco.DocumentList != "undefined")
         {
            alert("I have a document list");
         }
         if (typeof Alfresco.FileUpload != "undefined")
         {
            alert("I can upload files");
         }
         */
      },
   
      doclibResize: function(width)
      {
         if (typeof width != 'undefined')
         {
            /* 6px breathing space for resize gripper */
            Dom.setStyle(Dom.get('divDoclibDocs'), 'margin-left', 6 + width + 'px');
         }
      }
   }
}();

Alfresco.DocumentLibrary.init();
