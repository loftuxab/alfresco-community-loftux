/*
 *** Alfresco.DocumentLibrary
*/
(function()
{
   /* Constants */
   var DEFAULT_MinFilterPanelWidth = 150,
      DEFAULT_MaxFilterPanelWidth = 600;
   
   Alfresco.DocumentLibrary = function()
   {
      /* Load YUI Components */
      new Alfresco.util.YUILoaderHelper().load(["resize"], this.componentsLoaded, this);
            
      return this;
   };
   
   Alfresco.DocumentLibrary.prototype =
   {
      componentsLoaded: function()
      {
         YAHOO.util.Event.onDOMReady(this.init, this, true);
      },
   
      init: function()
      {
         /* Horizontal Resizer */
         var horizResize = new YAHOO.util.Resize('divDoclibFilters',
         {
            handles: ['r'],
            minWidth: DEFAULT_MinFilterPanelWidth,
            maxWidth: DEFAULT_MaxFilterPanelWidth
         });

         /* Resizer listener event */
         horizResize.addListener('resize', function(eventTarget)
         {
            this.doclibResize(eventTarget.width);
         }, this, true);

         /* Initial size */
         horizResize.resize(null, null, this.DEFAULT_MinFilterPanelWidth, 0, 0, true);
      },
   
      doclibResize: function(width)
      {
         var Dom = YAHOO.util.Dom;
         
         if (typeof width != 'undefined')
         {
            /* 6px breathing space for resize gripper */
            Dom.setStyle(Dom.get('divDoclibDocs'), 'margin-left', 6 + width + 'px');
         }
      }
   };
})();

new Alfresco.DocumentLibrary();