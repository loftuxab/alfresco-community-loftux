package org.alfresco.enterprise.repo.officeservices.metadata;

import org.alfresco.service.cmr.repository.NodeRef;

public interface ContentPostProcessor
{

    public void execute(NodeRef nodeRef);

}
