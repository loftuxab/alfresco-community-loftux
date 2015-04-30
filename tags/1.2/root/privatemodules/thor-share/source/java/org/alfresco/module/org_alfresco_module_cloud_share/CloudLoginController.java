/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.module.org_alfresco_module_cloud_share;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.surf.FrameworkUtil;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.UserFactory;
import org.springframework.extensions.surf.mvc.LoginController;
import org.springframework.extensions.surf.support.AlfrescoUserFactory;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.surf.util.StringBuilderWriter;
import org.springframework.extensions.surf.util.URLEncoder;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.connector.Connector;
import org.springframework.extensions.webscripts.connector.ConnectorContext;
import org.springframework.extensions.webscripts.connector.HttpMethod;
import org.springframework.extensions.webscripts.connector.Response;
import org.springframework.extensions.webscripts.json.JSONWriter;
import org.springframework.web.servlet.ModelAndView;

import org.alfresco.web.site.servlet.SlingshotLoginController;

/**
 * <p>Extend LoginController to retrieve the cookie "alfLocale" and set its value as the user preference "locale"</p>
 * 
 * @author Jamie Allison
 * @author Alex Miller
 */
public class CloudLoginController extends SlingshotLoginController
{
    private static Log logger = LogFactory.getLog(CloudLoginController.class);

    protected void storeLocale(HttpServletRequest request) throws Exception
    {
        String userId = (String) request.getSession().getAttribute(UserFactory.SESSION_ATTRIBUTE_KEY_USER_ID);

        String locale = null;
        //Check if alfLanguage has been set.  If it has use it to set the locale.
        Cookie[] cookies = request.getCookies();
        if (cookies != null)
        {
            for(Cookie c : cookies)
            {
                if(c.getName().equals("alfLocale"))
                {
                    // get language and convert to java locale format
                    locale = c.getValue().replace('-', '_');

                    break;
                }
            }
        }

        StringBuilderWriter buf = new StringBuilderWriter(64);
        JSONWriter writer = new JSONWriter(buf);

        writer.startObject();
        writer.writeValue("locale", locale);
        writer.endObject();

        Connector conn = FrameworkUtil.getConnector(request.getSession(), userId, AlfrescoUserFactory.ALFRESCO_ENDPOINT_ID);
        ConnectorContext c = new ConnectorContext(HttpMethod.POST);
        c.setContentType("application/json");
        Response res = conn.call("/api/people/" + URLEncoder.encode(userId) + "/preferences", c,
            new ByteArrayInputStream(buf.toString().getBytes()));
        if (Status.STATUS_OK != res.getStatus().getCode())
        {
            logger.info("Unable to save language preference for user " + userId);
            logger.info(res.getStatus().getMessage());
        }
    }

    @Override
    protected void onSuccess(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        // Save the locale:
        storeLocale(request);

        super.onSuccess(request, response);
    }
    
    
}
