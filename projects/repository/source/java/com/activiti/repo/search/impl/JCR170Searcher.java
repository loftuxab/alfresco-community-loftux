/*
 * Created on Mar 24, 2005
 *
 * TODO Comment this class
 * 
 * 
 */
package com.activiti.repo.search.impl;

import com.activiti.repo.ref.Path;
import com.activiti.repo.ref.StoreRef;
import com.activiti.repo.search.QueryParameter;
import com.activiti.repo.search.ResultSet;
import com.activiti.repo.search.Searcher;

/**
 * Simple searcher against another store using the JSR 170 API
 */
public class JCR170Searcher implements Searcher
{

   public ResultSet query(StoreRef store, String language, String query, Path[] queryOptions, QueryParameter[] queryParameters)
   {
      // TODO Auto-generated method stub
      throw new UnsupportedOperationException();
   }

  

}
