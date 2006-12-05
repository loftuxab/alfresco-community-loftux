/**
 * 
 */
package org.alfresco.util;

/**
 * Trivial interface for an object that matches names in some way.
 * It's immediate use is for filtering what gets considered for 
 * compare/update purposes in the AVM.
 * @author britt
 */
public interface NameMatcher 
{
    /**
     * Does the given name match, in whatever way the implementation
     * defines.
     * @param name The name to check for matching.
     * @return Whether the named matched.
     */
    public boolean matches(String name);
}
