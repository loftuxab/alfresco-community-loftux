package org.alfresco.repo.ref.qname;

import org.alfresco.repo.ref.QName;

/**
 * Provides pattern matching against {@link org.alfresco.repo.ref.QName qnames}.
 * <p>
 * Implementations will use different mechanisms to match against the
 * {@link org.alfresco.repo.ref.QName#getNamespaceURI() namespace} and
 * {@link org.alfresco.repo.ref.QName#getLocalName()() localname}.
 * 
 * @see org.alfresco.repo.ref.QName
 * 
 * @author Derek Hulley
 */
public interface QNamePattern
{
    /**
     * Checks if the given qualified name matches the pattern represented
     * by this instance
     * 
     * @param qname the instance to check
     * @return Returns true if the qname matches this pattern
     */
    public boolean isMatch(QName qname);
}
