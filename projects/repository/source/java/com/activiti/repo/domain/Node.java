package com.activiti.repo.domain;

import java.util.Map;
import java.util.Set;

/**
 * @author derekh
 */
public interface Node {
    public Long getId();

    public void setId(Long id);

    public Set getParentAssocs();

    public void setParentAssocs(Set parentAssocs);

    public Map getProperties();

    public void setProperties(Map properties);
}
