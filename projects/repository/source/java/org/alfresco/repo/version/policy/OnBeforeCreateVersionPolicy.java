/**
 * Created on Apr 27, 2005
 */
package org.alfresco.repo.version.policy;

import org.alfresco.repo.ref.NodeRef;

/**
 * @author Roy Wetherall
 */
public interface OnBeforeCreateVersionPolicy
{
    public void OnBeforeCreateVersion(NodeRef versionableNode);

}
