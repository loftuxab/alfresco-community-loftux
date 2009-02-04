package org.alfresco.web.config;

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

    @Override
    public boolean equals(Object otherObj)
    {
        if (otherObj == null
                || !otherObj.getClass().equals(this.getClass()))
        {
            return false;
        }
        FormSet otherSet = (FormSet) otherObj;
        boolean component1 = setId == null ? otherSet.setId == null : this.setId.equals(otherSet.setId);
        boolean component2 = parentId == null ? otherSet.parentId == null : this.parentId.equals(otherSet.parentId);
        boolean component3 = appearance == null ? otherSet.appearance == null : this.appearance.equals(otherSet.appearance);
        return component1 && component2 && component3;
    }
    
    @Override
    public int hashCode()
    {
        int component1 = setId == null ? 0 : setId.hashCode();
        int component2 = parentId == null ? 0 : parentId.hashCode();
        int component3 = appearance == null ? 0 : appearance.hashCode();
        return component1 + 7 * component2 + 13 * component3;
    }
}