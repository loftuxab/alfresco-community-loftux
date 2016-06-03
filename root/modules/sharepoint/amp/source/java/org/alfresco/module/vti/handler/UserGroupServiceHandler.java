package org.alfresco.module.vti.handler;

import java.util.List;

import org.alfresco.module.vti.metadata.model.UserBean;

/**
 * Interface for user group web service handler
 * 
 * @author AndreyAk
 */
public interface UserGroupServiceHandler
{

    /**
     * Returns a user name based on the specified e-mail address.
     * 
     * @param dwsUrl dws url
     * @param emailList list that specifies the e-mail address of the user
     * @return List<UserBean>
     */
    List<UserBean> getUserLoginFromEmail(String dwsUrl, List<String> emailList);

    /**
     * Adds the collection of users to the specified site group.
     * 
     * @param dwsUrl dws url
     * @param roleName name of the site group to add users to
     * @param usersList list that contains information about the users to add ({@link UserBean})
     */
    void addUserCollectionToRole(String dwsUrl, String roleName, List<UserBean> usersList);

    /**
     * Check user on member
     * 
     * @param dwsUrl dws url
     * @param username list that contains information about the users to add
     * @return <i>true</i>, if user is member; otherwise, <i>false</i>
     */
    boolean isUserMember(String dwsUrl, String username);

}
