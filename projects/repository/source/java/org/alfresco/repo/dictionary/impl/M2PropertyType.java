package org.alfresco.repo.dictionary.impl;


/**
 * Property Type Definition
 * 
 * @author David Caruana
 *
 */
public class M2PropertyType
{
    private String name = null;
    private String title = null;
    private String description = null;
    private String analyserClassName = null;
    
    
    /*package*/ M2PropertyType()
    {
        super();
    }
    

    public String getName()
    {
        return name;
    }
    
    
    public void setName(String name)
    {
        this.name = name;
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
    
    
    public String getAnalyserClassName()
    {
        return analyserClassName;
    }
    
    
    public void setAnalyserClassName(String analyserClassName)
    {
        this.analyserClassName = analyserClassName;;
    }
    
}
