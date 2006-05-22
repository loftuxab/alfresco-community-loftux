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
