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
 * BlogPostList Archive component.
 * 
 * @namespace Alfresco
 * @class Alfresco.BlogPostListArchive
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Element = YAHOO.util.Element;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;

   /**
    * Blog Tags constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.BlogPostListArchive} The new DoclistTags instance
    * @constructor
    */
   Alfresco.BlogPostListArchive = function(htmlId)
   {
      this.name = "Alfresco.BlogPostListArchive";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);
      
      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require([], this.onComponentsLoaded, this);
      
      // Decoupled event listeners
      YAHOO.Bubbling.on("archiveRefresh", this.onArchiveRefresh, this);
      YAHOO.Bubbling.on("filterChanged", this.onFilterChanged, this);
      YAHOO.Bubbling.on("deactivateAllControls", this.onDeactivateAllControls, this);
      
      return this;
   }
   
   Alfresco.BlogPostListArchive.prototype =
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
          */
         siteId: "",

         /**
          * ContainerId representing root container
          *
          * @property containerId
          * @type string
          * @default "blog"
          */
         containerId: "blog"
      },

      /**
       * Object literal used to generate unique tag ids
       * 
       * @property tagId
       * @type object
       */
      monthId:
      {
         id: 0,
         months: {}
      },

      /**
       * Selected filter.
       * 
       * @property selectedFilter
       * @type {element}
       */
      selectedFilter: null,

      /**
       * Flag to indicate whether all controls are deactivated or not.
       * 
       * @property controlsDeactivated
       * @type {boolean}
       * @default false
       */
      controlsDeactivated: false,

      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.BlogPostListArchive} returns 'this' for method chaining
       */
       setOptions: function BlogPostListArchive_setOptions(obj)
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
      onComponentsLoaded: function BlogPostListArchive_onComponentsLoaded()
      {
         YAHOO.util.Event.onContentReady(this.id, this.onReady, this, true);
      },
   
      /**
       * Fired by YUI when parent element is available for scripting.
       * Registers event handler on 'onTagRefresh' event. If a component wants to refresh
       * the tags component, they need to fire this event.
       *
       * @method onReady
       */   
      onReady: function BlogPostListArchive_onReady()
      {
         var me = this;
         
         YAHOO.Bubbling.addDefaultAction('archive-link', function(layer, args)
         {
            var link = args[1].target;
            if (link && !me.controlsDeactivated)
            {
               var liElem = Dom.getAncestorByTagName(link, 'li');
               var date = me._getMonthFromId(liElem.id);  
               YAHOO.Bubbling.fire("filterChanged",
               {
                  filterId: "bymonth",
                  filterOwner: me.name,
                  filterData: { year: date.getFullYear(), month: date.getMonth() }
               });
            }
            return true;
         });
         
         // Kick-off tag population
         if (this.options.siteId && this.options.containerId)
         {
            YAHOO.Bubbling.fire("archiveRefresh");
         }
      },
      

      /**
       * BUBBLING LIBRARY EVENT HANDLERS
       * Disconnected event handlers for inter-component event notification
       */

      /**
       * Fired when the currently active filter has changed
       * @method onFilterChanged
       * @param layer {string} the event source
       * @param args {object} arguments object
       */
      onFilterChanged: function BlogPostListArchive_onFilterChanged(layer, args)
      {
         var obj = args[1];
         if ((obj !== null) && (obj.filterId !== null))
         {
            if (obj.filterOwner == this.name)
            {
               // Remove the old highlight, as it might no longer be correct
               if (this.selectedFilter !== null)
               {
                  Dom.removeClass(this.selectedFilter, "selected");
               }

               var date = new Date(obj.filterData.year, obj.filterData.month, 1);
               // Need to find the selectedFilter element, from the current filterId
               this.selectedFilter = Dom.get(this._generateIdFromMonth(date));
               
               // This component now owns the active filter
               Dom.addClass(this.selectedFilter, "selected");
            }
            else
            {
               // Currently filtering by something other than this component
               if (this.selectedFilter !== null)
               {
                  Dom.removeClass(this.selectedFilter, "selected");
                  this.selectedFilter = null;
               }
            }
         }
      },
      
      /**
       * Function that gets called when another component fires "tagRefresh"
       * Issues a request to retrieve the latest tag data.
       *
       * @method onTagRefresh
       * @param layer {string} the event source
       * @param args {object} arguments object
       */
      onArchiveRefresh: function BlogPostListArchive_onArchiveRefresh(layer, args)
      {
         var timestamp = new Date().getTime();
         var url = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "api/blog/site/{site}/{container}/postspermonth?d=" + timestamp,
         {
            site: this.options.siteId,
            container: this.options.containerId
         });
         
         Alfresco.util.Ajax.request(
         {
            method: Alfresco.util.Ajax.GET,
				url: url,
				successCallback:
				{
					fn: this.onArchiveRefresh_success,
					scope: this
				},
				failureCallback:
				{
					fn: this.onArchiveRefresh_success,
					scope: this
				}
			});
      },
      
      /**
       * Event handler for when the tag data loads successfully.
       *
       * @method onTagRefresh_success
       * @param response {object} Server response object
       */ 
      onArchiveRefresh_success: function BlogPostListArchive_onTagRefresh_success(response)
      {
         if (response && !YAHOO.lang.isUndefined(response.json.items))
         {
            var html = "";
            var items = response.json.items;
            for (var i = 0, j = items.length; i < j; i++)
            {
               var date = new Date(items[i].year, items[i].month, 1);
               html += this._generateMonthMarkup(date);
            }
            
            var eMonths = Dom.get(this.id + "-archive");
            eMonths.innerHTML = html;
         }
      },
      
      /**
       * Deactivate All Controls event handler
       *
       * @method onDeactivateAllControls
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onDeactivateAllControls: function BlogPostListArchive_onDeactivateAllControls(layer, args)
      {
         this.controlsDeactivated = true;
         var controls = YAHOO.util.Selector.query("a.archive-link", this.id + "-body");
         for (var i = 0, j = controls.length; i < j; i++)
         {
            Dom.addClass(controls[i], "disabled");
         }
      },


      /**
       * PRIVATE FUNCTIONS
       */

      /**
       * Gets a custom message
       *
       * @method _msg
       * @param messageId {string} The messageId to retrieve
       * @return {string} The custom message
       * @private
       */
      _msg: function BlogPostListArchive__msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, "Alfresco.BlogPostListArchive", Array.prototype.slice.call(arguments).slice(1));
      },
      
      /**
       * Generates the HTML for a tag.
       *
       * @method _generateTagMarkup
       * @param tag {object} the tag to render
       */
      _generateMonthMarkup: function BlogPostListArchive__generateTagMarkup(date)
      {
         
         var html = '<li id="' + this._generateIdFromMonth(date) + '"><span class="nav-label">';
         html += '<a href="#" class="archive-link nav-link">' + Alfresco.util.formatDate(date, "mmmm yyyy") + '</a>';
         html += '</span></li>';
         return html;
      },

      /**
       * Generate ID alias for tag, suitable for DOM ID attribute
       *
       * @method _generateIdFromMonth
       * @param tagName {string} Tag name
       * @return {string} A unique DOM-safe ID for the tag
       */
      _generateIdFromMonth: function BlogPostListArchive__generateMonthId(date)
      {
         return this.id + "-month-" + date.getTime();
      },
      
      /**
       * Returns the date object that got encoded into the id by _generateIdFromMonth.
       * @method _getMonthFromId
       * @param id an id encoded by _generateMonthId
       * @return a date object
       */
      _getMonthFromId: function BlogPostListArchive__getMonthFromId(id)
      {
         // get the date millis part
         var millis = id.substring((this.id + "-month-").length);
         millis = parseInt(millis);
         return new Date(millis);
      }
   }
})();