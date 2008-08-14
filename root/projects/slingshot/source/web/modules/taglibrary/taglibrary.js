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
 * TagLibrary
 * 
 * Component that manages a selection of tags.
 * Note: The component fires events whenever the selection of tags changes.
 *       Use TagLibraryListener to keep an html forn in sync with the TagLibrary.
 *
 * @namespace Alfresco
 * @class Alfresco.TagLibrary
 */
(function()
{
   Alfresco.TagLibrary = function(htmlId)
   {
      this.name = "Alfresco.TagLibrary";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "dom"], this.onComponentsLoaded, this);

      return this;
   };

   Alfresco.TagLibrary.prototype =
   {
       /**
        * Object container for initialization options
        */
       options:
       {
          /**
           * Current siteId.
           * 
           * @property siteId
           * @type string
           * @default ""
           */
          siteId: "",
          
          /**
           * Maximum number of tags popular tags displayed
           */
          topN: 10
       },

      
      /**
       * Currently selected tags.
       * 
       * @type: array of strings
       * @default empty array
       */
      currentTags: [],

      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function TagLibrary_componentsLoaded()
      {
         YAHOO.util.Event.onContentReady(this.id, this.onReady, this, true);
      },

      onReady: function TagLibrary_onReady()
      {
         // register an action handler for all elements with class tag-link.
         // the action to be called is specified in the id of the enclosing li
         // element
         //this.registerDefaultActionHandler(this, this.id, "taglibrary-action", "li");
         
         // Hook tag actions
         var me = this;
         var fnActionHandlerDiv = function DiscussionsTopic_fnActionHandlerDiv(layer, args)
         {
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "li");
            if (owner !== null)
            {
               var action = "";
               action = owner.className;
               if (typeof me[action] == "function")
               {
                  // fetch the tag name, which is inside the form id-action-tag
                  // PENDING: hold this information in the js object and generate
                  // int id's like for the tag actions
                  var id = owner.id;
                  var tagName = id.substring((me.id + '-' + action + '-').length);
                  me[action].call(me, tagName);
                  args[1].stop = true;
               }
            }
      		 
            return true;
         }
         YAHOO.Bubbling.addDefaultAction("taglibrary-action", fnActionHandlerDiv);
         
         // load link for popular tags
         YAHOO.util.Event.addListener(this.id + "-load-popular-tags-link", "click", this.onPopularTagsLinkClicked, this, true);
         
         // register the "enter" event on the tag text field to add the tag (otherwise
         // the form gets submitted
         var zinput = YAHOO.util.Dom.get(this.id + "-tag-input-field");
         var me = this;
         new YAHOO.util.KeyListener(zinput, 
         {
            keys:13
         }, 
         {
            fn: function(eventName, event, obj) {
               me.onAddTagButtonClick();
               YAHOO.util.Event.stopEvent(event[1]);
               return false;
            },
            scope:this,
            correctScope:true
         }, 
         "keypress").enable();
         
         // button to add tag to list
         var addTagButton = new YAHOO.widget.Button(this.id + "-add-tag-button", {type: "button"});
         addTagButton.subscribe("click", this.onAddTagButtonClick, this, true);
      },
      
      /**
       * Set multiple initialization options at once.
       *
       * @method setOptions
       * @param obj {object} Object literal specifying a set of options
       */
      setOptions: function TagLibrary_setOptions(obj)
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
      setMessages: function TagLibrary_setMessages(obj)
      {
         Alfresco.util.addMessages(obj, this.name);
         return this;
      },
      
      /**
       * Set the currently selected tags. This method should be used
       * to initialize the component to bring it in sync with what has
       * been generated in html
       *
       * @method setCurrentTags
       * @param tags {array} Array containing the tags (by name)
       * @return {Alfresco.TagLibrary} returns 'this' for method chaining
       */
      setCurrentTags: function TagLibrary_setCurrentTags(tags)
      {
         this.currentTags = tags;
         return this;
      },
      
      /**
       * Adds an array of tags to the current tags.
       * For each tag the html is generated, this function can therefore
       * be used to set the tags when using the taglibrary as a client-side
       * only component (no tags generated on the server)
       *
       * @method setCurrentTags
       * @param tags {array} Array containing the tags (by name)
       */
      addTags: function TagLibrary_addTags(tags)
      {  
         // add each tag to the list, also generating the html
         for (var x=0; x < tags.length; x++)
         {
            this._addTagImpl(tags[x]);
         }
      },

      /**
       * Triggered by a click on one of the selected tags
       */
      onRemoveTag: function TagLibrary_onRemoveTag(tagName)
      {
          this._removeTagImpl(tagName);
      },
      
      /**
       * Triggered by a click onto one of the popular tags.
       */
      onAddTag: function TagLibrary_onAddTag(tagName)
      {
         this._addTagImpl(tagName);
      },

      /**
       * Loads the popular tags
       */
      onPopularTagsLinkClicked: function TagLibrary_onPopularTagsLinkClicked(e, obj)
      {
         // load the popular tags through an ajax call
         // make an ajax request to delete the topic
         var url = Alfresco.constants.PROXY_URI + "api/site/" + this.options.siteId +
                   "/tagscopetags?topN=" + this.options.topN;
         Alfresco.util.Ajax.request(
         {
            url: url,
            method: "GET",
            responseContentType : "application/json",
            successCallback:
            {
               fn: this._onPopularTagsLoaded,
               scope: this
            },
            failureMessage: this._msg("taglibrary.msg.failedLoadTags")
         });
         YAHOO.util.Event.stopEvent(e);
      },
      
      _onPopularTagsLoaded: function TagLibrary__onPopularTagsLoaded(response)
      {
         if (response.json.error != undefined)
         {
            Alfresco.util.PopupManager.displayMessage({text: this._msg("taglibrary.msg.unableLoadTags", response.json.error)});
         }
         else
         {
            this._displayPopularTags(response.json.tags);
         }
      },
      
      /**
       * Update the UI with the popular tags loaded via AJAX.
       */
      _displayPopularTags: function TagLibrary__showPopularTags(tags)
      {
         // remove the popular tags load link
         YAHOO.util.Dom.setStyle(this.id + "-load-popular-tags-link", "display", "none");

         // add all tags to the ui
         /*
         <li class="onAddTag" id="${htmlid}-onAddTag-Car">
        	<a href="#" class="taglibrary-action">
        	    <span>Car</span>
        		<span class="close">
        			<img src="icon_close.gif" alt="x" />
         		</span>
        	</a>
         </li>
         */
         var popularTagsElem = YAHOO.util.Dom.get(this.id + "-popular-tags");
         for (var x=0; x < tags.length; x++)
         {
            var elem = document.createElement('li');
            var elemId = this.id + "-onAddTag-" + tags[x].name;
            elem.setAttribute('id', elemId);
            elem.setAttribute('class', 'onAddTag');
            elem.innerHTML = '<a href="#" class="taglibrary-action"> <span>' + tags[x].name +
        		' </span> <span class="add">&nbsp;</span> </a>'; // <img src="icon_close.gif" alt="x" />
            popularTagsElem.appendChild(elem);
         }
      },

      /**
       * Adds the content of the text field as a new tag.
       */
      onAddTagButtonClick: function(type, args)
      {
         // get the text of the input field
         var inputField = YAHOO.util.Dom.get(this.id + "-tag-input-field");
         var text = inputField.value;
         
         // extract all full words and add them as tags
         // take all full words in the string
         var tags = [];
         var tag = null;
         var regexp = /(\w+)/gi;
         while ((tag = regexp.exec(text)))
         {
            tags.push(tag[1]);
         }
         for (var x=0; x < tags.length; x++)
         {
            this._addTagImpl(tags[x]);
         }
         
         // finally clear the text field
         inputField.value = "";
      },
       
      /**
       * Fires a tags changed event.
       */
      _fireTagsChangedEvent: function TagLibrary__fireTagsChangedEvent()
      {
         // send out a message informing about the new set of tags
         YAHOO.Bubbling.fire('onTagLibraryTagsChanged', {tags : this.currentTags});
      },

      /**
       * Add a tag to the current set of selected tags
       */
      _addTagImpl: function TagLibrary__addTagImpl(tagName)
      {
         // sanity checks
         if (tagName == null || tagName.length < 1)
         {
             return;
         }
         
         // check whether the tag has already been added
         for (var x=0; x < this.currentTags.length; x++)
         {
            if (tagName == this.currentTags[x])
            {
               return;
            }
         }
         
         // add the tag to the internal data structure
         this.currentTags.push(tagName);
         
         // add the tag to the UI
         /*
         <li id="${htmlid}-onRemoveTag-${tag}">
        	<a href="#" class="taglibrary-action"><span>${tag}</span>
        		<span class="close">
        			<img src="/modules/taglibrary/images/icon_add.gif" alt="x" />
        		</span>
        	</a>
         </li>
         */
         var currentTagsElem = YAHOO.util.Dom.get(this.id + "-current-tags")
         var elem = document.createElement('li');
         var elemId = this.id + '-onRemoveTag-' + tagName;
         elem.setAttribute('id', elemId);
         elem.setAttribute('class', 'onRemoveTag');
         elem.innerHTML = '<a href="#" class="taglibrary-action"> <span>' + tagName +
                          ' </span> <span class="remove">&nbsp;</span> </a>'; // <img src="/modules/taglibrary/images/icon_add.gif" alt="x" />
         currentTagsElem.appendChild(elem);

         // inform interested parties about change
         this._fireTagsChangedEvent();
      },

      /**
       * Remove a tag from the current set of selected tags
       */
      _removeTagImpl: function TagLibrary__removeTagImpl(tagName)
      {
         // sanity checks
         if (tagName == null || tagName.length < 1)
         {
             return;
         }
         
         // remove the tag from the array
         for (var x=0; x < this.currentTags.length; x++)
         {
            if (tagName == this.currentTags[x])
            {
               this.currentTags.splice(x, 1);
               x--;
            }
         }

         // remove the ui element
         var tagElemToRemove = YAHOO.util.Dom.get(this.id + "-onRemoveTag-" + tagName);
         tagElemToRemove.parentNode.removeChild(tagElemToRemove);
         
         // inform interested parties about change
         this._fireTagsChangedEvent();
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
         return Alfresco.util.message.call(this, messageId, "Alfresco.TagLibrary", Array.prototype.slice.call(arguments).slice(1));
      }

   };     
})();
