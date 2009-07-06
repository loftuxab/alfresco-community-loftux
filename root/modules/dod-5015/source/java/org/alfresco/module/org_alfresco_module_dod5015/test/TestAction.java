package org.alfresco.module.org_alfresco_module_dod5015.test;

import org.alfresco.module.org_alfresco_module_dod5015.action.RMActionExecuterAbstractBase;
import org.alfresco.service.cmr.action.Action;
import org.alfresco.service.cmr.repository.NodeRef;

public class TestAction extends RMActionExecuterAbstractBase
{
    public static final String NAME = "testAction";
    public static final String PARAM = "testActionParam";
    public static final String PARAM_VALUE = "value";
    
    @Override
    protected void executeImpl(Action action, NodeRef actionedUponNodeRef)
    {
        if (action.getParameterValue(PARAM).equals(PARAM_VALUE) == false)
        {
            throw new RuntimeException("Unexpected parameter value.  Expected " + PARAM_VALUE + " actual " + action.getParameterValue(PARAM));
        }
        this.nodeService.addAspect(actionedUponNodeRef, ASPECT_RECORD, null);
    }      
    
    @Override
    public boolean isDispositionAction()
    {
        return true;
    }
}
