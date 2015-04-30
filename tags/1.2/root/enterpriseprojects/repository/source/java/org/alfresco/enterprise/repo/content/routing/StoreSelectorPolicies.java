/*
 * Copyright 2005-2010 Alfresco Software, Ltd.  All rights reserved.
 *
 * License rights for this program may be obtained from Alfresco Software, Ltd. 
 * pursuant to a written agreement and any use of this program without such an 
 * agreement is prohibited. 
 */
package org.alfresco.enterprise.repo.content.routing;

import java.io.Serializable;
import java.util.Map;

import org.alfresco.repo.policy.ClassPolicy;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;

/**
 * Policies that will be triggered by StoreSelectorAspectContentStore when
 * content is moved from one store to another
 * 
 * @author Philippe Dubois
 * 
 */
public interface StoreSelectorPolicies
{
    /**
     * @deprecated          Use {@link AfterMoveContentPolicy#QNAME}
     */
    public static final QName ON_CONTENT_MOVED = QName.createQName(NamespaceService.ALFRESCO_URI, "afterMoveContent");

    /**
     * @deprecated          Use {@link AfterMoveContentPolicy}; the method signatures stay the same
     */
    public interface OnContentMovedPolicy extends ClassPolicy
    {
        /**
         * Called after content has been moved.
         */
        public void afterMoveContent(NodeRef ref, Map<QName, Serializable> before, Map<QName, Serializable> after);
    }

    public interface AfterMoveContentPolicy extends ClassPolicy
    {
        public static final QName QNAME = QName.createQName(NamespaceService.ALFRESCO_URI, "afterMoveContent");
        /**
         * Called after content has been moved by the store selector.
         */
        public void afterMoveContent(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after);
    }
}
