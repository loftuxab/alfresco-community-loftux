package org.alfresco.web.evaluator;

import java.io.Serializable;
import java.util.Map;

import org.json.simple.JSONObject;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.webscripts.connector.User;

/**
 * Determines whether the current user is a network admin
 *
 * @author: taksoy
 */
public class IsNetworkAdminEvaluator extends BaseEvaluator
{

   private static final String PROP_KEY_NETWORK_ADMIN = "isNetworkAdmin";

   /**
    * Get the current user associated with this request
    *
    * @return User user
    */
   private final User getUser()
   {
       final RequestContext rc = ThreadLocalRequestContext.getRequestContext();
       final User user = rc.getUser();
       if (user == null)
       {
           throw new RuntimeException("User must exist!");
       }
       return user;
   }

   /**
    * Determine whether the current user is a network admin or not
    *
    * @return true if the current user is a network admin
    */
   private final boolean isNetworkAdmin()
   {
      User user = getUser();
      Map<String, Serializable> properties = user.getProperties();
      for (Map.Entry<String, Serializable> entry : properties.entrySet())
      {
         if (entry.getKey().equals(PROP_KEY_NETWORK_ADMIN))
         {
            return ((Boolean) entry.getValue()).booleanValue();
         }
      }
      return false;
   }

   @Override
   public boolean evaluate(JSONObject jsonObject)
   {
      return isNetworkAdmin();
   }

}
