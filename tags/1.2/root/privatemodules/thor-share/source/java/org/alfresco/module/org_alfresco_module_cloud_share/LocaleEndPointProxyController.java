/**
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This file is part of the Spring Surf Extension project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.alfresco.module.org_alfresco_module_cloud_share;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.extensions.webscripts.servlet.mvc.EndPointProxyController;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;

/**
 * A locale specific implementation of endpoint proxy controller where requests take on accept-language based
 * on the locale of the calling thread.
 */
public class LocaleEndPointProxyController extends EndPointProxyController
{
    private LocaleResolver localeResolver;
    
    public void setLocaleResolver(LocaleResolver service)
    {
        localeResolver = service;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.mvc.AbstractController#createModelAndView(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ModelAndView handleRequestInternal(HttpServletRequest req, HttpServletResponse res) throws Exception
    {
        Locale locale = localeResolver.resolveLocale(req);
        if (locale != null)
        {
            req = new LocaleHttpServletRequest(req, locale);
        }
        return super.handleRequestInternal(req, res);
    }
}