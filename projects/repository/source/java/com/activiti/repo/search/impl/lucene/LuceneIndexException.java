/*
 * Created on Mar 24, 2005
 */
package com.activiti.repo.search.impl.lucene;

import com.activiti.repo.search.IndexerException;

/**
 * Exceptions relating to indexing within the lucene implementation
 * 
 * @author andyh
 *
 */
public class LuceneIndexException extends IndexerException
{

   /**
    * 
    */
   private static final long serialVersionUID = 3688505480817422645L;

   public LuceneIndexException()
   {
      super();
   }

   public LuceneIndexException(String message, Throwable cause)
   {
      super(message, cause);
   }

   public LuceneIndexException(String message)
   {
      super(message);
   }

   public LuceneIndexException(Throwable cause)
   {
      super(cause);
   }

  

}
