package org.alfresco.module.org_alfresco_module_cloud_share;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/*package*/ class LocaleHttpServletRequest extends HttpServletRequestWrapper
{
    private static String ACCEPT_LANGUAGE = "accept-language";
    private String acceptLanguage;
    
    public LocaleHttpServletRequest(HttpServletRequest request, Locale locale)
    {
        super(request);
        this.acceptLanguage = locale.getCountry() + "-" + locale.getLanguage();
    }

    @Override
    public String getHeader(String name)
    {
        if (ACCEPT_LANGUAGE.equalsIgnoreCase(name))
        {
            return acceptLanguage;
        }
        return super.getHeader(name);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Enumeration<String> getHeaderNames()
    {
        List<String> headerNames = new ArrayList<String>();
        for (Enumeration<String> e = super.getHeaderNames(); e.hasMoreElements() ;)
        {
            String name = e.nextElement();
            headerNames.add(name);
        }
        headerNames.add(ACCEPT_LANGUAGE);
        return Collections.enumeration(headerNames);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Enumeration<String> getHeaders(String name)
    {
        if (ACCEPT_LANGUAGE.equalsIgnoreCase(name))
        {
            List<String> values = new ArrayList<String>();
            values.add(acceptLanguage);
            return Collections.enumeration(values);
        }
        return super.getHeaders(name);
    }

    @Override
    public long getDateHeader(String name)
    {
        return super.getDateHeader(name);
    }

    @Override
    public int getIntHeader(String name)
    {
        return super.getIntHeader(name);
    }
}