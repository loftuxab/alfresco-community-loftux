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
 * Contains utility functions to work with node references on the client side.
 * Node references can't be used as element id's as they contain invalid characters.
 * 
 * These helper function allow converting from/to normal/escaped node references.
 */
Alfresco.util.noderefs = {};

/** Escapes a node reference to be usable as element id.
 * @return a nodeReference where :// has been replaced by _
 */
Alfresco.util.noderefs.escape = function(nodeRef)
{
   return nodeRef.replace(/\:\/\//, "_").replace(/\//, "_");
}

     
Alfresco.util.noderefs.unescape = function(escapedNodeRef)
{
   return escapedNodeRef.replace(/_/, "://").replace(/_/, "/");
}
     
Alfresco.util.noderefs.escapedToUrl = function(escapedNodeRef)
{
   return escapedNodeRef.replace(/_/g, "/");
}



Alfresco.util.dom = {};

/**
 * Updates a div content and makes sure the div is displayed
 */
Alfresco.util.dom.updateAndShowDiv = function(divId, newHTML)
{
   var elem = YAHOO.util.Dom.get(divId);
   elem.innerHTML = newHTML;
   YAHOO.util.Dom.removeClass(elem, "hidden");
};
     
Alfresco.util.dom.showDiv = function(divId)
{
   var elem = YAHOO.util.Dom.get(divId);
   YAHOO.util.Dom.removeClass(elem, "hidden");
};
      
Alfresco.util.dom.hideDiv = function(divId)
{
   var elem = YAHOO.util.Dom.get(divId);
   YAHOO.util.Dom.addClass(elem, "hidden");          
};

Alfresco.util.dom.hideAndRemoveDivContent = function(divId)
{
   var elem = YAHOO.util.Dom.get(divId);
   YAHOO.util.Dom.addClass(elem, "hidden");
   elem.innerHTML = "";
},


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




Alfresco.util.blog = {};

/**
 * Redirects to the view page of a blog post
 */
Alfresco.util.blog.loadBlogPostViewPage = function(site, container, path, postId)
{
   window.location =  Alfresco.util.blog.blogPostViewPageUrl(site, container, path, postId);
}

Alfresco.util.blog.blogPostViewPageUrl = function(site, container, path, postId)
{
   return Alfresco.constants.URL_CONTEXT + "page/site/" + site + "/blog-postview" +
                      "?container=" + container + 
                      "&path=" + path +
                      "&postId=" + postId;
}

Alfresco.util.blog.loadBlogPostCreatePage = function(site, container, path)
{
   window.location =  Alfresco.constants.URL_CONTEXT + "page/site/" + site + "/blog-postedit" +
                      "?container=" + container +
                      "&path=" + path;
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
Alfresco.util.blog.loadBlogPostListPage = function(site, container, path, filter, tag)
{
   var url = Alfresco.constants.URL_CONTEXT + "page/site/" + site + "/blog-postlist" +
               "?container=" + container +
               "&path=" + path;
   if (filter != null)
   {
      url += "&filter=" + filter;
   }
   if (tag != null)
   {
      url += "&tag=" + tag;
   }
   window.location = url;
}
     
/**
 * Get the rest api url for a blog post
 */
Alfresco.util.blog.getBlogPostRestUrl = function(site, container, path, postId)
{
   var url = Alfresco.constants.PROXY_URI + "api/blog/post/site/" + site + "/" +
            container + "/";
   if (path != undefined && path.length > 0)
   {
      url += path + "/";
   }
   url += postId;
   return url;
};

Alfresco.util.blog.getPublishingRestUrl = function(site, container, path, postId)
{
   var url = Alfresco.constants.PROXY_URI + "api/blog/post/site/" + site + "/" + container + "/";
   if (path != undefined && path.length > 0)
   {
      url += path + "/";
   }
   url += postId + "/publishing";
   return url;
};



Alfresco.util.discussions = {};

/**
 * Redirects to the view page of a forum post
 */
Alfresco.util.discussions.loadForumPostViewPage = function(site, container, path, postId)
{
   window.location = Alfresco.constants.URL_CONTEXT + "page/site/" + site + "/discussions-topicview" +
                     "?container=" + container + 
                     "&path=" + path +
                     "&topicId=" + postId;
                     
}

Alfresco.util.blog.loadForumPostCreatePage = function(site, container, path)
{
   window.location =  Alfresco.constants.URL_CONTEXT + "page/site/" + site + "/discussions-createtopic" +
                      "?container=" + container +
                      "&path=" + path;
}

/**
 * Redirects to the EDIT page of a forum post
 */
Alfresco.util.discussions.loadForumPostEditPage = function(site, container, path, postId)
{
   window.location = Alfresco.constants.URL_CONTEXT + "page/site/" + site + "/discussions-topicview" +
                     "?container=" + container + 
                     "&path=" + path +
                     "&topicId=" + postId +
                     "&edit=true";
}

/**
 * Redirects to the forum listing page
 */
Alfresco.util.discussions.loadForumPostListPage = function(site, container, path, filter, tag)
{
   var url =  Alfresco.constants.URL_CONTEXT + "page/site/" + site + "/discussions-topiclist" +
                      "?container=" + container +
                      "&path=" + path;
   if (filter != null)
   {
      url += "&filter=" + filter;
   }
   if (tag != null)
   {
      url += "&tag=" + tag;
   }
   window.location = url;           
};

/**
 * Get the rest api url for a topic
 */
Alfresco.util.discussions.getTopicRestUrl = function(site, container, path, topicId)
{
   var url = Alfresco.constants.PROXY_URI + "api/forum/post/site/" + site + "/" +
            container + "/";
   if (path != undefined && path.length > 0)
   {
      url += path + "/";
   }
   url += topicId;
   return url;
};


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



Alfresco.util.ajaxtools = {};

/**
 * Extracts all the javascript parts from the the
 * html and returns both of them as an array
 * 
 * @return all js snippets concatenated or null if none could be found
 */
Alfresco.util.ajaxtools.extractScripts = function(html)
{
   var scripts = [];
   var script = null;
   var regexp = /<script[^>]*>([\s\S]*?)<\/script>/gi;
   while ((script = regexp.exec(html)))
   {
      scripts.push(script[1]);
   }
   if (scripts.length > 0)
   {
      return scripts.join("\n");
   }
   else
   {
      return null;
   }
}

Alfresco.util.ajaxtools.removeScripts = function(html)
{
   var regexp = /<script[^>]*>([\s\S]*?)<\/script>/gi;
   return html.replace(regexp, '');
}

Alfresco.util.ajaxtools.loadJSCode = function(jscode)
{
   window.setTimeout(jscode, 0);
}
