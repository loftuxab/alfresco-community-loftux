package org.alfresco.repo.search.impl.querymodel.impl;

import org.alfresco.repo.search.impl.querymodel.StaticArgument;

/**
 * @author andyh
 *
 */
public abstract class BaseStaticArgument extends BaseArgument implements StaticArgument
{

    /**
     * @param name String
     * @param queryable boolean
     * @param orderable boolean
     */
    public BaseStaticArgument(String name, boolean queryable, boolean orderable)
    {
        super(name, queryable, orderable);
    }


}
