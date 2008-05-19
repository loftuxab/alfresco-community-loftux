/*
 *** Alfresco.DocumentLibrary
*/
(function()
{
   /* Constants */
   var DEFAULT_MIN_FILTER_PANEL_WIDTH = 150,
      DEFAULT_MAX_FILTER_PANEL_WIDTH = 600;
   
   Alfresco.DocumentLibrary = function DocumentLibrary_constructor()
   {
      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["resize"], this.componentsLoaded, this);
            
      return this;
   };
   
   Alfresco.DocumentLibrary.prototype =
   {
      componentsLoaded: function DocumentLibrary_componentsLoaded()
      {
         YAHOO.util.Event.onDOMReady(this.init, this, true);
      },
   
      init: function DocumentLibrary_init()
      {
         /* Horizontal Resizer */
         var horizResize = new YAHOO.util.Resize('divDoclibFilters',
         {
            handles: ['r'],
            minWidth: DEFAULT_MIN_FILTER_PANEL_WIDTH,
            maxWidth: DEFAULT_MAX_FILTER_PANEL_WIDTH
         });

         /* Resizer listener event */
         horizResize.addListener('resize', function(eventTarget)
         {
            this.doclibResize(eventTarget.width);
         }, this, true);

         /* Initial size */
         horizResize.resize(null, null, this.DEFAULT_MIN_FILTER_PANEL_WIDTH, 0, 0, true);
      },
   
      doclibResize: function DocumentLibrary_doclibResize(width)
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