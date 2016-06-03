package org.alfresco.repo.dictionary.constraint;

/**
 * A No operation constraint, it does nothing
 * @author Gethin James
 */
public class NoOpConstraint extends AbstractConstraint
{

    @Override
    protected void evaluateSingleValue(Object value)
    {
        //No operation, it does nothing
    }

}
