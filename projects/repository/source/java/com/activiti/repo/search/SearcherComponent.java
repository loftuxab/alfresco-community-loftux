/*
 * Created on Mar 24, 2005
 *
 * TODO Comment this class
 * 
 * 
 */
package com.activiti.repo.search;

import com.activiti.repo.ref.Path;
import com.activiti.repo.ref.StoreRef;

/**
 * Component API for searching.
 * 
 * Transactional support is free
 * 
 * @see Searcher
 * 
 * TODO: Support for Spring and IOC. Avoid the singleton pattern.
 * 
 * @author andyh
 *
 */
public class SearcherComponent implements Searcher
{
   /*
    * Searcher implementation
    */

   public ResultSet query(StoreRef store, String language, String query, Path[] queryOptions, QueryParameter[] queryParameters)
   {
      Searcher searcher = IndexerAndSearcherFactory.getInstance().getSearcher(store, false );
      return searcher.query(store, language, query, queryOptions, queryParameters);
   }

 

}
