package org.alfresco.web.ui.common.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.DateTimeConverter;

import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.service.cmr.repository.MimetypeService;
import org.alfresco.web.bean.repository.Repository;
import org.springframework.web.jsf.FacesContextUtils;

/**
 * Converter class to convert an XML date representation into a Date
 * 
 * @author gavinc
 */
public class MimeTypeConverter extends DateTimeConverter
{
   /**
    * <p>The standard converter id for this converter.</p>
    */
   public static final String CONVERTER_ID = "org.alfresco.faces.MimeTypeConverter";

   /**
    * @see javax.faces.convert.Converter#getAsObject(javax.faces.context.FacesContext, javax.faces.component.UIComponent, java.lang.String)
    */
   public Object getAsObject(FacesContext context, UIComponent component, String value)
   {
      return value;
   }

   /**
    * @see javax.faces.convert.Converter#getAsString(javax.faces.context.FacesContext, javax.faces.component.UIComponent, java.lang.Object)
    */
   public String getAsString(FacesContext context, UIComponent component, Object value)
   {
      String result = value.toString();
      
      if (value instanceof String)
      {
         MimetypeService mimetypeService = Repository.getServiceRegistry(context).getMimetypeService();
         result = mimetypeService.getDisplaysByMimetype().get(value.toString());
      }
      
      return result;
   }
   
   
}
