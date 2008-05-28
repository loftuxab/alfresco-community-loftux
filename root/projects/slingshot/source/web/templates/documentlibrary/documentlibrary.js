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
 * DocumentLibrary template.
 * 
 * @namespace Alfresco
 * @class Alfresco.DocumentLibrary
 */
(function()
{
   /**
    * DocumentLibrary constructor.
    * 
    * @return {Alfresco.DocumentLibrary} The new DocumentLibrary instance
    * @constructor
    */
   Alfresco.DocumentLibrary = function DocumentLibrary_constructor()
   {
      // Load YUI Components
      Alfresco.util.YUILoaderHelper.require(["resize"], this.onComponentsLoaded, this);
            
      return this;
   };
   
   Alfresco.DocumentLibrary.prototype =
   {
      /**
       * Minimum Filter Panel width.
       * 
       * @property MIN_FILTER_PANEL_WIDTH
       * @type int
       */
      MIN_FILTER_PANEL_WIDTH: 100,

      /**
       * Default Filter Panel width.
       * 
       * @property DEFAULT_FILTER_PANEL_WIDTH
       * @type int
       */
      DEFAULT_FILTER_PANEL_WIDTH: 240,

      /**
       * Maximum Filter Panel width.
       * 
       * @property MAX_FILTER_PANEL_WIDTH
       * @type int
       */
      MAX_FILTER_PANEL_WIDTH: 750,
      
      /**
       * Object container for storing YUI widget instances.
       * 
       * @property widgets
       * @type object
       */
       widgets: {},

      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function DocumentLibrary_onComponentsLoaded()
      {
         YAHOO.util.Event.onDOMReady(this.onReady, this, true);
      },
   
      /**
       * Fired by YUI when parent element is available for scripting.
       * Template initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function DocumentLibrary_onReady()
      {
         // Horizontal Resizer
         this.widgets.horizResize = new YAHOO.util.Resize("divDocLibraryFilters",
         {
            handles: ["r"],
            minWidth: this.MIN_FILTER_PANEL_WIDTH,
            maxWidth: this.MAX_FILTER_PANEL_WIDTH
         });
         this.widgets.horizResize.on("resize", function(eventTarget)
         {
            this.onDocLibraryResize(eventTarget.width);
         }, this, true);
         
         // Initial size
         this.widgets.horizResize.resize(null, null, this.DEFAULT_FILTER_PANEL_WIDTH, 0, 0, true);
      },
   
      /**
       * Fired by via resize event listener.
       *
       * @method onDocLibraryResize
       */
      onDocLibraryResize: function DocumentLibrary_onDocLibraryResize(width)
      {
         var Dom = YAHOO.util.Dom;
         
         if (typeof width != 'undefined')
         {
            // Reset widget height to ensure correct rendering
            Dom.setStyle(Dom.get("divDocLibraryFilters"), "height", "auto");
            // 6px breathing space for resize gripper
            Dom.setStyle(Dom.get("divDocLibraryDocs"), "margin-left", 6 + width + "px");
         }
      }
   };
})();

// Instantiate the Document Library template
new Alfresco.DocumentLibrary();