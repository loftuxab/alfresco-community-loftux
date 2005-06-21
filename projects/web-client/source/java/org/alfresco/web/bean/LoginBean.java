/*
 * Created on Feb 24, 2005
 */
package org.alfresco.web.bean;

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
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.web.app.servlet.AuthenticationFilter;
import org.alfresco.web.bean.repository.Repository;
import org.alfresco.web.bean.repository.User;
import org.alfresco.web.ui.common.Utils;

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
      if (this.username != null && this.password != null)
      {
         // Authenticate via the authentication service, then save the details of user in an object
         // in the session - this is used by the servlet filter etc. on each page to check for login
         try
         {
            UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(this.username, this.password);
            Authentication auth = this.authenticationService.authenticate(Repository.getStoreRef(FacesContext.getCurrentInstance()), token);
            RepositoryUserDetails principal = (RepositoryUserDetails)auth.getPrincipal();
            
            // setup User object and Home space ID etc.
            User user = new User(this.username, this.authenticationService.getCurrentTicket(), principal.getPersonNodeRef());
            String homeSpaceId = (String)this.nodeService.getProperty(principal.getPersonNodeRef(), ContentModel.PROP_HOMEFOLDER);
            user.setHomeSpaceId(homeSpaceId);
            
            Map session = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
            session.put(AuthenticationFilter.AUTHENTICATION_USER, user);
            
            return "success";
         }
         catch (AuthenticationException aerr)
         {
            Utils.addErrorMessage("Unable to login - unknown username/password.");
            return null;
         }
      }
      else
      {
         Utils.addErrorMessage("Must specify username and password.");
         return null;
      }
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
}
