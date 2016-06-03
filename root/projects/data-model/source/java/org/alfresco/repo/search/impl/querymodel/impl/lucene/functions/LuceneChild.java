package org.alfresco.repo.search.impl.querymodel.impl.lucene.functions;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.alfresco.repo.search.adaptor.lucene.LuceneQueryParserAdaptor;
import org.alfresco.repo.search.adaptor.lucene.QueryConstants;
import org.alfresco.repo.search.impl.querymodel.Argument;
import org.alfresco.repo.search.impl.querymodel.FunctionEvaluationContext;
import org.alfresco.repo.search.impl.querymodel.QueryModelException;
import org.alfresco.repo.search.impl.querymodel.impl.functions.Child;
import org.alfresco.repo.search.impl.querymodel.impl.lucene.LuceneQueryBuilderComponent;
import org.alfresco.repo.search.impl.querymodel.impl.lucene.LuceneQueryBuilderContext;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;

/**
 * @author andyh
 *
 */
public class LuceneChild<Q, S, E extends Throwable> extends Child implements LuceneQueryBuilderComponent<Q, S, E>
{

    /**
     * 
     */
    public LuceneChild()
    {
        super();
    }
    
    private StoreRef getStore(LuceneQueryBuilderContext<Q, S, E> luceneContext)
    {
    	ArrayList<StoreRef> stores = luceneContext.getLuceneQueryParserAdaptor().getSearchParameters().getStores();
    	if(stores.size() < 1)
    	{
    		// default
    		return StoreRef.STORE_REF_WORKSPACE_SPACESSTORE;
    	}
    	return stores.get(0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.repo.search.impl.querymodel.impl.lucene.LuceneQueryBuilderComponent#addComponent(org.apache.lucene.search.BooleanQuery,
     *      org.apache.lucene.search.BooleanQuery, org.alfresco.service.cmr.dictionary.DictionaryService,
     *      java.lang.String)
     */
    public Q addComponent(Set<String> selectors, Map<String, Argument> functionArgs, LuceneQueryBuilderContext<Q, S, E> luceneContext, FunctionEvaluationContext functionContext)
            throws E
    {
        LuceneQueryParserAdaptor<Q, S, E> lqpa = luceneContext.getLuceneQueryParserAdaptor();
        Argument argument = functionArgs.get(ARG_PARENT);
        String id = (String) argument.getValue(functionContext);
        argument = functionArgs.get(ARG_SELECTOR);
        if(argument != null)
        {
            String selector = (String) argument.getValue(functionContext);
            if(!selectors.contains(selector))
            {
                throw new QueryModelException("Unkown selector "+selector); 
            }
        }
        else
        {
            if(selectors.size() > 1)
            {
                throw new QueryModelException("Selector must be specified for child constraint (IN_FOLDER) and join"); 
            }
        }

        NodeRef nodeRef;
        if(NodeRef.isNodeRef(id))
        {
            nodeRef= new NodeRef(id);
        }
        else
        {
        	// assume id is the node uuid e.g. for OpenCMIS
            StoreRef storeRef = getStore(luceneContext);
            nodeRef = new NodeRef(storeRef, id);
        }

        Q query = lqpa.getFieldQuery(QueryConstants.FIELD_PARENT, nodeRef.toString());
        return query;
    }
}
