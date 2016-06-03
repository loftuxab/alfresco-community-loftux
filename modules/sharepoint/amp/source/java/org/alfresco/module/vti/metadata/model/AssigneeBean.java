package org.alfresco.module.vti.metadata.model;

import java.io.Serializable;

/**
 * <p>Represent the assignee of the Sharepoint site.</p>
 * 
 * @author AndreyAk
 */
public class AssigneeBean implements Serializable
{

    private static final long serialVersionUID = -1763995360866475924L;
    
    private String id;
    private String name;
    private String loginName;
    
    
    /**
     * Constructor
     * 
     * @param id
     * @param name
     * @param loginName
     */
    public AssigneeBean(String id, String name, String loginName)
    {
        super();
        this.id = id;
        this.name = name;
        this.loginName = loginName;
    }

    /**
     * @return the id
     */
    public String getId()
    {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id)
    {
        this.id = id;
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
     * @return the loginName
     */
    public String getLoginName()
    {
        return loginName;
    }

    /**
     * @param loginName the loginName to set
     */
    public void setLoginName(String loginName)
    {
        this.loginName = loginName;
    }
    
}
