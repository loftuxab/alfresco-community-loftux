package org.alfresco.repo.search.impl.querymodel.impl.lucene;

import java.io.Serializable;

import org.alfresco.repo.search.impl.querymodel.impl.BaseLiteralArgument;
import org.alfresco.service.namespace.QName;

/**
 * @author andyh
 *
 */
public class LuceneLiteralArgument extends BaseLiteralArgument
{

    /**
     * @param name String
     * @param type QName
     * @param value Serializable
     */
    public LuceneLiteralArgument(String name, QName type, Serializable value)
    {
        super(name, type, value);
    }

}
