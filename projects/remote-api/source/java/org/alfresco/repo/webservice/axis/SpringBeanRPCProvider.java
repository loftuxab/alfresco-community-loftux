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
package org.alfresco.repo.webservice.axis;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.handlers.soap.SOAPService;
import org.apache.axis.providers.java.RPCProvider;
import org.apache.axis.transport.http.HTTPConstants;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * A custom Axis RPC Provider that retrieves services via Spring
 * 
 * @author gavinc
 */
public class SpringBeanRPCProvider extends RPCProvider
{
   private static final long serialVersionUID = 2173234269124176995L;
   private static final String OPTION_NAME = "springBean";
   private WebApplicationContext webAppCtx;

   /**
    * Retrieves the class of the bean represented by the given name
    * 
    * @see org.apache.axis.providers.java.JavaProvider#getServiceClass(java.lang.String, org.apache.axis.handlers.soap.SOAPService, org.apache.axis.MessageContext)
    */
   @Override
   protected Class getServiceClass(String beanName, SOAPService service, MessageContext msgCtx) throws AxisFault
   {
      Class clazz = null;
      
      Object bean = getBean(msgCtx, beanName);
      if (bean != null)
      {
         clazz = bean.getClass();
      }
      
      return clazz;
   }

   /**
    * @see org.apache.axis.providers.java.JavaProvider#getServiceClassNameOptionName()
    */
   @Override
   protected String getServiceClassNameOptionName()
   {
      return OPTION_NAME;
   }

   /**
    * Retrieves the bean with the given name from the current spring context
    * 
    * @see org.apache.axis.providers.java.JavaProvider#makeNewServiceObject(org.apache.axis.MessageContext, java.lang.String)
    */
   @Override
   protected Object makeNewServiceObject(MessageContext msgCtx, String beanName) throws Exception
   {
      return getBean(msgCtx, beanName);
   }
   
   /**
    * Retrieves the bean with the given name from the current spring context
    * 
    * @param msgCtx Axis MessageContext
    * @param beanName Name of the bean to lookup
    * @return The instance of the bean
    */
   private Object getBean(MessageContext msgCtx, String beanName) throws AxisFault
   {
      return getWebAppContext(msgCtx).getBean(beanName);
   }
   
   /**
    * Retrieves the Spring context from the web application
    * 
    * @param msgCtx Axis MessageContext
    * @return The Spring web app context
    */
   private WebApplicationContext getWebAppContext(MessageContext msgCtx) throws AxisFault
   {
      if (this.webAppCtx == null && msgCtx != null)
      {
         // get hold of the web application context via the message context
         HttpServletRequest req = (HttpServletRequest)msgCtx.getProperty(HTTPConstants.MC_HTTP_SERVLETREQUEST);
         ServletContext servletCtx = req.getSession().getServletContext();
         this.webAppCtx = WebApplicationContextUtils.getRequiredWebApplicationContext(servletCtx);
      }
      
      if (this.webAppCtx == null)
      {
         throw new AxisFault("Failed to retrieve the Spring web application context");
      }
      
      return this.webAppCtx;
   }
}
