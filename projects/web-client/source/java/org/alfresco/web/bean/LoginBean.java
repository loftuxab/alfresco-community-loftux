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
package org.alfresco.web.bean;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

import net.sf.acegisecurity.Authentication;
import net.sf.acegisecurity.providers.UsernamePasswordAuthenticationToken;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationException;
import org.alfresco.repo.security.authentication.AuthenticationService;
import org.alfresco.repo.security.authentication.RepositoryUserDetails;
import org.alfresco.service.cmr.repository.InvalidNodeRefException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.web.app.Application;
import org.alfresco.web.app.servlet.AuthenticationFilter;
import org.alfresco.web.bean.repository.Node;
import org.alfresco.web.bean.repository.Repository;
import org.alfresco.web.bean.repository.User;
import org.alfresco.web.ui.common.Utils;
import org.alfresco.web.ui.common.component.IBreadcrumbHandler;

/**
 * JSF Managed Bean. Backs the "login.jsp" view to provide the form fields used
 * to enter user data for login. Also contains bean methods to validate form fields
 * and action event fired in response to the Login button being pressed. 
 * 
 * @author Kevin Roast
 */
public class LoginBean
{
   // ------------------------------------------------------------------------------
   // Managed bean properties 

   /**
    * @param authenticationService  The AuthenticationService to set.
    */
   public void setAuthenticationService(AuthenticationService authenticationService)
   {
      this.authenticationService = authenticationService;
   }

   /**
    * @param nodeService The nodeService to set.
    */
   public void setNodeService(NodeService nodeService)
   {
      this.nodeService = nodeService;
   }
   
   /**
    * @param browseBean The BrowseBean to set.
    */
   public void setBrowseBean(BrowseBean browseBean)
   {
      this.browseBean = browseBean;
   }
   
   public void setUsername(String val)
   {
      this.username = val;
   }
   
   public String getUsername()
   {
      return this.username;
   }
   
   public void setPassword(String val)
   {
      this.password = val;
   }
   
   public String getPassword()
   {
      return this.password;
   }
   
   
   // ------------------------------------------------------------------------------
   // Validator methods 
   
   /**
    * Validate password field data is acceptable 
    */
   public void validatePassword(FacesContext context, UIComponent component, Object value)
      throws ValidatorException
   {
      String pass = (String)value;
      if (pass.length() < 5 || pass.length() > 12)
      {
         String err = Application.getMessage(context, MSG_PASSWORD_LENGTH);
         throw new ValidatorException(new FacesMessage(err));
      }
      
      for (int i=0; i<pass.length(); i++)
      {
         if (Character.isLetterOrDigit( pass.charAt(i) ) == false)
         {
            String err = Application.getMessage(context, MSG_PASSWORD_CHARS);
            throw new ValidatorException(new FacesMessage(err));
         }
      }
   }
   
   /**
    * Validate Username field data is acceptable 
    */
   public void validateUsername(FacesContext context, UIComponent component, Object value)
      throws ValidatorException
   {
      String pass = (String)value;
      if (pass.length() < 5 || pass.length() > 12)
      {
         String err = Application.getMessage(context, MSG_USERNAME_LENGTH);
         throw new ValidatorException(new FacesMessage(err));
      }
      
      for (int i=0; i<pass.length(); i++)
      {
         if (Character.isLetterOrDigit( pass.charAt(i) ) == false)
         {
            String err = Application.getMessage(context, MSG_USERNAME_CHARS);
            throw new ValidatorException(new FacesMessage(err));
         }
      }
   }
   
   
   // ------------------------------------------------------------------------------
   // Action event methods
   
   /**
    * Login action handler
    * 
    * @return outcome view name
    */
   public String login()
   {
      String outcome = null;
      
      FacesContext fc = FacesContext.getCurrentInstance();
      
      if (this.username != null && this.password != null)
      {
         // Authenticate via the authentication service, then save the details of user in an object
         // in the session - this is used by the servlet filter etc. on each page to check for login
         try
         {
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(this.username, this.password);
            Authentication auth = this.authenticationService.authenticate(Repository.getStoreRef(), token);
            RepositoryUserDetails principal = (RepositoryUserDetails)auth.getPrincipal();
            
            // setup User object and Home space ID
            User user = new User(principal.getUserNodeRef(), this.username, this.authenticationService.getCurrentTicket(), principal.getPersonNodeRef());
            String homeSpaceId = (String)this.nodeService.getProperty(principal.getPersonNodeRef(), ContentModel.PROP_HOMEFOLDER);
            NodeRef homeSpaceRef = new NodeRef(Repository.getStoreRef(), homeSpaceId);
            // check that the home space node exists - else user cannot login
            if (this.nodeService.exists(homeSpaceRef) == false)
            {
               throw new InvalidNodeRefException(homeSpaceRef);
            }
            user.setHomeSpaceId(homeSpaceId);
            
            // put the User object in the Session - the authentication servlet will then allow
            // the app to continue without redirecting to the login page
            Map session = fc.getExternalContext().getSessionMap();
            session.put(AuthenticationFilter.AUTHENTICATION_USER, user);
            
            // if an external outcome has been provided then use that, else use default
            String externalOutcome = (String)fc.getExternalContext().getSessionMap().get(LOGIN_OUTCOME_KEY);
            if (externalOutcome != null)
            {
               // TODO: This is a quick solution. It would be better to specify the (identifier?) of a handler
               //       class that would be responsible for processing specific outcome arguments.
               
               // setup is required for certain outcome requests
               if (OUTCOME_DOCDETAILS.equals(externalOutcome))
               {
                  String[] args = (String[])fc.getExternalContext().getSessionMap().get(LOGIN_OUTCOME_ARGS);
                  if (args.length == 3)
                  {
                     StoreRef storeRef = new StoreRef(args[0], args[1]);
                     NodeRef nodeRef = new NodeRef(storeRef, args[2]);
                     // setup the Document on the browse bean
                     // TODO: the browse bean should accept a full NodeRef - not just an ID
                     this.browseBean.setupContentAction(nodeRef.getId(), true);
                  }
               }
               else if (OUTCOME_SPACEDETAILS.equals(externalOutcome))
               {
                  String[] args = (String[])fc.getExternalContext().getSessionMap().get(LOGIN_OUTCOME_ARGS);
                  if (args.length == 3)
                  {
                     StoreRef storeRef = new StoreRef(args[0], args[1]);
                     NodeRef nodeRef = new NodeRef(storeRef, args[2]);
                     // setup the Space on the browse bean
                     // TODO: the browse bean should accept a full NodeRef - not just an ID
                     this.browseBean.setupSpaceAction(nodeRef.getId(), true);
                  }
               }
               
               fc.getExternalContext().getSessionMap().remove(LOGIN_OUTCOME_KEY);
               return externalOutcome;
            }
            else
            {
               return "success";
            }
         }
         catch (AuthenticationException aerr)
         {
            Utils.addErrorMessage(Application.getMessage(fc, MSG_ERROR_UNKNOWN_USER));
         }
         catch (InvalidNodeRefException refErr)
         {
            Utils.addErrorMessage(MessageFormat.format(Application.getMessage(
                  fc, Repository.ERROR_NOHOME), refErr.getNodeRef().getId()));
         }
      }
      else
      {
         Utils.addErrorMessage(Application.getMessage(fc, MSG_ERROR_MISSING));
      }
      
      return outcome;
   }
   
   /**
    * Invalidate ticket and logout user
    */
   public String logout()
   {
      Map session = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
      User user = (User)session.get(AuthenticationFilter.AUTHENTICATION_USER);
      if (user != null)
      {
         this.authenticationService.invalidate(user.getTicket());
      }
      FacesContext.getCurrentInstance().getExternalContext().getSessionMap().clear();
      
      return "logout";
   }
   
   
   // ------------------------------------------------------------------------------
   // Private data
   
   /** I18N messages */
   private static final String MSG_ERROR_MISSING = "error_login_missing";
   private static final String MSG_ERROR_UNKNOWN_USER = "error_login_user";
   private static final String MSG_USERNAME_CHARS = "login_err_username_chars";
   private static final String MSG_USERNAME_LENGTH = "login_err_username_length";
   private static final String MSG_PASSWORD_CHARS = "login_err_password_chars";
   private static final String MSG_PASSWORD_LENGTH = "login_err_password_length";
   
   public static final String LOGIN_OUTCOME_KEY  = "_alfOutcome";
   public static final String LOGIN_OUTCOME_ARGS = "_alfOutcomeArgs";
   
   private final static String OUTCOME_DOCDETAILS = "showDocDetails";
   private final static String OUTCOME_SPACEDETAILS = "showSpaceDetails";
   
   /** user name */
   private String username = null;
   
   /** password */
   private String password = null;
   
   /** AuthenticationService bean reference */
   private AuthenticationService authenticationService;
   
   /** NodeService bean reference */
   private NodeService nodeService;
   
   /** The BrowseBean reference */
   private BrowseBean browseBean;
}
