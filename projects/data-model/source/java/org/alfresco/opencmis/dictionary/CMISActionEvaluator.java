package org.alfresco.opencmis.dictionary;

import org.apache.chemistry.opencmis.commons.enums.Action;

public interface CMISActionEvaluator
{
    /**
     * Gets the CMIS Allowed Action
     * 
     * @return Action
     */
    public Action getAction();

    /**
     * Determines if an action is allowed on an object
     * 
     * @param nodeInfo CMISNodeInfo
     * @return boolean
     */
    public boolean isAllowed(CMISNodeInfo nodeInfo);
}
