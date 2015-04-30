/*
 * Copyright (C) 2005-2011 Alfresco Software Limited.
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

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.springframework.extensions.surf.mvc.LocaleResolver;
import org.springframework.extensions.surf.util.I18NUtil;


public class CloudLocaleResolver extends LocaleResolver
{
    protected List<Locale> locales = new LinkedList<Locale>();

    public void setLanguages(List<String> languages)
    {
        // Turn them into locales to make them easier to manage below
        for (String l : languages)
        {
            this.locales.add(I18NUtil.parseLocale(l));
        }
    }

    @Override
    public Locale resolveLocale(HttpServletRequest request)
    {
        Locale locale = null;
        //Check if alfLocale has been set.  If it has use it to set the locale.
        Cookie[] cookies = request.getCookies();
        
        if(cookies != null)
        {
            for(Cookie c : cookies)
            {
                if(c.getName().equals("alfLocale"))
                {
                    // get language and convert to java locale format
                    String language = c.getValue().replace('-', '_');
                    locale = I18NUtil.parseLocale(language);
                    
                    break;
                }
            }
        }
        
        // If the alfLocale cookie has not been set use standard Share/Surf locale resolver
        // which will look in browsers Accept header for the locale
        if(locale == null)
        {
            locale = super.resolveLocale(request);
        }
        
        if (locale != null)
        {
            // Check that the locale provided by the browser is valid for cloud
            boolean valid = false;
            for (Locale l : locales)
            {
                if (l.getLanguage().equals(locale.getLanguage()))
                {
                    valid = true;
                }
            }
            if (!valid)
            {
                // Locale was NOT allowed, set it to null so the configured default will be used below
                locale = null;
            }
        }

        // If no locale was found (or it wasn't allowed) use default locale (first locale specified in the config)
        if (locale == null && locales.size() > 0)
        {
            locale = locales.get(0);
        }

        // set locale onto Alfresco thread local
        I18NUtil.setLocale(locale);

        // Return locale to Spring MVC
        return locale;
    }
}