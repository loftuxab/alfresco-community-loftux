package org.alfresco.repo.policy;

import org.alfresco.repo.dictionary.NamespaceService;

/**
 * Marker interface for representing a Policy.
 * 
 * @author David Caruana
 */
public interface Policy
{
    /**
     * mandatory static field on a <tt>Policy</tt> that can be overridden in
     * derived policies
     */
    static String NAMESPACE = NamespaceService.ALFRESCO_URI;
}
