package org.alfresco.repo.search.impl.querymodel.impl.lucene;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.repo.search.adaptor.lucene.LuceneQueryParserExpressionAdaptor;
import org.alfresco.repo.search.impl.querymodel.Argument;
import org.alfresco.repo.search.impl.querymodel.Constraint;
import org.alfresco.repo.search.impl.querymodel.FunctionEvaluationContext;
import org.alfresco.repo.search.impl.querymodel.impl.BaseConjunction;

/**
 * @author andyh
 */
public class LuceneConjunction<Q, S, E extends Throwable> extends BaseConjunction implements LuceneQueryBuilderComponent<Q, S, E>
{

    /**
     * @param constraints List<Constraint>
     */
    public LuceneConjunction(List<Constraint> constraints)
    {
        super(constraints);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.repo.search.impl.querymodel.impl.lucene.LuceneQueryBuilderComponent#addComponent(java.lang.String,
     *      java.util.Map, org.alfresco.repo.search.impl.querymodel.impl.lucene.LuceneQueryBuilderContext,
     *      org.alfresco.repo.search.impl.querymodel.FunctionEvaluationContext)
     */
    public Q addComponent(Set<String> selectors, Map<String, Argument> functionArgs, LuceneQueryBuilderContext<Q, S, E> luceneContext, FunctionEvaluationContext functionContext)
            throws E
    {
       
        LuceneQueryParserExpressionAdaptor<Q, E> expressionAdaptor = luceneContext.getLuceneQueryParserAdaptor().getExpressionAdaptor();
        boolean must = false;
        boolean must_not = false;
        for (Constraint constraint : getConstraints())
        {
            if (constraint instanceof LuceneQueryBuilderComponent)
            {
                @SuppressWarnings("unchecked")
                LuceneQueryBuilderComponent<Q, S, E> luceneQueryBuilderComponent = (LuceneQueryBuilderComponent<Q, S, E>) constraint;
                Q constraintQuery = luceneQueryBuilderComponent.addComponent(selectors, functionArgs, luceneContext, functionContext);
                
                if (constraintQuery != null)
                {
                    switch (constraint.getOccur())
                    {
                    case DEFAULT:
                    case MANDATORY:
                        expressionAdaptor.addRequired(constraintQuery, constraint.getBoost());
                        must = true;
                        break;
                    case OPTIONAL:
                        expressionAdaptor.addOptional(constraintQuery, constraint.getBoost());
                        break;
                    case EXCLUDE:
                        expressionAdaptor.addExcluded(constraintQuery, constraint.getBoost());
                        must_not = true;
                        break;
                    }
                    
                }
            }
            else
            {
                throw new UnsupportedOperationException();
            }
            if(!must &&  must_not)
            {
                expressionAdaptor.addRequired(luceneContext.getLuceneQueryParserAdaptor().getMatchAllNodesQuery());
            }
        }
        return expressionAdaptor.getQuery();

    }

}
