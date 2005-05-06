package org.alfresco.web.bean.wizard;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.apache.log4j.Logger;

import org.alfresco.repo.node.InvalidNodeRefException;
import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.ref.NodeRef;
import org.alfresco.repo.ref.QName;
import org.alfresco.web.bean.NavigationBean;
import org.alfresco.web.bean.repository.Node;
import org.alfresco.web.bean.repository.Repository;
import org.alfresco.web.ui.common.Utils;
import org.alfresco.web.ui.common.component.UIModeList;

/**
 * Handler class used by the Add Content Wizard 
 * 
 * @author gavinc
 */
public class AddContentWizard
{
   private static Logger logger = Logger.getLogger(AddContentWizard.class);
   private static final String ERROR_NODEREF = "Unable to find the repository node referenced by Id: {0} - the node has probably been deleted from the database.";

   // add content wizard specific properties
   private String file;
   private String name;
   private String owner;
   private boolean overwrite;
      
   // common wizard properties
   private int currentStep = 1;
   private boolean finishDisabled = false;
   private String currentSpaceName;
   private NodeService nodeService;
   private NavigationBean navigator;

   /**
    * @return Returns the nodeService.
    */
   public NodeService getNodeService()
   {
      return this.nodeService;
   }

   /**
    * @param nodeService The nodeService to set.
    */
   public void setNodeService(NodeService nodeService)
   {
      this.nodeService = nodeService;
   }
   
   /**
    * @return Returns the navigation bean instance.
    */
   public NavigationBean getNavigator()
   {
      return navigator;
   }
   
   /**
    * @param navigator The NavigationBean to set.
    */
   public void setNavigator(NavigationBean navigator)
   {
      this.navigator = navigator;
   }
   
   /**
    * @return Returns the currentStep.
    */
   public int getCurrentStep()
   {
      return currentStep;
   }

   /**
    * @param currentStep The currentStep to set.
    */
   public void setCurrentStep(int currentStep)
   {
      this.currentStep = currentStep;
   }
   
   /**
    * @return Returns the currentSpaceName.
    */
   public String getCurrentSpaceName()
   {
      // NOTE: We do this ourself rather than using the browse bean as when the 
      //       wizard potentially gets restored from the shelf the current node
      //       may have changed from when the wizard was launched. That means we
      //       should also store the current id too!
      
      if (this.currentSpaceName == null)
      {
         String spaceName = "Alfresco Home";
         String currentNodeId = this.navigator.getCurrentNodeId();
         if (currentNodeId != null)
         {
            try
            {
               NodeRef ref = new NodeRef(Repository.getStoreRef(), currentNodeId);
               QName qname = this.nodeService.getPrimaryParent(ref).getQName();
               spaceName = qname.getLocalName();
            }
            catch (InvalidNodeRefException refErr)
            {
               Utils.addErrorMessage(MessageFormat.format(ERROR_NODEREF, new Object[] {currentNodeId}));
            }
         }
         
         this.currentSpaceName = spaceName;
      } 
         
      return this.currentSpaceName;
   }

   /**
    * @param currentSpaceName The currentSpaceName to set.
    */
   public void setCurrentSpaceName(String currentSpaceName)
   {
      this.currentSpaceName = currentSpaceName;
   }

   /**
    * @return Returns the finishDisabled.
    */
   public boolean isFinishDisabled()
   {
      return finishDisabled;
   }

   /**
    * @param finishDisabled The finishDisabled to set.
    */
   public void setFinishDisabled(boolean finishDisabled)
   {
      this.finishDisabled = finishDisabled;
   }

   /**
    * @return Returns the file.
    */
   public String getFile()
   {
      return file;
   }

   /**
    * @param file The file to set.
    */
   public void setFile(String file)
   {
      this.file = file;
   }

   /**
    * @return Returns the name.
    */
   public String getName()
   {
      return name;
   }

   /**
    * @param name The name to set.
    */
   public void setName(String name)
   {
      this.name = name;
   }

   /**
    * @return Returns the overwrite.
    */
   public boolean isOverwrite()
   {
      return overwrite;
   }

   /**
    * @param overwrite The overwrite to set.
    */
   public void setOverwrite(boolean overwrite)
   {
      this.overwrite = overwrite;
   }

   /**
    * @return Returns the owner.
    */
   public String getOwner()
   {
      return owner;
   }

   /**
    * @param owner The owner to set.
    */
   public void setOwner(String owner)
   {
      this.owner = owner;
   }

   /**
    * Deals with the next button being pressed
    * 
    * @return
    */
   public String next()
   {
      this.currentStep++;
      
      // determine whether the finish button should be enabled
      evaluateFinishButtonState();
      
      // determine which page to go to next
      String nextPage = determinePageForStep(this.currentStep);
      
      // navigate
      navigate(nextPage);
      
      if (logger.isDebugEnabled())
      {
         logger.debug("Navigating to next page: " + nextPage);
         logger.debug("currentStep: " + this.currentStep);
      }
      
      // return null to prevent the naviagtion handler looking up the next page
      return null;
   }
   
   /**
    * Deals with the back button being pressed
    * 
    * @return
    */
   public String back()
   {       
      this.currentStep--;
      
      // determine whether the finish button should be enabled
      evaluateFinishButtonState();
      
      // determine which page to go to next
      String previousPage = determinePageForStep(this.currentStep);
      
      // navigate
      navigate(previousPage);
      
      if (logger.isDebugEnabled())
      {
         logger.debug("Navigating to previous page: " + previousPage);
         logger.debug("currentStep: " + this.currentStep);
      }
      
      // return null to prevent the naviagtion handler looking up the next page
      return null;
   }
   
   /**
    * Deals with the finish button being pressed
    * 
    * @return outcome
    */
   public String finish()
   {
      if (logger.isDebugEnabled())
         logger.debug(getSummary());
      
      // String nodeId = getNavigator().getCurrentNodeId();
      
      // reset the state
      reset();
      
      // navigate
      navigate("/jsp/browse/browse.jsp");
      
      return null;
   }
   
   /**
    * Deals with the cancel button being pressed
    * 
    * @return
    */
   public String cancel()
   {
      // reset the state
      reset();
      
      // navigate
      navigate("/jsp/browse/browse.jsp");
      
      return null;
   }
   
   /**
    * Deals with the minimise button being pressed
    * 
    * @return
    */
   public String minimise()
   {
      // navigate
      navigate("/jsp/browse/browse.jsp");
      
      return null;
   }
   
   /**
    * @return Returns the summary data for the wizard.
    */
   public String getSummary()
   {
      StringBuilder builder = new StringBuilder();
      builder.append("File: ").append(this.file).append("<br/>");
      builder.append("Name: ").append(this.name).append("<br/>");
      builder.append("Owner: ").append(this.owner).append("<br/>");
      builder.append("Overwrite: ").append(this.overwrite).append("<br/>");
      
      return builder.toString();
   }

   /**
    * Called when the step is changed by the left panel
    * 
    * @param event The event representing the step change
    */
   public void stepChanged(ActionEvent event)
   {
      UIModeList viewList = (UIModeList)event.getComponent();
      int step = Integer.parseInt(viewList.getValue().toString());
      
      String page = determinePageForStep(step);
      navigate(page);
      
      if (logger.isDebugEnabled())
         logger.debug("Navigating directly to: " + page);
   }
   
   /**
    * Sets the page to navigate to
    * 
    * @param page Page to navigate to
    */
   private void navigate(String page)
   {
      FacesContext ctx = FacesContext.getCurrentInstance();
      UIViewRoot newRoot = ctx.getApplication().getViewHandler().createView(ctx, page);
      ctx.setViewRoot(newRoot);
      ctx.renderResponse();
   }
   
   /**
    * Returns the page to be navigated to for the given step
    * 
    * @param step
    * @return
    */
   private String determinePageForStep(int step)
   {
      // TODO: in the wizard framework make this abstract and represent step
      //       0 as the page the wizard was launched from and step -1 as the
      //       cancel page
      
      String page = null;
      String dir = "/jsp/wizard/add-content/";
      
      switch (step)
      {
         case 1:
         {
            page = dir + "details.jsp";
            break;
         }
         case 2:
         {
            page = dir + "summary.jsp";
            break;
         }
         default:
         {
            page = "/jsp/browse/browse.jsp";
         }
      }
      
      return page;
   }
   
   /**
    * Determines whether the finish button should be enabled and sets
    * the finishDisabled flag appropriately
    */
   private void evaluateFinishButtonState()
   {
      // always enabled for now
      this.finishDisabled = false;
   }
   
   /**
    * Resets the state of the wizard
    */
   private void reset()
   {  
      // reset all variables
      this.currentStep = 1;
      this.currentSpaceName = null;
      this.finishDisabled = false;
      this.name = null;
      this.owner = null;
      this.file = null;
      this.overwrite = false;
   }
}
