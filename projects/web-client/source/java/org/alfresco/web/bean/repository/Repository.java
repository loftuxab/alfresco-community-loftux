package org.alfresco.web.bean.repository;

import org.alfresco.repo.ref.StoreRef;

/**
 * Helper class for accessing the repository
 * 
 * @author gavinc
 */
public final class Repository
{
   public static final String REPOSITORY_STORE = "SpacesStore";
   public static final String NODE_SERVICE = "indexingNodeService";
   public static final String CONTENT_SERVICE = "contentService";
   public static final String SEARCH_SERVICE = "searcherComponent";
   public static final String DICTIONARY_SERVICE = "dictionaryService";
   public static final String DATA_DICTIONARY = "dataDictionary";
   public static final String USER_TRANSACTION = "userTransaction";
   
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
