package org.alfresco.repo.dictionary;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract Property Constraint.
 * 
 * @author Derek Hulley
 */
public class M2Constraint
{
    private String name;
    private String ref;
    private String type;
    private String title;
    private String description;
    private List<M2NamedValue> parameters = new ArrayList<M2NamedValue>(2);

    /*package*/ M2Constraint()
    {
    }
    
    @Override
    public String toString()
    {
        return this.name;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getRef()
    {
        return ref;
    }
    
    public void setRef(String refName)
    {
        this.ref = refName;
    }

    public String getType()
    {
        return type;
    }
    
    public void setType(String type)
    {
        this.type = type;
    }
    
    public String getTitle()
    {
        return title;
    }
    
    public void setTitle(String title)
    {
        this.title = title;
    }
    
    public String getDescription()
    {
        return description;
    }
    
    public void setDescription(String description)
    {
        this.description = description;
    }
    
    public List<M2NamedValue> getParameters()
    {
        return parameters;
    }
    
    public M2NamedValue createParameter(String name, String simpleValue)
    {
        M2NamedValue param = new M2NamedValue();
        param.setName(name);
        param.setSimpleValue(simpleValue);
        parameters.add(param);
        return param;
    }
    
    public M2NamedValue createParameter(String name, List<String> listValue)
    {
        M2NamedValue param = new M2NamedValue();
        param.setName(name);
        param.setListValue(listValue);
        parameters.add(param);
        return param;
    }
    
    public void removeParameter(String name)
    {
        List<M2NamedValue> params = new ArrayList<M2NamedValue>(getParameters());
        for (M2NamedValue param : params)
        {
            if (param.getName().equals(name))
            {
                parameters.remove(param);
            }
        }
    }
}
