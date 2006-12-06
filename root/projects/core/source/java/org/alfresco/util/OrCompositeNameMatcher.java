/**
 * 
 */
package org.alfresco.util;

import java.io.Serializable;
import java.util.List;

/**
 * A composite name matcher that matches if any of its member
 * matchers match.
 * @author britt
 */
public class OrCompositeNameMatcher implements NameMatcher, Serializable
{
    private static final long serialVersionUID = 8751285104404230814L;

    /**
     * The NameMatchers this is composed of.
     */
    List<NameMatcher> fMatchers;
    
    /**
     * Default constructor.
     */
    public OrCompositeNameMatcher()
    {
    }

    public void setMatchers(List<NameMatcher> matchers)
    {
        fMatchers = matchers;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.util.NameMatcher#matches(java.lang.String)
     */
    public boolean matches(String name) 
    {
        for (NameMatcher matcher : fMatchers)
        {
            if (matcher.matches(name))
            {
                return true;
            }
        }
        return false;
    }
}
