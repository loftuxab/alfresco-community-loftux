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
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.validator.ValidatorException;

import org.alfresco.config.ConfigService;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationComponent;
import org.alfresco.repo.security.authentication.AuthenticationException;
import org.alfresco.service.cmr.repository.InvalidNodeRefException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.web.app.Application;
import org.alfresco.web.app.servlet.AuthenticationFilter;
import org.alfresco.web.app.servlet.AuthenticationHelper;
import org.alfresco.web.bean.repository.Repository;
import org.alfresco.web.bean.repository.User;
import org.alfresco.web.config.ClientConfigElement;
import org.alfresco.web.ui.common.Utils;

/**
 * JSF Managed Bean. Backs the "login.jsp" view to provide the form fields used
 * to enter user data for login. Also contains bean methods to validate form
 * fields and action event fired in response to the Login button being pressed.
 * 
 * @author Kevin Roast
 */
public class LoginBean
{
   // ------------------------------------------------------------------------------
   // Managed bean properties

   /**
    * @param authenticationService      The AuthenticationService to set.
    */
   public void setAuthenticationService(AuthenticationService authenticationService)
   {
      this.authenticationService = authenticationService;
   }

   /**
    * @param authenticationComponent    The AuthenticationComponent to set.
    */
   public void setAuthenticationComponent(AuthenticationComponent authenticationComponent)
   {
      this.authenticationComponent = authenticationComponent;
   }

   /**
    * @param nodeService                The nodeService to set.
    */
   public void setNodeService(NodeService nodeService)
   {
      this.nodeService = nodeService;
   }

   /**
    * @param browseBean                 The BrowseBean to set.
    */
   public void setBrowseBean(BrowseBean browseBean)
   {
      this.browseBean = browseBean;
   }

   /**
    * @param configService              The ConfigService to set.
    */
   public void setConfigService(ConfigService configService)
   {
      this.configService = configService;
   }

   /**
    * @param val        Username from login dialog
    */
   public void setUsername(String val)
   {
      this.username = val;
   }

   /**
    * @return The username string from login dialog
    */
   public String getUsername()
   {
      return this.username;
   }

   /**
    * @param val         Password from login dialog
    */
   public void setPassword(String val)
   {
      this.password = val;
   }

   /**
    * @return The password string from login dialog
    */
   public String getPassword()
   {
      return this.password;
   }

   /**
    * @return the available languages
    */
   public SelectItem[] getLanguages()
   {
      ClientConfigElement config = (ClientConfigElement) this.configService.getGlobalConfig()
            .getConfigElement(ClientConfigElement.CONFIG_ELEMENT_ID);
      
      List<String> languages = config.getLanguages();
      SelectItem[] items = new SelectItem[languages.size()];
      int count = 0;
      for (String locale : languages)
      {
         // get label associated to the locale
         String label = config.getLabelForLanguage(locale);

         // set default selection
         if (count == 0 && this.language == null)
         {
            // first try to get the language that the current user is using
            Locale lastLocale = Application.getLanguage(FacesContext.getCurrentInstance());
            if (lastLocale != null)
            {
               this.language = lastLocale.toString();
            }
            // else we default to the first item in the list
            else
            {
               this.language = locale;
            }
         }
         
         items[count++] = new SelectItem(locale, label);
      }
      
      return items;
   }

   /**
    * @return Returns the language selection.
    */
   public String getLanguage()
   {
      return this.language;
   }

   /**
    * @param language       The language selection to set.
    */
   public void setLanguage(String language)
   {
      this.language = language;
      Application.setLanguage(FacesContext.getCurrentInstance(), this.language);
   }


   // ------------------------------------------------------------------------------
   // Validator methods

   /**
    * Validate password field data is acceptable
    */
   public void validatePassword(FacesContext context, UIComponent component, Object value)
         throws ValidatorException
   {
      String pass = (String) value;
      if (pass.length() < 5 || pass.length() > 12)
      {
         String err = Application.getMessage(context, MSG_PASSWORD_LENGTH);
         throw new ValidatorException(new FacesMessage(err));
      }

      for (int i = 0; i < pass.length(); i++)
      {
         if (Character.isLetterOrDigit(pass.charAt(i)) == false)
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
      String pass = (String) value;
      if (pass.length() < 5 || pass.length() > 12)
      {
         String err = Application.getMessage(context, MSG_USERNAME_LENGTH);
         throw new ValidatorException(new FacesMessage(err));
      }

      for (int i = 0; i < pass.length(); i++)
      {
         if (Character.isLetterOrDigit(pass.charAt(i)) == false)
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
            this.authenticationService.authenticate(this.username, this.password.toCharArray());
            
            // setup User object and Home space ID
            User user = new User(this.username, this.authenticationService.getCurrentTicket(),
                  authenticationComponent.getPerson(Repository.getStoreRef(), this.username));
            String homeSpaceId = (String) this.nodeService.getProperty(authenticationComponent.getPerson(
                  Repository.getStoreRef(), this.username), ContentModel.PROP_HOMEFOLDER);
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
            session.put(AuthenticationHelper.AUTHENTICATION_USER, user);
            
            // if an external outcome has been provided then use that, else use default
            String externalOutcome = (String) fc.getExternalContext().getSessionMap().get(LOGIN_OUTCOME_KEY);
            if (externalOutcome != null)
            {
               // TODO: This is a quick solution. It would be better to specify the (identifier?)
               // of a handler class that would be responsible for processing specific outcome arguments.

               // setup is required for certain outcome requests
               if (OUTCOME_DOCDETAILS.equals(externalOutcome))
               {
                  String[] args = (String[]) fc.getExternalContext().getSessionMap().get(LOGIN_OUTCOME_ARGS);
                  if (args.length == 3)
                  {
                     StoreRef storeRef = new StoreRef(args[0], args[1]);
                     NodeRef nodeRef = new NodeRef(storeRef, args[2]);
                     // setup the Document on the browse bean
                     // TODO: the browse bean should accept a full
                     // NodeRef - not just an ID
                     this.browseBean.setupContentAction(nodeRef.getId(), true);
                  }
               }
               else if (OUTCOME_SPACEDETAILS.equals(externalOutcome))
               {
                  String[] args = (String[]) fc.getExternalContext().getSessionMap().get(LOGIN_OUTCOME_ARGS);
                  if (args.length == 3)
                  {
                     StoreRef storeRef = new StoreRef(args[0], args[1]);
                     NodeRef nodeRef = new NodeRef(storeRef, args[2]);
                     // setup the Space on the browse bean
                     // TODO: the browse bean should accept a full
                     // NodeRef - not just an ID
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
            Utils.addErrorMessage(MessageFormat.format(Application.getMessage(fc,
                  Repository.ERROR_NOHOME), refErr.getNodeRef().getId()));
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
      FacesContext context = FacesContext.getCurrentInstance();

      // invalidate User ticket
      Map session = context.getExternalContext().getSessionMap();
      User user = (User) session.get(AuthenticationHelper.AUTHENTICATION_USER);
      if (user != null)
      {
         this.authenticationService.invalidateTicket(user.getTicket());
      }

      // clear Session for this user
      context.getExternalContext().getSessionMap().clear();

      // set language to last used
      if (this.language != null && this.language.length() != 0)
      {
         Application.setLanguage(context, this.language);
      }

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

   public static final String LOGIN_OUTCOME_KEY = "_alfOutcome";
   public static final String LOGIN_OUTCOME_ARGS = "_alfOutcomeArgs";

   private final static String OUTCOME_DOCDETAILS = "showDocDetails";
   private final static String OUTCOME_SPACEDETAILS = "showSpaceDetails";

   /** user name */
   private String username = null;

   /** password */
   private String password = null;

   /** language locale selection */
   private String language = null;

   /** AuthenticationComponent bean reference */
   private AuthenticationComponent authenticationComponent;
   
   /** AuthenticationService bean reference */
   private AuthenticationService authenticationService;

   /** NodeService bean reference */
   private NodeService nodeService;

   /** The BrowseBean reference */
   private BrowseBean browseBean;

   /** ConfigService bean reference */
   private ConfigService configService;
}
