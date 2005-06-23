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
import org.alfresco.config.ConfigException;
import org.alfresco.config.xml.elementreader.ConfigElementReader;
import org.dom4j.Element;

/**
 * Custom element reader to parse config for server details
 * 
 * @author gavinc
 */
public class ServerElementReader implements ConfigElementReader
{
   public static final String ELEMENT_SERVER = "server";
   public static final String ELEMENT_MODE = "mode";
   public static final String ELEMENT_ERROR_PAGE = "error-page";
   public static final String ELEMENT_LOGIN_PAGE = "login-page";
   
   /**
    * @see org.alfresco.config.xml.elementreader.ConfigElementReader#parse(org.dom4j.Element)
    */
   public ConfigElement parse(Element element)
   {
      ServerConfigElement configElement = null;
      
      if (element != null)
      {
         String name = element.getName();
         if (name.equals(ELEMENT_SERVER) == false)
         {
            throw new ConfigException("ServerElementReader can only parse " +
                  ELEMENT_SERVER + "elements, " + "the element passed was '" + 
                  name + "'");
         }
         
         configElement = new ServerConfigElement();
         
         // get the server mode
         Element mode = element.element(ELEMENT_MODE);
         if (mode != null)
         {
            configElement.setMode(mode.getTextTrim());
         }
         
         // get the error page
         Element errorPage = element.element(ELEMENT_ERROR_PAGE);
         if (errorPage != null)
         {
            configElement.setErrorPage(errorPage.getTextTrim());
         }
         
         // get the login page
         Element loginPage = element.element(ELEMENT_LOGIN_PAGE);
         if (loginPage != null)
         {
            configElement.setLoginPage(loginPage.getTextTrim());
         }
      }
      
      return configElement;
   }
}
