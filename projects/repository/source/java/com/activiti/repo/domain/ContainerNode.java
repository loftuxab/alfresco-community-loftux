package com.activiti.repo.domain;

import java.util.Set;

/**
 * @author derekh
 */
public interface ContainerNode extends RealNode
{
    public Set getChildAssocs();

    public void setChildAssocs(Set childAssocs);
}
