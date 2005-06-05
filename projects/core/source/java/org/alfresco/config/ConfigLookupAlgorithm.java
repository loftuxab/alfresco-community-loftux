package org.alfresco.config;

import org.alfresco.config.evaluator.Evaluator;

/**
 * Interface definition for a config lookup algorithm, this may be last value
 * wins, a merging strategy or based on inheritance.
 * 
 * @author gavinc
 */
public interface ConfigLookupAlgorithm
{
   /**
    * Determines whether the given section applies to the given object, if so
    * the section is added to the results
    * 
    * @param section The config section to test 
    * @param evaluator The evaluator for the section being processed
    * @param object The object which is the subject of the config lookup
    * @param results The Config object holding all the matched sections
    */
   public void process(ConfigSection section, Evaluator evaluator, Object object, Config results);
}
