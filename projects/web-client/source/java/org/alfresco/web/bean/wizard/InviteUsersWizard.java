/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 * You may obtain a copy of the License at
 *
 *     http://www.gnu.org/licenses/lgpl.txt
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific
 * language governing permissions and limitations under the
 * License.
 */
package org.alfresco.web.bean.wizard;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.faces.component.UISelectMany;
import javax.faces.component.UISelectOne;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.transaction.UserTransaction;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.web.app.Application;
import org.alfresco.web.bean.repository.Repository;
import org.alfresco.web.ui.common.Utils;
import org.alfresco.web.ui.common.component.UIGenericPicker;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Kevin Roast
 */
public class InviteUsersWizard extends AbstractWizardBean
{
   private static Log logger = LogFactory.getLog(InviteUsersWizard.class);
   
   // TODO: retrieve these from the config service
   private static final String WIZARD_TITLE = "Invite Users Wizard";
   private static final String WIZARD_DESC = "This wizard helps you to give other users access to your space.";
   private static final String STEP1_TITLE = "Step One - Invite Users";
   private static final String STEP1_DESCRIPTION = "Select the users and roles they will play in this space.";
   private static final String STEP2_TITLE = "Step Two - Notify Users";
   private static final String STEP2_DESCRIPTION = "Notify the selected users.";
   private static final String FINISH_INSTRUCTION = "To close this wizard and apply your changes click Finish.";
   
   /** I18N message strings */
   private static final String MSG_USERS  = "users";
   private static final String MSG_GROUPS = "groups";
   private static final String MSG_INVITED_SPACE = "invite_space";
   private static final String MSG_INVITED_ROLE  = "invite_role";
   
   /** NamespaceService bean reference */
   private NamespaceService namespaceService;
   
   /** whether to invite all or specify individual users or groups */
   private String invite = "users";
   private String notify = "yes";
   private List<SelectItem> selectedItems = null;
   private String subject = null;
   private String body = null;
   private String automaticText = null;
   

   /**
    * Initialises the wizard
    */
   public void init()
   {
      super.init();
      
      invite = "users";
      notify = "yes";
      selectedItems = new ArrayList<SelectItem>(8);
      subject = "";
      body = "";
      automaticText = "";
   }

   /**
    * @see org.alfresco.web.bean.wizard.AbstractWizardBean#finish()
    */
   public String finish()
   {
      return "browse";
   }
   
   /**
    * Populates the wizard's values with the current values
    * of the node about to be edited
    */
   public void populate()
   {
   }
   
   /**
    * Query callback method executed by the Generic Picker component.
    * This method is part of the contract to the Generic Picker, it is up to the backing bean
    * to execute whatever query is appropriate and return the results.
    * 
    * @param filterIndex        Index of the filter drop-down selection
    * @param contains           Text from the contains textbox
    * 
    * @return An array of SelectItem objects containing the results to display in the picker.
    */
   public SelectItem[] pickerCallback(int filterIndex, String contains)
   {
      FacesContext context = FacesContext.getCurrentInstance();
      
      SelectItem[] items;
      
      UserTransaction tx = null;
      try
      {
         tx = Repository.getUserTransaction(context);
         tx.begin();
         
         NodeRef peopleRef = Repository.getSystemPeopleFolderRef(context, nodeService, searchService);
         // build xpath to match 
         // NOTE: see SearcherComponentTest
         String xpath = "*[like(@" + NamespaceService.CONTENT_MODEL_PREFIX + ":" + "firstName, '%" + contains + "%', false)" +
                 " or " + "like(@" + NamespaceService.CONTENT_MODEL_PREFIX + ":" + "lastName, '%" + contains + "%', false)]";
         
         if (logger.isDebugEnabled())
            logger.debug("User/Groups Picker Query: " + xpath);
         
         List<NodeRef> nodes = searchService.selectNodes(
               peopleRef,
               xpath,
               null,
               this.namespaceService,
               false);
         
         items = new SelectItem[nodes.size()];
         for (int index=0; index<nodes.size(); index++)
         {
            NodeRef ref = nodes.get(index);
            String firstName = (String)this.nodeService.getProperty(ref, ContentModel.PROP_FIRSTNAME);
            String lastName = (String)this.nodeService.getProperty(ref, ContentModel.PROP_LASTNAME);
            SelectItem item = new SelectItem(ref, firstName + " " + lastName);
            items[index] = item;
         }
         
         // commit the transaction
         tx.commit();
      }
      catch (Exception err)
      {
         Utils.addErrorMessage(MessageFormat.format(Application.getMessage(
               FacesContext.getCurrentInstance(), Repository.ERROR_GENERIC), err.getMessage()), err );
         try { if (tx != null) {tx.rollback();} } catch (Exception tex) {}
         
         items = new SelectItem[0];
      }
      
      return items;
   }
   
   /**
    * Action handler called when the Add button is pressed to process the current selection
    */
   public void addSelection(ActionEvent event)
   {
      UIGenericPicker picker = (UIGenericPicker)event.getComponent().findComponent("picker");
      UISelectOne rolePicker = (UISelectOne)event.getComponent().findComponent("roles");
      
      String[] results = picker.getSelectedResults();
      if (results != null)
      {
         // TODO: get Role here from selectedRole (value) in role list component!
         String role = (String)rolePicker.getValue();
         
         for (int i=0; i<results.length; i++)
         {
            NodeRef ref = new NodeRef(results[i]);
            String firstName = (String)this.nodeService.getProperty(ref, ContentModel.PROP_FIRSTNAME);
            String lastName = (String)this.nodeService.getProperty(ref, ContentModel.PROP_LASTNAME);
            
            // only add if ref not already present in the list
            boolean foundExisting = false;
            String refString = ref.toString();
            for (int n=0; n<this.selectedItems.size(); n++)
            {
               if (refString.equals(this.selectedItems.get(n).getValue()))
               {
                  foundExisting = true;
                  break;
               }
            }
            if (foundExisting == false)
            {
               this.selectedItems.add(new SelectItem(refString, firstName + " " + lastName));
            }
         }
      }
   }
   
   /**
    * Action handler called when the Remove button is pressed to remove current selection.
    */
   public void removeSelection(ActionEvent event)
   {
      UISelectMany selector = (UISelectMany)event.getComponent().findComponent("selection");
      Object[] selection = selector.getSelectedValues();
      if (selection != null)
      {
         for (int i=0; i<selection.length; i++)
         {
            String value = (String)selection[i];
            for (int n=0; n<this.selectedItems.size(); n++)
            {
               if (value.equals(this.selectedItems.get(n).getValue()))
               {
                  this.selectedItems.remove(n);
               }
            }
         }
      }
   }
   
   /**
    * Property accessed by the Generic Picker component.
    * 
    * @return the array of filter options to show in the users/groups picker
    */
   public SelectItem[] getFilters()
   {
      ResourceBundle bundle = Application.getBundle(FacesContext.getCurrentInstance());
      
      return new SelectItem[] {
            new SelectItem("0", bundle.getString(MSG_USERS)),
            new SelectItem("1", bundle.getString(MSG_GROUPS)) };
   }
   
   /**
    * @return The list of available roles for the users/groups
    */
   public SelectItem[] getRoles()
   {
      // TODO: get roles from the Permission services?
      return new SelectItem[] {
            new SelectItem("0", "Administrator"),
            new SelectItem("1", "Contributor"),
            new SelectItem("2", "Guest") };
   }
   
   /**
    * @param namespaceService The namespaceService to set.
    */
   public void setNamespaceService(NamespaceService namespaceService)
   {
      this.namespaceService = namespaceService;
   }
   
   /**
    * @return Returns the invite listbox selection.
    */
   public String getInvite()
   {
      return this.invite;
   }

   /**
    * @param invite The invite listbox selection to set.
    */
   public void setInvite(String invite)
   {
      this.invite = invite;
   }
   
   /**
    * @return Returns the notify listbox selection.
    */
   public String getNotify()
   {
      return this.notify;
   }

   /**
    * @param notify The notify listbox selection to set.
    */
   public void setNotify(String notify)
   {
      this.notify = notify;
   }

   /**
    * @return Returns the automaticText.
    */
   public String getAutomaticText()
   {
      return this.automaticText;
   }

   /**
    * @param automaticText The automaticText to set.
    */
   public void setAutomaticText(String automaticText)
   {
      this.automaticText = automaticText;
   }

   /**
    * @return Returns the selectedItems.
    */
   public List<SelectItem> getSelectedItems()
   {
      return this.selectedItems;
   }

   /**
    * @param selectedItems The selectedItems to set.
    */
   public void setSelectedItems(List<SelectItem> selectedItems)
   {
      this.selectedItems = selectedItems;
   }
   
   /**
    * @return Returns the email body text.
    */
   public String getBody()
   {
      return this.body;
   }

   /**
    * @param body The email body text to set.
    */
   public void setBody(String body)
   {
      this.body = body;
   }

   /**
    * @return Returns the email subject text.
    */
   public String getSubject()
   {
      return this.subject;
   }

   /**
    * @param subject The email subject text to set.
    */
   public void setSubject(String subject)
   {
      this.subject = subject;
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
         default:
         {
            stepDesc = "";
         }
      }
      
      return stepDesc;
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
         default:
         {
            stepTitle = "";
         }
      }
      
      return stepTitle;
   }
   
   /**
    * @see org.alfresco.web.bean.wizard.AbstractWizardBean#getStepInstructions()
    */
   public String getStepInstructions()
   {
      String stepInstruction = null;
      
      switch (this.currentStep)
      {
         case 2:
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
    * @see org.alfresco.web.bean.wizard.AbstractWizardBean#next()
    */
   public String next()
   {
      String outcome = super.next();
      
      if (outcome.equals("notify"))
      {
         FacesContext context = FacesContext.getCurrentInstance();
         
         // prepare automatic text for email
         StringBuilder buf = new StringBuilder(256);
         
         String personName = Application.getCurrentUser(context).getFullName(getNodeService());
         String msgInvite = Application.getMessage(context, MSG_INVITED_SPACE);
         buf.append(MessageFormat.format(msgInvite, new Object[] {
               getNavigator().getNodeProperties().get("name"),
               personName}) );
         
         buf.append("<br>");
         
         // TODO: show the role label here!
         String role = "Administrator [TBD]";
         String msgRole = Application.getMessage(context, MSG_INVITED_ROLE);
         buf.append(MessageFormat.format(msgRole, new Object[] {role}));
         
         this.automaticText = buf.toString();;
      }
      
      return outcome;
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
            outcome = "invite";
            break;
         }
         case 2:
         {
            outcome = "notify";
            break;
         }
         default:
         {
            outcome = CANCEL_OUTCOME;
         }
      }
      
      return outcome;
   }
}
