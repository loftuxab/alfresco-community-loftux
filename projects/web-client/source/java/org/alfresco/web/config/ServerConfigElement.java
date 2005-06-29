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
package org.alfresco.web.config;

import org.alfresco.config.ConfigElement;
import org.alfresco.config.element.ConfigElementAdapter;

/**
 * Custom config element that represents the config data for the server
 * 
 * @author gavinc
 */
public class ServerConfigElement extends ConfigElementAdapter
{
   private String errorPage;
   private String loginPage;
   
   /**
    * Default constructor
    */
   public ServerConfigElement()
   {
      super("server");
   }
   
   /**
    * Constructor
    * 
    * @param name Name of the element this config element represents
    */
   public ServerConfigElement(String name)
   {
      super(name);
   }
   
   public ConfigElement combine(ConfigElement configElement)
   {
      // NOTE: combining these would simply override the values so we just need
      //       to return a new instance of the given config element
      
      ServerConfigElement combined = new ServerConfigElement();
      combined.setErrorPage(((ServerConfigElement)configElement).getErrorPage());
      combined.setLoginPage(((ServerConfigElement)configElement).getLoginPage());
      return combined;
   }
   
   /**
    * @return The error page the application should use
    */
   public String getErrorPage()
   {
      return this.errorPage;
   }

   /**
    * @param errorPage Sets the error page
    */
   public void setErrorPage(String errorPage)
   {
      this.errorPage = errorPage;
   }
   
   /**
    * @return Returns the login Page.
    */
   public String getLoginPage()
   {
      return this.loginPage;
   }
   
   /**
    * @param loginPage The login Page to set.
    */
   public void setLoginPage(String loginPage)
   {
      this.loginPage = loginPage;
   }
}
