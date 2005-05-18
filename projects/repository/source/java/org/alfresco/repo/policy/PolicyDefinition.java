package org.alfresco.repo.policy;

import org.alfresco.repo.ref.QName;


/**
 * Definition of a Policy
 * 
 * @author David Caruana
 *
 * @param <P>  the policy interface
 */
public interface PolicyDefinition<P extends Policy>
{
    /**
     * Gets the name of the Policy
     * 
     * @return  policy name
     */
    public QName getName();
    
    
    /**
     * Gets the Policy interface class
     * 
     * @return  the class
     */
    public Class<P> getPolicyInterface();

    
    /**
     * Gets the Policy type
     * @return  the policy type
     */
    public PolicyType getType();
}
