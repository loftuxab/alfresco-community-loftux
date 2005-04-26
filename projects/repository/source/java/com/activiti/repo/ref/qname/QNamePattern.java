package com.activiti.repo.ref.qname;

import com.activiti.repo.ref.QName;

/**
 * Provides pattern matching against {@link com.activiti.repo.ref.QName qnames}.
 * <p>
 * Implementations will use different mechanisms to match against the
 * {@link com.activiti.repo.ref.QName#getNamespaceURI() namespace} and
 * {@link com.activiti.repo.ref.QName#getLocalName()() localname}.
 * 
 * @see com.activiti.repo.ref.QName
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
