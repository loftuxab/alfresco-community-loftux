/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
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
      String username = this.getUsername();
      if (username == null)
      {
         username = "Guest";
      }
      
      logger.info(username + " has logged in at " + new Date());
      
      return outcome;
   }

   @Override
   public String logout()
   {
      String outcome = super.logout();
      
      // log to the console who logged out and when
      String username = this.getUsername();
      if (username == null)
      {
         username = "Guest";
      }
      
      logger.info(username + " logged out at " + new Date());
      
      return outcome;
   }
}
