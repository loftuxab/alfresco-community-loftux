package org.alfresco.config;

import java.util.List;

/**
 * Definition of a config section.
 * 
 * @author gavinc
 */
public interface ConfigSection
{
    /**
     * Returns the name of an evaluator to use to determine whether the config
     * section this object represents is applicable to the current lookup
     * operation
     * 
     * @return Name of an evaluator held by the config service
     */
    public String getEvaluator();

    /**
     * Returns the condition to use to determine whether the config section this
     * object represents is applicable. The condition is passed to the evaluator
     * 
     * @return Condition to test
     */
    public String getCondition();

    /**
     * Returns the config elements that make up this config section
     * 
     * @return List of config elements
     */
    public List<ConfigElement> getConfigElements();

    /**
     * Determines whether this config section is global
     * 
     * @return true if it is global, false otherwise
     */
    public boolean isGlobal();
}
