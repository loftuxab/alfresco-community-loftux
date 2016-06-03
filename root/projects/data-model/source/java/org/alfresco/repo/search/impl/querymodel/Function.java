package org.alfresco.repo.search.impl.querymodel;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.alfresco.service.namespace.QName;

/**
 * @author andyh
 */
public interface Function
{   
    /**
     * Evaluation a function
     * 
     * @param args Map<String, Argument>
     * @param context FunctionEvaluationContext
     * @return Serializable
     */
    public Serializable getValue(Map<String, Argument> args, FunctionEvaluationContext context);

    /**
     * Get the return type for the function
     * 
     * @return QName
     */
    public QName getReturnType();

    /**
     * Get the function name
     * 
     * @return String
     */
    public String getName();
    
    /**
     * Get the argument Definitions
     * @return LinkedHashMap
     */
    public LinkedHashMap<String, ArgumentDefinition> getArgumentDefinitions();
    
    
    /**
     * Get the argument Definition
     * @return ArgumentDefinition
     */
    public ArgumentDefinition getArgumentDefinition(String name);
    

}
