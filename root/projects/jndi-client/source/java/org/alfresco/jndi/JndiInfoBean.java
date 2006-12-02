/**
 * 
 */
package org.alfresco.jndi;

/**
 * An informational bean for jndi-client configuration. Currently
 * it holds the user name and password for an Admin user on the 
 * Alfresco server.
 * @author britt
 */
public class JndiInfoBean 
{
    /**
     * The user to log into the Alfresco server as.
     */
    private String alfrescoServerUser_;
    
    /**
     * The password for the user to login to the Alfresco server as.
     */
    private String alfrescoServerPassword_;

    /**
     * A Default constructor.
     */
    public JndiInfoBean()
    {
    }

    /**
     * Getter for the password.
     */
    public String getAlfrescoServerPassword() 
    {
        return alfrescoServerPassword_;
    }

    /**
     * Setter for the password.
     */
    public void setAlfrescoServerPassword(String alfrescoServerPassword) 
    {
        alfrescoServerPassword_ = alfrescoServerPassword;
    }

    /**
     * Getter for the user name.
     */
    public String getAlfrescoServerUser() 
    {
        return alfrescoServerUser_;
    }

    /**
     * Setter for the user name.
     */
    public void setAlfrescoServerUser(String alfrescoServerUser)
    {
        alfrescoServerUser_ = alfrescoServerUser;
    }
}
