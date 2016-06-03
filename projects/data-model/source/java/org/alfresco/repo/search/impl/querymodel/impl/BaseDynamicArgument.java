package org.alfresco.repo.search.impl.querymodel.impl;

import org.alfresco.repo.search.impl.querymodel.DynamicArgument;

/**
 * @author andyh
 *
 */
public abstract class BaseDynamicArgument extends BaseArgument implements DynamicArgument
{

    /**
     * @param name String
     * @param queryable boolean
     * @param orderable boolean
     */
    public BaseDynamicArgument(String name, boolean queryable, boolean orderable)
    {
        super(name, queryable, orderable);
    }

}
