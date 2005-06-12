package org.alfresco.web.ui.common.converter;

import java.text.DecimalFormat;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.DateTimeConverter;

import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.web.bean.repository.Repository;
import org.springframework.web.jsf.FacesContextUtils;

/**
 * Converter class to convert the size of an item in bytes into a readable KB/MB form.
 * 
 * @author gavinc
 */
public class ByteSizeConverter extends DateTimeConverter
{
   /**
    * <p>The standard converter id for this converter.</p>
    */
   public static final String CONVERTER_ID = "org.alfresco.faces.ByteSizeConverter";

   private static final String POSTFIX_KB = " KB";
   private static final String POSTFIX_MB = " MB";
   private static final String POSTFIX_GB = " GB";
   
   private static final String NUMBER_PATTERN = "###,###.##";
   
   /**
    * @see javax.faces.convert.Converter#getAsObject(javax.faces.context.FacesContext, javax.faces.component.UIComponent, java.lang.String)
    */
   public Object getAsObject(FacesContext context, UIComponent component, String value)
   {
      return Long.parseLong(value);
   }

   /**
    * @see javax.faces.convert.Converter#getAsString(javax.faces.context.FacesContext, javax.faces.component.UIComponent, java.lang.Object)
    */
   public String getAsString(FacesContext context, UIComponent component, Object value)
   {
      String result = "";
      if (value != null)
      {
         result = value.toString();
      }
      
      long size;
      if (value instanceof Long)
      {
         size = (Long)value;
      }
      else if (value instanceof String)
      {
         try
         {
            size = Long.parseLong((String)value);
         }
         catch (NumberFormatException ne)
         {
            return result;
         }
      }
      else
      {
         return result;
      }
      
      // get formatter
      // TODO: can we cache this instance...? DecimalFormat is not threadsafe! Need threadlocal instance.
      DecimalFormat formatter = new DecimalFormat(NUMBER_PATTERN);
      
      StringBuilder buf = new StringBuilder();
      
      if (size < 999999)
      {
         double val = ((double)size) / 1024.0;
         buf.append(formatter.format(val));
         buf.append(POSTFIX_KB);
      }
      else if (size < 999999999)
      {
         double val = ((double)size) / 1048576.0;
         buf.append(formatter.format(val));
         buf.append(POSTFIX_MB);
      }
      else
      {
         double val = ((double)size) / 1073741824.0;
         buf.append(formatter.format(val));
         buf.append(POSTFIX_GB);
      }
      
      return buf.toString();
   }
}
