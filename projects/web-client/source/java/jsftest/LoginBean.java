/*
 * Created on Feb 24, 2005
 */
package jsftest;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;

/**
 * JSF Managed Bean. Backs the login.jsp view to provide the form fields used
 * to enter user data for login. Also contains bean methods to validate form fields
 * and action event fired in response to the Login button being pressed. 
 * 
 * @author kevinr
 */
public class LoginBean
{
   // ===========================================================================
   // Managed bean properties 
   
   public void setName(String val)
   {
      m_name = val;
   }
   
   public String getName()
   {
      return m_name;
   }
   
   public void setPassword(String val)
   {
      m_password = val;
   }
   
   public String getPassword()
   {
      return m_password;
   }
   
   
   // ===========================================================================
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
   
   
   // ===========================================================================
   // Action event methods
   
   /**
    * Login action handler
    * 
    * @return outcome view name
    */
   public String login()
   {
      if (m_name.equals("admin") && m_password.equals("admin"))
      {
         return "success";
      }
      else
      {
         addErrorMessage("Unknown username/password.");
         return null;
      }
   }
   
   
   // ===========================================================================
   // Private helpers 
   
   /**
    * Add an ERROR level message that can be displayed by the 'messages' tag
    * 
    * @param msg
    */
   private void addErrorMessage(String msg)
   {
      FacesContext context = FacesContext.getCurrentInstance( );
      
      FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg);
      context.addMessage(null, facesMsg);
   }
   
   
   // ===========================================================================
   // Private data
   
   private String m_name = null;
   private String m_password = null;
}
