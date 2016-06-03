package org.alfresco.repo.search.impl.querymodel;

import java.util.List;

/**
 * @author andyh
 *
 */
public interface Disjunction extends Constraint
{
    /**
     * Get the list of constraints for which at least one must be met.
     * 
     * @return List<Constraint>
     */
    public List<Constraint> getConstraints();
}
