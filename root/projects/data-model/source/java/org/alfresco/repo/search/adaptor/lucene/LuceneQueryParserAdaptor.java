/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.repo.search.adaptor.lucene;

import java.util.List;

import org.alfresco.repo.search.impl.querymodel.FunctionEvaluationContext;
import org.alfresco.repo.search.impl.querymodel.Ordering;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.search.SearchParameters;

/**
 * Adaptor class principally to wrap lucene parser implementations and encapsulate changes between lucene versions
 * of query building.
 * 
 * @param <Q> the query type used by the query engine implementation
 * @param <S> the sort type used by the query engine implementation
 * @param <E> the exception it throws 
 * 
 * @author Andy
 *
 */
public interface LuceneQueryParserAdaptor<Q, S, E extends Throwable>
{

    /**
     * @param field String
     * @param queryText String
     * @param analysisMode AnalysisMode
     * @param luceneFunction LuceneFunction
     * @return
     */
    Q getFieldQuery(String field, String queryText, AnalysisMode analysisMode, LuceneFunction luceneFunction) throws E;

    /**
     * @param field String
     * @param lower String
     * @param upper String
     * @param includeLower boolean
     * @param includeUpper boolean
     * @param analysisMode AnalysisMode
     * @param luceneFunction LuceneFunction
     * @return
     */
    Q getRangeQuery(String field, String lower, String upper, boolean includeLower, boolean includeUpper, AnalysisMode analysisMode, LuceneFunction luceneFunction) throws E;

    
    /**
     * A query that matches all docs
     * 
     * @return
     * @throws E
     */
    Q getMatchAllQuery() throws E;
    
    /**
     * A query that matches no docs.
     * 
     * @return
     * @throws E
     */
    Q getMatchNoneQuery() throws E;

    /**
     * @param field String
     * @param sqlLikeClause String
     * @param analysisMode AnalysisMode
     * @return
     */
    Q getLikeQuery(String field, String sqlLikeClause, AnalysisMode analysisMode) throws E;

    /**
     * @return
     */
    SearchParameters getSearchParameters();

    /**
     * @param field String
     * @return
     */
    String getSortField(String field) throws E;

    /**
     * Wrap generating a potentially complex id + version query
     * 
     * @param field String
     * @param stringValue String
     * @param analysisMode AnalysisMode
     * @param luceneFunction LuceneFunction
     * @return
     */
    Q getIdentifierQuery(String field, String stringValue, AnalysisMode analysisMode, LuceneFunction luceneFunction) throws E;

    /**
     * Wrap generating a potentially complex id + version query
     * 
     * @param field String
     * @param stringValue String
     * @param analysisMode AnalysisMode
     * @return
     */
    Q getIdentifieLikeQuery(String field, String stringValue, AnalysisMode analysisMode) throws E;

    /**
     * @param noLocalField String
     * @return
     */
    boolean sortFieldExists(String noLocalField);

    /**
     * @param field String
     * @param value String
     * @return
     * @throws E
     */
    Q getFieldQuery(String field, String value) throws E;

    /**
     * @param list List
     * @param functionContext FunctionEvaluationContext
     * @return
     * @throws E 
     */
    S buildSort(List<Ordering> list, FunctionEvaluationContext functionContext) throws E;

    /**
     * @param luceneFieldName String
     * @param term String
     * @param minSimilarity Float
     * @return
     * @throws E
     */
    Q getFuzzyQuery(String luceneFieldName, String term, Float minSimilarity) throws E;

    /**
     * Get the default field
     * 
     * @return
     */
    String getField();

    /**
     * Get the default phrase slop
     * 
     * @return
     */
    int getPhraseSlop();

    /**
     * @param luceneFieldName String
     * @param term String
     * @param analysisMode AnalysisMode
     * @param slop Integer
     * @param luceneFunction LuceneFunction
     * @return
     */
    Q getFieldQuery(String luceneFieldName, String term, AnalysisMode analysisMode, Integer slop, LuceneFunction luceneFunction) throws E;

    /**
     * @param luceneFieldName String
     * @param term String
     * @param analysisMode AnalysisMode
     * @return
     */
    Q getPrefixQuery(String luceneFieldName, String term, AnalysisMode analysisMode) throws E;

    /**
     * @param luceneFieldName String
     * @param first String
     * @param last String
     * @param slop int
     * @param inOrder boolean
     * @return
     */
    Q getSpanQuery(String luceneFieldName, String first, String last, int slop, boolean inOrder) throws E;

    /**
     * @param luceneFieldName String
     * @param term String
     * @param mode AnalysisMode
     * @return
     */
    Q getWildcardQuery(String luceneFieldName, String term, AnalysisMode mode) throws E;
    
    /**
     * Invert a query - add a mandatory must not match anything query alnogside 
     * 
     * @param query
     * @return
     */
    Q getNegatedQuery(Q query) throws E;
    
    /**
     * Utility to build conjunctions, disjunctions and negation
     * @return
     */
    LuceneQueryParserExpressionAdaptor<Q, E> getExpressionAdaptor();

    /**
     * A query that matches all alfresco nodes (not extra stuff that may be in the underlying index)
     * 
     * @return
     */
    Q getMatchAllNodesQuery();

    /**
     * @param field String
     * @param propertyDef PropertyDefinition
     * @return
     */
    String getDatetimeSortField(String field, PropertyDefinition propertyDef); 
}
