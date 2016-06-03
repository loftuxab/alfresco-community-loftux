package org.alfresco.repo.search.impl.querymodel.impl;

import java.util.List;

import org.alfresco.repo.search.impl.querymodel.Constraint;
import org.alfresco.repo.search.impl.querymodel.Disjunction;

/**
 * @author andyh
 *
 */
public class BaseDisjunction extends BaseConstraint implements Disjunction
{
    private List<Constraint> constraints;

    public BaseDisjunction(List<Constraint> constraints)
    {
        this.constraints = constraints;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.querymodel.Disjunction#getConstraints()
     */
    public List<Constraint> getConstraints()
    {
        return constraints;
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.querymodel.Constraint#evaluate()
     */
    public boolean evaluate()
    {
        throw new UnsupportedOperationException();
    }

    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("BaseDisjunction[");
        builder.append("constraints=").append(getConstraints());
        builder.append("]");
        return builder.toString();
    }
}
