package org.alfresco.repo.search.impl.querymodel;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author andyh
 *
 */
public interface Source
{
    public Map<String, Selector> getSelectors();
    
    public Selector getSelector(String name);
    
    public List<Set<String>> getSelectorGroups(FunctionEvaluationContext functionContext);
}
