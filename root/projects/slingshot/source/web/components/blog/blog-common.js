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
 * Generate the REST url for a given blog post
 * 
 * @method Alfresco.util.blog.generatePublishingRestURL
 * @param site {string} the site id
 * @param container {string} the container id
 * @param postId {string} the post id/name
 * @return a REST url for publishing the post
 */
Alfresco.util.blog.generatePublishingRestURL = function generatePublishingRestURL(site, container, postId)
{
   return YAHOO.lang.substitute(Alfresco.constants.PROXY_URI + "api/blog/post/site/{site}/{container}/{postId}/publishing",
   {
      site: site,
      container: container,
      postId: postId
   });
};

/**
 * Generate a view url for a given site, container and blog post id.
 * 
 * @param postId the id/name of the post
 * @return an url to access the post
 */
Alfresco.util.blog.generateBlogPostViewUrl =  function generateBlogPostViewUrl(site, container, postId)
{
   var url = YAHOO.lang.substitute(Alfresco.constants.URL_CONTEXT + "page/site/{site}/blog-postview?container={container}&postId={postId}",
   {
      site: site,
      container: container,
      postId: postId
   });
   return url;
};

/**
 * Generates the status label text for a given blog post
 */
Alfresco.util.blog.generatePostStatusLabel = function generatePostStatusLabel(me, data)
{
   if (data.isDraft)
   {
      return "(" + me._msg("post.draft") + ")";
   }
   else if (data.isUpdated || data.isPublished)
   {
      var status = '';
      if (data.isUpdated)
      {
         status += "(" + me._msg("post.updated") + ") ";
      }

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

/**
 * Returns the html for the actions for a given blog post.
 * @param me the object that holds the _msg method used for i18n
 * @param data the blog post data
 * @param tagName the tag name to use for the actions. This will either be div or span, depending
 *                whether the actions are for the simple or detailed view.
 */         
Alfresco.util.blog.generateBlogPostActions = function generateBlogPostActions(me, data, tagName)
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

