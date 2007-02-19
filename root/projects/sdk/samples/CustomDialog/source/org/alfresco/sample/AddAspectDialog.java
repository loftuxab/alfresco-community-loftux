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

import javax.faces.context.FacesContext;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.web.bean.dialog.BaseDialogBean;
import org.alfresco.web.bean.repository.Repository;

/**
 * Bean implementation for the "Add Aspect Dialog"
 * 
 * @author gavinc
 */
public class AddAspectDialog extends BaseDialogBean
{
   protected String aspect;
   
   @Override
   protected String finishImpl(FacesContext context, String outcome) throws Exception
   {
      // get the space the action will apply to
      NodeRef nodeRef = this.browseBean.getActionSpace().getNodeRef();
      
      // resolve the fully qualified aspect name
      QName aspectToAdd = Repository.resolveToQName(this.aspect);
      
      // add the aspect to the space
      this.nodeService.addAspect(nodeRef, aspectToAdd, null);
      
      // return the default outcome
      return outcome;
   }

   @Override
   public boolean getFinishButtonDisabled()
   {
      return false;
   }

   public String getAspect()
   {
      return aspect;
   }

   public void setAspect(String aspect)
   {
      this.aspect = aspect;
   }
}
