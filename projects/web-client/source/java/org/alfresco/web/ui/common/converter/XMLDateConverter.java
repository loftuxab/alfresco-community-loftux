package org.alfresco.web.ui.common.converter;

import java.util.Date;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.DateTimeConverter;

import org.alfresco.util.Conversion;

/**
 * Converter class to convert an XML date representation into a Date
 * 
 * @author gavinc
 */
public class XMLDateConverter extends DateTimeConverter
{
   /**
    * <p>The standard converter id for this converter.</p>
    */
   public static final String CONVERTER_ID = "org.alfresco.faces.XMLDataConverter";

   /**
    * @see javax.faces.convert.Converter#getAsObject(javax.faces.context.FacesContext, javax.faces.component.UIComponent, java.lang.String)
    */
   public Object getAsObject(FacesContext context, UIComponent component, String value)
   {
      return Conversion.dateFromXmlDate(value);
   }

   /**
    * @see javax.faces.convert.Converter#getAsString(javax.faces.context.FacesContext, javax.faces.component.UIComponent, java.lang.Object)
    */
   public String getAsString(FacesContext context, UIComponent component, Object value)
   {
      String str = null;
      
      if (value instanceof String)
      {
         Date date = Conversion.dateFromXmlDate((String)value);
         str = super.getAsString(context, component, date);
      }
      else
      {
         str = super.getAsString(context, component, value);
      }
      
      return str;
   }
   
   
}
