package org.alfresco.repo.search.impl.querymodel;

/**
 * @author andyh
 *
 */
public interface Join extends Source
{
    /**
     * The source for the left hand side of the join
     * @return Source
     */
    public Source getLeft();
    
    /**
     * The source for the right hand side of the join
     * @return Source
     */
    public Source getRight();
    
    /**
     * Get the join type
     * @return JoinType
     */
    public JoinType getJoinType();
    
    /**
     * Get the join condition.
     * Not all constraints are valid join conditions
     * @return Constraint
     */
    public Constraint getJoinCondition();
}
