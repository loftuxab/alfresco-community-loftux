package org.alfresco.repo.search.impl.querymodel;

import java.util.Map;

/**
 * @author andyh
 */
public interface FunctionInvokation
{
    /**
     * Get the function
     * 
     * @return Function
     */
    public Function getFunction();

    /**
     * Get the functions arguments.
     * 
     * @return Map
     */
    public Map<String, Argument> getFunctionArguments();
}
