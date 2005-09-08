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
package org.alfresco.web.ui.repo.component.template;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.service.namespace.QName;

import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans.StringModel;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

/**
 * @author Kevin Roast
 */
public final class HasAspectMethod implements TemplateMethodModelEx
{
   /**
    * @see freemarker.template.TemplateMethodModel#exec(java.util.List)
    */
   public Object exec(List args) throws TemplateModelException
   {
      int result = 0;
      
      if (args.size() == 2)
      {
         BeanModel arg0 = (BeanModel)args.get(0);
         BeanModel arg1 = (BeanModel)args.get(1);
         if (arg0.getWrappedObject() instanceof TemplateNode)
         {
            if ( ((TemplateNode)arg0.getWrappedObject()).hasAspect(arg1.getWrappedObject().toString()) )
            {
               result = 1;
            }
         }
      }
      
      return Integer.valueOf(result);
   }
}
