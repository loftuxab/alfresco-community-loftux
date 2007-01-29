/**
 * 
 */
package org.alfresco.util;

import java.io.Serializable;

/**
 * Utility class for containing two things that aren't like each other
 * @author britt
 */
public final class Pair<F, S> implements Serializable
{
    private static final long serialVersionUID = -7406248421185630612L;
    
    /**
     * The first member of the pair.
     */
    private final F fFirst;
    
    /**
     * The second member of the pair.
     */
    private final S fSecond;
    
    /**
     * Make a new one.
     * 
     * @param first The first member.
     * @param second The second member.
     */
    public Pair(F first, S second)
    {
        fFirst = first;
        fSecond = second;
    }
    
    /**
     * Get the first member of the tuple.
     * @return The first member.
     */
    public F getFirst()
    {
        return fFirst;
    }
    
    /**
     * Get the second member of the tuple.
     * @return The second member.
     */
    public S getSecond()
    {
        return fSecond;
    }
    
    /**
     * Override of equals.
     * @param other The thing to compare to.
     * @return equality.
     */
    @Override
    public boolean equals(Object other)
    {
        if (this == other)
        {
            return true;
        }
        if (!(other instanceof Pair))
        {
            return false;
        }
        Pair o = (Pair)other;
        return fFirst.equals(o.getFirst()) && fSecond.equals(o.getSecond());
    }
    
    /**
     * Override of hashCode.
     */
    @Override
    public int hashCode()
    {
        return fFirst.hashCode() + fSecond.hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return "(" + fFirst + ", " + fSecond + ")";
    }
}
