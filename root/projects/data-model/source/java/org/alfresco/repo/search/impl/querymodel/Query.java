package org.alfresco.repo.search.impl.querymodel;

import java.util.List;

/**
 * @author andyh
 *
 */
public interface Query
{
    /**
     * Get the columns to return from the query
     * 
     * This may not be null and must contain at least one entry.
     * "*"  "A.*" etc column specifications are not supported.
     * These should have been previously expanded between any query parse and building the query model. 
     * 
     * @return List<Column>
     */
    public List<Column> getColumns();
    
    /**
     * Get the constraints for the query.
     * This is as defined - with no hoisting etc.
     * Hoisting is the problem of the implementation layer.
     * 
     * May be null for unconstrained.
     * 
     * @return Constraint
     */
    public Constraint getConstraint();
    
    /**
     * Get any orderings (may be an empty list or null)
     *  
     * @return List<Ordering>
     */
    public List<Ordering> getOrderings();
    
    /**
     * Get the source for the query
     * Must not be null.
     * @return Source
     */
    public Source getSource();
}
