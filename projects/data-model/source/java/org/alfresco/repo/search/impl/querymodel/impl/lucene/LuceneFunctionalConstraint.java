package org.alfresco.repo.search.impl.querymodel.impl.lucene;

import java.util.Map;
import java.util.Set;

import org.alfresco.repo.search.impl.querymodel.Argument;
import org.alfresco.repo.search.impl.querymodel.Function;
import org.alfresco.repo.search.impl.querymodel.FunctionEvaluationContext;
import org.alfresco.repo.search.impl.querymodel.impl.BaseFunctionalConstraint;

/**
 * @author andyh
 *
 */
public class LuceneFunctionalConstraint<Q, S, E extends Throwable> extends BaseFunctionalConstraint implements LuceneQueryBuilderComponent<Q, S, E>
{

    /**
     * @param function Function
     * @param arguments Map<String, Argument>
     */
    public LuceneFunctionalConstraint(Function function, Map<String, Argument> arguments)
    {
        super(function, arguments);
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.search.impl.querymodel.impl.lucene.LuceneQueryBuilderComponent#addComponent(org.apache.lucene.search.BooleanQuery, org.apache.lucene.search.BooleanQuery, org.alfresco.service.cmr.dictionary.DictionaryService, java.lang.String)
     */
    public Q addComponent(Set<String> selectors, Map<String, Argument> functionArgs, LuceneQueryBuilderContext<Q, S, E> luceneContext, FunctionEvaluationContext functionContext) throws E
    {
        Function function = getFunction();
        if(function != null)
        {
            if(function instanceof LuceneQueryBuilderComponent)
            {
                @SuppressWarnings("unchecked")
                LuceneQueryBuilderComponent<Q, S, E> luceneQueryBuilderComponent = (LuceneQueryBuilderComponent<Q, S, E>)function;
                return luceneQueryBuilderComponent.addComponent(selectors, getFunctionArguments(), luceneContext, functionContext);            
            }
            else
            {
                throw new UnsupportedOperationException();
            }
        }
        return null;
    }

}
