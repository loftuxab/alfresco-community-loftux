/**
 * Copyright (C) 2005-2010 Alfresco Software Limited.
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
 
/**
 * Dashlet Resizer.
 * 
 * @namespace Alfresco.widget
 * @class Alfresco.widget.DashletResizer
 */

/**
* YUI Library aliases
*/
var Dom = YAHOO.util.Dom,
   Event = YAHOO.util.Event,
   Selector = YAHOO.util.Selector;

/**
 * Dashlet Resizer constructor.
 * 
 * @return {Alfresco.widget.DashletResizer} The new Alfresco.widget.DashletResizer instance
 * @constructor
 */
Alfresco.widget.DashletResizer = function DashletResizer_constructor(htmlId, dashletId)
{
   this.name = "Alfresco.widget.DashletResizer";
   this.id = htmlId;
   this.dashletId = dashletId;

   // Load YUI Components
   Alfresco.util.YUILoaderHelper.require(["resize", "selector"], this.onComponentsLoaded, this);
   
   // Initialise prototype properties
   this.widgets = {};
         
   return this;
};

Alfresco.widget.DashletResizer.prototype =
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
       * The initial dashlet height.
       * 
       * @property dashletHeight
       * @type int
       */
      dashletHeight: -1,

      /**
       * Minimum Dashlet height.
       * 
       * @property minDashletHeight
       * @type int
       * @default 100
       */
      minDashletHeight: 80,

      /**
       * Maximum Dashlet height.
       * 
       * @property maxDashletHeight
       * @type int
       * @default 1200
       */
      maxDashletHeight: 1200
   },
   
   /**
    * The dashletId.
    * 
    * @property dashletId
    * @type string
    */
   dashletId: "",

   /**
    * Object container for storing YUI widget instances.
    * 
    * @property widgets
    * @type object
    */
   widgets: null,
   
   /**
    * DOM node of dashlet
    * Resizer will look for first child DIV of dashlet with class="dashlet" and attach to this
    *
    * @property dashlet
    * @type object
    * @default null
    */
   dashlet: null,

   /**
    * DOM node of dashlet body
    * Resizer will look for first child DIV of dashlet with class="body" and resize this element
    *
    * @property dashletBody
    * @type object
    * @default null
    */
   dashletBody: null,

   /**
    * Difference in height between dashlet offsetHeight and dashletBody CSS height
    *
    * @property heightDelta
    * @type int
    * @default 0
    */
   heightDelta: 0,

   /**
    * Set multiple initialization options at once.
    *
    * @method setOptions
    * @param obj {object} Object literal specifying a set of options
    * @return {Alfresco.widget.DashletResizer} returns 'this' for method chaining
    */
   setOptions: function DashletResizer_setOptions(obj)
   {
      this.options = YAHOO.lang.merge(this.options, obj);
      return this;
   },
   
   /**
    * Fired by YUILoaderHelper when required component script files have
    * been loaded into the browser.
    *
    * @method onComponentsLoaded
    */
   onComponentsLoaded: function DashletResizer_onComponentsLoaded()
   {
      Event.onDOMReady(this.onReady, this, true);
   },

   /**
    * Fired by YUI when parent element is available for scripting.
    * Template initialisation, including instantiation of YUI widgets and event listener binding.
    *
    * @method onReady
    */
   onReady: function DashletResizer_onReady()
   {
      // Have permission to resize?
      if (!Alfresco.constants.DASHLET_RESIZE)
      {
         return;
      }
      
      // Find dashlet div
      this.dashlet = Selector.query("div.dashlet", Dom.get(this.id), true);
      if (!this.dashlet)
      {
         return;
      }
      Dom.addClass(this.dashlet, "resizable");

      // Find dashlet body div?
      this.dashletBody = Selector.query("div.body", this.dashlet, true);
      if (!this.dashletBody)
      {
         return;
      }

      // Difference in height between dashlet and dashletBody for resize events
      var origHeight = Dom.getStyle(this.dashlet, "height");
      if (origHeight == "auto")
      {
         origHeight = this.dashlet.offsetHeight - parseInt(Dom.getStyle(this.dashlet, "padding-bottom"));
      }
      else
      {
         origHeight = parseInt(origHeight, 10);
      }
      this.heightDelta = origHeight - parseInt(Dom.getStyle(this.dashletBody, "height"), 10);

      // Create and attach Vertical Resizer
      this.widgets.resizer = new YAHOO.util.Resize(this.dashlet,
      {
         handles: ["b"],
         minHeight: this.options.minDashletHeight,
         maxHeight: this.options.maxDashletHeight
      });
      
      // During resize event handler
      this.widgets.resizer.on("resize", function()
      {
         this.onResize();
      }, this, true);
      
      // End resize event handler
      this.widgets.resizer.on("endResize", function(eventTarget)
      {
         this.onEndResize(eventTarget.height);
      }, this, true);

      // Clear the fixed-pixel width the dashlet has been given
      Dom.setStyle(this.dashlet, "width", "");
   },

   /**
    * Fired by resize event listener.
    *
    * @method onResize
    */
   onResize: function DashletResizer_onResize()
   {
      var height = parseInt(Dom.getStyle(this.dashlet, "height"), 10) - this.heightDelta;
      Dom.setStyle(this.dashletBody, "height", height + "px");
   },

   /**
    * Fired by end resize event listener.
    *
    * @method onResize
    * @param h Height - not used
    */
   onEndResize: function DashletResizer_onEndResize(h)
   {
      // Clear the fixed-pixel width the dashlet has been given
      Dom.setStyle(this.dashlet, "width", "");
      
      Alfresco.util.Ajax.jsonRequest(
      {
         method: "POST",
         url: Alfresco.constants.URL_SERVICECONTEXT + "modules/dashlet/config/" + this.dashletId,
         dataObj:
         {
            height: parseInt(Dom.getStyle(this.dashlet, "height"), 10) - this.heightDelta
         },
         successCallback: function(){},
         successMessage: null,
         failureCallback: function(){},
         failureMessage: null
      });
   }
};
