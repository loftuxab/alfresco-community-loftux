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
 * Alfresco.WikiDashlet
 * Aggregates events from all the sites the user belongs to.
 * For use on the user's dashboard.
 *
 * @namespace Alfresco
 * @class Alfresco.WikiDashlet
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;

   /**
    * WikiDashlet constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.WikiDashlet} The new WikiDashlet instance
    * @constructor
    */
   Alfresco.WikiDashlet = function(htmlId)
   {
      this.name = "Alfresco.WikiDashlet";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);
      
      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require([], this.onComponentsLoaded, this);
      
      this.parser = new Alfresco.WikiParser();
      
      return this;
   };

   Alfresco.WikiDashlet.prototype =
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
          * The gui id.
          *
          * @property guid
          * @type string
          */
         guid: "",

         /**
          * Current siteId.
          *
          * @property siteId
          * @type string
          */
         siteId: "",

         /**
          * The pages on this site's wiki.
          *
          * @property pages
          * @type Array
          */
         pages: []
      },

      /**
		 * Allows the user to configure the feed for the dashlet.
		 *
		 * @property configDialog
	    * @type DOM node
		 */
      configDialog: null,
		
      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.WikiDashlet} returns 'this' for method chaining
       */
      setOptions: function WikiDashlet_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },

      /**
       * Set messages for this component.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.DocumentList} returns 'this' for method chaining
       */
      setMessages: function WikiDashlet_setMessages(obj)
      {
         Alfresco.util.addMessages(obj, this.name);
         return this;
      },
      
      /**
		 * Fired by YUILoaderHelper when required component script files have
		 * been loaded into the browser.
		 *
		 * @method onComponentsLoaded
	    */	
      onComponentsLoaded: function WikiDashlet_onComponentsLoaded()
      {
         Event.onContentReady(this.id, this.init, this, true);
      },
      
      /**
	    * Fired by YUI when parent element is available for scripting.
	    * Initialises components, including YUI widgets.
	    *
	    * @method init
	    */ 
      init: function WikiDashlet_init()
      {
         Event.addListener(this.id + "-wiki-link", "click", this.onConfigFeedClick, this, true);
         
         this.parser.URL = this._getAbsolutePath();
         var wikiDiv = Dom.get(this.id + "-scrollableList");
         wikiDiv.innerHTML = this.parser.parse(wikiDiv.innerHTML, this.options.pages);
      },
		
      /**
		 * Returns the absolute path (URL) to a wiki page, minus the title of the page.
		 *
		 * @method _getAbsolutePath
		 * @private
		 */
      _getAbsolutePath: function WikiDashlet__getAbsolutePath()
      {
         return Alfresco.constants.URL_CONTEXT + "page/site/" + this.options.siteId + "/wiki-page?title=";
      },
		
      /**
		 * Configuration click handler
		 *
		 * @method onConfigFeedClick
		 * @param e {object} HTML event
		 */
      onConfigFeedClick: function WikiDashlet_onConfigFeedClick(e)
      {
         var actionUrl = Alfresco.constants.URL_SERVICECONTEXT + "modules/wiki/config/" + encodeURIComponent(this.options.guid);
         
         Event.stopEvent(e);
         
         if (!this.configDialog)
         {
            this.configDialog = new Alfresco.module.SimpleDialog(this.id + "-configDialog").setOptions(
            {
               width: "50em",
               templateUrl: Alfresco.constants.URL_SERVICECONTEXT + "modules/wiki/config/" + this.options.siteId,
               actionUrl: actionUrl,
               onSuccess:
               {
                  fn: function WikiDashlet_onConfigFeed_callback(e)
                  {
                     var obj = YAHOO.lang.JSON.parse(e.serverResponse.responseText);
                     if (obj)
                     {
                        // Update the content via the parser
                        Dom.get(this.id + "-scrollableList").innerHTML = this.parser.parse(obj["content"], this.options.pages);
                        
                        // Update the title
                        Dom.get(this.id + "-title").innerHTML = Alfresco.util.message("label.header-prefix", this.name) + " - <a href=\"wiki-page?title=" + encodeURIComponent(e.config.dataObj.wikipage) + "\">" + obj.title + "</a>";
                     }
                  },
                  scope: this
               }
            });
         }
         else
         {
            this.configDialog.setOptions(
            {
               actionUrl: actionUrl
            });
         }
         
         this.configDialog.show();
      }
   };
})();