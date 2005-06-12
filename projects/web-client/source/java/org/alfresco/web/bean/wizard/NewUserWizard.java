/*
 * Created on 10-Jun-2005
 */
package org.alfresco.web.bean.wizard;

import java.util.Collections;
import java.util.List;

import org.alfresco.web.bean.repository.Node;
import org.alfresco.web.ui.common.component.data.UIRichList;
import org.apache.log4j.Logger;

/**
 * @author Kevin Roast
 */
public class NewUserWizard extends AbstractWizardBean
{
   private static Logger logger = Logger.getLogger(NewUserWizard.class);
   
   // TODO: retrieve these from the config service
   private static final String WIZARD_TITLE = "New User Wizard";
   private static final String WIZARD_DESC = "Use this wizard to add a user to the repository.";
   private static final String STEP1_TITLE = "Step One - Person Properties";
   private static final String STEP1_DESCRIPTION = "Enter information about this person.";
   private static final String STEP2_TITLE = "Step Two - User Properties";
   private static final String STEP2_DESCRIPTION = "Enter information about this user.";
   private static final String FINISH_INSTRUCTION = "To add the user to this space click Finish.<br/>" +
                                                    "To review or change your selections click Back.";

   /** form variables */
   private String firstName = null;
   private String lastName = null;
   private String userName = null;
   private String email = null;
   private String companyId = null;
   private String homeSpaceName = null;
   private String homeSpaceLocation = null;
   
   /** Component references */
   private UIRichList usersRichList;
   
   
   /**
    * Initialises the wizard
    */
   public void init()
   {
      super.init();
      
      // reset all variables
      this.firstName = "";
      this.lastName = "";
      this.userName = "";
      this.email = "";
      this.companyId = "";
      this.homeSpaceName = "";
      this.homeSpaceLocation = null;
   }

   /**
    * @see org.alfresco.web.bean.wizard.AbstractWizardBean#populate()
    */
   public void populate()
   {
      // TODO: implement for Edit mode
   }
   
   /**
    * @see org.alfresco.web.bean.wizard.AbstractWizardBean#getWizardDescription()
    */
   public String getWizardDescription()
   {
      return WIZARD_DESC;
   }

   /**
    * @see org.alfresco.web.bean.wizard.AbstractWizardBean#getWizardTitle()
    */
   public String getWizardTitle()
   {
      return WIZARD_TITLE;
   }

   /**
    * @see org.alfresco.web.bean.wizard.AbstractWizardBean#getStepTitle()
    */
   public String getStepTitle()
   {
      String stepTitle = null;
      
      switch (this.currentStep)
      {
         case 1:
         {
            stepTitle = STEP1_TITLE;
            break;
         }
         case 2:
         {
            stepTitle = STEP2_TITLE;
            break;
         }
         case 3:
         {
            stepTitle = SUMMARY_TITLE;
            break;
         }
         default:
         {
            stepTitle = "";
         }
      }
      
      return stepTitle;
   }

   /**
    * @see org.alfresco.web.bean.wizard.AbstractWizardBean#getStepDescription()
    */
   public String getStepDescription()
   {
      String stepDesc = null;
      
      switch (this.currentStep)
      {
         case 1:
         {
            stepDesc = STEP1_DESCRIPTION;
            break;
         }
         case 2:
         {
            stepDesc = STEP2_DESCRIPTION;
            break;
         }
         case 3:
         {
            stepDesc = SUMMARY_DESCRIPTION;
            break;
         }
         default:
         {
            stepDesc = "";
         }
      }
      
      return stepDesc;
   }

   /**
    * @see org.alfresco.web.bean.wizard.AbstractWizardBean#getStepInstructions()
    */
   public String getStepInstructions()
   {
      String stepInstruction = null;
      
      switch (this.currentStep)
      {
         case 3:
         {
            stepInstruction = FINISH_INSTRUCTION;
            break;
         }
         default:
         {
            stepInstruction = DEFAULT_INSTRUCTION;
         }
      }
      
      return stepInstruction;
   }

   /**
    * @see org.alfresco.web.bean.wizard.AbstractWizardBean#determineOutcomeForStep(int)
    */
   protected String determineOutcomeForStep(int step)
   {
      String outcome = null;
      
      switch(step)
      {
         case 1:
         {
            outcome = "person-properties";
            break;
         }
         case 2:
         {
            outcome = "user-properties";
            break;
         }
         case 3:
         {
            outcome = "summary";
            break;
         }
         default:
         {
            outcome = CANCEL_OUTCOME;
         }
      }
      
      return outcome;
   }

   /**
    * @see org.alfresco.web.bean.wizard.AbstractWizardBean#finish()
    */
   public String finish()
   {
      String outcome = FINISH_OUTCOME;
      
      return outcome;
   }
   
   /**
    * @return Returns the summary data for the wizard.
    */
   public String getSummary()
   {
      return buildSummary(
            new String[] {"Name", "User Name", "Password", "Home Space"},
            new String[] {this.firstName + " " + this.lastName, this.userName, "********", this.homeSpaceName});
   }
   
   /**
    * @return the list of user Nodes to display
    */
   public List<Node> getUsers()
   {
      return Collections.EMPTY_LIST;
   }

   /**
    * @return Returns the companyId.
    */
   public String getCompanyId()
   {
      return this.companyId;
   }
   
   /**
    * @param companyId The companyId to set.
    */
   public void setCompanyId(String companyId)
   {
      this.companyId = companyId;
   }
   
   /**
    * @return Returns the email.
    */
   public String getEmail()
   {
      return this.email;
   }
   
   /**
    * @param email The email to set.
    */
   public void setEmail(String email)
   {
      this.email = email;
   }
   
   /**
    * @return Returns the firstName.
    */
   public String getFirstName()
   {
      return this.firstName;
   }
   
   /**
    * @param firstName The firstName to set.
    */
   public void setFirstName(String firstName)
   {
      this.firstName = firstName;
   }
   
   /**
    * @return Returns the homeSpaceLocation.
    */
   public String getHomeSpaceLocation()
   {
      return this.homeSpaceLocation;
   }
   
   /**
    * @param homeSpaceLocation The homeSpaceLocation to set.
    */
   public void setHomeSpaceLocation(String homeSpaceLocation)
   {
      this.homeSpaceLocation = homeSpaceLocation;
   }
   
   /**
    * @return Returns the homeSpaceName.
    */
   public String getHomeSpaceName()
   {
      return this.homeSpaceName;
   }
   
   /**
    * @param homeSpaceName The homeSpaceName to set.
    */
   public void setHomeSpaceName(String homeSpaceName)
   {
      this.homeSpaceName = homeSpaceName;
   }
   
   /**
    * @return Returns the lastName.
    */
   public String getLastName()
   {
      return this.lastName;
   }
   
   /**
    * @param lastName The lastName to set.
    */
   public void setLastName(String lastName)
   {
      this.lastName = lastName;
   }
   
   /**
    * @return Returns the userName.
    */
   public String getUserName()
   {
      return this.userName;
   }
   
   /**
    * @param userName The userName to set.
    */
   public void setUserName(String userName)
   {
      this.userName = userName;
   }

   /**
    * @return Returns the usersRichList.
    */
   public UIRichList getUsersRichList()
   {
      return this.usersRichList;
   }
   
   /**
    * @param usersRichList The usersRichList to set.
    */
   public void setUsersRichList(UIRichList usersRichList)
   {
      this.usersRichList = usersRichList;
   }
}
