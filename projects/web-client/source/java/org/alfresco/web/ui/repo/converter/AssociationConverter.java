/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.web.ui.repo.converter;

import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.web.bean.repository.Repository;

/**
 * Converter class to convert an association state into a human readable form
 * 
 * @author Gavin Cornwell
 */
public class AssociationConverter implements Converter
{
   /**
    * <p>The standard converter id for this converter.</p>
    */
   public static final String CONVERTER_ID = "org.alfresco.faces.AssociationConverter";
      
   /**
    * @see javax.faces.convert.Converter#getAsObject(javax.faces.context.FacesContext, javax.faces.component.UIComponent, java.lang.String)
    */
   public Object getAsObject(FacesContext context, UIComponent component, String value)
         throws ConverterException
   {
      return value;
   }

   /**
    * @see javax.faces.convert.Converter#getAsString(javax.faces.context.FacesContext, javax.faces.component.UIComponent, java.lang.Object)
    */
   public String getAsString(FacesContext context, UIComponent component, Object value)
         throws ConverterException
   {
      String result = null;
      
      if (value != null)
      {
         NodeService nodeService = Repository.getServiceRegistry(context).getNodeService();
         if (nodeService != null)
         {
            StringBuilder builder = new StringBuilder();
            
            // get the path of the child of each association
            List assocs = (List)value;
            for (int x = 0; x < assocs.size(); x++)
            {
               if (x != 0)
               {
                  builder.append(", ");
               }
               AssociationRef assoc = (AssociationRef)assocs.get(x);
               builder.append(Repository.getDisplayPath(nodeService.getPath(assoc.getTargetRef())));
               builder.append("/");
               builder.append(Repository.getNameForNode(nodeService, assoc.getTargetRef()));
            }
            
            result = builder.toString();
         }
      }
      
      return result;
   }
}
