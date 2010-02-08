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

package org.customer;

import java.io.IOException;
import java.io.Writer;

import javax.servlet.jsp.JspException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Tag used to display the value of a property of an Alfresco node.
 * 
 * @author Gavin Cornwell
 */
public class PropertyTag extends AbstractCustomerTag
{
   private static final long serialVersionUID = -7972734141482504413L;

   private String property;

   /**
    * Returns the name of the property to display
    * 
    * @return Name of the property to display
    */
   public String getProperty()
   {
      return property;
   }

   /**
    * Sets the name of the property to display
    * 
    * @param name The name of the property to display
    */
   public void setProperty(String name)
   {
      this.property = name;
   }

   /**
    * @see javax.servlet.jsp.tagext.TagSupport#doStartTag()
    */
   public int doStartTag() throws JspException
   {
      try
      {
         Writer out = pageContext.getOut();

         // setup http call to content webscript
         String url = this.getRepoUrl() + "/service/api/metadata?nodeRef=" + getNodeRef() + "&shortQNames=true";
         HttpClient client = getHttpClient();
         GetMethod getContent = new GetMethod(url);
         getContent.setDoAuthentication(true);

         try
         {
            // execute the method
            client.executeMethod(getContent);

            // get the JSON response
            String jsonResponse = getContent.getResponseBodyAsString();
            JSONObject json = new JSONObject(jsonResponse);
            JSONObject props = json.getJSONObject("properties");
            if (props.has(this.property))
            {
               out.write(props.getString(this.property));
            }
         }
         finally
         {
            getContent.releaseConnection();
         }
      }
      catch (IOException ioe)
      {
         throw new JspException(ioe.toString());
      }
      catch (JSONException je)
      {
         throw new JspException(je.toString());
      }

      return SKIP_BODY;
   }

   @Override
   public void release()
   {
      super.release();

      this.property = null;
   }
}
