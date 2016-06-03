package org.alfresco.repo.search.impl.querymodel.impl.lucene;

import java.util.Map;
import java.util.Set;

import org.alfresco.repo.search.adaptor.lucene.LuceneQueryParserAdaptor;
import org.alfresco.repo.search.impl.querymodel.Argument;
import org.alfresco.repo.search.impl.querymodel.FunctionEvaluationContext;
import org.alfresco.repo.search.impl.querymodel.impl.BaseSelector;
import org.alfresco.service.namespace.QName;

/**
 * @author andyh
 *
 */
public class LuceneSelector<Q, S, E extends Throwable> extends BaseSelector implements LuceneQueryBuilderComponent<Q, S, E>
{

    /**
     * @param type QName
     * @param alias String
     */
    public LuceneSelector(QName type, String alias)
    {
        super(type, alias);
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.querymodel.impl.lucene.LuceneQueryBuilderComponent#addComponent(org.apache.lucene.search.BooleanQuery, org.apache.lucene.search.BooleanQuery)
     */
    public Q addComponent(Set<String> selectors, Map<String, Argument> functionArgs, LuceneQueryBuilderContext<Q, S, E> luceneContext, FunctionEvaluationContext functionContext) throws E
    {
        LuceneQueryParserAdaptor<Q, S, E> lqpa = luceneContext.getLuceneQueryParserAdaptor();
        return lqpa.getFieldQuery("CLASS", getType().toString());
        
    }

    
}
