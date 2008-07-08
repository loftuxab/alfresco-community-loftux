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

Alfresco.util.blog = {};

/**
 * Redirects to the view page of a blog post
 */
Alfresco.util.blog.loadBlogPostViewPage = function(site, container, path, postId)
{
   window.location =  Alfresco.constants.URL_CONTEXT + "page/site/" + site + "/blog-postview" +
                      "?container=" + container + 
                      "&postId=" + post;
}

/**
 * Redirects to the blog edit page
 */
Alfresco.util.blog.loadBlogPostEditPage = function(site, container, path, postId)
{
   window.location =  Alfresco.constants.URL_CONTEXT + "page/site/" + site + "/blog-postedit" +
                      "?container=" + container +
                      "&path=" + path +
                      "&postId=" + postId;
}

/**
 * Redirects to the blog listing page
 */
Alfresco.util.blog.loadBlogPostListPage = function(site, container, path)
{
   window.location =  Alfresco.constants.URL_CONTEXT + "page/site/" + site + "/blog-postlist" +
                      "?container=" + container +
                      "&path=" + path;
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