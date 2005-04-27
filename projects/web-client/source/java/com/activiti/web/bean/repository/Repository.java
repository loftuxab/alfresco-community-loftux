package com.activiti.web.bean.repository;

import com.activiti.repo.ref.StoreRef;

/**
 * Helper class for accessing the repository
 * 
 * @author gavinc
 */
public class Repository
{
   public static final String REPOSITORY_STORE = "SpacesStore";
   public static final String NODE_SERVICE = "indexingNodeService";
   public static final String SEARCH_SERVICE = "searcherComponent";
   public static final String DICTIONARY_SERVICE = "dictionaryService";
   
   /**
    * Returns a store reference object
    * 
    * @return A StoreRef object
    */
   public static StoreRef getStoreRef()
   {
      return new StoreRef(StoreRef.PROTOCOL_WORKSPACE, REPOSITORY_STORE);
   }
}
