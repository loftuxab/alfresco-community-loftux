package org.alfresco.module.vti.metadata.model;

import java.io.Serializable;
import java.util.List;

/**
 * <p>Represents Sharepoint document work space after creation. </p>
 * 
 * @author AndreyAk
 *
 */
public class DwsBean implements Serializable
{

    private static final long serialVersionUID = -1659680970545671188L;

    private String url;
    private String doclibUrl;
    private String parentWeb;
    private List<String> failedUsers;
    private String addUsersUrl = "";
    private String addUsersRole = "";

    public DwsBean()
    {
        super();
    }
    
    /**
     * @param url
     * @param doclibUrl
     * @param parentWeb
     * @param failedUsers
     * @param addUsersUrl TODO
     * @param addUsersRole TODO
     */
    public DwsBean(String url, String doclibUrl, String parentWeb, List<String> failedUsers, String addUsersUrl, String addUsersRole)
    {
        this.url = url;
        this.doclibUrl = doclibUrl;
        this.parentWeb = parentWeb;
        this.failedUsers = failedUsers;
        this.addUsersUrl = addUsersUrl;
        this.addUsersRole = addUsersRole;
    }

    /**
     * @return the url
     */
    public String getUrl()
    {
        return url;
    }

    /**
     * <p>Sets the dws url.</p>
     * 
     * @param url the url to set
     */
    public void setUrl(String url)
    {
        this.url = url;
    }

    /**
     * @return the doclibUrl
     */
    public String getDoclibUrl()
    {
        return doclibUrl;
    }

    /**
     * <p>Sets the document library url for current dws.</p>
     * 
     * @param doclibUrl the doclibUrl to set
     */
    public void setDoclibUrl(String doclibUrl)
    {
        this.doclibUrl = doclibUrl;
    }

    /**
     * @return the parentWeb
     */
    public String getParentWeb()
    {
        return parentWeb;
    }

    /**
     * <p>Sets the url of the parent site.</p>
     * 
     * @param parentWeb the parentWeb to set
     */
    public void setParentWeb(String parentWeb)
    {
        this.parentWeb = parentWeb;
    }

    /**
     * @return the failedUsers
     */
    public List<String> getFailedUsers()
    {
        return failedUsers;
    }

    /**
     * @param failedUsers the failedUsers to set
     */
    public void setFailedUsers(List<String> failedUsers)
    {
        this.failedUsers = failedUsers;
    }
        
    /**
     * @return the addUsersRole
     */
    public String getAddUsersRole()
    {
        return addUsersRole;
    }
    
    /**
     * @return the addUsersUrl
     */
    public String getAddUsersUrl()
    {
        return addUsersUrl;
    }
}
