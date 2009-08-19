/**
 * Patch to Menu to allow empty groups to remain in the menu structure.
 * Required by: Sites dynamic drop-down menu.
 * Patches: YUI 2.6.0, YUI 2.7.0
 * Escalated: Yes, but closed as "by design"
 */
(function()
{
   var Lang = YAHOO.lang,
      Dom = YAHOO.util.Dom,
      _FIRST_OF_TYPE = "first-of-type";

   YAHOO.widget.Menu.prototype._removeItemFromGroupByIndex = function (p_nGroupIndex, p_nItemIndex, p_keepEmptyGroup) {

       var nGroupIndex = Lang.isNumber(p_nGroupIndex) ? p_nGroupIndex : 0,
           aGroup = this._getItemGroup(nGroupIndex),
           aArray,
           oItem,
           oUL;

       if (aGroup) {

           aArray = aGroup.splice(p_nItemIndex, 1);
           oItem = aArray[0];
    
           if (oItem) {
    
               // Update the index and className properties of each member        
               this._updateItemProperties(nGroupIndex);
    
               if (aGroup.length === 0 && !p_keepEmptyGroup) {
    
                   // Remove the UL
                   oUL = this._aListElements[nGroupIndex];
    
                   if (this.body && oUL) {
                       this.body.removeChild(oUL);
                   }
    
                   // Remove the group from the array of items
                   this._aItemGroups.splice(nGroupIndex, 1);
    
                   // Remove the UL from the array of ULs
                   this._aListElements.splice(nGroupIndex, 1);
    
                   /*
                        Assign the "first-of-type" class to the new first UL 
                        in the collection
                   */
                   oUL = this._aListElements[0];
    
                   if (oUL) {
                       Dom.addClass(oUL, _FIRST_OF_TYPE);
                   }            
               }

               this.itemRemovedEvent.fire(oItem);
               this.changeContentEvent.fire();
           }
       }

      // Return a reference to the item that was removed
      return oItem;
   };

   YAHOO.widget.Menu.prototype._removeItemFromGroupByValue = function (p_nGroupIndex, p_oItem, p_keepEmptyGroup) {

       var aGroup = this._getItemGroup(p_nGroupIndex),
           nItems,
           nItemIndex,
           returnVal,
           i;

       if (aGroup) {
           nItems = aGroup.length;
           nItemIndex = -1;
    
           if (nItems > 0) {
               i = nItems-1;
               do {
                   if (aGroup[i] == p_oItem) {
                       nItemIndex = i;
                       break;    
                   }
               }
               while (i--);
        
               if (nItemIndex > -1) {
                   returnVal = this._removeItemFromGroupByIndex(p_nGroupIndex, nItemIndex, p_keepEmptyGroup);
               }
           }
       }
       return returnVal;
   };

   YAHOO.widget.Menu.prototype.removeItem = function (p_oObject, p_nGroupIndex, p_keepEmptyGroup) {
       var oItem,
          returnVal;
    
       if (!Lang.isUndefined(p_oObject)) {
           if (p_oObject instanceof YAHOO.widget.MenuItem) {
               oItem = this._removeItemFromGroupByValue(p_nGroupIndex, p_oObject, p_keepEmptyGroup);           
           }
           else if (Lang.isNumber(p_oObject)) {
               oItem = this._removeItemFromGroupByIndex(p_nGroupIndex, p_oObject, p_keepEmptyGroup);
           }

           if (oItem) {
               oItem.destroy();
               returnVal = oItem;
           }
       }

      return returnVal;
   };
})();

/**
 * Patch to Container to prevent IE6 trying to set properties on elements that have been removed from the DOM.
 * This function is called via a setTimer(), so this patch fixes a race condition.
 * Required by: Document List "Loading Document Library..." pop-up.
 * Patches: YUI 2.7.0
 */
(function()
{
   /**
   * Adjusts the size of the shadow based on the size of the element.
   * @method sizeUnderlay
   */
   YAHOO.widget.Panel.prototype.sizeUnderlay = function()
   {
       var oUnderlay = this.underlay,
           oElement;

       if (oUnderlay) {
           oElement = this.element;
           if (oElement) {
              oUnderlay.style.width = oElement.offsetWidth + "px";
              oUnderlay.style.height = oElement.offsetHeight + "px";
           }
       }
   };
})();

/**
 * Patch to Dom.get for the case where a form has an element with a name="id".
 * Required by: DoD 5015 module, Disposition Schedule Edit component.
 * Patches: YUI 2.7.0
 * Known bug: Yes. See http://yuilibrary.com/projects/yui2/ticket/2527744
 * Patch: http://github.com/yui/yui2/commit/83bad40c45411577812825f091ba397d81a2d86f
 */
(function()
{
   var Y = YAHOO.util,
      NODE_TYPE = 'nodeType';
   
   /**
    * Returns an HTMLElement reference.
    * @method get
    * @param {String | HTMLElement |Array} el Accepts a string to use as an ID for getting a DOM reference, an actual DOM reference, or an Array of IDs and/or HTMLElements.
    * @return {HTMLElement | Array} A DOM reference to an HTML element or an array of HTMLElements.
    */
   YAHOO.util.Dom.get = function(el)
   {
       var id, nodes, c, i, len;

       if (el) {
           if (el[NODE_TYPE] || el.item) { // Node, or NodeList
               return el;
           }

           if (typeof el === 'string') { // id
               id = el;
               el = document.getElementById(el);
               if (el && el.attributes["id"].value === id) { // IE: avoid false match on "name" attribute
               return el;
               } else if (el && document.all) { // filter by name
                   el = null;
                   nodes = document.all[id];
                   for (i = 0, len = nodes.length; i < len; ++i) {
                       if (nodes[i].id === id) {
                           return nodes[i];
                       }
                   }
               }
               return el;
           }
           
           if (el.DOM_EVENTS) { // YAHOO.util.Element
               el = el.get('element');
           }

           if ('length' in el) { // array-like 
               c = [];
               for (i = 0, len = el.length; i < len; ++i) {
                   c[c.length] = Y.Dom.get(el[i]);
               }
               
               return c;
           }

           return el; // some other object, just pass it back
       }

       return null;
   };
})();