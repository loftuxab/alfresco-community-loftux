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
    * Example of a specific validation method. Could have simply used a validator
    * tag inside the input field tag - this is just an example.
    */
   public void validatePassword(FacesContext context, UIComponent component, Object value)
      throws ValidatorException
   {
      String pass = (String)value;
      if (pass.length() < 5)
      {
         String err = "Password name must be a minimum of 5 characters.";
         throw new ValidatorException(new FacesMessage(err));
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
