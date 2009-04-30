package org.alfresco.web.config.forms;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class represents a &lt;set&gt; element within a &lt;form&gt; element.
 * 
 * @author Neil McErlean.
 */
public class FormSet
{
    private final String setId;
    private final String parentId;
    private final String appearance;
    
    /**
     * This filed will be null for a 'root' set.
     */
    private FormSet parent;
    private Map<String, FormSet> children = new LinkedHashMap<String, FormSet>();
    
    public FormSet(String setId, String parentId, String appearance)
    {
        this.setId = setId;
        this.parentId = parentId;
        this.appearance = appearance;
    }
    public String getSetId()
    {
        return setId;
    }
    public String getParentId()
    {
        return parentId;
    }
    public String getAppearance()
    {
        return appearance;
    }

    public FormSet getParent()
    {
        return this.parent;
    }
    
    public Map<String, FormSet> getChildren()
    {
        return Collections.unmodifiableMap(this.children);
    }
    
    void setParent(FormSet parentObject)
    {
        this.parent = parentObject;
    }
    
    void addChild(FormSet newChild)
    {
        this.children.put(newChild.getSetId(), newChild);
    }
    
    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        result.append("Set id='").append(setId).append("' parentId='")
            .append(parentId).append("' appearance='").append(appearance)
            .append("'");

        return result.toString();
    }
}