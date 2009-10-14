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
 * Dashboard SiteLinks component.
 * 
 * @namespace Alfresco
 * @class Alfresco.SiteLinks
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Event = YAHOO.util.Event;

   /**
    * Dashboard SiteLinks constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.SiteLinks} The new component instance
    * @constructor
    */
   Alfresco.SiteLinks = function Links_constructor(htmlId)
   {
      this.name = "Alfresco.SiteLinks";
      this.id = htmlId;

      Alfresco.util.ComponentManager.register(this);

      Alfresco.util.YUILoaderHelper.require(["container"], this.onComponentsLoaded, this);

      return this;
   };

   Alfresco.SiteLinks.prototype =
   {
      /**
       * Object container for initialization options
       *
       * @property options
       * @type {object} object literal
       */
      options:
      {
         /**
          * @property siteId
          * @type String
          * */
         siteId : ""
      },

      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.SiteLinks} returns 'this' for method chaining
       */
      setOptions: function SL_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },

      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function SL_onComponentsLoaded()
      {
         Event.onContentReady(this.id, this.onReady, this, true);
      },

      /**
       * Fired by YUI when parent element is available for scripting
       * @method onReady
       */
      onReady: function SL_onReady()
      {
         Event.on(this.id + "-createLink-button", "click", this.onCreateLinkClick, null, this);
      },

      /**
       * Fired by YUI Link when the "Create link" link is clicked
       * @method onCreateLinkClick
       * @param event {domEvent} DOM event
       */
      onCreateLinkClick: function SL_onCreateLinkButtonClick(e)
      {
         Event.stopEvent(e);
         var url = YAHOO.lang.substitute(Alfresco.constants.URL_CONTEXT + "page/site/{site}/links-linkedit",
         {
            site: this.options.siteId
         });
         window.location = url;
      }
   };
})();