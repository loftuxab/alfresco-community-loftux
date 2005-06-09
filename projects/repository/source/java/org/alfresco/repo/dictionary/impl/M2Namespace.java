package org.alfresco.repo.dictionary.impl;


/**
 * Namespace Definition.
 * 
 * @author David Caruana
 *
 */
public class M2Namespace
{
    private String uri = null;
    private String prefix = null;
   
    
    /*package*/ M2Namespace()
    {
    }

    
    public String getUri()
    {
        return uri;
    }
    

    public void setUri(String uri)
    {
        this.uri = uri;
    }
   

    public String getPrefix()
    {
        return prefix;
    }
    

    public void setPrefix(String prefix)
    {
        this.prefix = prefix;
    }
    
}
