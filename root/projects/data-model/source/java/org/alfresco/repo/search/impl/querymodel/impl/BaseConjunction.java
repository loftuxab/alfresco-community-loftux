package org.alfresco.repo.search.impl.querymodel.impl;

import java.util.List;

import org.alfresco.repo.search.impl.querymodel.Conjunction;
import org.alfresco.repo.search.impl.querymodel.Constraint;

/**
 * @author andyh
 *
 */
public class BaseConjunction extends BaseConstraint implements Conjunction
{

    private List<Constraint> constraints;

    public BaseConjunction(List<Constraint> constraints)
    {
        this.constraints = constraints;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.querymodel.Conjunction#getConstraints()
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
        builder.append("BaseConjunction[");
        builder.append("constraints=").append(getConstraints());
        builder.append("]");
        return builder.toString();
    }
}
