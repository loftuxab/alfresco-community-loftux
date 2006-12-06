/**
 * 
 */
package org.alfresco.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


/**
 * A name matcher that matches any of a list of regular expressions.
 * @author britt
 */
public class RegexNameMatcher implements NameMatcher, Serializable
{
    private static final long serialVersionUID = 2686220370729761489L;

    /**
     * The regular expressions that can match.
     */
    private List<Pattern> fPatterns;

    /**
     * Default constructor.
     */
    public RegexNameMatcher()
    {
        fPatterns = new ArrayList<Pattern>();
    }
    
    /**
     * Set the patterns.  
     * @param patterns
     */
    public void setPatterns(List<String> patterns)
    {
        for (String regex : patterns)
        {
            fPatterns.add(Pattern.compile(regex));
        }
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.util.NameMatcher#matches(java.lang.String)
     */
    public boolean matches(String name) 
    {
        for (Pattern pattern : fPatterns)
        {
            if (pattern.matcher(name).matches())
            {
                return true;
            }
        }
        return false;
    }
}
