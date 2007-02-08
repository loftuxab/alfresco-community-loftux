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
package org.alfresco.webservice.util;

import org.apache.axis.AxisFault;
import org.apache.axis.MessageContext;
import org.apache.axis.handlers.BasicHandler;
import org.apache.axis.transport.http.HTTPConstants;

/**
 * @author Roy Wetherall
 */
public class CookieHandler extends BasicHandler 
{
    private static final long serialVersionUID = 5355053439499560511L;

    public void invoke(MessageContext context) 
        throws AxisFault 
    {
        String sessionId = AuthenticationUtils.getAuthenticationDetails().getSessionId();
        if (sessionId != null)
        {
            context.setProperty(HTTPConstants.HEADER_COOKIE, "JSESSIONID=" + sessionId);
        }
    }
 }
