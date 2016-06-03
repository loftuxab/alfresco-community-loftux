package org.alfresco.repo.dictionary;


/**
 * Property Type Definition
 * 
 * @author David Caruana
 *
 */
public class M2DataType
{
    private String name = null;
    private String title = null;
    private String description = null;
    private String defaultAnalyserClassName = null;
    private String javaClassName = null;
    private String analyserResourceBundleName = null;
    
    
    /*package*/ M2DataType()
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
    
    
    public String getDefaultAnalyserClassName()
    {
        return defaultAnalyserClassName;
    }
    
    
    public void setDefaultAnalyserClassName(String defaultAnalyserClassName)
    {
        this.defaultAnalyserClassName = defaultAnalyserClassName;;
    }

    
    public String getJavaClassName()
    {
        return javaClassName;
    }
    
    
    public void setJavaClassName(String javaClassName)
    {
        this.javaClassName = javaClassName;;
    }


    /**
     * @return String
     */
    public String getAnalyserResourceBundleName()
    {
        return analyserResourceBundleName; 
    }


    public void setAnalyserResourceBundleName(String analyserResourceBundleName)
    {
        this.analyserResourceBundleName = analyserResourceBundleName;
    }
    
}
