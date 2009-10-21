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
 * This file defines functions used across different links related components
 */

Alfresco.util.links = {};

/**
 * Returns the html for the actions for a given links.
 * @param me the object that holds the _msg method used for i18n
 * @param data the links data
 * @param tagName the tag name to use for the actions. This will either be div or span, depending
 *                whether the actions are for the simple or detailed view.
 */
Alfresco.util.links.generateLinksActions = function generateLinksActions(me, data, tagName)
{
   var desc = '<div class="nodeEdit">';

   if (data.permissions["edit"])
   {
      desc += '<' + tagName + ' class="onEditLink"><a href="#" class="link-action-link-' + tagName + '">' + me.msg("action.edit") + '</a></' + tagName + '>';
   }
   if (data.permissions["delete"])
   {
      desc += '<' + tagName + ' class="onDeleteLink"><a href="#" class="link-action-link-' + tagName + '">' + me.msg("action.delete") + '</a></' + tagName + '>';
   }
   desc += '</div>';

   return desc;
};

