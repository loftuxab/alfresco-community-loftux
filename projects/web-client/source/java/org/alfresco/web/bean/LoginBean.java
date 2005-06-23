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
import org.alfresco.web.app.Application;
import org.alfresco.web.app.servlet.AuthenticationFilter;
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
    * @return Returns the AuthenticationService.
    */
   public AuthenticationService getAuthenticationService()
   {
      return this.authenticationService;
   }
   
   /**
    * @param authenticationService  The AuthenticationService to set.
    */
   public void setAuthenticationService(AuthenticationService authenticationService)
   {
      this.authenticationService = authenticationService;
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
    * @return Returns the navigation bean instance.
    */
   public NavigationBean getNavigator()
   {
      return this.navigator;
   }
   
   /**
    * @param navigator The NavigationBean to set.
    */
   public void setNavigator(NavigationBean navigator)
   {
      this.navigator = navigator;
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
         String err = "Password must be between 5 and 12 characters in length.";
         throw new ValidatorException(new FacesMessage(err));
      }
      
      for (int i=0; i<pass.length(); i++)
      {
         if (Character.isLetterOrDigit( pass.charAt(i) ) == false)
         {
            String err = "Password can only contain characters or digits.";
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
         String err = "Username must be between 5 and 12 characters in length.";
         throw new ValidatorException(new FacesMessage(err));
      }
      
      for (int i=0; i<pass.length(); i++)
      {
         if (Character.isLetterOrDigit( pass.charAt(i) ) == false)
         {
            String err = "Username can only contain characters or digits.";
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
      
      if (this.username != null && this.password != null)
      {
         // Authenticate via the authentication service, then save the details of user in an object
         // in the session - this is used by the servlet filter etc. on each page to check for login
         FacesContext fc = FacesContext.getCurrentInstance();
         try
         {
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(this.username, this.password);
            Authentication auth = this.authenticationService.authenticate(Repository.getStoreRef(), token);
            RepositoryUserDetails principal = (RepositoryUserDetails)auth.getPrincipal();
            
            // setup User object and Home space ID etc.
            User user = new User(this.username, this.authenticationService.getCurrentTicket(), principal.getPersonNodeRef());
            String homeSpaceId = (String)this.nodeService.getProperty(principal.getPersonNodeRef(), ContentModel.PROP_HOMEFOLDER);
            user.setHomeSpaceId(homeSpaceId);
            
            Map session = fc.getExternalContext().getSessionMap();
            session.put(AuthenticationFilter.AUTHENTICATION_USER, user);
            
            // kick off the breadcrumb path and our root node Id
            NodeRef homeSpaceRef = new NodeRef(Repository.getStoreRef(), homeSpaceId);
            String homeSpaceName = Repository.getNameForNode(this.nodeService, homeSpaceRef);
            
            this.navigator.setCurrentNodeId(homeSpaceId);
            
            List<IBreadcrumbHandler> elements = new ArrayList(1);
            elements.add(this.navigator.new NavigationBreadcrumbHandler(homeSpaceRef, homeSpaceName));
            this.navigator.setLocation(elements);
            
            outcome = "success";
         }
         catch (AuthenticationException aerr)
         {
            Utils.addErrorMessage("Unable to login - unknown username/password.");
         }
         catch (InvalidNodeRefException refErr)
         {
            Utils.addErrorMessage( MessageFormat.format(Repository.ERROR_NOHOME, Application.getCurrentUser(fc).getHomeSpaceId()), refErr );
         }
      }
      else
      {
         Utils.addErrorMessage("Must specify username and password.");
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
         session.remove(AuthenticationFilter.AUTHENTICATION_USER);
      }
      
      return "logout";
   }
   
   
   // ------------------------------------------------------------------------------
   // Private data
   
   /** user name */
   private String username = null;
   
   /** password */
   private String password = null;
   
   /** AuthenticationService bean reference */
   private AuthenticationService authenticationService;
   
   /** NodeService bean reference */
   private NodeService nodeService;
   
   /** The NavigationBean reference */
   private NavigationBean navigator;
}
