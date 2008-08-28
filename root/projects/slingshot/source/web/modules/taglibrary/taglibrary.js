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
 * Module that manages the selection of tags in a form
 *
 * @namespace Alfresco
 * @class Alfresco.module.TagLibrary
 */
(function()
{
   Alfresco.module.TagLibrary = function(htmlId)
   {
      this.name = "Alfresco.module.TagLibrary";
      this.id = htmlId;
      
      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "dom"], this.onComponentsLoaded, this);

      return this;
   };

   Alfresco.module.TagLibrary.prototype =
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
       * Currently selected tags.
       * 
       * @type: array of strings
       * @default empty array
       */
      currentTags: [],

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
       * Sets the current list of tags.
       * Use this method if the tags html and inputs have been generated on the server.
       * If you create the taglibrary in javascript, use setTags to also update the UI.
       */
      setCurrentTags: function TagLibrary_setCurrentTags(tags)
      {
         this.currentTags = tags;
         return this;
      },

      /**
       * Fired by YUILoaderHelper when required component script files have
       * been loaded into the browser.
       *
       * @method onComponentsLoaded
       */
      onComponentsLoaded: function TagLibrary_componentsLoaded()
      {
      },

      
      /**
       * Registers the tag library logic with the dom tree
       */
      initialize: function TagLibrary_initialize()
      {
         // Hook tag actions
         var me = this;
         var fnActionHandlerDiv = function DiscussionsTopic_fnActionHandlerDiv(layer, args)
         {
            var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, "li");
            if (owner !== null)
            {
               var action = "";
               action = owner.getAttribute("class");
               if (typeof me[action] == "function")
               {
                  var tagName = me.findTagName(me, owner.id);
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
            fn: function(eventName, event, obj)
            {
               me.onAddTagButtonClick();
               YAHOO.util.Event.stopEvent(event[1]);
               return false;
            },
            scope: this,
            correctScope: true
         }, 
         "keypress").enable();
         
         // button to add tag to list
         var addTagButton = new YAHOO.widget.Button(this.id + "-add-tag-button", {type: "button"});
         addTagButton.subscribe("click", this.onAddTagButtonClick, this, true);
      },
      
      /**
       * Generate ID alias for tag, suitable for DOM ID attribute
       *
       * @method generateTagId
       * @param scope {object} instance that contains a tagId object (which stores the generated tag id mappings)
       * @param tagName {string} Tag name
       * @return {string} A unique DOM-safe ID for the tag
       */
      generateTagId : function generateTagId(scope, tagName, action)
      {
         var id = 0;
         var tagId = scope.tagId;
         if (tagName in tagId.tags)
         {
            id = tagId.tags[tagName];
         }
         else
         {
           tagId.id++;
           id = tagId.tags[tagName] = tagId.id;
         }
         return scope.id + "-" + action + "-" + id;
      },
      
      /**
       * Returns the tagName given a id generated by generateTagId.
       */
      findTagName: function findTagName(scope, tagId)
      {
         var actionAndId = tagId.substring(scope.id.length + 1);
         var tagIdValue = actionAndId.substring(actionAndId.indexOf('-') + 1);
         for (tag in scope.tagId.tags)
         {
            if (scope.tagId.tags[tag] == tagIdValue)
            {
               return tag;
            }
         }
         return null;
      },
      
      /**
       * Adds an array of tags to the current tags.
       * For each tag the html is generated, this function can therefore
       * be used to set the tags when using the taglibrary as a client-side
       * only component (no tags generated on the server)
       *
       * @method setTags
       * @param tags {array} Array containing the tags (by name)
       */
      setTags: function TagLibrary_setTags(tags)
      {
         // first make sure that there are no previous tags available
         YAHOO.util.Dom.get(this.id + '-current-tags').innerHTML = '';
         this.currentTags = [];
         
         // add each tag to the list, also generating the html
         for (var x=0; x < tags.length; x++)
         {
            this._addTagImpl(tags[x]);
         }
      },

      /**
       * Get all tags currently selected
       */
      getTags: function TagLibrary_getTags()
      {
         return this.currentTags;
      },
      
      
      /**
       * Updates a form with the currently selected tags.
       * 
       * @param formId {string} the id of the form to update
       * @param tagsFieldName {string} the name of the field to use to store the tags in
       */
      updateForm: function TagLibrary_updateForm(formId, tagsFieldName)
      {
         // construct the complete name to use for the field
         var fullFieldName = tagsFieldName + '[]';
         
         // clean out the currently available tag inputs
         var formElem = YAHOO.util.Dom.get(formId);
         
         // find all input fields, delete the inputs that match the field name
         var inputs = formElem.getElementsByTagName("input");
         for (var x=0; x < inputs.length; x++)
         {
            if (inputs[x].name == fullFieldName)
            {
                // remove the field
                inputs[x].parentNode.removeChild(inputs[x]);
                x--;
            }
         }
         
         // generate inputs for the selected tags
         for (var x=0; x < this.currentTags.length; x++)
         {
            var tagName = this.currentTags[x];
            var elem = document.createElement('input');
            elem.setAttribute('name', fullFieldName);
            elem.setAttribute('value', tagName);
            elem.setAttribute('type', 'hidden');
            formElem.appendChild(elem);
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
         var url = YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "api/tagscopes/site/{site}/tags?d={d}&tn={tn}",
         {
            site: this.options.siteId,
            d: new Date().getTime(),
            tn: this.options.topN
         });
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
         this._displayPopularTags(response.json.tags);
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
            var elemId = this.generateTagId(this, tags[x].name, 'onAddTag');
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
         var elemId = this.generateTagId(this, tagName, 'onRemoveTag');
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
         var elemId = this.generateTagId(this, tagName, 'onRemoveTag');
         var tagElemToRemove = YAHOO.util.Dom.get(elemId);
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
