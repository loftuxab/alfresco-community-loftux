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
package org.alfresco.web.app.servlet;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.repo.security.authentication.AuthenticationService;
import org.alfresco.repo.security.authentication.StoreContextHolder;
import org.alfresco.web.app.Application;
import org.alfresco.web.bean.repository.Repository;
import org.alfresco.web.bean.repository.User;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/**
 * @author Kevin Roast
 * 
 * Servlet filter responsible for redirecting to the login page for the web-client if the user
 * does not have a valid ticket.
 * <p>
 * The current ticker is validated for each page request and the login page is shown if the
 * ticker has expired.
 * <p>
 * Note that this filter is only active when the system is running in a servlet container -
 * the AlfrescoFacesPortlet will be used for a JSR-168 Portal environment.
 */
public class AuthenticationFilter implements Filter
{
   public final static String AUTHENTICATION_USER = "_alfAuthTicket";
   
   /**
    * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
    */
   public void init(FilterConfig config) throws ServletException
   {
      this.context = config.getServletContext();
   }

   /**
    * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
    */
   public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
         throws IOException, ServletException
   {
      HttpServletRequest httpRequest = (HttpServletRequest)req;
      // allow the login page to proceed
      if (httpRequest.getRequestURI().endsWith(getLoginPage()) == false)
      {
         // examine the session for our User object
         User user = (User)httpRequest.getSession().getAttribute(AUTHENTICATION_USER);
         if (user == null)
         {
            // no user/ticket - redirect to login page
            HttpServletResponse httpResponse = (HttpServletResponse)res; 
            httpResponse.sendRedirect(httpRequest.getContextPath() + "/faces" + getLoginPage());
         }
         else
         {
            // setup the authentication context
            WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(this.context);
            AuthenticationService auth = (AuthenticationService)ctx.getBean("authenticationService");
            StoreContextHolder.setContext(Repository.getStoreRef());
            auth.validate(user.getTicket());
            
            // continue filter chaining
            chain.doFilter(req, res);
         }
      }
      else
      {
         // continue filter chaining
         chain.doFilter(req, res);
      }
   }

   /**
    * @see javax.servlet.Filter#destroy()
    */
   public void destroy()
   {
      // nothing to do
   }
   
   private String getLoginPage()
   {
      if (this.loginPage == null)
      {
         this.loginPage = Application.getLoginPage(this.context);
      }
      
      return this.loginPage;
   }
   
   
   private String loginPage = null;
   private ServletContext context;
}
