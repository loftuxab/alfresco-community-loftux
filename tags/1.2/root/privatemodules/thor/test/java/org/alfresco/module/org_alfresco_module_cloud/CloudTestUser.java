package org.alfresco.module.org_alfresco_module_cloud;

/**
 * Utility class to collect test data for users so they can easily be used in 
 * JUnit tests when creating multiple users
 * 
 * @author David Gildeh
 */
public class CloudTestUser
{

    private String email = "";
    private String firstName = "";
    private String lastName = "";
    
    /**
     * Constructor to create new user object
     * 
     * @param email         The user's email address
     * @param firstName     The user's first name
     * @param lastName      The user's last name
     */
    public CloudTestUser(String email, String firstName, String lastName)
    {
        this.setEmail(email);
        this.setFirstName(firstName);
        this.setLastName(lastName);
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getFirstName()
    {
        return firstName;
    }

    public void setFirstName(String firstName)
    {
        this.firstName = firstName;
    }

    public String getLastName()
    {
        return lastName;
    }

    public void setLastName(String lastName)
    {
        this.lastName = lastName;
    }
}
