package org.alfresco.repo.search.impl.querymodel;

import java.util.List;

/**
 * @author andyh
 *
 */
public interface Conjunction extends Constraint
{
    /**
     * Get the list of constraints which must all be met
     * @return List<Constraint>
     */
    public List<Constraint> getConstraints();
}
