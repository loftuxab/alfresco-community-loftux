package org.alfresco.repo.dictionary;

/**
 * Dictionary Listener interface.
 * <p>
 * This interface allows Dictionary Listeners to register with the DictionaryService.
 *
 */
public interface DictionaryListener
{        
    /**
     * Callback for (re-)initialising the Dictionary caches (executed in the current tenant context)
     */
    void onDictionaryInit();

    /**
     * Callback once dictionary destroy is complete (executed in the current tenant context)
     */
    void afterDictionaryDestroy();
    
    /**
     * Callback once dictionary initialisation is complete (executed in the current tenant context)
     */
    void afterDictionaryInit();
}
