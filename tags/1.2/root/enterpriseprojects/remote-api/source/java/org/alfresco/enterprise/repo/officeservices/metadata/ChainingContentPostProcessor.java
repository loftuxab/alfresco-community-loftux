package org.alfresco.enterprise.repo.officeservices.metadata;

import java.util.LinkedList;

import org.alfresco.service.cmr.repository.NodeRef;

public class ChainingContentPostProcessor implements ContentPostProcessor
{

    private LinkedList<ContentPostProcessor> processors;
    
    public ChainingContentPostProcessor()
    {
        processors = new LinkedList<ContentPostProcessor>();
    }
    
    public void add(ContentPostProcessor processor)
    {
        processors.add(processor);
    }
    
    @Override
    public void execute(NodeRef nodeRef)
    {
        for(ContentPostProcessor processor : processors)
        {
            processor.execute(nodeRef);
        }
    }

}
