package org.alfresco.tools;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

public class FakeHttpSession implements HttpSession
{
    public FakeHttpSession()
    {
        super();
        creationTime = System.currentTimeMillis();
    }

    public long getCreationTime()
    {
        return creationTime;
    }

    public String getId()
    {
        return "alfresco";
    }

    public long getLastAccessedTime()
    {
        return creationTime;
    }

    public ServletContext getServletContext()
    {
        return null;
    }

    public void setMaxInactiveInterval(int maxInactiveInterval)
    {
        this.maxInactiveInterval = maxInactiveInterval;
    }

    public int getMaxInactiveInterval()
    {
        return maxInactiveInterval;
    }

    public javax.servlet.http.HttpSessionContext getSessionContext()
    {
        return null;
    }

    public Object getAttribute(String name)
    {
        return attributes.get(name);
    }

    public Object getValue(String name)
    {
        return attributes.get(name);
    }

    public Enumeration getAttributeNames()
    {
        return Collections.enumeration(attributes.keySet());
    }

    public String[] getValueNames()
    {
        return (String[]) attributes.keySet().toArray(
                new String[attributes.keySet().size()]);
    }

    public void setAttribute(String name, Object value)
    {
        attributes.put(name, value);
    }

    public void putValue(String name, Object value)
    {
        attributes.put(name, value);
    }

    public void removeAttribute(String name)
    {
        attributes.remove(name);
    }

    public void removeValue(String name)
    {
        attributes.remove(name);
    }

    public void invalidate()
    {
    }

    public boolean isNew()
    {
        return true;
    }

    private Map attributes = new HashMap();
    private long creationTime;
    private int maxInactiveInterval = 30 * 60 * 1000;
}
