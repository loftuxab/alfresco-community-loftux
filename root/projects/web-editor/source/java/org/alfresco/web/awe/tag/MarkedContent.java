/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
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
 * http://www.alfresco.com/legal/licensing"
 */

package org.alfresco.web.awe.tag;

/**
 * Object representing marker content.
 * 
 * @author Gavin Cornwell
 */
public class MarkedContent
{
   private String markerId;
   private String contentId;
   private String formId;

   /**
    * Constructor
    * 
    * @param markerId The identifier of the span element being used to mark the content
    * @param contentId The identifier of the actual content
    * @param formId The identifier of the form to use to edit the content
    */
   public MarkedContent(String markerId, String contentId, String formId)
   {
      if (markerId == null || markerId.length() == 0) 
      { 
         throw new IllegalArgumentException("markerId is a mandatory parameter"); 
      }

      if (contentId == null || contentId.length() == 0) 
      { 
         throw new IllegalArgumentException("contentId is a mandatory parameter"); 
      }

      this.markerId = markerId;
      this.contentId = contentId;
      this.formId = formId;
   }

   /**
    * Returns the identifier of the span element being used to mark the content
    * 
    * @return The marker identifier
    */
   public String getMarkerId()
   {
      return this.markerId;
   }

   /**
    * Returns the identifier of the content
    * 
    * @return The content identifier
    */
   public String getContentId()
   {
      return this.contentId;
   }

   /**
    * Returns the identifier of the form
    * 
    * @return The form identifier
    */
   public String getFormId()
   {
      return this.formId;
   }

   @Override
   public String toString()
   {
      StringBuilder builder = new StringBuilder(super.toString());
      builder.append(" (contentId: ");
      builder.append(this.contentId);
      builder.append(", markerId: ");
      builder.append(this.markerId);
      builder.append(", formId: ");
      builder.append(this.formId).append(")");
      
      return builder.toString();
   }
}
