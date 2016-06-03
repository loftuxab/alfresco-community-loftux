package org.alfresco.repo.dictionary;

/**
 * A dictionary listener that allows listeners to listen for models being added to
 * the dictionary. Used by the OpenCMIS dictionary to refresh its registry when new models
 * are added to the core dictionary.
 * 
 * @author sglover
 *
 */
public interface ExtendedDictionaryListener extends DictionaryListener
{
    void modelAdded(CompiledModel model, String tenant);
}
