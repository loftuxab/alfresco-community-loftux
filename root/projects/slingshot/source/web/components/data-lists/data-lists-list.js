/**
 * DataListList component.
 * 
 * Displays a list of datalists
 * 
 * @namespace Alfresco
 * @class Alfresco.DataListList
 */
(function()
{
    
   /**
   * YUI Library aliases
   */
   var Dom = YAHOO.util.Dom,
       Event = YAHOO.util.Event,
       Selector = YAHOO.util.Selector,
       Bubbling = YAHOO.Bubbling;

   /**
    * DataList constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.DataListList} The new DataListList instance
    * @constructor
    */
   Alfresco.DataListList = function(htmlId)
   {
      Alfresco.DataListList.superclass.constructor.call(this, "Alfresco.DataListList", htmlId, ["button", "container"]);
      
      this.initEvents();
      return this;
   };
   
   YAHOO.extend(Alfresco.DataListList, Alfresco.component.Base,
   {
      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       */
      options:
      {
      /**
        * Current siteId.
        * 
        * @property siteId
        * @type string
        * @default null
        */
         siteId: ""
      },

      /**
       * Fired by YUI when parent element is available for scripting
       * @method onReady
       */
      onReady: function DataList_onReady()
      {

      },
      
      onInteractionEvent: function onInteractionEvent(e)
      {
         var evtName = e,
            evtArgs = Array.prototype.slice.apply(arguments);
            elTarget = Event.getTarget(e);

         if (e.type==='click') 
         {
            if ( Selector.test(elTarget, 'button#newListBtn-button') )
            {
               // Create the CreateList module if it doesnt exist
               if (this.modules['createList'] === undefined)
               {
                  this.modules['createList']  = Alfresco.module.getCreateListInstance();
                  this.modules['createList'].setOptions({siteId:this.options.siteId})
               }
               // and show it
               this.modules['createList'].show();
            }
         } 
      },
      
      initEvents: function initEvents()
      {
         Event.on(this.id,'click',this.onInteractionEvent,this, true);
      },
   });
   
})();