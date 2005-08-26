/*
 * Copyright (C) 2005 Alfresco, Inc.
 *
 * Licensed under the Mozilla Public License version 1.1 
 * with a permitted attribution clause. You may obtain a
 * copy of the License at
 *
 *   http://www.alfresco.org/legal/license.txt
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
import java.text.MessageFormat;
import java.util.StringTokenizer;

import javax.faces.context.FacesContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.web.bean.LoginBean;
import org.alfresco.web.bean.repository.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Servlet allowing external URL access to various global JSF views in the Web Client.
 * <p>
 * The servlet accepts a well formed URL that can easily be generated from a Content or Space NodeRef.
 * The URL also specifies the JSF "outcome" to be executed which provides the correct JSF View to be
 * displayed. The JSF "outcome" must equate to a global navigation rule or it will not be displayed.
 * Servlet URL is of the form:
 * <p>
 * <pre>http://<server>/alfresco/navigate/<outcome>[/<workspace>/<store>/<nodeId>]</pre>
 * 
 * @author Kevin Roast
 */
public class ExternalAccessServlet extends HttpServlet
{
   private static final long serialVersionUID = -4118907921337237802L;
   
   private static Log logger = LogFactory.getLog(ExternalAccessServlet.class);
   
   
   /**
    * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
    */
   protected void service(HttpServletRequest req, HttpServletResponse res)
      throws ServletException, IOException
   {
      // The URL contains multiple parts
      // /alfresco/navigate/<outcome>
      // the protocol, followed by the store, followed by the Id
      // the last part is only used for mimetype and browser use
      String uri = req.getRequestURI();
      
      if (logger.isDebugEnabled())
         logger.debug("Processing URL: " + uri);
      
      StringTokenizer t = new StringTokenizer(uri, "/");
      int count = t.countTokens();
      if (count < 3)
      {
         throw new IllegalArgumentException("Externally addressable URL did not contain all required args: " + uri); 
      }
      t.nextToken();    // skip web app name
      t.nextToken();    // skip servlet name
      
      String outcome = t.nextToken();
      
      // get rest of the tokens
      String[] tokens = new String[count - 3];
      for (int i=0; i<count - 3; i++)
      {
         tokens[i] = t.nextToken();
      }
      
      // clear the User object from the Session - this will force a relogin
      // we do this so the outcome from the login page can then be changed
      req.getSession().removeAttribute(AuthenticationFilter.AUTHENTICATION_USER);
      
      if (logger.isDebugEnabled())
        logger.debug("Removing User session - will redirect via login page...");
      
      // set the session variable so the login bean knows which outcome to use
      req.getSession().setAttribute(LoginBean.LOGIN_OUTCOME_KEY, outcome);
      
      // set the args if any
      req.getSession().setAttribute(LoginBean.LOGIN_OUTCOME_ARGS, tokens);
      
      // redirect to any faces URL will force the login page to appear via the Authentication Filter
      res.sendRedirect(req.getContextPath() + "/faces");
   }
   
   /*
   public final static String generateExternalURL(NodeRef ref, String name)
   {
      return MessageFormat.format(EXTERNAL_URL, new Object[] {
            // TODO: get context path here?
            ref.getStoreRef().getProtocol(),
            ref.getStoreRef().getIdentifier(),
            ref.getId(),
            name} );
   }
   
   // example: http://<server>/alfresco/navigate/<outcome>[/<workspace>/<store>/<nodeId>]
   private static final String EXTERNAL_URL  = "http://{0}:{1}/{2}/navigate/{3}/{4}/{5}/{6}";
   */
}
