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
 
//
// Note: this is a copy of the tags javascript file in the document library !
//
 
/**
 * BlogPostList Tags component.
 * 
 * @namespace Alfresco
 * @class Alfresco.TopicListTags
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
    * @return {Alfresco.TopicListTags} The new DoclistTags instance
    * @constructor
    */
   Alfresco.TopicListTags = function(htmlId)
   {
      this.name = "Alfresco.TopicListTags";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);
      
      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require([], this.onComponentsLoaded, this);
      
      // Decoupled event listeners
      YAHOO.Bubbling.on("tagRefresh", this.onTagRefresh, this);
      YAHOO.Bubbling.on("filterChanged", this.onFilterChanged, this);
      YAHOO.Bubbling.on("deactivateAllControls", this.onDeactivateAllControls, this);
      
      return this;
   }
   
   Alfresco.TopicListTags.prototype =
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
          * @default "discussions"
          */
         containerId: "discussions",

         /**
          * Number of tags to show
          *
          * @property numTags
          * @type int
          * @default 15
          */
         numTags: 15
      },

      /**
       * Object literal used to generate unique tag ids
       * 
       * @property tagId
       * @type object
       */
      tagId:
      {
         id: 0,
         tags: {}
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
       * @return {Alfresco.TopicListTags} returns 'this' for method chaining
       */
       setOptions: function TopicListTags_setOptions(obj)
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
      onComponentsLoaded: function TopicListTags_onComponentsLoaded()
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
      onReady: function TopicListTags_onReady()
      {
         var me = this;
         
         YAHOO.Bubbling.addDefaultAction('tag-link', function(layer, args)
         {
            var link = args[1].target;
            if (link && !me.controlsDeactivated)
            {
               var tagName = link.firstChild.nodeValue;
               YAHOO.Bubbling.fire("filterChanged",
               {
                  filterId: "tag",
                  filterOwner: me.name,
                  filterData: tagName
               });
            }
            return true;
         });
         
         // Kick-off tag population
         if (this.options.siteId && this.options.containerId)
         {
            YAHOO.Bubbling.fire("tagRefresh");
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
      onFilterChanged: function TopicListTags_onFilterChanged(layer, args)
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

               // Need to find the selectedFilter element, from the current filterId
               this.selectedFilter = Dom.get(this.id + "-tagId-" + this.tagId.tags[obj.filterData]);
               // This component now owns the active filter
               Dom.addClass(this.selectedFilter, "selected");
            }
            else
            {
               // Currently filtering by something other than this component
               if (this.selectedFilter !== null)
               {
                  Dom.removeClass(this.selectedFilter, "selected");
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
      onTagRefresh: function TopicListTags_onRefresh(layer, args)
      {
         var timestamp = new Date().getTime();
         var url = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "api/site/{site}/{container}/tagscopetags?d=" + timestamp + "&topN=" + this.options.numTags,
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
					fn: this.onTagRefresh_success,
					scope: this
				},
				failureCallback:
				{
					fn: this.onTagRefresh_success,
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
      onTagRefresh_success: function TopicListTags_onTagRefresh_success(response)
      {
         if (response && !YAHOO.lang.isUndefined(response.json.tags))
         {
            var html = "";
            var tags = response.json.tags;
            for (var i = 0, j = tags.length; i < j; i++)
            {
               html += this._generateTagMarkup(tags[i]);
            }
            
            var eTags = Dom.get(this.id + "-tags");
            eTags.innerHTML = html;
         }
      },
      
      /**
       * Deactivate All Controls event handler
       *
       * @method onDeactivateAllControls
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onDeactivateAllControls: function TopicListTags_onDeactivateAllControls(layer, args)
      {
         this.controlsDeactivated = true;
         var controls = YAHOO.util.Selector.query("a.tag-link", this.id + "-body");
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
      _msg: function TopicListTags__msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, "Alfresco.TopicListTags", Array.prototype.slice.call(arguments).slice(1));
      },
      
      /**
       * Generates the HTML for a tag.
       *
       * @method _generateTagMarkup
       * @param tag {object} the tag to render
       */
      _generateTagMarkup: function TopicListTags__generateTagMarkup(tag)
      {
         var html = '<li id="' + this._generateTagId(tag.name) + '"><span class="onTagSelection nav-label">';
         html += '<a href="#" class="tag-link nav-link">' + $html(tag.name) + '</a> (' + tag.count + ')';
         html += '</span></li>';
         return html;
      },

      /**
       * Generate ID alias for tag, suitable for DOM ID attribute
       *
       * @method generateTagId
       * @param tagName {string} Tag name
       * @return {string} A unique DOM-safe ID for the tag
       */
      _generateTagId: function TopicListTags__generateTagId(tagName)
      {
         var id = 0;
         var tagId = this.tagId;
         if (tagName in tagId.tags)
         {
             id = tagId.tags[tagName];
         }
         else
         {
            tagId.id++;
            id = tagId.tags[tagName] = tagId.id;
         }
         return this.id + "-tagId-" + id;
      }
   }
})();