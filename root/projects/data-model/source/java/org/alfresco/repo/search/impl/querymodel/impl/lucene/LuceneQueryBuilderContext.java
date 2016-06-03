package org.alfresco.repo.search.impl.querymodel.impl.lucene;

import org.alfresco.repo.search.adaptor.lucene.LuceneQueryParserAdaptor;
import org.alfresco.service.namespace.NamespacePrefixResolver;

/**
 * @author Andy
 *
 */
public interface LuceneQueryBuilderContext<Q, S, E extends Throwable>
{

    /**
     * @return - the parser
     */
    public abstract LuceneQueryParserAdaptor<Q, S, E> getLuceneQueryParserAdaptor();

    /**
     * @return - the namespace prefix resolver
     */
    public abstract NamespacePrefixResolver getNamespacePrefixResolver();

}