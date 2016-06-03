package org.alfresco.opencmis.dictionary;

import java.io.Serializable;
import java.util.Collection;

import org.alfresco.repo.search.adaptor.lucene.LuceneFunction;
import org.alfresco.repo.search.adaptor.lucene.LuceneQueryParserAdaptor;
import org.alfresco.repo.search.impl.querymodel.PredicateMode;


/**
 * Encapsulate the building of lucene queries for property predicates
 */
public interface CMISPropertyLuceneBuilder
{
    /**
     * @param lqpa LuceneQueryParserAdaptor<Q, S, E>
     * @param value Serializable
     * @param mode PredicateMode
     * @param luceneFunction LuceneFunction
     * @return the query - may be null if no query is required
     * @throws E
     */
    public <Q, S, E extends Throwable> Q buildLuceneEquality(LuceneQueryParserAdaptor<Q, S, E> lqpa, Serializable value, PredicateMode mode, LuceneFunction luceneFunction) throws E;

    /**
     * @param lqpa LuceneQueryParserAdaptor<Q, S, E>
     * @param not Boolean
     * @return the query - may be null if no query is required
     * @throws E
     */
    public <Q, S, E extends Throwable> Q buildLuceneExists(LuceneQueryParserAdaptor<Q, S, E> lqpa, Boolean not) throws E;

    /**
     * @param lqpa LuceneQueryParserAdaptor<Q, S, E>
     * @param value Serializable
     * @param mode PredicateMode
     * @param luceneFunction LuceneFunction
     * @return the query - may be null if no query is required
     * @throws E
     */
    public <Q, S, E extends Throwable> Q buildLuceneGreaterThan(LuceneQueryParserAdaptor<Q, S, E> lqpa, Serializable value, PredicateMode mode, LuceneFunction luceneFunction) throws E;

    /**
     * @param lqpa LuceneQueryParserAdaptor<Q, S, E>
     * @param value Serializable
     * @param mode PredicateMode
     * @param luceneFunction LuceneFunction
     * @return the query - may be null if no query is required
     * @throws E
     */
    public <Q, S, E extends Throwable> Q buildLuceneGreaterThanOrEquals(LuceneQueryParserAdaptor<Q, S, E> lqpa, Serializable value, PredicateMode mode, LuceneFunction luceneFunction) throws E;

    /**
     * @param lqpa LuceneQueryParserAdaptor<Q, S, E>
     * @param values Collection<Serializable>
     * @param not Boolean
     * @param mode PredicateMode
     * @return the query - may be null if no query is required
     * @throws E
     */
    public <Q, S, E extends Throwable> Q buildLuceneIn(LuceneQueryParserAdaptor<Q, S, E> lqpa, Collection<Serializable> values, Boolean not, PredicateMode mode) throws E;

    /**
     * @param lqpa LuceneQueryParserAdaptor<Q, S, E>
     * @param value PredicateMode
     * @param mode PredicateMode
     * @param luceneFunction LuceneFunction
     * @return the query - may be null if no query is required
     * @throws E
     */
    public <Q, S, E extends Throwable> Q buildLuceneInequality(LuceneQueryParserAdaptor<Q, S, E> lqpa, Serializable value, PredicateMode mode, LuceneFunction luceneFunction) throws E;

    /**
     * @param lqpa LuceneQueryParserAdaptor<Q, S, E>
     * @param value Serializable
     * @param mode PredicateMode
     * @param luceneFunction LuceneFunction
     * @return the query - may be null if no query is required
     * @throws E
     */
    public <Q, S, E extends Throwable> Q buildLuceneLessThan(LuceneQueryParserAdaptor<Q, S, E> lqpa, Serializable value, PredicateMode mode, LuceneFunction luceneFunction) throws E;

    /**
     * @param lqpa LuceneQueryParserAdaptor<Q, S, E>
     * @param value Serializable
     * @param mode PredicateMode
     * @param luceneFunction LuceneFunction
     * @return the query - may be null if no query is required
     * @throws E
     */
    public <Q, S, E extends Throwable> Q buildLuceneLessThanOrEquals(LuceneQueryParserAdaptor<Q, S, E> lqpa, Serializable value, PredicateMode mode, LuceneFunction luceneFunction) throws E;

    /**
     * @param lqpa LuceneQueryParserAdaptor<Q, S, E>
     * @param value Serializable
     * @param not Boolean
     * @return the query - may be null if no query is required
     * @throws E
     */
    public <Q, S, E extends Throwable> Q buildLuceneLike(LuceneQueryParserAdaptor<Q, S, E> lqpa, Serializable value, Boolean not) throws E;

    /**
     * @param lqpa TODO
     * @return the sort field
     * @throws E 
     */
    public <Q, S, E extends Throwable> String getLuceneSortField(LuceneQueryParserAdaptor<Q, S, E> lqpa) throws E;
    
    /**
     * @return the field name
     * 
     */
    public String getLuceneFieldName();
}
