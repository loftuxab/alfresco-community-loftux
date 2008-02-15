/*
 * Copyright (C) 2005-2007 Alfresco Software Limited.
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
package org.alfresco.sample;

import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.alfresco.config.Config;
import org.alfresco.config.ConfigElement;
import org.alfresco.config.ConfigService;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.namespace.QName;
import org.alfresco.web.app.Application;
import org.alfresco.web.bean.content.CreateContentWizard;
import org.alfresco.web.bean.repository.Repository;
import org.alfresco.web.data.IDataContainer;
import org.alfresco.web.data.QuickSort;
import org.alfresco.web.ui.common.Utils;

public class CustomCreateContentWizard extends CreateContentWizard
{
   protected List<SelectItem> aspects;
   protected String aspect;
   
   // ------------------------------------------------------------------------------
   // Wizard implementation
   
   @Override
   protected String finishImpl(FacesContext context, String outcome) throws Exception
   {
      super.finishImpl(context, outcome);
      
      // add the selected aspect (the properties page after the wizard
      // will allow us to set the properties)
      QName aspectToAdd = Repository.resolveToQName(this.aspect);
      this.getNodeService().addAspect(this.createdNode, aspectToAdd, null);
      
      return outcome;
   }
   
   // ------------------------------------------------------------------------------
   // Bean Getters and Setters

   public String getAspect()
   {
      return aspect;
   }

   public void setAspect(String aspect)
   {
      this.aspect = aspect;
   }
   
   public List<SelectItem> getAspects()
   {
      if (this.aspects == null)
      {
         ConfigService svc = Application.getConfigService(FacesContext.getCurrentInstance());
         Config wizardCfg = svc.getConfig("Content Wizards");
         if (wizardCfg != null)
         {
            ConfigElement aspectsCfg = wizardCfg.getConfigElement("aspects");
            if (aspectsCfg != null)
            {
               FacesContext context = FacesContext.getCurrentInstance();
               this.aspects = new ArrayList<SelectItem>();
               for (ConfigElement child : aspectsCfg.getChildren())
               {
                  QName idQName = Repository.resolveToQName(child.getAttribute("name"));

                  // try and get the display label from config
                  String label = Utils.getDisplayLabel(context, child);

                  // if there wasn't a client based label try and get it from the dictionary
                  if (label == null)
                  {
                     AspectDefinition aspectDef = this.getDictionaryService().getAspect(idQName);
                     if (aspectDef != null)
                     {
                        label = aspectDef.getTitle();
                     }
                     else
                     {
                        label = idQName.getLocalName();
                     }
                  }
                  
                  this.aspects.add(new SelectItem(idQName.toString(), label));
               }
               
               // make sure the list is sorted by the label
               QuickSort sorter = new QuickSort(this.aspects, "label", true, IDataContainer.SORT_CASEINSENSITIVE);
               sorter.sort();
            }  
         }
      }
      
      return this.aspects;
   }
}
