/**
 * Created on Apr 27, 2005
 */
package com.activiti.repo.version.policy;

import com.activiti.repo.ref.NodeRef;

/**
 * @author Roy Wetherall
 */
public interface OnBeforeCreateVersionPolicy
{
    public void OnBeforeCreateVersion(NodeRef versionableNode);

}
