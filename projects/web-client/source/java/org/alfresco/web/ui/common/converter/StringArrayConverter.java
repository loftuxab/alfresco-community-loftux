package org.alfresco.web.ui.common.converter;

import java.util.ArrayList;
import java.util.StringTokenizer;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 * Converter class to convert a String array to and from a comma separated list
 * 
 * @author gavinc
 */
public class StringArrayConverter implements Converter
{
   /**
    * <p>The standard converter id for this converter.</p>
    */
   public static final String CONVERTER_ID = "converter:StringArray";

   /**
    * @see javax.faces.convert.Converter#getAsObject(javax.faces.context.FacesContext, javax.faces.component.UIComponent, java.lang.String)
    */
   public Object getAsObject(FacesContext context, UIComponent component, String value)
   {
      ArrayList list = new ArrayList();
      StringTokenizer tokenizer = new StringTokenizer(value, ",");
      while (tokenizer.hasMoreTokens())
      {
         String token = tokenizer.nextToken();
         list.add(token);
      }
      
      // convert the list to a string array
      String[] arr = new String[list.size()]; 
      list.toArray(arr);
      
      return arr;
   }

   /**
    * @see javax.faces.convert.Converter#getAsString(javax.faces.context.FacesContext, javax.faces.component.UIComponent, java.lang.Object)
    */
   public String getAsString(FacesContext context, UIComponent component, Object value)
   {
      String str = null;
      
      if (value instanceof String[])
      {
         StringBuilder buffer = new StringBuilder();
         String[] arr = (String[])value;
         for (int x = 0; x < arr.length; x++)
         {
            buffer.append(arr[x]);
            if (x != (arr.length-1))
            {
               buffer.append(",");
            }
         }
         
         str = buffer.toString();
      }
      
      return str;
   }
   
   
}
