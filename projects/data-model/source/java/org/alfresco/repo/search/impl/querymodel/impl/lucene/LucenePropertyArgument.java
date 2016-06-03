package org.alfresco.repo.search.impl.querymodel.impl.lucene;

import org.alfresco.repo.search.impl.querymodel.impl.BasePropertyArgument;

/**
 * @author andyh
 *
 */
public class LucenePropertyArgument extends BasePropertyArgument
{

    /**
     * @param name String
     * @param queryable boolean
     * @param orderable boolean
     * @param selector String
     * @param propertyName String
     */
    public LucenePropertyArgument(String name, boolean queryable, boolean orderable, String selector, String propertyName)
    {
        super(name, queryable, orderable, selector, propertyName);
    }

}
