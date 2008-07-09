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
 * Paginator
 * 
 * Paginator component
 *
 * @namespace Alfresco
 * @class Alfresco.Paginator
 */
(function()
{
   Alfresco.Paginator = function(htmlId)
   {
      this.name = "Alfresco.Paginator";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require([], this.onComponentsLoaded, this);

      return this;
   };

   Alfresco.Paginator.prototype =
   {
       /**
        * Object container for initialization options
        */
       options:
       {
           total: 0,
           pageSize: 10,
           startIndex: 0,
           itemCount: 0
       },

      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function Paginator_componentsLoaded()
      {
         YAHOO.util.Event.onContentReady(this.id, this.onReady, this, true);
      },

      onReady: function Paginator_onReady()
      {
         Alfresco.util.registerDefaultActionHandler(this.id, 'paginator-action', 'span', this);
         
         // listen for onPagingDataChanged events
         YAHOO.Bubbling.on("onPagingDataChanged", this._onPagingDataChanged, this);
      },
      
      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       */
      setOptions: function Paginator_setOptions(obj)
      {
         this.options = YAHOO.lang.merge(this.options, obj);
         return this;
      },
      
      /**
       * Set messages for this component.
       *
       * @method setMessages
       * @param obj {object} Object literal specifying a set of messages
       * @return {Alfresco.DiscussionsTopicListFilters} returns 'this' for method chaining
       */
      setMessages: function Paginator_setMessages(obj)
      {
         Alfresco.util.addMessages(obj, this.name);
         return this;
      },
      
      onFirstPage: function Paginator_onFirstPage(htmlId, ownerId, param)
      {
         this.options.startIndex = 0;
         this._onStartIndexChanged();
      },
      
      onPreviousPage: function Paginator_onFirstPage(htmlId, ownerId, param)
      {
         var newIndex = this.options.startIndex - this.options.pageSize;
         if (newIndex < 0)
         {
            newIndex = 0;
         }
         if (newIndex != this.options.startIndex)
         {
            this.options.startIndex = newIndex;
            this._onStartIndexChanged();
         }
      },
      
      onNextPage: function Paginator_onFirstPage(htmlId, ownerId, param)
      {
         var newIndex = this.options.startIndex + this.options.pageSize;
         this.options.startIndex = newIndex;
         this._onStartIndexChanged();
      },
      
      onLastPage: function Paginator_onFirstPage(htmlId, ownerId, param)
      {
         // calculate the last page index
         var lastPage = (this.options.total / this.options.pageSize) | 0; // will convert to int
         if (this.options.total % this.options.pageSize == 0)
         {
            lastPage--;
         }
           
         var newIndex = lastPage * this.options.pageSize;
         this.options.startIndex = newIndex;
         this._onStartIndexChanged();
      },
      
      onGoToPage: function Paginator_onFirstPage(htmlId, ownerId, param)
      {
         var page = parseInt(param);
         if (page < 1)
         {
             page = 1;
         }
         var newIndex = this.options.pageSize * (page-1);
         if (newIndex != this.options.startIndex)
         {
            this.options.startIndex = newIndex;
            this._onStartIndexChanged();
         }
      },
      
      /**
       * Updates the internal data state with new values
       */
      _onPagingDataChanged: function Paginator__onPagingDataChanged(layer, args)
      {
         var data = args[1].data;
         this.options.startIndex = data.startIndex;
         this.options.pageSize = data.pageSize;
         if (this.options.pageSize < 1)
         {
            this.options.pageSize = 1;
         }
         this.options.total = data.total;
         this.options.itemCount = data.itemCount;
         
         // update the html
         var elem = YAHOO.util.Dom.get(this.id + "-paginator");
         elem.innerHTML = data.html;
      },
      
      /**
       * Fires a onStartIndexChanged event to let interested parties know that they
       * have to change the page
       */
      _onStartIndexChanged: function Paginator__firePagingDataChanged()
      {
         YAHOO.Bubbling.fire('onStartIndexChanged',
            {
               pageSize: this.options.pageSize,
               startIndex: this.options.startIndex
            }
         );
      },

      /**
       * Gets a custom message
       *
       * @method _msg
       * @param messageId {string} The messageId to retrieve
       * @return {string} The custom message
       * @private
       */
      _msg: function DL__msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, "Alfresco.Paginator", Array.prototype.slice.call(arguments).slice(1));
      }

   };     
})();
