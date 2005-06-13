package org.alfresco.service.namespace;


/**
 * Provides pattern matching against {@link org.alfresco.service.namespace.QName qnames}.
 * <p>
 * Implementations will use different mechanisms to match against the
 * {@link org.alfresco.service.namespace.QName#getNamespaceURI() namespace} and
 * {@link org.alfresco.repo.ref.QName#getLocalName()() localname}.
 * 
 * @see org.alfresco.service.namespace.QName
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
