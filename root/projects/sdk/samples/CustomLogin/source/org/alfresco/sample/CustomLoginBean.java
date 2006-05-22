package org.alfresco.sample;

import java.util.Date;

import org.alfresco.web.bean.LoginBean;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CustomLoginBean extends LoginBean
{
   private static final Log logger = LogFactory.getLog(CustomLoginBean.class);

   @Override
   public String login()
   {
      String outcome = super.login();
      
      // log to the console who logged in and when
      logger.info(this.getUsername() + " has logged in at " + new Date());
      
      return outcome;
   }

   @Override
   public String logout()
   {
      String outcome = super.logout();
      
      // log to the console who logged out and when
      logger.info(this.getUsername() + " logged out at " + new Date());
      
      return outcome;
   }
}
