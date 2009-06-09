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
 * Alfresco.TagComponent
 * 
 * @namespace Alfresco
 * @class Alfresco.TagComponent
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML;

   /**
    * TagComponent constructor.
    * 
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.TagComponent} The new TagComponent instance
    * @constructor
    */
   Alfresco.TagComponent = function(htmlId)
   {
      this.name = "Alfresco.TagComponent";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);
      
      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require([], this.onComponentsLoaded, this);
      
      return this;
   }
   
   Alfresco.TagComponent.prototype =
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
          */
         containerId: ""
      },      

      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       * @return {Alfresco.TagComponent} returns 'this' for method chaining
       */
       setOptions: function TagComponent_setOptions(obj)
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
      onComponentsLoaded: function TagComponent_onComponentsLoaded()
      {
         Event.onContentReady(this.id, this.onReady, this, true);
      },
      /**
       * Set messages for this component
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       */
      setMessages: function(obj)
      {
         Alfresco.util.addMessages(obj, this.name);
         return this;
      },
   
      /**
       * Fired by YUI when parent element is available for scripting.
       * Registers event handler on "tagRefresh" event. If a component wants to refresh
       * the tags component, they need to fire this event.
       *
       * @method onReady
       */   
      onReady: function TagComponent_onReady()
      {
         this._registerDefaultActionHandler();
         
         YAHOO.Bubbling.on("tagRefresh", this.onTagRefresh, this);
      },
      
      /**
       * Registers a default action listener on <em>all</em> of the tag links in the 
       * component. Fires "tagSelected" event with the name of the tag that was selected.
       *
       * To register for the event, interested components should do something like this:
       * YAHOO.Bubbling.on("tagSelected", this.onTagSelected, this); 
       *
       * @method _registerDefaultActionHandler
       */
      _registerDefaultActionHandler: function TagComponent_registerDefaultActionHandler()
      {
         YAHOO.Bubbling.addDefaultAction('tag-link', function(layer, args)
         {
            var link = args[1].target;
            if (link)
            {
               var tagName = link.firstChild.nodeValue;
               YAHOO.Bubbling.fire("tagSelected",
               {
                  "tagname": tagName
               });
            }
            return true;
         });
      },
      
      /**
       * Function that gets called when another component fires ? 
       * Issues a request to the repo to retrieve the latest tag data.
       *
       * @method onTagRefresh
       * @param e {object} DomEvent
       */
      onTagRefresh: function TagComponent_onRefresh(e)
      {
         var uri = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "api/tagscopes/site/{site}/{container}/tags?d={d}",
         {
            site: this.options.siteId,
            container: this.options.containerId,
            d: new Date().getTime()
         });
         
         Alfresco.util.Ajax.request(
         {
            method: Alfresco.util.Ajax.GET,
            url: uri,
            successCallback:
            {
              fn: this.onTagsLoaded,
              scope: this
            },
          failureMessage: "Couldn't refresh tag data"
        });
      },
      
      /**
       * Event handler that gets called when the tag data 
       * loads successfully.
       *
       * @method onTagsLoaded
       * @param e {object} DomEvent
       */ 
      onTagsLoaded: function TagComponent_onTagsLoaded(e)
      {
         var resp = YAHOO.lang.JSON.parse(e.serverResponse.responseText);
         if (resp && !YAHOO.lang.isUndefined(resp.tags))
         {
            var html = "", tags = resp.tags, tag, i, j;

            for (i = 0, j = tags.length; i < j; i++)
            {
               tag = tags[i];
               html += this._generateTagMarkup(tag);
            }
            
            var elem = Dom.get('tagFilterLinks');
            if (elem)
            {
               html = '<li class="onTagSelection nav-label">'+elem.getElementsByTagName('li')[0].innerHTML+'</li>'+html;
               elem.innerHTML = html;
               this._registerDefaultActionHandler();
            }
         }
      },
      
      /**
       * Generates the HTML for a tag.
       *
       * @method _generateTagMarkup
       * @param tag {Object} the tag to render
       */
      _generateTagMarkup: function (tag)
      {
         var html = '<li class="onTagSelection nav-label">';
         html += '<a href="#" class="tag-link nav-link">' + $html(tag.name) + '</a>&nbsp;(' + tag.count + ')';
         html += '</li>';
         return html;
      }
   }
})();