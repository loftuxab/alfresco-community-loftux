package org.alfresco.repo.policy;

import org.alfresco.repo.ref.QName;

public interface PolicyDefinition<P extends Policy>
{
    public QName getName();
    
    public Class<P> getPolicyInterface();

    public PolicyType getType();

}
