package org.alfresco.repo.search.impl.querymodel.impl.lucene;

import java.util.Map;

import org.alfresco.repo.search.impl.querymodel.Argument;
import org.alfresco.repo.search.impl.querymodel.Function;
import org.alfresco.repo.search.impl.querymodel.impl.BaseColumn;

/**
 * @author andyh
 *
 */
public class LuceneColumn extends BaseColumn
{

    /**
     * @param function Function
     * @param functionArguments Map<String, Argument>
     * @param alias String
     */
    public LuceneColumn(Function function, Map<String, Argument> functionArguments, String alias)
    {
        super(function, functionArguments, alias);
    }

}
