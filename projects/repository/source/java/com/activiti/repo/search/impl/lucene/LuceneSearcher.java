/*
 * Created on Mar 24, 2005
 */
package com.activiti.repo.search.impl.lucene;

import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.Query;

import com.activiti.repo.ref.Path;
import com.activiti.repo.ref.StoreRef;
import com.activiti.repo.search.QueryParameter;
import com.activiti.repo.search.ResultSet;
import com.activiti.repo.search.Searcher;
import com.activiti.repo.search.SearcherException;

/**
 * The Lucene implementation of Searcher
 * At the moment we support only lucene based queries.
 * 
 * TODO: Support for other query languages
 * @author andyh
 *
 */
public class LuceneSearcher extends LuceneBase implements Searcher
{  
   /**
    * Lucence languages key = temporary implementation
    */
   private static final String LUCENE = "lucene";
   
   /**
    * Default field name 
    */
   private static final String DEFAULT_FIELD = "FTS";

   /*
    * Searcher implementation 
    */
   
   public ResultSet query(StoreRef store, String language, String queryString, Path[] queryOptions,
         QueryParameter[] queryParameters) throws SearcherException
   {
      if (language.equalsIgnoreCase(LUCENE))
      {
         try
         {
            Query query = QueryParser.parse(queryString, DEFAULT_FIELD, new StandardAnalyzer());
            Hits hits = getSearcher().search(query);
            return new LuceneResultSet(store, hits);
         }
         catch (ParseException e)
         {
            throw new SearcherException("Failed to parse query: " + queryString, e);
         }
         catch (IOException e)
         {
            throw new SearcherException("IO exception during search", e);
         }
      }
      else
      {
         throw new SearcherException("Unknown query language: " + language);
      }
   }

   /**
    * Get an intialised searcher for the store and transaction
    * Normally we do not search againsta a store and delta.
    * Currently only gets the searcher against the main index.
    * 
    * @param storeRef
    * @param deltaId
    * @return
    */
   public static LuceneSearcher getSearcher(StoreRef storeRef, String deltaId)
   {
      LuceneSearcher searcher = new LuceneSearcher();
      try
      {
         searcher.initialise(storeRef, deltaId);
      }
      catch (IOException e)
      {
        throw new SearcherException(e);
      }
      return searcher;
   }
   
   /**
    * Get an intialised searcher for the store.
    * No transactional ammendsmends are searched.
    *
    * 
    * @param storeRef
    * @return
    */
   public static LuceneSearcher getSearcher(StoreRef storeRef)
   {
      return getSearcher(storeRef, null);
   }
}
