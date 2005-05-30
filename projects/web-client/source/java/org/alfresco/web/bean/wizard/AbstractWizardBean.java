package org.alfresco.web.bean.wizard;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.alfresco.repo.node.NodeService;
import org.alfresco.repo.search.Searcher;
import org.alfresco.web.app.context.UIContextService;
import org.alfresco.web.bean.BrowseBean;
import org.alfresco.web.bean.NavigationBean;
import org.apache.log4j.Logger;

/**
 * Abstract bean used as the base class for all wizard backing beans.
 * 
 * @author gavinc
 */
public abstract class AbstractWizardBean
{
   private static Logger logger = Logger.getLogger(AbstractWizardBean.class);
   
   protected static final String FINISH_OUTCOME = "finish";
   protected static final String CANCEL_OUTCOME = "cancel";
   protected static final String DEFAULT_INSTRUCTION = "To continue click Next.";
   protected static final String SUMMARY_TITLE = "Summary";
   protected static final String SUMMARY_DESCRIPTION = "The information you entered is shown below.";
   
   // common wizard properties
   protected int currentStep = 1;
   protected boolean editMode = false;
   protected NodeService nodeService;
   protected Searcher searchService;
   protected NavigationBean navigator;
   protected BrowseBean browseBean;
   
   /**
    * @return Returns the wizard description
    */
   public abstract String getWizardDescription();

   /**
    * @return Returns the wizard title
    */
   public abstract String getWizardTitle();
   
   /**
    * @return Returns the title for the current step
    */
   public abstract String getStepTitle();

   /**
    * @return Returns the description for the current step
    */
   public abstract String getStepDescription();
   
   /**
    * @return Returns the instructional text for the current step
    */
   public abstract String getStepInstructions();

   /**
    * Determines the outcome string for the given step number
    * 
    * @param step The step number to get the outcome for
    * @return The outcome
    */
   protected abstract String determineOutcomeForStep(int step);
   
   /**
    * Handles the finish button being pressed
    * 
    * @return The finish outcome
    */
   public abstract String finish();
   
   /**
    * Action listener called when the wizard is being launched allowing
    * state to be setup
    */
   public void startWizard(ActionEvent event)
   {
      // refresh the UI, calling this method now is fine as it basically makes sure certain
      // beans clear the state - so when we finish the wizard other beans will have been reset
      UIContextService.getInstance(FacesContext.getCurrentInstance()).notifyBeans();
      
      // initialise the wizard in case we are launching 
      // after it was navigated away from
      init();
      
      if (logger.isDebugEnabled())
         logger.debug("Started wizard : " + getWizardTitle());
   }
   
   /**
    * Action listener called when the wizard is being launched for 
    * editing an existing node.
    */
   public void startWizardForEdit(ActionEvent event)
   {
      // refresh the UI, calling this method now is fine as it basically makes sure certain
      // beans clear the state - so when we finish the wizard other beans will have been reset
      UIContextService.getInstance(FacesContext.getCurrentInstance()).notifyBeans();
      
      // set the wizard in edit mode
      this.editMode = true;
      
      // populate the wizard's default values with the current value
      // from the node being edited
      init();
      populate();
      
      if (logger.isDebugEnabled())
         logger.debug("Started wizard : " + getWizardTitle() + " for editing");
   }
 
   /**
    * Deals with the next button being pressed
    * 
    * @return
    */
   public String next()
   {  
      this.currentStep++;
      
      // determine which page to go to next
      String outcome = determineOutcomeForStep(this.currentStep);
            
      if (logger.isDebugEnabled())
      {
         logger.debug("current step is now: " + this.currentStep);
         logger.debug("Next outcome: " + outcome);
      }
      
      // return the outcome for navigation
      return outcome;
   }
   
   /**
    * Deals with the back button being pressed
    * 
    * @return
    */
   public String back()
   {      
      this.currentStep--;
      
      // determine which page to go to next
      String outcome = determineOutcomeForStep(this.currentStep);
      
      if (logger.isDebugEnabled())
      {
         logger.debug("current step is now: " + this.currentStep);
         logger.debug("Back outcome: " + outcome);
      }
      
      // return the outcome for navigation
      return outcome;
   }
   
   /**
    * Handles the cancelling of the wizard
    * 
    * @return The cancel outcome
    */
   public String cancel()
   {
      // reset the state
      init();
      
      return CANCEL_OUTCOME;
   }
   
   /**
    * Initialises the wizard
    */
   public void init()
   {
      this.currentStep = 1;
   }
   
   /**
    * Populates the wizard's values with the current values
    * of the node about to be edited
    */
   public void populate()
   {
      // subclasses will override this method to setup accordingly
   }

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
    * @return Returns the searchService.
    */
   public Searcher getSearchService()
   {
      return searchService;
   }

   /**
    * @param searchService The searchService to set.
    */
   public void setSearchService(Searcher searchService)
   {
      this.searchService = searchService;
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
    * @return The BrowseBean
    */
   public BrowseBean getBrowseBean()
   {
      return this.browseBean;
   }

   /**
    * @param browseBean The BrowseBean to set.
    */
   public void setBrowseBean(BrowseBean browseBean)
   {
      this.browseBean = browseBean;
   }  
}
