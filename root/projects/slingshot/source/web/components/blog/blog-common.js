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
 * This file defines functions used across different blog related components
 */

Alfresco.util.blog = {};


/**
 * Generates the status label text for a given blog post
 */
Alfresco.util.blog.generatePostStatusLabel = function BlogPostUtils_generatePostStatusLabel(me, data)
{
   if (data.isDraft)
   {
      return "(" + me._msg("post.draft") + ")";
   }
   else if (data.isUpdated || data.isPublished)
   {
      var status = "(" + me._msg("post.updated") + ") ";
      if (data.isPublished)
      {                  
         if (data.outOfDate)
         {
            return status + "(" + me._msg("post.published.outofsync") + ")";
         }
         else
         {
            return status + "(" + me._msg("post.published") + ")";
         }
      }
      else
      {
         return status;
      }
   }
   else
   {
      // internally published, no status displayed
      return "";
   }
};

         
Alfresco.util.blog.generateBlogPostActions = function BlogPostUtils_renderBlogPostActions(me, data, tagName)
{
   var desc = '';
   // begin actions
   desc += '<div class="nodeEdit">';
   if (data.permissions.edit)
   {
      desc += '<' + tagName + ' class="onEditBlogPost"><a href="#" class="blogpost-action-link-' + tagName + '">' + me._msg("post.action.edit") + '</a></' + tagName + '>';
   }
   if (data.permissions.publishExt && ! data.isDraft)
   {
      if (data.isPublished)
      {
         if (data.outOfDate)
         {
            desc += '<' + tagName + ' class="onUpdateExternal"><a href="#" class="blogpost-action-link-' + tagName + '">' + me._msg("post.action.updateexternal") + '</a></' + tagName + '>';
         }
         desc += '<' + tagName + ' class="onUnpublishExternal"><a href="#" class="blogpost-action-link-' + tagName + '">' + me._msg("post.action.unpublishexternal") + '</a></' + tagName + '>';
      }
      else
      {
         desc += '<' + tagName + ' class="onPublishExternal"><a href="#" class="blogpost-action-link-' + tagName + '">' + me._msg("post.action.publishexternal") + '</a></' + tagName + '>';
      }
   }
   if (data.permissions['delete'])
   {
      desc += '<' + tagName + ' class="onDeleteBlogPost"><a href="#" class="blogpost-action-link-' + tagName + '">' + me._msg("post.action.delete") + '</a></' + tagName + '>';
   }
   desc += '</div>';
   return desc;
};

         
/**
 * Returns the url to the post view page for a given record.
 */
/*Alfresco.util.blog.generatePostViewUrl = function BlogPostList_getPostViewUrl(site, container, data)
{
   return YAHOO.lang.substitute(Alfresco.constants.URL_PAGECONTEXT + "site/{site}/blog-postview?postId={postId}&container={container}",
   {
      site : site,
      container: container,
      postId:  data['name']
   });
}*/
         
      
Alfresco.util.blog.generateUserLink = function BlogPostList_generateUserProfileUrl(person)
{
   var link = Alfresco.util.blog.generateUserProfileUrl(person);
   var name = Alfresco.util.blog.generateUserDisplayName(person);
   
   return '<a href="' + link + '">' + Alfresco.util.encodeHTML(name) + '</a>';
}
         
/**
 * Generate URL to user profile page
 *
 * @method generateUserProfileUrl
 * @param userName {string} Username
 * @return {string} URL to profile page
 */
Alfresco.util.blog.generateUserProfileUrl = function BlogPostList_generateUserProfileUrl(person)
{
   return Alfresco.util.uriTemplate("userpage",
   {
      userid: person.userName,
      pageid: "profile"
   });
}
         
/**
 * Returns the display name given a person object
 * @param person an object with userName, optionally with firstName and lastName.
 * @return the display name of the person
 */
Alfresco.util.blog.generateUserDisplayName = function BlogPostList_getUserDisplayName(person)
{
   var displayName = person.userName;
   if ((person.firstName != undefined && person.firstName.length > 0) ||
       (person.lastName != undefined && person.lastName.length > 0))
   {
      displayName = '';
      if (person.firstName != undefined)
      {
         displayName = person.firstName + ' ';
      }
      if (person.lastName != undefined)
      {
         displayName += person.lastName;
      }
   }
   return displayName;
}
         
         
         
Alfresco.util.rollover = {}

/**
 * Attaches mouseover/exit event listener to the passed element.
 * 
 * @param elem the element to which to add the listeners
 * @param mouseOverEventName the bubble event name to fire for mouse enter events
 * @param mouseOutEventName the bubble event name to fire for mouse out events
 */
Alfresco.util.rollover._attachRolloverListener = function(elem, mouseOverEventName, mouseOutEventName)
{  
   var eventElem = elem;
     
   var mouseOverHandler = function(e)
   {
      // find the correct target element and check whether we only moved between
      // subelements of the hovered element
      if (! e) var e = window.event;
      var relTarg = e.relatedTarget || e.fromElement;
      while (relTarg != null && relTarg != eventElem && relTarg.nodeName != 'BODY')
      {
         relTarg = relTarg.parentNode
      }
      if (relTarg == eventElem) return;
    
      // the mouse entered the element, fire an event to inform about it
      YAHOO.Bubbling.fire(mouseOverEventName, {event : e, target : eventElem});
   };
 
   var mouseOutHandler = function(e)
   {
      // find the correct target element and check whether we only moved between
      // subelements of the hovered element
      if (! e) var e = window.event;
      var relTarg = e.relatedTarget || e.toElement;
      while (relTarg != null && relTarg != eventElem && relTarg.nodeName != 'BODY')
      {
         relTarg = relTarg.parentNode
      }
      if (relTarg == eventElem) return;
     
      // the mouse exited the element, fire an event to inform about it
      YAHOO.Bubbling.fire(mouseOutEventName, {event : e, target : eventElem});
   };
 
   YAHOO.util.Event.addListener(elem, 'mouseover', mouseOverHandler);
   YAHOO.util.Event.addListener(elem, 'mouseout', mouseOutHandler);
}

/**
 * Register rollover listeners to elements identified by a class and tag name.
 * 
 * @param htmlId the id of the component for which the listeners get registered.
 *        This id is used to distinguish events from different components.
 * @param className the class name of elements to add the listener to
 * @param tagName the tag name of elements to add the listener to.
 */
Alfresco.util.rollover.registerListenersByClassName = function(htmlId, className, tagName)
{
   var mouseEnteredBubbleEventName = 'onRolloverMouseEntered-' + htmlId;
   var mouseExitedBubbleEventName = 'onRolloverMouseExited-' + htmlId;
   var elems = YAHOO.util.Dom.getElementsByClassName(className, tagName);
   for (var x=0; x < elems.length; x++) {
      Alfresco.util.rollover._attachRolloverListener(elems[x], mouseEnteredBubbleEventName, mouseExitedBubbleEventName);
   }

}

/**
 * Register handle functions that handle the mouse enter/exit events
 * 
 * @param htmlId the id of the component for which the listeners got registered
 * @param mouseEnteredFn the function to call for mouse entered events
 * @param mouseExitedFunction the function to call for mouse exited events
 * @param scope the object which is used as scope for the function execution
 */
Alfresco.util.rollover.registerHandlerFunctions = function(htmlId, mouseEnteredFn, mouseExitedFn, scope)
{
   // register bubble events
   var mouseEnteredBubbleEventName = 'onRolloverMouseEntered-' + htmlId;
   var mouseExitedBubbleEventName = 'onRolloverMouseExited-' + htmlId;
   YAHOO.Bubbling.on(mouseEnteredBubbleEventName, mouseEnteredFn, scope);
   YAHOO.Bubbling.on(mouseExitedBubbleEventName, mouseExitedFn, scope);
}



/**
 * Register a default action handler for a given set
 * of elements described by their class name.
 * @parma handlerObject object that is used as for the method calls
 * @param className The elements to which the action should be added to
 * @param ownerTagName the owner tag name to search for. This has to be a
 *        parent element of the default action element. The id of this element is used
 *        to call the correct method. Id's should follow the form htmlid-actionname[-param]
 *        Actions methods should have the form f(htmlid, ownerId, param)
 */
Alfresco.util.registerDefaultActionHandler = function(htmlId, className, ownerTagName, handlerObject)
{         
   // Hook the tag events
   YAHOO.Bubbling.addDefaultAction(className,
      function TagLibrary_genericDefaultAction(layer, args)
      {
         var owner = YAHOO.Bubbling.getOwnerByTagName(args[1].anchor, ownerTagName);
         if (owner !== null)
         {
            // check that the html id matches, abort otherwise
            var tmp = owner.id;
            if (tmp.indexOf(htmlId) != 0)
            {
               return true;
            }
            var tmp = tmp.substring(htmlId.length + 1);
            var parts = tmp.split('-');
            if (parts.length < 1)
            {
               // stop here
               return true;
            }
            // the first entry is the handler method to call
            var action = parts[0];
            if (typeof handlerObject[action] == "function")
            {
               // extract the param part of the id
               var param = parts.length > 1 ? tmp.substring(action.length + 1) : null;
               handlerObject[action].call(handlerObject, htmlId, owner.id, param);
               args[1].stop = true;
            }
         }
         return true;
      }
   );
}


Alfresco.util.editor = {};

Alfresco.util.editor.getTextOnlyToolbarConfig = function(msg)
{
   var toolbar = {
      titlebar: false,
      buttons: [
         { group: 'textstyle', label: msg("yuieditor.toolbar.group.font"),
            buttons: [
               { type: 'push', label: msg("yuieditor.toolbar.item.bold"), value: 'bold' },
               { type: 'push', label: msg("yuieditor.toolbar.item.italic"), value: 'italic' },
               { type: 'push', label: msg("yuieditor.toolbar.item.underline"), value: 'underline' },
               { type: 'separator' },
               { type: 'color', label: msg("yuieditor.toolbar.item.fontcolor"), value: 'forecolor', disabled: true },
               { type: 'color', label: msg("yuieditor.toolbar.item.backgroundcolor"), value: 'backcolor', disabled: true }
            ]
         },
         { type: 'separator' },
         { group: 'indentlist', label: msg("yuieditor.toolbar.group.lists"),
            buttons: [
               { type: 'push', label: msg("yuieditor.toolbar.item.createunorderedlist"), value: 'insertunorderedlist' },
               { type: 'push', label: msg("yuieditor.toolbar.item.createorderedlist"), value: 'insertorderedlist' }
            ]
         },
         { type: 'separator' },
         { group: 'insertitem', label: msg("yuieditor.toolbar.group.link"),
            buttons: [
              { type: 'push', label: msg("yuieditor.toolbar.item.link"), value: 'createlink', disabled: true }
            ]
        }
      ]
   };
   return toolbar
}
