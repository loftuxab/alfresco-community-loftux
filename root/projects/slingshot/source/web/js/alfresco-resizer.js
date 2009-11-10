/**
 * Copyright (C) 2005-2008 Alfresco Software Limited.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.

 * As a special exception to the terms and conditions of version 2.0 of 
 * the GPL, you may redistribute this Program in connection with Free/Libre 
 * and Open Source Software ("FLOSS") applications as described in Alfresco's 
 * FLOSS exception.  You should have recieved a copy of the text describing 
 * the FLOSS exception, and it is also available here: 
 * http://www.alfresco.com/legal/licensing
 */
 
/**
 * Alfresco Resizer.
 * 
 * @namespace Alfresco.widget
 * @class Alfresco.widget.Resizer
 */
/**
 * Resizer constructor.
 * 
 * @return {Alfresco.widget.Resizer} The new Alfresco.widget.Resizer instance
 * @constructor
 */
Alfresco.widget.Resizer = function Resizer_constructor(p_name)
{
   // Load YUI Components
   Alfresco.util.YUILoaderHelper.require(["resize"], this.onComponentsLoaded, this);
   
   this.name = p_name;
   
   // Initialise prototype properties
   this.widgets = {};
         
   return this;
};

Alfresco.widget.Resizer.prototype =
{
   /**
    * Minimum Filter Panel height.
    * 
    * @property MIN_FILTER_PANEL_HEIGHT
    * @type int
    */
   MIN_FILTER_PANEL_HEIGHT: 200,

   /**
    * Minimum Filter Panel width.
    * 
    * @property MIN_FILTER_PANEL_WIDTH
    * @type int
    */
   MIN_FILTER_PANEL_WIDTH: 140,

   /**
    * Default Filter Panel width.
    * 
    * @property DEFAULT_FILTER_PANEL_WIDTH
    * @type int
    */
   DEFAULT_FILTER_PANEL_WIDTH: 160,

   /**
    * Maximum Filter Panel width.
    * 
    * @property MAX_FILTER_PANEL_WIDTH
    * @type int
    */
   MAX_FILTER_PANEL_WIDTH: 500,
   
   /**
    * Object container for storing YUI widget instances.
    * 
    * @property widgets
    * @type object
    */
   widgets: null,
   
   /**
    * DOM ID of left-hand container DIV
    *
    * @property divLeft
    * @type string
    * @default "divLeft"
    */
   divLeft: "divLeft",

   /**
    * DOM ID of right-hand container DIV
    *
    * @property divRight
    * @type string
    * @default "divRight"
    */
   divRight: "divRight",
   
   /**
    * Used to monitor document length
    *
    * @property documentHeight
    * @type int
    */
   documentHeight: -1,

   /**
    * Fired by YUILoaderHelper when required component script files have
    * been loaded into the browser.
    *
    * @method onComponentsLoaded
    */
   onComponentsLoaded: function Resizer_onComponentsLoaded()
   {
      YAHOO.util.Event.onDOMReady(this.onReady, this, true);
   },

   /**
    * Fired by YUI when parent element is available for scripting.
    * Template initialisation, including instantiation of YUI widgets and event listener binding.
    *
    * @method onReady
    */
   onReady: function Resizer_onReady()
   {
      // Horizontal Resizer
      this.widgets.horizResize = new YAHOO.util.Resize(this.divLeft,
      {
         handles: ["r"],
         minWidth: this.MIN_FILTER_PANEL_WIDTH,
         maxWidth: this.MAX_FILTER_PANEL_WIDTH
      });

      // Before and End resize event handlers
      this.widgets.horizResize.on("beforeResize", function(eventTarget)
      {
         this.onResize(eventTarget.width);
      }, this, true);
      this.widgets.horizResize.on("endResize", function(eventTarget)
      {
         this.onResize(eventTarget.width);
      }, this, true);

      // Recalculate the vertical size on a browser window resize event
      YAHOO.util.Event.on(window, "resize", function(e)
      {
         this.onResize();
      }, this, true);
      
      // Monitor the document height for ajax updates
      this.documentHeight = YAHOO.util.Dom.getDocumentHeight();
      
      YAHOO.lang.later(1000, this, function()
      {
         var h = YAHOO.util.Dom.getDocumentHeight();
         if (Math.abs(this.documentHeight - h) > 4)
         {
            this.documentHeight = h;
            this.onResize();
         }
      }, null, true);
               
      // Initial size
      if (YAHOO.env.ua.ie > 0)
      {
         this.widgets.horizResize.resize(null, this.widgets.horizResize.get("element").offsetHeight, this.DEFAULT_FILTER_PANEL_WIDTH, 0, 0, true);
      }
      else
      {
         this.widgets.horizResize.resize(null, this.widgets.horizResize.get("height"), this.DEFAULT_FILTER_PANEL_WIDTH, 0, 0, true);
      }

      this.onResize(this.DEFAULT_FILTER_PANEL_WIDTH);
   },

   /**
    * Fired by via resize event listener.
    *
    * @method onResize
    */
   onResize: function Resizer_onResize(width)
   {
      var Dom = YAHOO.util.Dom,
         cn = Dom.get(this.divLeft).childNodes,
         handle = cn[cn.length - 1];
      
      Dom.setStyle(this.divLeft, "height", "auto");
      Dom.setStyle(handle, "height", "");

      var h = Dom.getDocumentHeight() - Dom.get("alf-ft").offsetHeight - Dom.get("alf-hd").offsetHeight;
      
      if (YAHOO.env.ua.ie === 6)
      {
         var hd = Dom.get("alf-hd"), tmpHeight = 0;
         for (var i = 0, il = hd.childNodes.length; i < il; i++)
         {
            tmpHeight += hd.childNodes[i].offsetHeight;
         }
         h = Dom.get("alf-ft").parentNode.offsetTop - tmpHeight; 
      }
      if (h < this.MIN_FILTER_PANEL_HEIGHT)
      {
         h = this.MIN_FILTER_PANEL_HEIGHT;
      }

      Dom.setStyle(handle, "height", h + "px");
      
      if (width !== undefined)
      {
         // 8px breathing space for resize gripper
         Dom.setStyle(this.divRight, "margin-left", 8 + width + "px");
      }
   }
};
