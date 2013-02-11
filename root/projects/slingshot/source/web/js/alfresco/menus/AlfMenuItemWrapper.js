/**
 * Copyright (C) 2005-2013 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
define(["dojo/_base/declare",
        "dijit/_WidgetBase",
        "dijit/_TemplatedMixin",
        "dijit/_Contained",
        "dojo/text!./templates/AlfMenuItemWrapper.html",
        "alfresco/core/Core",
        "dojo/dom-construct"], 
        function(declare, _WidgetBase, _TemplatedMixin, _Contained, template, AlfCore, domConstruct) {
   
   /**
    * This class has been created to act as the main container for the popup referenced by "alfresco/menus/AlfMenuBarPopup".
    * It currently just acts as a container object but is intended to allow instances of "alfresco/menus/AlfMenuGroup" to be
    * added into a menu bar popup.
    */
   return declare([_WidgetBase, _TemplatedMixin, AlfCore], {
      
      /**
       * The HTML template to use for the widget.
       * @property template {String}
       */
      templateString: template,
      
      /**
       * An array of the CSS files to use with this widget.
       * 
       * @property cssRequirements {Array}
       */
      cssRequirements: [{cssFile:"./css/AlfMenuItemWrapper.css"}],
      
      /**
       * The item to be wrapped.
       * 
       * @property item {object}
       */
      item: null,
      
      /**
       * Add the assigned item (the thing that is to be wrapped) to the appropriate DOM node in the HTML template. 
       * 
       * @method postCreate
       */
      postCreate: function alf_menus_AlfMenuItemWrapper__postCreate() {
         if (this.item)
         {
            domConstruct.place(this.item.domNode, this._itemNode);
         }
      },
      
      /**
       * This function is implemented to indicate whether or not the wrapped item can be focused. It is focusable if
       * the item has a focus function that can be called.
       * 
       * @method isFocusable
       * @returns {boolean} true if there is a wrapped item and it has a focus function.
       */
      isFocusable: function  alf_menus_AlfMenuItemWrapper__isFocusable() {
         var focusable = (this.item && this.item.focus);
         this.alfLog("log", "Item Wrapper focusable?", focusable);
         return focusable;
      },

      /**
       * This function is implemented to delegate the handling of focus events to the wrapped item.
       * 
       * @method focus
       */
      focus: function alf_menus_AlfMenuItemWrapper__focus() {
         this.alfLog("log", "Item Wrapper focus");
         if (this.item && this.item.focus)
         {
            this.item.focus();
         }
      },
      
      /**
       * This function is implemented to delegate the handling of _setSelected calls to the wrapped item.
       * 
       * @method _setSelected
       * @param {boolean} Indicates whether ot not the item is selected
       */
      _setSelected: function alf_menus_AlfMenuItemWrapper___setSelected(selected) {
         this.alfLog("log", "Item Wrapper _setSelected", selected);
         if (this.item && this.item._setSelected)
         {
            this.item._setSelected(selected);
         }
      },
      
      /**
       * This function is implemented to delegate the handling of onClick calls to the wrapped item.
       * 
       * @method onClick
       * @param {object} evt The click event
       */
      onClick: function alf_menus_AlfMenuItemWrapper__onClick(evt){
         this.alfLog("log", "Item Wrapper onClick", evt);
         if (this.item && typeof this.item.onClick == "function")
         {
            this.item.onClick(evt);
         }
      }
   });
});