package org.alfresco.repo.search.impl.querymodel.impl;

/**
 * @author Andy
 *
 */
public class SimpleConstraint extends BaseConstraint
{

    public SimpleConstraint(Occur occur)
    {
        super();
        setOccur(occur);
    }
    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.querymodel.Constraint#evaluate()
     */
    @Override
    public boolean evaluate()
    {
        throw new UnsupportedOperationException();
    }

}
