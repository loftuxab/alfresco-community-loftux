package org.alfresco.module.vti.metadata.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.alfresco.module.vti.metadata.dic.Permission;
import org.alfresco.service.cmr.repository.NodeRef;

/**
 * <p>Represents the Sharepoint List with its meta-information.</p>
 * 
 * @author AndreyAk
 */
public class ListInfoBean implements Serializable
{
    private static final long serialVersionUID = 216886247863517038L;
    
    private ListTypeBean type;
    private NodeRef nodeRef;
    private String name;
    private String title;
    private String description;
    private Date created;
    private Date modified;
    private String author;
    private int numItems;
    
    private boolean moderated;
    private List<Permission> permissionList;
    
    /**
     * @param name
     * @param moderated
     * @param permissionList
     */
    public ListInfoBean(NodeRef nodeRef, String name, ListTypeBean type, 
          boolean moderated, List<Permission> permissionList)
    {
        super();
        this.name = name;
        this.type = type;
        this.nodeRef = nodeRef;
        this.moderated = moderated;
        this.permissionList = permissionList;
    }
    
    /**
     * @return The underlying NodeRef of the list
     */
    public NodeRef getNodeRef() 
    {
       return nodeRef;
    }
    
    /**
     * @return The unique ID of the list, based on the NodeRef
     */
    public String getId()
    {
        return '{' + nodeRef.getId() + '}';
    }
    
    /**
     * @return The Type of the list
     */
    public ListTypeBean getType()
    {
       return type;
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
     * @return The List Title
     */
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

    /**
     * @return the moderated
     */
    public boolean isModerated()
    {
        return moderated;
    }
    /**
     * @param moderated the moderated to set
     */
    public void setModerated(boolean moderated)
    {
        this.moderated = moderated;
    }
    
    public Date getCreated() 
    {
       return created;
    }
    public void setCreated(Date created) 
    {
       this.created = created;
    }

    public Date getModified() 
    {
       return modified;
    }
    public void setModified(Date modified) 
    {
       this.modified = modified;
    }

    public String getAuthor() 
    {
       return author;
    }
    public void setAuthor(String author) 
    {
       this.author = author;
    }
    
    /**
     * @return Number of items in this list
     */
    public int getNumItems() 
    {
       return numItems;
    }
    public void setNumItems(int numItems) 
    {
       this.numItems = numItems;
    }

    /**
     * @return the permissionList
     */
    public List<Permission> getPermissionList()
    {
        return permissionList;
    }
    /**
     * @param permissionList the permissionList to set
     */
    public void setPermissionList(List<Permission> permissionList)
    {
        this.permissionList = permissionList;
    }
}
