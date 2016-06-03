package org.alfresco.module.vti.metadata.model;

import java.io.Serializable;
import java.util.List;

/**
 * <p>Represents the Sharepoint schema with its meta-inforamtion.</p>
 * 
 * @author AndreyAk
 *
 */
public class SchemaBean implements Serializable
{

    private static final long serialVersionUID = -2075342655994340968L;
    
    private String name;
    private String url;
    private List<SchemaFieldBean> fields;
    
    
    /**
     * @param name
     * @param url
     * @param fields
     */
    public SchemaBean(String name, String url, List<SchemaFieldBean> fields)
    {
        super();
        this.name = name;
        this.url = url;
        this.fields = fields;
    }
    
    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }
    /**
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }
    /**
     * @return the url
     */
    public String getUrl()
    {
        return url;
    }
    /**
     * @param url the url to set
     */
    public void setUrl(String url)
    {
        this.url = url;
    }
    /**
     * @return the fields
     */
    public List<SchemaFieldBean> getFields()
    {
        return fields;
    }
    /**
     * <p>Sets the fields that schema contains.</p>
     * 
     * @param fields the fields to set
     */
    public void setFields(List<SchemaFieldBean> fields)
    {
        this.fields = fields;
    }
        
}
