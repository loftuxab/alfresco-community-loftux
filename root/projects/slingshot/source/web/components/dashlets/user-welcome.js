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
 
/*
 * Alfresco.UserWelcome
 * Registers a event handler on the 'Remove Me' button to have the component remove itself.
 *
 * @namespace Alfresco
 * @class Alfresco.UserWelcome
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;

   /**
    * UserWelcome constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.UserWelcome} The new component instance
    * @constructor
    */
   Alfresco.UserWelcome = function(htmlId)
   {
      this.name = "Alfresco.UserWelcome";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);
      
      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["grids"], this.onComponentsLoaded, this);
      
      return this;
   }

   Alfresco.UserWelcome.prototype =
   {
      /**
       * CreateSite module instance.
       *
       * @property createSite
       * @type Alfresco.module.CreateSite
       */
      createSite: null,

      /**
		 * Fired by YUILoaderHelper when required component script files have
		 * been loaded into the browser.
		 *
		 * @method onComponentsLoaded
	    */	
      onComponentsLoaded: function UW_onComponentsLoaded()
      {
         Event.onContentReady(this.id, this.onReady, this, true);
      },
      
      /**
	    * Fired by YUI when parent element is available for scripting.
	    * Initialises components, including YUI widgets.
	    *
	    * @method onReady
	    */ 
      onReady: function UW_onReady()
      {
         // Listen on clicks for the create site link
         Event.addListener(this.id + "-createSite-button", "click", this.onCreateSiteLinkClick, this, true);
      },

      /**
       * Fired by YUI Link when the "Create site" label is clicked
       * @method onCreateSiteLinkClick
       * @param event {domEvent} DOM event
       */
      onCreateSiteLinkClick: function(event)
      {
         // Create the CreateSite module if it doesnt exist
         if (this.createSite === null)
         {
            this.createSite = Alfresco.module.getCreateSiteInstance();
         }
         // and show it
         this.createSite.show();
      }
   };
})();