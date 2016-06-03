package org.alfresco.repo.search.impl.querymodel.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.repo.search.impl.querymodel.FunctionEvaluationContext;
import org.alfresco.repo.search.impl.querymodel.Selector;
import org.alfresco.service.namespace.QName;

/**
 * @author andyh
 */
public class BaseSelector implements Selector
{
    private QName type;

    private String alias;

    public BaseSelector(QName type, String alias)
    {
        this.type = type;
        this.alias = alias;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.repo.search.impl.querymodel.Selector#getAlias()
     */
    public String getAlias()
    {
        return alias;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.repo.search.impl.querymodel.Selector#getType()
     */
    public QName getType()
    {
        return type;
    }

    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("BaseSelector[");
        builder.append("alias=").append(getAlias()).append(", ");
        builder.append("type=").append(getType());
        builder.append("]");
        return builder.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.repo.search.impl.querymodel.Source#getSelectorNames()
     */
    public Map<String, Selector> getSelectors()
    {
        HashMap<String, Selector> answer = new HashMap<String, Selector>();
        answer.put(getAlias(), this);
        return answer;
    }

    public Selector getSelector(String name)
    {
        if (getAlias().equals(name))
        {
            return this;
        }
        else
        {
            return null;
        }
    }

    public List<Set<String>> getSelectorGroups(FunctionEvaluationContext functionContext)
    {
        HashSet<String> set = new HashSet<String>();
        set.add(getAlias());
        List<Set<String>> answer = new ArrayList<Set<String>>();
        answer.add(set);
        return answer;
    }
}
